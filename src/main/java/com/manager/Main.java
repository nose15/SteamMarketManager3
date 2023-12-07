package com.manager;

import com.manager.httpCommunication.Client;
import com.manager.marketdata.SteamMarketClient;
import org.json.Cookie;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException {
        SteamMarketClient steamMarketClient = new SteamMarketClient("76561198151374664", "730", System.getenv("STEAM_SECURE"));
        steamMarketClient.getInventory();
    }
}