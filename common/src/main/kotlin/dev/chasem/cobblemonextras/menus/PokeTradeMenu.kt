package dev.chasem.cobblemonextras.menus

import com.cobblemon.mod.common.Cobblemon
import dev.chasem.cobblemonextras.commands.PokeTrade
import dev.chasem.cobblemonextras.util.ItemBuilder
import dev.chasem.cobblemonextras.util.PokemonUtility
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import kotlin.math.floor


class PokeTradeMenu : MenuProvider {
    private val container: SimpleContainer = SimpleContainer(9 * 6)
    private val tradeSession: PokeTrade.TradeSession
    val unacceptedItem: ItemStack = ItemBuilder(Items.GRAY_DYE).setCustomName(Component.literal("Click to Accept")).build()
    val acceptedItem: ItemStack = ItemBuilder(Items.LIME_DYE).setCustomName(Component.literal("Accepted. Click to undo.")).build()

    constructor (tradeSession: PokeTrade.TradeSession) {
        this.tradeSession = tradeSession;
        setupContainer();

    }

    private fun fillWithPanes() {
        for (i in 0 until container.containerSize) {
            val row = floor(i.toDouble() / 9.0)
            val index = i % 9
            if (index == 4) {
                container.setItem(i, ItemBuilder(Items.YELLOW_STAINED_GLASS_PANE).setCustomName(Component.literal(" ")).build())
            } else if ((row == 1.0 || row == 3.0) && (index in 1..7)) {
                container.setItem(i, ItemBuilder(Items.LIGHT_BLUE_STAINED_GLASS_PANE).setCustomName(Component.literal(" ")).build())
            } else if (row == 2.0 && (index > 0 && index % 2 == 1)) {
                container.setItem(i, ItemBuilder(Items.LIGHT_BLUE_STAINED_GLASS_PANE).setCustomName(Component.literal(" ")).build())
            } else {
                container.setItem(i, ItemBuilder(Items.GRAY_STAINED_GLASS_PANE).setCustomName(Component.literal(" ")).build())
            }
        }
    }

    private fun setupContainer() {
        fillWithPanes();

        container.setItem(53, unacceptedItem)
        container.setItem(45, unacceptedItem)
    }

