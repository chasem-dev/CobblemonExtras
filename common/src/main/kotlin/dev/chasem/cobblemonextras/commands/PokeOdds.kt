package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

class PokeOdds {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        // Register /pokeodds to get the current shiny rate no permission required, and /pokeodds setRate <rate> to set the shiny rate, requires permission
        dispatcher.register(Commands.literal("pokeodds")
                .then(Commands.literal("setRate")
                        .requires { source: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(source, CobblemonExtras.permissions.POKEODDS_PERMISSION) }
                        .then(Commands.argument<Float>("rate", FloatArgumentType.floatArg(1.0f, 10000.0f))
                                .executes { ctx: CommandContext<CommandSourceStack> -> setRate(ctx, FloatArgumentType.getFloat(ctx, "rate")) }))
                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })
    }

    private fun setRate(ctx: CommandContext<CommandSourceStack>, rate: Float): Int {
        ctx.getSource().sendSystemMessage(Component.literal("The shiny rate has been set to: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(rate.toString()).withStyle(ChatFormatting.AQUA)))
        Cobblemon.config.shinyRate = rate
        return 1
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        ctx.getSource().sendSystemMessage(Component.literal("The current shiny rate is: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(Cobblemon.config.shinyRate.toString()).withStyle(ChatFormatting.AQUA)))
        return 1
    }
}
