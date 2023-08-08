package com.gavoza.backend.domain.post.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.post.dto.PostResponseDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import com.gavoza.backend.domain.post.repository.PostImgRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.global.exception.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional //(readOnly = true)
public class PostService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;


    //upload
    public MessageResponseDto upload(PostRequestDto requestDto, List<MultipartFile> photos) throws IOException {
        List<PostImg> postImgList = new ArrayList<>();

        for (MultipartFile photo : photos) {
            long size = photo.getSize();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(photo.getContentType());
            objectMetadata.setContentLength(size);

            String prefix = UUID.randomUUID().toString();
            String fileName = prefix + "_" + photo.getOriginalFilename();
            String bucketFilePath = "photos/" + fileName;

            //S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, bucketFilePath, photo.getInputStream(), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            // Create a new PostImg instance and add it to the list
            PostImg postImg = new PostImg(fileName, null);
            postImgList.add(postImg);

        }

        //post 저장
        Post post = new Post(requestDto);
        postRepository.save(post);

        // Connect the PostImg instances to the saved post and save them
        for (PostImg postImg : postImgList) {
            postImg.setPost(post);
            postImgRepository.save(postImg);
        }

        return new MessageResponseDto("파일 저장 성공");
    }

    //게시글 전체 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.findAll()
                .stream()
                .map(post-> new PostResponseDto(post,"전체 조회 성공"))
                .collect(Collectors.toList());
    }
}


