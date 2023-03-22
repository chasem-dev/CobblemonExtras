package dev.chasem.cobblemonextras.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.chasem.cobblemonextras.commands.PokeTrade;
import dev.chasem.cobblemonextras.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
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

public class PokeTradeHandlerFactory implements NamedScreenHandlerFactory {

    private PokeTrade.TradeSession tradeSession;

    public PokeTradeHandlerFactory(PokeTrade.TradeSession tradeSession) {
        this.tradeSession = tradeSession;
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Trade: " + tradeSession.trader1.getEntityName() + " - " + tradeSession.trader2.getEntityName());
    }

    @Override
    public boolean shouldCloseCurrentScreen() {
        return false;
    }

    int rows() {
        return 6;
    }

    int size() {
        return rows() * 9;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {

        ItemStack unacceptedItem = new ItemStack(Items.GRAY_DYE).setCustomName(Text.literal("Click to Accept"));
        ItemStack acceptedItem = new ItemStack(Items.LIME_DYE).setCustomName(Text.literal("Accepted. Click to undo."));

        SimpleInventory inventory = new SimpleInventory(size());
        for (int i = 0; i < size(); i++) {
            double row = Math.floor((double) i / 9.0D);
            int index = i % 9;
            if (index == 4) {
                inventory.setStack(i, new ItemStack(Items.YELLOW_STAINED_GLASS_PANE).setCustomName(Text.of(" ")));
            } else if ((row == 1 || row == 3) && (index >= 1 && index <= 7)) {
                inventory.setStack(i, new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE).setCustomName(Text.of(" ")));
            } else if (row == 2 && (index > 0 && index % 2 == 1)) {
                inventory.setStack(i, new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE).setCustomName(Text.of(" ")));
            } else {
                inventory.setStack(i, new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setCustomName(Text.of(" ")));
            }
        }

        inventory.setStack(53, unacceptedItem);
        inventory.setStack(45, unacceptedItem);

        PlayerPartyStore trader1Storage = Cobblemon.INSTANCE.getStorage().getParty(tradeSession.trader1);
        for (int i = 0; i < 6; i++) {
            Pokemon pokemon = trader1Storage.get(i);
            if (pokemon != null) {
                ItemStack item = PokemonUtility.pokemonToItem(pokemon);
                NbtCompound slotNbt = item.getOrCreateSubNbt("slot");
                slotNbt.putInt("slot", i);
                item.setSubNbt("slot", slotNbt);
                inventory.setStack(37 + i + (i >= 3 ? 6 : 0), item);
            } else {
                inventory.setStack(37 + i + (i >= 3 ? 6 : 0), new ItemStack(Items.RED_STAINED_GLASS_PANE).setCustomName(Text.literal("Empty").formatted(Formatting.GRAY)));
            }
        }

        PlayerPartyStore trader2Storage = Cobblemon.INSTANCE.getStorage().getParty(tradeSession.trader2);
        for (int i = 0; i < 6; i++) {
            Pokemon pokemon = trader2Storage.get(i);
            if (pokemon != null) {
                ItemStack item = PokemonUtility.pokemonToItem(pokemon);
                NbtCompound slotNbt = item.getOrCreateSubNbt("slot");
                slotNbt.putInt("slot", i);
                item.setSubNbt("slot", slotNbt);
                inventory.setStack(41 + i + (i >= 3 ? 6 : 0), item);
            } else {
                inventory.setStack(41 + i + (i >= 3 ? 6 : 0), new ItemStack(Items.RED_STAINED_GLASS_PANE).setCustomName(Text.literal("Empty").formatted(Formatting.GRAY)));
            }
        }

