package dev.chasem.cobblemonextras.commands

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.storage
import com.cobblemon.mod.common.battles.BattleBuilder.pve
import com.cobblemon.mod.common.battles.BattleFormat
import com.cobblemon.mod.common.battles.BattleRegistry.getBattleByParticipatingPlayer
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType.Companion.getPokemonProperties
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType.Companion.properties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import java.util.*

class PokeBattle {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
                Commands.literal("pokebattle")
                        .requires { src: CommandSourceStack? -> CobblemonExtrasPermissions.checkPermission(src, CobblemonExtras.permissions.POKEBATTLE_PERMISSION) }
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("properties", properties())
                                        .executes { ctx: CommandContext<CommandSourceStack> -> this.execute(ctx) }))
        )
    }


    private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val battlingPlayer: ServerPlayer
        try {
            battlingPlayer = EntityArgument.getPlayer(ctx, "player")
        } catch (e: CommandSyntaxException) {
            ctx.source.sendFailure(Component.literal("Error finding player."))
            return 1
        }
        val pokemonProperties = getPokemonProperties(ctx, "properties")
        if (pokemonProperties.species == null) {
            ctx.source.sendSystemMessage(Component.literal("No pokemon found for species provided"))
            return 1
        }

        if (getBattleByParticipatingPlayer(battlingPlayer) != null) {
            ctx.source.sendSystemMessage(Component.literal("Player is in an active battle.").withStyle(ChatFormatting.RED))
            return 1
        }

        val party = storage.getParty(battlingPlayer)
        var pokemonUUID: UUID? = null
        for (pokemon in party) {
            if (!pokemon.isFainted()) {
                pokemonUUID = pokemon.uuid
            }
        }

        if (pokemonUUID == null) {
            ctx.source.sendSystemMessage(Component.literal("Player unable to battle, no available pokemon in their party.").withStyle(ChatFormatting.RED))
            return 1
        }

        val world = ctx.source.level
        val pokemonEntity = pokemonProperties.createEntity(world)
        pokemonEntity.moveTo(battlingPlayer.x, battlingPlayer.y, battlingPlayer.z, pokemonEntity.yRot, pokemonEntity.xRot)
        pokemonEntity.entityData.set(PokemonEntity.SPAWN_DIRECTION, pokemonEntity.random.nextFloat() * 360F)
        if (world.addFreshEntity(pokemonEntity)) {
            pokemonEntity.teleportTo(battlingPlayer.x, battlingPlayer.y, battlingPlayer.z)
            pve(battlingPlayer, pokemonEntity, pokemonUUID, BattleFormat.Companion.GEN_9_SINGLES, false, false, Cobblemon.config.defaultFleeDistance, party)
        } else {
            ctx.source.sendFailure(Component.literal("Failed to spawn Pokemon in world for player..."))
        }
        return 1
    }
}
