package dev.chasem.cobblemonextras

import dev.chasem.cobblemonextras.commands.*
import dev.chasem.cobblemonextras.config.CobblemonExtrasConfig
import net.fabricmc.api.ModInitializer

class CobblemonExtras : ModInitializer {

    override fun onInitialize() {
        println("CobblemonExtras - Initialized")
        PokeTrade()
        PokeSee()
        PC()
        CompSee()
        PokeShout()
        CobblemonExtrasConfig();
    }

}