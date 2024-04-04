package dev.chasem.cobblemonextras.forge.event;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventHandler {
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
        CobblemonExtras.INSTANCE.getLogger().info("Registered CobblemonExtras Forge Event Handler");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerStopped(ServerStoppedEvent event) {
        CobblemonExtras.INSTANCE.getLogger().error("CobblemonExtras Server Stopped");
        CobblemonExtras.INSTANCE.onShutdown();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerStopping(ServerStoppingEvent event) {
        CobblemonExtras.INSTANCE.getLogger().error("Server stopping, shutting down CobblemonExtras");
        CobblemonExtras.INSTANCE.onShutdown();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        CobblemonExtras.INSTANCE.getEventHandler().onPlayerLogin((ServerPlayerEntity) event.getEntity());
    }

//    @SubscribeEvent
//    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
//        CobblemonExtras.INSTANCE.getEventHandler().onPlayerLogout((ServerPlayerEntity) event.getEntity());
//    }
}
