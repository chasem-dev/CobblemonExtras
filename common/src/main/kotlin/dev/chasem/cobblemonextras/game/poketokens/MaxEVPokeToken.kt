package dev.chasem.cobblemonextras.game.poketokens

import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import dev.chasem.cobblemonextras.util.ItemBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.component.CustomData

class MaxEVPokeToken(private val stat: Stat) : PokeToken(PokeTokenType.MAXEV) {

    override fun getName(): MutableComponent {
        return Component.literal("Max EVs Token")
    }

    override fun getDescription(): MutableComponent {
        return Component.literal("Right click on a pokemon to give it max EVs.")
    }

    override fun generateItem(amount: Int): ItemBuilder {
        val builder = super.generateItem(amount)
        return builder.setCustomData(CustomData.of(
                CompoundTag().apply {
                    this.putString("stat", stat.displayName.string)
                }
        ))
                .addLore(
                        arrayOf(
                                Component.literal("Stat: ").withStyle(ChatFormatting.YELLOW),
                                Component.literal(stat.displayName.string).withStyle(ChatFormatting.WHITE),
                        )
                )
    }

    override fun onUseItem(event: ServerPlayerEvent.RightClickEntity, entity: PokemonEntity) {
        val player = event.player
        val heldItem = player.mainHandItem
        if (heldItem.has(DataComponents.CUSTOM_DATA) && heldItem.get(DataComponents.CUSTOM_DATA)!!.contains("PokeTokenType")) {
            player.playNotifySound(
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 1.0F, 1.0F
            )
            entity.pokemon.setEV(stat, 252)
            player.sendSystemMessage(Component.literal("Your ${entity.pokemon.species.translatedName.string} has received the maximum EVs for ${stat.displayName.string}").withStyle(ChatFormatting.YELLOW))
        }
    }


}