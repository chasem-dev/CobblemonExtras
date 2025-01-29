package dev.chasem.cobblemonextras.menus

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.storage
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException
import com.cobblemon.mod.common.api.storage.pc.PCStore
import dev.chasem.cobblemonextras.util.ItemBuilder
import dev.chasem.cobblemonextras.util.PokemonUtility
import net.minecraft.ChatFormatting
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
import kotlin.math.floor


class CompSeeMenu : MenuProvider {
    private val container: Container = SimpleContainer(9 * 6)
    private var playerToView: ServerPlayer? = null;

    private var MAX_BOX_COUNT = Cobblemon.config.defaultBoxCount;
    private var boxNumber = 0;

    constructor (playerToView: ServerPlayer, boxNumber: Int) {
        this.playerToView = playerToView
        setupContainer()
        displayBox(0)
    }

    private fun fillWithPanes() {
        for (i in 0 until container.containerSize) {
            container.setItem(i, ItemBuilder(Items.WHITE_STAINED_GLASS_PANE).setCustomName(Component.literal(" ")).build())
        }
    }

    private fun setupContainer() {
        fillWithPanes();
    }

    private fun getNextBox(): Int {
        var newBox = boxNumber + 1;
        if (newBox >= MAX_BOX_COUNT) {
            newBox = 0;
        }
        return newBox;
    }

    private fun getPreviousBox(): Int {
        var newBox = boxNumber - 1;
        if (newBox < 0) {
            newBox = MAX_BOX_COUNT - 1;
        }
        return newBox;
    }

    private fun displayBox(boxNumber: Int) {
        this.boxNumber = boxNumber;
        container.setChanged()



        container?.setItem(42, ItemBuilder(Items.ARROW).hideAdditional().setCustomName(Component.literal("Goto Box ${ getPreviousBox() + 1 }")).build())
        container?.setItem(43, ItemBuilder(CobblemonItems.PC).hideAdditional().setCustomName(Component.literal("Current Box ${ boxNumber + 1 }")).build())
        container?.setItem(44, ItemBuilder(Items.ARROW).hideAdditional().setCustomName(Component.literal("Goto Box ${ getNextBox() + 1 }")).build())


        var pcStore: PCStore? = null
        try {
            pcStore = storage.getPC(playerToView!!)
        } catch (e: NoPokemonStoreException) {

        }
        val box = pcStore?.boxes?.get(boxNumber);
        for (i in 0..<container.containerSize) {
            if (i >= 30) {
                break;
            }
            val pokemon = box?.get(i);
            val row = floor(i.toDouble() / 6.0)
            val index = i % 6

            if (pokemon != null) {
                val item = PokemonUtility.pokemonToItem(pokemon)
                container.setItem((row * 9).toInt() + index, item)
            } else {
                container.setItem((row * 9).toInt() + index, ItemBuilder(Items.RED_STAINED_GLASS_PANE).setCustomName(Component.literal("Empty").withStyle(ChatFormatting.GRAY)).build())
            }
        }
    }

    override fun createMenu(i: Int, inventory: Inventory?, player: Player?): AbstractContainerMenu {




        return object : ChestMenu(MenuType.GENERIC_9x6, i, inventory, container, 6
        ) {
            override fun clicked(slot: Int, buttonNo: Int, clickType: ClickType, player: Player) {
                if (slot == 42) {
                    displayBox(getPreviousBox())
                } else if (slot == 44) {
                    displayBox(getNextBox())
                }
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
        return Component.literal("${Component.literal(playerToView?.name!!.string).withStyle(ChatFormatting.DARK_GRAY).string} PC")
    }
}