package com.gavoza.backend.domain.post.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gavoza.backend.domain.post.entity.PostImg;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //이미지를 S3에 업로드하고, PostImg 객체를 생성하여 리스트에 추가
    public List<PostImg> uploadPhotosToS3AndCreatePostImages(List<MultipartFile> photos) throws IOException {
        List<PostImg> postImgList = new ArrayList<>();

        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) { // 파일이 비어있지 않은 경우에만 업로드 진행
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

        InputStream inputStream = photo.getInputStream();

        BufferedImage originalImage = ImageIO.read(inputStream);

        int targetWidth = 500; // 가로 길이를 500px로 설정

        // 이미지가 500px보다 크면 리사이즈
        if (originalImage.getWidth() > targetWidth) {
            BufferedImage resizedImage = resizeImage(originalImage, targetWidth);

            // 리사이즈된 이미지를 S2에 업로드
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            byte[] resizedImageBytes = outputStream.toByteArray();

            objectMetadata.setContentLength(resizedImageBytes.length);

            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, bucketFilePath, new ByteArrayInputStream(resizedImageBytes), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } else {
            // 이미지가 500px 이하이면 그대로 S2에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, bucketFilePath, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        }

        return fileName;
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) {
        double aspectRatio = (double) originalImage.getHeight() / originalImage.getWidth();
        int targetHeight= (int) (targetWidth * aspectRatio);

        BufferedImage scaledImge= new BufferedImage(targetWidth,targetHeight ,BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d= scaledImge.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage ,0 ,0 ,targetWidth,targetHeight,null );

        g2d.dispose();

        return scaledImge;
    }

    private String fileNameToURL(String fileName) {
        return "https://living-in-seoul.s3.ap-northeast-2.amazonaws.com/photos/" + fileName;
    }
}