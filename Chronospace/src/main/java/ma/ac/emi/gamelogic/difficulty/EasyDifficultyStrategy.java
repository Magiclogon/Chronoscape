package ma.ac.emi.gamelogic.difficulty;

import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;
import ma.ac.emi.gamelogic.shop.StatModifier;
import ma.ac.emi.gamelogic.wave.Wave;

public class EasyDifficultyStrategy extends AbstractDifficultyStrategy {
    @Override
    public double getEnemyHpMultiplier() { return 0.8; }

    @Override
    public double getEnemyDamageMultiplier() { return 0.8; }

    @Override
    public double getPickableDropRate() { return 1.2; }

    @Override
    public double getEnemyCountMultiplier() { return 1; }
}