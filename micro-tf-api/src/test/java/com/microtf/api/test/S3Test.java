package com.microtf.api.test;

import com.microtf.api.Application;
import com.microtf.framework.services.storage.StorageManagerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = Application.class)
@Slf4j
public class S3Test {
    @Autowired
    StorageManagerService storageManagerService;
    @Test
    public void test(){
        storageManagerService.selectStorage("sentany").ifPresent(
                item->{
                    item.upload("This is china an programmer dev".getBytes(StandardCharsets.UTF_8),"sentany/abc.txt");
                }
        );
    }
}