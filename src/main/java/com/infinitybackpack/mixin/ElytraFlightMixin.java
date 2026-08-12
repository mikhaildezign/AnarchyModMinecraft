package com.infinitybackpack.mixin;

import com.infinitybackpack.item.CustomElytraItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ElytraFlightMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void infinitybackpack$applyJetElytraSpeed(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Только на сервере, чтобы не было рассинхрона
        if (entity.level().isClientSide()) return;
        // Только если игрок сейчас летит на элитре
        if (!entity.isFallFlying()) return;

        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof CustomElytraItem elytra)) return;
        if (!elytra.isJet()) return;

        Vec3 motion = entity.getDeltaMovement();
        // Увеличиваем ТОЛЬКО горизонтальную скорость (X и Z) в 1.5 раза
        // Y не трогаем — взлетать вверх можно только фейерверком
        entity.setDeltaMovement(
                motion.x * 1.5,
                motion.y,
                motion.z * 1.5
        );
    }
}