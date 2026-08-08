package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.item.CustomElytraItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "canEnchant", at = @At("RETURN"), cancellable = true)
    private void allowOnArmoredElytra(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && stack.getItem() instanceof CustomElytraItem elytra && elytra.isArmored()) {
            Enchantment self = (Enchantment)(Object)this;
            cir.setReturnValue(self.canEnchant(new ItemStack(Items.NETHERITE_CHESTPLATE)));
        }
    }

    @Inject(method = "canEnchant", at = @At("RETURN"), cancellable = true)
    private void restrictMagnetism(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment self = (Enchantment)(Object)this;
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.getKey(self);
        if (key == null || !key.equals(ResourceLocation.fromNamespaceAndPath("infinitybackpack", "magnetism"))) {
            return;
        }
        boolean isTool = stack.getItem() instanceof PickaxeItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof ShovelItem
                || stack.getItem() instanceof HoeItem
                || stack.is(Items.BOOK);
        cir.setReturnValue(isTool);
    }

    @Inject(method = "getMaxLevel", at = @At("RETURN"), cancellable = true)
    private void increaseMaxLevel(CallbackInfoReturnable<Integer> cir) {
        Enchantment self = (Enchantment)(Object)this;
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.getKey(self);
        if (key == null) return;

        switch (key.toString()) {
            case "minecraft:efficiency" -> {
                if (cir.getReturnValue() < 8) cir.setReturnValue(8);
            }
            case "minecraft:sharpness" -> {
                if (cir.getReturnValue() < 7) cir.setReturnValue(7);
            }
            case "minecraft:smite" -> {
                if (cir.getReturnValue() < 7) cir.setReturnValue(7);
            }
            case "minecraft:bane_of_arthropods" -> {
                if (cir.getReturnValue() < 7) cir.setReturnValue(7);
            }
            case "minecraft:unbreaking" -> {
                if (cir.getReturnValue() < 5) cir.setReturnValue(5);
            }
            case "minecraft:looting" -> {
                if (cir.getReturnValue() < 5) cir.setReturnValue(5);
            }
            case "minecraft:fortune" -> {
                if (cir.getReturnValue() < 5) cir.setReturnValue(5);
            }
            case "minecraft:protection" -> {
                if (cir.getReturnValue() < 5) cir.setReturnValue(5);
            }
            case "minecraft:fire_protection" -> {
                if (cir.getReturnValue() < 5) cir.setReturnValue(5);
            }
            case "minecraft:blast_protection" -> {
                if (cir.getReturnValue() < 5) cir.setReturnValue(5);
            }
            case "minecraft:projectile_protection" -> {
                if (cir.getReturnValue() < 5) cir.setReturnValue(5);
            }
        }
    }

    @Inject(method = "getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;", at = @At("RETURN"), cancellable = true)
    private static void onGetFullname(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {
        if (enchantment.is(InfinityBackpackMod.IMPENETRABLE)) {
            MutableComponent component = Component.translatable("enchantment.infinitybackpack.impenetrable")
                    .withStyle(ChatFormatting.GRAY);
            component.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + level).withStyle(ChatFormatting.GRAY));
            cir.setReturnValue(component);
        }
    }
}