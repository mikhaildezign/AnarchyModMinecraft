package com.infinitybackpack.mixin;

import com.infinitybackpack.event.ModEvents;
import com.infinitybackpack.InfinityBackpackMod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderpearlItem.class)
public class EnderpearlItemMixin {

    // Отменяем спавн сущности жемчуга
    @WrapOperation(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean cancelSpawn(Level level, Entity entity, Operation<Boolean> original) {
        if (entity instanceof ThrownEnderpearl pearl
                && pearl.getOwner() instanceof ServerPlayer player
                && ModEvents.isPlayerInAnyStasis(player)) {
            return false;
        }
        return original.call(level, entity);
    }

    // Отменяем трату предмета
    @WrapOperation(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    private void cancelConsume(ItemStack stack, int amount, LivingEntity entity, Operation<Void> original) {
        if (entity instanceof ServerPlayer player && ModEvents.isPlayerInAnyStasis(player)) {
            player.displayClientMessage(
                    Component.literal("Вы не можете здесь активировать данный предмет!")
                            .withStyle(Style.EMPTY.withColor(0xFFFFFF)), false);
            return;
        }
        original.call(stack, amount, entity);
    }
}