package com.microtf.framework.services.storage;

import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.exceptions.BizException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.Getter;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 存储服务
 * @author guliuzhong
 */
public class S3StorageService implements StorageService {
    @Setter
    @Getter
    private String pathStart;
    @Setter
    @Getter
    private Config config;
    private MinioClient getClient(){
        MinioClient.Builder builder = MinioClient.builder();
        builder.endpoint(config.getEndPoint());
        builder.region(config.getRegion());
        builder.credentials(config.getAccessKeyId(),config.getSecretAccessKey());
        return builder.build();
    }
    @Override
    public StorageObject upload(byte[] data, String objName) throws BizException {
        StorageObject storageObject=new StorageObject();
        try {
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(data);
            PutObjectArgs.Builder builder = PutObjectArgs.builder();
            builder.bucket(config.getBucket());
            builder.object(objName);
            builder.stream(byteArrayInputStream,byteArrayInputStream.available(),-1);
            byteArrayInputStream.close();
            ObjectWriteResponse objectWriteResponse = getClient().putObject(builder.build());
            storageObject.setObjectName(objName);
            storageObject.setUrl(config.getRootPath()+objName);
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        return storageObject;
    }

    @Override
    public StorageObject upload(URI url, String objName) throws BizException {
        StorageObject storageObject=new StorageObject();
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        OkHttpClient build = builder.build();
        Request.Builder builder1=new Request.Builder();
        try {
            builder1.url(url.toURL()).get();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Response execute = build.newCall(builder1.build()).execute();
            if(execute.isSuccessful()){
                MediaType mediaType = execute.body().contentType();
                byte[] bytes = execute.body().bytes();
                ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
                PutObjectArgs.Builder builder2 = PutObjectArgs.builder();
                builder2.bucket(config.getBucket());
                builder2.object(objName);
                builder2.stream(byteArrayInputStream,byteArrayInputStream.available(),-1);
                builder2.contentType(mediaType.toString());
                try {
                    ObjectWriteResponse objectWriteResponse = getClient().putObject(builder2.build());
                    storageObject.setObjectName(objName);
                    storageObject.setUrl(config.getRootPath()+objName);
                } catch (ErrorResponseException e) {
                    e.printStackTrace();
                } catch (InsufficientDataException e) {
                    e.printStackTrace();
                } catch (InternalException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidResponseException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ServerException e) {
                    e.printStackTrace();
                } catch (XmlParserException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storageObject;
    }

    @Override
    public StorageObject upload(InputStream inputStream, String objName) throws BizException {
        StorageObject storageObject=new StorageObject();
        PutObjectArgs.Builder builder = PutObjectArgs.builder();
        builder.bucket(config.getBucket());
        builder.object(objName);
        try {
            builder.stream(inputStream,inputStream.available(),-1);
            ObjectWriteResponse objectWriteResponse = getClient().putObject(builder.build());
            storageObject.setObjectName(objName);
            storageObject.setUrl(config.getRootPath()+objName);
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        storageObject.setObjectName(objName);
        storageObject.setUrl(config.getRootPath()+objName);
        return storageObject;
    }

    @Override
    public List<String> delete(String objName) throws BizException {
        DeleteObject deleteObject=new DeleteObject(objName);
        List<DeleteObject> strings = new ArrayList<>();
        strings.add(deleteObject);

        Iterable<Result<DeleteError>> results = getClient().removeObjects(RemoveObjectsArgs.builder().bucket(config.getBucket()).objects(strings).build());
        Iterator<Result<DeleteError>> iterator = results.iterator();
        List<String> ret=new ArrayList<>();
        for (Iterator<Result<DeleteError>> it = iterator; it.hasNext(); ) {
            Result<DeleteError> item = it.next();
            DeleteError deleteError = null;
            try {
                deleteError = item.get();
            } catch (ErrorResponseException e) {
                e.printStackTrace();
            } catch (InsufficientDataException e) {
                e.printStackTrace();
            } catch (InternalException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidResponseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (XmlParserException e) {
                e.printStackTrace();
            }
            ret.add(deleteError.message());

        }
        return  ret;

    }

    @Override
    public StorageObject getObject(String objName) throws BizException {
        return StorageService.super.getObject(objName);
    }
}
