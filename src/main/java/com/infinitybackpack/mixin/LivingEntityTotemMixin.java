package com.infinitybackpack.mixin;

import com.infinitybackpack.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityTotemMixin {

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void onCheckTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Талисман Infinity
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (stack.is(ModItems.INFINITY_TALISMAN)) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.awardStat(Stats.ITEM_USED.get(ModItems.INFINITY_TALISMAN));
                    CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, stack);
                    serverPlayer.level().broadcastEntityEvent(serverPlayer, (byte) 35);
                }

                entity.setHealth(1.0F);
                entity.removeAllEffects();
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null && "immortality".equals(customData.copyTag().getString("RuneType"))) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 255, false, false, true));
                }

                stack.shrink(1);
                cir.setReturnValue(true);
                return;
            }
        }

        // Ванильный тотем с руной Бессмертие
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null && "immortality".equals(customData.copyTag().getString("RuneType"))) {
                    if (entity instanceof ServerPlayer serverPlayer) {
                        serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                        CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, stack);
                        serverPlayer.level().broadcastEntityEvent(serverPlayer, (byte) 35);
                    }

                    entity.setHealth(1.0F);
                    entity.removeAllEffects();
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                    entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 255, false, false, true));

                    stack.shrink(1);
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}