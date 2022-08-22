package com.microtf.framework.services.storage;

import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.dto.storage.StorageObjectStream;
import com.microtf.framework.exceptions.BizException;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * 存储服务
 *
 * @author guliuzhong
 */
public interface StorageService {
    /**
     * 保存内容到存储
     *
     * @param data    存储的内容
     * @param objName 存储名称
     * @return 存储信息
     * @throws BizException 失败信息
     */
    default StorageObject upload(byte[] data, String objName,String contentType) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }

    /**
     * 保存指定url的内容到存储
     *
     * @param url     URL地址
     * @param objName 存储名称
     * @return 存储信息
     * @throws BizException 失败信息
     */
    default StorageObject upload(URI url, String objName) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }

    /**
     * 将inputStream内容保存到存储
     *
     * @param inputStream inputStream
     * @param objName     存储名称ID
     * @return 存储信息
     * @throws BizException 失败信息
     */
    default StorageObject upload(InputStream inputStream, String objName,String contentType) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }

    /**
     * 删除存储
     *
     * @param objectId 存储名称ID
     * @throws BizException 失败信息
     */
    default void delete(String objectId) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }
    /**
     * 删除存储
     *
     * @param objectIdList 存储名称列表ID
     * @return 是否成功信息
     * @throws BizException 失败信息
     */
    default List<String> delete(List<String> objectIdList) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }

    /**
     * 获取存储信息
     *
     * @param objectId 存储名称ID
     * @return 存储信息
     * @throws BizException 失败信息
     */
    default StorageObject getUrl(String objectId) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }
    /**
     * 获取文件输入流
     *
     * @param objectId 存储名称ID
     * @return 文件流
     * @throws BizException 失败信息
     */
    default StorageObjectStream getStream(String objectId) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }

    /**
     * 存储路径开始点，
     * 用于选择存储点
     */
    default String getPathStart() {
        throw new UnsupportedOperationException("不支持的功能");
    }
}
