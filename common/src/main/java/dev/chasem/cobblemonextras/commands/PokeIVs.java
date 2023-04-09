package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.BattleSide;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class PokeIVs {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("pokeivs")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKEIVS_PERMISSION))
                        .executes(this::execute));
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        if (ctx.getSource().getPlayer() != null) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            PokemonBattle battle = BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player);

            if (battle == null) {
                player.sendMessage(Text.literal("You are not currently in a battle.").formatted(Formatting.RED));
                return 1;
            }

            BattleActor playerActor = battle.getActor(player.getUuid());
            BattleSide side = null;
            for (BattleActor battleActor : battle.getSide1().getActors()) {
                if (playerActor.getUuid().equals(battleActor.getUuid())) {
                    side = battle.getSide2(); // Player is on SIDE 1, get oppposing side 2.
                    break;
                }
            }
            if (side == null) {
                side = battle.getSide1();  // Player is on SIDE 2, get oppposing side 1.
            }

            for (ActiveBattlePokemon activeBattlePokemon : side.getActivePokemon()) {
                if (activeBattlePokemon.getBattlePokemon() != null) {
                    Pokemon pokemon = activeBattlePokemon.getBattlePokemon().getOriginalPokemon();

                    MutableText hoveredText = Text.literal("").fillStyle(Style.EMPTY.withUnderline(false));

                    MutableText header = pokemon.getDisplayName().formatted(Formatting.WHITE).styled(style -> style.withUnderline(true));

                    MutableText line1 = Text.literal("HP: ").formatted(Formatting.RED).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.HP))).formatted(Formatting.WHITE))
                            .append(Text.literal(" Atk: ").formatted(Formatting.BLUE).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.ATTACK))).formatted(Formatting.WHITE)))
                            .append(Text.literal(" Def: ").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.DEFENCE))).formatted(Formatting.WHITE)));

                    MutableText line2 = Text.literal("SpAtk: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_ATTACK))).formatted(Formatting.WHITE))
                            .append(Text.literal(" SpDef: ").formatted(Formatting.YELLOW).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_DEFENCE))).formatted(Formatting.WHITE)))
                            .append(Text.literal(" Spd: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPEED))).formatted(Formatting.WHITE)));

                    hoveredText.append(header).append(Text.literal("\n")).append(line1).append(Text.literal("\n")).append(line2);
                    List<Text> hoverableText = Texts.join(pokemon.getDisplayName().getWithStyle(Style.EMPTY.withColor(Formatting.GREEN)), Text.literal("")).copy()
                            .append(Text.literal(" ").styled(style -> style.withUnderline(false)))
                            .append(Text.literal("IVs").formatted(Formatting.YELLOW)).getWithStyle(Style.EMPTY
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoveredText)));

                    player.sendMessage(Text.literal("[").append(Texts.join(hoverableText, Text.of(""))).append("]"));
                } else {
                    player.sendMessage(Text.literal("You are not currently in a battle.").formatted(Formatting.RED));
                    return 1;
                }
            }
        } else {
            ctx.getSource().sendError(Text.of("Sorry, this is only for players."));
        }
        return 1;
    }
}