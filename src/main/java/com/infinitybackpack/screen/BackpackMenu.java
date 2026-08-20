package com.infinitybackpack.screen;

import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.registry.ModMenus;
import com.infinitybackpack.InfinityBackpackMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BackpackMenu extends AbstractContainerMenu {
    private static final TagKey<Item> SHULKER_BOXES_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "shulker_boxes"));

    private final Container container;
    private final ItemStack backpackStack;

    public BackpackMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ItemStack.EMPTY);
    }

    public BackpackMenu(int syncId, Inventory playerInventory, ItemStack backpackStack) {
        super(ModMenus.BACKPACK_MENU, syncId);
        this.backpackStack = backpackStack;

        ItemContainerContents contents = backpackStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
        contents.copyInto(items);

        this.container = new SimpleContainer(items.toArray(new ItemStack[0]));
        this.container.startOpen(playerInventory.player);

        // 4 ряда по 9 слотов рюкзака (с защитой от шалкеров и рюкзаков)
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new BackpackSlot(this.container, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Инвентарь игрока (3 ряда) — защищён от поднятия рюкзака
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new BackpackInventorySlot(playerInventory, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }

        // Хотбар игрока — тоже защищён
        for (int col = 0; col < 9; col++) {
            this.addSlot(new BackpackInventorySlot(playerInventory, col, 8 + col * 18, 162));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.INFINITY_BACKPACK)) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModItems.INFINITY_BACKPACK)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);

        if (!this.backpackStack.isEmpty()) {
            NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
            for (int i = 0; i < 36; i++) {
                items.set(i, this.container.getItem(i));
            }
            ItemContainerContents newContents = ItemContainerContents.fromItems(items);
            this.backpackStack.set(DataComponents.CONTAINER, newContents);
        }

        player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.SHULKER_BOX_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, player.level().random.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < 36) {
                if (!this.moveItemStackTo(itemStack2, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack2, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    // Слот рюкзака: нельзя класть шалкеры и рюкзаки Infinity
    private static class BackpackSlot extends Slot {
        public BackpackSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.is(SHULKER_BOXES_TAG)) {
                return false;
            }
            if (stack.is(ModItems.INFINITY_BACKPACK)) {
                return false;
            }
            return super.mayPlace(stack);
        }
    }

    // Слот инвентаря игрока: нельзя поднимать рюкзак Infinity
    private static class BackpackInventorySlot extends Slot {
        public BackpackInventorySlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            ItemStack stack = this.getItem();
            if (stack.is(ModItems.INFINITY_BACKPACK)) {
                return false;
            }
            return super.mayPickup(player);
        }
    }
}