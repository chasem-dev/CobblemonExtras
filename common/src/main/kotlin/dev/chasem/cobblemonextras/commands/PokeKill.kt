package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.entity.EntityTypeTest

class PokeKill {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("pokekill")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKEKILL_PERMISSION) }
                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }
        )
    }

    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val serverWorld = ctx.getSource().server.overworld();
        val count = 0
        // Get all entities in world.
        val allPokemon = serverWorld.getEntities(EntityTypeTest.forClass(PokemonEntity::class.java), { pokemonEntity -> true });
        ctx.getSource().sendSystemMessage(Component.literal("Killing " + allPokemon.size + " Pokemon"))

        allPokemon.forEach { pokemonEntity: PokemonEntity ->
            if (!pokemonEntity.isBattling && pokemonEntity.owner == null) {
                pokemonEntity.setRemoved(Entity.RemovalReason.DISCARDED)
                pokemonEntity.kill()
            }
        }
        return 1
    }
}
