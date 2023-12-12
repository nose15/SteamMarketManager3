package com.manager.steamitems;

import org.json.JSONObject;

public class SteamItem {
    private final boolean marketable;
    private final int amount;
    private final String marketHashName;
    private float price;
    private final String classId;

    public SteamItem(JSONObject jsonObject) {
        this.classId = jsonObject.getString("classid");
        this.amount = jsonObject.getInt("amount");
        this.marketHashName = jsonObject.getJSONObject("description").getString("market_hash_name");
        this.marketable = jsonObject.getJSONObject("description").getInt("marketable") != 0;
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

    public String getClassId() { return this.classId; }

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
