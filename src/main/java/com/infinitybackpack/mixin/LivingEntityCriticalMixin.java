package com.infinitybackpack.mixin;

import com.infinitybackpack.registry.ModConstants;
import com.infinitybackpack.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityCriticalMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float applyCriticalEnchantment(float amount, DamageSource source) {
        if (!(source.getEntity() instanceof Player player)) return amount;
        if (source.getDirectEntity() != player) return amount;

        ItemStack weapon = player.getMainHandItem();
        int level = ModItems.getCriticalLevel(weapon);
        if (level <= 0) return amount;

        if (!(weapon.getItem() instanceof SwordItem) && !(weapon.getItem() instanceof AxeItem)) {
            return amount;
        }

        float chance = level == 1 ? 0.10f : 0.20f;
        boolean triggered = player.getRandom().nextFloat() < chance;
        float result = triggered ? amount * 1.15f : amount;

        // Тестовый вывод в чат
        if (player instanceof ServerPlayer serverPlayer) {
            Boolean testMode = ModConstants.TEST_CRITICAL_PLAYERS.get(serverPlayer.getUUID());
            if (testMode != null && testMode) {
                if (triggered) {
                    serverPlayer.sendSystemMessage(Component.literal("[Критический] ")
                            .withStyle(Style.EMPTY.withColor(0x00FF00))
                            .append(Component.literal("Сработал! Урон: " + String.format("%.2f", amount) + " HP → " + String.format("%.2f", result) + " HP")
                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF))));
                } else {
                    serverPlayer.sendSystemMessage(Component.literal("[Критический] ")
                            .withStyle(Style.EMPTY.withColor(0xFF0000))
                            .append(Component.literal("Не сработал. Урон: " + String.format("%.2f", amount) + " HP")
                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF))));
                }
            }
        }

        return result;
    }
}