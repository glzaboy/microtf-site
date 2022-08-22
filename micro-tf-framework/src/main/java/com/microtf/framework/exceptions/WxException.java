package com.microtf.framework.exceptions;

import com.microtf.framework.dto.BaseResponse;

/**
 * 业务异常信息
 * @author glzaboy
 */
public class WxException extends RuntimeException{
    public BaseResponse.ErrorShowType errorShowType= BaseResponse.ErrorShowType.ERROR_MESSAGE;
    public WxException(String message) {
        super(message);
    }

    public WxException(String message, BaseResponse.ErrorShowType errorShowType) {
        super(message);
        this.errorShowType = errorShowType;
    }
}
