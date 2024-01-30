package dev.chasem.cobblemonextras.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.api.storage.pc.link.PCLink;
import com.cobblemon.mod.common.api.storage.pc.link.PCLinkManager;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.storage.pc.OpenPCPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class PokeKill {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("pokekill")
                        .requires(src -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKEKILL_PERMISSION))
                            .executes(this::execute)
        );
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        ServerWorld serverWorld = ctx.getSource().getWorld();
        final int count = 0;
        // Get all entities in world.
        List<? extends PokemonEntity> allPokemon = serverWorld.getEntitiesByType(CobblemonEntities.POKEMON, pokemonEntity -> true);
        ctx.getSource().sendMessage(Text.of("Killing " + allPokemon.size() + " Pokemon"));
        allPokemon.forEach(pokemonEntity -> {
            // Kill all entities.
            pokemonEntity.setRemoved(Entity.RemovalReason.DISCARDED);
            pokemonEntity.kill();
        });
        return 1;
    }
}
