package dev.chasem.cobblemonextras.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.chasem.cobblemonextras.util.ItemBuilder;
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

public class CompSeeHandlerFactory implements NamedScreenHandlerFactory {

    private ServerPlayerEntity toView;
    private int boxNumber = 0;

    public CompSeeHandlerFactory(ServerPlayerEntity toView) {
        this.toView = toView;
    }

    public CompSeeHandlerFactory(ServerPlayerEntity toView, int boxNumber) {
        this.toView = toView;
        this.boxNumber = boxNumber;
    }


    public CompSeeHandlerFactory() {
    }

    @Override
    public Text getDisplayName() {
        return Text.of(toView.getEntityName() + " PC : Box " + (boxNumber + 1));
    }

    int rows() {
        return 5;
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
        } else {
            this.toView = serverPlayer;
        }
        PCStore pcStore = null;
        try {
            pcStore = Cobblemon.INSTANCE.getStorage().getPC(serverPlayer.getUuid());
        } catch (NoPokemonStoreException e) {
            return null;
        }
        PCBox box = pcStore.getBoxes().get(boxNumber);
        for (int i = 0; i < 30; i++) {
            Pokemon pokemon = box.get(i);
            double row = Math.floor((double) i / 6.0D);
            int index = i % 6;

            if (pokemon != null) {
                ItemStack item = PokemonUtility.pokemonToItem(pokemon);
                inventory.setStack((int) (row * 9) + index, item);
            } else {
                inventory.setStack((int) (row * 9) + index, new ItemStack(Items.RED_STAINED_GLASS_PANE).setCustomName(Text.literal("Empty").formatted(Formatting.GRAY)));
            }
        }

        if (boxNumber < 29) {
            inventory.setStack(44, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(Text.literal("Next Box")).build());
        }

        if (boxNumber > 0) {
            inventory.setStack(42, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(Text.literal("Previous Box")).build());
        }

        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, inv, inventory, rows()) {
            @Override
            public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
                if (slotIndex == 44 && boxNumber < 29) {
                    player.openHandledScreen(new CompSeeHandlerFactory(toView, boxNumber + 1));
                }
                if (slotIndex == 42 && boxNumber > 0) {
                    player.openHandledScreen(new CompSeeHandlerFactory(toView, boxNumber - 1));
                }
            }

            @Override
            public ItemStack quickMove(PlayerEntity player, int slot) {
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
