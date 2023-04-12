package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PokeBattle {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("pokebattle")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKEBATTLE_PERMISSION))
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("properties", PokemonPropertiesArgumentType.Companion.properties())
                                        .executes(this::execute)))
        );
    }


    private int execute(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity battlingPlayer;
        try {
            battlingPlayer = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendError(Text.of("Error finding player."));
            return 1;
        }
        PokemonProperties pokemonProperties = PokemonPropertiesArgumentType.Companion.getPokemonProperties(ctx, "properties");
        if (pokemonProperties.getSpecies() == null) {
            ctx.getSource().sendMessage(Text.literal("No pokemon found for species provided"));
            return 1;
        }

        if (BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(battlingPlayer) != null) {
            ctx.getSource().sendMessage(Text.literal("Player is in an active battle.").formatted(Formatting.RED));
            return 1;
        }

        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(battlingPlayer);
        UUID pokemonUUID = null;
        for (Pokemon pokemon : party) {
            if (!pokemon.isFainted()) {
                pokemonUUID = pokemon.getUuid();
            }
        }

        if (pokemonUUID == null) {
            ctx.getSource().sendMessage(Text.literal("Player unable to battle, no available pokemon in their party.").formatted(Formatting.RED));
            return 1;
        }

        PokemonEntity pokemonEntity = pokemonProperties.createEntity(battlingPlayer.getWorld());
        pokemonEntity.refreshPositionAndAngles(battlingPlayer.getX(), battlingPlayer.getY(), battlingPlayer.getZ(), pokemonEntity.getYaw(), pokemonEntity.getPitch());
        if (battlingPlayer.getWorld().spawnEntity(pokemonEntity)) {
            BattleBuilder.INSTANCE.pve(battlingPlayer, pokemonEntity, pokemonUUID, BattleFormat.Companion.getGEN_9_SINGLES(), false, false, Cobblemon.config.getDefaultFleeDistance(), party);
        } else {
            ctx.getSource().sendError(Text.literal("Failed to spawn Pokemon in world for player..."));
        }
        return 1;
    }

    private int respond(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            String response = ctx.getInput().split(" ")[1];
            ServerPlayerEntity player = ctx.getSource().getPlayer();
        }
        return 1;
    }
}
