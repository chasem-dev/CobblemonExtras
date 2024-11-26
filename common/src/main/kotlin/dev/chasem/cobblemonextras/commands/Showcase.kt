package dev.chasem.cobblemonextras.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer

class Showcase {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("showcase")
                        .then(Commands.literal("off")
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.toggle(ctx, false) })
                        .then(Commands.literal("on")
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.toggle(ctx, true) })
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }
        )
    }

    private fun toggle(ctx: CommandContext<CommandSourceStack>, enable: Boolean): Int {
        if (ctx.getSource().getPlayer() != null) {
            val player: ServerPlayer = ctx.getSource().getPlayer()!!
            player.sendSystemMessage(Component.literal("Toggling player showcase visiblity..."))
            CobblemonExtras.showcaseService.togglePlayerPublic(player, enable)
        } else {
            ctx.getSource().sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.getSource().getPlayer() != null) {
            val player: ServerPlayer = ctx.getSource().getPlayer()!!
            val hoverable = Component.literal("HERE").withStyle(
                    Style.EMPTY.withUnderlined(true)
                            .withColor(ChatFormatting.AQUA)
                            .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://cobblemonextras.com/"))
                            .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to go to the Cobblemon Extras website!")))
            )
            player.sendSystemMessage(Component.literal("Find out more about CobblemonExtras Showcase ").append(hoverable))
        } else {
            ctx.getSource().sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }
}
