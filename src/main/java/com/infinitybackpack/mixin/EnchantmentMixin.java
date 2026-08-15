package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.item.CustomElytraItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
        if (!InfinityBackpackMod.isMagnetism(self)) {
            return;
        }
        boolean isTool = stack.getItem() instanceof PickaxeItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof ShovelItem
                || stack.getItem() instanceof HoeItem
                || stack.is(Items.BOOK);
        cir.setReturnValue(isTool);
    }

    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int modifyMaxLevel(int original) {
        Enchantment self = (Enchantment)(Object)this;
        String id = InfinityBackpackMod.getEnchantmentId(self);
        System.out.println("[DEBUG] modifyMaxLevel called for '" + id + "', original = " + original);

        if (id == null || id.isEmpty()) {
            System.out.println("[DEBUG] ID is empty, returning original");
            return original;
        }

        int result = switch (id) {
            case "minecraft:efficiency" -> Math.max(original, 8);
            case "minecraft:sharpness" -> Math.max(original, 7);
            case "minecraft:smite" -> Math.max(original, 7);
            case "minecraft:bane_of_arthropods" -> Math.max(original, 7);
            case "minecraft:unbreaking" -> Math.max(original, 5);
            case "minecraft:looting" -> Math.max(original, 5);
            case "minecraft:fortune" -> Math.max(original, 5);
            case "minecraft:protection" -> Math.max(original, 5);
            case "minecraft:fire_protection" -> Math.max(original, 5);
            case "minecraft:blast_protection" -> Math.max(original, 5);
            case "minecraft:projectile_protection" -> Math.max(original, 5);
            default -> original;
        };

        System.out.println("[DEBUG] Returning " + result + " for " + id);
        return result;
    }

    @Inject(method = "getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;", at = @At("RETURN"), cancellable = true)
    private static void onGetFullname(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {
        if (enchantment.is(InfinityBackpackMod.IMPENETRABLE)) {
            MutableComponent component = Component.translatable("enchantment.infinitybackpack.impenetrable")
                    .withStyle(ChatFormatting.GRAY);
            component.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + level).withStyle(ChatFormatting.GRAY));
            cir.setReturnValue(component);
        } else if (enchantment.is(InfinityBackpackMod.UNBREAKABLE_ENCHANT)) {
            cir.setReturnValue(Component.translatable("enchantment.infinitybackpack.unbreakable")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}