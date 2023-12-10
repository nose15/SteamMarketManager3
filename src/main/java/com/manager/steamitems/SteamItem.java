package com.manager.steamitems;

import org.json.JSONObject;

public class SteamItem {
    private final boolean marketable;
    private final int amount;
    private final String marketHashName;
    private float price;

    public SteamItem(JSONObject jsonObject) {
        this.amount = jsonObject.getInt("amount");
        JSONObject description = jsonObject.getJSONObject("description");

        this.marketHashName = description.getString("market_hash_name");
        this.marketable = description.getInt("marketable") != 0;
        this.price = -1;
    }

    public int getAmount() {
        return amount;
    }

    public String getMarketHashName() {
        return this.marketHashName;
    }

    public void setPrice(float newPrice) {
        this.price = newPrice;
    }

    public float getPricePerPiece() {
        return this.price;
    }

    public float getTotalPrice() {
        return this.price * this.amount;
    }

    public boolean isMarketable() {
        return marketable;
    }
}
