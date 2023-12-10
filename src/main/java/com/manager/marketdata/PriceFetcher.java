package com.manager.marketdata;

import com.manager.httpCommunication.Client;
import com.manager.steamitems.SteamItem;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PriceFetcher {
    ArrayList<SteamItem> inventory;
    Client client = new Client("https://steamcommunity.com/market/priceoverview/?appid=730&market_hash_name=");

    public PriceFetcher(ArrayList<SteamItem> inventory) {
        this.inventory = inventory;
        StartFetchingPrices();
    }

    private void StartFetchingPrices() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            for (int i = 0; i < 20; i++) {
                SteamItem item = this.inventory.get(i);
                if (item.isMarketable()) {
                    float price = FetchPrice(item.getMarketHashName());
                    item.setPrice(price);
                }

                if (i > this.inventory.size())
                {
                    i = 0;
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private float FetchPrice(String marketHashName){
        JSONObject priceOverview = client.GET(marketHashName);
        System.out.println(priceOverview);
        float price = Float.parseFloat(priceOverview.getString("lowest_price").substring(1));
        return price;

    }
}