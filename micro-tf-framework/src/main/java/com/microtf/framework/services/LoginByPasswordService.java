package com.microtf.framework.services;

import com.microtf.framework.dto.login.LoginType;
import com.microtf.framework.dto.login.LoginUser;
import com.microtf.framework.exceptions.LoginException;
import com.microtf.framework.jpa.LoginRepository;
import com.microtf.framework.jpa.entity.LoginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 通过用户名和密码进行登录
 *
 * @author glzaboy
 */
@Service
public class LoginByPasswordService implements LoginAuth {
    LoginRepository loginRepository;

    @Autowired
    public void setLoginUserRepository(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.PASSWORD;
    }

    @Override
    public Optional<LoginUser> login(LoginUser loginUser) throws LoginException {
        LoginEntity loginEntityExample = new LoginEntity();
        loginEntityExample.setLoginType(String.valueOf(getLoginType()));
        loginEntityExample.setLoginId(loginUser.getLoginId());
        Optional<LoginEntity> one = loginRepository.findOne(Example.of(loginEntityExample));
        if (one.isEmpty()) {
            //用户不存在登录失败
            throw new LoginException("登录失败，用户不存在");
        }
        LoginEntity loginEntity = one.get();

        if (!validPassword(loginUser.getPasswd(), loginEntity.getUserEntity().getLoginToken())) {
            //登录失败
            throw new LoginException("登录失败，用户名密码不正确");
        }
        //登录成功
        LoginUser loginUser2 = new LoginUser();
        loginUser2.setLoginId(loginUser.getLoginId());
        loginUser2.setUserId(loginEntity.getUserEntity().getId().toString());
        loginUser2.setLoginType(getLoginType());
        loginUser2.setName(loginEntity.getUserEntity().getName());
        return Optional.of(loginUser2);
    }


}
