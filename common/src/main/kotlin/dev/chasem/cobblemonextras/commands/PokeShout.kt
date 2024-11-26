package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import dev.chasem.cobblemonextras.util.PokemonUtility
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

class PokeShout {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("pokeshout")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESHOUT_PERMISSION) }
                        .then(Commands.argument("slot", IntegerArgumentType.integer(1, 6)).executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })
        )
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player: ServerPlayer = ctx.source.player!!
            val slot: Int = ctx.getArgument<Int>("slot", Int::class.java)
            val party = Cobblemon.storage.getParty(player)
            val pokemon = party.get(slot - 1)
            if (pokemon != null) {
                val toSend = Component.literal("[").withStyle(ChatFormatting.GREEN)
                        .append(Component.literal("PokeShout").withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal("] ").withStyle(ChatFormatting.GREEN))
                        .append(player.displayName!!.copy().append(Component.literal(": ")).withStyle(ChatFormatting.WHITE))
                val pokemonName = pokemon.species.translatedName.withStyle(ChatFormatting.GREEN).append(" ")
                toSend.append(pokemonName)
                if (pokemon.shiny) {
                    toSend.append(Component.literal("★ ").withStyle(ChatFormatting.GOLD))
                }
                PokemonUtility.getHoverText(toSend, pokemon)
                ctx.source.server.playerList.players.forEach { serverPlayer -> serverPlayer.sendSystemMessage(toSend) }
            } else {
                ctx.source.sendFailure(Component.literal("No Pokemon in slot."))
            }
        } else {
            ctx.source.sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }
}
