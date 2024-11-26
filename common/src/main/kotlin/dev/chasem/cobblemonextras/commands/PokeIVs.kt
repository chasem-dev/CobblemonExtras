package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.battles.BattleSide
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer

class PokeIVs {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("pokeivs")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKEIVS_PERMISSION) }
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) })
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.source.player != null) {
            val player: ServerPlayer = ctx.source.player!!
            val battle = BattleRegistry.getBattleByParticipatingPlayer(player)

            if (battle == null) {
                player.sendSystemMessage(Component.literal("You are not currently in a battle.").withStyle(ChatFormatting.RED))
                return 1
            }

            val playerActor = battle.getActor(player)
            var side: BattleSide? = null
            for (battleActor in battle.side1.actors) {
                if (playerActor?.uuid == battleActor.uuid) {
                    side = battle.side2 // Player is on SIDE 1, get oppposing side 2.
                    break
                }
            }
            if (side == null) {
                side = battle.side1 // Player is on SIDE 2, get oppposing side 1.
            }

            for (activeBattlePokemon in side.activePokemon) {
                if (activeBattlePokemon.battlePokemon != null) {
                    val pokemon = activeBattlePokemon.battlePokemon!!.originalPokemon

                    val hoveredText = Component.literal("").withStyle(Style.EMPTY.withUnderlined(false))

                    val header = pokemon.getDisplayName().withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withUnderlined(true))

                    val line1 = Component.literal("HP: ").withStyle(ChatFormatting.RED).append(Component.literal(pokemon.ivs.getOrDefault(Stats.HP).toString()).withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(" Atk: ").withStyle(ChatFormatting.BLUE).append(Component.literal(pokemon.ivs.getOrDefault(Stats.ATTACK).toString()).withStyle(ChatFormatting.WHITE)))
                            .append(Component.literal(" Def: ").withStyle(ChatFormatting.GRAY).append(Component.literal(pokemon.ivs.getOrDefault(Stats.DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))

                    val line2 = Component.literal("SpAtk: ").withStyle(ChatFormatting.AQUA).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPECIAL_ATTACK).toString()).withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(" SpDef: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPECIAL_DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))
                            .append(Component.literal(" Spd: ").withStyle(ChatFormatting.GREEN).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPEED).toString()).withStyle(ChatFormatting.WHITE)))

                    hoveredText.append(header).append(Component.literal("\n")).append(line1).append(Component.literal("\n")).append(line2)

                    val text = pokemon.getDisplayName().withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN))
                                .append(Component.literal(" IVs")
                                        .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)
                                                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hoveredText))
                                        )
                                )


//                    val hoverableText = Texts.join(pokemon.getDisplayName().withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), Component.literal("")).copy()
//                            .append(Component.literal(" ").styled { style -> style.withUnderline(false) })
//                            .append(Component.literal("IVs").withStyle(ChatFormatting.YELLOW)).getWithStyle(Style.EMPTY
//                                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hoveredText)))

                    player.sendSystemMessage(Component.literal("[").append(text).append("]"))
                } else {
                    player.sendSystemMessage(Component.literal("You are not currently in a battle.").withStyle(ChatFormatting.RED))
                    return 1
                }
            }
        } else {
            ctx.getSource().sendFailure(Component.literal("Sorry, this is only for players."))
        }
        return 1
    }
}