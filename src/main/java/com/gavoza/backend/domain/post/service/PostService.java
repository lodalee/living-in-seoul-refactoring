package com.gavoza.backend.domain.post.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.alarm.AlarmEventType;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.post.dto.LocationResponseDto;
import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import com.gavoza.backend.domain.post.repository.PostImgRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.post.response.PostResponse;
import com.gavoza.backend.domain.scrap.entity.PostScrap;
import com.gavoza.backend.domain.scrap.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional //(readOnly = true)
public class PostService {

    private final AmazonS3Client amazonS3Client;
    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final AlarmRepository alarmRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //게시물 생성 : 게시물을 업로드하고 이미지를 저장
    public MessageResponseDto upload(PostRequestDto requestDto, User user, List<MultipartFile> photos) throws IOException {
        requestDto.validateCategory();

        //본문에서 해시태그 추출
        List<String> hashtags = extractHashtagsFromContent(requestDto);
        // [#맛집, #좋아요]

        List<PostImg> postImgList = uploadPhotosToS3AndCreatePostImages(photos);

        Post post = new Post(requestDto, user);
        postRepository.save(post);

        associatePostImagesWithPost(post, postImgList);

        // 게시물에 연결된 해시태그를 구독하는 사용자 찾기
        for (String hashtag : hashtags) {
            List<User> subscribers = findSubscribersForHashtag(hashtag);

            for (User subscriber : subscribers) {
                AlarmEventType eventType = AlarmEventType.NEW_POST_WITH_HASHTAG; // 댓글에 대한 알림 타입 설정
                Boolean isRead = false; // 초기값으로 미읽음 상태 설정
                String notificationMessage = post.getContent(); // 알림 메시지 설정
                LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정

                Alarm alarm = new Alarm(post, subscriber, eventType, isRead, notificationMessage, registeredAt);
                alarmRepository.save(alarm);
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

    //이미지를 S3에 업로드하고, PostImg 객체를 생성하여 리스트에 추가
    private List<PostImg> uploadPhotosToS3AndCreatePostImages(List<MultipartFile> photos) throws IOException {
        List<PostImg> postImgList = new ArrayList<>();

        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String fileName = uploadPhotoToS3AndGetFileName(photo);
                PostImg postImg = new PostImg(fileNameToURL(fileName), null);
                postImgList.add(postImg);
            }
        }
        return postImgList;
    }

    //S3에 이미지를 업로드하고, 업로드된 파일의 이름을 반환
    private String uploadPhotoToS3AndGetFileName(MultipartFile photo) throws IOException {
        long size = photo.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(photo.getContentType());
        objectMetadata.setContentLength(size);
        objectMetadata.setContentDisposition("inline");

        String prefix = UUID.randomUUID().toString();
        String fileName = prefix + "_" + photo.getOriginalFilename();
        String bucketFilePath = "photos/" + fileName;

        amazonS3Client.putObject(
                new PutObjectRequest(bucketName, bucketFilePath, photo.getInputStream(), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return fileName;
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

    //상세 게시물 조회
    public PostResponse getOnePost(Long postId, User user) {
        Post post = findPost(postId);
        post.increaseViewCount();

        UserResponseDto userResponseDto = new UserResponseDto(post.getUser().getNickname(), post.getUser().getEmail());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());

        if (Objects.isNull(user)) {
            return new PostResponse("게시글 조회 성공", new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, false,false));
        }

        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        return new PostResponse("게시글 조회 성공", new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped));
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
            return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, false,false);
        }
        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped);
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

    private String fileNameToURL(String fileName) {
        return "https://living-in-seoul.s3.ap-northeast-2.amazonaws.com/photos/" + fileName;
    }
}


