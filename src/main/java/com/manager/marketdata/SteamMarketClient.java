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
    private boolean debug;
    private ArrayList<SteamItem> inventory;
    private final PriceFetcher priceFetcher;

    public SteamMarketClient(String userId, String appId, String steamSecure, boolean debug) {
        this.userId = userId;
        this.appId = appId;
        this.steamSecure = steamSecure;
        this.client = new Client("https://steamcommunity.com");
        this.debug = debug;
        this.inventory = new ArrayList<>();
        this.priceFetcher = new PriceFetcher(this.inventory, debug);
    }

    public void Run() {
        try {
            FetchInventory();
            priceFetcher.Run();
        } catch (FetchingException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetDebug(boolean value) {
        this.debug = value;
    }

    public ArrayList<SteamItem> GetInventory() {
        return this.inventory;
    }

    private void FetchInventory() throws FetchingException {
        JSONObject inventoryJson;

        if (debug) {
            inventoryJson = FetchJsonFromFile("./mockdata/inventoryRequest.json");
        }
        else {
            inventoryJson = RetrieveInventoryJson();
        }

        this.inventory.addAll(DataParser.ParseInventory(inventoryJson));
    }

    private JSONObject RetrieveInventoryJson() throws FetchingException {
        this.client.AddCookie("steamLoginSecure", this.steamSecure);
        try {
            return this.client.GET("/inventory/" + this.userId + "/" + this.appId + "/2?l=english&count=525");
        }
        catch (RequestException e) {
            throw new FetchingException(e);
        }
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
