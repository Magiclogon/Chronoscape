package ma.ac.emi.gamelogic.shop;

import com.google.gson.*;

import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.gamelogic.entity.behavior.EntityBehaviorDefinition;
import ma.ac.emi.gamelogic.entity.behavior.EntityBehaviorFactory;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorFactory;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingFactory;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

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
                        	JsonObject attackStrategyObj = obj.get("attackStrategy").getAsJsonObject();
                        	String attackType = attackStrategyObj.get("type").getAsString();

                        	switch(attackType) {
                        	case "range":
                        		int projectileCount = obj.get("attackStrategy").getAsJsonObject().get("projectileCount").getAsInt();
                        		double spread = obj.get("attackStrategy").getAsJsonObject().get("spread").getAsDouble();
                        		weaponDef.setAttackStrategyDefinition(new WeaponItemDefinition.RangeStrategyDefinition(projectileCount, spread));
                        		break;

                        	case "melee":
                        		weaponDef.setAttackStrategyDefinition(new WeaponItemDefinition.MeleeStrategyDefinition());
                        		break;
                        	}

                        	if(attackStrategyObj.has("cameraShake")) {
                        		double intensity = attackStrategyObj.get("cameraShake").getAsJsonObject().get("intensity").getAsDouble();
                        		double dampingFactor = attackStrategyObj.get("cameraShake").getAsJsonObject().get("damping").getAsDouble();
                        		CameraShakeDefinition camDef = new CameraShakeDefinition(intensity, dampingFactor);

                        		weaponDef.getAttackStrategyDefinition().setCameraShakeDefinition(camDef);
                        	}
                        }

                        if (obj.has("postProcessingDetails")) {
                        	System.out.println("Weapon with id: " + obj.get("id").getAsString());
        	                JsonObject ppDetails = obj.getAsJsonObject("postProcessingDetails");
        	                PostProcessingDetails postProcessing = gson.fromJson(ppDetails, PostProcessingDetails.class);

        	                // Create ColorCorrection from config
        	                SpriteColorCorrection colorCorrection = PostProcessingFactory.createColorCorrection(postProcessing);
        	                weaponDef.setColorCorrection(colorCorrection);

        	                // Create LightingStrategy from config
        	                LightingStrategy lightingStrategy = PostProcessingFactory.createLightingStrategy(postProcessing);
        	                weaponDef.setLightingStrategy(lightingStrategy);
        	            }
                        
                      List<WeaponBehaviorDefinition> behaviors = new ArrayList<>();
                    	if (obj.has("behaviors")) {
                            JsonArray behaviorArray = obj.getAsJsonArray("behaviors");
                            for (JsonElement b : behaviorArray) {
                            	JsonObject behaviorJson = b.getAsJsonObject();
                                behaviors.add(WeaponBehaviorFactory.create(behaviorJson));
                            }
                        }
                    	
                    	weaponDef.setBehaviorDefinitions(behaviors);
                        
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
