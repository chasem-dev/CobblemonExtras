package dev.chasem.cobblemonextras.neforge;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
@Mod(CobblemonExtras.MODID)
public final class CobblemonExtrasForge {

    final ForgeEventHandler forgeEventHandler = new ForgeEventHandler();

    public CobblemonExtrasForge() {
        CobblemonExtras.INSTANCE.getLogger().info("CobblemonExtras NeoForge Starting...");
        CobblemonExtras.INSTANCE.initialize();
        NeoForge.EVENT_BUS.register(CobblemonExtrasForge.class);
        forgeEventHandler.register();
    }

    @SubscribeEvent
    public static void onCommandRegistration(final RegisterCommandsEvent event) {
        CobblemonExtras.INSTANCE.registerCommands(event.getDispatcher());
    }
}