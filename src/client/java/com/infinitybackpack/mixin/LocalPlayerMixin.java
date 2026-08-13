package com.infinitybackpack.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Unique
    private boolean infinitybackpack$wasJumping = false;

    @Inject(method = "aiStep", at = @At("RETURN"))
    private void onAiStep(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer)(Object)this;

        boolean jumping = self.input.jumping;
        // Срабатывает только в момент НАЖАТИЯ прыжка (не при удержании)
        boolean justPressed = jumping && !infinitybackpack$wasJumping;
        infinitybackpack$wasJumping = jumping;

        if (!justPressed) return;
        if (self.onGround()) return;
        if (self.isFallFlying()) return;
        if (self.isInWater() || self.isInLava()) return;
        if (self.isPassenger()) return;

        ItemStack chest = self.getItemBySlot(EquipmentSlot.CHEST);
        // Если на груди кастомная элитра (не ванильная) и она целая
        if (chest.getItem() instanceof ElytraItem && !chest.is(Items.ELYTRA)
                && ElytraItem.isFlyEnabled(chest)) {
            // Отправляем пакет на сервер и запускаем полёт локально
            self.connection.send(new ServerboundPlayerCommandPacket(
                    self, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            self.startFallFlying();
        }
    }
}