package com.microtf.api.controller;

import com.microtf.framework.annotations.Login;
import com.microtf.framework.dto.*;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.entity.CategoryEntity;
import com.microtf.framework.jpa.entity.SiteEntity;
import com.microtf.framework.services.ArticleService;
import com.microtf.framework.services.SiteService;
import com.microtf.framework.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 站点控制器
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
@Api(value = "文章",tags = "article")
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
    @RequestMapping(value = "/getCategory", method = {RequestMethod.POST},produces = {"application/json"})
    @ApiResponse(code = 200,message = "类目列表")
    @ApiOperation(value = "类目列表",notes = "类目列表")
    public ResponsePage<CategoryDto> getCategory(@Validated  @RequestBody RequestCategory requestCategory) {
        SiteEntity currentSite = siteService.getCurrentSite();
        Page<CategoryEntity> list = articleService.getCategory(requestCategory, currentSite);
        return ResponseUtil.responseData(list, CategoryDto.class);
    }
    @Login
    @RequestMapping(value = "/saveCategory", method = {RequestMethod.POST},produces = {"application/json"})
    @ApiResponse(code = 200,message = "保存类目")
    @ApiOperation(value = "保存类目",notes = "保存类目")
    public Response<CategoryDto> saveCategory(@Validated  @RequestBody CategoryDto categoryDto) {
        SiteEntity currentSite = siteService.getCurrentSite();
        if(currentSite==null || currentSite.getId()==null){
            throw new BizException("用户还未设置站点。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        CategoryEntity categoryEntity = articleService.saveCategory(categoryDto, currentSite);
        return ResponseUtil.responseData(categoryEntity, CategoryDto.class);
    }
}
