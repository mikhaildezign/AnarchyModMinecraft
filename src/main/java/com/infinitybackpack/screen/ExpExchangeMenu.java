package com.infinitybackpack.screen;

import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.registry.ModMenus;
import com.infinitybackpack.InfinityBackpackMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ExpExchangeMenu extends AbstractContainerMenu {
    public ExpExchangeMenu(int syncId, Inventory playerInventory) {
        super(ModMenus.EXP_EXCHANGE_MENU, syncId);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 85 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 143));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;

        boolean shift = id >= 100;
        int buttonId = shift ? id - 100 : id;

        int cost;
        ItemStack result;

        switch (buttonId) {
            case 0 -> { cost = 7; result = new ItemStack(Items.EXPERIENCE_BOTTLE); }
            case 1 -> { cost = 315; result = new ItemStack(ModItems.EXP_BOTTLE_15); }
            case 2 -> { cost = 1395; result = new ItemStack(ModItems.EXP_BOTTLE_30); }
            case 3 -> { cost = 5345; result = new ItemStack(ModItems.EXP_BOTTLE_50); }
            case 4 -> { cost = 30971; result = new ItemStack(ModItems.EXP_BOTTLE_100); }
            default -> { return false; }
        }

        if (cost <= 0) return false;

        if (!serverPlayer.isCreative()) {
            if (serverPlayer.totalExperience < cost) {
                sendFail(serverPlayer);
                return true;
            }
            boolean hasGlass = false;
            for (ItemStack stack : serverPlayer.getInventory().items) {
                if (stack.is(Items.GLASS_BOTTLE)) {
                    hasGlass = true;
                    break;
                }
            }
            if (!hasGlass) {
                sendFail(serverPlayer);
                return true;
            }
        }

        int maxCount = 1;
        if (shift) {
            if (serverPlayer.isCreative()) {
                maxCount = 64;
            } else {
                int glassCount = 0;
                for (ItemStack stack : serverPlayer.getInventory().items) {
                    if (stack.is(Items.GLASS_BOTTLE)) glassCount += stack.getCount();
                }
                maxCount = Math.min(glassCount, serverPlayer.totalExperience / cost);
            }
        }

        if (maxCount <= 0) {
            sendFail(serverPlayer);
            return true;
        }

        for (int i = 0; i < maxCount; i++) {
            if (!serverPlayer.isCreative()) {
                if (serverPlayer.totalExperience < cost) break;

                boolean hasGlass = false;
                for (ItemStack stack : serverPlayer.getInventory().items) {
                    if (stack.is(Items.GLASS_BOTTLE)) {
                        hasGlass = true;
                        break;
                    }
                }
                if (!hasGlass) break;

                serverPlayer.giveExperiencePoints(-cost);
                for (ItemStack stack : serverPlayer.getInventory().items) {
                    if (stack.is(Items.GLASS_BOTTLE)) {
                        stack.shrink(1);
                        break;
                    }
                }
            }

            ItemStack give = result.copy();
            if (!serverPlayer.getInventory().add(give)) {
                serverPlayer.drop(give, false);
            }
        }

        serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.PLAYERS,
                0.5f, 0.4f / (serverPlayer.level().getRandom().nextFloat() * 0.4f + 0.8f));

        return true;
    }

    private static void sendFail(ServerPlayer player) {
        player.sendSystemMessage(
                Component.literal("Недостаточно ")
                        .append(Component.literal("опыта/бутылочек").withStyle(Style.EMPTY.withColor(0xFF0000)))
                        .append(Component.literal(" для обмена"))
        );
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}