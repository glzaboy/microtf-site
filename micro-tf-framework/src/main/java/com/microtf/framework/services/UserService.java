package com.microtf.framework.services;

import com.microtf.framework.dto.login.LoginStateDto;
import com.microtf.framework.dto.user.UserDto;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.UserRepository;
import com.microtf.framework.jpa.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * 登录控制器
 *
 * @author glzaboy
 */
@Slf4j
@Service
public class UserService {

    UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    LoginService loginService;

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 获取当前用户Entity
     *
     * @return 当前登录用户的Entity
     */
    public Optional<UserEntity> getCurrentUserEntity() {
        LoginStateDto loginStateDto = loginService.getLoginStateDto();
        if (!loginStateDto.getGuest()) {
            return getUserEntity(loginStateDto.getUserId());
        }
        return Optional.empty();
    }

    /**
     * 根据用户ID获取用户存储 于数据库中对象JPA
     *
     * @param userId 用户ID
     * @return 用户存储
     */
    public Optional<UserEntity> getUserEntity(String userId) {
        return userRepository.findById(Integer.valueOf(userId));
    }

    /**
     * 获取当前用户Dto
     * Guest 时为空
     *
     * @return 用户Dto
     */
    public Optional<UserDto> getCurrentUserDto() {
        LoginStateDto loginStateDto = loginService.getLoginStateDto();
        if (!loginStateDto.getGuest()) {
            return getUserDto(loginStateDto.getUserId());
        }
        return Optional.empty();
    }
    /**
     * 根据用户ID获取用户Dto
     *
     * @param userId 用户ID
     * @return  用户Dto
     */
    public Optional<UserDto> getUserDto(String userId) {
        Optional<UserEntity> byId = getUserEntity(userId);
        if (byId.isPresent()) {
            UserEntity userEntity = byId.get();
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }
    @Transactional(rollbackOn = {BizException.class})
    public void save(UserDto userDto) {
        UserEntity userEntity1 = new UserEntity();
        if ("123".equals(userDto.getPhone())) {
            throw new BizException("电话不正确");
        }
        userEntity1.setId(userDto.getId());
        Optional<UserEntity> one = userRepository.findOne(Example.of(userEntity1));
        if (one.isPresent()) {
            one.ifPresent(userEntityDbItem -> {
                BeanUtils.copyProperties(userDto, userEntityDbItem, "id", "loginToken");
                userRepository.save(userEntityDbItem);
            });
        } else {
            throw new BizException("用户不存在");
        }
    }
}
