package dev.chasem.cobblemonextras.events;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.server.network.ServerPlayerEntity;

public class CobblemonExtrasEventHandler {

    public void onPlayerLogin(ServerPlayerEntity player) {
        System.out.println(player.getName().getString() + " has logged in!");
        CobblemonExtras.INSTANCE.getShowcaseService().syncPlayers(new ServerPlayerEntity[]{player}, true);
    }

    public void onPlayerLogout(ServerPlayerEntity player) {
        System.out.println(player.getName().getString() + " has logged out!");
        CobblemonExtras.INSTANCE.getShowcaseService().syncPlayers(new ServerPlayerEntity[]{player}, true);
    }

}
