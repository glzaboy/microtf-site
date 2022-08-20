package com.microtf.framework.compontent;

import com.microtf.framework.dto.login.LoginStateDto;
import com.microtf.framework.services.LoginService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class AuditorComponent implements AuditorAware<Long> {
    LoginService loginService;

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    @NotNull
    @Override
    public Optional<Long> getCurrentAuditor() {
        LoginStateDto loginStateDto = loginService.getLoginStateDto();
        if(loginStateDto.getGuest()){
            return Optional.empty();
        }else{
            return Optional.of(Long.valueOf(loginStateDto.getUserId()));
        }

    }
}
