package com.manager.marketdata;

import com.manager.steamitems.Case;
import com.manager.steamitems.SteamItem;
import com.manager.steamitems.SteamItemFactory;
import com.manager.steamitems.SteamItemType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: The item amounts are not parsed properly. Its probably because of duplicate class ids. Need to rebuild the whole parser
// TODO: Probably the right thing to do would be to delete the duplicate classes while fetching the assets

public class DataParser {
    private DataParser() {
        throw new AssertionError("Instantiation of static class DataParser");
    }

    public static ArrayList<SteamItem> ParseInventory(JSONObject inventory) {
        JSONArray assets = inventory.getJSONArray("assets");
        JSONArray descriptions = inventory.getJSONArray("descriptions");

        Map<String, Integer> assetCounts = CountAssets(ParseAssetClassIds(assets));
        ArrayList<JSONObject> itemDescriptions = ParseDescriptions(descriptions);
        ArrayList<JSONObject> itemJsons = MatchCountsToDescriptions(itemDescriptions, assetCounts);

        return CreateItemsFromJsons(itemJsons);
    }

    private static Map<String, Integer> CountAssets(ArrayList<String> classIds) {
        Map<String, Integer> counts = new HashMap<>();

        for (var classId : classIds) {
            if (counts.containsKey(classId)) {
                int count = counts.get(classId);
                counts.put(classId, count + 1);
                continue;
            }

            counts.put(classId, 1);
        }

        return counts;
    }

    private static ArrayList<String> ParseAssetClassIds(JSONArray assets) {
        ArrayList<String> classIds = new ArrayList<>();

        for (Object assetObj : assets) {
            JSONObject asset = (JSONObject) assetObj;
            classIds.add(asset.getString("classid"));
        }

        return classIds;
    }

    private static ArrayList<JSONObject> ParseDescriptions(JSONArray descriptions) {
        ArrayList<JSONObject> itemsData = new ArrayList<>();
        ArrayList<String> usedHashNames = new ArrayList<>();

        for (Object description : descriptions) {
            try {
                JSONObject desc = (JSONObject) description;
                int usedHashNamesIndex = usedHashNames.indexOf(desc.getString("market_hash_name"));


                if (usedHashNamesIndex == -1) {
                    JSONObject itemData = FetchItemData(desc);
                    itemsData.add(itemData);
                    usedHashNames.add(itemData.getString("market_hash_name"));
                    continue;
                }

                AddDuplicateClassId(itemsData.get(usedHashNamesIndex), desc.getString("classid"));
            } catch (NullPointerException ignored) {}
        }

        return itemsData;
    }

    private static JSONObject FetchItemData(JSONObject desc) {
        JSONObject itemData = new JSONObject();

        itemData.put("classid", desc.getString("classid"));
        itemData.put("market_hash_name", desc.getString("market_hash_name"));
        itemData.put("marketable", desc.getInt("marketable") != 0);
        itemData.put("type", ParseType(desc));

        return itemData;
    }

    private static ArrayList<JSONObject> MatchCountsToDescriptions(ArrayList<JSONObject> descriptions, Map<String, Integer> assetCounts) {
        for (JSONObject description : descriptions) {
            JSONArray duplicateClassIds = description.optJSONArray("duplicate_classids");

            if (duplicateClassIds == null) {
                String classId = description.getString("classid");
                int amount = assetCounts.get(classId);
                description.put("amount", amount);
                continue;
            }

            description.remove("duplicate_classids");
            description.put("amount", SumDuplicates(duplicateClassIds, assetCounts));
        }

        return descriptions;
    }

    private static ArrayList<SteamItem> CreateItemsFromJsons(ArrayList<JSONObject> itemJsons) {
        ArrayList<SteamItem> steamItems = new ArrayList<>();

        for (JSONObject itemJson : itemJsons) {
            try {
                steamItems.add(SteamItemFactory.CreateFromJson(itemJson));
            }
            catch (RuntimeException e) {
                System.out.println("In CreateItemsFromJsons: " + e.getMessage() + " for " + itemJson.getString("classid"));
            }
        }

        //RemoveDuplicateMarketHashNames(steamItems);
        return steamItems;
    }

    private static int SumDuplicates(JSONArray duplicateClassIds, Map<String, Integer> assetCounts) {
        int totalAmount = 0;

        for (var classIdObj : duplicateClassIds) {
            String classId = classIdObj.toString();
            totalAmount += assetCounts.get(classId);
        }

        return totalAmount;
    }

    private static String ParseType(JSONObject description) {
        JSONArray tags = description.getJSONArray("tags");
        String[] typeTagArr = tags.getJSONObject(0).getString("internal_name").split("_");
        String typeTag = typeTagArr[typeTagArr.length - 1];

        switch (typeTag) {
            case "WeaponCase":
                return "Case";
            case "Spray":
                return "Graffiti";
            case "Shotgun":
            case "SMG":
            case "SniperRifle":
            case "Pistol":
            case "Rifle":
                return "Skin";
            case "Sticker":
                return "Sticker";
            default:
                throw new NullPointerException("Type " + typeTag + " not found");
        }
    }

    private static void AddDuplicateClassId(JSONObject itemData, String classId) {
        JSONArray duplicateClassIds = itemData.optJSONArray("duplicate_classids");

        if (duplicateClassIds == null) {
            duplicateClassIds = new JSONArray();
        }

        AddIfNotIn(classId, duplicateClassIds);
        itemData.put("duplicate_classids", duplicateClassIds);
    }

    private static void AddIfNotIn(String element, JSONArray jsonArray) {
        boolean alreadyAdded = false;
        for (var duplicate : jsonArray) {
            if (Objects.equals(duplicate.toString(), element)) {
                alreadyAdded = true;
            }
        }

        if (!alreadyAdded) {
            jsonArray.put(element);
        }
    }
}
