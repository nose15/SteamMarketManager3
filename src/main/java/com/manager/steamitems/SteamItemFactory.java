package com.manager.steamitems;

import org.json.JSONArray;
import org.json.JSONObject;

public class SteamItemFactory {
    // TODO: Remove this json from SteamItem constructors.

    public static SteamItem CreateSteamItem(String classId, int amount, String className, String marketHashName, boolean marketable) {
        JSONObject itemJsonObj = MakeItemJsonObj(classId, amount, marketHashName, marketable);
        return MakeSteamItemOfClass(className, itemJsonObj);
    }

    public static SteamItem CreateSteamItem(String classId, int amount, JSONObject description) {
        SteamItemType type = ParseType(description);
        JSONObject itemJsonObj = MakeItemJsonObj(classId, amount, description);
        return MakeSteamItemOfType(type, itemJsonObj);
    }

    public static SteamItem CreateSteamItem(String classId, JSONObject description) {
        SteamItemType type = ParseType(description);
        JSONObject itemJsonObj = MakeItemJsonObj(classId, description.getInt("amount"), description);
        return MakeSteamItemOfType(type, itemJsonObj);
    }

    private static SteamItemType ParseType(JSONObject description) {
        JSONArray tags = description.getJSONArray("tags");
        String[] typeTagArr = tags.getJSONObject(0).getString("internal_name").split("_");
        String typeTag = typeTagArr[typeTagArr.length - 1];

        switch (typeTag) {
            case "WeaponCase":
                return SteamItemType.Case;
            case "Spray":
                return SteamItemType.Graffiti;
            case "Shotgun":
            case "SMG":
            case "SniperRifle":
            case "Pistol":
            case "Rifle":
                return SteamItemType.Skin;
            case "Sticker":
                return SteamItemType.Sticker;
            default:
                throw new NullPointerException("Type " + typeTag + " not found");
        }
    }

    private static JSONObject MakeItemJsonObj(String classId, int amount, String marketHashName, boolean marketable) {
        JSONObject itemJsonObj = new JSONObject();
        itemJsonObj.put("classid", classId);
        itemJsonObj.put("amount", amount);

        JSONObject description = new JSONObject();
        description.put("market_hash_name", marketHashName);
        description.put("marketable", marketable ? 1 : 0);

        itemJsonObj.put("description", description);
        return itemJsonObj;
    }


    private static JSONObject MakeItemJsonObj(String classId, int amount, JSONObject description) {
        JSONObject itemJsonObj = new JSONObject();
        itemJsonObj.put("classid", classId);
        itemJsonObj.put("amount", amount);
        itemJsonObj.put("description", description);

        return itemJsonObj;
    }

    private static SteamItem MakeSteamItemOfClass(String className, JSONObject itemJsonObj) {
        switch (className) {
            case "Case":
                return new Case(itemJsonObj);
            case "Graffiti":
                return new Graffiti(itemJsonObj);
            case "Skin":
                return new Skin(itemJsonObj);
            case "Sticker":
                return new Sticker(itemJsonObj);
            default:
                throw new IllegalArgumentException("Type not recognized");
        }
    }

    private static SteamItem MakeSteamItemOfType(SteamItemType type, JSONObject itemJsonObj) {
        switch (type) {
            case Case:
                return new Case(itemJsonObj);
            case Graffiti:
                return new Graffiti(itemJsonObj);
            case Skin:
                return new Skin(itemJsonObj);
            case Sticker:
                return new Sticker(itemJsonObj);
            default:
                throw new IllegalArgumentException("Type not recognized");
        }
    }
}
