package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.UI.FloatingText;
import ma.ac.emi.UI.FloatingTextManager;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;

public class DamageBehavior implements ProjectileBehavior {

    private boolean destroyOnHit;

    public DamageBehavior(boolean destroyOnHit) {
        this.destroyOnHit = destroyOnHit;
    }

    @Override
    public void onHit(Projectile p, LivingEntity entity) {
        if (entity == null) return;
        if (entity.isInvincible()) return;

        // ── Dodge check — only the player can dodge ───────────────────────
        if (entity instanceof Player player) {
            if (rollDodge(player)) {
                FloatingTextManager.getInstance().spawn(
                        "DODGED!", FloatingText.Preset.DODGED, entity.getPos());
                player.getInventory().getEffectContext().fireOnDodge(player);
                return; // attack does nothing
            }
        }

        WeaponItemDefinition definition =
                (WeaponItemDefinition) p.getWeapon().getWeaponItem().getItemDefinition();

        double damage = definition.getDamage();
        damage *= p.getWeapon().getBearer().getStrength();
        if (p.getWeapon().getBearer() instanceof Player player && player.getConfig() != null)
            damage = Math.max(player.getConfig().getCaps().minDamage, damage);
        entity.takeDamage(damage, p.getWeapon().getBearer());
        System.out.println("Target hit, damage: " + damage + ", remaining hp: " + entity.getHp());

        double knockback = definition.getKnockbackForce();
        if (knockback != 0) {
            Vector3D kbDir = p.getVelocity().normalize();
            entity.applyKnockback(kbDir.mult(knockback));
        }
    }

   
    private boolean rollDodge(Player player) {
        double maxDodge = player.getConfig() != null ? player.getConfig().getCaps().maxDodge : 0.75;
        double dodge = Math.max(0, Math.min(maxDodge, player.getDodge()));
        return dodge > 0 && Math.random() < dodge;
    }

    @Override public void onUpdate(Projectile p, double step) {}
    @Override public void onInit(Projectile p) {}
    @Override public void onDesactivate(Projectile p) {}
    @Override public void onHit(Projectile p, Obstacle obstacle) {}
}