package dev.chasem.cobblemonextras.events

import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.platform.events.ServerPlayerEvent
import dev.chasem.cobblemonextras.game.poketokens.NaturePokeToken
import dev.chasem.cobblemonextras.game.poketokens.PokeToken
import dev.chasem.cobblemonextras.game.poketokens.PokeTokenType
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData

class PokeTokensInteractionHandler {

    init {
        PlatformEvents.RIGHT_CLICK_ENTITY.subscribe { event ->
            if (event.entity.type == CobblemonEntities.POKEMON) {
                val entity = event.entity as PokemonEntity
                onPlayerRightClickPokemon(event, entity)
            }
        }
    }

    fun onPlayerRightClickPokemon(event: ServerPlayerEvent.RightClickEntity, entity: PokemonEntity) {
        val player = event.player as ServerPlayer
        val heldItem = player.mainHandItem.copy()
        if (heldItem != null && heldItem.has(DataComponents.CUSTOM_DATA) && heldItem.get(DataComponents.CUSTOM_DATA)!!.contains("PokeTokenType")) {
            val customData = heldItem.get(DataComponents.CUSTOM_DATA) ?: CustomData.EMPTY
            val pokeToken = PokeToken.fromCustomData(customData)
            pokeToken.onUseItem(event, entity)
            player.mainHandItem.shrink(1)
        }

    }
}