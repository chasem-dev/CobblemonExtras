package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon.storage
import com.cobblemon.mod.common.api.storage.party.PartyPosition
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
import net.minecraft.server.level.ServerPlayer

class PokeDelete {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val main = dispatcher.register(
                Commands.literal("pokedelete")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKEDELETE_PERMISSION) }
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("slot", IntegerArgumentType.integer(1, 6))
                                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })))
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val player: ServerPlayer = EntityArgument.getPlayer(ctx, "player")
        val slotNum: Int = ctx.getArgument("slot", Int::class.java) - 1
        val party = storage.getParty(player)
        val pokemon = party.get(slotNum)
        if (pokemon != null) {
            val toSend = Component.literal("Deleted: ").append(Component.literal("").setStyle(Style.EMPTY.withBold(true)))
            val text = PokemonUtility.getHoverText(toSend, pokemon)
            ctx.source.sendSystemMessage(text)
            party.remove(PartyPosition(slotNum))
        } else {
            ctx.source.sendSystemMessage(Component.literal("No Pokemon found in slot.").withStyle(ChatFormatting.RED))
        }

        return 1
    }
}
