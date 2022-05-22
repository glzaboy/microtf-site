package com.microtf.framework.autoconfigure;

import com.microtf.framework.dto.login.LoginType;
import com.microtf.framework.services.LoginAuth;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * jwt配置properties
 * @author guliuzhong
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "login")
public class LoginProperties {
    /**
     * 密匙,不设置自动生成
     *
     */
    private String secret;
    /**
     * 有效期 单伴位秒
     */
    private Integer expires=86400;
    /**
     * 签发人
     */
    private String iss;
    /**
     * 是否开启guest模式
     *
     * 开启后guest模式，默认可读，角色是guest，isGuest为真
     *
     */
    private Boolean enableGuest=false;

    /**
     * 开启登录方式
     * ＠see LoginType
     */
    private List<LoginType> enableLoginType;
}
