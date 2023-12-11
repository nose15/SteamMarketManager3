package com.manager.marketdata;

import com.manager.httpCommunication.Client;
import com.manager.httpCommunication.RequestException;
import com.manager.steamitems.SteamItem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.manager.marketdata.DataParser;

public class SteamMarketClient {
    private final String steamSecure;
    private final Client client;
    private final String userId;
    private final String appId;
    private boolean Debug;
    private ArrayList<SteamItem> inventory;
    private final PriceFetcher priceFetcher;

    public SteamMarketClient(String userId, String appId, String steamSecure) {
        this.userId = userId;
        this.appId = appId;
        this.steamSecure = steamSecure;
        this.client = new Client("https://steamcommunity.com");
        this.Debug = false;

        try {
            FetchInventory();
        } catch (FetchingException e) {
            throw new RuntimeException(e);
        }

        this.priceFetcher = new PriceFetcher(this.inventory);
    }

    public SteamMarketClient(String userId, String appId, String steamSecure, boolean debug) {
        this.userId = userId;
        this.appId = appId;
        this.steamSecure = steamSecure;
        this.client = new Client("https://steamcommunity.com");
        this.Debug = debug;

        try {
            FetchInventory();
        } catch (FetchingException e) {
            throw new RuntimeException(e);
        }

        this.priceFetcher = new PriceFetcher(this.inventory);
    }

    public ArrayList<SteamItem> getInventory() {
        return this.inventory;
    }

    private void FetchInventory() throws FetchingException{
        JSONObject inventoryJson = Debug ? FetchJsonFromFile("./mockdata/inventoryRequest.json") : RetrieveInventoryJson();
        this.inventory = DataParser.ParseInventory(inventoryJson);
    }

    private JSONObject RetrieveInventoryJson() throws FetchingException {
        this.client.addCookie("steamLoginSecure", this.steamSecure);
        try {
            return this.client.GET("/inventory/" + this.userId + "/" + this.appId + "/2?l=english&count=525");
        } catch (RequestException e) {
            throw new FetchingException(e);
        }
    }

    public void SetDebug(boolean value) {
        this.Debug = value;
    }

    private JSONObject FetchJsonFromFile(String filePath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            return new JSONObject(jsonTokener);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
