package dev.chasem.cobblemonextras.forge.event;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventHandler {
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
        CobblemonExtras.INSTANCE.getLogger().info("Registered CobblemonExtras Forge Event Handler");
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event) {
        CobblemonExtras.INSTANCE.onShutdown();
    }
}
