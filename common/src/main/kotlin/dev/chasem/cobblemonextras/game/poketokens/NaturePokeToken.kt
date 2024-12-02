package dev.chasem.cobblemonextras.game.poketokens

import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import com.cobblemon.mod.common.pokemon.Nature
import dev.chasem.cobblemonextras.util.ItemBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.component.CustomData

class NaturePokeToken (val nature: Nature) : PokeToken(PokeTokenType.NATURE) {

    override fun getName(): MutableComponent {
        return Component.literal("Nature Token")
    }

    override fun getDescription(): MutableComponent {
        return Component.literal("Right click on a pokemon to change its nature to the specified nature.")
    }

    override fun generateItem(amount: Int) : ItemBuilder {
        val builder = super.generateItem(amount)
        val natureCapitalized = nature!!.displayName.replace("cobblemon.nature.", "").capitalize()

        return builder.setCustomData(CustomData.of(
                CompoundTag().apply {
                    putString("nature", nature.displayName)
                }
        )).addLore(arrayOf(Component.literal(""), Component.literal("Nature: ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal(natureCapitalized).withStyle(ChatFormatting.WHITE))))
    }

    override fun onUseItem(event: ServerPlayerEvent.RightClickEntity, entity: PokemonEntity) {
        val player = event.player
        val heldItem = player.mainHandItem
        if (heldItem.has(DataComponents.CUSTOM_DATA) && heldItem.get(DataComponents.CUSTOM_DATA)!!.contains("PokeTokenType")) {
            entity.pokemon.nature = this.nature
            player.playNotifySound(
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 1.0F, 1.0F
            )
            player.sendSystemMessage(Component.literal("Your ${entity.pokemon.species.translatedName.string} has received the ${this.nature.displayName} nature").withStyle(ChatFormatting.YELLOW))
        }
    }

}