package dev.chasem.cobblemonextras.events

import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.CobblemonExtras.getLogger
import dev.chasem.cobblemonextras.CobblemonExtras.showcaseService
import net.minecraft.server.level.ServerPlayer
import java.util.List
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class CobblemonExtrasEventHandler {
    fun onPlayerLogin(player: ServerPlayer) {
        if (CobblemonExtras.config.showcase.debug) {
            getLogger().info(player.name.string + " has logged in! Waiting 10 seconds...")
        }
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            val delayed = CompletableFuture.delayedExecutor(10L, TimeUnit.SECONDS)
            val future = CompletableFuture<String>()
            future.completeAsync({
                if (CobblemonExtras.config.showcase.debug) {
                    getLogger().info("Syncing " + player.name.string + " to showcase...")
                }
                showcaseService.syncPlayers(List.of(player))
                "done"
            }, delayed)
        }
    }

    fun onPlayerLogout(player: ServerPlayer) {
//        System.out.println(player.getName().getString() + " has logged out!");
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            showcaseService.syncPlayers(List.of(player))
        }
    }
}
