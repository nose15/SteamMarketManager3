package com.manager.httpCommunication;

import org.json.Cookie;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Client {
    private final HttpClient client;
    private String baseUrl;
    private String cookies;

    public Client(String baseUrl) {
        if (baseUrl.isEmpty()) {
            throw new IllegalArgumentException("Passed url is empty");
        }

        this.client = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
        cookies = "";
    }

    public String GetBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String url) {
        if (url.isEmpty()) {
            throw new IllegalArgumentException("Passed url is empty");
        }

        this.baseUrl = url;
    }

    public Map<String, String> GetCookies() {
        Map<String, String> cookieMap = new HashMap<>();

        if (cookies.isEmpty()) {
            return cookieMap;
        }

        String[] cookieArray = cookies.split(";");
        for (String cookie : cookieArray) {
            String[] cookieElements = cookie.split("=");
            cookieMap.put(cookieElements[0], cookieElements[1]);
        }

        return cookieMap;
    }

    public void AddCookie(String name, String value) {
        if (name.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("Either key or value is empty");
        }

        cookies += name + "=" + value + ";";
    }

    public JSONObject GET(String url) throws RequestException {
        try {
            url = URLEncoder.encode(url, StandardCharsets.UTF_8);
            URI requestUrl = new URI(this.baseUrl + url);

            HttpRequest request = HttpRequest.newBuilder(requestUrl)
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Cookie", cookies)
                    .GET()
                    .build();

            HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();
            CompletableFuture<HttpResponse<String>> responseCompletableFuture = this.client.sendAsync(request, bodyHandler);
            HttpResponse<String> response = responseCompletableFuture.get();

            if (response.statusCode() != 200) {
                throw new RequestException(String.valueOf(response.statusCode()));
            }

            return new JSONObject(response.body());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Passed url is empty");
        }
        catch (RuntimeException | InterruptedException | ExecutionException | URISyntaxException e) {
            throw new RequestException(e);
        }
    }
}
