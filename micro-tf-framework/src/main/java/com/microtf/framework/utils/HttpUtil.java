package com.microtf.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microtf.framework.exceptions.BizException;
import kotlin.Pair;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Http客户端，
 * 基于okhttp进行二次封装，更适合调用api
 *
 * @author glzaboy@163.com
 */
@Slf4j
public class HttpUtil {
    private static final String URL_START = "?";
    private static final String URL_SPLIT = "&";
    private static final String SCHEME_FILE = "file://";
    private static final OkHttpClient.Builder OKHTTP_BUILD = new OkHttpClient.Builder();
    @SuppressWarnings("unused")
    public static Function<HttpAuth, HttpAuthReturn> httpBasic = (HttpAuth httpAuth) -> {
        HttpAuthReturn httpAuthReturn = new HttpAuthReturn();
        httpAuthReturn.setAuthValue(Credentials.basic(httpAuth.getUser(), httpAuth.getPwd()));
        return httpAuthReturn;
    };
    @SuppressWarnings("unused")
    public static Function<HttpAuth, HttpAuthReturn> httpBearValue = (HttpAuth httpAuth) -> {
        HttpAuthReturn httpAuthReturn = new HttpAuthReturn();
        httpAuthReturn.setAuthValue("Bearer " + httpAuth.getUser());
        return httpAuthReturn;
    };

    public static OkHttpClient getClient() {
        return getClient(false, 10);
    }

    public static OkHttpClient getClient(boolean retry, int timeOut) {
        OKHTTP_BUILD.writeTimeout(timeOut, TimeUnit.SECONDS);
        OKHTTP_BUILD.readTimeout(timeOut, TimeUnit.SECONDS);
        OKHTTP_BUILD.connectTimeout(3, TimeUnit.SECONDS);
        OKHTTP_BUILD.retryOnConnectionFailure(retry);
        return OKHTTP_BUILD.build();
    }

