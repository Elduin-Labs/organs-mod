package com.organs.client;

import com.organs.Organ;
import com.organs.OrganData;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Four small bars sitting just above the health bar — heart, lungs, liver, brain, left to right.
 * They only show when something is actually wrong, so a healthy player's HUD stays vanilla.
 */
public class OrganHudElement implements HudElement {
    private static final int BAR_WIDTH = 20;
    private static final int BAR_HEIGHT = 3;
    private static final int GAP = 2;
    /** Matches the vanilla health bar's left edge. */
    private static final int LEFT_OFFSET = 91;
    /** Sits one row above the health bar. */
    private static final int BOTTOM_OFFSET = 49;

    private static final int BACKDROP = 0xB0000000;
    private static final int EMPTY = 0xFF3A3A3A;

    private static final int[] COLORS = {
            0xFFE74C3C, // heart  — red
            0xFF5DADE2, // lungs  — blue
            0xFFE1A93A, // liver  — amber
            0xFFC77DD6, // brain  — violet
    };

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        if (client.player.isCreative() || client.player.isSpectator()) {
            return;
        }

        OrganData data = OrgansClient.current();
        if (data.isFullyHealthy()) {
            return; // Nothing wrong — don't clutter the screen.
        }

        int x = context.getScaledWindowWidth() / 2 - LEFT_OFFSET;
        int y = context.getScaledWindowHeight() - BOTTOM_OFFSET;

        Organ[] organs = Organ.values();
        for (int i = 0; i < organs.length; i++) {
            int value = data.get(organs[i]);
            int barX = x + i * (BAR_WIDTH + GAP);

            // A one-pixel dark border keeps the bars readable against bright terrain.
            context.fill(barX - 1, y - 1, barX + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, BACKDROP);
            context.fill(barX, y, barX + BAR_WIDTH, y + BAR_HEIGHT, EMPTY);

            int filled = Math.round(value / (float) Organ.MAX * BAR_WIDTH);
            if (filled > 0) {
                context.fill(barX, y, barX + filled, y + BAR_HEIGHT, color(i, value));
            }
        }
    }

    /** Failing organs pulse so you notice them without having to read the bar. */
    private static int color(int index, int value) {
        int base = COLORS[index];
        if (value < Organ.CRITICAL && (System.currentTimeMillis() / 400) % 2 == 0) {
            return 0xFFFFFFFF;
        }
        return base;
    }
}
