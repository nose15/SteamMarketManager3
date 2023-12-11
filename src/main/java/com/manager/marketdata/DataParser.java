package com.manager.marketdata;

import com.manager.steamitems.SteamItem;
import com.manager.steamitems.SteamItemFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class DataParser {
    public static ArrayList<SteamItem> ParseInventory(JSONObject inventory) {
        JSONArray assets = inventory.getJSONArray("assets");
        JSONArray descriptions = inventory.getJSONArray("descriptions");

        Map<String, Integer> assetMap = ParseAssets(assets);
        Map<String, JSONObject> itemsMap = MatchDescriptions(descriptions, assetMap);

        return DataParser.CreateItemsFromMap(itemsMap);
    }

    private static Map<String, Integer> ParseAssets(JSONArray assets) {
        Map<String, Integer> countMap = new HashMap<>();

        for (var asset : assets) {
            JSONObject assetJson = (JSONObject) asset;
            String classid = assetJson.getString("classid");

            if (countMap.containsKey(classid)) {
                int count = countMap.get(classid);
                countMap.put(classid, count + 1);
                continue;
            }

            countMap.put(classid, 1);
        }

        return countMap;
    }

    private static Map<String, JSONObject> MatchDescriptions(JSONArray descriptions, Map<String, Integer> assets) {
        Map<String, JSONObject> assetDescriptions = new HashMap<>();

        ArrayList<String> assetKeys = new ArrayList<>(assets.keySet());
        for (int i = 0; i < assetKeys.size(); i++) {
            String key = assetKeys.get(i);
            try {
                JSONObject description = descriptions.getJSONObject(i);
                description.put("amount", assets.get(key));
                assetDescriptions.put(key, description);
            } catch (RuntimeException ignored) {}
        }

        return assetDescriptions;
    }

    private static ArrayList<SteamItem> CreateItemsFromMap(Map<String, JSONObject> itemsMap) {
        ArrayList<SteamItem> steamItems = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>(itemsMap.keySet());

        for (String key : keys) {
            try {
                steamItems.add(SteamItemFactory.CreateSteamItem(key, itemsMap.get(key)));
            }
            catch (RuntimeException e) {
                System.out.println("In createItemsFromMap: " + e.getMessage() + " for " + key);
            }
        }

        RemoveDuplicateMarketHashNames(steamItems);
        return steamItems;
    }

    private static void RemoveDuplicateMarketHashNames(ArrayList<SteamItem> steamItems) {
        ArrayList<String> duplicates = GetDuplicateHashNames(steamItems);

        for (String duplicateName : duplicates) {
            ArrayList<SteamItem> duplicateItems = new ArrayList<>();


            for (SteamItem item : steamItems) {
                if (Objects.equals(item.getMarketHashName(), duplicateName)) {
                    duplicateItems.add(item);
                }
            }

            String newClassId = duplicateItems.get(0).getClassId();

            int totalAmount = 0;
            for (SteamItem duplicateItem : duplicateItems) {
                totalAmount += duplicateItem.getAmount();
            }

            //TODO: Once a new factory and new Item constructors are done, create a new object
            System.out.println("DUP: " + duplicateName);
        }
    }

    private static ArrayList<String> GetDuplicateHashNames(ArrayList<SteamItem> items) {
        Map<String, Integer> hashNameCount = new HashMap<>();

        for (SteamItem item : items) {
            String marketHashName = item.getMarketHashName();

            if (hashNameCount.containsKey(marketHashName)) {
                int value = hashNameCount.get(marketHashName);
                hashNameCount.put(marketHashName, value + 1);
                continue;
            }

            hashNameCount.put(marketHashName, 1);
        }

        return new ArrayList<>(hashNameCount
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).keySet());
    }

}
