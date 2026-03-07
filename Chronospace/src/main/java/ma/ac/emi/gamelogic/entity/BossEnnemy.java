package ma.ac.emi.gamelogic.entity;

import java.util.List;

import lombok.Getter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.ai.RobotBossAIBehavior;
import ma.ac.emi.gamelogic.factory.BossEnemyDefinition;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.WeaponItemFactory;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.math.Vector3D;

@Getter
public class BossEnnemy extends Ennemy{
	private Weapon[] equippedWeapons;
	private WeaponItem[] equippedWeaponItems;
	private int weaponIndex;
	private boolean switching;

    public BossEnnemy() {
    	hasHands = true;
    	equippedWeaponItems = new WeaponItem[Inventory.MAX_EQU];
    	equippedWeapons = new Weapon[Inventory.MAX_EQU];

	}
    
    public void initWeaponItems() {
    	int i = 0;
    	for(String weaponId : ((BossEnemyDefinition)definition).weaponIds) {
    		WeaponItem item = WeaponItemFactory.getInstance().createWeaponItem(weaponId);
    		equippedWeaponItems[i] = item;
    		i++;
    	}
    }
    
    @Override
    public void initWeapon() {
    	initWeaponItems();
		for(Weapon weapon: equippedWeapons) {
			if(weapon != null) GameController.getInstance().removeDrawable(weapon);
		}
		for(int i = 0; i < Inventory.MAX_EQU; i++) {
			if(getEquippedWeaponItems()[i] == null) {
				equippedWeapons[i] = null;
				continue;
			}
			WeaponItem item = getEquippedWeaponItems()[i];
			List<WeaponBehaviorDefinition> behaviors = ((WeaponItemDefinition) item.getItemDefinition()).getBehaviorDefinitions();
			Weapon weapon = new Weapon(item, this);
			
			behaviors.forEach(b -> weapon.getBehaviors().add(b.create()));
			weapon.init();
			
			GameController.getInstance().removeDrawable(weapon);

			weapon.setAttackObjectManager(attackObjectManager);
			weapon.snapTo(this);
			equippedWeapons[i] = weapon;
		}
		setActiveWeapon(equippedWeapons[weaponIndex]);
	}
    
    @Override
    public void update(double step, Vector3D target) {
    	super.update(step, target);
    	
    	if(aiBehavior != null) {
	    	if(((RobotBossAIBehavior)aiBehavior).consumeShouldSwitchWeapon()) {
				switching = true;
			}
    	}
		if(switching) {
			switchWeapons();
		}
    }
    
    @Override
	public void switchWeapons() {
		if(activeWeapon != null) {
			activeWeapon.triggerSwitchingOut();
		
			if(activeWeapon.isCurrentAnimationDone()) {
				activeWeapon.resetCurrentAnimation();
				
				weaponIndex = (int) (((RobotBossAIBehavior)aiBehavior).getWeaponPointer()*Inventory.MAX_EQU);
				
				GameController.getInstance().removeDrawable(activeWeapon);
				activeWeapon = equippedWeapons[weaponIndex];
				if(activeWeapon != null) GameController.getInstance().addDrawable(activeWeapon);
				
				switching = false;
				if(activeWeapon != null) activeWeapon.triggerSwitchingIn();

			}
		}else {
			weaponIndex = (int) (((RobotBossAIBehavior)aiBehavior).getWeaponPointer()*Inventory.MAX_EQU);
			
			activeWeapon = equippedWeapons[weaponIndex];
			if(activeWeapon != null) GameController.getInstance().addDrawable(activeWeapon);
			
			switching = false;
			if(activeWeapon != null) activeWeapon.triggerSwitchingIn();

		}
	}
}
