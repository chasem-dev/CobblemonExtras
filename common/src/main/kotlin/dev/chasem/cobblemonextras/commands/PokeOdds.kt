package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.config.CobblemonExtrasConfig
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class PokeOdds {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("pokeodds")
                // anyone can read the current rate
                .executes { ctx -> execute(ctx) }
                .then(
                    Commands.literal("setRate")
                        // Allow if OP level >= 2, or the explicit permission is granted, or it's the console
                        .requires { src ->
                            val isConsole = src.entity == null
                            val isOp = src.hasPermission(2) // vanilla admin level gate
                            val hasNode = CobblemonExtrasPermissions.checkPermission(
                                src, CobblemonExtras.permissions.POKEODDS_PERMISSION
                            )
                            isConsole || isOp || hasNode
                        }
                        .then(
                            Commands.argument("rate", FloatArgumentType.floatArg(1.0f, 10000.0f))
                                .executes { ctx ->
                                    setRate(ctx, FloatArgumentType.getFloat(ctx, "rate"))
                                }
                        )
                )
        )
    }

    private fun setRate(ctx: CommandContext<CommandSourceStack>, rate: Float): Int {
        Cobblemon.config.shinyRate = rate
        
        // Save the Cobblemon config to persist changes across restarts
        try {
            saveCobblemonConfig(rate)
            ctx.source.sendSystemMessage(
                Component.literal("The shiny rate has been set to: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(rate.toString()).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" (saved to config)").withStyle(ChatFormatting.GREEN))
            )
        } catch (e: Exception) {
            ctx.source.sendSystemMessage(
                Component.literal("The shiny rate has been set to: ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(rate.toString()).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" (WARNING: failed to save to config)").withStyle(ChatFormatting.RED))
            )
            CobblemonExtras.getLogger().error("Failed to save Cobblemon config after setting shiny rate", e)
        }
        
        return 1
    }

    private fun saveCobblemonConfig(newShinyRate: Float) {
        val configFileLoc = System.getProperty("user.dir") + File.separator + "config" + File.separator + "cobblemon" + File.separator + "main.json"
        val configFile = File(configFileLoc)
        
        if (!configFile.exists()) {
            CobblemonExtras.getLogger().warn("Cobblemon config file not found at: $configFileLoc")
            return
        }
        
        try {
            // Read the existing config
            val fileReader = FileReader(configFile)
            val configJson = JsonParser.parseReader(fileReader).asJsonObject
            fileReader.close()
            
            // Update the shiny rate directly at the root level
            configJson.addProperty("shinyRate", newShinyRate)
            
            // Write the updated config back to file
            val fileWriter = FileWriter(configFile)
            CobblemonExtrasConfig.GSON.toJson(configJson, fileWriter)
            fileWriter.flush()
            fileWriter.close()
            
            CobblemonExtras.getLogger().info("Successfully saved shiny rate $newShinyRate to Cobblemon config")
        } catch (e: Exception) {
            CobblemonExtras.getLogger().error("Failed to save Cobblemon config", e)
            throw e
        }
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        ctx.source.sendSystemMessage(
            Component.literal("The current shiny rate is: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(Cobblemon.config.shinyRate.toString()).withStyle(ChatFormatting.AQUA))
        )
        return 1
    }
}
