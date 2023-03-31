package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

import static dev.chasem.cobblemonextras.util.PokemonUtility.getHoverText;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PokeShout {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("pokeshout")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESHOUT_PERMISSION))
                        .then(argument("slot", IntegerArgumentType.integer(1, 6)).executes(this::execute))
        );
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            Integer slot = ctx.getArgument("slot", Integer.class);
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            Pokemon pokemon = party.get(slot - 1);
            if (pokemon != null) {
                MutableText toSend = Text.literal("[").formatted(Formatting.GREEN)
                        .append(Text.literal("PokeShout").formatted(Formatting.YELLOW))
                        .append(Text.literal("] ").formatted(Formatting.GREEN))
                        .append(player.getDisplayName().copy().append(Text.of(": ")).formatted(Formatting.WHITE));
                MutableText pokemonName = pokemon.getSpecies().getTranslatedName().formatted(Formatting.GREEN).append(" ");
                toSend.append(pokemonName);
                if (pokemon.getShiny()) {
                    toSend.append(Text.literal("★ ").formatted(Formatting.GOLD));
                }
                getHoverText(toSend, pokemon);
                ctx.getSource().getServer().getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(toSend));
            } else {
                ctx.getSource().sendError(Text.literal("No Pokemon in slot."));
            }
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }



}
