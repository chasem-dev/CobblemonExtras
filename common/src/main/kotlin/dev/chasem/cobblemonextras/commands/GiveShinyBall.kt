package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.CobblemonItems
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import dev.chasem.cobblemonextras.util.ItemBuilder
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

class GiveShinyBall {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("giveshinyball")
                .requires { src ->
                    CobblemonExtrasPermissions.checkPermission(
                        src,
                        CobblemonExtras.permissions.GIVE_SHINYBALL_PERMISSION
                    )
                }
                .then(
                    Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                            .suggests { context, builder ->
                                SharedSuggestionProvider.suggest(arrayOf("1", "32", "64"), builder)
                            }
                            .then(Commands.argument("ballType", StringArgumentType.word())
                                .suggests { context, builder ->
                                    SharedSuggestionProvider.suggest(
                                        arrayOf("poke", "great", "ultra", "master"),
                                        builder
                                    )
                                }
                                .executes { ctx: CommandContext<CommandSourceStack> ->
                                    this.execute(
                                        ctx,
                                        StringArgumentType.getString(ctx, "ballType")
                                    )
                                }
                            )
                            .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx, "poke") }
                        )
                )
        )
    }


    private fun execute(ctx: CommandContext<CommandSourceStack>, ballType: String = "poke"): Int {
        val targettedPlayer: ServerPlayer = EntityArgument.getPlayer(ctx, "player")

        if (targettedPlayer == null) {
            ctx.source.sendFailure(Component.literal("Player not found."))
            return 0
        }
        val amount = IntegerArgumentType.getInteger(ctx, "amount")

        targettedPlayer.inventory.add(createShinyBall(amount, ballType))

        return 1;
    }

    companion object {
        @JvmStatic
        fun createShinyBall(amount: Int, ballType: String = "poke"): ItemStack {

            val item = when(ballType) {
                "poke" -> CobblemonItems.POKE_BALL
                "great" -> CobblemonItems.GREAT_BALL
                "ultra" -> CobblemonItems.ULTRA_BALL
                "master" -> CobblemonItems.MASTER_BALL
                else -> CobblemonItems.POKE_BALL
            }

            return ItemBuilder(item)
                .setAmount(amount)
                .setCustomData(
                    CustomData.of(CompoundTag().apply {
                        this.putString("CobblemonExtrasBallType", "shiny")
                        this.putString("ShinyBallBallType", ballType)
                    })
                )
                .setCustomName(
                    Component.literal("Shiny Ball").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withBold(true))
                )
                .setCustomModel(CobblemonExtras.config.customModels.SHINY_BALL)
                .addLore(
                    arrayOf(
                        Component.literal("A unique ball that forces the captured")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)),
                        Component.literal(" pokemon to transform into a shiny variant.")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                    )
                )
                .build()
        }
    }
}