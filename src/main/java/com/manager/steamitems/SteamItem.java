package com.manager.steamitems;

import org.json.JSONObject;

public class SteamItem {
    private final String classId;
    private final int amount;
    private final String marketHashName;
    private final boolean marketable;
    private float price;
    private final String type;

    public SteamItem(JSONObject jsonObject) {
        this.classId = jsonObject.getString("classid");
        this.amount = jsonObject.getInt("amount");
        this.marketHashName = jsonObject.getString("market_hash_name");
        this.marketable = jsonObject.getBoolean("marketable");
        this.type = jsonObject.getString("type");
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

    public String getClassId() {
        return this.classId;
    }

    public float getPricePerPiece() {
        return this.price;
    }

    public float getTotalPrice() {
        return this.price * this.amount;
    }

    public String getType() {
        return this.type;
    }
    public boolean isMarketable() {
        return marketable;
    }
}
