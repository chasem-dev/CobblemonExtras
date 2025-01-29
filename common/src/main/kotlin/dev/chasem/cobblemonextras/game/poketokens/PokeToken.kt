package dev.chasem.cobblemonextras.game.poketokens

import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import dev.chasem.cobblemonextras.util.ItemBuilder
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData

abstract class PokeToken(val tokenType: PokeTokenType) {


    abstract fun getName(): MutableComponent;
    abstract fun getDescription(): MutableComponent;

    open fun generateItem(amount: Int = 1) : ItemBuilder {
        return ItemBuilder(Items.PAPER)
                .setCustomData(CustomData.of(
                        CompoundTag().apply {
                            putString("PokeTokenType", tokenType.name)
                        }
                ))
                .setCustomName(getName().withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)))
                .addLore(
                        arrayOf(
                                Component.literal("One time use").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                                Component.literal(""),
                                getDescription().withStyle(ChatFormatting.GRAY),
                        )
                )
                .setAmount(amount)
    }

    abstract fun onUseItem(event: ServerPlayerEvent.RightClickEntity, entity: PokemonEntity)

    companion object {

        fun getStatFromString(stat: String): Stat {
            return when (stat.lowercase()) {
                "hp" -> Stats.HP
                "atk" -> Stats.ATTACK
                "def" -> Stats.DEFENCE
                "spatk" -> Stats.SPECIAL_ATTACK
                "spdef" -> Stats.SPECIAL_DEFENCE
                "spd" -> Stats.SPEED
                else -> throw IllegalArgumentException("Invalid getStatFromString: $stat")
            }
        }

        fun fromCustomData(customData: CustomData): PokeToken {
            val tag = customData.copyTag();
            val type = tag.getString("PokeTokenType")
            val tokenType = PokeTokenType.valueOf(type)
            val natureString = tag.getString("nature") ?: ""
            val ivStatsString = tag.getString("stats") ?: ""
//            val statsSplit = stats.joinToString(",") { it.displayName.string };
            var ivStats = emptySet<Stat>()
            if (ivStatsString.isNotEmpty()) {
                ivStats = ivStatsString.split(",").map { statString ->
                    Stats.PERMANENT.find {
                        it.displayName.string == statString
                    }!!
                }.toSet()
            }
            val evStatsString = tag.getString("stat") ?: ""
            val evStat = Stats.getStat(evStatsString)
            val nature = Natures.all().find { it.displayName == natureString }



            if (natureString.isNotEmpty() && nature == null) {
                throw IllegalArgumentException("Invalid nature string: $natureString")
            }


            return when(tokenType) {
                PokeTokenType.SHINY -> ShinyPokeToken()
                PokeTokenType.MAXIV -> MaxIVPokeToken(ivStats)
                PokeTokenType.MAXEV -> MaxEVPokeToken(evStat)
                PokeTokenType.NATURE -> NaturePokeToken(nature!!)
            }
        }
    }
}