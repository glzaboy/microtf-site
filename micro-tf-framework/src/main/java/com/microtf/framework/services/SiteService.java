package com.microtf.framework.services;

import com.microtf.framework.dto.site.SiteDto;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.SiteRepository;
import com.microtf.framework.jpa.entity.SiteEntity;
import com.microtf.framework.jpa.entity.UserEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * 站点设置
 *
 * @author glzaboy
 */
@Service
public class SiteService {
    private SiteRepository siteRepository;

    @Autowired
    public void setSiteRepository(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户站点信息
     *
     * @return 获取当前用户默认站点
     */
    @Transactional(rollbackOn = BizException.class)
    public SiteEntity saveSite(SiteEntity siteEntity) {
        return siteRepository.save(siteEntity);
//        throw new BizException("此用户未绑定到站点");
    }

    /**
     * 获取当前用户站点信息
     *
     * @return 获取当前用户默认站点
     */
    public SiteEntity getCurrentSite() {
        Optional<UserEntity> currentUserEntity = userService.getCurrentUserEntity();
        if (currentUserEntity.isPresent()) {
            return currentUserEntity.get().getSiteEntity();
        }
        throw new BizException("此用户未绑定到站点");
    }

    /**
     * 获取当前用户站点信息
     *
     * @return 获取当前用户默认站点
     */
    @Validated
    public SiteEntity saveCurrentSite(SiteDto siteDto) {
        SiteEntity currentSite = getCurrentSite();
        if (currentSite != null) {
            BeanUtils.copyProperties(siteDto, currentSite, "id");
            return saveSite(currentSite);
        } else {
            throw new BizException("当前用户未设置站点，请联系管理员");
        }
    }
}
