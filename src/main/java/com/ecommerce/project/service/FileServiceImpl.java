package com.ecommerce.project.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{


    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    private final S3Client s3Client;

    public FileServiceImpl(){
        this.s3Client=S3Client.builder().build();
    }


    @Override
        public  String uploadImage(MultipartFile file) throws IOException {
        //File name of  current /original file
        String originalFileName=file.getOriginalFilename();
        //Generate a unique file name -using random UUID
        String randomId= UUID.randomUUID().toString();
        String fileName=randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));


        PutObjectRequest putObjectRequest= PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();


         s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(),file.getSize()));

//        //check if path exits and create
//        String filePath=path+ File.separator+fileName;
//
//        File folder=new File(path);
//        if(!folder.exists())
//            folder.mkdir();
//
//        //upload to server
//
//        Files.copy(file.getInputStream(), Paths.get(filePath));
//
//        //returning the file
//        return fileName;

        //return full public s3 url directly
        return String.format("https://%s.s3.%s.amazonaws.com/%s",bucketName,region,fileName);
    }
}
