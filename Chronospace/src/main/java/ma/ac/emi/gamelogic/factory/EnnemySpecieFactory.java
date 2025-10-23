package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.entity.Ennemy;

public interface EnnemySpecieFactory {
    Ennemy createCommon();
    Ennemy createSpeedster();
    Ennemy createTank();
    Ennemy createRanged();
    Ennemy createBoss();
}