package dev.chasem.cobblemonextras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import dev.chasem.cobblemonextras.screen.PokeSeeHandlerFactory;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PokeSee {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("pokesee")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESEE_PERMISSION))
                        .executes(this::self)
        );
        dispatcher.register(
                literal("pokeseeother")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKESEE_OTHER_PERMISSION))
                        .then(argument("player", EntityArgumentType.player())
                                .executes(this::other))
        );
    }

    private int self(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            player.openHandledScreen(new PokeSeeHandlerFactory());
        }
        return 1;
    }
    private int other(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            String otherPlayerName = ctx.getInput().split(" ")[1]; // ctx.getArgument("player", String.class);
            ServerPlayerEntity otherPlayer = ctx.getSource().getServer().getPlayerManager().getPlayer(otherPlayerName);
            if (otherPlayer != null) {
                player.openHandledScreen(new PokeSeeHandlerFactory(otherPlayer));
            }
        }
        return 1;
    }

}
