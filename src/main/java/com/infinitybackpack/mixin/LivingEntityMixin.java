package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.item.CustomElytraItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // === ПОДДЕРЖИВАЕМ ПОЛЁТ ДЛЯ КАСТОМНЫХ ЭЛИТР (сервер) ===
    @WrapOperation(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean customElytraCheck(ItemStack stack, Item item, Operation<Boolean> original) {
        if (item == Items.ELYTRA) {
            return stack.is(Items.ELYTRA) || stack.getItem() instanceof ElytraItem;
        }
        return original.call(stack, item);
    }

    // === БУСТ СКОРОСТИ РЕАКТИВНЫХ ЭЛИТР ===
    @Inject(method = "travel", at = @At("RETURN"))
    private void boostJetElytra(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!self.isFallFlying()) return;

        ItemStack chest = self.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof CustomElytraItem elytra) || !elytra.isJet()) return;

        Vec3 motion = self.getDeltaMovement();
        // Не бустим при полёте вверх — взлетать только фейерверками
        if (motion.y > 0) return;

        double hSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        if (hSpeed <= 0.001) return;

        // Ванильная крейсерская скорость ≈ 0.9 блоков/тик. В 1.5 раза = 1.35
        double targetSpeed = 1.35;
        // Если уже летим быстрее (фейерверк, резкое пикирование) — не мешаем
        if (hSpeed >= targetSpeed) return;

        // Масштабируем в 1.5 раза, но жёстко ограничиваем потолком 1.35
        double scale = Math.min(1.5, targetSpeed / hSpeed);

        // ВРЕМЕННОЕ ЛОГИРОВАНИЕ (каждую секунду в консоль IDE)
        if (self.level().getGameTime() % 20 == 0) {
            System.out.println("[InfinityBackpack] Jet boost! hSpeed=" + String.format("%.3f", hSpeed)
                    + " scale=" + String.format("%.3f", scale) + " y=" + String.format("%.3f", motion.y));
        }

        self.setDeltaMovement(
                motion.x * scale,
                motion.y,
                motion.z * scale
        );
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