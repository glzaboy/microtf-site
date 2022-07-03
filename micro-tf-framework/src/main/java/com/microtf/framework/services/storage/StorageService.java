package com.microtf.framework.services.storage;

import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.dto.storage.StorageObjectStream;
import com.microtf.framework.exceptions.BizException;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
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
    default StorageObject upload(byte[] data, String objName) throws BizException {
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
     * @param objName     存储名称
     * @return 存储信息
     * @throws BizException 失败信息
     */
    default StorageObject upload(InputStream inputStream, String objName) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }

    /**
     * 删除存储
     *
     * @param objName 存储名称
     * @return 是否成功
     * @throws BizException 失败信息
     */
    default void delete(String objName) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }
    /**
     * 删除存储
     *
     * @param objNameList 存储名称列表
     * @return 是否成功信息
     * @throws BizException 失败信息
     */
    default List<String> delete(List<String> objNameList) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }

    /**
     * 获取存储信息
     *
     * @param objName 存储名称
     * @return 存储信息
     * @throws BizException 失败信息
     */
    default StorageObject getUrl(String objName) throws BizException {
        throw new UnsupportedOperationException("不支持的功能");
    }
    /**
     * 获取文件输入流
     *
     * @param objName 存储名称
     * @return 文件流
     * @throws BizException 失败信息
     */
    default StorageObjectStream getStream(String objName) throws BizException {
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
