package com.organs.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.net.URI;

/**
 * /Elduin-Labs — opens the Elduin-Labs GitHub page in your browser.
 * It runs on your own game, so it works on any server.
 */
public final class ElduinLabsCommand {
    private static final URI GITHUB = URI.create("https://github.com/Elduin-Labs");

    private ElduinLabsCommand() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("Elduin-Labs").executes(ctx -> open(ctx.getSource())));
        // Commands care about capital letters, so the lower-case spelling works too.
        dispatcher.register(ClientCommandManager.literal("elduin-labs").executes(ctx -> open(ctx.getSource())));
    }

    private static int open(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("Elduin-Labs").formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal(" - made by Elduin").formatted(Formatting.YELLOW)));
        // A clickable link too, in case the browser doesn't pop up by itself.
        source.sendFeedback(Text.literal(GITHUB.toString()).styled(style -> style
                .withColor(Formatting.AQUA)
                .withUnderline(true)
                .withClickEvent(new ClickEvent.OpenUrl(GITHUB))));
        Util.getOperatingSystem().open(GITHUB);
        return 1;
    }
}
