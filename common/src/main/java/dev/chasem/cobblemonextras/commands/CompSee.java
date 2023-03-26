package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.CobblemonPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import dev.chasem.cobblemonextras.screen.CompSeeHandlerFactory;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CompSee {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("compsee")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.COMPSEE_PERMISSION))
                        .executes(this::self)
        );
        dispatcher.register(
                literal("compseeother")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.COMPESEE_OTHER_PERMISSION))
//                        .requires(Permissions.require("cobblemonextras.command.compseeother", CobblemonExtrasConfig.COMMAND_COMPSEEOTHER_PERMISSION_LEVEL))
                        .then(argument("player", EntityArgumentType.player())
                                .executes(this::other)));

    }

    private int self(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            player.openHandledScreen(new CompSeeHandlerFactory());
        }
        return 1;
    }

    private int other(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            String otherPlayerName = ctx.getInput().split(" ")[1]; // ctx.getArgument("player", String.class);
            ServerPlayerEntity otherPlayer = ctx.getSource().getServer().getPlayerManager().getPlayer(otherPlayerName);
            if (otherPlayer != null) {
                player.openHandledScreen(new CompSeeHandlerFactory(otherPlayer));
            }
        }
        return 1;
    }

}
