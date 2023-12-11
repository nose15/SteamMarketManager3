package com.manager.steamitems;

import org.json.JSONArray;
import org.json.JSONObject;

public class SteamItemFactory {
    public static SteamItem CreateSteamItem(String classId, int amount, JSONObject description) {
        JSONArray tags = description.getJSONArray("tags");
        String[] typeTagArr = tags.getJSONObject(0).getString("internal_name").split("_");
        String typeTag = typeTagArr[typeTagArr.length - 1];

        JSONObject itemJsonObj = new JSONObject();
        itemJsonObj.put("classid", classId);
        itemJsonObj.put("amount", amount);
        itemJsonObj.put("description", description);

        switch (typeTag) {
            case "WeaponCase":
                return new Case(itemJsonObj);
            case "Spray":
                return new Graffiti(itemJsonObj);
            case "Shotgun":
            case "SMG":
            case "SniperRifle":
            case "Pistol":
            case "Rifle":
                return new Skin(itemJsonObj);
            case "Sticker":
                return new Sticker(itemJsonObj);
        }

        throw new RuntimeException("Invalid category");
    }

    public static SteamItem CreateSteamItem(String classId, JSONObject description) {
        JSONArray tags = description.getJSONArray("tags");
        String[] typeTagArr = tags.getJSONObject(0).getString("internal_name").split("_");
        String typeTag = typeTagArr[typeTagArr.length - 1];

        JSONObject itemJsonObj = new JSONObject();
        itemJsonObj.put("classid", classId);
        itemJsonObj.put("amount", description.getInt("amount"));
        itemJsonObj.put("description", description);

        switch (typeTag) {
            case "WeaponCase":
                return new Case(itemJsonObj);
            case "Spray":
                return new Graffiti(itemJsonObj);
            case "Shotgun":
            case "SMG":
            case "SniperRifle":
            case "Pistol":
            case "Rifle":
                return new Skin(itemJsonObj);
            case "Sticker":
                return new Sticker(itemJsonObj);
        }

        throw new RuntimeException("Invalid category");
    }
}
