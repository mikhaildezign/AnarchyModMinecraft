package com.infinitybackpack.mixin;

import com.infinitybackpack.event.ModEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFruitItem.class)
public class ChorusFruitItemMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void onFinishUsing(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (!(livingEntity instanceof Player player)) return;

        boolean inStasis;
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            inStasis = ModEvents.isPlayerInAnyStasis(serverPlayer);
        } else {
            inStasis = player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }

        if (inStasis) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Вы не можете здесь активировать данный предмет!")
                                .withStyle(Style.EMPTY.withColor(0xFFFFFF)), false);
            }
            cir.setReturnValue(stack);
        }
    }
}