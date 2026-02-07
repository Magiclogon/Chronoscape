package ma.ac.emi.gamelogic.entity;

import ma.ac.emi.gamelogic.factory.EnemyDefinition;

public class CommonEnnemy extends Ennemy{

    public CommonEnnemy(EnemyDefinition definition) {
		super(definition);
		this.weaponXOffset = 8;
        this.weaponYOffset = 0;	}

}
