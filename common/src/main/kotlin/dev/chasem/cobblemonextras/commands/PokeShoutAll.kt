package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import dev.chasem.cobblemonextras.util.PokemonUtility
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class PokeShoutAll {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("pokeshoutall")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESHOUT_ALL_PERMISSION) }
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }
        )
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player: ServerPlayer = ctx.source.player!!;
            val party = Cobblemon.storage.getParty(player)
            val toSend = Component.literal("[").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal("PokeShoutAll").withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal("] ").withStyle(ChatFormatting.GREEN))
                    .append(player.displayName!!.copy().append(Component.literal(": ")).withStyle(ChatFormatting.WHITE))
            toSend.append(("\n"))
            for (i in 0..5) {
                val pokemon = party.get(i)
                if (pokemon != null) {
                    toSend.append("    " + (i + 1) + ": ")
                    val pokemonName = pokemon.species.translatedName.withStyle(ChatFormatting.GREEN).append(" ")
                    toSend.append(pokemonName)
                    if (pokemon.shiny) {
                        toSend.append(Component.literal("★ ").withStyle(ChatFormatting.GOLD))
                    }
                    PokemonUtility.getHoverText(toSend, pokemon)
                    if (i != 5) {
                        toSend.append(("\n"))
                    }
                } else {
                    toSend.append("    " + (i + 1) + ": ")
                    toSend.append(Component.literal("Empty").withStyle(ChatFormatting.RED))
                    if (i != 5) {
                        toSend.append(("\n"))
                    }
                }
            }
            ctx.getSource().getServer().playerList.players.forEach { serverPlayer -> serverPlayer.sendSystemMessage(toSend) }
        } else {
            ctx.getSource().sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }
}
