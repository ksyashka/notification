package com.ksyashka.notification.utils;


import com.ksyashka.notification.domain.HttpAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

import static com.ksyashka.notification.constants.CommonConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HttpSender {

    private final String keepAlive = "keep-alive";
    private final String authorization = AUTHORIZATION;

    private String url;
    private String login;
    private String pass;
    private String body = "";
    private Map<String, String> headers;
    private Integer ttl = DEFAULT_TTL_HTTP_SENDER;
    private String contentType = DEFAULT_CONTENT_TYPE;


    public HttpAnswer post(String url, String body, Map<String, String> headers, Integer ttl) {
        return post(url, body, this.login, this.pass, headers, ttl, this.contentType);
    }

    public HttpAnswer post(String url, String body, Integer ttl) {
        return post(url, body, this.login, this.pass, this.headers, ttl, this.contentType);
    }

    public HttpAnswer post(String url, int ttl) {
        return post(url, this.body, this.login, this.pass, this.headers, ttl, this.contentType);
    }

    public HttpAnswer post(String url, String body) {
        return post(url, body, this.login, this.pass, this.headers, ttl, this.contentType);
    }

    public HttpAnswer post(String url, String body, Map<String, String> headers) {
        return post(url, body, this.login, this.pass, headers, this.ttl, this.contentType);
    }

    public HttpAnswer post(String url, String body, String login, String pass, Map<String, String> headers, Integer ttl, String contentType) {
        long start = System.currentTimeMillis();
        HttpAnswer answer = post(url, body, headers, ttl, contentType, login, pass);
        answer.setDuration(System.currentTimeMillis() - start);
        return answer;
    }

    public HttpAnswer get(String url, Integer ttl) {
        return get(url, this.headers, ttl, this.login, this.pass, this.contentType);
    }

    public HttpAnswer get(String url, Map<String, String> headers, String login, String pass, Integer ttl, String contentType) {
        long start = System.currentTimeMillis();
        HttpAnswer answer = get(url, headers, ttl, contentType, login, pass);
        answer.setDuration(System.currentTimeMillis() - start);
        return answer;
    }

    private HttpAnswer get(String url, Map<String, String> headers, Integer ttl, String contentType, String login, String pass) {
        HttpURLConnection connection = null;

        try {
            URI uri = new URI(url);
            connection = createConnection(uri.toURL(), contentType);
            setTimeOut(connection, ttl);
            setAuth(connection, login, pass);
            setHeaders(connection, headers);
            return getResponse(connection);
        } catch (Exception e) {
            return getErrorsResponse(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpAnswer post(String url, String body, Map<String, String> headers, Integer ttl, String contentType, String login, String pass) {
        HttpURLConnection connection = null;
        try {
            URI uri = new URI(url);
            connection = createConnection(uri.toURL(), contentType);
            setTimeOut(connection, ttl);
            setAuth(connection, login, pass);
            setHeaders(connection, headers);
            if (uri.getScheme().equalsIgnoreCase("http")) {
                sendHttp(connection.getOutputStream(), body);
            } else if (uri.getScheme().equalsIgnoreCase("https")) {
                sendHttps((HttpsURLConnection) connection, body);
            }
            return getResponse(connection);
        } catch (Exception e) {
            return getErrorsResponse(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendHttp(OutputStream outputStream, String json) throws IOException {
        try (DataOutputStream postOut = new DataOutputStream(outputStream)) {
            postOut.write(json.getBytes(StandardCharsets.UTF_8));
            postOut.flush();
        }
    }

    private void sendHttps(HttpsURLConnection connection, String json) throws NoSuchAlgorithmException, KeyManagementException, IOException {

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        connection.setSSLSocketFactory(sc.getSocketFactory());
        //don't verify hostname, force set validation result to true
        connection.setHostnameVerifier((s, sslSession) -> true);
        sendHttp(connection.getOutputStream(), json);
    }

    private void setTimeOut(HttpURLConnection connection, Integer ttl) {
        if (ttl != null) {
            connection.setConnectTimeout((ttl / 2) * MILLIS_IN_SECOND);
            connection.setReadTimeout(ttl * MILLIS_IN_SECOND);
        }
    }

    private HttpURLConnection createConnection(URL url, String contentType) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Connection", keepAlive);
        return connection;
    }

    private void setAuth(HttpURLConnection connection, String login, String pass) {
        if (login != null && pass != null) {
            String auth = "Basic " + new String(Base64.encodeBase64((login + ":" + pass).getBytes()));
            connection.setRequestProperty(this.authorization, auth);
        }
    }

    private void setHeaders(HttpURLConnection connection, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> mapEntry : headers.entrySet()) {
                connection.setRequestProperty(mapEntry.getKey(), mapEntry.getValue());
            }
        }
    }

    private HttpAnswer getResponse(HttpURLConnection connection) {
        HttpAnswer response = new HttpAnswer();
        try {
            int code = connection.getResponseCode();
            response.setCode(code);
            response.setText(getTextResponse(code < 400 ? connection.getInputStream() : connection.getErrorStream()));
        } catch (Exception e) {
            return getErrorsResponse(e);
        }
        return response;
    }

    private String getTextResponse(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private HttpAnswer getErrorsResponse(Exception e) {
        HttpAnswer response = new HttpAnswer();
        if (e instanceof SocketTimeoutException) {
            response.setCode(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
            response.setText(e.getMessage());
        } else {
            response.setCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            response.setText(e.getMessage());
        }
        return response;
    }
}


