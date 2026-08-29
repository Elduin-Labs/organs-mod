package com.organs;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Turns organ condition into how the player actually feels. Runs once a second per player.
 */
public final class OrganEffects {
    /** Effects are re-applied every second; a 3-second duration keeps them from flickering. */
    private static final int EFFECT_TICKS = 60;

    private OrganEffects() {
    }

    public static void apply(ServerPlayerEntity player, OrganData data, long worldTime) {
        int heart = data.get(Organ.HEART);
        int lungs = data.get(Organ.LUNGS);
        int liver = data.get(Organ.LIVER);
        int brain = data.get(Organ.BRAIN);

        // Heart — the pump falters, so you slow down and hit softer.
        if (heart < Organ.CRITICAL) {
            give(player, StatusEffects.SLOWNESS, 1);
            give(player, StatusEffects.WEAKNESS, 0);
        } else if (heart < Organ.IMPAIRED) {
            give(player, StatusEffects.SLOWNESS, 0);
        }

        // Lungs — your breath capacity is capped to whatever's left of them.
        if (lungs < Organ.IMPAIRED) {
            // Floor the cap at a quarter of normal breath. Without this, damaged lungs mean less
            // air, which means drowning sooner, which damages the lungs further — a spiral that
            // bottoms out with you unable to hold your breath at all.
            int floor = player.getMaxAir() / 4;
            int cap = Math.max(floor, player.getMaxAir() * lungs / Organ.MAX);
            if (player.getAir() > cap) {
                player.setAir(cap);
            }
            if (lungs < Organ.CRITICAL && player.isSprinting()) {
                give(player, StatusEffects.MINING_FATIGUE, 0);
            }
        }

        // Liver — you burn through food, and at the end you can't hold anything down.
        if (liver < Organ.IMPAIRED) {
            player.addExhaustion(0.08F * (Organ.IMPAIRED - liver) / (float) Organ.IMPAIRED);
            if (liver < Organ.CRITICAL) {
                give(player, StatusEffects.HUNGER, 0);
            }
        }

        // Brain — the world swims, and eventually goes dark in bursts.
        if (brain < Organ.IMPAIRED && worldTime % 200 < 60) {
            give(player, StatusEffects.NAUSEA, 0);
        }
        if (brain < Organ.CRITICAL && worldTime % 300 < 40) {
            give(player, StatusEffects.BLINDNESS, 0);
        }
    }

    private static void give(ServerPlayerEntity player, net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect, int amplifier) {
        player.addStatusEffect(new StatusEffectInstance(effect, EFFECT_TICKS, amplifier, true, false, false));
    }

    /** Nudges the player when an organ crosses into trouble, so failures aren't invisible. */
    public static void warn(ServerPlayerEntity player, Organ organ, int value) {
        Formatting color = value < Organ.CRITICAL ? Formatting.DARK_RED : Formatting.YELLOW;
        String key = value < Organ.CRITICAL ? "organs.warn.critical" : "organs.warn.impaired";
        player.sendMessage(Text.translatable(key, Text.translatable(organ.translationKey())).formatted(color), true);
    }
}
