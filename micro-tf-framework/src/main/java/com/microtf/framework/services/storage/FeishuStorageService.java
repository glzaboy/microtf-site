package com.microtf.framework.services.storage;

import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.dto.storage.StorageObjectStream;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.services.storage.feishu.RootDirInfo;
import com.microtf.framework.services.storage.feishu.Token;
import com.microtf.framework.services.storage.feishu.TokenInput;
import com.microtf.framework.services.storage.feishu.UploadInfo;
import com.microtf.framework.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * S3协议存储服务
 * @author guliuzhong
 */
@Slf4j
public class FeishuStorageService implements StorageService {
    private String pathStart;
    private Config config;

    public void setPathStart(String pathStart) {
        this.pathStart = pathStart;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public FeishuStorageService() {
    }

    @Override
    public String getPathStart() {
        return pathStart;
    }

    public Config getConfig() {
        return config;
    }

    public static BiFunction<String,String, HttpUtil.HttpAuthReturn> httpBearValue=(String user, String pwd)-> {
        HttpUtil.HttpAuthReturn.HttpAuthReturnBuilder builder1 = HttpUtil.HttpAuthReturn.builder();
        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.url("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal");
        builder.method(HttpUtil.Method.JSON).postObject(TokenInput.builder().appId(user).appSecret(pwd).build());
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
        if(sent.getStatus().intValue()==200){
            Token json = sent.json(Token.class);
            if(json.getCode()==0){
                builder1.authValue("Bearer "+json.getTenantAccessToken());
                return builder1.build();
            }
            return null;
        }else {
            return null;
        }
    };
    public RootDirInfo getRootDir(){
        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.url("https://open.feishu.cn/open-apis/drive/explorer/v2/root_folder/meta");
        builder.method(HttpUtil.Method.GET)
                .auth(HttpUtil.HttpAuth.builder().user(config.getAccessKeyId()).pwd(config.getSecretAccessKey()).build())
                .authFunction(httpBearValue).build();
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
        if(sent.getStatus().intValue()==200){
            return sent.json(RootDirInfo.class);
        }else {
            return null;
        }
    }
    @Override
    public StorageObject upload(byte[] data, String objName,String contentType) throws BizException {
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(data);
        return upload(byteArrayInputStream,objName,contentType);
    }
    @Override
    public StorageObject upload(URI url, String objName) throws BizException {
        return null;
//        OkHttpClient.Builder httpClientBuilder=new OkHttpClient.Builder();
//        OkHttpClient httpClient = httpClientBuilder.build();
//        Request.Builder requestBuilder=new Request.Builder();
//        try {
//            requestBuilder.url(url.toURL()).get();
//        } catch (MalformedURLException e) {
//            log.error("S3StorageService-->upload download url {}失败 {}",url,e);
//            throw new BizException("上传文件失败，获取远程内容失败");
//        }
//        try {
//            Response execute = httpClient.newCall(requestBuilder.build()).execute();
//            if(!execute.isSuccessful()){
//                throw new BizException("S3StorageService-->upload response fail");
//            }
//            ResponseBody body = execute.body();
//            if(body==null){
//                log.error("S3StorageService-->upload response Body is null");
//                throw new BizException("上传文件失败，获取远程内容失败");
//            }
////            InputStream inputStream = body.byteStream();
////            PutObjectArgs.Builder pubObjectArgBuilder = PutObjectArgs.builder();
////            pubObjectArgBuilder.bucket(config.getBucket());
////            pubObjectArgBuilder.object(objName);
////            pubObjectArgBuilder.stream(inputStream,inputStream.available(),-1);
////            MediaType mediaType = body.contentType();
////            if(mediaType!=null){
////                pubObjectArgBuilder.contentType(mediaType.toString());
////            }
////            ObjectWriteResponse objectWriteResponse = getClient().putObject(pubObjectArgBuilder.build());
//            log.info("storage upload result{},{}",objectWriteResponse.etag(),objectWriteResponse.versionId());
//            return getUrl(objName);
//        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e) {
//            log.error("S3StorageService-->upload storage upload fail",e);
//            throw new BizException("上传文件失败");
//        }
    }

    @Override
    public StorageObject upload(InputStream inputStream, String objName,String contentType) throws BizException {
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)){
            RootDirInfo rootDir = getRootDir();
            log.info(rootDir.toString());
            HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
            Map<String,String> formData=new HashMap<>();
            formData.put("parent_node",rootDir.getData().getToken());
            formData.put("parent_type","explorer");
            formData.put("file_name",objName);
            byte[] bytes = inputStream.readAllBytes();
            formData.put("size", String.valueOf(bytes.length));
            Map<String, HttpUtil.File> postFile=new HashMap<>();

            postFile.put("file",HttpUtil.File.builder().contentType(contentType).fileName(objName).content(bytes).build());
            builder.method(HttpUtil.Method.FILE).url("https://open.feishu.cn/open-apis/drive/v1/files/upload_all")
                            .postFile(postFile).form(formData);
            builder.auth(HttpUtil.HttpAuth.builder().user(config.getAccessKeyId()).pwd(config.getSecretAccessKey()).build())
                    .authFunction(httpBearValue);
            HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
            UploadInfo json = sent.json(UploadInfo.class);
            log.info("上传结果{}",json);
            return getUrl(json.getData().getFileToken());
        } catch (IOException e) {
            log.error("S3StorageService-->upload storage upload fail",e);
            throw new BizException("上传文件失败");
        }
    }

    @Override
    public void delete(String objName) throws BizException {
        return;
//        try {
//            getClient().removeObject(RemoveObjectArgs.builder().bucket(config.getBucket()).object(objName).build());
//        } catch (ErrorResponseException | InternalException | InsufficientDataException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
//            log.error("S3StorageService-->delete storage file fail",e);
//            throw new BizException("删除文件失败");
//        }
    }
    @Override
    public List<String> delete(List<String> objNameList) throws BizException {
        return null;
//        List<DeleteObject> strings = new ArrayList<>();
//        for(String item:objNameList){
//            DeleteObject deleteObject=new DeleteObject(item);
//            strings.add(deleteObject);
//        }
//        Iterable<Result<DeleteError>> results =getClient().removeObjects(RemoveObjectsArgs.builder().bucket(config.getBucket()).objects(strings).build());
//        Iterator<Result<DeleteError>> iterator = results.iterator();
//        List<String> ret=new ArrayList<>();
//        while (iterator.hasNext()) {
//            Result<DeleteError> item = iterator.next();
//            DeleteError deleteError;
//            try {
//                deleteError = item.get();
//                ret.add(deleteError.message());
//            } catch (ErrorResponseException | InternalException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | InsufficientDataException e) {
//                log.error("S3StorageService-->delete storage file fail",e);
//                e.printStackTrace();
//            }
//        }
//        return  ret;
    }

    @Override
    public StorageObject getUrl(String objName) throws BizException {
        StorageObject storageObject=new StorageObject();
        storageObject.setObjectName(objName);
        return storageObject;
    }

    @Override
    public StorageObjectStream getStream(String objName) throws BizException {
        return null;
//        StorageObjectStream storageObjectStream=new StorageObjectStream();
//        storageObjectStream.setObjectName(objName);
//        Map<String,String> metaData=new HashMap<>(16);
//        GetObjectArgs.Builder builder = GetObjectArgs.builder();
//        builder.bucket(config.getBucket()).object(objName);
//        try {
//            GetObjectResponse object = getClient().getObject(builder.build());
//            if (object != null) {
//                Headers headers = object.headers();
//                for (int i=0;i<headers.size();i++){
//                    metaData.put(headers.name(i),headers.value(i));
//                }
//                storageObjectStream.setMetaData(metaData);
//                storageObjectStream.setBufferedInputStream(new BufferedInputStream(object));
//                return storageObjectStream;
//            }else{
//                throw new BizException("获取远程数据出错");
//            }
//        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e) {
//            log.error("S3StorageService-->getStream fail",e);
//            throw new BizException("获取远程数据出错");
//        }
    }
}
