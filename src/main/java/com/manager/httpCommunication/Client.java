package com.manager.httpCommunication;

import org.json.Cookie;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client {
    private final HttpClient client;
    private String baseUrl;
    private String cookies;

    public Client(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
        cookies = "";
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void addCookie(String name, String value) {
        cookies+= name + "=" + value + ";";
    }

    public Map<String, String> getCookies() {
        Map<String, String> cookieMap = new HashMap<>();

        String[] cookieArray = cookies.split(";");
        for (String cookie : cookieArray) {
            String[] cookieElements = cookie.split("=");
            cookieMap.put(cookieElements[0], cookieElements[1]);
        }

        return cookieMap;
    }

    public JSONObject GET(String url) throws ExecutionException, InterruptedException, URISyntaxException {
        URI requestUrl = new URI(this.baseUrl + url);

        HttpRequest request = HttpRequest.newBuilder(requestUrl)
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .header("Cookie", cookies)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();
        CompletableFuture<HttpResponse<String>> responseCompletableFuture = this.client.sendAsync(request, bodyHandler);
        HttpResponse<String> response = responseCompletableFuture.get();

        return new JSONObject(response.body());
    }
}
