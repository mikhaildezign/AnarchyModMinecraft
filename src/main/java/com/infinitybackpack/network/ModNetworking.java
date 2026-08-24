package com.infinitybackpack.network;

import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.registry.ModUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ModNetworking {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(ToggleAutoSmeltPayload.TYPE, ToggleAutoSmeltPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleDrillPayload.TYPE, ToggleDrillPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StasisSyncPayload.TYPE, StasisSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ToggleAutoSmeltPayload.TYPE, (payload, context) -> {
            ItemStack stack = context.player().getMainHandItem();
            if (stack.isEmpty()) return;

            int autoSmeltLevel = ModItems.getAutoSmeltLevel(stack);
            if (autoSmeltLevel <= 0) return;

            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();
            boolean currentlyDisabled = tag.getBoolean("AutoSmeltDisabled");
            tag.putBoolean("AutoSmeltDisabled", !currentlyDisabled);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            boolean nowDisabled = !currentlyDisabled;
            Component message = nowDisabled
                    ? ModUtils.createDrillToggleMessage("Автоплавка выключена", 0x8B0000, 0xFF0000)
                    : ModUtils.createDrillToggleMessage("Автоплавка включена", 0x006400, 0x00FF00);
            context.player().displayClientMessage(message, true);

            context.player().level().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(),
                    SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, nowDisabled ? 0.5f : 1.5f);
        });

        ServerPlayNetworking.registerGlobalReceiver(ToggleDrillPayload.TYPE, (payload, context) -> {
            ItemStack stack = context.player().getMainHandItem();
            if (stack.isEmpty()) return;

            int drillLevel = ModItems.getDrillLevel(stack);
            if (drillLevel <= 0) return;

            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();
            boolean currentlyDisabled = tag.getBoolean("DrillDisabled");
            tag.putBoolean("DrillDisabled", !currentlyDisabled);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            boolean nowDisabled = !currentlyDisabled;
            Component message = nowDisabled
                    ? ModUtils.createDrillToggleMessage("Бур выключен", 0x8B0000, 0xFF0000)
                    : ModUtils.createDrillToggleMessage("Бур включен", 0x006400, 0x00FF00);
            context.player().displayClientMessage(message, true);

            context.player().level().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(),
                    SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, nowDisabled ? 0.5f : 1.5f);
        });
    }
}