package ma.ac.emi.gamelogic.shop;

import com.google.gson.*;
import java.io.FileReader;
import java.util.*;

public class ItemLoader {
    private Map<Rarity, Map<String, ItemDefinition>> itemsByRarity = new EnumMap<>(Rarity.class);
    private static ItemLoader instance;

    private ItemLoader() {
        for (Rarity r : Rarity.values())
            itemsByRarity.put(r, new HashMap<>());

    }
    
    public static ItemLoader getInstance() {
    	if(instance == null) {
    		instance = new ItemLoader();
    	}
    	return instance;
    }

    public void loadItems(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray itemsArray = root.getAsJsonArray("items");

            Gson gson = new Gson();

            for (JsonElement el : itemsArray) {
                JsonObject obj = el.getAsJsonObject();
                String type = obj.get("type").getAsString();

                ItemDefinition def = null;
                switch (type) {
                    case "weapon":
                        def = gson.fromJson(obj, WeaponItemDefinition.class);
                        break;
                    case "statModifier":
                        def = gson.fromJson(obj, StatModifierItemDefinition.class);
                        break;
                }

                if (def != null)
                    itemsByRarity.get(def.getRarity()).put(def.getId(), def);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Rarity, Map<String, ItemDefinition>> getItemsByRarity() {
        return itemsByRarity;
    }

    public Map<String, ItemDefinition> getItemsOfRarity(Rarity rarity) {
        return itemsByRarity.getOrDefault(rarity, Collections.emptyMap());
    }
}
