package com.microtf.api.test;

import com.microtf.api.Application;
import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.dto.storage.StorageObjectStream;
import com.microtf.framework.services.storage.S3StorageService;
import com.microtf.framework.services.storage.StorageManagerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = Application.class)
@Slf4j
public class S3Test {
//    @Autowired
//    S3StorageService s3StorageService;
    @Autowired
    StorageManagerService storageManagerService;
//    @Test
//    public void test(){
//        StorageObject aa = s3StorageService.upload("abc".getBytes(StandardCharsets.UTF_8), "aa.txt");
//        log.info(aa.toString());
//    }
//    @Test
//    public void test2(){
//        s3StorageService.delete("aa.txt");
//    }
//    @Test
//    public void test3(){
//        StorageObject upload = s3StorageService.upload(URI.create("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF"), "a.gif");
//
//
//        log.info(String.valueOf(upload));
//    }
//    @Test
//    public void test4(){
//        try {
//            FileInputStream fileInputStream = new FileInputStream("/Users/guliuzhong/Desktop/zbj.png");
//            StorageObject aa = s3StorageService.upload(fileInputStream.readAllBytes(), "abcdef");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void test5(){
//        try {
//            FileInputStream fileInputStream = new FileInputStream("/Users/guliuzhong/Desktop/zbj.png");
//            StorageObject aa = s3StorageService.upload(fileInputStream, "abcdefg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void test6(){
//        StorageObject aa = s3StorageService.getUrl("a.gif");
//        System.out.printf(String.valueOf(aa));
//    }
//    @Test
//    public void test7(){
//        StorageObjectStream aa = s3StorageService.getStream("a.gif");
//
//        try {
//            FileOutputStream fileOutputStream=new FileOutputStream(new File("abc.gif"));
//            aa.getBufferedInputStream().transferTo(
//                    fileOutputStream
//            );
//            fileOutputStream.close();
//            aa.getBufferedInputStream().close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        System.out.printf(String.valueOf(aa));
//    }
    @Test
    public void test8(){
        final String objName="s3/abc.txt";
        storageManagerService.selectStorage(objName).ifPresent(item-> item.upload("china".getBytes(StandardCharsets.UTF_8),objName));
    }
}