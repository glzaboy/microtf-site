package com.microtf.framework.services.storage;

import com.microtf.framework.dto.storage.StorageObject;
import com.microtf.framework.dto.storage.StorageObjectStream;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.jpa.FeishuUserTokenRepository;
import com.microtf.framework.jpa.entity.FeishuUserToken;
import com.microtf.framework.services.storage.feishu.RootDirInfo;
import com.microtf.framework.services.storage.feishu.Token;
import com.microtf.framework.services.storage.feishu.TokenInput;
import com.microtf.framework.services.storage.feishu.UploadInfo;
import com.microtf.framework.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    RedisTemplate<String, HttpUtil.HttpAuthReturn> redisTemplate;

    @Resource
    public void setRedisTemplate(RedisTemplate<String, HttpUtil.HttpAuthReturn> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    FeishuUserTokenRepository feishuUserTokenRepository;

    @Autowired
    public void setFeishuUserTokenRepository(FeishuUserTokenRepository feishuUserTokenRepository) {
        this.feishuUserTokenRepository = feishuUserTokenRepository;
    }

    public Function<HttpUtil.HttpAuth, HttpUtil.HttpAuthReturn> httpBearValue=(HttpUtil.HttpAuth httpAuth)-> {
        if(httpAuth.getOpenUserId()==null){//走应用api
            HttpUtil.HttpAuthReturn httpAuthReturn = redisTemplate.opsForValue().get(httpAuth.getUser() + httpAuth.getPwd());
            if(httpAuthReturn!=null){
                return httpAuthReturn;
            }
            HttpUtil.HttpAuthReturn httpAuthReturn1 = new HttpUtil.HttpAuthReturn();
            HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
            builder.url("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal");
            builder.method(HttpUtil.Method.JSON).postObject(TokenInput.builder().appId(httpAuth.getUser()).appSecret(httpAuth.getPwd()).build());
            HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
            if(sent.getStatus() ==200){
                Token json = sent.json(Token.class);
                if(json.getCode()==0){
                    httpAuthReturn1.setAuthValue("Bearer "+json.getTenantAccessToken());
                    redisTemplate.opsForValue().set(httpAuth.getUser() + httpAuth.getPwd(),httpAuthReturn1,json.getExpire()-1800, TimeUnit.SECONDS);
                    return httpAuthReturn1;
                }
                return null;
            }else {
                return null;
            }
        }else{
            HttpUtil.HttpAuthReturn httpAuthReturn = redisTemplate.opsForValue().get("fsUserToken"+httpAuth.getUser() + httpAuth.getOpenUserId());
            if(httpAuthReturn!=null){
                return httpAuthReturn;
            }
            FeishuUserToken token=new FeishuUserToken();
            token.setAppId(httpAuth.getUser());
            token.setOpenId(httpAuth.getOpenUserId());
            Optional<FeishuUserToken> one = feishuUserTokenRepository.findOne(Example.of(token));
            HttpUtil.HttpAuthReturn httpAuthReturn1 = new HttpUtil.HttpAuthReturn();
            if(one.isPresent()){
                FeishuUserToken feishuUserToken = one.get();
                httpAuthReturn1.setAuthValue("Bearer "+feishuUserToken.getAccessToken());
                redisTemplate.opsForValue().set("fsUserToken"+httpAuth.getUser() + httpAuth.getOpenUserId(),httpAuthReturn1,60, TimeUnit.SECONDS);
                return httpAuthReturn1;
            }
            return null;
        }
    };
    private RootDirInfo getRootDir(){
        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        builder.url("https://open.feishu.cn/open-apis/drive/explorer/v2/root_folder/meta");
        builder.method(HttpUtil.Method.GET)
                .auth(HttpUtil.HttpAuth.builder().user(config.getAccessKeyId()).pwd(config.getSecretAccessKey()).openUserId(config.getOpenUserId()).build())
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
        OkHttpClient.Builder httpClientBuilder=new OkHttpClient.Builder();
        OkHttpClient httpClient = httpClientBuilder.build();
        Request.Builder requestBuilder=new Request.Builder();
        try {
            requestBuilder.url(url.toURL()).get();
        } catch (MalformedURLException e) {
            log.error("FeishuStorageService-->upload download url {}失败 {}",url,e);
            throw new BizException("上传文件失败，获取远程内容失败");
        }
        try {
            Response execute = httpClient.newCall(requestBuilder.build()).execute();
            if(!execute.isSuccessful()){
                throw new BizException("FeishuStorageService-->upload response fail");
            }
            ResponseBody body = execute.body();
            if(body==null){
                log.error("FeishuStorageService-->upload response Body is null");
                throw new BizException("上传文件失败，获取远程内容失败");
            }
            return upload(body.byteStream(),objName,body.contentType().toString());
        } catch (IOException e) {
            log.error("FeishuStorageService-->upload storage upload fail",e);
            throw new BizException("上传文件失败");
        }
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
            builder.auth(HttpUtil.HttpAuth.builder().user(config.getAccessKeyId()).pwd(config.getSecretAccessKey()).openUserId(config.getOpenUserId()).build())
                    .authFunction(httpBearValue);
            HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
            UploadInfo json = sent.json(UploadInfo.class);
            log.info("上传结果{}",json);
            return getUrl(json.getData().getFileToken());
        } catch (IOException e) {
            log.error("FeishuStorageService-->upload storage upload fail",e);
            throw new BizException("上传文件失败");
        }
    }

    @Override
    public void delete(String objectId) throws BizException {
        return;
//        try {
//            getClient().removeObject(RemoveObjectArgs.builder().bucket(config.getBucket()).object(objName).build());
//        } catch (ErrorResponseException | InternalException | InsufficientDataException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
//            log.error("S3StorageService-->delete storage file fail",e);
//            throw new BizException("删除文件失败");
//        }
    }
    @Override
    public List<String> delete(List<String> objectIdList) throws BizException {
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
    public StorageObject getUrl(String objectId) throws BizException {
        StorageObject storageObject=new StorageObject();
        storageObject.setObjectId(objectId);
        storageObject.setUrl(config.getUrlHost()+objectId);
        return storageObject;
    }

    @Override
    public StorageObjectStream getStream(String objectId) throws BizException {
        StorageObjectStream storageObjectStream=new StorageObjectStream();
        storageObjectStream.setObjectId(objectId);
        Map<String,String> metaData=new HashMap<>(16);
        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
        Map<String,String> pathVar=new HashMap<>();
        pathVar.put("file_token",objectId);
        builder.method(HttpUtil.Method.GET).url("https://open.feishu.cn/open-apis/drive/v1/files/:file_token/download");
        builder.auth(HttpUtil.HttpAuth.builder().user(config.getAccessKeyId()).pwd(config.getSecretAccessKey()).openUserId(config.getOpenUserId()).build())
                .authFunction(httpBearValue)
                .pathVar(pathVar);
        HttpUtil.HttpResponse sent = HttpUtil.sent(builder.build());
        sent.getHeaders().forEach((item,value)->{
            metaData.put(item,value);
        });
        storageObjectStream.setMetaData(metaData);
        storageObjectStream.setBufferedInputStream(new BufferedInputStream(new ByteArrayInputStream(sent.getBody())));
        return storageObjectStream;
    }
}
