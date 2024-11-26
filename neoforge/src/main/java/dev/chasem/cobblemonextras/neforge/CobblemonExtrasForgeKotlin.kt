//package dev.chasem.cobblemonextras.forge
//
//import dev.chasem.cobblemonextras.CobblemonExtras
//import net.minecraft.server.level.ServerPlayer
//import net.neoforged.fml.common.Mod
//import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
//import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
//import net.neoforged.neoforge.common.NeoForge
//import net.neoforged.neoforge.event.RegisterCommandsEvent
//import net.neoforged.neoforge.event.entity.player.PlayerEvent
//import net.neoforged.neoforge.event.server.ServerStoppedEvent
//import net.neoforged.neoforge.event.server.ServerStoppingEvent
//import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
//
//@Mod(CobblemonExtras.MODID)
//class CobblemonExtrasForge {
//    init {
//        with(MOD_BUS) {
//            addListener(this@CobblemonExtrasForge::initialize)
//            addListener(this@CobblemonExtrasForge::serverInit)
//        }
//
//        with(NeoForge.EVENT_BUS) {
//            addListener(this@CobblemonExtrasForge::registerCommands)
//            addListener(this@CobblemonExtrasForge::onServerStopped)
//            addListener(this@CobblemonExtrasForge::onServerStopping)
//            addListener(this@CobblemonExtrasForge::onPlayerLogin)
//        }
//    }
//
//    private fun registerCommands(e: RegisterCommandsEvent) {
//        CobblemonExtras.registerCommands(e.dispatcher)
//    }
//
//    fun serverInit(event: FMLDedicatedServerSetupEvent) {
//    }
//
//    fun initialize(event: FMLCommonSetupEvent) {
//        CobblemonExtras.initialize()
//        CobblemonExtras.getLogger().info("CobblemonExtras NeoForge Init")
//    }
//
//    fun onServerStopped(event: ServerStoppedEvent?) {
//        CobblemonExtras.getLogger().error("CobblemonExtras Server Stopped")
//        CobblemonExtras.onShutdown()
//    }
//
//    fun onServerStopping(event: ServerStoppingEvent?) {
//        CobblemonExtras.getLogger().error("Server stopping, shutting down CobblemonExtras")
//        CobblemonExtras.onShutdown()
//    }
//
//    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
//        CobblemonExtras.eventHandler.onPlayerLogin(event.entity as ServerPlayer)
//    }
//
//}