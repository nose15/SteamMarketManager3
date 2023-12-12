package com.manager.marketdata;

import com.manager.httpCommunication.Client;
import com.manager.httpCommunication.RequestException;
import com.manager.steamitems.SteamItem;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PriceFetcher {
    private final ArrayList<SteamItem> inventory;
    private final Client client = new Client("https://steamcommunity.com/market/priceoverview/?appid=730&market_hash_name=");
    private boolean debug;


    public PriceFetcher(ArrayList<SteamItem> inventory, boolean debug) {
        this.inventory = inventory;
        this.debug = debug;
    }

    public void Run() {
        if (debug) {
            StartFetchingPrices(2, 2, 20);
            return;
        }

        StartFetchingPrices(5, 60, 20);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void StartFetchingPrices(int initialDelay, int interval, int countPerCycle) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        AtomicInteger currentStep = new AtomicInteger(0);
        scheduler.scheduleAtFixedRate(() -> {
            FetchPrices(currentStep, countPerCycle);
        }, initialDelay, interval, TimeUnit.SECONDS);
    }

    private void FetchPrices(AtomicInteger begin, int count) {
        int currentStep = begin.get();
        int requestsMade = 0;

        for (int i = currentStep; i < currentStep + (count - 1); i++) {
            if (i == inventory.size()) {
                begin.set(-requestsMade);
                break;
            }

            SteamItem item = this.inventory.get(i);

            if (!item.isMarketable()) {
                continue;
            }

            if (this.debug) {
                JSONObject priceoverview = FetchJsonFromFile("./mockdata/priceoverview.json");
                item.setPrice(priceoverview.getFloat("lowest_price"));
                requestsMade++;
                continue;
            }

            try {
                item.setPrice(FetchSinglePrice(item.getMarketHashName()));
                requestsMade++;
            }
            catch (IllegalArgumentException | FetchingException e) {
                System.out.println(e + e.getMessage() + " for " + item.getMarketHashName());
            }
            catch (RequestException e) {
                switch (e.getMessage()) {
                    case "429":
                        System.out.println("Rate limit exceeded for " + item.getMarketHashName());
                        break;
                    case "400":
                        System.out.println("No permission for " + item.getMarketHashName());
                        break;
                }
                break;
            }
        }

        begin.addAndGet(requestsMade);
    }

    private float FetchSinglePrice(String marketHashName) throws FetchingException, RequestException {
        try {
            JSONObject priceOverview = client.GET(marketHashName);
            return ParsePriceFromJson(priceOverview);
        } catch (ParsingException e) {
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

    private float ParsePriceFromJson(JSONObject priceOverview) throws ParsingException {
        try {
            return Float.parseFloat(priceOverview.getString("lowest_price").substring(1));
        } catch (JSONException e) {
            throw new ParsingException(e);
        }
    }
}