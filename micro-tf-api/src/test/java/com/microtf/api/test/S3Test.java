package com.microtf.api.test;

import com.microtf.api.Application;
import com.microtf.framework.dto.ResponsePage;
import com.microtf.framework.dto.article.CategoryDto;
import com.microtf.framework.dto.common.ResponseList;
import com.microtf.framework.jpa.CategoryRepository;
import com.microtf.framework.jpa.entity.CategoryEntity;
import com.microtf.framework.services.ArticleService;
import com.microtf.framework.services.SiteService;
import com.microtf.framework.services.storage.StorageManagerService;
import com.microtf.framework.services.storage.StorageService;
import com.microtf.framework.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ServiceLoader;

@SpringBootTest(classes = Application.class)
@Slf4j
public class S3Test {
    SiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    ArticleService articleService;

    @Autowired
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Autowired
    StorageManagerService storageManagerService;

    @Autowired
    CategoryRepository categoryRepository;
    @Test
    public void test(){
        storageManagerService.selectStorage("sentany").ifPresent(
                item-> item.upload("This is china an programmer dev".getBytes(StandardCharsets.UTF_8),"sentany/abc.txt",null)
        );
    }
    @Test
    public void test2(){
        Page<CategoryEntity> all = categoryRepository.findAll(PageRequest.of(0,10));
        ResponsePage<CategoryDto> categoryDtoResponsePage = ResponseUtil.responseAsPage(all, CategoryDto.class);
        log.info(String.valueOf(categoryDtoResponsePage));
    }
    @Test
    public void test3(){
        List<CategoryEntity> all = categoryRepository.findAll();
        ResponseList<CategoryDto> listResponseList = ResponseUtil.responseAsList(all, CategoryDto.class);
        log.info(String.valueOf(listResponseList));
    }
    @Test
    public void test4(){
        ServiceLoader<StorageService> load = ServiceLoader.load(StorageService.class);
        for (StorageService item:load){
            System.out.println(item);
        }
    }
}