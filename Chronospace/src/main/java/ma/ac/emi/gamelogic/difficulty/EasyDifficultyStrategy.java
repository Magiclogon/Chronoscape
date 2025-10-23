package ma.ac.emi.gamelogic.difficulty;

import ma.ac.emi.gamelogic.difficulty.DifficultyStarategy.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;
import ma.ac.emi.gamelogic.shop.StatModifier;

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

	@Override
	public void adjustEnemyStats(Ennemy enemy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void adjustPickableDrop(Pickable pickable) {
		// TODO Auto-generated method stub
		
	}
}