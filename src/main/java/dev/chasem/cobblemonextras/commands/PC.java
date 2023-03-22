package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermissions;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.api.storage.pc.link.PCLink;
import com.cobblemon.mod.common.api.storage.pc.link.PCLinkManager;
import com.cobblemon.mod.common.net.messages.client.storage.pc.OpenPCPacket;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class PC {

    public PC() {
        register();
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("pc")
                        .requires(Permissions.require("cobblemonextras.command.pc", 2))
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(this::execute)
            );
        });
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            PCStore playerPc = null;
            try {
                playerPc = Cobblemon.INSTANCE.getStorage().getPC(player.getUuid());
            } catch (NoPokemonStoreException e) {
                player.sendMessage(Text.of("Error accessing PC..."));
                return -1;
            }
            PCLinkManager.INSTANCE.addLink(new PCLink(playerPc, player.getUuid()));
            new OpenPCPacket(playerPc.getUuid()).sendToPlayer(player);
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }
}
