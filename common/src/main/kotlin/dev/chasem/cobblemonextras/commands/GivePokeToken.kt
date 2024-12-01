package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.pokemon.Nature
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
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData

class GivePokeToken {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                literal("givepoketoken")
                        .requires { src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.GIVE_POKETOKEN_PERMISSION) }
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(literal("shiny")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                                                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }))
                                .then(literal("maxivs")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                                                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }))
                                .then(literal("maxevs")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))

                                                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }))
                                .then(literal("nature")
                                        .then(Commands.argument("nature", StringArgumentType.word())
                                                .suggests { ctx, builder ->
                                                    SharedSuggestionProvider.suggest(
                                                            Natures.all().map { it.displayName },
                                                            builder)
                                                }
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                                                .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })))
                        )
        )
    }


    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val targettedPlayer: ServerPlayer = EntityArgument.getPlayer(ctx, "player")

        if (targettedPlayer == null) {
            ctx.source.sendFailure(Component.literal("Player not found."))
            return 0
        }

        val tokenType = ctx.input.split(" ")[2]
        val amount = IntegerArgumentType.getInteger(ctx, "amount")

        val itemBuilder = generateItem(tokenType, amount)

        if (tokenType == "nature") {

            itemBuilder.setCustomData(CustomData.of(
                    CompoundTag().apply {
                        putString("PokeTokenType", "nature")
                        putString("nature", StringArgumentType.getString(ctx, "nature"))
                    }
            ))

            val natureText = StringArgumentType.getString(ctx, "nature")
            val nature = Natures.getNature(natureText.replace("cobblemon.nature.", ""))
            val natureCapitalized = nature!!.displayName.replace("cobblemon.nature.", "").capitalize()
            itemBuilder.addLore(arrayOf(Component.literal(""), Component.literal("Nature: ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(natureCapitalized).withStyle(ChatFormatting.WHITE))))
        }

        val itemStack = itemBuilder.build()
        targettedPlayer.inventory.add(itemStack)

        return 1;
    }

    private fun generateItem(tokenType: String, amount: Int) : ItemBuilder {

        val itemName = when(tokenType) {
            "shinytoken" -> "Shiny Token"
            "maxivs" -> "Max IVs Token"
            "maxevs" -> "Max EVs Token"
            "nature" -> "Nature Token"
            else -> "Token"
        }

        val itemDescription = when(tokenType) {
            "shiny" -> "Right click on a pokemon to make it shiny."
            "maxivs" -> "Right click on a pokemon to give it max IVs."
            "maxevs" -> "Right click on a pokemon to give it max EVs."
            "nature" -> "Right click on a pokemon to change its nature to the specified nature."
            else -> "Right click on a pokemon to use this token."
        }

        return ItemBuilder(Items.PAPER)
                .setCustomData(CustomData.of(
                        CompoundTag().apply {
                            putString("PokeTokenType", tokenType)
                        }
                ))
                .setCustomName(Component.literal(itemName).withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)))
                .addLore(
                         arrayOf(
                                 Component.literal("One time use").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                                 Component.literal(""),
                                 Component.literal(itemDescription).withStyle(ChatFormatting.GRAY),
                         )
                )
                .setAmount(amount)
    }


}