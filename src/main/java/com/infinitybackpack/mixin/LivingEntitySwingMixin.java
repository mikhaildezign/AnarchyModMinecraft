package com.infinitybackpack.mixin;

import com.infinitybackpack.registry.ModConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntitySwingMixin {
    @Unique
    private boolean infinitybackpack$isTestHitting = false;

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"))
    private void onSwing(InteractionHand hand, boolean updateSelf, CallbackInfo ci) {
        if (infinitybackpack$isTestHitting) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof ServerPlayer player)) return;

        if (ModConstants.TEST_HIT_PLAYERS.getOrDefault(player.getUUID(), false)) {
            infinitybackpack$isTestHitting = true;
            try {
                player.attack(player);
            } finally {
                infinitybackpack$isTestHitting = false;
            }
        }
    }
}