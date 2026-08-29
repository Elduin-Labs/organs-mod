package com.organs.client;

import com.organs.OrganData;
import com.organs.OrganSyncPayload;
import com.organs.OrgansMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.util.Identifier;

public class OrgansClient implements ClientModInitializer {
    /** Latest state pushed by the server. Starts healthy so the bars aren't empty before the first sync. */
    private static volatile OrganData current = OrganData.healthy();

    public static OrganData current() {
        return current;
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(OrganSyncPayload.ID, (payload, context) ->
                context.client().execute(() -> current = payload.data()));

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.AIR_BAR,
                Identifier.of(OrgansMod.MOD_ID, "organ_bars"),
                new OrganHudElement());
    }
}
