package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // === РАЗРЕШАЕМ КАСТОМНЫМ ЭЛИТРАМ ЛЕТАТЬ (ванильная физика) ===
    @ModifyReturnValue(method = "canGlide", at = @At("RETURN"))
    private boolean allowCustomElytraGlide(boolean original) {
        if (original) return true;
        LivingEntity self = (LivingEntity)(Object)this;
        ItemStack chest = self.getItemBySlot(EquipmentSlot.CHEST);
        return chest.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(chest);
    }

    // === ТАЛИСМАН (бессмертие) ===
    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void onCheckTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (damageSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (stack.is(InfinityBackpackMod.INFINITY_TALISMAN)) {
                stack.shrink(1);
                entity.setHealth(1.0F);
                entity.removeAllEffects();
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                entity.level().broadcastEntityEvent(entity, (byte)35);
                cir.setReturnValue(true);
                return;
            }
        }
    }

    // === ЗАЧАРОВАНИЕ IMPENETRABLE ===
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float applyImpenetrable(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.level().isClientSide || amount <= 0) return amount;

        int totalLevel = 0;
        for (ItemStack stack : self.getArmorSlots()) {
            if (stack.isEmpty()) continue;
            ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                if (entry.getKey().is(InfinityBackpackMod.IMPENETRABLE)) {
                    totalLevel += entry.getIntValue();
                }
            }
        }

        if (totalLevel > 0) {
            float chance = totalLevel * 0.05f;
            if (self.getRandom().nextFloat() < chance) {
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(),
                        SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.NEUTRAL, 1.5f, 1.0f);
                return amount * 0.8f;
            }
        }
        return amount;
    }
}