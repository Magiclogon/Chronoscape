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
                        
                        WeaponItemDefinition weaponDef = (WeaponItemDefinition) def;
                        if(obj.has("attackStrategy")) {
                        	String attackType = obj.get("attackStrategy").getAsJsonObject().get("type").getAsString();
                        	
                        	switch(attackType) {
                        	case "range":
                        		int projectileCount = obj.get("attackStrategy").getAsJsonObject().get("projectileCount").getAsInt();
                        		double spread = obj.get("attackStrategy").getAsJsonObject().get("spread").getAsDouble();
                        		weaponDef.setAttackStrategyDefinition(new WeaponItemDefinition.RangeStrategyDefinition(projectileCount, spread));
                        		break;
                        	
                        	case "melee":
                        		weaponDef.setAttackStrategyDefinition(new WeaponItemDefinition.MeleeStrategyDefinition());
                        	}
                        }
                        
                        def = weaponDef;
                        break;
                    case "statModifier":
                        def = gson.fromJson(obj, StatModifierItemDefinition.class);
                        break;
                    case "upgrade":
                        def = gson.fromJson(obj, UpgradeItemDefinition.class);
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

	public Map<Rarity, Map<String, ItemDefinition>> getItemsCopy() {
		Map<Rarity, Map<String, ItemDefinition>> deepCopy = new HashMap<>();

		for (Map.Entry<Rarity, Map<String, ItemDefinition>> rarityEntry : itemsByRarity.entrySet()) {
		    Map<String, ItemDefinition> innerMapCopy = new HashMap<>();

		    for (Map.Entry<String, ItemDefinition> itemEntry : rarityEntry.getValue().entrySet()) {
		        ItemDefinition original = itemEntry.getValue();

		        // Create a manual deep copy depending on subclass type
		        ItemDefinition copy = original.clone();

		        innerMapCopy.put(itemEntry.getKey(), copy);
		    }

		    deepCopy.put(rarityEntry.getKey(), innerMapCopy);
		}

		return deepCopy;
	}

    public ItemDefinition getBaseItemDefinition(String id) {
        for (Map<String, ItemDefinition> map : itemsByRarity.values()) {
            if (map.containsKey(id))
                return map.get(id);
        }
        return null;
    }
}