    public static Map<String, String> object2Map(Object param) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestJsonStr = objectMapper.writeValueAsString(param);
            return objectMapper.readValue(requestJsonStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    public static Map<String, Object> object2MapObject(Object param) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestJsonStr = objectMapper.writeValueAsString(param);
            return objectMapper.readValue(requestJsonStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    public static String object2Json(Object param) throws BizException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            log.error("对象转json出错", e);
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
                    sb.append(URLEncoder.encode(next.getValue(), StandardCharsets.UTF_8));
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

    private static String applyPathVar(String url, Map<String, String> pathVar) {
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

    private static Request.Builder buildHttp(HttpRequest httpRequest) {
        Request.Builder builder = new Request.Builder();
        if (httpRequest.getAuthFunction() != null && httpRequest.getAuth() != null) {
            HttpAuthReturn apply = httpRequest.getAuthFunction().apply(httpRequest.getAuth());
            if (!Objects.isNull(apply)) {
                if (apply.requestParamName != null) {
                    Map<String, String> query = Optional.ofNullable(httpRequest.getQuery()).orElseGet(() -> new HashMap<>(16));
                    query.put(apply.getRequestParamName(), apply.getAuthValue());
                    httpRequest.setQuery(query);
                } else {
                    builder.addHeader("Authorization", apply.getAuthValue());
                }
            }
        }
        builder.url(applyQuery(applyPathVar(httpRequest.getUrl(), httpRequest.getPathVar()), httpRequest.getQuery()));
        if (Objects.isNull(httpRequest.getMethod())) {
            httpRequest.setMethod(Method.GET);
        }
        switch (httpRequest.getMethod()) {
            case FORM:
                FormBody.Builder builder1 = new FormBody.Builder();
                httpRequest.getForm().forEach(builder1::add);
                return builder.addHeader("Content-Type", "application/x-www-form-urlencoded").post(builder1.build());
            case JSON:
                RequestBody requestBody = RequestBody.create(object2Json(httpRequest.getPostObject()), MediaType.parse("application/json; charset=utf-8"));
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
                        String contentType = postItem.getValue().getContentType();
                        if (value.getContent() != null) {
                            bytes = value.getContent();
                            if (contentType == null) {
                                contentType = URLConnection.guessContentTypeFromName(value.getUri().toString());
                            }
                            RequestBody requestBody1 = RequestBody.create(bytes, MediaType.parse(contentType));
                            multiPartBodyBuilder.addFormDataPart(postItem.getKey(), value.getFileName() != null ? value.getFileName() : value.getUri().getPath(), requestBody1);
                        } else {
                            if (SCHEME_FILE.equalsIgnoreCase(value.getUri().getScheme())) {
                                if (contentType == null) {
                                    contentType = URLConnection.guessContentTypeFromName(value.getUri().toString());
                                }
                                if (contentType == null) {
                                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(value.getUri().getPath())))) {
                                        contentType = URLConnection.guessContentTypeFromStream(byteArrayInputStream);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (contentType == null) {
                                    contentType = "application/octet-stream";
                                }
                                RequestBody requestBody1 = RequestBody.create(new java.io.File(value.getUri()), MediaType.parse(contentType));
                                multiPartBodyBuilder.addFormDataPart(postItem.getKey(), value.getFileName() != null ? value.getFileName() : value.getUri().getPath(), requestBody1);
                            } else {
                                Request.Builder postRequestBuilder = new Request.Builder();
                                Response execute;
                                try {
                                    execute = getClient().newCall(postRequestBuilder.url(value.getUri().toString()).build()).execute();
                                    contentType = Objects.requireNonNull(Objects.requireNonNull(execute.body()).contentType()).toString();
                                    RequestBody requestBody1 = RequestBody.create(Objects.requireNonNull(execute.body()).bytes(), MediaType.parse(contentType));
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
                return builder.put(RequestBody.create(object2Json(httpRequest.getPostObject()), MediaType.parse("application/json; charset=utf-8")));
            case DELETE:
                return builder.delete(RequestBody.create(object2Json(httpRequest.getPostObject()), MediaType.parse("application/json; charset=utf-8")));
            case PATCH:
                return builder.patch(RequestBody.create(object2Json(httpRequest.getPostObject()), MediaType.parse("application/json; charset=utf-8")));
            default:
                return builder.get();
        }
    }

    public static HttpResponse sent(HttpRequest httpRequest) {
        return sent(httpRequest, getClient());
    }

    public static HttpResponse sent(HttpRequest httpRequest, OkHttpClient okHttpClient) {
        Request.Builder builder1 = buildHttp(httpRequest);
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();
        try {
            Response execute = okHttpClient.newCall(builder1.build()).execute();
            builder.status(execute.code());
            Iterator<Pair<String, String>> iterator = execute.headers().iterator();
            Map<String, String> headers = new HashMap<>(16);
            while (iterator.hasNext()) {
                Pair<String, String> next = iterator.next();
                next.getFirst();
                headers.put(next.getFirst(), next.getSecond());
            }
            builder.headers(headers);
            if (execute.isSuccessful()) {
                builder.body(Objects.requireNonNull(execute.body()).bytes());
                return builder.build();
            } else {
                log.error("请求出错，服务器返回{}", Objects.requireNonNull(execute.body()).string());
                throw new BizException("Http请求出错" + execute.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("Http请求出错" + e.getMessage());
        }
    }

    /**
     * HTTP 请求方法
     */
    public enum Method {
        /**
         * DELETE 请求
         */
        DELETE,
        /**
         * POST 文件
         */
        FILE,
        /**
         * POST 表单
         */
        FORM,
        /**
         * GET 请求
         */
        GET,
        /**
         * HEAD 请求
         */
        HEAD,
        /**
         * POST JSON 请求
         */
        JSON,
        /**
         * PATCH 请求
         */
        PATCH,
        /**
         * PUT 请求
         */
        PUT
    }

    @Data
    @Builder
    public static class HttpRequest {
        private Method method;
        private String url;
        private Map<String, String> query;
        private Map<String, String> pathVar;
        private Map<String, String> form;
        private Object postObject;
        private Map<String, File> postFile;
        private HttpAuth auth;
        private Function<HttpAuth, HttpAuthReturn> authFunction;
    }

    @Data
    @Builder
    public static class HttpResponse {
        private byte[] body;
        private Integer status;
        private Map<String, String> headers;

        @SuppressWarnings("unused")
        public String html() {
            return new String(body);
        }

        public <T> T json(Class<T> classic) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                T t = objectMapper.readValue(body, classic);
                log.info("json数据{},对象{}", new String(body), t);
                return t;
            } catch (IOException e) {
                log.error("转换Json出错{}", e.getMessage());
                throw new BizException("转换Json出错");
            }
        }
    }

    @Data
    @Builder
    public static class HttpAuth {
        private String user;
        private String pwd;
        private String openUserId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HttpAuthReturn implements Serializable {
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
        private String contentType;
    }
}
