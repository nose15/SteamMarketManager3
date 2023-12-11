package com.manager.marketdata;

import com.manager.httpCommunication.Client;
import com.manager.httpCommunication.RequestException;
import com.manager.steamitems.SteamItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PriceFetcher {
    ArrayList<SteamItem> inventory;
    Client client = new Client("https://steamcommunity.com/market/priceoverview/?appid=730&market_hash_name=");

    public PriceFetcher(ArrayList<SteamItem> inventory) {
        this.inventory = inventory;
        StartFetchingPrices();
    }

    private void StartFetchingPrices() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        AtomicInteger currentStep = new AtomicInteger(0);
        scheduler.scheduleAtFixedRate(() -> {
            FetchPrices(currentStep, 20);
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void FetchPrices(AtomicInteger begin, int count) {
        int step = begin.get();

        for (int i = step; i < step + (count - 1); i++) {
            SteamItem item = this.inventory.get(i);
            if (item.isMarketable()) {
                try {
                    float price = FetchSinglePrice(item.getMarketHashName());
                    item.setPrice(price);
                }
                catch (IllegalArgumentException | FetchingException e) {
                    System.out.println(e + e.getMessage() + " for " + item.getMarketHashName());
                }
            }

            if (i == inventory.size() - 1) {
                begin.set(-count);
                break;
            }
        }

        begin.addAndGet(count);
    }

    private float FetchSinglePrice(String marketHashName) throws FetchingException {
        try {
            JSONObject priceOverview = client.GET(marketHashName);
            return ParsePriceFromJson(priceOverview);
        } catch (RequestException | ParsingException e) {
            throw new FetchingException(e);
        }
    }

    private float ParsePriceFromJson(JSONObject priceOverview) throws ParsingException {
        try {
            return Float.parseFloat(priceOverview.getString("lowest_price").substring(1));
        } catch (JSONException e) {
            throw new ParsingException(e);
        }
    }
}