package com.microtf.framework.utils;

import com.microtf.framework.dto.BaseResponse;
import com.microtf.framework.dto.Response;
import com.microtf.framework.dto.ResponsePage;
import com.microtf.framework.dto.common.ResponseList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 浏览器返回 数据 util
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 * @version 1.0.0
 */
@Slf4j
public class ResponseUtil {

    /**
     * 返回出错信息
     *
     * @param errorMsg      错误信息
     * @param errorShowType 提示类型
     * @return 错误信息
     */
    public static Response<String> responseError(String errorMsg, BaseResponse.ErrorShowType errorShowType) {
        return responseError(errorMsg, errorShowType, String.class);
    }

    /**
     * 返回出错信息
     *
     * @param errorMsg      错误信息
     * @param errorShowType 提示类型
     * @param clazz         返回数据类型
     * @param <T>           返回数据类型
     * @return 错误信息
     */
    public static <T extends Serializable> Response<T> responseError(String errorMsg, BaseResponse.ErrorShowType errorShowType, Class<T> clazz) {
        Response<T> bizResponse = new Response<>();
        bizResponse.setSuccess(false);
        bizResponse.setErrorMessage(errorMsg);
        bizResponse.setData(null);
        bizResponse.setShowType(errorShowType.getErrorType());
        bizResponse.setErrorCode("1");
        return bizResponse;
    }

    /**
     * 返回出错信息，界面上做提示
     *
     * @param errorMsg 错误信息
     * @return 错误信息
     */
    public static Response<String> responseError(String errorMsg) {
        return responseError(errorMsg, Response.ErrorShowType.ERROR_MESSAGE);
    }

    public static <T extends Serializable> Response<T> responseData(T data) {
        return responseData(data, (item) -> item);
    }

    public static <T, S extends Serializable> Response<S> responseData(T data, Class<S> clazz) {
        return responseData(data, (item) -> {
            S s = BeanUtils.instantiateClass(clazz);
            BeanUtils.copyProperties(item, s);
            return s;
        });
    }

    public static <T, S extends Serializable> Response<S> responseData(T data, Function<T, S> function) {
        Response<S> bizResponse = new Response<>();
        bizResponse.setSuccess(true);
        S apply = function.apply(data);
        bizResponse.setData(apply);
        bizResponse.setShowType(Response.ErrorShowType.REDIRECT.getErrorType());
        bizResponse.setErrorCode("1");
        return bizResponse;
    }

    public static <T, S extends Serializable> ResponseList<S> responseAsList(List<T> data, Class<S> clazz) {
        return responseAsList(data, (item) -> {
            S s = BeanUtils.instantiateClass(clazz);
            BeanUtils.copyProperties(item, s);
            return s;
        });
    }

    public static <T, S extends Serializable> ResponseList<S> responseAsList(List<T> data, Function<T, S> function) {
        ResponseList<S> bizResponse = new ResponseList<>();
        bizResponse.setSuccess(true);
        List<S> dataList = new ArrayList<>();
        for (T item : data) {
            S apply = function.apply(item);
            dataList.add(apply);
        }
        bizResponse.setData(dataList);
        bizResponse.setErrorCode("1");
        return bizResponse;
    }

    public static <T, S extends Serializable> ResponsePage<S> responseAsPage(Page<T> data, Class<S> clazz) {
        return responseAsPage(data, (item) -> {
            S s = BeanUtils.instantiateClass(clazz);
            BeanUtils.copyProperties(item, s);
            return s;
        });
    }

    public static <T, S extends Serializable> ResponsePage<S> responseAsPage(Page<T> data, Function<T, S> function) {
        ResponsePage<S> bizResponse = new ResponsePage<>();
        bizResponse.setSuccess(true);
        bizResponse.setTotal(data.getTotalElements());
        bizResponse.setCurrent(data.getPageable().getPageNumber());
        bizResponse.setPageSize(data.getPageable().getPageSize());
        List<T> content = data.getContent();
        List<S> dataList = new ArrayList<>();
        for (T item : content) {
            S apply = function.apply(item);
            dataList.add(apply);
        }
        bizResponse.setData(dataList);
        bizResponse.setErrorCode("1");
        return bizResponse;
    }
}
