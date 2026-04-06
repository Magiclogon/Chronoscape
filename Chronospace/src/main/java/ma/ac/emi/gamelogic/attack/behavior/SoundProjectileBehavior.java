package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.sound.WeaponSoundController;
import ma.ac.emi.world.Obstacle;

public class SoundProjectileBehavior implements ProjectileBehavior {
    private final String soundId;

    public SoundProjectileBehavior(String soundId) {
        this.soundId = soundId;
    }

    @Override
    public void onInit(Projectile p) {}

    @Override
    public void onUpdate(Projectile p, double step) {}

    @Override
    public void onHit(Projectile p, LivingEntity entity) {}

    @Override
    public void onHit(Projectile p, Obstacle obstacle) {}

    @Override
    public void onDesactivate(Projectile p) {
        WeaponSoundController.playWeaponSound(soundId);
    }
}
