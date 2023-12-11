package com.manager;

import com.manager.marketdata.SteamMarketClient;
import com.manager.steamitems.SteamItem;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException {
        SteamMarketClient steamMarketClient = new SteamMarketClient("76561198151374664", "730", System.getenv("STEAM_SECURE"), true);
        ArrayList<SteamItem> inventory = steamMarketClient.getInventory();

        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

        AtomicInteger currentStep = new AtomicInteger();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Inventory Fetch");
            for (int i = currentStep.get(); i < currentStep.get() + 20; i++) {
                SteamItem item = inventory.get(i);
                System.out.println(item.getMarketHashName() + " " + item.getPricePerPiece());

                if (i == inventory.size() - 1) {
                    currentStep.set(-20);
                    break;
                }
            }

            currentStep.addAndGet(20);
        }, 10, 60, TimeUnit.SECONDS);
    }
}