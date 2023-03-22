package dev.chasem.cobblemonextras.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.chasem.cobblemonextras.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class PokeSeeHandlerFactory implements NamedScreenHandlerFactory {

    private ServerPlayerEntity toView;

    public PokeSeeHandlerFactory(ServerPlayerEntity toView) {
        this.toView = toView;
    }
    public PokeSeeHandlerFactory() {
    }
    @Override
    public Text getDisplayName() {
        return Text.of("PokeSee Screen");
    }

    @Override
    public boolean shouldCloseCurrentScreen() {
        return false;
    }

    int rows() {
        return 4;
    }

    int size() {
        return rows() * 9;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        SimpleInventory inventory = new SimpleInventory(size());
        for (int i = 0; i < size(); i++) {
            inventory.setStack(i, new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setCustomName(Text.of(" ")));
        }


        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        if (this.toView != null) {
            serverPlayer = this.toView;
        }
        PlayerPartyStore storage = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer);
        for (int i = 0; i < 6; i++) {
            Pokemon pokemon = storage.get(i);
            if (pokemon != null) {
                ItemStack item = PokemonUtility.pokemonToItem(pokemon);
                inventory.setStack(12 + i + (i >= 3 ? 6 : 0), item);
            } else {
                inventory.setStack(12 + i + (i >= 3 ? 6 : 0), new ItemStack(Items.RED_STAINED_GLASS_PANE).setCustomName(Text.literal("Empty").formatted(Formatting.GRAY)));
            }
        }

        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, inv, inventory, rows()) {
            @Override
            public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
            }

            @Override
            public ItemStack transferSlot(PlayerEntity player, int index) {
                return null;
            }

            @Override
            public boolean canInsertIntoSlot(Slot slot) {
                return false;
            }

            @Override
            protected void dropInventory(PlayerEntity player, Inventory inventory) {

            }
        };
    }
}
