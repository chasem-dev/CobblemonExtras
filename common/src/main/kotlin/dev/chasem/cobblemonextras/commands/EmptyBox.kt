package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException
import com.cobblemon.mod.common.api.storage.pc.PCPosition
import com.cobblemon.mod.common.api.storage.pc.PCStore
import com.cobblemon.mod.common.config.CobblemonConfig
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class EmptyBox {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("emptybox")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.EMPTYBOX_PERMISSION) }
                        .then(Commands.argument<Int>("box", IntegerArgumentType.integer(1, Cobblemon.config.defaultBoxCount))
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })
        )
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.getSource().getPlayer() != null) {
            val player: ServerPlayer = ctx.getSource().getPlayer()!!
            var playerPc: PCStore? = null
            try {
                playerPc = Cobblemon.storage.getPC(player)
            } catch (e: NoPokemonStoreException) {
                player.sendSystemMessage(Component.literal("Error accessing PC..."))
                return -1
            }
            val boxNum: Int = ctx.getArgument<Int>("box", Int::class.java) - 1
            val box = playerPc.boxes[boxNum]
            if (box == null) {
                player.sendSystemMessage(Component.literal("Error accessing box... $boxNum"))
                return -1
            }
            for (i in 0..29) {
                playerPc.remove(PCPosition(boxNum, i))
            }
            player.sendSystemMessage(Component.literal("Box " + (boxNum + 1) + " has been emptied."))
        } else {
            ctx.getSource().sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }
}
