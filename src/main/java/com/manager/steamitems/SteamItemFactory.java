package com.manager.steamitems;

import org.json.JSONArray;
import org.json.JSONObject;

public class SteamItemFactory {
    public static SteamItem CreateFromJson(JSONObject itemJson) {
        switch (itemJson.getString("type")) {
            case "Case":
                return new Case(itemJson);
            case "Graffiti":
                return new Graffiti(itemJson);
            case "Skin":
                return new Skin(itemJson);
            case "Sticker":
                return new Sticker(itemJson);
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
