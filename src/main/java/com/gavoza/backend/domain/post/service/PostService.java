package com.gavoza.backend.domain.post.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
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
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //게시물 생성 : 게시물을 업로드하고 이미지를 저장
    public MessageResponseDto upload(PostRequestDto requestDto, User user, List<MultipartFile> photos) throws IOException {
        requestDto.validateCategory();

        List<PostImg> postImgList = uploadPhotosToS3AndCreatePostImages(photos);

        Post post = new Post(requestDto, user);
        postRepository.save(post);

        associatePostImagesWithPost(post, postImgList);

        return new MessageResponseDto("파일 저장 성공");
    }

    //이미지를 S3에 업로드하고, PostImg 객체를 생성하여 리스트에 추가
    private List<PostImg> uploadPhotosToS3AndCreatePostImages(List<MultipartFile> photos) throws IOException {
        List<PostImg> postImgList = new ArrayList<>();

        if (photos != null) {
            for (MultipartFile photo : photos) {
                if (photo != null && !photo.isEmpty()) { // 이미지가 null이 아니고 비어있지 않은 경우에만 처리
                    String fileName = uploadPhotoToS3AndGetFileName(photo);
                    PostImg postImg = new PostImg(fileNameToURL(fileName), null);
                    postImgList.add(postImg);
                }
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
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getGu(), post.getDong(), post.getLat(), post.getLng());

        if(Objects.isNull(user)){
            return new PostResponse( "게시글 조회 성공", new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, false));
        }

        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);

        return new PostResponse( "게시글 조회 성공", new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost));
    }

    //게시물 전체 조회
    public PostListResponse getPost(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPages = postRepository.findAll(pageable);

        List<PostResultDto> postResultDtos = postPages.stream()
                .map(post -> mapToPostResultDto(post,user))
                .collect(Collectors.toList());

        return new PostListResponse("게시글 조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
    }

    //게시글 검색
    public PostListResponse searchPosts(int page, int size, String keyword, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPages = keyword.contains("#")
                ? postRepository.findAllByHashtagContaining(keyword, pageable)
                : postRepository.findAllByContentContaining(keyword, pageable);

        List<PostResultDto> postResultDtos = postPages.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostListResponse("검색 조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
    }


    //PostResultDto 타입으로 반환
    private PostResultDto mapToPostResultDto(Post post, User user) {
        UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getGu(), post.getDong(), post.getLat(), post.getLng());
        if (Objects.isNull(user)){
            return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, false);
        }
        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost);
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


