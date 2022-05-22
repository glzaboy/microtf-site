package com.microtf.api.controller;

import com.microtf.framework.annotations.Login;
import com.microtf.framework.dto.Response;
import com.microtf.framework.dto.site.SiteDto;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.entity.SiteEntity;
import com.microtf.framework.services.SiteService;
import com.microtf.framework.services.UserService;
import com.microtf.framework.utils.ResponseUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 站点控制器
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
@Api(value = "站点",tags = "site")
@RestController
@RequestMapping("/site")
public class SiteController {

    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    SiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @Login
    @RequestMapping(value = "/getCurrentSite", method = {RequestMethod.GET})
    public Response<SiteDto> getCurrentSite() {
        SiteDto siteDto = new SiteDto();
        SiteEntity currentSite = siteService.getCurrentSite();
        if (currentSite != null) {
            BeanUtils.copyProperties(currentSite, siteDto);
            return ResponseUtil.responseData(siteDto);
        } else {
            throw new BizException("未找到站点信息");
        }
    }
    @Login
    @RequestMapping(value = "/saveCurrentSite", method = {RequestMethod.GET})
    public Response<SiteDto> saveSite(SiteDto siteDto) {
        SiteEntity siteEntity = siteService.saveCurrentSite(siteDto);
        return ResponseUtil.responseData(siteDto);
    }
}
