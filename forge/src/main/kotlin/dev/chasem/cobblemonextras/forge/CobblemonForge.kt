package dev.chasem.cobblemonextras.forge

import dev.chasem.cobblemonextras.CobblemonExtras
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent

@Mod(CobblemonExtras.MODID)
class CobblemonExtrasForge {
    init {
        with(thedarkcolour.kotlinforforge.forge.MOD_BUS) {
//            EventBuses.registerModEventBus(CobblemonExtras.MODID, this)
            addListener(this@CobblemonExtrasForge::initialize)
            addListener(this@CobblemonExtrasForge::serverInit)
        }
        with(MinecraftForge.EVENT_BUS) {
            addListener(this@CobblemonExtrasForge::registerCommands)
        }
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        CobblemonExtras.registerCommands(e.dispatcher, e.buildContext, e.commandSelection)
    }



    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        CobblemonExtras.initialize()
        System.out.println("Cobblemon Forge Init.")
    }

}