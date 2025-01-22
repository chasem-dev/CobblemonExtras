package dev.chasem.cobblemonextras.game.poketokens

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import dev.chasem.cobblemonextras.config.CustomModelConstants
import dev.chasem.cobblemonextras.util.ItemBuilder
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource

class ShinyPokeToken : PokeToken(PokeTokenType.SHINY) {

    override fun generateItem(amount: Int): ItemBuilder {
        val builder = super.generateItem(amount)
        builder
            .setCustomModel(CustomModelConstants.SHINY_TOKEN)
        return builder
    }

    override fun getName(): MutableComponent {
        return Component.literal("Shiny Token")
    }

    override fun getDescription(): MutableComponent {
        return Component.literal("Right click on a pokemon to make it shiny.")
    }

    override fun onUseItem(event: ServerPlayerEvent.RightClickEntity, entity: PokemonEntity) {
        val player = event.player
        entity.pokemon.shiny = true
        player.playNotifySound(
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 1.0F, 1.0F
        )
        player.sendSystemMessage(Component.literal("Your Pokemon is now shiny!").withStyle(ChatFormatting.YELLOW))
    }


}