package com.microtf.api.controller;

import com.microtf.framework.annotations.Login;
import com.microtf.framework.dto.*;
import com.microtf.framework.dto.article.*;
import com.microtf.framework.dto.site.SiteDto;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.entity.ArticleEntity;
import com.microtf.framework.jpa.entity.CategoryEntity;
import com.microtf.framework.jpa.entity.SiteEntity;
import com.microtf.framework.services.ArticleService;
import com.microtf.framework.services.SiteService;
import com.microtf.framework.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


/**
 * 站点控制器
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
@Api(value = "文章", tags = "article")
@RestController
@RequestMapping("/article")
public class ArticleController {

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

    @Login
    @RequestMapping(value = "/getCategory", method = {RequestMethod.POST}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "类目列表")
    @ApiOperation(value = "类目列表", notes = "类目列表")
    public ResponsePage<CategoryDto> getCategory(@Validated @RequestBody RequestCategory requestCategory) {
        SiteEntity currentSite = siteService.getCurrentSite();
        Page<CategoryEntity> list = articleService.getCategory(requestCategory, currentSite);
        return ResponseUtil.responseAsPage(list, CategoryDto.class);
    }

    /**
     * 保存分类
     *
     * @param categoryDto 分类信息
     * @return 保存结果
     */
    @Login
    @RequestMapping(value = "/saveCategory", method = {RequestMethod.POST}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "保存类目")
    @ApiOperation(value = "保存类目", notes = "保存类目")
    public Response<CategoryDto> saveCategory(@Validated @RequestBody CategoryDto categoryDto) {
        SiteEntity currentSite = siteService.getCurrentSite();
        if (currentSite == null || currentSite.getId() == null) {
            throw new BizException("用户还未设置站点。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        CategoryEntity categoryEntity = articleService.saveCategory(categoryDto, currentSite);
        return ResponseUtil.responseData(categoryEntity, CategoryDto.class);
    }

    @Login
    @RequestMapping(value = "/delCategory", method = {RequestMethod.POST}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "删除类目")
    @ApiOperation(value = "删除类目", notes = "删除类目")
    public Response<String> delCategory(@RequestParam Long id) {
        SiteEntity currentSite = siteService.getCurrentSite();
        if (currentSite == null || currentSite.getId() == null) {
            throw new BizException("用户还未设置站点。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        articleService.delCategory(id, currentSite);
        return ResponseUtil.responseData("OK");
    }

    @Login
    @RequestMapping(value = "/getArticle", method = {RequestMethod.GET}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "获取单篇文章")
    @ApiOperation(value = "获取单篇文章", notes = "获取单篇文章")
    public Response<ArticleDto> getArticle(@RequestParam Long id) {
        SiteEntity currentSite = siteService.getCurrentSite();
        if (currentSite == null || currentSite.getId() == null) {
            throw new BizException("用户还未设置站点。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        ArticleEntity article = articleService.getArticle(currentSite, id);
        return ResponseUtil.responseData(article, (item) -> {
            ArticleDto n = new ArticleDto();
            BeanUtils.copyProperties(item, n);
            SiteDto siteDto = new SiteDto();
            BeanUtils.copyProperties(item.getSiteEntity(), siteDto);
            n.setSiteDto(siteDto);
            ArticleContentDto articleContentDto = new ArticleContentDto();
            BeanUtils.copyProperties(item.getContent(), articleContentDto);
            n.setContent(articleContentDto);
            n.setCategoryId(item.getCategoryEntityList().stream().map(CategoryEntity::getId).toList());
            return n;
        });
    }
    @RequestMapping(value = "/getArticle2", method = {RequestMethod.GET}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "获取单篇文章")
    @ApiOperation(value = "获取单篇文章", notes = "获取单篇文章")
    public Response<ArticleDto> getArticle2(@RequestParam Long id) {
        ArticleEntity article = articleService.getArticleById(id);
        return ResponseUtil.responseData(article, (item) -> {
            ArticleDto n = new ArticleDto();
            BeanUtils.copyProperties(item, n);
            SiteDto siteDto = new SiteDto();
            BeanUtils.copyProperties(item.getSiteEntity(), siteDto);
            n.setSiteDto(siteDto);
            ArticleContentDto articleContentDto = new ArticleContentDto();
            BeanUtils.copyProperties(item.getContent(), articleContentDto);
            n.setContent(articleContentDto);
            n.setCategoryId(item.getCategoryEntityList().stream().map(CategoryEntity::getId).toList());
            return n;
        });
    }
    /**
     * 保存文章
     *
     * @param articleDto 分类信息
     * @return 保存结果
     */
    @Login
    @RequestMapping(value = "/saveArticle", method = {RequestMethod.POST}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "保存文章")
    @ApiOperation(value = "保存文章", notes = "保存文章")
    @Transactional(rollbackOn = {BizException.class})
    public Response<ArticleDto> saveArticle(@Validated @RequestBody ArticleDto articleDto) {
        SiteEntity currentSite = siteService.getCurrentSite();
        if (currentSite == null || currentSite.getId() == null) {
            throw new BizException("用户还未设置站点。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        ArticleEntity articleEntity = articleService.saveArticle(articleDto, currentSite);
        return ResponseUtil.responseData(articleEntity, (item) -> {
            ArticleDto n = new ArticleDto();
            BeanUtils.copyProperties(item, n);
            SiteDto siteDto = new SiteDto();
            BeanUtils.copyProperties(item.getSiteEntity(), siteDto);
            n.setSiteDto(siteDto);
            ArticleContentDto articleContentDto = new ArticleContentDto();
            BeanUtils.copyProperties(item.getContent(), articleContentDto);
            n.setContent(articleContentDto);
            n.setCategoryId(item.getCategoryEntityList().stream().map(CategoryEntity::getId).toList());
            return n;
        });
    }
    @Login
    @RequestMapping(value = "/getArticleList", method = {RequestMethod.POST}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "文章列表")
    @ApiOperation(value = "文章列表", notes = "文章列表")
    public ResponsePage<ArticleDto> getArticleList(@Validated @RequestBody RequestArticleList requestArticleList) {
        SiteEntity currentSite = siteService.getCurrentSite();
        Page<ArticleEntity> articleList = articleService.getArticleList(requestArticleList, currentSite);
        return ResponseUtil.responseAsPage(articleList, (item) -> {
            ArticleDto n = new ArticleDto();
            BeanUtils.copyProperties(item, n);
            SiteDto siteDto = new SiteDto();
            BeanUtils.copyProperties(item.getSiteEntity(), siteDto);
            n.setSiteDto(siteDto);
            ArticleContentDto articleContentDto = new ArticleContentDto();
            BeanUtils.copyProperties(item.getContent(), articleContentDto);
            n.setContent(articleContentDto);
            n.setCategoryId(item.getCategoryEntityList().stream().map(CategoryEntity::getId).toList());
            n.setCategoryDtoList(item.getCategoryEntityList().stream().map((item2)->{
                CategoryDto categoryDto=new CategoryDto();
                BeanUtils.copyProperties(item2,categoryDto);
                return categoryDto;}).toList());
            return n;
        });
    }
    @Login
    @RequestMapping(value = "/delArticle", method = {RequestMethod.POST}, produces = {"application/json"})
    @ApiResponse(code = 200, message = "删除文章")
    @ApiOperation(value = "删除文章", notes = "删除文章")
    public Response<String> delArticle(@RequestParam Long id) {
        SiteEntity currentSite = siteService.getCurrentSite();
        if (currentSite == null || currentSite.getId() == null) {
            throw new BizException("用户还未设置站点。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        articleService.delArticle(id, currentSite);
        return ResponseUtil.responseData("OK");
    }
}
