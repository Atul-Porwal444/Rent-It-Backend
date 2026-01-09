package com.rentit.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final S3Client s3Client;

    private final String bucketName;

    private final String projectId;

    public ImageStorageService(@Value("${app.supabase.s3-endpoint}") String endpoint,
                               @Value("${app.supabase.access-key}") String accessKey,
                               @Value("${app.supabase.secret-key}") String secretKey,
                               @Value("${app.supabase.bucket-name}") String bucketName,
                               @Value("${app.supabase.region}") String region) {

        this.bucketName = bucketName;

        this.projectId = URI.create(endpoint).getHost().split("\\.")[0];

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putOb, RequestBody.fromBytes(file.getBytes()));

        String publicUrl = String.format("https://%s.supabase.co/storage/v1/object/public/%s/%s",
                projectId, bucketName, fileName);

        return publicUrl;
    }
}
