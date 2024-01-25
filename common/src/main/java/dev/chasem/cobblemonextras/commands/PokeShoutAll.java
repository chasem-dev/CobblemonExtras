package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static dev.chasem.cobblemonextras.util.PokemonUtility.getHoverText;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PokeShoutAll {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("pokeshoutall")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESHOUT_ALL_PERMISSION))
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            MutableText toSend = Text.literal("[").formatted(Formatting.GREEN)
                    .append(Text.literal("PokeShoutAll").formatted(Formatting.YELLOW))
                    .append(Text.literal("] ").formatted(Formatting.GREEN))
                    .append(player.getDisplayName().copy().append(Text.of(": ")).formatted(Formatting.WHITE));
            toSend.append(("\n"));
            for (int i = 0; i < 6; i++) {
                Pokemon pokemon = party.get(i);
                if (pokemon != null) {
                    toSend.append("    " + (i+1) + ": ");
                    MutableText pokemonName = pokemon.getSpecies().getTranslatedName().formatted(Formatting.GREEN).append(" ");
                    toSend.append(pokemonName);
                    if (pokemon.getShiny()) {
                        toSend.append(Text.literal("★ ").formatted(Formatting.GOLD));
                    }
                    getHoverText(toSend, pokemon);
                    if (i != 5) {
                        toSend.append(("\n"));
                    }
                } else {
                    toSend.append(Text.literal("Empty").formatted(Formatting.RED));
                }
            }
            ctx.getSource().getServer().getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(toSend));
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }


}
