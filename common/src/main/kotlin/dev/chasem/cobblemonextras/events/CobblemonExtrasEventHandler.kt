package dev.chasem.cobblemonextras.events

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.util.math.geometry.toRadians
import dev.chasem.cobblemonextras.CobblemonExtras
import dev.chasem.cobblemonextras.CobblemonExtras.getLogger
import dev.chasem.cobblemonextras.CobblemonExtras.showcaseService
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
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
        if (itemStack.item == CobblemonItems.POKE_BALL || itemStack.item == CobblemonItems.GREAT_BALL || itemStack.item == CobblemonItems.ULTRA_BALL || itemStack.item == CobblemonItems.MASTER_BALL) {
            if (itemStack.has(DataComponents.CUSTOM_DATA)) {
                val customData = itemStack.get(DataComponents.CUSTOM_DATA)
                val tag = customData?.copyTag() ?: CompoundTag()
                if (tag.contains("CobblemonExtrasBallType")) {
                    val ballType = tag.getString("CobblemonExtrasBallType")
                    if (ballType == "shiny") {

                        val itemBallType = tag.getString("ShinyBallBallType")
                        val pokeBall = when(itemBallType) {
                            "poke" -> CobblemonItems.POKE_BALL.pokeBall
                            "great" -> CobblemonItems.GREAT_BALL.pokeBall
                            "ultra" -> CobblemonItems.ULTRA_BALL.pokeBall
                            "master" -> CobblemonItems.MASTER_BALL.pokeBall
                            else -> CobblemonItems.POKE_BALL.pokeBall
                        }
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
