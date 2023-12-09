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
        JSONArray assets = inventory.getJSONArray("assets");
        JSONArray descriptions = inventory.getJSONArray("descriptions");
        ArrayList<SteamItem> steamItems = new ArrayList<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (var asset : assets) {
            JSONObject assetJson = (JSONObject) asset;
            String classid = assetJson.getString("classid");

            if (countMap.containsKey(classid)) {
                int count = (int)countMap.get(classid);
                countMap.put(classid, count + 1);
                continue;
            }

            countMap.put(classid, 1);
        }

        ArrayList<String> countMapKeys = new ArrayList<>(countMap.keySet());
        for (int i = 0; i < countMapKeys.size(); i++) {
            String key = countMapKeys.get(i);
            try {
                steamItems.add(SteamItemFactory.createFromJson(key, countMap.get(key), (JSONObject) descriptions.get(i)));
            } catch (RuntimeException e) {
                System.out.println(e.getMessage() + " for " + key);
            }
        }

        return steamItems;
    }


}
