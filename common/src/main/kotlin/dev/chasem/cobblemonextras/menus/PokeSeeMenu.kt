package dev.chasem.cobblemonextras.menus

import com.cobblemon.mod.common.Cobblemon.storage
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


class PokeSeeMenu : MenuProvider {
    private val container: Container = SimpleContainer(9 * 4)
    private var playerToView: ServerPlayer? = null;

    constructor (playerToView: ServerPlayer) {
        this.playerToView = playerToView
        setupContainer();
    }

    private fun fillWithPanes() {
        for (i in 0 until container.containerSize) {
            container.setItem(i, ItemBuilder(Items.GRAY_STAINED_GLASS_PANE).setCustomName(Component.literal(" ")).build())
        }
    }

    private fun setupContainer() {
        fillWithPanes();
    }

    override fun createMenu(i: Int, inventory: Inventory?, player: Player?): AbstractContainerMenu {
        var serverPlayer = player as ServerPlayer?
        if (this.playerToView != null) {
            serverPlayer = this.playerToView
        }
        val storage = storage.getParty(serverPlayer!!)
        for (i in 0..5) {
            val pokemon = storage.get(i)
            if (pokemon != null) {
                val item = PokemonUtility.pokemonToItem(pokemon)
                container.setItem(12 + i + (if (i >= 3) 6 else 0), item)
            } else {
                container.setItem(12 + i + (if (i >= 3) 6 else 0), ItemBuilder(Items.RED_STAINED_GLASS_PANE).setCustomName(Component.literal("Empty").withStyle(ChatFormatting.GRAY)).build())
            }
        }

        return object : ChestMenu(MenuType.GENERIC_9x4, i, inventory, container, 4
        ) {
            override fun clicked(slot: Int, buttonNo: Int, clickType: ClickType, player: Player) {

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
        return Component.literal("${playerToView?.name!!.string} Party")
    }
}