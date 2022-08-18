package com.microtf.framework.services.storage;

import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.dto.storage.StorageObjectStream;
import com.microtf.framework.exceptions.BizException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * S3协议存储服务
 * @author guliuzhong
 */
@Slf4j
public class S3StorageService implements StorageService {
    private String pathStart;
    private Config config;

    public void setPathStart(String pathStart) {
        this.pathStart = pathStart;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public S3StorageService() {
    }

    @Override
    public String getPathStart() {
        return pathStart;
    }

    public Config getConfig() {
        return config;
    }

    /**
     * S3客户端
     */
    private MinioClient minioClient;

    /**
     * 获取或初始化S3客户端
     * @return MinioClient客户端
     */
    private MinioClient getClient(){
        if(minioClient!=null){
            return minioClient;
        }
        MinioClient.Builder builder = MinioClient.builder();
        builder.endpoint(config.getEndPoint());
        builder.region(config.getRegion());
        builder.credentials(config.getAccessKeyId(),config.getSecretAccessKey());
        minioClient= builder.build();
        return minioClient;
    }
    @Override
    public StorageObject upload(byte[] data, String objName,String contentType) throws BizException {
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(data);
        return upload(byteArrayInputStream,objName,contentType);
    }
    @Override
    public StorageObject upload(URI url, String objName) throws BizException {
        OkHttpClient.Builder httpClientBuilder=new OkHttpClient.Builder();
        OkHttpClient httpClient = httpClientBuilder.build();
        Request.Builder requestBuilder=new Request.Builder();
        try {
            requestBuilder.url(url.toURL()).get();
        } catch (MalformedURLException e) {
            log.error("S3StorageService-->upload download url {}失败 {}",url,e);
            throw new BizException("上传文件失败，获取远程内容失败");
        }
        try {
            Response execute = httpClient.newCall(requestBuilder.build()).execute();
            if(!execute.isSuccessful()){
                throw new BizException("S3StorageService-->upload response fail");
            }
            ResponseBody body = execute.body();
            if(body==null){
                log.error("S3StorageService-->upload response Body is null");
                throw new BizException("上传文件失败，获取远程内容失败");
            }
            InputStream inputStream = body.byteStream();
            PutObjectArgs.Builder pubObjectArgBuilder = PutObjectArgs.builder();
            pubObjectArgBuilder.bucket(config.getBucket());
            pubObjectArgBuilder.object(objName);
            pubObjectArgBuilder.stream(inputStream,inputStream.available(),-1);
            MediaType mediaType = body.contentType();
            if(mediaType!=null){
                pubObjectArgBuilder.contentType(mediaType.toString());
            }
            ObjectWriteResponse objectWriteResponse = getClient().putObject(pubObjectArgBuilder.build());
            log.info("storage upload result{},{}",objectWriteResponse.etag(),objectWriteResponse.versionId());
            return getUrl(objName);
        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e) {
            log.error("S3StorageService-->upload storage upload fail",e);
            throw new BizException("上传文件失败");
        }
    }

    @Override
    public StorageObject upload(InputStream inputStream, String objName,String contentType) throws BizException {
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)){
            PutObjectArgs.Builder builder = PutObjectArgs.builder();
            builder.bucket(config.getBucket());
            builder.object(objName);
            if(contentType==null){
                contentType = URLConnection.guessContentTypeFromName(objName);
            }
            if(contentType==null){
                contentType=URLConnection.guessContentTypeFromStream(bufferedInputStream);
            }
            builder.contentType(contentType);
            builder.stream(bufferedInputStream,bufferedInputStream.available(),-1);
            ObjectWriteResponse objectWriteResponse = getClient().putObject(builder.build());
            log.info("storage upload result{},{}",objectWriteResponse.etag(),objectWriteResponse.versionId());
            return getUrl(objName);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.error("S3StorageService-->upload storage upload fail",e);
            throw new BizException("上传文件失败");
        }
    }

    @Override
    public void delete(String objName) throws BizException {
        try {
            getClient().removeObject(RemoveObjectArgs.builder().bucket(config.getBucket()).object(objName).build());
        } catch (ErrorResponseException | InternalException | InsufficientDataException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.error("S3StorageService-->delete storage file fail",e);
            throw new BizException("删除文件失败");
        }
    }
    @Override
    public List<String> delete(List<String> objNameList) throws BizException {
        List<DeleteObject> strings = new ArrayList<>();
        for(String item:objNameList){
            DeleteObject deleteObject=new DeleteObject(item);
            strings.add(deleteObject);
        }
        Iterable<Result<DeleteError>> results =getClient().removeObjects(RemoveObjectsArgs.builder().bucket(config.getBucket()).objects(strings).build());
        Iterator<Result<DeleteError>> iterator = results.iterator();
        List<String> ret=new ArrayList<>();
        while (iterator.hasNext()) {
            Result<DeleteError> item = iterator.next();
            DeleteError deleteError;
            try {
                deleteError = item.get();
                ret.add(deleteError.message());
            } catch (ErrorResponseException | InternalException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | InsufficientDataException e) {
                log.error("S3StorageService-->delete storage file fail",e);
                e.printStackTrace();
            }
        }
        return  ret;
    }

    @Override
    public StorageObject getUrl(String objName) throws BizException {
        StorageObject storageObject=new StorageObject();
        storageObject.setObjectName(objName);
        if(config.getIsPrivate()){
            GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder();
            builder.bucket(config.getBucket()).object(objName).expiry(config.getExpiry(), TimeUnit.SECONDS).method(Method.GET);
            try {
                storageObject.setUrl(getClient().getPresignedObjectUrl(builder.build()));
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException | ServerException e) {
                log.error("S3StorageService-->getUrl fail",e);
                throw new BizException("获取文件地址失败");
            }
        }else{
            storageObject.setUrl(config.getUrlHost()+objName);
        }
        return storageObject;
    }

    @Override
    public StorageObjectStream getStream(String objName) throws BizException {
        StorageObjectStream storageObjectStream=new StorageObjectStream();
        storageObjectStream.setObjectName(objName);
        Map<String,String> metaData=new HashMap<>(16);
        GetObjectArgs.Builder builder = GetObjectArgs.builder();
        builder.bucket(config.getBucket()).object(objName);
        try {
            GetObjectResponse object = getClient().getObject(builder.build());
            if (object != null) {
                Headers headers = object.headers();
                for (int i=0;i<headers.size();i++){
                    metaData.put(headers.name(i),headers.value(i));
                }
                storageObjectStream.setMetaData(metaData);
                storageObjectStream.setBufferedInputStream(new BufferedInputStream(object));
                return storageObjectStream;
            }else{
                throw new BizException("获取远程数据出错");
            }
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e) {
            log.error("S3StorageService-->getStream fail",e);
            throw new BizException("获取远程数据出错");
        }
    }
}
