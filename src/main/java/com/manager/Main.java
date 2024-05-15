package com.manager;

import com.manager.guiApp.GUIApp;
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
        SteamMarketClient steamMarketClient = new SteamMarketClient("user_id", "730", System.getenv("STEAM_SECURE"), true);
        steamMarketClient.Run();
        ArrayList<SteamItem> inventory = steamMarketClient.GetInventory();
        GUIApp guiApp = GUIApp.getInstance();
        guiApp.displayArrayListLive(inventory);
    }
}
