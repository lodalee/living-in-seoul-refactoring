package com.gavoza.backend.domain.post.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.post.entity.LocationTag;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import com.gavoza.backend.domain.post.entity.PurposeTag;
import com.gavoza.backend.domain.post.repository.LocationTagRepository;
import com.gavoza.backend.domain.post.repository.PostImgRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.repository.PurposeTagRepository;
import com.gavoza.backend.global.exception.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final LocationTagRepository locationTagRepository;
    private final PurposeTagRepository purposeTagRepository;


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

            //PostImg 저장
            PostImg postImg = new PostImg(fileName, null);
            postImgList.add(postImg);

        }
        //locationTag 처리
        LocationTag locationTag = processLocationTag(requestDto.getLocationTag());

        // purposeTag 처리
        PurposeTag purposeTag = processPurposeTag(requestDto.getPurposeTag());


        //post 저장
        Post post = new Post(requestDto);
        post.setLocationTag(locationTag);
        post.setPurposeTag(purposeTag);
        postRepository.save(post);


        // PostImg 인스턴스를 저장된 게시물에 연결하여 저장
        for (PostImg postImg : postImgList) {
            postImg.setPost(post);
            postImgRepository.save(postImg);
        }
        return new MessageResponseDto("파일 저장 성공");
    }
    private LocationTag processLocationTag(String locationTagName) {
        if (locationTagName == null || locationTagName.isEmpty()) {
            return null;
        }
        Optional<LocationTag> existingLocationTag = locationTagRepository.findByLocationTag(locationTagName);
        return existingLocationTag.orElseGet(() -> locationTagRepository.save(new LocationTag(locationTagName)));
    }

    private PurposeTag processPurposeTag(String purposeTagName) {
        if (purposeTagName == null || purposeTagName.isEmpty()) {
            return null;
        }
        Optional<PurposeTag> existingPurposeTag = purposeTagRepository.findByPurposeTag(purposeTagName);
        return existingPurposeTag.orElseGet(() -> purposeTagRepository.save(new PurposeTag(purposeTagName)));
    }


//    //게시글 전체 조회
//    public AllPostResponse getPosts() {
//        Long size = 5L;
//
//        Page<Post> posts = postRepository.findAll();
//        return new AllPostResponse("전체 조회 성공", (long) posts.getTotalPages(),
//                posts.getTotalElements(), size, posts.map(post->post).stream().toList());
//    }
}


