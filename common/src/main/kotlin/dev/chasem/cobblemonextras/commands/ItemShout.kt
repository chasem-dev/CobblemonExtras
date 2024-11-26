package dev.chasem.cobblemonextras.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

class ItemShout {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("itemshout")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.ITEMSHOUT_PERMISSION) }
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }
        )
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player: ServerPlayer = ctx.source.player!!
            val heldItem: ItemStack = player.mainHandItem
//            val itemContent: HoverEvent.ItemStackContent = ItemStackContent(heldItem)
            var itemHover = HoverEvent.ItemStackInfo(heldItem)
            val toSend = Component.literal("[").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal("ItemShout").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal("] ").withStyle(ChatFormatting.YELLOW))
                    .append(player.displayName!!.copy().append(Component.literal(": ")).withStyle(ChatFormatting.WHITE))

            val itemName = heldItem.displayName
            val hoverAbleName = itemName.copy().withStyle(itemName.style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_ITEM, itemHover)).withUnderlined(true))

//            val hoverAbleName = Texts.join(itemName.getWithStyle(itemName.getStyle()
//                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_ITEM, itemContent))
//                    .withUnderline(true)),
//                    Component.literal("")
//            )
            toSend.append(hoverAbleName)
            ctx.source.server.playerList.players.forEach { serverPlayer -> serverPlayer.sendSystemMessage(toSend) }
        } else {
            ctx.source.sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }
}
