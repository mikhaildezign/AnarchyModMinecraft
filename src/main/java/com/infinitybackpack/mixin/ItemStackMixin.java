package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyReturnValue(method = "getMaxStackSize", at = @At("RETURN"))
    private int infinitybackpack$onGetMaxStackSize(int original) {
        ItemStack self = (ItemStack)(Object)this;
        if (self.getItem() instanceof PotionItem potion) {
            if (potion instanceof SplashPotionItem || potion instanceof LingeringPotionItem) {
                return original;
            }
            return 64;
        }
        return original;
    }

    @ModifyVariable(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V",
            at = @At("HEAD"),
            ordinal = 0
    )
    private int onHurtAndBreak(int amount, ServerLevel level, LivingEntity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player)) return amount;
        if (slot != EquipmentSlot.MAINHAND) return amount;

        ItemStack self = (ItemStack)(Object)this;
        int unbreakableLevel = InfinityBackpackMod.getUnbreakableLevel(self);
        if (unbreakableLevel > 0) {
            int remaining = self.getMaxDamage() - self.getDamageValue();
            if (remaining <= 50) {
                return 0; // Не наносим урон предмету
            }
        }
        return amount;
    }
}