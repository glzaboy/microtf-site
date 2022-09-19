package com.microtf.framework.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author guliuzhong
 */
@Data
public class BaseResponse {
    /**
     * 成功标识
     */
    private Boolean success;
    /**
     * code for errorType
     */
    @ApiModelProperty(value = "错误编码")
    private String errorCode;
    /**
     * message display to user
     */
    @ApiModelProperty(value = "错误信息")
    private String errorMessage;
    /**
     * error display type： 0 silent; 1 message.warn; 2 message.error; 4 notification; 9 page
     */
    @ApiModelProperty(value = "显示类型", notes = "0 silent; 1 message.warn; 2 message.error; 4 notification; 9 page")
    private Integer showType;

    public enum ErrorShowType {
        /**
         * 静默信息
         */
        SILENT(0),
        /**
         * 警告信息
         */
        WARN_MESSAGE(1),
        /**
         * 错误信息
         */
        ERROR_MESSAGE(2),
        /**
         * 通知信息
         */
        NOTIFICATION(4),
        /**
         * 重定向信息
         */
        REDIRECT(9);
        private final Integer errorType;

        ErrorShowType(int errorType) {
            this.errorType = errorType;
        }

        public Integer getErrorType() {
            return errorType;
        }
    }
}
