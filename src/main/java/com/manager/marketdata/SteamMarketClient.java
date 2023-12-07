package com.manager.marketdata;

import com.manager.httpCommunication.Client;
import com.manager.steamitems.SteamItem;
import com.manager.steamitems.SteamItemFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class SteamMarketClient {
    private final String steamSecure;
    private final Client client;
    private final String userId;
    private String appId;

    public SteamMarketClient(String userId, String appId, String steamSecure) {
        this.userId = userId;
        this.appId = appId;
        this.steamSecure = steamSecure;
        this.client = new Client("https://steamcommunity.com");
    }

    public ArrayList<SteamItem> getInventory() throws URISyntaxException, ExecutionException, InterruptedException {
        JSONObject inventoryJson = RetrieveInventoryJson();
        return ParseInventoryJson(inventoryJson);
    }

    private JSONObject RetrieveInventoryJson() throws URISyntaxException, ExecutionException, InterruptedException {
        this.client.addCookie("steamLoginSecure", this.steamSecure);
        return this.client.GET("/inventory/" + this.userId + "/" + this.appId + "/2?l=english&count=525");
    }

    private ArrayList<SteamItem> ParseInventoryJson(JSONObject inventory) {

        return items;
    }


}
