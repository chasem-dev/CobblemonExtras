package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
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

public class PokeOdds {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register /pokeodds to get the current shiny rate no permission required, and /pokeodds setRate <rate> to set the shiny rate, requires permission
        dispatcher.register(literal("pokeodds")
                .then(literal("setRate")
                        .requires(source -> CobblemonExtrasPermissions.checkPermission(source, CobblemonExtras.permissions.POKEODDS_PERMISSION))
                        .then(argument("rate", FloatArgumentType.floatArg(0.0F, 100.0F))
                                .executes(ctx -> setRate(ctx, FloatArgumentType.getFloat(ctx, "rate")))))
                .executes(this::execute));
    }

    private int setRate(CommandContext<ServerCommandSource> ctx, float rate) {
        ctx.getSource().sendMessage(Text.literal("The shiny rate has been set to: ").formatted(Formatting.GOLD)
                .append(Text.literal(String.valueOf(rate)).formatted(Formatting.AQUA)));
        Cobblemon.config.setShinyRate(rate);
        return 1;
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendMessage(Text.literal("The current shiny rate is: ").formatted(Formatting.GOLD)
                .append(Text.literal(String.valueOf(Cobblemon.config.getShinyRate())).formatted(Formatting.AQUA)));
        return 1;
    }



}
