package com.manager.steamitems;

import org.json.JSONObject;

public class Graffiti implements SteamItem {
    private int amount;
    private boolean marketable;

    public Graffiti(JSONObject graffitiJson) {
        System.out.println(((JSONObject)graffitiJson.get("description")).get("market_hash_name") + " " + graffitiJson.get("classid").toString());
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public boolean isMarketable() {
        return this.marketable;
    }
}
