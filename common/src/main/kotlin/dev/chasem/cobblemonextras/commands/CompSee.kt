package dev.chasem.cobblemonextras.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.menus.CompSeeMenu
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument

class CompSee {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val mainSelf = dispatcher.register(
                Commands.literal("compsee")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.COMPSEE_PERMISSION) }
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.self(ctx) }
        )
        val mainOther = dispatcher.register(
                Commands.literal("compseeother")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.COMPESEE_OTHER_PERMISSION) }
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.other(ctx) }))
    }

    private fun self(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player = ctx.source.player
            player!!.openMenu(CompSeeMenu(player, 0))
        }
        return 1
    }

    private fun other(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player = ctx.source.player
            val otherPlayerName = ctx.input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] // ctx.getArgument("player", String.class);
            val otherPlayer = ctx.source.server.playerList.getPlayerByName(otherPlayerName)
            if (otherPlayer != null) {
                player!!.openMenu(CompSeeMenu(otherPlayer, 0))
            }
        }
        return 1
    }
}
