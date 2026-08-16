package com.infinitybackpack.mixin;

import com.infinitybackpack.InfinityBackpackMod;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

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

    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onHurtAndBreak(int amount, ServerLevel level, ServerPlayer player, Consumer<Item> onBreak, CallbackInfo ci) {
        if (player == null) return;

        ItemStack self = (ItemStack)(Object)this;
        // Проверяем, что предмет именно в основной руке (как было в старом коде)
        if (player.getMainHandItem() != self) return;

        int unbreakableLevel = InfinityBackpackMod.getUnbreakableLevel(self);
        if (unbreakableLevel > 0) {
            int remaining = self.getMaxDamage() - self.getDamageValue();
            if (remaining <= 50) {
                ci.cancel(); // Отменяем весь метод hurtAndBreak — урон не наносится
            }
        }
    }
}