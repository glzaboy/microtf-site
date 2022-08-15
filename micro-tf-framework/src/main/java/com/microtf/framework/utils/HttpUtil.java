package com.microtf.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microtf.framework.exceptions.BizException;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class HttpUtil {
    private static final String URL_START = "?";
    private static final String URL_SPLIT = "&";
    private static final String SCHEME_FILE = "file://";
    private static  OkHttpClient.Builder okhttpBuild=new OkHttpClient.Builder();
    public enum Method {
        JSON, FORM, FILE, DELETE, GET, PUT, HEAD,PATCH
    }

    public static OkHttpClient getClient(){
        return getClient(false,3);
    }
    public static OkHttpClient getClient(boolean retry,int timeOut){
        okhttpBuild.writeTimeout(timeOut, TimeUnit.SECONDS);
        okhttpBuild.readTimeout(timeOut, TimeUnit.SECONDS);
        okhttpBuild.connectTimeout(3, TimeUnit.SECONDS);
        okhttpBuild.retryOnConnectionFailure(retry);
        return okhttpBuild.build();
    }
    @Data
    @Builder
    public static class HttpRequest {
        private Method method= Method.GET;
        private String url;
        private Map<String,String> query;
        private Map<String,String> pathVar;
        private Map<String,String> form;
        private Object postObject;
        private Map<String, File> postFile;
        private HttpAuth auth;
        private BiFunction<String,String,HttpAuthReturn> authFunction;
    }
    @Data
    @Builder
    public static class HttpResponse {
        private byte[] body;
        private Integer status;
        public String html(){
            return new String(body);
        }
        public <T> T json(Class<T> classic){
            ObjectMapper objectMapper=new ObjectMapper();
            try {
                return objectMapper.readValue(body,classic);
            } catch (IOException e) {
                log.error("转换Json出错{}",e.getMessage());
                throw new BizException("转换Json出错");
            }
        }
    }
    @Data
    @Builder
    public static class HttpAuth{
        private String user;
        private String pwd;
    }
    @Builder
    @Getter
    @Setter
    public static class HttpAuthReturn{
        private String requestParamName;
        private String authValue;
    }
    @Data
    @Builder
    public static class File {
        /**
         * 文件
         */
        private URI uri;
        /**
         * 文件名
         */
        private String fileName;
        /**
         * 文件内容
         * 如果提供内容则不再从URI中获取
         */
        private byte[] content;
        /**
         * 文件mime
         * 如果空则默认为 application/octet-stream
         */
        private String contentType="application/octet-stream";
    }
    public static Map<String,String> object2Map(Object param){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestJsonStr = objectMapper.writeValueAsString(param);
            return objectMapper.readValue(requestJsonStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }
    public static String object2Json(Object param) throws BizException{
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            log.error("对象转json出错",e);
            throw new BizException("对象转json出错");
        }
    }
    /**
     * 设置url
     * 如果不调用 setJson,setBin,setPostMap,setPostFile 使用get发送。
     *
     * @param url     url 地址
     * @param queries get的map参数
     * @return 当前对象自身，支持链式调用
     */
    private static String applyQuery(String url, Map<String, String> queries) {
        StringBuilder sb = new StringBuilder();
        if (queries != null && queries.size() > 0) {
            for (Map.Entry<String, String> next : queries.entrySet()) {
                String key = next.getKey();
                Pattern pattern = Pattern.compile("\\{" + key + "}");
                if (pattern.matcher(url).find()) {
                    url = url.replaceAll(pattern.toString(), URLEncoder.encode(next.getValue(), StandardCharsets.UTF_8));
                } else {
                    sb.append(next.getKey());
                    sb.append("=");
                    sb.append(URLEncoder.encode(next.getValue(),StandardCharsets.UTF_8));
                    sb.append("&");
                }

            }
        }
        if (sb.length() > 0) {
            if (url.contains(URL_START)) {
                url = url + URL_SPLIT + sb.substring(0, sb.length() - 1);
            } else {
                url = url + URL_START + sb.substring(0, sb.length() - 1);
            }
        }
        return url;
    }
    private static String applyPathVar(String url, Map<String, String> pathVar){
         String var = url;
        if (Objects.isNull(pathVar) || pathVar.isEmpty()) {
            return url;
        }
        Iterator<Map.Entry<String, String>> iterator = pathVar.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<String, String> next = iterator.next();
            if (Objects.isNull(next.getKey()) || Objects.isNull(next.getValue())) {
                continue;
            }
            String replaceStr = ":".concat(next.getKey());
            if (var.contains(replaceStr)) {
                var = StringUtils.replace(var, replaceStr, next.getValue());
                iterator.remove();
            }
        }
        return var;
    }
    @SuppressWarnings("unused")
    public static BiFunction<String,String,HttpAuthReturn> httpBasic=(String user,String pwd)-> HttpAuthReturn.builder().authValue(Credentials.basic(user, pwd)).build();
    @SuppressWarnings("unused")
    public static BiFunction<String,String,HttpAuthReturn> httpBearValue=(String bearValue,String pwd)-> HttpAuthReturn.builder().authValue("Bearer "+ bearValue).build();

    private static Request.Builder buildHttp(HttpRequest httpRequest){
        Request.Builder builder=new Request.Builder();
        builder.url(applyQuery(applyPathVar(httpRequest.getUrl(), httpRequest.getPathVar()), httpRequest.getQuery()));
        if(httpRequest.getAuthFunction()!=null && httpRequest.getAuth()!=null){
            HttpAuthReturn apply = httpRequest.getAuthFunction().apply(httpRequest.getAuth().getUser(), httpRequest.getAuth().getPwd());
            if(apply.requestParamName!=null){
                Map<String, String> query = httpRequest.getQuery();
                query.put(apply.getRequestParamName(),apply.getAuthValue());
            }else{
                builder.addHeader("Authorization",apply.getAuthValue());
            }
        }
        switch (httpRequest.getMethod()){
            case FORM:
                FormBody.Builder builder1 = new FormBody.Builder();
                httpRequest.getForm().forEach(builder1::add);
                return builder.addHeader("Content-Type", "application/x-www-form-urlencoded").post(builder1.build());
            case JSON:
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpRequest.getPostObject()));
                return builder.addHeader("Content-Type", "application/json; charset=utf-8").post(requestBody);
            case FILE:
                MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder();
                multiPartBodyBuilder.setType(MultipartBody.FORM);
                httpRequest.getForm().forEach(multiPartBodyBuilder::addFormDataPart);
                Map<String, File> postFile = httpRequest.getPostFile();
                if (postFile != null && postFile.size() > 0) {
                    for (Map.Entry<String, File> postItem : postFile.entrySet()) {
                        File value = postItem.getValue();
                        byte[] bytes;
                        String contentType=postItem.getValue().getContentType();
                        if (value.getContent() != null) {
                            bytes = value.getContent();
                            if(contentType==null){
                                contentType = URLConnection.guessContentTypeFromName(value.getUri().toString());
                            }
                            RequestBody requestBody1 = RequestBody.create(MediaType.parse(contentType), bytes);
                            multiPartBodyBuilder.addFormDataPart(postItem.getKey(), value.getFileName() != null ? value.getFileName() : value.getUri().getPath(), requestBody1);
                        } else {
                            if (SCHEME_FILE.equalsIgnoreCase(value.getUri().getScheme())) {
                                if(contentType==null){
                                    contentType = URLConnection.guessContentTypeFromName(value.getUri().toString());
                                }
                                if(contentType==null) {
                                    try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(Files.readAllBytes(Paths.get(value.getUri().getPath())))) {
                                        contentType=URLConnection.guessContentTypeFromStream(byteArrayInputStream);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @SuppressWarnings(value = "dep-ann")
                                RequestBody requestBody1 = RequestBody.create(MediaType.parse(contentType), new java.io.File(value.getUri()));
                                multiPartBodyBuilder.addFormDataPart(postItem.getKey(), value.getFileName() != null ? value.getFileName() : value.getUri().getPath(), requestBody1);
                            } else {
                                Request.Builder postRequestBuilder = new Request.Builder();
                                Response execute;
                                try {
                                    execute = getClient().newCall(postRequestBuilder.url(value.getUri().toString()).build()).execute();
                                    contentType= Objects.requireNonNull(Objects.requireNonNull(execute.body()).contentType()).toString();
                                    RequestBody requestBody1 = RequestBody.create(MediaType.parse(contentType), Objects.requireNonNull(execute.body()).bytes());
                                    multiPartBodyBuilder.addFormDataPart(postItem.getKey(), value.getFileName() != null ? value.getFileName() : value.getUri().getPath(), requestBody1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                return builder.post(multiPartBodyBuilder.build());
            case HEAD:
                return builder.head();
            case PUT:
                return builder.put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpRequest.getPostObject())));
            case DELETE:
                return builder.delete(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpRequest.getPostObject())));
            case PATCH:
                return builder.patch(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpRequest.getPostObject())));
            default:
                return builder.get();
        }
    }
    public static HttpResponse sent(HttpRequest httpRequest){
        return sent(httpRequest,getClient());
    }
    public static HttpResponse sent(HttpRequest httpRequest,OkHttpClient okHttpClient){
        Request.Builder builder1 = buildHttp(httpRequest);
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();
        try {
            Response execute = okHttpClient.newCall(builder1.build()).execute();
            builder.status(execute.code());
            if(execute.isSuccessful()){
                builder.body(execute.body().bytes());
                return builder.build();
            }else{
                throw new BizException("Http请求出错"+execute.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("Http请求出错"+e.getMessage());
        }
    }
}