        GenericContainerScreenHandler container = new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, rows()) {

            @Override
            public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
                if (tradeSession.cancelled) {
                    player.sendMessage(Text.literal("Trade has been cancelled.").formatted(Formatting.RED));
                    player.closeHandledScreen();
                }
                double row = Math.floor((double) slotIndex / 9.0D);
                int index = slotIndex % 9;


                if (index > 4 && player.getUuid().equals(tradeSession.trader1.getUuid())) {
                    return;
                }
                if (index < 4 && player.getUuid().equals(tradeSession.trader2.getUuid())) {
                    return;
                }

                setStackInSlot(45, nextRevision(), tradeSession.trader1Accept ? acceptedItem : unacceptedItem);
                setStackInSlot(53, nextRevision(), tradeSession.trader2Accept ? acceptedItem : unacceptedItem);

                if (tradeSession.trader1Pokemon != null) {
                    ItemStack pokemonItem = PokemonUtility.pokemonToItem(tradeSession.trader1Pokemon);
                    setStackInSlot(20, nextRevision(), pokemonItem);
                }

                if (tradeSession.trader2Pokemon != null) {
                    ItemStack pokemonItem = PokemonUtility.pokemonToItem(tradeSession.trader2Pokemon);
                    setStackInSlot(24, nextRevision(), pokemonItem);
                }

                if (slotIndex == 45 && player.getUuid().equals(tradeSession.trader1.getUuid())) {
                    tradeSession.trader1Accept = !tradeSession.trader1Accept;
                    ItemStack item = tradeSession.trader1Accept ? acceptedItem : unacceptedItem;
                    setStackInSlot(45, nextRevision(), item);

                    // Syncs other players inventory immediately.
                    ScreenHandlerSlotUpdateS2CPacket packet = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), slotIndex, item.copy());
                    tradeSession.trader2.networkHandler.sendPacket(packet);
                }

                if (slotIndex == 53 && player.getUuid().equals(tradeSession.trader2.getUuid())) {
                    tradeSession.trader2Accept = !tradeSession.trader2Accept;
                    ItemStack item = tradeSession.trader2Accept ? acceptedItem : unacceptedItem;
                    setStackInSlot(53, nextRevision(), item);

                    // Syncs other players inventory immediately.
                    ScreenHandlerSlotUpdateS2CPacket packet = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), slotIndex, item.copy());
                    tradeSession.trader1.networkHandler.sendPacket(packet);
                }

                if (slotIndex > 54) {
                    return;
                }

                ItemStack stack = getInventory().getStack(slotIndex);
                if (stack != null && stack.hasNbt() && stack.getSubNbt("slot") != null) {
                    int slot = stack.getSubNbt("slot").getInt("slot");
                    if (player.getUuid().equals(tradeSession.trader1.getUuid())) {
                        Pokemon pokemon = trader1Storage.get(slot);
                        if (pokemon != null) {
                            tradeSession.trader1Pokemon = pokemon;
                            ItemStack pokemonItem = PokemonUtility.pokemonToItem(pokemon);

                            setStackInSlot(20, nextRevision(), pokemonItem);
                            ScreenHandlerSlotUpdateS2CPacket packet = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 20, pokemonItem);
                            tradeSession.trader2.networkHandler.sendPacket(packet);
                            tradeSession.trader1Accept = false;
                            tradeSession.trader2Accept = false;
                            setStackInSlot(45, nextRevision(), unacceptedItem);
                            setStackInSlot(53, nextRevision(), unacceptedItem);
                            ScreenHandlerSlotUpdateS2CPacket packet2 = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 45, unacceptedItem.copy());
                            tradeSession.trader2.networkHandler.sendPacket(packet2);
                            ScreenHandlerSlotUpdateS2CPacket packet3 = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 53, unacceptedItem.copy());
                            tradeSession.trader2.networkHandler.sendPacket(packet3);

                        }
                    } else {
                        Pokemon pokemon = trader2Storage.get(slot);
                        if (pokemon != null) {
                            tradeSession.trader2Pokemon = pokemon;
                            ItemStack pokemonItem = PokemonUtility.pokemonToItem(pokemon);

                            setStackInSlot(24, nextRevision(), pokemonItem);
                            ScreenHandlerSlotUpdateS2CPacket packet = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 24, pokemonItem);
                            tradeSession.trader1.networkHandler.sendPacket(packet);
                            tradeSession.trader1Accept = false;
                            tradeSession.trader2Accept = false;
                            setStackInSlot(45, nextRevision(), unacceptedItem);
                            setStackInSlot(53, nextRevision(), unacceptedItem);

                            ScreenHandlerSlotUpdateS2CPacket packet2 = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 45, unacceptedItem.copy());
                            tradeSession.trader1.networkHandler.sendPacket(packet2);
                            ScreenHandlerSlotUpdateS2CPacket packet3 = new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 53, unacceptedItem.copy());
                            tradeSession.trader1.networkHandler.sendPacket(packet3);
                        }
                    }
                }

                if (tradeSession.trader1Accept && tradeSession.trader2Accept) {
                    tradeSession.doTrade();
                    tradeSession.trader1.closeHandledScreen();
                    tradeSession.trader2.closeHandledScreen();
                }
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

            @Override
            public void close(PlayerEntity player) {
                if (!tradeSession.cancelled) {
                    tradeSession.cancel();
                    tradeSession.trader1.closeHandledScreen();
                    tradeSession.trader2.closeHandledScreen();
                }
            }
        };

        container.disableSyncing();

        return container;
    }
}
