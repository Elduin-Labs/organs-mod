package com.organs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class OrgansMod implements ModInitializer {
    public static final String MOD_ID = "organs";

    /** How often the per-player organ tick runs. */
    private static final int TICK_INTERVAL = 20;
    /** Organs only start knitting back together this long after your last injury. */
    private static final int REST_TICKS_BEFORE_HEALING = 200;
    /** One point of natural healing per this many seconds, while well fed. */
    private static final int SECONDS_PER_HEAL = 15;
    /** Regeneration turns that into one point per this many seconds. */
    private static final int SECONDS_PER_HEAL_REGENERATING = 3;
    private static final int WELL_FED = 16;

    public static final AttachmentType<OrganData> ORGANS = AttachmentRegistry.<OrganData>builder()
            .initializer(OrganData::healthy)
            .persistent(OrganData.CODEC)
            .buildAndRegister(Identifier.of(MOD_ID, "organs"));

    /** Last tick each player was hurt, so resting can be rewarded. Not persisted — it's fine to forget. */
    private static final Map<java.util.UUID, Long> LAST_INJURY = new java.util.HashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(OrganSyncPayload.ID, OrganSyncPayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sync(handler.player));

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, dealt, taken, blocked) -> {
            if (!(entity instanceof ServerPlayerEntity player) || taken <= 0.0F) {
                return;
            }
            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            OrganData before = player.getAttachedOrCreate(ORGANS);
            OrganData after = before;
            for (Map.Entry<Organ, Integer> injury : OrganInjury.forDamage(source, taken).entrySet()) {
                after = after.injure(injury.getKey(), injury.getValue());
            }

            if (!after.equals(before)) {
                player.setAttached(ORGANS, after);
                LAST_INJURY.put(player.getUuid(), player.getEntityWorld().getTime());
                announceCrossings(player, before, after);
                sync(player);
            }
        });

        // A fresh body on respawn — organ damage shouldn't outlive you.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                newPlayer.setAttached(ORGANS, OrganData.healthy());
                LAST_INJURY.remove(newPlayer.getUuid());
            }
            sync(newPlayer);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % TICK_INTERVAL != 0) {
                return;
            }
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tickPlayer(player);
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                OrgansCommand.register(dispatcher));
    }

    private static void tickPlayer(ServerPlayerEntity player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        OrganData data = player.getAttachedOrCreate(ORGANS);
        long time = player.getEntityWorld().getTime();

        OrganEffects.apply(player, data, time);

        if (data.isFullyHealthy()) {
            return;
        }

        long lastInjury = LAST_INJURY.getOrDefault(player.getUuid(), 0L);
        if (time - lastInjury < REST_TICKS_BEFORE_HEALING) {
            return;
        }

        boolean regenerating = player.hasStatusEffect(StatusEffects.REGENERATION);
        int seconds = regenerating ? SECONDS_PER_HEAL_REGENERATING : SECONDS_PER_HEAL;
        boolean fed = player.getHungerManager().getFoodLevel() >= WELL_FED;

        // Healing costs energy: you mend on a full stomach, or with a regeneration potion.
        if ((fed || regenerating) && (time / TICK_INTERVAL) % seconds == 0) {
            OrganData healed = data.mend(1);
            if (!healed.equals(data)) {
                player.setAttached(ORGANS, healed);
                sync(player);
            }
        }
    }

    /** Pushes a player's organ state to their own client for the HUD. No-op for vanilla clients. */
    public static void sync(ServerPlayerEntity player) {
        if (ServerPlayNetworking.canSend(player, OrganSyncPayload.ID)) {
            ServerPlayNetworking.send(player, new OrganSyncPayload(player.getAttachedOrCreate(ORGANS)));
        }
    }

    /** Only speaks up when an organ actually crosses a threshold, not on every scratch. */
    private static void announceCrossings(ServerPlayerEntity player, OrganData before, OrganData after) {
        for (Organ organ : Organ.values()) {
            int was = before.get(organ);
            int now = after.get(organ);
            boolean crossedCritical = was >= Organ.CRITICAL && now < Organ.CRITICAL;
            boolean crossedImpaired = was >= Organ.IMPAIRED && now < Organ.IMPAIRED;
            if (crossedCritical || crossedImpaired) {
                OrganEffects.warn(player, organ, now);
            }
        }
    }
}
