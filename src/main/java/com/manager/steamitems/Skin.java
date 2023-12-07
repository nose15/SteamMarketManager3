package com.manager.steamitems;

import org.json.JSONObject;

public class Skin implements SteamItem {
    private int amount;
    private boolean marketable;

    public Skin(JSONObject skinJson) {
        System.out.println(((JSONObject)skinJson.get("description")).get("market_hash_name") + " " + skinJson.get("classid").toString());
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
