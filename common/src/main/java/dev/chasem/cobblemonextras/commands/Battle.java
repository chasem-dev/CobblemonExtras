package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleRegistry;
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

import static com.cobblemon.mod.common.util.LocalizationUtilsKt.lang;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Battle {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("battle")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.COMPESEE_OTHER_PERMISSION))
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

            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);

            // Check in on battle requests, if the other player has challenged me, this starts the battle
            BattleRegistry.BattleChallenge existingChallenge = BattleRegistry.INSTANCE.getPvpChallenges().get(battlePartner.getUuid());
            if (existingChallenge != null && !existingChallenge.isExpired()) {
                BattleBuilder.INSTANCE.pvp1v1(player, battlePartner, BattleFormat.Companion.getGEN_9_SINGLES(),
                        false, false, (serverPlayerEntity) -> Cobblemon.INSTANCE.getStorage().getParty(serverPlayerEntity));
                BattleRegistry.INSTANCE.getPvpChallenges().remove(battlePartner.getUuid());
            } else {
                BattleRegistry.BattleChallenge challenge = new BattleRegistry.BattleChallenge(battlePartner.getUuid(), 30);
                BattleRegistry.INSTANCE.getPvpChallenges().put(player.getUuid(), challenge);

                // TODO EXPIRE AFTER 30 seconds.
                // BattleRegistry.pvpChallenges.remove(player.uuid, challenge)

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
