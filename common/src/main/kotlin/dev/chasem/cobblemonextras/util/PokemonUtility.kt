package dev.chasem.cobblemonextras.util

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.lang
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import java.lang.Boolean
import kotlin.arrayOf

object PokemonUtility {
    fun getHoverText(toSend: MutableComponent, pokemon: Pokemon): Component {
        val statsHoverText = Component.literal("").withStyle(Style.EMPTY)

        if (pokemon.shiny) {
            statsHoverText.append(Component.literal(" ★").withStyle(ChatFormatting.GOLD))
        }
        statsHoverText.append(pokemon.species.translatedName.copy().withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN).withUnderlined(true)))
        statsHoverText.append(Component.literal("\n"))
        if (pokemon.nickname != null) {
            statsHoverText.append(Component.literal("Nickname: ").withStyle(ChatFormatting.GRAY).append(Component.literal(pokemon.nickname!!.string).withStyle(ChatFormatting.WHITE)))
            statsHoverText.append(Component.literal("\n"))
        }
        statsHoverText.append(Component.literal("Level: ").withStyle(ChatFormatting.AQUA).append(Component.literal(pokemon.level.toString()).withStyle(ChatFormatting.WHITE)))
        statsHoverText.append(Component.literal("\n"))
        statsHoverText.append(Component.literal("Nature: ").withStyle(ChatFormatting.YELLOW).append(lang(pokemon.nature.displayName.replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)))
        statsHoverText.append(Component.literal("\n"))
        statsHoverText.append(Component.literal("Ability: ").withStyle(ChatFormatting.GOLD).append(lang(pokemon.ability.displayName.replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)))
        statsHoverText.append(Component.literal("\n"))
        statsHoverText.append(Component.literal("Form: ").withStyle(ChatFormatting.GREEN).append(Component.literal(pokemon.form.name).withStyle(ChatFormatting.WHITE)))

        val statsText = Component.literal("[Stats]").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, statsHoverText)))

        toSend.append(statsText)

        // Add up Evs, round to 2 decimal places, multiply by 100 to get percent
        val allEvs = (pokemon.evs.getOrDefault(Stats.HP) + pokemon.evs.getOrDefault(Stats.ATTACK) + pokemon.evs.getOrDefault(Stats.DEFENCE) + pokemon.evs.getOrDefault(Stats.SPECIAL_ATTACK) + pokemon.evs.getOrDefault(Stats.SPECIAL_DEFENCE) + pokemon.evs.getOrDefault(Stats.SPEED)).toDouble()
        val avg = (allEvs / (510.0))
        val evPercent = Math.round(avg * 10000).toDouble() / 100

        val evsText = Component.literal("[EVs]").withStyle(ChatFormatting.GOLD)
        val evsHoverText = Component.literal("").withStyle(Style.EMPTY)
        evsHoverText.append(Component.literal("EVs ($evPercent%) ").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withUnderlined(Boolean.TRUE)))
        evsHoverText.append(Component.literal("\n").withStyle(Style.EMPTY.withUnderlined(false)))
        evsHoverText.append(Component.literal("HP: ").withStyle(ChatFormatting.RED).append(Component.literal(pokemon.evs.getOrDefault(Stats.HP).toString()).withStyle(ChatFormatting.WHITE)))
        evsHoverText.append(Component.literal("\n"))
        evsHoverText.append(Component.literal("Attack: ").withStyle(ChatFormatting.BLUE).append(Component.literal(pokemon.evs.getOrDefault(Stats.ATTACK).toString()).withStyle(ChatFormatting.WHITE)))
        evsHoverText.append(Component.literal("\n"))
        evsHoverText.append(Component.literal("Defense: ").withStyle(ChatFormatting.GRAY).append(Component.literal(pokemon.evs.getOrDefault(Stats.DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))
        evsHoverText.append(Component.literal("\n"))
        evsHoverText.append(Component.literal("Sp. Attack: ").withStyle(ChatFormatting.AQUA).append(Component.literal(pokemon.evs.getOrDefault(Stats.SPECIAL_ATTACK).toString()).withStyle(ChatFormatting.WHITE)))
        evsHoverText.append(Component.literal("\n"))
        evsHoverText.append(Component.literal("Sp. Defense: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(pokemon.evs.getOrDefault(Stats.SPECIAL_DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))
        evsHoverText.append(Component.literal("\n"))
        evsHoverText.append(Component.literal("Speed: ").withStyle(ChatFormatting.GREEN).append(Component.literal(pokemon.evs.getOrDefault(Stats.SPEED).toString()).withStyle(ChatFormatting.WHITE)))
        evsText.setStyle(evsText.style.withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        evsHoverText
                ))
        )
        toSend.append(evsText)

        // Add up Ivs, take average, round to 2 decimal places, multiply by 100 to get percent
        val allIvs = pokemon.ivs.getOrDefault(Stats.HP) + pokemon.ivs.getOrDefault(Stats.ATTACK) + pokemon.ivs.getOrDefault(Stats.DEFENCE) + pokemon.ivs.getOrDefault(Stats.SPECIAL_ATTACK) + pokemon.ivs.getOrDefault(Stats.SPECIAL_DEFENCE) + pokemon.ivs.getOrDefault(Stats.SPEED)
        val ivAvg = (allIvs / (186.0))
        val ivPercent = Math.round(ivAvg * 10000).toDouble() / 100

        val ivsText = Component.literal("[IVs]").withStyle(ChatFormatting.LIGHT_PURPLE)
        val ivsHoverText = Component.literal("").withStyle(Style.EMPTY)
        ivsHoverText.append(Component.literal("IVs ($ivPercent%)").withStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE).withUnderlined(true)))
        ivsHoverText.append(Component.literal("\n").withStyle(Style.EMPTY.withUnderlined(false)))
        ivsHoverText.append(Component.literal("HP: ").withStyle(ChatFormatting.RED).append(Component.literal(pokemon.ivs.getOrDefault(Stats.HP).toString()).withStyle(ChatFormatting.WHITE)))
        ivsHoverText.append(Component.literal("\n"))
        ivsHoverText.append(Component.literal("Attack: ").withStyle(ChatFormatting.BLUE).append(Component.literal(pokemon.ivs.getOrDefault(Stats.ATTACK).toString()).withStyle(ChatFormatting.WHITE)))
        ivsHoverText.append(Component.literal("\n"))
        ivsHoverText.append(Component.literal("Defense: ").withStyle(ChatFormatting.GRAY).append(Component.literal(pokemon.ivs.getOrDefault(Stats.DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))
        ivsHoverText.append(Component.literal("\n"))
        ivsHoverText.append(Component.literal("Sp. Attack: ").withStyle(ChatFormatting.AQUA).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPECIAL_ATTACK).toString()).withStyle(ChatFormatting.WHITE)))
        ivsHoverText.append(Component.literal("\n"))
        ivsHoverText.append(Component.literal("Sp. Defense: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPECIAL_DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))
        ivsHoverText.append(Component.literal("\n"))
        ivsHoverText.append(Component.literal("Speed: ").withStyle(ChatFormatting.GREEN).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPEED).toString()).withStyle(ChatFormatting.WHITE)))
        ivsText.setStyle(ivsText.style.withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        ivsHoverText
                ))
        )
        toSend.append(ivsText)

        val movesText = Component.literal("[Moves]").withStyle(ChatFormatting.BLUE)
        val movesHoverText = Component.literal("").withStyle(Style.EMPTY)
        movesHoverText.append(Component.literal("Moves").withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withUnderlined(Boolean.TRUE)))
        movesHoverText.append(Component.literal("\n").withStyle(Style.EMPTY.withUnderlined(false)))
        val moveOne = if (pokemon.moveSet.getMoves().size >= 1) pokemon.moveSet[0]!!.displayName.string else "Empty"
        val moveTwo = if (pokemon.moveSet.getMoves().size >= 2) pokemon.moveSet[1]!!.displayName.string else "Empty"
        val moveThree = if (pokemon.moveSet.getMoves().size >= 3) pokemon.moveSet[2]!!.displayName.string else "Empty"
        val moveFour = if (pokemon.moveSet.getMoves().size >= 4) pokemon.moveSet[3]!!.displayName.string else "Empty"
        movesHoverText.append(Component.literal("Move 1: ").withStyle(ChatFormatting.RED).append(Component.literal(moveOne).withStyle(ChatFormatting.WHITE)))
        movesHoverText.append(Component.literal("\n"))
        movesHoverText.append(Component.literal("Move 2: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(moveTwo).withStyle(ChatFormatting.WHITE)))
        movesHoverText.append(Component.literal("\n"))
        movesHoverText.append(Component.literal("Move 3: ").withStyle(ChatFormatting.AQUA).append(Component.literal(moveThree).withStyle(ChatFormatting.WHITE)))
        movesHoverText.append(Component.literal("\n"))
        movesHoverText.append(Component.literal("Move 4: ").withStyle(ChatFormatting.GREEN).append(Component.literal(moveFour).withStyle(ChatFormatting.WHITE)))
        movesText.setStyle(movesText.style.withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        movesHoverText
                ))
        )
        toSend.append(movesText)

        return toSend
    }

    fun pokemonToItem(pokemon: Pokemon): ItemStack {
        val moveOne = if (pokemon.moveSet.getMoves().size >= 1) pokemon.moveSet[0]!!.displayName.string else "Empty"
        val moveTwo = if (pokemon.moveSet.getMoves().size >= 2) pokemon.moveSet[1]!!.displayName.string else "Empty"
        val moveThree = if (pokemon.moveSet.getMoves().size >= 3) pokemon.moveSet[2]!!.displayName.string else "Empty"
        val moveFour = if (pokemon.moveSet.getMoves().size >= 4) pokemon.moveSet[3]!!.displayName.string else "Empty"



        val itemstack: ItemStack = ItemBuilder(PokemonItem.from(pokemon,1))
                .hideAdditional()
                .addLore(arrayOf<Component>(Component.literal(pokemon.caughtBall.item().defaultInstance.displayName.string).setStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.DARK_GRAY)),
                        Component.literal("Level: ").withStyle(ChatFormatting.AQUA).append(Component.literal(pokemon.level.toString()).withStyle(ChatFormatting.WHITE)),

                        Component.literal("Nickname: ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal(
                                pokemon.nickname?.string ?: "No nickname"
                        ).withStyle(ChatFormatting.WHITE)),

                        Component.literal("Nature: ").withStyle(ChatFormatting.YELLOW).append(lang(pokemon.nature.displayName.replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)),
                        Component.literal("Ability: ").withStyle(ChatFormatting.GOLD).append(lang(pokemon.ability.displayName.replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)),
                        Component.literal("IVs: ").withStyle(ChatFormatting.LIGHT_PURPLE),
                        Component.literal("  HP: ").withStyle(ChatFormatting.RED).append(Component.literal(pokemon.ivs.getOrDefault(Stats.HP).toString()).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  Atk: ").withStyle(ChatFormatting.BLUE).append(Component.literal(pokemon.ivs.getOrDefault(Stats.ATTACK).toString()).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Def: ").withStyle(ChatFormatting.GRAY).append(Component.literal(pokemon.ivs.getOrDefault(Stats.DEFENCE).toString()).withStyle(ChatFormatting.WHITE))),
                        Component.literal("  SpAtk: ").withStyle(ChatFormatting.AQUA).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPECIAL_ATTACK).toString()).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  SpDef: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPECIAL_DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Spd: ").withStyle(ChatFormatting.GREEN).append(Component.literal(pokemon.ivs.getOrDefault(Stats.SPEED).toString()).withStyle(ChatFormatting.WHITE))),

                        Component.literal("EVs: ").withStyle(ChatFormatting.DARK_AQUA),
                        Component.literal("  HP: ").withStyle(ChatFormatting.RED).append(Component.literal(pokemon.evs.getOrDefault(Stats.HP).toString()).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  Atk: ").withStyle(ChatFormatting.BLUE).append(Component.literal(pokemon.evs.getOrDefault(Stats.ATTACK).toString()).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Def: ").withStyle(ChatFormatting.GRAY).append(Component.literal(pokemon.evs.getOrDefault(Stats.DEFENCE).toString()).withStyle(ChatFormatting.WHITE))),
                        Component.literal("  SpAtk: ").withStyle(ChatFormatting.AQUA).append(Component.literal(pokemon.evs.getOrDefault(Stats.SPECIAL_ATTACK).toString()).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  SpDef: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(pokemon.evs.getOrDefault(Stats.SPECIAL_DEFENCE).toString()).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Spd: ").withStyle(ChatFormatting.GREEN).append(Component.literal(pokemon.evs.getOrDefault(Stats.SPEED).toString()).withStyle(ChatFormatting.WHITE))),
                        Component.literal("Moves: ").withStyle(ChatFormatting.DARK_GREEN),
                        Component.literal(" ").append(Component.literal(moveOne).withStyle(ChatFormatting.WHITE)),
                        Component.literal(" ").append(Component.literal(moveTwo).withStyle(ChatFormatting.WHITE)),
                        Component.literal(" ").append(Component.literal(moveThree).withStyle(ChatFormatting.WHITE)),
                        Component.literal(" ").append(Component.literal(moveFour).withStyle(ChatFormatting.WHITE)),
                        Component.literal("Form: ").withStyle(ChatFormatting.GOLD).append(pokemon.form.name)
                ))
                .setCustomName(
                        if (pokemon.shiny) pokemon.species.translatedName.copy().withStyle(ChatFormatting.GRAY).append(Component.literal(" ★").withStyle(ChatFormatting.GOLD)) else pokemon.species.translatedName.copy().withStyle(ChatFormatting.GRAY)
                )
                .build()
        return itemstack
    }
}