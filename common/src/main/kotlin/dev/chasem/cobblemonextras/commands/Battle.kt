package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.battles.BattleFormat
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.battles.ChallengeManager
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import dev.chasem.cobblemonextras.services.PVPChallengeService
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer

class Battle {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                literal("battle")
                        .requires { src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.BATTLE_PERMISSION) }
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })
                        .then(Commands.literal("deny")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes { ctx: CommandContext<CommandSourceStack> -> this.deny(ctx) }))
        )
    }
    private fun deny(ctx: CommandContext<CommandSourceStack>): Int {
        val player: ServerPlayer = ctx.source.playerOrException;
        val battlePartner: ServerPlayer = EntityArgument.getPlayer(ctx, "player")
        val request = PVPChallengeService.getInboundRequestBySender(player.uuid, battlePartner.uuid)
        if (request != null) {
            PVPChallengeService.declineRequest(player, request.requestID)
        }
        return 1;
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val player: ServerPlayer = ctx.source.playerOrException;
        val battlePartner: ServerPlayer = EntityArgument.getPlayer(ctx, "player")

        if (battlePartner.uuid.equals(player.uuid)) {
            ctx.getSource().sendFailure(Component.literal("Life's tough enough, don't battle yourself."))
            return 1
        }

        if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
            player.sendSystemMessage(Component.literal("You can't start a new battle, while in a battle.").withStyle(ChatFormatting.RED))
            return 1
        }

        if (BattleRegistry.getBattleByParticipatingPlayer(battlePartner) != null) {
            player.sendSystemMessage(Component.literal("Opponent is currently in a battle.").withStyle(ChatFormatting.RED))
            return 1
        }

        val storage1 = Cobblemon.storage.getParty(player);
        val firstAvailablePokemon: Pokemon? = storage1.firstOrNull() {
            !it.isFainted()
        }

        if (firstAvailablePokemon == null) {
            ctx.source.sendFailure(Component.literal("You don't have any available Pokemon to battle with."))
            return 1
        }

        val challenge = ChallengeManager.SinglesBattleChallenge(player, battlePartner, firstAvailablePokemon!!.uuid, BattleFormat.GEN_9_SINGLES)

        val request = PVPChallengeService.getInboundRequestBySender(player.uuid, battlePartner.uuid)
        if (request != null) {
            PVPChallengeService.acceptRequest(player, request.requestID)
            return 1
        }
        PVPChallengeService.sendRequest(challenge)

        val accept = Component.literal("[ACCEPT]")
                .withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN)
                        .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/battle ${player.name.string}"))
                        .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Accept ${player.name.string} Challenge"))));

        val deny = Component.literal("[DENY]")
                .withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.RED)
                        .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/battle deny ${player.name.string}"))
                        .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Deny ${player.name.string} Challenge"))));
        battlePartner.sendSystemMessage(accept.copy().append(" ").append(deny))
        return 1;
    }


}