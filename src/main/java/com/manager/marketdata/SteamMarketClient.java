package com.manager.marketdata;

import com.manager.httpCommunication.Client;
import com.manager.steamitems.SteamItem;
import com.manager.steamitems.SteamItemFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class SteamMarketClient {
    private final String steamSecure;
    private final Client client;
    private final String userId;
    private final String appId;
    private boolean Debug;
    private ArrayList<SteamItem> inventory;
    private PriceFetcher priceFetcher;

    public SteamMarketClient(String userId, String appId, String steamSecure) {
        this.userId = userId;
        this.appId = appId;
        this.steamSecure = steamSecure;
        this.client = new Client("https://steamcommunity.com");
        this.Debug = true;

        FetchInventory();
        this.priceFetcher = new PriceFetcher(this.inventory);
    }

    //TODO: A method that returns a stream of prices.
    public ArrayList<SteamItem> getInventory() {
        return this.inventory;
    }

    private void FetchInventory() {
        JSONObject inventoryJson = new JSONObject();

        if (Debug) {
            String filePath = "./mockdata/inventoryRequest.json";

            try (FileReader fileReader = new FileReader(filePath)) {
                //TODO: Diy
                JSONTokener jsonTokener = new JSONTokener(fileReader);
                inventoryJson = new JSONObject(jsonTokener);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            inventoryJson = RetrieveInventoryJson();
        }

        this.inventory = ParseInventoryJson(inventoryJson);
    }

    private JSONObject RetrieveInventoryJson() {
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

    public void SetDebug(boolean value) {
        this.Debug = value;
    }
}
