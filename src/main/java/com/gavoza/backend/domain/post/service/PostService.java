package com.gavoza.backend.domain.post.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import com.gavoza.backend.domain.post.repository.PostImgRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.post.response.PostResponse;
import com.gavoza.backend.domain.user.dto.UserResponseDto;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional //(readOnly = true)
public class PostService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostLikeRepository postLikeRepository;


    //upload
    public MessageResponseDto upload(PostRequestDto requestDto, User user,List<MultipartFile> photos) throws IOException {

        //존재하지 않는 카테고리 에러처리
        requestDto.validateCategory();

        List<PostImg> postImgList = new ArrayList<>();

        List<String> uuidFilePaths = new ArrayList<>();

        //S3에 이미지 저장
        //photos 목록이 null이 아니고 비어있지 않을 때만 업로드 작업 수행
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                long size = photo.getSize();

                //ObjectMetadata 객체를 생성하여 파일의 메타데이터를 설정합니다.
                //이 메타데이터에는 파일 크기, 콘텐츠 유형 및 인라인 콘텐츠 디스포지션을 설정합니다.
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(photo.getContentType());
                objectMetadata.setContentLength(size);
                objectMetadata.setContentDisposition("inline");

                String prefix = UUID.randomUUID().toString();
                String fileName = prefix + "_" + photo.getOriginalFilename();
                String bucketFilePath = "photos/" + fileName;

                //S3에 업로드
                //Amazon S3 클라이언트를 사용하여 파일을 지정된 버킷(bucketName) 경로(bucketFilePath)에 업로드합니다.
                //withCannedAcl은 업로드된 파일을 공개 읽기 권한으로 설정하는 데 사용됩니다.
                amazonS3Client.putObject(
                        new PutObjectRequest(bucketName, bucketFilePath, photo.getInputStream(), objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead)
                );

                uuidFilePaths.add(fileName);

                //PostImg 저장
                PostImg postImg = new PostImg(fileNameToURL(fileName), null);
                postImgList.add(postImg);
            }
        }

            Post post = new Post(requestDto, user, uuidFilePaths);
            postRepository.save(post);

            // PostImg 인스턴스를 저장된 게시물에 연결하여 저장
            for (PostImg postImg : postImgList) {
                postImg.setPost(post);
                postImgRepository.save(postImg);
            }
            return new MessageResponseDto("파일 저장 성공");
        }

    private String fileNameToURL(String fileName){
        return "https://living-in-seoul.s3.ap-northeast-2.amazonaws.com/photos/" + fileName;
    }


    //post 수정
    public void updatePost(Long postId, PostRequestDto requestDto, User user) {
        Post post = findPost(postId);
        if (!(post.getUser().getNickname().equals(user.getNickname()))){
            throw new IllegalArgumentException("해당 게시글의 작성자가 아닙니다.");
        }
        long lat = requestDto.getLat();
        long lng = requestDto.getLng();
        String content = requestDto.getContent();

        post.update(content,lat,lng);
    }

    //post 삭제
    public void deletePost(Long postId, User user) {
        Post post = findPost(postId);

        if (!(post.getUser().getNickname().equals(user.getNickname()))) {
            throw new IllegalArgumentException("해당 게시글의 작성자가 아닙니다.");
        }
        postRepository.delete(post);
    }

    //게시물 상세 조회
    public PostResponse getOnePost(Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물은 존재하지 않습니다."));

        findPost.increaseViewCount();
        boolean hasLikedPost = false;

        if (postLikeRepository.existsLikeByPostAndUser(findPost, findPost.getUser())){
            hasLikedPost = true;
        }

        UserResponseDto userResponseDto = new UserResponseDto(findPost.getUser().getNickname(),findPost.getUser()
                .getEmail());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(findPost);

        return new PostResponse(findPost,"게시글 조회 성공", new PostResultDto(userResponseDto, postInfoResponseDto),hasLikedPost);
    }

    //커뮤니티 전체조회
    public PostListResponse getPost(int page, int size) {
        // 페이지 및 사이즈 계산
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPages = postRepository.findAll(pageable);
        List<PostResultDto> postResultDtos = new ArrayList<>();

        for (Post post : postPages) {
            UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
            PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
            postResultDtos.add(new PostResultDto(userResponseDto, postInfoResponseDto));
        }
        return new PostListResponse("검색 조회 성공",postPages.getTotalPages(),postPages.getTotalElements(), size, postResultDtos);
    }



    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.")
        );
    }
}


