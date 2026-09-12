package com.organs.client;

import com.organs.Organ;
import com.organs.OrganData;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * The organ bar. It takes the place of the vanilla hearts: four bars in the same row the health
 * bar used to sit in — heart, lungs, liver, brain, left to right.
 */
public class OrganHudElement implements HudElement {
    private static final int BAR_WIDTH = 18;
    private static final int BAR_HEIGHT = 9;
    private static final int GAP = 3;
    /** Matches the vanilla health bar's left edge. */
    private static final int LEFT_OFFSET = 91;
    /** The vanilla health bar's row. */
    private static final int BOTTOM_OFFSET = 39;

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
        int x = context.getScaledWindowWidth() / 2 - LEFT_OFFSET;
        int y = context.getScaledWindowHeight() - BOTTOM_OFFSET;

        Organ[] organs = Organ.values();
        for (int i = 0; i < organs.length; i++) {
            int value = data.get(organs[i]);
            int barX = x + i * (BAR_WIDTH + GAP);

            // A one-pixel dark border keeps the bars readable against bright terrain.
            context.fill(barX, y, barX + BAR_WIDTH, y + BAR_HEIGHT, BACKDROP);
            context.fill(barX + 1, y + 1, barX + BAR_WIDTH - 1, y + BAR_HEIGHT - 1, EMPTY);

            int inner = BAR_WIDTH - 2;
            int filled = Math.round(value / (float) Organ.MAX * inner);
            if (filled > 0) {
                context.fill(barX + 1, y + 1, barX + 1 + filled, y + BAR_HEIGHT - 1, color(i, value));
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
