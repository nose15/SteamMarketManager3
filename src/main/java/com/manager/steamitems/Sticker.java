package com.manager.steamitems;

import org.json.JSONObject;

public class Sticker implements SteamItem{
    private int amount;
    private boolean marketable;

    public Sticker(JSONObject stickerJson) {
        System.out.println(((JSONObject)stickerJson.get("description")).get("market_hash_name") + " " + stickerJson.get("classid").toString());
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
