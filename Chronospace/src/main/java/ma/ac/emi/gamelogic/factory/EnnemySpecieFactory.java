package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;

public abstract class EnnemySpecieFactory implements DifficultyObserver {
	protected DifficultyStrategy currentDifficulty;

	public EnnemySpecieFactory() {
		GameController.getInstance().addDifficultyObserver(this);
		this.currentDifficulty = GameController.getInstance().getDifficulty();
	}

	@Override
	public void refreshDifficulty(DifficultyStrategy difficulty) {
		this.currentDifficulty = difficulty;
	}

	// Applique la difficulté aux ennemies crées
	protected void applyDifficultyStats(Ennemy enemy) {
		if (currentDifficulty == null) return;

		enemy.setHpMax(enemy.getHpMax() * currentDifficulty.getEnemyHpMultiplier());
		enemy.setHp(enemy.getHpMax());
		enemy.setDamage(enemy.getDamage() * currentDifficulty.getEnemyDamageMultiplier());
		enemy.setSpeed(enemy.getSpeed() * currentDifficulty.getEnemySpeedMultiplier());
	}

	public abstract Ennemy createCommon();
	public abstract Ennemy createSpeedster();
	public abstract Ennemy createTank();
	public abstract Ennemy createRanged();
	public abstract Ennemy createBoss();
}