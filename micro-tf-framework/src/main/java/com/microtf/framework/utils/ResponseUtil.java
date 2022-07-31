package com.microtf.framework.utils;

import com.microtf.framework.dto.BaseResponse;
import com.microtf.framework.dto.Response;
import com.microtf.framework.dto.ResponsePage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 浏览器返回 数据 util
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 * @version 1.0.0
 */
@Slf4j
public class ResponseUtil {

    /**
     * 返回出错信息
     * @param errorMsg 错误信息
     * @param errorShowType 提示类型
     * @return 错误信息
     */
    public static Response<String> responseError(String  errorMsg, BaseResponse.ErrorShowType errorShowType) {
//        Response<String> bizResponse=new Response<>();
//        bizResponse.setSuccess(false);
//        bizResponse.setErrorMessage(errorMsg);
//        bizResponse.setData("");
//        bizResponse.setShowType(errorShowType.getErrorType());
//        bizResponse.setErrorCode("1");
        return responseError(errorMsg,errorShowType,String.class);
    }
    /**
     * 返回出错信息
     * @param errorMsg 错误信息
     * @param errorShowType 提示类型
     * @param classiz
     * @param <T>
     * @return 错误信息
     */
    public static<T extends Serializable> Response<T> responseError(String  errorMsg, BaseResponse.ErrorShowType errorShowType,Class<T> classiz) {
        Response<T> bizResponse=new Response<>();
        bizResponse.setSuccess(false);
        bizResponse.setErrorMessage(errorMsg);
        bizResponse.setData(null);
        bizResponse.setShowType(errorShowType.getErrorType());
        bizResponse.setErrorCode("1");
        return bizResponse;
    }

    /**
     * 返回出错信息，界面上做提示
     * @param errorMsg 错误信息
     * @return 错误信息
     */
    public static Response<String> responseError(String  errorMsg) {
        return responseError(errorMsg,Response.ErrorShowType.ERROR_MESSAGE);
    }
    public static<T extends Serializable> Response<T> responseData(T  data) {
        Response<T> bizResponse=new Response<>();
        bizResponse.setSuccess(true);
        bizResponse.setData(data);
        bizResponse.setShowType(Response.ErrorShowType.REDIRECT.getErrorType());
        bizResponse.setErrorCode("1");
        return bizResponse;
    }
    public static<T,S extends Serializable> Response<S> responseData(T  data, Class<S> clazz) {
        Response<S> bizResponse=new Response<>();
        bizResponse.setSuccess(true);
        S s = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(data,s);
        bizResponse.setData(s);
        bizResponse.setShowType(Response.ErrorShowType.REDIRECT.getErrorType());
        bizResponse.setErrorCode("1");
        return bizResponse;
    }

    public static<T,S extends Serializable> Response<S> responseData(T  data, Function<T,S> function) {
        Response<S> bizResponse=new Response<>();
        bizResponse.setSuccess(true);
        S apply = function.apply(data);
        bizResponse.setData(apply);
        bizResponse.setShowType(Response.ErrorShowType.REDIRECT.getErrorType());
        bizResponse.setErrorCode("1");
        return bizResponse;
    }
    public static<T,S extends Serializable> ResponsePage<S> responseData(Page<T> data, Class<S> clazz) {
        ResponsePage<S> bizResponse=new ResponsePage<>();
        bizResponse.setSuccess(true);
        bizResponse.setTotal(data.getTotalElements());
        bizResponse.setCurrent(data.getPageable().getPageNumber());
        bizResponse.setPageSize(data.getPageable().getPageSize());
        List<T> content = data.getContent();
        List<S> dataList=new ArrayList<>();
        for (T item:content){
            S s= BeanUtils.instantiateClass(clazz);
            BeanUtils.copyProperties(item,s);
            dataList.add(s);
        }
        bizResponse.setData(dataList);
        bizResponse.setErrorCode("1");
        return bizResponse;
    }
    public static<T,S extends Serializable> ResponsePage<S> responseDataPage(Page<T> data, Function<T,S> function) {
        ResponsePage<S> bizResponse=new ResponsePage<>();
        bizResponse.setSuccess(true);
        bizResponse.setTotal(data.getTotalElements());
        bizResponse.setCurrent(data.getPageable().getPageNumber());
        bizResponse.setPageSize(data.getPageable().getPageSize());
        List<T> content = data.getContent();
        List<S> dataList=new ArrayList<>();
        for (T item:content){
            S apply = function.apply(item);
            dataList.add(apply);
        }
        bizResponse.setData(dataList);
        bizResponse.setErrorCode("1");
        return bizResponse;
    }
}
