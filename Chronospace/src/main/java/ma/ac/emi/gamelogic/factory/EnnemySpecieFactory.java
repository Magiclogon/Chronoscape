package ma.ac.emi.gamelogic.factory;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;

@Getter
@Setter
public abstract class EnnemySpecieFactory implements DifficultyObserver{
	protected DifficultyStrategy difficulty;
	
	public EnnemySpecieFactory() {
		GameController.getInstance().addDifficultyObserver(this);
        System.out.println("manager added");

	}
	
	@Override
	public void refreshDifficulty(DifficultyStrategy difficulty) {
		setDifficulty(difficulty);
		System.out.println("difficulty set");
	}
    public abstract Ennemy createCommon();
    public abstract Ennemy createSpeedster();
    public abstract Ennemy createTank();
    public abstract Ennemy createRanged();
    public abstract Ennemy createBoss();
}