package dev.chasem.cobblemonextras.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.menus.PokeSeeMenu
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.server.level.ServerPlayer

class PokeSee {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("pokesee")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESEE_PERMISSION) }
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.self(ctx) }
        )
        dispatcher.register(
                Commands.literal("pokeseeother")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESEE_OTHER_PERMISSION) }
                        .then(Commands.argument<EntitySelector>("player", EntityArgument.player())
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.other(ctx) })
        )
    }

    private fun self(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player: ServerPlayer = ctx.source.player!!
            player.openMenu(PokeSeeMenu(player))
        }
        return 1
    }

    private fun other(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player: ServerPlayer = ctx.source.player!!
            val otherPlayerName: String = ctx.input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().get(1) // ctx.getArgument("player", String.class);
            val otherPlayer: ServerPlayer? = ctx.source.server.playerList.getPlayerByName(otherPlayerName)
            if (otherPlayer != null) {
                player.openMenu(PokeSeeMenu(otherPlayer))
            }
        }
        return 1
    }
}
