package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.api.storage.pc.link.PCLink;
import com.cobblemon.mod.common.api.storage.pc.link.PCLinkManager;
import com.cobblemon.mod.common.net.messages.client.storage.pc.OpenPCPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EmptyBox {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("emptybox")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.EMPTYBOX_PERMISSION))
                        .then(argument("box", IntegerArgumentType.integer(1, Cobblemon.config.getDefaultBoxCount()))
                        .executes(this::execute))
        );
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
            Integer boxNum = ctx.getArgument("box", Integer.class) - 1;
            PCBox box = playerPc.getBoxes().get(boxNum);
            if (box == null) {
                player.sendMessage(Text.of("Error accessing box... " + boxNum));
                return -1;
            }
            for (int i = 0; i < 30; i++) {
                playerPc.remove(new PCPosition(boxNum, i));
            }
            player.sendMessage(Text.of("Box " + (boxNum + 1) + " has been emptied."));
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }
}
