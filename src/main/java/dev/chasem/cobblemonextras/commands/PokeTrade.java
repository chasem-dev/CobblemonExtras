package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.config.CobblemonExtrasConfig;
import dev.chasem.cobblemonextras.screen.PokeSeeHandlerFactory;
import dev.chasem.cobblemonextras.screen.PokeTradeHandlerFactory;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PokeTrade {

    public PokeTrade() {
        register();
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("poketrade")
                            .requires(Permissions.require("cobblemonextras.command.poketrade", CobblemonExtrasConfig.COMMAND_POKETRADE_PERMISSION_LEVEL))
                            .then(literal("accept").executes(this::respond))
            );
            dispatcher.register(
                    literal("poketrade")
                            .requires(Permissions.require("cobblemonextras.command.poketrade", CobblemonExtrasConfig.COMMAND_POKETRADE_PERMISSION_LEVEL))
                            .then(literal("deny").executes(this::respond))
            );
            dispatcher.register(
                    literal("poketrade")
                            .requires(Permissions.require("cobblemonextras.command.poketrade", CobblemonExtrasConfig.COMMAND_POKETRADE_PERMISSION_LEVEL))
                            .then(literal("cancel").executes(this::respond))
            );
            dispatcher.register(
                    literal("poketrade")
                            .requires(Permissions.require("cobblemonextras.command.poketrade", CobblemonExtrasConfig.COMMAND_POKETRADE_PERMISSION_LEVEL))
                            .then(argument("player", EntityArgumentType.player())
                                    .executes(this::createTrade))
            );
        });
    }


    public HashMap<UUID, TradeSession> tradeSessions = new HashMap<>();

    public class TradeSession {
        public ServerPlayerEntity trader1;
        UUID trader1UUID; // In case of offline.
        public boolean trader1Accept = false;
        public ServerPlayerEntity trader2;
        UUID trader2UUID; // In case of offline.

        public boolean trader2Accept = false;
        public Pokemon trader1Pokemon;
        public Pokemon trader2Pokemon;
        long timestamp;
        public boolean cancelled = false;

        public TradeSession(ServerPlayerEntity trader1, ServerPlayerEntity trader2) {
            this.trader1 = trader1;
            this.trader2 = trader2;
            this.trader1UUID = trader1.getUuid();
            this.trader2UUID = trader2.getUuid();
            this.timestamp = System.currentTimeMillis();
        }

        public void cancel() {
            trader1.sendMessage(Text.literal("Trade cancelled.").formatted(Formatting.RED));
            trader2.sendMessage(Text.literal("Trade cancelled.").formatted(Formatting.RED));
            tradeSessions.remove(trader1UUID);
            tradeSessions.remove(trader2UUID);
            this.cancelled = true;
        }
        public void deny() {
            trader1.sendMessage(Text.literal("Trade declined.").formatted(Formatting.RED));
            trader2.sendMessage(Text.literal("Trade declined.").formatted(Formatting.RED));
            tradeSessions.remove(trader1UUID);
            tradeSessions.remove(trader2UUID);
            this.cancelled = true;
        }

        public void expire() {
            trader1.sendMessage(Text.literal("Trade request expired.").formatted(Formatting.RED));
            trader2.sendMessage(Text.literal("Trade request expired.").formatted(Formatting.RED));
            tradeSessions.remove(trader1UUID);
            tradeSessions.remove(trader2UUID);
            this.cancelled = true;
        }

        public void accept() {
            PokeTradeHandlerFactory tradeHandler = new PokeTradeHandlerFactory(this);
            trader1.openHandledScreen(tradeHandler);
            trader2.openHandledScreen(tradeHandler);
        }

        public void doTrade() {
            if (this.cancelled) {
                System.out.println("Something funky is goin' on");
                cancel();
                return;
            }
            cancelled = true;
            PlayerPartyStore party1 = Cobblemon.INSTANCE.getStorage().getParty(trader1);
            PlayerPartyStore party2 = Cobblemon.INSTANCE.getStorage().getParty(trader2);
            if (trader1Pokemon != null) {
                party1.remove(trader1Pokemon);
            }
            if (trader2Pokemon != null) {
                party2.remove(trader2Pokemon);
            }

            if (trader1Pokemon != null) {
                party2.add(trader1Pokemon);
                trader1Pokemon.getEvolutions().forEach(evolution -> {
                    if (evolution instanceof TradeEvolution) {
                        evolution.evolve(trader1Pokemon);
                    }
                });
            }
            if (trader2Pokemon != null) {
                party1.add(trader2Pokemon);
                trader2Pokemon.getEvolutions().forEach(evolution -> {
                    if (evolution instanceof TradeEvolution) {
                        evolution.evolve(trader2Pokemon);
                    }
                });
            }

            Text toSend = Text.literal("Trade complete!").formatted(Formatting.GREEN);
            trader1.sendMessage(toSend);
            trader2.sendMessage(toSend);
            tradeSessions.remove(trader1UUID);
            tradeSessions.remove(trader2UUID);

        }

    }

    private int createTrade(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {

            ServerPlayerEntity player = ctx.getSource().getPlayer();

            if (tradeSessions.containsKey(player.getUuid())) {
                TradeSession tradeSession = tradeSessions.get(player.getUuid());
                long timeSince = System.currentTimeMillis() - tradeSession.timestamp;
                if (timeSince > 1000 * 60) {
                    // Expire sender's trade session.
                    tradeSession.expire();
                } else {
                    Text cancel = Texts.join(Text.literal("[CANCEL]")
                            .getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.DARK_RED)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poketrade cancel"))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Cancel Trade")))
                            ), Text.of(""));

                    player.sendMessage(Text.literal("You have a trade pending. Cancel your last before creating a new trade.")
                            .formatted(Formatting.RED).append(" ").append(cancel));

                    return 1;
                }
            }

            ServerPlayerEntity tradePartnerPlayer;
            try {
                tradePartnerPlayer = EntityArgumentType.getPlayer(ctx, "player");
            } catch (CommandSyntaxException e) {
                ctx.getSource().sendError(Text.of("Error finding player."));
                return 1;
            }

            if (tradePartnerPlayer.getUuid().equals(player.getUuid())) {
                ctx.getSource().sendError(Text.of("Trading yourself? Your worth more than that <3"));
                return 1;
            }

            if (tradeSessions.containsKey(tradePartnerPlayer.getUuid())) {
                TradeSession tradeSession = tradeSessions.get(tradePartnerPlayer.getUuid());
                long timeSince = System.currentTimeMillis() - tradeSession.timestamp;
                if (timeSince > 1000 * 60) {
                    // Expire trade partner's trade session.
                    tradeSession.expire();
                } else {
                    player.sendMessage(Text.literal("Trade partner already has a trade pending, they must cancel or complete their trade before starting a new one.").formatted(Formatting.RED));
                    return 1;
                }
            }

            TradeSession tradeSession = new TradeSession(player, tradePartnerPlayer);
            tradeSessions.put(tradePartnerPlayer.getUuid(), tradeSession);
            tradeSessions.put(player.getUuid(), tradeSession);
            player.sendMessage(Text.literal("Trade request sent.").formatted(Formatting.YELLOW));
            tradePartnerPlayer.sendMessage(Text.literal("Pokemon trade request received from ").formatted(Formatting.YELLOW)
                    .append(Text.literal(player.getEntityName() + ". ").formatted(Formatting.GREEN)));

            Text accept = Texts.join(Text.literal("[ACCEPT]")
                    .getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poketrade accept"))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Accept Trade")))
                    ), Text.of(""));

            Text deny = Texts.join(Text.literal("[DENY]").formatted(Formatting.RED)
                    .getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poketrade deny"))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Deny Trade")))
                    ), Text.of(""));
            tradePartnerPlayer.sendMessage(accept.copy().append(" ").append(deny));
        }
        return 1;
    }


    private int respond(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            String response = ctx.getInput().split(" ")[1];
            ServerPlayerEntity player = ctx.getSource().getPlayer();

            TradeSession tradeSession = this.tradeSessions.getOrDefault(player.getUuid(), null);
            if (tradeSession == null) {
                player.sendMessage(Text.literal("No pending trade session.").formatted(Formatting.YELLOW));
                return 1;
            }

            if (response.equalsIgnoreCase("cancel")) {
                tradeSession.cancel();
            } else if (response.equalsIgnoreCase("deny")) {
                if (tradeSession.trader2UUID.equals(player.getUuid())) {
                    tradeSession.deny();
                }
            } else if (response.equalsIgnoreCase("accept")) {
                if (tradeSession.trader2UUID.equals(player.getUuid())) { // The INVITED user (trader2) accepted.
                    tradeSession.accept();
                }
            }
        }
        return 1;
    }
}
