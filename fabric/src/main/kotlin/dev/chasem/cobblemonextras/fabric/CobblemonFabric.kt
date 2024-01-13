package dev.chasem.cobblemonextras.fabric

import dev.chasem.cobblemonextras.CobblemonExtras
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

class CobblemonFabric : ModInitializer {
    override fun onInitialize() {
        System.out.println("Fabric Mod init")
        CobblemonExtras.initialize();
        CommandRegistrationCallback.EVENT.register(CobblemonExtras::registerCommands)
    }
}