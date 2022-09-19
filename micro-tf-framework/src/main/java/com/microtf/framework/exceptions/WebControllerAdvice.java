package com.microtf.framework.exceptions;

import com.microtf.framework.utils.ResponseUtil;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * 统一处理异常功能
 *
 * @author glzaboy
 */
@ControllerAdvice
public class WebControllerAdvice {
    /**
     * 登录提醒
     *
     * @param e 异常信息
     * @return 返回客户端内容
     */
    @ExceptionHandler(LoginException.class)
    @ResponseBody
    public Object loginException(LoginException e) {
        return ResponseUtil.responseError(e.getMessage());
    }

    /**
     * 登录提醒
     *
     * @param e 异常信息
     * @return 返回客户端内容
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public Object bizException(BizException e) {
        if (e.errorShowType != null) {
            return ResponseUtil.responseError(e.getMessage(), e.errorShowType);
        }
        return ResponseUtil.responseError(e.getMessage());
    }

    /**
     * Controller层参数Valid出错统一处理
     *
     * @param e 异常信息
     * @return 返回客户端内容
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Object methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError error : allErrors) {
            stringBuilder.append(error.getDefaultMessage()).append(",");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        stringBuilder.append("。");
        return ResponseUtil.responseError(stringBuilder.toString());
    }

    /**
     * Service层参数Valid出错统一处理
     *
     * @param e 异常信息
     * @return 返回客户端内容
     */
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Object constraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder stringBuilder = new StringBuilder();

        for (ConstraintViolation error : constraintViolations) {
            stringBuilder.append(error.getMessage()).append(",");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        stringBuilder.append("。");
        return ResponseUtil.responseError(stringBuilder.toString());
    }
}
