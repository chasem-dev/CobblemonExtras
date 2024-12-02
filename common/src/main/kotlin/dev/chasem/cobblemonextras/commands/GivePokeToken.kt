package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Nature
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.game.poketokens.MaxEVPokeToken
import dev.chasem.cobblemonextras.game.poketokens.MaxIVPokeToken
import dev.chasem.cobblemonextras.game.poketokens.NaturePokeToken
import dev.chasem.cobblemonextras.game.poketokens.ShinyPokeToken
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
                                                .then(Commands.argument("stats", StringArgumentType.greedyString())
                                                        .suggests { ctx, builder ->
                                                            SharedSuggestionProvider.suggest(
                                                                    listOf("HP", "Atk", "Def", "SpAtk", "SpDef", "Spd"),
                                                                    builder)
                                                        }
                                                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })
                                        ))
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

        var itemStack: ItemStack? = null

        if (tokenType == "nature") {
            val natureText = StringArgumentType.getString(ctx, "nature")
            val nature = Natures.getNature(natureText.replace("cobblemon.nature.", ""))
            if (nature == null) {
                ctx.source.sendFailure(Component.literal("Nature not found."))
                return 0
            }
            itemStack = NaturePokeToken(nature).generateItem(amount).build()
        } else if (tokenType == "maxivs") {
            val statsArg = StringArgumentType.getString(ctx, "stats");
            // Create a list of stats from Stats.PERMANENT
            val stats = statsArg.split(" ").map {
                when (it.lowercase()) {
                    "hp" -> Stats.HP
                    "atk" -> Stats.ATTACK
                    "def" -> Stats.DEFENCE
                    "spatk" -> Stats.SPECIAL_ATTACK
                    "spdef" -> Stats.SPECIAL_DEFENCE
                    "spd" -> Stats.SPEED
                    else -> throw IllegalArgumentException("Invalid stat: $it")
                }
            }

            itemStack = MaxIVPokeToken(stats.toSet()).generateItem(amount).build()
        } else if (tokenType == "maxevs") {
            itemStack = MaxEVPokeToken(Stats.ATTACK).generateItem(amount).build()
        } else if (tokenType == "shiny") {
            itemStack = ShinyPokeToken().generateItem(amount).build()
        }

        if (itemStack == null) {
            ctx.source.sendFailure(Component.literal("Failed to create PokeToken."))
            return 0
        }

        targettedPlayer.inventory.add(itemStack)

        return 1;
    }
}