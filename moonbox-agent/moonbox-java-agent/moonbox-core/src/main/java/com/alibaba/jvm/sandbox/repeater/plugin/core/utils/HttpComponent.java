package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.HttpResponseMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.HttpException;

import java.io.*;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.*;


/**
 * http组件，支持同步和异步请求
 */
@Slf4j
public class HttpComponent {

    public static final Map<String, String> HEAD_JSON = new HashMap<String, String>();

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static final int EOF = -1;

    private int maxTotal = 200;

    private int defaultMaxPerRoute =50;

    private static final String defaultEncoding = "utf-8";

    private CloseableHttpClient defaultHttpClient;

    private CloseableHttpClient longHttpClient;

    private final RequestConfig defaultConfig;

    private final RequestConfig longConConfig;

    private HttpComponent() {

        try {
            HEAD_JSON.put("Content-Type", "application/json");
            HEAD_JSON.put("Accept", "application/json");
            //30s
            int defaultTimeout = 30000;
            //5 min
            int longConnectionTimeout = 300000;
            maxTotal = 200;
            defaultMaxPerRoute = 50;

            defaultConfig = RequestConfig.custom().setSocketTimeout(defaultTimeout).setConnectTimeout(defaultTimeout).build();
            longConConfig = RequestConfig.custom().setSocketTimeout(longConnectionTimeout).setConnectTimeout(longConnectionTimeout).build();

            httpClientInit();
            // add hook
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    shutdown();
                }
            }));
        } catch (Exception e) {
            throw new HttpException(e);
        }
    }

    public static HttpComponent getInstance() {
        return HttpFactoryHolder.INSTANCE;
    }

    private void httpClientInit() throws Exception {

        //***********不进行主机名验证 httpclient SSL******************/
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] certificate, String authType) {
                return true;
            }
        });

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(),
                NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();
        // **************************************************/

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        defaultHttpClient = HttpClients.custom().setDefaultRequestConfig(defaultConfig)
                .setConnectionManager(connectionManager)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultCookieStore(new BasicCookieStore())
                .build();

        longHttpClient = HttpClients.custom().setDefaultRequestConfig(longConConfig)
                .setConnectionManager(connectionManager)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultCookieStore(new BasicCookieStore())
                .build();
    }



    private HttpClient getClient() {
        return defaultHttpClient;
    }

    private HttpClient getLongHttpClient() {
        return longHttpClient;
    }

    private String parseURL(String url) {
        if (url != null) {
            if (url.startsWith("http")) {
                return url;
            } else {
                return "http://" + url;
            }
        } else {
            return null;
        }
    }

    public String encodeParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            Set<String> keys = params.keySet();
            int first = 0;
            for (String key : keys) {
                Object value = params.get(key);
                if (first > 0) {
                    sb.append("&");
                }
                first++;
                sb.append(key);
                sb.append("=");
                String v = String.valueOf(value);
                try {
                    String encodeValue = URLEncoder.encode(v, defaultEncoding);
                    sb.append(encodeValue);
                } catch (UnsupportedEncodingException e) {
                    log.error("UnsupportedEncoding:" + defaultEncoding);
                }
            }
        }
        return sb.toString();
    }

    private void setHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (request != null && headers != null) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                String value = headers.get(key);
                request.setHeader(key, value);
            }
        }
    }

    private String getEncoding(String contentType) {
        if (contentType != null) {
            String[] strs = contentType.split(";");
            if (strs != null && strs.length > 1) {
                String charSet = strs[1].trim();
                String[] charSetKeyValues = charSet.split("=");
                if (charSetKeyValues.length == 2 && charSetKeyValues[0].equalsIgnoreCase("charset")) {
                    return charSetKeyValues[1];
                }
            }
        }
        return defaultEncoding;
    }


    /**
     * 从httpResponse中获取数据转成HttpResponseMeta
     * 可以中Meta中得到具体response
     *
     * @param response
     * @return
     */
    public HttpResponseMeta getResponse(HttpResponse response) {
        if (response != null) {
            StatusLine line = response.getStatusLine();
            if (line != null) {
                HttpResponseMeta responseMeta = new HttpResponseMeta();
                int code = line.getStatusCode();
                responseMeta.setStatusCode(code);
                if (code < 500 && code != 204) {
                    try {
                        InputStream inputStream = response.getEntity().getContent();
                        if (inputStream != null) {
                            byte[] bs = this.toByteArray(inputStream);
                            responseMeta.setResponse(bs);
                            Header[] headers = response.getAllHeaders();
                            if(null != headers){
                                responseMeta.setHeaders(headers);
                            }
                            Header contentType = response.getEntity().getContentType();
                            if(null!=contentType){
                                responseMeta.setContentType(contentType.getValue());
                                responseMeta.setEncode(getEncoding(contentType.getValue()));
                            }
                        }
                    } catch (ClientProtocolException e) {
                        this.handleNetException(e);
                    } catch (IOException e) {
                        this.handleNetException(e);
                    }
                }
                return responseMeta;
            }
        }
        throw new HttpException("http response null");
    }

    private void setBodyParameters(HttpEntityEnclosingRequestBase request, Map<String, Object> params) throws UnsupportedEncodingException {
        if (params != null) {
            List<NameValuePair> list = new LinkedList<NameValuePair>();
            Set<String> keys = params.keySet();
            for (String key : keys) {
                Object v = params.get(key);
                if (v != null) {
                    list.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            HttpEntity entity = new UrlEncodedFormEntity(list);
            request.setEntity(entity);
        }
    }

    public HttpResponseMeta httpPut(String url, Map<String, String> headers, Map<String, Object> params) {
        String newUrl = parseURL(url);
        HttpPut put = new HttpPut(newUrl);
        setHeaders(put, headers);
        HttpClient client = getClient();
        try {
            this.setBodyParameters(put, params);
        } catch (UnsupportedEncodingException e) {
            this.handleNetException(e);
        }
        return getResponse(url, client, put);
    }

    public HttpResponseMeta httpPut(String url, Map<String, Object> urlParams, Map<String, String> headers, String body) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        if (urlParams != null) {
            newUrl = newUrl + "?" + encodeParams(urlParams);
        }
        HttpPut put = new HttpPut(newUrl);
        setHeaders(put, headers);
        HttpClient client = getClient();
        if (body != null) {
            StringEntity entity = new StringEntity(body, "utf-8");
            put.setEntity(entity);
        }
        return getResponse(url, client, put);
    }

    public HttpResponseMeta httpPut(String url, Map<String, String> headers, String body) {
        String newUrl = parseURL(url);
        HttpPut put = new HttpPut(newUrl);
        setHeaders(put, headers);
        HttpClient client = getClient();
        if (body != null) {
            StringEntity entity = new StringEntity(body, "utf-8");
            put.setEntity(entity);
        }
        return getResponse(url, client, put);
    }

    public HttpResponseMeta httpPost(String url, Map<String, String> headers, Map<String, Object> params) {
        String newUrl = parseURL(url);
        HttpPost post = new HttpPost(newUrl);
        setHeaders(post, headers);
        HttpClient client = getClient();
        try {
            this.setBodyParameters(post, params);
        } catch (UnsupportedEncodingException e) {
            this.handleNetException(e);
        }
        return getResponse(url, client, post);
    }

    public HttpResponseMeta httpPost(String url, Map<String, Object> urlParams, Map<String, String> headers, String body) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        if (urlParams != null) {
            newUrl = newUrl + "?" + encodeParams(urlParams);
        }
        HttpPost post = new HttpPost(newUrl);
        setHeaders(post, headers);
        HttpClient client = getClient();
        if (body != null) {
            StringEntity entity = new StringEntity(body, "utf-8");
            post.setEntity(entity);
        }
        return getResponse(url, client, post);
    }

    public HttpResponseMeta httpPost(String url, Map<String, String> headers, String body) {
        String newUrl = parseURL(url);
        HttpPost post = new HttpPost(newUrl);
        setHeaders(post, headers);
        HttpClient client = getClient();
        if (body != null) {
            StringEntity entity = new StringEntity(body, "utf-8");
            post.setEntity(entity);
        }
        return getResponse(url, client, post);
    }

    public HttpResponseMeta httpPostForAlarm(String url, Map<String, String> headers, String body) {

        String contentType = "application/json;charset=utf-8";
        HttpPost httpPost = new HttpPost(url);
        final RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000)
                .setConnectTimeout(2000).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader(HTTP.CONTENT_TYPE, contentType);
        httpPost.setEntity(new StringEntity(body, Consts.UTF_8));
        HttpClient client = getClient();
        if (body != null) {
            StringEntity entity = new StringEntity(body, "utf-8");
            httpPost.setEntity(entity);
        }
        return getResponse(url, client, httpPost);
    }

    public HttpResponseMeta httpDelete(String url, Map<String, String> headers, Map<String, Object> params) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        if (params != null) {
            newUrl = newUrl + "?" + encodeParams(params);
        }
        HttpDelete delete = new HttpDelete(newUrl);
        setHeaders(delete, headers);
        HttpClient client = getClient();
        return getResponse(url, client, delete);
    }

    private void handleNetException(Exception e) {
        log.error("http exception:" + e.getMessage(), e);
        throw new HttpException(e);
    }


    public HttpResponseMeta httpGet(String url, Map<String, String> headers, Map<String, Object> params) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        if (params != null) {
            newUrl = newUrl + "?" + encodeParams(params);
        }
        HttpGet get = new HttpGet(newUrl);
        setHeaders(get, headers);
        HttpClient client = getClient();
        return getResponse(url, client, get);
    }

    public HttpResponseMeta httpLongGet(String url, Map<String, String> headers, Map<String, Object> params) {
        String newUrl = parseURL(url);
        if (newUrl == null) {
            return null;
        }
        if (params != null) {
            newUrl = newUrl + "?" + encodeParams(params);
        }
        HttpGet get = new HttpGet(newUrl);
        setHeaders(get, headers);
        HttpClient client = getLongHttpClient();
        return getResponse(url, client, get);
    }

    private <T extends HttpRequestBase> HttpResponseMeta getResponse(String url, HttpClient client, T request) {
        try {
            HttpResponse response = client.execute(request);
            return getResponse(response);
        } catch (ClientProtocolException e) {
            this.handleNetException(e);
        } catch (IOException e) {
            this.handleNetException(e);
        } finally {
            request.releaseConnection();
        }
        throw new HttpException("http " + url + " response null");
    }

    public byte[] toByteArray(InputStream ins) throws IOException {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            copy(ins, output);
            return output.toByteArray();
        } finally {
            ins.close();
        }
    }

    public int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public void shutdown() {
        try {
            defaultHttpClient.close();
            longHttpClient.close();
        } catch (IOException e) {
            log.error("shutdown hook error", e);
        }
    }

    private static class HttpFactoryHolder {
        private static final HttpComponent INSTANCE = new HttpComponent();
    }
}

