package com.gavoza.backend.domain.post.service;

import com.gavoza.backend.domain.post.entity.Postlike;
import com.gavoza.backend.domain.post.repository.PostLikeRepository;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.service.SSENotificationService;
import com.gavoza.backend.domain.alarm.type.AlarmEventType;
import com.gavoza.backend.domain.post.dto.request.PostRequestDto;
import com.gavoza.backend.domain.post.dto.response.*;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import com.gavoza.backend.domain.post.repository.PostImgRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.post.entity.PostScrap;
import com.gavoza.backend.domain.post.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
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
public class PostUserService{

    private final AmazonS3Service amazonS3Service;
    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final AlarmRepository alarmRepository;
    private final ReportRepository reportRepository;
    private final SSENotificationService sseNotificationService;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //게시물 생성 : 게시물을 업로드하고 이미지를 저장
    public MessageResponseDto upload(PostRequestDto requestDto, User user, List<MultipartFile> photos) throws IOException {
        requestDto.validateCategory();

        //본문에서 해시태그 추출
        List<String> hashtags = extractHashtagsFromContent(requestDto);

        List<PostImg> postImgList = null;
        if (photos != null && !photos.isEmpty()) {
            postImgList = amazonS3Service.uploadPhotosToS3AndCreatePostImages(photos);
        }

        Post post = new Post(requestDto, user);
        postRepository.save(post);

        if (postImgList != null) {
            associatePostImagesWithPost(post, postImgList);
        }

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
                    sseNotificationService.notifyAddEvent(subscriber, subscriber.isHashtagAlarm());
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

    //내가 쓴 글
    public PostsResponseDto getMyPost(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPages = postRepository.findAllByUserId(user.getId(), pageable);

        List<PostResultDto> postResultDtos = postPages.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostsResponseDto("조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
    }

    //PostResultDto 타입으로 반환
    private PostResultDto mapToPostResultDto(Post post, User user) {
        PostUserDto postUserDto = new PostUserDto(post.getUser());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());
        if (Objects.isNull(user)) {
            return new PostResultDto(postUserDto, postInfoResponseDto, locationResponseDto, false,false,false);
        }
        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        boolean hasReported = reportRepository.existsReportByPostAndUser(post,user);
        return new PostResultDto(postUserDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped, hasReported);
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

    //post 좋아요
    public MessageResponseDto postLike(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (!postLikeRepository.existsLikeByPostAndUser(post, user)){
            Postlike like = new Postlike(post, user) ;
            postLikeRepository.save(like);

            // 좋아요 알림 생성 및 저장
            String notificationMessage = "<b>" + user.getNickname() + "</b>" + "님이 [" + post.getContent() + "] 글에 좋아요를 눌렀어요!"; // 알림 메시지 설정
            AlarmEventType eventType = AlarmEventType.NEW_LIKE_ON_POST; // 알림 타입 설정
            Boolean isRead = false; // 초기값으로 미읽음 상태 설정
            LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정
            String userImg = user.getProfileImageUrl();

            if (!post.getUser().getId().equals(user.getId())) {
                Alarm likeNotification = new Alarm(post, post.getUser(), eventType, isRead, notificationMessage, registeredAt, userImg);
                alarmRepository.save(likeNotification);
                sseNotificationService.notifyAddEvent(post.getUser(), post.getUser().isLikeAlarm());
            }
            return new MessageResponseDto("좋아요");
        }

        Postlike like = postLikeRepository.findByPostAndUser(post, user).orElseThrow(
                ()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        postLikeRepository.delete(like);

        return new MessageResponseDto("좋아요 취소");
    }
}