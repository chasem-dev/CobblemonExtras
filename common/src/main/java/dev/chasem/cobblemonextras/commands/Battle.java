package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.UUID;

import static com.cobblemon.mod.common.util.LocalizationUtilsKt.lang;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Battle {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("battle")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.BATTLE_PERMISSION))
                        .then(argument("player", EntityArgumentType.player())
                                .executes(this::execute))
        );
    }


    private int execute(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();

            ServerPlayerEntity battlePartner;
            try {
                battlePartner = EntityArgumentType.getPlayer(ctx, "player");
            } catch (CommandSyntaxException e) {
                ctx.getSource().sendError(Text.of("Error finding player."));
                return 1;
            }

            if (battlePartner.getUuid().equals(player.getUuid())) {
                ctx.getSource().sendError(Text.of("Life's tough enough, don't battle yourself."));
                return 1;
            }

            if (BattleRegistry.INSTANCE.getBattle(player.getUuid()) != null) {
                player.sendMessage(Text.literal("You can't start a new battle, while in a battle.").formatted(Formatting.RED));
                return 1;
            }

            if (BattleRegistry.INSTANCE.getBattle(battlePartner.getUuid()) != null) {
                player.sendMessage(Text.literal("Opponent is currently in a battle.").formatted(Formatting.RED));
                return 1;
            }


            BattleRegistry.BattleChallenge opponentExistingChallenge = BattleRegistry.INSTANCE.getPvpChallenges().get(battlePartner.getUuid());
            // Check in on battle requests, if the opponent has challenged me previously, start the battle
            if (opponentExistingChallenge != null && !opponentExistingChallenge.isExpired() && opponentExistingChallenge.getChallengedPlayerUUID() == player.getUuid()) {

                Pokemon playerLeadingPokemon = null;
                for (Pokemon pokemon : Cobblemon.INSTANCE.getStorage().getParty(player)) {
                    if (!pokemon.isFainted()) {
                        playerLeadingPokemon = pokemon;
                        break;
                    }
                }

                if (playerLeadingPokemon == null) {
                    battlePartner.sendMessage(Text.literal(player.getEntityName()).formatted(Formatting.YELLOW).append(Text.literal(" has no available Pokemon to battle.").formatted(Formatting.RED)));
                    player.sendMessage(Text.literal("No available Pokemon to battle.").formatted(Formatting.RED));
                    BattleRegistry.INSTANCE.removeChallenge(player.getUuid(), battlePartner.getUuid());
                    return 1;
                }

                Pokemon partnerLeadingPokemon = null;
                for (Pokemon pokemon : Cobblemon.INSTANCE.getStorage().getParty(battlePartner)) {
                    if (!pokemon.isFainted()) {
                        partnerLeadingPokemon = pokemon;
                        break;
                    }
                }

                if (partnerLeadingPokemon == null) {
                    player.sendMessage(Text.literal(battlePartner.getEntityName()).formatted(Formatting.YELLOW).append(Text.literal(" has no available Pokemon to battle.").formatted(Formatting.RED)));
                    battlePartner.sendMessage(Text.literal("No available Pokemon to battle.").formatted(Formatting.RED));
                    BattleRegistry.INSTANCE.removeChallenge(player.getUuid(), battlePartner.getUuid());
                    return 1;
                }

                BattleStartResult result = BattleBuilder.INSTANCE.pvp1v1(player, battlePartner, playerLeadingPokemon.getUuid(), partnerLeadingPokemon.getUuid());
                BattleRegistry.INSTANCE.removeChallenge(player.getUuid(), battlePartner.getUuid());
                BattleRegistry.INSTANCE.getPvpChallenges().remove(battlePartner.getUuid());
                BattleRegistry.INSTANCE.getPvpChallenges().remove(player.getUuid());
            } else {

                Pokemon playerLeadingPokemon = null;
                for (Pokemon pokemon : Cobblemon.INSTANCE.getStorage().getParty(player)) {
                    if (!pokemon.isFainted()) {
                        playerLeadingPokemon = pokemon;
                        break;
                    }
                }

                if (playerLeadingPokemon == null) {
                    player.sendMessage(Text.literal("You have no available Pokemon to battle.").formatted(Formatting.RED));
                    return 1;
                }

                BattleRegistry.BattleChallenge challenge = new BattleRegistry.BattleChallenge(UUID.randomUUID(), battlePartner.getUuid(), playerLeadingPokemon.getUuid(), 60);
                BattleRegistry.INSTANCE.getPvpChallenges().put(player.getUuid(), challenge);
                Text accept = Texts.join(Text.literal("[ACCEPT]")
                        .getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/battle " + player.getEntityName()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Accept Battle")))
                        ), Text.of(""));

                battlePartner.sendMessage(player.getDisplayName().copy().formatted(Formatting.YELLOW).append(Text.literal(" has challenged you to a battle.").formatted(Formatting.WHITE)));
                battlePartner.sendMessage(Text.literal("Click ").formatted(Formatting.WHITE).append(accept).append(Text.literal(" to start a battle.").formatted(Formatting.WHITE)));
                player.sendMessage(lang("challenge.sender", battlePartner.getDisplayName().copy().formatted(Formatting.YELLOW)));
            }

        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
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
