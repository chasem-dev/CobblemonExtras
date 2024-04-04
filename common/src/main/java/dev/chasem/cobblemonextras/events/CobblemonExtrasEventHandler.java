package dev.chasem.cobblemonextras.events;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class CobblemonExtrasEventHandler {

    public void onPlayerLogin(ServerPlayerEntity player) {
        if (CobblemonExtras.config.showcase.debug) {
            CobblemonExtras.INSTANCE.getLogger().info(player.getName().getString() + " has logged in! Waiting 10 seconds...");
        }
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {

            Executor delayed = CompletableFuture.delayedExecutor(10L, TimeUnit.SECONDS);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeAsync(() -> {
                if (CobblemonExtras.config.showcase.debug) {
                    CobblemonExtras.INSTANCE.getLogger().info("Syncing " + player.getName().getString() + " to showcase...");
                }
                CobblemonExtras.INSTANCE.getShowcaseService().syncPlayers(new ServerPlayerEntity[]{player}, CobblemonExtras.config.showcase.async);
                return "done";
            }, delayed);

        }
    }

    public void onPlayerLogout(ServerPlayerEntity player) {
//        System.out.println(player.getName().getString() + " has logged out!");
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            CobblemonExtras.INSTANCE.getShowcaseService().syncPlayers(new ServerPlayerEntity[]{player}, CobblemonExtras.config.showcase.async);
        }
    }

}
