package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.net.messages.client.storage.pc.SetPCBoxPokemonPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static dev.chasem.cobblemonextras.util.PokemonUtility.getHoverText;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PCDelete {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> main = dispatcher.register(
                literal("comptake")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.COMPTAKE_PERMISSION))
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("box", IntegerArgumentType.integer(1, Cobblemon.config.getDefaultBoxCount()))
                                        .then(argument("slot", IntegerArgumentType.integer(1, 30))
                                                .executes(this::other)))));
    }

    private int other(CommandContext<ServerCommandSource> ctx) {
        String otherPlayerName = ctx.getInput().split(" ")[1]; // ctx.getArgument("player", String.class);
        ServerPlayerEntity otherPlayer = ctx.getSource().getServer().getPlayerManager().getPlayer(otherPlayerName);
        if (otherPlayer != null) {
            Integer boxNum = ctx.getArgument("box", Integer.class) - 1;
            Integer slotNum = ctx.getArgument("slot", Integer.class) - 1;
            try {
                PCStore pcStore = Cobblemon.INSTANCE.getStorage().getPC(otherPlayer.getUuid());
                if (boxNum < pcStore.getBoxes().size()) {
                    PCBox box = pcStore.getBoxes().get(boxNum);
                    Pokemon pokemon = box.get(slotNum);
                    if (pokemon != null) {
                        Text text = getHoverText(Text.literal("Deleted: ").setStyle(Style.EMPTY.withBold(true)), pokemon);
                        ctx.getSource().sendMessage(text);
                    } else {
                        ctx.getSource().sendMessage(Text.literal("No Pokemon found in slot.").formatted(Formatting.RED));
                    }
                    box.set(slotNum, null);
                    new SetPCBoxPokemonPacket(box).sendToPlayer(otherPlayer);
                }
            } catch (NoPokemonStoreException e) {
                throw new RuntimeException(e);
            }

        }

        return 1;
    }

}
