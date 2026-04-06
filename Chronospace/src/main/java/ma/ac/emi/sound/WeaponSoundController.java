package ma.ac.emi.sound;

import ma.ac.emi.gamecontrol.GameController;

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * WeaponSoundController
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * Encapsulates all weapon-sound logic so AttackStrategy stays clean.
 *
 * Three firing modes are supported:
 *
 *  1. ONE-SHOT  – Standard weapons (sword, RPG, shotgun, sniper …).
 *                 Plays the clip once per attack; pitch is adjusted when
 *                 attack-speed exceeds the clip duration.
 *
 *  2. RAPID-FIRE – High-cadence automatic weapons (AK-47, boss machinegun).
 *                 Uses a per-sound clip pool so every bullet gets its own
 *                 playback slot; no sample is ever dropped.
 *
 *  3. LOOPED    – Sustained-fire weapons (flamethrower).
 *                 Starts a seamless loop on fire-begin and stops it on
 *                 fire-end; an optional tail/click sound is played on release.
 *
 * Usage in AttackStrategy
 * ───────────────────────
 *   // at the top of execute():
 *   WeaponSoundController.playAttackSound(definition, weapon);
 *
 *   // when the weapon stops firing (e.g. in a "stopFiring" callback):
 *   WeaponSoundController.stopLoopedAttackSound(definition);
 */
public final class WeaponSoundController {

    // IDs of weapons whose firing sounds should be seamless loops
    private static final java.util.Set<String> LOOPED_WEAPONS = java.util.Set.of(

    );

    // IDs of weapons that fire fast enough to warrant the rapid-fire path
    // (attack speed > ~6 shots/sec).  Everything else uses the one-shot path.
    private static final double RAPID_FIRE_THRESHOLD = 6.0;

    private WeaponSoundController() {}

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Call once per attack tick from AttackStrategy.execute().
     *
     * @param weaponId    the weapon's id field from JSON (e.g. "ak47")
     * @param soundId     the attackSound field from JSON (e.g. "ak47")
     * @param attackSpeed shots per second from WeaponItemDefinition
     */
    public static void playAttackSound(String weaponId, String soundId, double attackSpeed) {
        if (soundId == null || soundId.isEmpty()) return;

        SoundManager sm = GameController.getInstance().getSoundManager();
        if (sm == null) return;

        // ── Looped path ───────────────────────────────────────────────────────
        if (LOOPED_WEAPONS.contains(weaponId)) {
            sm.playLooped(soundId);
            return;
        }

        // ── Rapid-fire path ───────────────────────────────────────────────────
        if (attackSpeed >= RAPID_FIRE_THRESHOLD) {
            // No pitch adjustment needed; the pool handles concurrency.
            sm.play(soundId, false);   // false = don't restart mid-shot
            return;
        }

        // ── One-shot path  (+ optional pitch shift) ───────────────────────────
        double timeBetweenAttacks = attackSpeed > 0 ? 1.0 / attackSpeed : Double.MAX_VALUE;
        double soundDuration      = sm.getDuration(soundId);

        float speedFactor = 1.0f;
        if (soundDuration > 0 && soundDuration > timeBetweenAttacks) {
            speedFactor = (float) (soundDuration / timeBetweenAttacks);
            // Cap at 3× to avoid chipmunk artefacts on very slow reload weapons
            speedFactor = Math.min(speedFactor, 3.0f);
        }

        sm.play(soundId, true, speedFactor);
    }

    /**
     * Call when a looped weapon stops firing (e.g. fire button released,
     * magazine empty, weapon switched out).
     *
     * @param weaponId the weapon's id field from JSON
     * @param soundId  the attackSound field
     */
    public static void stopLoopedAttackSound(String weaponId, String soundId) {
        if (!LOOPED_WEAPONS.contains(weaponId)) return;
        if (soundId == null || soundId.isEmpty()) return;

        SoundManager sm = GameController.getInstance().getSoundManager();
        if (sm != null) sm.stopLoop(soundId);
    }

    /**
     * Convenience – stops any looped sound regardless of weapon, used on
     * weapon switch-out to guarantee no orphaned loops.
     */
    public static void forceStopLoopedSound(String soundId) {
        if (soundId == null || soundId.isEmpty()) return;
        SoundManager sm = GameController.getInstance().getSoundManager();
        if (sm != null) sm.stopLoop(soundId);
    }
}