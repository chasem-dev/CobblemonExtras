package dev.chasem.cobblemonextras.events;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.server.network.ServerPlayerEntity;

public class CobblemonExtrasEventHandler {

    public void onPlayerLogin(ServerPlayerEntity player) {
//        System.out.println(player.getName().getString() + " has logged in!");
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            CobblemonExtras.INSTANCE.getShowcaseService().syncPlayers(new ServerPlayerEntity[]{player}, CobblemonExtras.config.showcase.async);
        }
    }

    public void onPlayerLogout(ServerPlayerEntity player) {
//        System.out.println(player.getName().getString() + " has logged out!");
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            CobblemonExtras.INSTANCE.getShowcaseService().syncPlayers(new ServerPlayerEntity[]{player}, CobblemonExtras.config.showcase.async);
        }
    }

}
