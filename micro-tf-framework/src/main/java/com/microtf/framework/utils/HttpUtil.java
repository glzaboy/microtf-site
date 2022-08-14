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
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Slf4j
public class HttpUtil {
    public static final String URL_START = "?";
    private static final String URL_SPLIT = "&";
    private static final String SCHEME_FILE = "file://";
    public static  OkHttpClient.Builder okhttpBuild=new OkHttpClient.Builder();
    public enum HttpMethod {
        POSTJSON,POSTFORM,POSTFILE, DELETE, GET, PUT, HEAD,PATCH
    }

    public static OkHttpClient getClient(){
        return okhttpBuild.build();
    }
    @Data
    public static class HttpObject{
        private HttpMethod method=HttpMethod.GET;
        private String url;
        private Map<String,String> query;
        private Map<String,String> pathVar;
        private Map<String,String> form;
        private Object postObject;
        private Map<String, PostFile> postFile;
        private HttpAuth auth;
    }
    @Data
    @Builder
    public static class HttpAuth{
        private String bearAuth;
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
    public static class PostFile{
        /**
         * 文件
         */
        URI uri;
        /**
         * 文件名
         */
        String fileName;
        /**
         * 文件内容
         * 如果提供内容则不再从URI中获取
         */
        byte[] content;
        /**
         * 文件mime
         * 如果空则默认为 application/octet-stream
         */
        String contentType="application/octet-stream";
    }
    public static Map<String,String> object2Map(Object param){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestJsonStr = objectMapper.writeValueAsString(param);
            return objectMapper.readValue(requestJsonStr, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
    public static String object2Json(Object param){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
    public static String applyQuery(String url, Map<String, String> queries) {
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
    public static String applyPathVar(String url, Map<String, String> pathVar){
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
    public static BiFunction<String,String,HttpAuthReturn> httpBasic=(String user,String pwd)-> HttpAuthReturn.builder().authValue(Credentials.basic(user, pwd)).build();
    BiFunction<String,String,HttpAuthReturn> httpBearValue=(String bearValue,String pwd)-> HttpAuthReturn.builder().authValue("Bearer "+ bearValue).build();
    public static Request.Builder buildHttp(HttpObject httpObject, BiFunction<String,String,HttpAuthReturn> authFunction){
        Request.Builder builder=new Request.Builder();
        builder.url(applyQuery(applyPathVar(httpObject.getUrl(), httpObject.getPathVar()), httpObject.getQuery()));
        if(authFunction!=null && httpObject.getAuth()!=null){
            HttpAuthReturn apply = authFunction.apply(httpObject.getAuth().getUser(), httpObject.getAuth().getPwd());
            if(apply.requestParamName!=null){
                Map<String, String> query = httpObject.getQuery();
                query.put(apply.getRequestParamName(),apply.getAuthValue());
            }else{
                builder.addHeader("Authorization",apply.getAuthValue());
            }
        }
        switch (httpObject.getMethod()){
            case POSTFORM:
                FormBody.Builder builder1 = new FormBody.Builder();
                httpObject.getForm().forEach(builder1::add);
                return builder.addHeader("Content-Type", "application/x-www-form-urlencoded").post(builder1.build());
            case POSTJSON:
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpObject.getPostObject()));
                return builder.addHeader("Content-Type", "application/json; charset=utf-8").post(requestBody);
            case POSTFILE:
                MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder();
                multiPartBodyBuilder.setType(MultipartBody.FORM);
                httpObject.getForm().forEach(multiPartBodyBuilder::addFormDataPart);
                Map<String, PostFile> postFile = httpObject.getPostFile();
                if (postFile != null && postFile.size() > 0) {
                    for (Map.Entry<String, PostFile> postItem : postFile.entrySet()) {
                        PostFile value = postItem.getValue();
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
                                RequestBody requestBody1 = RequestBody.create(MediaType.parse(contentType), new File(value.getUri()));
                                multiPartBodyBuilder.addFormDataPart(postItem.getKey(), value.getFileName() != null ? value.getFileName() : value.getUri().getPath(), requestBody1);
                            } else {
                                Request.Builder postRequestBuilder = new Request.Builder();
                                Response execute = null;
                                try {
                                    execute = getClient().newCall(postRequestBuilder.url(value.getUri().toString()).build()).execute();
                                    contentType=execute.body().contentType().toString();
                                    RequestBody requestBody1 = RequestBody.create(MediaType.parse(contentType), execute.body().bytes());
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
                return builder.put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpObject.getPostObject())));
            case DELETE:
                return builder.delete(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpObject.getPostObject())));
            case PATCH:
                return builder.patch(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object2Json(httpObject.getPostObject())));
            default:
                return builder.get();
        }
    }
    public static String sent(Request.Builder requestBuild,OkHttpClient okHttpClient){
        try {
            Response execute = okHttpClient.newCall(requestBuild.build()).execute();
            if(execute.isSuccessful()){
                return execute.body().string();
            }else{
                throw new BizException("Http请求出错"+execute.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("Http请求出错"+e.getMessage());
        }

    }

    public static void main(String[] args) throws IOException {
        OkHttpClient client = getClient();
        HttpObject httpObject=new HttpObject();
        Map<String,String> q=new HashMap<>();
        q.put("a","abcder");
        httpObject.setQuery(q);
        httpObject.setUrl("http://test.microtf.com/test.php");
        httpObject.setForm(q);
        httpObject.setMethod(HttpMethod.POSTFORM);
        httpObject.setAuth(HttpAuth.builder().user("123456").pwd("123").build());
        String sent = sent(buildHttp(httpObject, httpBasic), client);
        log.info(sent);
    }
}
