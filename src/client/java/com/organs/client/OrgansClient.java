package com.organs.client;

import com.organs.OrganData;
import com.organs.OrganSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

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

        // The hearts are gone. The organ bar sits where they used to be.
        HudElementRegistry.replaceElement(VanillaHudElements.HEALTH_BAR, hearts -> new OrganHudElement());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                ElduinLabsCommand.register(dispatcher));
    }
}
