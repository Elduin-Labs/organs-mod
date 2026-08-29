package com.organs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;

/**
 * /organs                      — your own readout
 * /organs <player>             — someone else's
 * /organs set <organ> <0-100>  — operators, for testing
 * /organs heal                 — operators, back to full
 */
public final class OrgansCommand {
    private static final int BAR_SEGMENTS = 20;

    private OrgansCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("organs")
                .executes(ctx -> report(ctx.getSource(), ctx.getSource().getPlayerOrThrow()))
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(ctx -> report(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target"))))
                .then(CommandManager.literal("heal")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            player.setAttached(OrgansMod.ORGANS, OrganData.healthy());
                            OrgansMod.sync(player);
                            ctx.getSource().sendFeedback(
                                    () -> Text.translatable("organs.command.healed").formatted(Formatting.GREEN), false);
                            return 1;
                        }))
                .then(CommandManager.literal("set")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("organ", StringArgumentType.word())
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(
                                        Arrays.stream(Organ.values()).map(Organ::id), builder))
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, Organ.MAX))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                            String name = StringArgumentType.getString(ctx, "organ");
                                            Organ organ = Organ.byId(name);
                                            if (organ == null) {
                                                ctx.getSource().sendError(
                                                        Text.translatable("organs.command.unknown", name));
                                                return 0;
                                            }
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            player.setAttached(OrgansMod.ORGANS,
                                                    player.getAttachedOrCreate(OrgansMod.ORGANS).with(organ, value));
                                            OrgansMod.sync(player);
                                            return report(ctx.getSource(), player);
                                        })))));
    }

    private static int report(ServerCommandSource source, ServerPlayerEntity player) {
        OrganData data = player.getAttachedOrCreate(OrgansMod.ORGANS);

        source.sendFeedback(() -> Text.translatable("organs.command.header", player.getDisplayName())
                .formatted(Formatting.GRAY), false);

        for (Organ organ : Organ.values()) {
            int value = data.get(organ);
            source.sendFeedback(() -> Text.literal(" ")
                    .append(Text.translatable(organ.translationKey()).formatted(organ.color()))
                    .append(Text.literal(" ").formatted(Formatting.GRAY))
                    .append(bar(value))
                    .append(Text.literal(" " + value + "%").formatted(statusColor(value)))
                    .append(status(value)), false);
        }
        return data.get(Organ.HEART);
    }

    private static Text bar(int value) {
        int filled = Math.round(value / (float) Organ.MAX * BAR_SEGMENTS);
        return Text.literal("|".repeat(filled)).formatted(statusColor(value))
                .append(Text.literal("|".repeat(BAR_SEGMENTS - filled)).formatted(Formatting.DARK_GRAY));
    }

    private static Text status(int value) {
        if (value < Organ.CRITICAL) {
            return Text.literal(" ").append(Text.translatable("organs.status.critical").formatted(Formatting.DARK_RED));
        }
        if (value < Organ.IMPAIRED) {
            return Text.literal(" ").append(Text.translatable("organs.status.impaired").formatted(Formatting.YELLOW));
        }
        return Text.empty();
    }

    private static Formatting statusColor(int value) {
        if (value < Organ.CRITICAL) {
            return Formatting.DARK_RED;
        }
        if (value < Organ.IMPAIRED) {
            return Formatting.YELLOW;
        }
        return Formatting.GREEN;
    }
}
