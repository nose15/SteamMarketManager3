package com.manager.steamitems;

import org.json.JSONObject;

public class Case implements SteamItem {
    private int amount;
    private boolean marketable;

    public Case(JSONObject caseJson) {
        System.out.println(((JSONObject)caseJson.get("description")).get("market_hash_name") + " " + caseJson.get("classid").toString());
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
