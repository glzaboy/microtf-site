package com.microtf.api.test;

import com.microtf.api.Application;
import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.services.storage.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest(classes = Application.class)
@Slf4j
public class S3Test {
    @Autowired
    S3StorageService s3StorageService;
    @Test
    public void test(){
        StorageObject aa = s3StorageService.upload("abc".getBytes(StandardCharsets.UTF_8), "aa.txt");
        log.info(aa.toString());
    }
    @Test
    public void test2(){
        List<String> aa = s3StorageService.delete("aa");

        log.info(aa.toString());
    }
    @Test
    public void test3(){
        StorageObject upload = s3StorageService.upload(URI.create("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF"), "a.gif");


        log.info(String.valueOf(upload));
    }
    @Test
    public void test4(){
        try {
            FileInputStream fileInputStream = new FileInputStream("/Users/guliuzhong/Desktop/zbj.png");
            StorageObject aa = s3StorageService.upload(fileInputStream.readAllBytes(), "abcdef");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test5(){
        try {
            FileInputStream fileInputStream = new FileInputStream("/Users/guliuzhong/Desktop/zbj.png");
            StorageObject aa = s3StorageService.upload(fileInputStream, "abcdefg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