    override fun createMenu(i: Int, inventory: Inventory?, player: Player?): AbstractContainerMenu {

        val trader1StorageInit = Cobblemon.storage.getParty(tradeSession.trader1)
        for (i in 0..5) {
            val pokemon = trader1StorageInit.get(i)
            if (pokemon != null) {
                val item: ItemStack = PokemonUtility.pokemonToItem(pokemon)
                item.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply { putInt("slot", i) }))
                container.setItem(37 + i + (if (i >= 3) 6 else 0), item)
            } else {
                container.setItem(37 + i + (if (i >= 3) 6 else 0), ItemBuilder(Items.RED_STAINED_GLASS_PANE).setCustomName(Component.literal("Empty").withStyle(ChatFormatting.GRAY)).build())
            }
        }

        val trader2StorageInit = Cobblemon.storage.getParty(tradeSession.trader2)
        for (i in 0..5) {
            val pokemon = trader2StorageInit.get(i)
            if (pokemon != null) {
                val item: ItemStack = PokemonUtility.pokemonToItem(pokemon)
                item.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply { putInt("slot", i) }))
                container.setItem(41 + i + (if (i >= 3) 6 else 0), item)
            } else {
                container.setItem(41 + i + (if (i >= 3) 6 else 0), ItemBuilder(Items.RED_STAINED_GLASS_PANE).setCustomName(Component.literal("Empty").withStyle(ChatFormatting.GRAY)).build())
            }
        }

        return object : ChestMenu(MenuType.GENERIC_9x6, i, inventory, container, 6
        ) {
            override fun clicked(slot: Int, buttonNo: Int, clickType: ClickType, player: Player) {
                if (tradeSession.cancelled) {
                    val serverPlayer = player as ServerPlayer
                    serverPlayer.sendSystemMessage(Component.literal("Trade has been cancelled.").withStyle(ChatFormatting.RED))
                    serverPlayer.closeContainer()
                }
                val row = floor(slot.toDouble() / 9.0)
                val index = slot % 9


                if (index > 4 && player.uuid.equals(tradeSession.trader1.uuid)) {
                    return
                }
                if (index < 4 && player.uuid.equals(tradeSession.trader2.uuid)) {
                    return
                }

                container.setItem(45, if (tradeSession.trader1Accept) acceptedItem else unacceptedItem)
                container.setItem(53, if (tradeSession.trader2Accept) acceptedItem else unacceptedItem)

                if (tradeSession.trader1Pokemon != null) {
                    val pokemonItem: ItemStack = PokemonUtility.pokemonToItem(tradeSession.trader1Pokemon!!)
                    container.setItem(20, pokemonItem)
                }

                if (tradeSession.trader2Pokemon != null) {
                    val pokemonItem: ItemStack = PokemonUtility.pokemonToItem(tradeSession.trader2Pokemon!!)
                    container.setItem(24, pokemonItem)
                }

                if (slot == 45 && player.uuid.equals(tradeSession.trader1.uuid)) {
                    tradeSession.trader1Accept = !tradeSession.trader1Accept
                    val item: ItemStack = if (tradeSession.trader1Accept) acceptedItem else unacceptedItem
                    container.setItem(45, item)

                    // Syncs other players inventory immediately.
//                    val packet: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, slot, item.copy())
//                    tradeSession.trader2.networkHandler.sendPacket(packet)
                }

                if (slot == 53 && player.uuid.equals(tradeSession.trader2.uuid)) {
                    tradeSession.trader2Accept = !tradeSession.trader2Accept
                    val item: ItemStack = if (tradeSession.trader2Accept) acceptedItem else unacceptedItem
                    container.setItem(53, item)

                    // Syncs other players inventory immediately.
//                    val packet: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, slot, item.copy())
//                    tradeSession.trader1.networkHandler.sendPacket(packet)
                }

                if (slot > 54) {
                    return
                }
                val trader1Storage = Cobblemon.storage.getParty(tradeSession.trader1)
                val trader2Storage = Cobblemon.storage.getParty(tradeSession.trader2)

                val stack: ItemStack = container.getItem(slot);
                if (stack != null && stack.has(DataComponents.CUSTOM_DATA) && stack.get(DataComponents.CUSTOM_DATA)!!.contains("slot")) {
                    var customData = stack.get(DataComponents.CUSTOM_DATA)!!
                    var tag = customData.copyTag();
                    val slot: Int = tag.getInt("slot")
                    if (player.uuid.equals(tradeSession.trader1.uuid)) {
                        val pokemon = trader1Storage.get(slot)
                        if (pokemon != null) {
                            tradeSession.trader1Pokemon = pokemon
                            val pokemonItem: ItemStack = PokemonUtility.pokemonToItem(pokemon)

                            container.setItem(20, pokemonItem)
//                            val packet: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, 20, pokemonItem)
//                            tradeSession.trader2.networkHandler.sendPacket(packet)
                            tradeSession.trader1Accept = false
                            tradeSession.trader2Accept = false
                            container.setItem(45, unacceptedItem)
                            container.setItem(53, unacceptedItem)
//                            val packet2: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, 45, unacceptedItem.copy())
//                            tradeSession.trader2.networkHandler.sendPacket(packet2)
//                            val packet3: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, 53, unacceptedItem.copy())
//                            tradeSession.trader2.networkHandler.sendPacket(packet3)
                        }
                    } else {
                        val pokemon = trader2Storage.get(slot)
                        if (pokemon != null) {
                            tradeSession.trader2Pokemon = pokemon
                            val pokemonItem: ItemStack = PokemonUtility.pokemonToItem(pokemon)

                            container.setItem(24, pokemonItem)
//                            val packet: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, 24, pokemonItem)
//                            tradeSession.trader1.networkHandler.sendPacket(packet)
                            tradeSession.trader1Accept = false
                            tradeSession.trader2Accept = false
                            container.setItem(45, unacceptedItem)
                            container.setItem(53, unacceptedItem)

//                            val packet2: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, 45, unacceptedItem.copy())
//                            tradeSession.trader1.networkHandler.sendPacket(packet2)
//                            val packet3: ScreenHandlerSlotUpdateS2CPacket = ScreenHandlerSlotUpdateS2CPacket(syncId, 53, unacceptedItem.copy())
//                            tradeSession.trader1.networkHandler.sendPacket(packet3)
                        }
                    }
                }

                if (tradeSession.trader1Accept && tradeSession.trader2Accept) {
                    tradeSession.doTrade()
                    tradeSession.trader1.closeContainer()
                    tradeSession.trader2.closeContainer()
                }
            }


            override fun removed(player: Player) {
                if (player is ServerPlayer) {
                    if (!tradeSession.cancelled) {
                        tradeSession.cancel();
                        tradeSession.trader1.closeContainer()
                        tradeSession.trader2.closeContainer();
                    }
                }
                super.removed(player)
            }

            override fun quickMoveStack(player: Player, i: Int): ItemStack {
                return ItemStack.EMPTY
            }

            override fun stillValid(player: Player): Boolean {
                return true
            }
        }
    }

    override fun getDisplayName(): Component {
        return Component.literal(("Trade: " + tradeSession.trader1.name.string).toString() + " - " + tradeSession.trader2.name.string)
    }
}