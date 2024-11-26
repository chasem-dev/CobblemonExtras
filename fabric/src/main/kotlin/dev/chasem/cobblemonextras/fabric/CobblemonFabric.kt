package dev.chasem.cobblemonextras.fabric

import dev.chasem.cobblemonextras.CobblemonExtras
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents

class CobblemonFabric : ModInitializer {
    override fun onInitialize() {
        CobblemonExtras.getLogger().info("Fabric Mod init")
        CobblemonExtras.initialize();
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            CobblemonExtras.registerCommands(dispatcher)
        }
        ServerLifecycleEvents.SERVER_STOPPING.register { CobblemonExtras.onShutdown() }
        ServerLifecycleEvents.SERVER_STOPPED.register { CobblemonExtras.onShutdown() }
        ServerPlayConnectionEvents.JOIN.register { serverPlayNetworkHandler, _, _ ->
            CobblemonExtras.eventHandler.onPlayerLogin(serverPlayNetworkHandler.getPlayer())
        }
    }
}