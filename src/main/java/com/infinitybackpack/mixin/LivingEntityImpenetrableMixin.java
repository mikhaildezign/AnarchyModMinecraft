package com.infinitybackpack.mixin;

import com.infinitybackpack.registry.ModConstants;
import com.infinitybackpack.registry.ModEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityImpenetrableMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float applyImpenetrable(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof ServerPlayer player)) return amount;

        // Считаем суммарный шанс: 5% за каждый уровень на каждом элементе брони
        float totalChance = 0.0f;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) continue;
            ItemEnchantments enchantments = armor.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            for (var entry : enchantments.entrySet()) {
                if (entry.getKey().is(ModEnchantments.IMPENETRABLE)) {
                    totalChance += entry.getIntValue() * 0.05f;
                }
            }
        }

        if (totalChance <= 0.0f) return amount;

        // Максимум 40% (4 шмотки × уровень 2)
        totalChance = Math.min(totalChance, 0.40f);

        boolean triggered = player.getRandom().nextFloat() < totalChance;
        float result = triggered ? amount * 0.80f : amount;

        // Тестовый вывод
        Boolean testMode = ModConstants.TEST_IMPENETRABLE_PLAYERS.get(player.getUUID());
        if (testMode != null && testMode) {
            if (triggered) {
                player.sendSystemMessage(Component.literal("[Непробиваемый] ")
                        .withStyle(Style.EMPTY.withColor(0x00FF00))
                        .append(Component.literal("Сработал! Урон: " + String.format("%.2f", amount) + " HP → " + String.format("%.2f", result) + " HP")
                                .withStyle(Style.EMPTY.withColor(0xFFFFFF))));
            } else {
                player.sendSystemMessage(Component.literal("[Непробиваемый] ")
                        .withStyle(Style.EMPTY.withColor(0xFF0000))
                        .append(Component.literal("Не сработал. Урон: " + String.format("%.2f", amount) + " HP")
                                .withStyle(Style.EMPTY.withColor(0xFFFFFF))));
            }
        }

        return result;
    }
}