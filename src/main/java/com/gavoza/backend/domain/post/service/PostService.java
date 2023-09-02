package com.gavoza.backend.domain.post.service;

import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.alarm.AlarmEventType;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.sse.NotificationService;
import com.gavoza.backend.domain.post.dto.*;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import com.gavoza.backend.domain.post.repository.PostImgRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.post.response.PostResponse;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.scrap.entity.PostScrap;
import com.gavoza.backend.domain.scrap.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import com.gavoza.backend.domain.user.all.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional //(readOnly = true)
public class PostService {

    private final AmazonS3Service amazonS3Service;
    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final AlarmRepository alarmRepository;
    private final ReportRepository reportRepository;
    private final NotificationService notificationService;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //게시물 생성 : 게시물을 업로드하고 이미지를 저장
    public MessageResponseDto upload(PostRequestDto requestDto, User user, List<MultipartFile> photos) throws IOException {
        requestDto.validateCategory();

        //본문에서 해시태그 추출
        List<String> hashtags = extractHashtagsFromContent(requestDto);

        List<PostImg> postImgList = amazonS3Service.uploadPhotosToS3AndCreatePostImages(photos);

        Post post = new Post(requestDto, user);
        postRepository.save(post);

        associatePostImagesWithPost(post, postImgList);

        // 게시물에 연결된 해시태그를 구독하는 사용자 찾기
        for (String hashtag : hashtags) {
            List<User> subscribers = findSubscribersForHashtag(hashtag);

            for (User subscriber : subscribers) {
                // 로그인한 사용자와 게시물을 올린 사용자가 다를 때만 알림 생성
                if (!subscriber.getId().equals(user.getId())) {
                    AlarmEventType eventType = AlarmEventType.NEW_POST_WITH_HASHTAG; // 댓글에 대한 알림 타입 설정
                    Boolean isRead = false; // 초기값으로 미읽음 상태 설정
                    String notificationMessage = post.getContent(); // 알림 메시지 설정
                    LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정
                    String userImg = user.getProfileImageUrl();
                    String hashtagName = hashtag;

                    Alarm alarm = new Alarm(post, subscriber, eventType, isRead, notificationMessage, registeredAt, userImg, hashtagName);
                    alarmRepository.save(alarm);
                    notificationService.notifyAddEvent(subscriber, subscriber.isHashtagAlarm());
                }
            }
        }

        return new MessageResponseDto("파일 저장 성공");
    }

    //해시태그 추출
    public List<String> extractHashtagsFromContent(PostRequestDto requestDto) {
        List<String> hashtags = new ArrayList<>();

        String[] hashTagList = requestDto.getHashtag().split("#");

        // 첫 번째 요소는 공백이므로 무시하고 나머지 요소를 리스트에 추가
        for (int i = 1; i < hashTagList.length; i++) {
            hashtags.add("#" + hashTagList[i]);
        }

        return hashtags;
    }

    //해당 해시태그를 구독하는 사용자 찾기
    public List<User> findSubscribersForHashtag(String hashtag) {
        // 해당 해시태그를 구독하는 사용자를 찾는 쿼리 작성
        String query = "SELECT s.user FROM SubscribeHashtag s WHERE s.hashtag = :hashtag";
        return entityManager.createQuery(query, User.class)
                .setParameter("hashtag", hashtag)
                .getResultList();
    }

    //PostImg 객체와 Post를 연결하고 DB에 저장
    private void associatePostImagesWithPost(Post post, List<PostImg> postImgList) {
        for (PostImg postImg : postImgList) {
            postImg.setPost(post);
            postImgRepository.save(postImg);
        }
    }

    //게시물 수정
    public void updatePost(Long postId, PostRequestDto requestDto, User user) {
        Post post = findPost(postId);
        validateUserOwnership(user, post);
        post.update(requestDto.getContent(), requestDto.getLat(), requestDto.getLng());
    }

    //게시물 삭제
    public void deletePost(Long postId, User user) {
        Post post = findPost(postId);
        validateUserOwnership(user, post);
        postRepository.delete(post);
    }

    //유저 상세 게시물 조회
    public PostResponse getOnePost(Long postId, User user) {
        Post post = findPost(postId);
        post.increaseViewCount();

        UserResponseDto userResponseDto = new UserResponseDto(post.getUser().getNickname(), post.getUser().getEmail());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());

        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        boolean hasReported = reportRepository.existsReportByPostAndUser(post,user);
        return new PostResponse("게시글 조회 성공", new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped, hasReported));
    }

    //비회원 상세 게시물 조회
    public PostResponse getOnePost2(Long postId) {
        Post post = findPost(postId);
        post.increaseViewCount();

        UserResponseDto userResponseDto = new UserResponseDto(post.getUser().getNickname(), post.getUser().getEmail());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());

        boolean hasLikedPost = false;
        boolean hasScrapped = false;
        boolean hasReported = false;

        return new PostResponse("게시글 조회 성공", new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost, hasScrapped, hasReported));
    }

    //게시물 전체 조회
    public PostListResponse getPost(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPages = postRepository.findAll(pageable);

        List<PostResultDto> postResultDtos = postPages.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostListResponse("검색 조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
    }

    //내가 쓴 글
    public PostListResponse getMyPost(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPages = postRepository.findAllByUserId(user.getId(), pageable);

        List<PostResultDto> postResultDtos = postPages.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostListResponse("조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
    }

    // 사용자가 스크랩한 글 조회
    public PostListResponse getMyScrap(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scrapedAt"));
        Page<PostScrap> scrapPages = postScrapRepository.findAllByUser(user, pageable);

        // 스크랩한 글을 가져와서 리스트로 변환
        List<Post> myScrapPosts = scrapPages.getContent().stream()
                .map(PostScrap::getPost)
                .collect(Collectors.toList());

        // 가져온 글을 PostResultDto로 매핑하여 리스트로 변환
        List<PostResultDto> postResultDtos = myScrapPosts.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostListResponse("스크랩한 글 조회 성공", scrapPages.getTotalPages(), scrapPages.getTotalElements(), size, postResultDtos);
    }

    //PostResultDto 타입으로 반환
    private PostResultDto mapToPostResultDto(Post post, User user) {
        UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());
        if (Objects.isNull(user)) {
            return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, false,false,false);
        }
        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        boolean hasReported = reportRepository.existsReportByPostAndUser(post,user);
        return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped, hasReported);
    }

    //주어진 게시물 ID에 해당하는 게시물 조회
    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다."));
    }

    //사용자가 게시물의 작성자인지 확인
    private void validateUserOwnership(User user, Post post) {
        if (!post.getUser().getNickname().equals(user.getNickname())) {
            throw new IllegalArgumentException("해당 게시글의 작성자가 아닙니다.");
        }
    }


}


