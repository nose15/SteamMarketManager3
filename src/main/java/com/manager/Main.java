package com.manager;

import com.manager.httpCommunication.Client;
import org.json.Cookie;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException {
        Client steamClient = new Client("https://steamcommunity.com");
        steamClient.addCookie("steamLoginSecure", System.getenv("STEAM_SECURE"));
        JSONObject data = steamClient.GET("/inventory/76561198151374664/730/2?l=english&count=10");
        System.out.println(data);
    }
}