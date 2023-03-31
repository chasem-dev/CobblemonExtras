package dev.chasem.cobblemonextras.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

public class PokemonUtility {


    public static Text getHoverText(MutableText toSend, Pokemon pokemon) {
        MutableText statsHoverText = Text.literal("").fillStyle(Style.EMPTY);
        statsHoverText.append(pokemon.getDisplayName().setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN).withUnderline(true)));
        if (pokemon.getShiny()) {
            statsHoverText.append(Text.literal(" ★").formatted(Formatting.GOLD));
        }
        statsHoverText.append(Text.literal("\n"));
        statsHoverText.append(Text.literal("Level: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getLevel())).formatted(Formatting.WHITE)));
        statsHoverText.append(Text.literal("\n"));
        statsHoverText.append(Text.literal("Nature: ").formatted(Formatting.YELLOW).append(LocalizationUtilsKt.lang(pokemon.getNature().getDisplayName().replace("cobblemon.", "")).formatted(Formatting.WHITE)));
        statsHoverText.append(Text.literal("\n"));
        statsHoverText.append(Text.literal("Ability: ").formatted(Formatting.GOLD).append(LocalizationUtilsKt.lang(pokemon.getAbility().getDisplayName().replace("cobblemon.", "")).formatted(Formatting.WHITE)));
        statsHoverText.append(Text.literal("\n"));
        statsHoverText.append(Text.literal("Form: ").formatted(Formatting.GREEN).append(Text.literal(pokemon.getForm().getName()).formatted(Formatting.WHITE)));

        Text statsText = Texts.join(Text.literal("[Stats]").formatted(Formatting.RED)
                .getWithStyle(Style.EMPTY.withColor(Formatting.RED)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, statsHoverText))), Text.of(""));

        toSend.append(statsText);

        MutableText evsText = Text.literal(" [EVs]").formatted(Formatting.GOLD);
        MutableText evsHoverText = Text.literal("");
        List<Text> evsHoverTextList = Text.literal("EVs").formatted(Formatting.GOLD).getWithStyle(Style.EMPTY.withUnderline(Boolean.TRUE));
        evsHoverTextList.add(Text.literal("\n"));
        evsHoverTextList.add(Text.literal("HP: ").formatted(Formatting.RED).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.HP))).formatted(Formatting.WHITE)));
        evsHoverTextList.add(Text.literal("\n"));
        evsHoverTextList.add(Text.literal("Attack: ").formatted(Formatting.BLUE).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.ATTACK))).formatted(Formatting.WHITE)));
        evsHoverTextList.add(Text.literal("\n"));
        evsHoverTextList.add(Text.literal("Defense: ").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.DEFENCE))).formatted(Formatting.WHITE)));
        evsHoverTextList.add(Text.literal("\n"));
        evsHoverTextList.add(Text.literal("Sp. Attack: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_ATTACK))).formatted(Formatting.WHITE)));
        evsHoverTextList.add(Text.literal("\n"));
        evsHoverTextList.add(Text.literal("Sp. Defense: ").formatted(Formatting.YELLOW).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_DEFENCE))).formatted(Formatting.WHITE)));
        evsHoverTextList.add(Text.literal("\n"));
        evsHoverTextList.add(Text.literal("Speed: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPEED))).formatted(Formatting.WHITE)));

        evsHoverTextList.forEach(evsHoverText::append);
        List<Text> evsList = evsText.getWithStyle(evsText.getStyle().withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        evsHoverText
                ))
        );
        evsList.forEach(toSend::append);

        MutableText ivsText = Text.literal(" [IVs]").formatted(Formatting.LIGHT_PURPLE);
        MutableText ivsHoverText = Text.literal("");
        List<Text> ivsHoverTextList = Text.literal("IVs").formatted(Formatting.GOLD).getWithStyle(Style.EMPTY.withUnderline(Boolean.TRUE));
        ivsHoverTextList.add(Text.literal("\n"));
        ivsHoverTextList.add(Text.literal("HP: ").formatted(Formatting.RED).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.HP))).formatted(Formatting.WHITE)));
        ivsHoverTextList.add(Text.literal("\n"));
        ivsHoverTextList.add(Text.literal("Attack: ").formatted(Formatting.BLUE).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.ATTACK))).formatted(Formatting.WHITE)));
        ivsHoverTextList.add(Text.literal("\n"));
        ivsHoverTextList.add(Text.literal("Defense: ").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.DEFENCE))).formatted(Formatting.WHITE)));
        ivsHoverTextList.add(Text.literal("\n"));
        ivsHoverTextList.add(Text.literal("Sp. Attack: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_ATTACK))).formatted(Formatting.WHITE)));
        ivsHoverTextList.add(Text.literal("\n"));
        ivsHoverTextList.add(Text.literal("Sp. Defense: ").formatted(Formatting.YELLOW).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_DEFENCE))).formatted(Formatting.WHITE)));
        ivsHoverTextList.add(Text.literal("\n"));
        ivsHoverTextList.add(Text.literal("Speed: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPEED))).formatted(Formatting.WHITE)));

        ivsHoverTextList.forEach(ivsHoverText::append);
        List<Text> ivsList = ivsText.getWithStyle(ivsText.getStyle().withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        ivsHoverText
                ))
        );
        ivsList.forEach(toSend::append);

        MutableText movesText = Text.literal(" [Moves]").formatted(Formatting.BLUE);
        MutableText movesHoverText = Text.literal("");
        List<Text> movesHoverTextList = Text.literal("Moves").formatted(Formatting.BLUE).getWithStyle(Style.EMPTY.withUnderline(Boolean.TRUE));
        movesHoverTextList.add(Text.literal("\n"));
        String moveOne = pokemon.getMoveSet().getMoves().size() >= 1 ? pokemon.getMoveSet().get(0).getDisplayName().getString() : "Empty";
        String moveTwo = pokemon.getMoveSet().getMoves().size() >= 2 ? pokemon.getMoveSet().get(1).getDisplayName().getString() : "Empty";
        String moveThree = pokemon.getMoveSet().getMoves().size() >= 3 ? pokemon.getMoveSet().get(2).getDisplayName().getString() : "Empty";
        String moveFour = pokemon.getMoveSet().getMoves().size() >= 4 ? pokemon.getMoveSet().get(3).getDisplayName().getString() : "Empty";
        movesHoverTextList.add(Text.literal("Move 1: ").formatted(Formatting.RED).append(Text.literal(moveOne).formatted(Formatting.WHITE)));
        movesHoverTextList.add(Text.literal("\n"));
        movesHoverTextList.add(Text.literal("Move 2: ").formatted(Formatting.YELLOW).append(Text.literal(moveTwo).formatted(Formatting.WHITE)));
        movesHoverTextList.add(Text.literal("\n"));
        movesHoverTextList.add(Text.literal("Move 3: ").formatted(Formatting.AQUA).append(Text.literal(moveThree).formatted(Formatting.WHITE)));
        movesHoverTextList.add(Text.literal("\n"));
        movesHoverTextList.add(Text.literal("Move 4: ").formatted(Formatting.GREEN).append(Text.literal(moveFour).formatted(Formatting.WHITE)));

        movesHoverTextList.forEach(movesHoverText::append);
        List<Text> movesList = movesText.getWithStyle(movesText.getStyle().withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        movesHoverText
                ))
        );
        movesList.forEach(toSend::append);
        return toSend;
    }

    public static ItemStack pokemonToItem(Pokemon pokemon) {
        String moveOne = pokemon.getMoveSet().getMoves().size() >= 1 ? pokemon.getMoveSet().get(0).getDisplayName().getString() : "Empty";
        String moveTwo = pokemon.getMoveSet().getMoves().size() >= 2 ? pokemon.getMoveSet().get(1).getDisplayName().getString() : "Empty";
        String moveThree = pokemon.getMoveSet().getMoves().size() >= 3 ? pokemon.getMoveSet().get(2).getDisplayName().getString() : "Empty";
        String moveFour = pokemon.getMoveSet().getMoves().size() >= 4 ? pokemon.getMoveSet().get(3).getDisplayName().getString() : "Empty";

        ItemStack itemstack = new ItemBuilder(PokemonItem.from(pokemon, 1))
                .hideAdditional()
                .addLore(new Text[]{
                        pokemon.getCaughtBall().item().getName().copy().setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.DARK_GRAY)),
                        Text.literal("Level: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getLevel())).formatted(Formatting.WHITE)),
                        Text.literal("Nature: ").formatted(Formatting.YELLOW).append(LocalizationUtilsKt.lang(pokemon.getNature().getDisplayName().replace("cobblemon.", "")).formatted(Formatting.WHITE)),
                        Text.literal("Ability: ").formatted(Formatting.GOLD).append(LocalizationUtilsKt.lang(pokemon.getAbility().getDisplayName().replace("cobblemon.", "")).formatted(Formatting.WHITE)),
                        Text.literal("IVs: ").formatted(Formatting.LIGHT_PURPLE),
                        Text.literal("  HP: ").formatted(Formatting.RED).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.HP))).formatted(Formatting.WHITE))
                                .append(Text.literal("  Atk: ").formatted(Formatting.BLUE).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.ATTACK))).formatted(Formatting.WHITE)))
                                .append(Text.literal("  Def: ").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.DEFENCE))).formatted(Formatting.WHITE))),
                        Text.literal("  SpAtk: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_ATTACK))).formatted(Formatting.WHITE))
                                .append(Text.literal("  SpDef: ").formatted(Formatting.YELLOW).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_DEFENCE))).formatted(Formatting.WHITE)))
                                .append(Text.literal("  Spd: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPEED))).formatted(Formatting.WHITE))),

                        Text.literal("EVs: ").formatted(Formatting.DARK_AQUA),
                        Text.literal("  HP: ").formatted(Formatting.RED).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.HP))).formatted(Formatting.WHITE))
                                .append(Text.literal("  Atk: ").formatted(Formatting.BLUE).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.ATTACK))).formatted(Formatting.WHITE)))
                                .append(Text.literal("  Def: ").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.DEFENCE))).formatted(Formatting.WHITE))),
                        Text.literal("  SpAtk: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_ATTACK))).formatted(Formatting.WHITE))
                                .append(Text.literal("  SpDef: ").formatted(Formatting.YELLOW).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_DEFENCE))).formatted(Formatting.WHITE)))
                                .append(Text.literal("  Spd: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPEED))).formatted(Formatting.WHITE))),
                        Text.literal("Moves: ").formatted(Formatting.DARK_GREEN),
                        Text.literal(" ").append(Text.literal(moveOne).formatted(Formatting.WHITE)),
                        Text.literal(" ").append(Text.literal(moveTwo).formatted(Formatting.WHITE)),
                        Text.literal(" ").append(Text.literal(moveThree).formatted(Formatting.WHITE)),
                        Text.literal(" ").append(Text.literal(moveFour).formatted(Formatting.WHITE)),
                        Text.literal("Form: ").formatted(Formatting.GOLD).append(pokemon.getForm().getName())
                })
                .setCustomName(
                        pokemon.getShiny() ?
                                pokemon.getDisplayName().formatted(Formatting.GRAY).append(Text.literal(" ★").formatted(Formatting.GOLD)) :
                                pokemon.getDisplayName().formatted(Formatting.GRAY)
                )
                .build();
        return itemstack;
    }
}
