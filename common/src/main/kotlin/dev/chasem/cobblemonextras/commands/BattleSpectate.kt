package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.battles.BattleFormat
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.battles.ChallengeManager
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.net.messages.client.battle.BattleInitializePacket
import com.cobblemon.mod.common.net.messages.client.battle.BattleMessagePacket
import com.cobblemon.mod.common.net.messages.client.battle.BattleMusicPacket
import com.cobblemon.mod.common.net.serverhandling.battle.SpectateBattleHandler
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

class BattleSpectate {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                literal("battlespectate")
                        .requires { src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.BATTLE_PERMISSION) }
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })
        )
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val player: ServerPlayer = ctx.source.playerOrException;
        val playerToSpectate: ServerPlayer = EntityArgument.getPlayer(ctx, "player")

        if (playerToSpectate.uuid.equals(player.uuid)) {
            ctx.getSource().sendFailure(Component.literal("Unable to spectate your own battle."))
            return 1
        }

        val battle = BattleRegistry.getBattleByParticipatingPlayer(playerToSpectate);
        if (battle == null) {
            player.sendSystemMessage(Component.literal("${playerToSpectate.name} is not in a battle, failed to spectated.").withStyle(ChatFormatting.RED))
            return 1
        }

        battle.spectators.add(player.uuid)
        player.sendPacket(BattleInitializePacket(battle, null))
        player.sendPacket(BattleMessagePacket(battle.chatLog))

        val targettedBattleActor = battle.actors.filterIsInstance<PlayerBattleActor>().firstOrNull { it.uuid == playerToSpectate.uuid}
        targettedBattleActor?.battleTheme?.let { player.sendPacket(BattleMusicPacket(it)) }
        return 1;
    }


}