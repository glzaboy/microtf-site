package com.microtf.framework.services;

import com.microtf.framework.dto.BaseResponse;
import com.microtf.framework.dto.CategoryDto;
import com.microtf.framework.dto.RequestCategory;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.ArticleRepository;
import com.microtf.framework.jpa.CategoryRepository;
import com.microtf.framework.jpa.entity.CategoryEntity;
import com.microtf.framework.jpa.entity.SiteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

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
        if(categoryDto.getId()==null){
            categoryExample.setId(-1);
        }else{
            categoryExample.setId(categoryDto.getId());
        }
        Optional<CategoryEntity> one = categoryRepository.findOne(Example.of(categoryExample));
        CategoryEntity categoryEntity = one.orElseGet(CategoryEntity::new);
        if(categoryEntity.getId()!=null){
            if(!siteEntity.equals(categoryEntity.getSiteEntity())){
                throw new BizException("没有权限编辑。", BaseResponse.ErrorShowType.ERROR_MESSAGE);
            }
        }else{
            categoryEntity.setSiteEntity(siteEntity);
            categoryEntity.setCreateTime(new Date());
        }
        categoryEntity.setName(categoryDto.getName());
        categoryEntity.setEnable(categoryDto.getEnable());
        categoryEntity.setUpdateTime(new Date());
        return categoryRepository.save(categoryEntity);
    }
}
