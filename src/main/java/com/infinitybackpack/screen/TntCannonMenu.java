package com.infinitybackpack.screen;

import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.block.TntCannonBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TntCannonMenu extends AbstractContainerMenu {
    private final Container cannonInventory;

    public TntCannonMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return TntCannonBlockEntity.isValidAmmo(stack);
            }
        });
    }

    public TntCannonMenu(int containerId, Inventory playerInventory, Container cannonInventory) {
        super(InfinityBackpackMod.TNT_CANNON_MENU, containerId);
        this.cannonInventory = cannonInventory;
        cannonInventory.startOpen(playerInventory.player);

        SimpleContainer fake = new SimpleContainer(8);
        ItemStack blackPane = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        blackPane.set(DataComponents.CUSTOM_NAME, Component.literal(" "));
        for (int i = 0; i < 8; i++) {
            fake.setItem(i, blackPane.copy());
        }

        int fakeIndex = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int x = 62 + col * 18;
                int y = 17 + row * 18;
                if (row == 1 && col == 1) {
                    this.addSlot(new Slot(cannonInventory, 0, x, y) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return TntCannonBlockEntity.isValidAmmo(stack);
                        }
                    });
                } else {
                    final int fakeSlot = fakeIndex++;
                    this.addSlot(new Slot(fake, fakeSlot, x, y) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return false;
                        }

                        @Override
                        public boolean mayPickup(Player player) {
                            return false;
                        }

                        @Override
                        public void set(ItemStack stack) {
                        }
                    });
                }
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.cannonInventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.cannonInventory.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}