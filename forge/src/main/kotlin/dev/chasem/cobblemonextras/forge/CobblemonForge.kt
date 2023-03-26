package dev.chasem.cobblemonextras.forge

import dev.architectury.platform.forge.EventBuses
import dev.chasem.cobblemonextras.CobblemonExtras
import java.util.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent

@Mod(CobblemonExtras.MODID)
class CobblemonExtrasForge {
    init {
        with(thedarkcolour.kotlinforforge.forge.MOD_BUS) {
            EventBuses.registerModEventBus(CobblemonExtras.MODID, this)
            addListener(this@CobblemonExtrasForge::initialize)
            addListener(this@CobblemonExtrasForge::serverInit)
        }
    }

    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        CobblemonExtras.initialize()
        System.out.println("Cobblemon Forge Init.")
    }

}