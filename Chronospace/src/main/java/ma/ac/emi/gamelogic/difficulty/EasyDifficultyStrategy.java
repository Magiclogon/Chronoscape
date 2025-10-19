package ma.ac.emi.gamelogic.difficulty;

public class EasyDifficultyStrategy implements DifficultyStrategy {
    @Override
    public void addStatModifier(String statName, StatModifier modifier) {
        if (!activeEffects.containsKey(statName)) {
            activeEffects.put(statName, new ArrayList<>());
        }
        activeEffects.get(statName).add(modifier);
    }

    public void removeStatModifier(String statName, StatModifier modifier) {
        if (activeEffects.containsKey(statName)) {
            activeEffects.get(statName).remove(modifier);
        }
    }
}