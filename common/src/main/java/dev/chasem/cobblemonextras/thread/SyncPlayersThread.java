package dev.chasem.cobblemonextras.thread;

import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncPlayersThread extends Thread {
        ServerPlayerEntity[] players;
        public SyncPlayersThread(ServerPlayerEntity[] players) {
            super("CobblemonExtrasSyncPlayersThread");
            this.players = players;
        }

        @Override
        public void run() {
            CobblemonExtras.INSTANCE.getShowcaseService().syncPlayers(players, CobblemonExtras.config.showcase.async);
            this.interrupt();

        }
}
