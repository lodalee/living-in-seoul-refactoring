package com.gavoza.backend.domain.user.all.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageEditService {

    private final AmazonS3 s3Client;
    private final String bucketName;

    public ImageEditService(AmazonS3 s3Client, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        // Use UUID for unique file names
        String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String url) {
        try {
            URI uri = new URI(url);
            String fileName = Paths.get(uri.getPath()).getFileName().toString();
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("유효하지 않은 URL 입니다.");
        }
    }
}
