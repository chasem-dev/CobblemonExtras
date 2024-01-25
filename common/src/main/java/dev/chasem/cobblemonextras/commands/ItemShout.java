package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

import static dev.chasem.cobblemonextras.util.PokemonUtility.getHoverText;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ItemShout {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("itemshout")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.ITEMSHOUT_PERMISSION))
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            ItemStack heldItem = player.getInventory().getMainHandStack();
            HoverEvent.ItemStackContent itemContent = new HoverEvent.ItemStackContent(heldItem);
            MutableText toSend = Text.literal("[").formatted(Formatting.YELLOW)
                    .append(Text.literal("ItemShout").formatted(Formatting.GREEN))
                    .append(Text.literal("] ").formatted(Formatting.YELLOW))
                    .append(player.getDisplayName().copy().append(Text.of(": ")).formatted(Formatting.WHITE));

            Text itemName = heldItem.getName();
            Text hoverAbleName = Texts.join(itemName.getWithStyle(itemName.getStyle()
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemContent))
                    .withUnderline(true)),
                Text.of("")
            );
            toSend.append(hoverAbleName);
            ctx.getSource().getServer().getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(toSend));
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }


}
