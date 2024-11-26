package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.storage
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException
import com.cobblemon.mod.common.net.messages.client.storage.pc.SetPCBoxPokemonPacket
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import dev.chasem.cobblemonextras.util.PokemonUtility
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class CompDelete {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val main = dispatcher.register(
                Commands.literal("compdelete")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.COMPDELETE_PERMISSION) }
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("box", IntegerArgumentType.integer(1, Cobblemon.config.defaultBoxCount))
                                        .then(Commands.argument<Int>("slot", IntegerArgumentType.integer(1, 30))
                                                .executes { ctx: CommandContext<CommandSourceStack> -> this.other(ctx) }))))
    }

    private fun other(ctx: CommandContext<CommandSourceStack>): Int {
        val otherPlayerName = ctx.input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] // ctx.getArgument("player", String.class);
        val otherPlayer = ctx.source.server.playerList.getPlayerByName(otherPlayerName)
        if (otherPlayer != null) {
            val boxNum = ctx.getArgument("box", Int::class.java) - 1
            val slotNum = ctx.getArgument("slot", Int::class.java) - 1
            try {
                val pcStore = storage.getPC(otherPlayer)
                if (boxNum < pcStore.boxes.size) {
                    val box = pcStore.boxes[boxNum]
                    val pokemon = box[slotNum]
                    if (pokemon != null) {
                        val toSend = Component.literal("Deleted: ").append(Component.literal("").setStyle(Style.EMPTY.withBold(true)))
                        val text = PokemonUtility.getHoverText(toSend, pokemon)
                        ctx.source.sendSystemMessage(text)
                    } else {
                        ctx.source.sendSystemMessage(Component.literal("No Pokemon found in slot.").withStyle(ChatFormatting.RED))
                    }
                    box[slotNum] = null
                    SetPCBoxPokemonPacket(box).sendToPlayer(otherPlayer)
                }
            } catch (e: NoPokemonStoreException) {
                throw RuntimeException(e)
            }
        }

        return 1
    }
}
