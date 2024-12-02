package dev.chasem.cobblemonextras.events

import com.cobblemon.mod.common.CobblemonItems
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.CobblemonExtras.getLogger
import dev.chasem.cobblemonextras.CobblemonExtras.showcaseService
import net.minecraft.server.level.ServerPlayer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.pokeball.catching.CaptureEffect
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment.Server
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import kotlin.math.cos

class CobblemonExtrasEventHandler {
    fun onPlayerLogin(player: ServerPlayer) {
        if (CobblemonExtras.config.showcase.debug) {
            getLogger().info(player.name.string + " has logged in! Waiting 10 seconds...")
        }
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            val delayed = CompletableFuture.delayedExecutor(10L, TimeUnit.SECONDS)
            val future = CompletableFuture<String>()
            future.completeAsync({
                if (CobblemonExtras.config.showcase.debug) {
                    getLogger().info("Syncing " + player.name.string + " to showcase...")
                }
                showcaseService.syncPlayers(listOf(player))
                "done"
            }, delayed)
        }
    }

    fun onPlayerLogout(player: ServerPlayer) {
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            showcaseService.syncPlayers(listOf(player))
        }
    }


    fun onPokemonCapture(event: PokemonCapturedEvent) {
        getLogger().info("Pokemon Captured Event!")
        val pokemon = event.pokemon
        val pokeBallEntity = event.pokeBallEntity
        if (pokeBallEntity.tags.contains("shinyBall")) {
            getLogger().info("Shiny Ball Capture Effect!")
            pokemon.shiny = true
        }
    }

    fun onUseItem(player: ServerPlayer, world: Level, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(hand)
        getLogger().info("onUseItem()")
        if (itemStack.item == CobblemonItems.POKE_BALL) {
            if (itemStack.has(DataComponents.CUSTOM_DATA)) {
                getLogger().info("PokeBall has custom data!")
                val customData = itemStack.get(DataComponents.CUSTOM_DATA)
                val tag = customData?.copyTag() ?: CompoundTag()
                if (tag.contains("CobblemonExtrasBallType")) {
                    val ballType = tag.getString("CobblemonExtrasBallType")
                    if (ballType == "shiny") {
                        getLogger().info("Shiny Ball used!")
                        val pokeBall = CobblemonItems.POKE_BALL.pokeBall
//                        private fun pokeBallItem(pokeBall: PokeBall): PokeBallItem {
//                            val item = CobblemonItems.create(pokeBall.name.path, PokeBallItem(pokeBall))
//                            pokeBall.item = item
//                            CobblemonItems.pokeBalls.add(item)
//                            return item
//                        }

                        val pokeBallEntity = EmptyPokeBallEntity(pokeBall, player.level(), player)



                        pokeBallEntity.apply {
                            val overhandFactor: Float = if (player.xRot < 0) {
                                5f * cos(player.xRot.toRadians())
                            } else {
                                5f
                            }

                            shootFromRotation(player, player.xRot - overhandFactor, player.yRot, 0.0f, pokeBall.throwPower, 1.0f)
                            setPos(position().add(deltaMovement.normalize().scale(1.0)))
                            owner = player

                        }

                        // subscribe to the pokeBallEntity.captureFuture and set the pokemon to shiny
//                        pokeBallEntity.captureFuture.thenAccept { res ->
//                            getLogger().info("Shiny Ball Capture Effect! ${res}")
//                                getLogger().info("Shiny Ball Capture Effect!")
//                                pokemon.shiny = true
//                        }
                        pokeBallEntity.setGlowingTag(true)
                        pokeBallEntity.addTag("shinyBall")
                        world.addFreshEntity(pokeBallEntity)
                        itemStack.shrink(1)
                    }
                    return InteractionResultHolder.fail(itemStack)
                }
            }
        }

        return InteractionResultHolder.pass(itemStack)
    }
}
