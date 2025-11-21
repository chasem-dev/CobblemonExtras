package dev.chasem.cobblemonextras.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer

class PlayerGames {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("playergames")
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }
        )
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.getSource().getPlayer() != null) {
            val player: ServerPlayer = ctx.getSource().getPlayer()!!
            val hoverable = Component.literal("PLAYER GAMES").withStyle(
                    Style.EMPTY.withUnderlined(true)
                            .withColor(ChatFormatting.LIGHT_PURPLE)
                            .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.player.games/en-US/creator-hub"))
                            .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to go to Player.Games!")))
            )
            player.sendSystemMessage(Component.literal("Create your own Minecraft mod with ").append(hoverable))
        } else {
            ctx.getSource().sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }
}
