package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.api.storage.pc.link.PCLink;
import com.cobblemon.mod.common.api.storage.pc.link.PCLinkManager;
import com.cobblemon.mod.common.net.messages.client.storage.pc.OpenPCPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class Showcase {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("showcase")
                        .then(literal("off")
                                .executes((ctx) -> this.toggle(ctx, false)))
                        .then(literal("on")
                                .executes((ctx) -> this.toggle(ctx, true)))
                        .executes(this::execute)
        );
    }

    private int toggle(CommandContext<ServerCommandSource> ctx, boolean enable) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            player.sendMessage(Text.of("Toggling player showcase visiblity..."));
            CobblemonExtras.INSTANCE.getShowcaseService().togglePlayerPublic(player, enable);
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            Text hoverable = Texts.join(Text.literal("HERE").getWithStyle(
                    Style.EMPTY.withUnderline(true)
                            .withColor(Formatting.AQUA)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://cobblemonextras.com/"))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to go to the Cobblemon Extras website!")))
            ), Text.of(""));
            player.sendMessage(Text.literal("Find out more about CobblemonExtras Showcase ").append(hoverable));
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }
}
