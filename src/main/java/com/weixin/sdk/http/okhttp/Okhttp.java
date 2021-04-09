package com.weixin.sdk.http.okhttp;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Wenbo
 * @date 2021/3/16 11:00
 * @Email 969100115@qq.com
 * @phone 17621847037
 */
public class Okhttp {

    //    private static volatile OkHttpClient okHttpClient;
    private static OkHttpClient okHttpClient;
    //    private static Semaphore semaphore;
    private Map<String, String> headerMap;
    private Map<String, String> urlParam;
    private String bodyParam;
    private Map<String, String> formParam;
    private String url;
    private Request.Builder request;


    private Okhttp(boolean isHttps) {
        if (okHttpClient == null) {
            TrustManager[] trustManagers = buildTrustManagers();
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS);
            if (isHttps) {
                builder = builder.sslSocketFactory(createSSLSocketFactory(trustManagers), (X509TrustManager) trustManagers[0]);
            }
            okHttpClient = builder.hostnameVerifier((hostName, session) -> true)
                    .retryOnConnectionFailure(true).build();
            addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        }
    }

    /**
     * @param overSecureSocketLayer 选择协议是http还是https
     * @return
     */
    public static Okhttp builder(boolean overSecureSocketLayer) {
        return new Okhttp(overSecureSocketLayer);
    }

    /**
     * 添加url参数
     *
     * @param url
     * @return
     */
    public Okhttp url(String url) {
        this.url = url;
        return this;
    }

    public Okhttp urlParam(String key, String value) {
        if (urlParam == null) {
            urlParam = new LinkedHashMap<>();
        }
        urlParam.put(key, value);
        return this;
    }

    public Okhttp urlParam(Map<String, String> urlParam) {
        if (urlParam == null) {
            urlParam = new LinkedHashMap<>();
        }
        urlParam.putAll(urlParam);
        return this;
    }

    /**
     * 添加表单参数
     *
     * @param key
     * @param value
     * @return
     */
    public Okhttp form(String key, String value) {
        if (formParam == null) {
            formParam = new LinkedHashMap<>();
        }
        formParam.put(key, value);
        return this;
    }

    public Okhttp body(String bodyParam) {
        this.bodyParam = bodyParam;
        return this;
    }

    public Okhttp body(Map bodyParam) {
        this.bodyParam = JSONObject.toJSONString(bodyParam);
        return this;
    }

    public Okhttp body(String key, Object value) {
        if (StringUtils.isBlank(bodyParam)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, value);
            this.bodyParam = jsonObject.toJSONString();
        } else {
            JSONObject jsonBody = JSONObject.parseObject(bodyParam);
            jsonBody.put(key, value);
            this.bodyParam = jsonBody.toJSONString();
        }
        return this;
    }


    /**
     * 添加请求头
     *
     * @param key
     * @param value
     * @return
     */
    public Okhttp addHeader(String key, String value) {
        if (headerMap == null) {
            headerMap = new LinkedHashMap<>(16);
        }
        headerMap.put(key, value);
        return this;
    }

    /**
     * 初始化get方法
     *
     * @return
     */
    public Okhttp get() {
        request = new Request.Builder().get();
        StringBuilder urlBuilder = new StringBuilder(url);
        if (urlParam != null) {
            urlBuilder.append("?");
            try {
                for (Map.Entry<String, String> entry : urlParam.entrySet()) {
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }
            } catch (UnsupportedEncodingException e) {
                //抛出统一异常
                e.printStackTrace();
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        request.url(urlBuilder.toString());
        return this;
    }

    @Deprecated
    public Okhttp post(boolean jsonPost){
        RequestBody requestBody;
        if(jsonPost){
            requestBody = RequestBody.create(Optional.ofNullable(bodyParam).orElse(""), MediaType.parse("application/json; charset=utf-8"));
        }else {
            FormBody.Builder formBody = new FormBody.Builder();
            if(formParam != null){
                formParam.forEach(formBody::add);
            }
            requestBody = formBody.build();
        }
        request = new Request.Builder().post(requestBody);

        StringBuilder urlBuilder = new StringBuilder(url);
        if(urlParam != null){
            urlBuilder.append("?");
            try {
                for (Map.Entry<String, String> entry : urlParam.entrySet()){
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }
            } catch (UnsupportedEncodingException e) {
                //抛出统一异常
                e.printStackTrace();
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        request.url(urlBuilder.toString());
        return this;
    }

    public Okhttp filePost(Map<String,File> fileParam) {
        request = new Request.Builder()
                .url(UrlParamsBuild())
                .post(fileResponseBodyBuild(fileParam));
        return this;
    }

    public Okhttp filePost(String key, File file) {
        Map<String,File> fileParam = new HashMap<>();
        fileParam.put(key,file);
        request = new Request.Builder()
                .url(UrlParamsBuild())
                .post(fileResponseBodyBuild(fileParam));
        return this;
    }

    public Okhttp formPost() {
        request = new Request.Builder()
                .url(UrlParamsBuild())
                .post(formBuild());
        return this;
    }

    public Okhttp jsonPost() {
        request = new Request.Builder()
                .url(UrlParamsBuild())
                .post(jsonResponseBodyBuild());
        return this;
    }

    /**
     * 请求
     *
     * @return
     */
    public String excut() {
        setHeader(request);
        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            assert response.body() != null;
            return new String(response.body().bytes());
        } catch (IOException e) {
            //统一异常抛出
            e.printStackTrace();
        }
        return null;
    }

    private void setHeader(Request.Builder request) {
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 生成安全套接字工厂,用于https请求的证书跳过
     *
     * @param trustAllCerts
     * @return
     */
    private static SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {
        SSLSocketFactory factory = null;
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAllCerts, new SecureRandom());
            factory = context.getSocketFactory();
        } catch (Exception e) {
            //统一异常异常抛出
            e.printStackTrace();
        }
        return factory;
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
    }

    private MultipartBody fileResponseBodyBuild(Map<String,File> fileParam) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        fileParam.forEach((key, value) -> {
            multipartBuilder.addFormDataPart(
                    key,value.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"), value));

        });

        return multipartBuilder.build();

    }

    private RequestBody formBuild() {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        formParam.forEach((key, value) -> {
            multipartBuilder.addFormDataPart(key,value);
        });
        return multipartBuilder.build();

    }


    private RequestBody jsonResponseBodyBuild() {
        RequestBody requestBody = RequestBody.create(Optional.ofNullable(bodyParam).orElse(""), MediaType.parse("application/json; charset=utf-8"));
        return requestBody;

    }

    private String UrlParamsBuild() {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (urlParam != null) {
            urlBuilder.append("?");
            try {
                for (Map.Entry<String, String> entry : urlParam.entrySet()) {
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }
            } catch (UnsupportedEncodingException e) {
                //抛出统一异常
                e.printStackTrace();
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        return urlBuilder.toString();
    }

}
