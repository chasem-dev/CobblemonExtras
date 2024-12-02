package dev.chasem.cobblemonextras.neforge;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;


public class ForgeEventHandler {
    public void register() {
        NeoForge.EVENT_BUS.register(this);
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
        CobblemonExtras.INSTANCE.getEventHandler().onPlayerLogin((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer) {
            InteractionResultHolder<ItemStack> result = CobblemonExtras.INSTANCE.getEventHandler().onUseItem((ServerPlayer) event.getEntity(), event.getLevel(), event.getHand());
            if (result.getResult() == InteractionResult.FAIL) {
                event.setCanceled(true);
            }
        }
    }
}