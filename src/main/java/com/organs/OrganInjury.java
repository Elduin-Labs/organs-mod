package com.organs;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;

import java.util.EnumMap;
import java.util.Map;

/**
 * Decides which organ a given kind of damage hurts. Blunt trauma rattles the brain, anything
 * that stops you breathing goes for the lungs, poisons and starvation load the liver, and
 * ordinary violence wears on the heart.
 */
public final class OrganInjury {
    /** No single hit can wreck more than this much of an organ. */
    private static final int MAX_PER_HIT = 12;

    private OrganInjury() {
    }

    public static Map<Organ, Integer> forDamage(DamageSource source, float amount) {
        Map<Organ, Integer> injuries = new EnumMap<>(Organ.class);
        int base = Math.clamp(Math.round(amount), 1, MAX_PER_HIT);

        if (source.isOf(DamageTypes.FALL)
                || source.isOf(DamageTypes.FLY_INTO_WALL)
                || source.isOf(DamageTypes.FALLING_ANVIL)
                || source.isOf(DamageTypes.FALLING_BLOCK)
                || source.isOf(DamageTypes.FALLING_STALACTITE)) {
            injuries.put(Organ.BRAIN, base);
            return injuries;
        }

        if (source.isOf(DamageTypes.DROWN)
                || source.isOf(DamageTypes.IN_WALL)
                || source.isOf(DamageTypes.IN_FIRE)
                || source.isOf(DamageTypes.ON_FIRE)
                || source.isOf(DamageTypes.LAVA)
                || source.isOf(DamageTypes.HOT_FLOOR)
                || source.isOf(DamageTypes.CAMPFIRE)) {
            injuries.put(Organ.LUNGS, base);
            return injuries;
        }

        if (source.isOf(DamageTypes.MAGIC)
                || source.isOf(DamageTypes.INDIRECT_MAGIC)
                || source.isOf(DamageTypes.WITHER)
                || source.isOf(DamageTypes.WITHER_SKULL)
                || source.isOf(DamageTypes.STARVE)
                || source.isOf(DamageTypes.DRAGON_BREATH)
                || source.isOf(DamageTypes.SONIC_BOOM)) {
            injuries.put(Organ.LIVER, base);
            return injuries;
        }

        // Explosions rattle your head and crush your chest at once.
        if (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION)) {
            injuries.put(Organ.BRAIN, Math.max(1, base / 2));
            injuries.put(Organ.LUNGS, Math.max(1, base / 2));
            return injuries;
        }

        // Everything else — mobs, players, arrows, cactus — is strain on the heart.
        injuries.put(Organ.HEART, Math.max(1, base / 2));
        return injuries;
    }
}
