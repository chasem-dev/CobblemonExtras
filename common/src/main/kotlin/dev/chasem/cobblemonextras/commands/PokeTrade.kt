package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.menus.PokeTradeMenu
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import java.util.*
import java.util.function.Consumer

class PokeTrade {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("poketrade")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKETRADE_PERMISSION) }
                        .then(Commands.literal("accept").executes { ctx: CommandContext<CommandSourceStack> -> this.respond(ctx) })
        )
        dispatcher.register(
                Commands.literal("poketrade")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKETRADE_PERMISSION) }
                        .then(Commands.literal("deny").executes { ctx: CommandContext<CommandSourceStack> -> this.respond(ctx) })
        )
        dispatcher.register(
                Commands.literal("poketrade")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKETRADE_PERMISSION) }
                        .then(Commands.literal("cancel").executes { ctx: CommandContext<CommandSourceStack> -> this.respond(ctx) })
        )
        dispatcher.register(
                Commands.literal("poketrade")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKETRADE_PERMISSION) }
                        .then(Commands.argument<EntitySelector>("player", EntityArgument.player())
                                .executes { ctx: CommandContext<CommandSourceStack> -> this.createTrade(ctx) })
        )
    }

    var tradeSessions: HashMap<UUID, TradeSession?> = HashMap()

    inner class TradeSession(var trader1: ServerPlayer, var trader2: ServerPlayer) {
        var trader1UUID: UUID = trader1.uuid // In case of offline.
        var trader1Accept: Boolean = false
        var trader2UUID: UUID = trader2.uuid // In case of offline.

        var trader2Accept: Boolean = false
        var trader1Pokemon: Pokemon? = null
        var trader2Pokemon: Pokemon? = null
        var timestamp: Long = System.currentTimeMillis()
        var cancelled: Boolean = false

        fun cancel() {
            trader1.sendSystemMessage(Component.literal("Trade cancelled.").withStyle(ChatFormatting.RED))
            trader2.sendSystemMessage(Component.literal("Trade cancelled.").withStyle(ChatFormatting.RED))
            tradeSessions.remove(trader1UUID)
            tradeSessions.remove(trader2UUID)
            this.cancelled = true
        }

        fun deny() {
            trader1.sendSystemMessage(Component.literal("Trade declined.").withStyle(ChatFormatting.RED))
            trader2.sendSystemMessage(Component.literal("Trade declined.").withStyle(ChatFormatting.RED))
            tradeSessions.remove(trader1UUID)
            tradeSessions.remove(trader2UUID)
            this.cancelled = true
        }

        fun expire() {
            trader1.sendSystemMessage(Component.literal("Trade request expired.").withStyle(ChatFormatting.RED))
            trader2.sendSystemMessage(Component.literal("Trade request expired.").withStyle(ChatFormatting.RED))
            tradeSessions.remove(trader1UUID)
            tradeSessions.remove(trader2UUID)
            this.cancelled = true
        }

        fun accept() {
            val tradeMenu = PokeTradeMenu(this);
            trader1.openMenu(tradeMenu);
            trader2.openMenu(tradeMenu);
        }

        fun doTrade() {
            if (this.cancelled) {
                println("Something funky is goin' on")
                cancel()
                return
            }
            cancelled = true
            val party1 = Cobblemon.storage.getParty(trader1)
            val party2 = Cobblemon.storage.getParty(trader2)
            if (trader1Pokemon != null) {
                party1.remove(trader1Pokemon!!)
            }
            if (trader2Pokemon != null) {
                party2.remove(trader2Pokemon!!)
            }

            if (trader1Pokemon != null) {
                party2.add(trader1Pokemon!!)
                trader1Pokemon!!.evolutions.forEach(Consumer { evolution: Evolution ->
                    if (evolution is TradeEvolution) {
                        evolution.evolve(trader1Pokemon!!)
                    }
                })
            }
            if (trader2Pokemon != null) {
                party1.add(trader2Pokemon!!)
                trader2Pokemon!!.evolutions.forEach(Consumer { evolution: Evolution ->
                    if (evolution is TradeEvolution) {
                        evolution.evolve(trader2Pokemon!!)
                    }
                })
            }

            val toSend = Component.literal("Trade complete!").withStyle(ChatFormatting.GREEN)
            trader1.sendSystemMessage(toSend)
            trader2.sendSystemMessage(toSend)
            tradeSessions.remove(trader1UUID)
            tradeSessions.remove(trader2UUID)
        }
    }

    private fun createTrade(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.getSource().getPlayer() != null) {
            val player: ServerPlayer = ctx.source.player!!;

            if (tradeSessions.containsKey(player.uuid)) {
                val tradeSession = tradeSessions[player.uuid]
                val timeSince = System.currentTimeMillis() - tradeSession!!.timestamp
                if (timeSince > 1000 * 60) {
                    // Expire sender's trade session.
                    tradeSession.expire()
                } else {
                    val cancel = Component.literal("[CANCEL]")
                            .withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.RED)
                                    .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poketrade cancel"))
                                    .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Cancel Trade"))));

                    player.sendSystemMessage(Component.literal("You have a trade pending. Cancel your last before creating a new trade.")
                            .withStyle(ChatFormatting.RED).append(" ").append(cancel))

                    return 1
                }
            }

            val tradePartnerPlayer: ServerPlayer = EntityArgument.getPlayer(ctx, "player")

            if (tradePartnerPlayer.uuid.equals(player.uuid)) {
                ctx.getSource().sendFailure(Component.literal("Trading yourself? Your worth more than that <3"))
                return 1
            }

            if (tradeSessions.containsKey(tradePartnerPlayer.uuid)) {
                val tradeSession = tradeSessions[tradePartnerPlayer.uuid]
                val timeSince = System.currentTimeMillis() - tradeSession!!.timestamp
                if (timeSince > 1000 * 60) {
                    // Expire trade partner's trade session.
                    tradeSession.expire()
                } else {
                    player.sendSystemMessage(Component.literal("Trade partner already has a trade pending, they must cancel or complete their trade before starting a new one.").withStyle(ChatFormatting.RED))
                    return 1
                }
            }

            val tradeSession = TradeSession(player, tradePartnerPlayer)
            tradeSessions[tradePartnerPlayer.uuid] = tradeSession
            tradeSessions[player.uuid] = tradeSession
            player.sendSystemMessage(Component.literal("Trade request sent.").withStyle(ChatFormatting.YELLOW))
            tradePartnerPlayer.sendSystemMessage(Component.literal("Pokemon trade request received from ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal("${player.name}. ").withStyle(ChatFormatting.GREEN)))

            val accept = Component.literal("[ACCEPT]")
                    .withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN)
                            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poketrade accept"))
                            .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Accept Trade"))));

            val deny = Component.literal("[DENY]")
                    .withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.RED)
                            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poketrade deny"))
                            .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Deny Trade"))));
            tradePartnerPlayer.sendSystemMessage(accept.copy().append(" ").append(deny))
        }
        return 1
    }


    private fun respond(ctx: CommandContext<CommandSourceStack>): Int {
        if (ctx.getSource().getPlayer() != null) {
            val response: String = ctx.getInput().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().get(1)
            val player: ServerPlayer = ctx.source.player!!;

            val tradeSession = tradeSessions.getOrDefault(player.uuid, null)
            if (tradeSession == null) {
                player.sendSystemMessage(Component.literal("No pending trade session.").withStyle(ChatFormatting.YELLOW))
                return 1
            }

            if (response.equals("cancel", ignoreCase = true)) {
                tradeSession.cancel()
            } else if (response.equals("deny", ignoreCase = true)) {
                if (tradeSession.trader2UUID == player.uuid) {
                    tradeSession.deny()
                }
            } else if (response.equals("accept", ignoreCase = true)) {
                if (tradeSession.trader2UUID == player.uuid) { // The INVITED user (trader2) accepted.
                    tradeSession.accept()
                }
            }
        }
        return 1
    }
}
