package com.microtf.framework.services;

import com.microtf.framework.dto.BaseResponse;
import com.microtf.framework.dto.article.ArticleDto;
import com.microtf.framework.dto.article.CategoryDto;
import com.microtf.framework.dto.article.RequestArticleList;
import com.microtf.framework.dto.article.RequestCategory;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.ArticleRepository;
import com.microtf.framework.jpa.CategoryRepository;
import com.microtf.framework.jpa.entity.ArticleContentEntity;
import com.microtf.framework.jpa.entity.ArticleEntity;
import com.microtf.framework.jpa.entity.CategoryEntity;
import com.microtf.framework.jpa.entity.SiteEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

/**
 * 文章服务
 *
 * @author guliuzhong
 */
@Service
@Validated
@Slf4j
public class ArticleService {
    /**
     * 类目分类
     */
    private CategoryRepository categoryRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 文章
     */
    private ArticleRepository articleRepository;

    @Autowired
    public void setArticleRepository(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Page<CategoryEntity> getCategory(RequestCategory requestCategory, @NotNull SiteEntity siteEntity) {
        CategoryEntity category = new CategoryEntity();
        category.setSiteEntity(siteEntity);
        if (StringUtils.hasText(requestCategory.getName())) {
            category.setName(requestCategory.getName());
        }
        return categoryRepository.findAll(Example.of(category), PageRequest.of(requestCategory.getPage().getCurrent() - 1, requestCategory.getPage().getPageSize()));
    }

    public CategoryEntity saveCategory(@NotNull CategoryDto categoryDto, @NotNull SiteEntity siteEntity) {
        CategoryEntity categoryExample = new CategoryEntity();
        if (categoryDto.getId() == null) {
            categoryExample.setId(-1L);
        } else {
            categoryExample.setId(categoryDto.getId());
        }
        Optional<CategoryEntity> one = categoryRepository.findOne(Example.of(categoryExample));
        CategoryEntity categoryEntity = one.orElseGet(CategoryEntity::new);
        if (categoryEntity.getId() != null) {
            if (!siteEntity.equals(categoryEntity.getSiteEntity())) {
                throw new BizException("没有权限编辑。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
            }
        } else {
            categoryEntity.setSiteEntity(siteEntity);
            categoryEntity.setCreateTime(new Date());
        }
        categoryEntity.setName(categoryDto.getName());
        categoryEntity.setEnable(categoryDto.getEnable());
        categoryEntity.setUpdateTime(new Date());
        return categoryRepository.save(categoryEntity);
    }

    public void delCategory(@NotNull Long id, @NotNull SiteEntity siteEntity) {
        Optional<CategoryEntity> one = categoryRepository.findById(id);
        if (one.isEmpty()) {
            throw new BizException("没有权限删除。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        CategoryEntity categoryEntity = one.get();
        if (!siteEntity.equals(categoryEntity.getSiteEntity())) {
            throw new BizException("没有权限删除。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        try {
            categoryRepository.delete(categoryEntity);
        } catch (Exception e) {
            log.error("删除类目出错原因{}", e.getMessage());
            throw new BizException("没有权限删除2。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }

    }

    public ArticleEntity getArticle(@NotNull SiteEntity siteEntity, @NotNull Long id) {
        Optional<ArticleEntity> byId = articleRepository.findById(id);
        if (byId.isEmpty()) {
            throw new BizException("内容不存在。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        ArticleEntity articleEntity = byId.get();
        if (!siteEntity.equals(articleEntity.getSiteEntity())) {
            throw new BizException("内容不存在。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        return articleEntity;
    }

    public ArticleEntity getArticleById(@NotNull Long id) {
        Optional<ArticleEntity> byId = articleRepository.findById(id);
        if (byId.isEmpty()) {
            throw new BizException("内容不存在。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        return byId.get();
    }

    @Transactional(rollbackOn = {BizException.class})
    public ArticleEntity saveArticle(@NotNull ArticleDto articleDto, @NotNull SiteEntity siteEntity) {
        ArticleEntity articleEntityExample = new ArticleEntity();
        if (articleDto.getId() == null) {
            articleEntityExample.setId(-1L);
        } else {
            articleEntityExample.setId(articleDto.getId());
        }
        Optional<ArticleEntity> one = articleRepository.findOne(Example.of(articleEntityExample));
        ArticleEntity articleEntity = one.orElseGet(ArticleEntity::new);
        if (articleEntity.getId() != null) {
            if (!siteEntity.equals(articleEntity.getSiteEntity())) {
                throw new BizException("没有权限编辑。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
            }
        } else {
            articleEntity.setSiteEntity(siteEntity);
            articleEntity.setCreateTime(new Date());
        }
        articleEntity.setTitle(articleDto.getTitle());
        articleEntity.setCategoryEntityList(categoryRepository.findAllByIdIn(articleDto.getCategoryId()));
        if (articleEntity.getContent() != null) {
            ArticleContentEntity content = articleEntity.getContent();
            content.setHtml(articleDto.getContent().getHtml());
        } else {
            ArticleContentEntity content = new ArticleContentEntity();
            content.setHtml(articleDto.getContent().getHtml());
            articleEntity.setContent(content);
        }
        articleEntity.setUpdateTime(new Date());
        return articleRepository.save(articleEntity);
    }

    public Page<ArticleEntity> getArticleList(RequestArticleList requestArticleList, @NotNull SiteEntity siteEntity) {
        ArticleEntity articleEntityExample = new ArticleEntity();
        articleEntityExample.setSiteEntity(siteEntity);
        if (StringUtils.hasText(requestArticleList.getTitle())) {
            articleEntityExample.setTitle(requestArticleList.getTitle());
        }

        return articleRepository.findAll(Example.of(articleEntityExample),
                PageRequest.of(requestArticleList.getPage().getCurrent() - 1, requestArticleList.getPage().getPageSize(), Sort.by(new Sort.Order(Sort.Direction.DESC, "updateTime"))));
    }

    public void delArticle(@NotNull Long id, @NotNull SiteEntity siteEntity) {
        Optional<ArticleEntity> byId = articleRepository.findById(id);
        if (byId.isEmpty()) {
            throw new BizException("没有权限删除。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        ArticleEntity articleEntity = byId.get();
        if (!siteEntity.equals(articleEntity.getSiteEntity())) {
            throw new BizException("没有权限删除。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }
        try {
            articleRepository.delete(articleEntity);
        } catch (Exception e) {
            log.error("删除文章出错原因{}", e.getMessage());
            throw new BizException("没有权限删除2。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
        }

    }
}
