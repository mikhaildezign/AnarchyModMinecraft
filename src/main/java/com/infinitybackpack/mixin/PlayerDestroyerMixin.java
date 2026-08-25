package com.infinitybackpack.mixin;

import com.infinitybackpack.registry.ModConstants;
import com.infinitybackpack.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class PlayerDestroyerMixin {

    @ModifyVariable(method = "hurtArmor", at = @At("HEAD"), argsOnly = true)
    private float modifyArmorDamage(float damage, DamageSource source) {
        Player target = (Player) (Object) this;

        // Проверяем, есть ли хотя бы один элемент брони
        boolean hasArmor = false;
        for (ItemStack armor : target.getArmorSlots()) {
            if (!armor.isEmpty()) {
                hasArmor = true;
                break;
            }
        }
        if (!hasArmor) return damage;

        if (!(source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker)) return damage;

        ItemStack weapon = attacker.getMainHandItem();
        int level = ModItems.getDestroyerLevel(weapon);
        if (level <= 0) return damage;

        if (!(weapon.getItem() instanceof SwordItem) && !(weapon.getItem() instanceof AxeItem)) {
            return damage;
        }

        float multiplier = switch (level) {
            case 1 -> 1.10f;
            case 2 -> 1.25f;
            case 3 -> 1.40f;
            default -> 1.0f;
        };

        float result = damage * multiplier;

        // Тестовый вывод — показываем ЖЕРТВЕ (игроку, которого бьют)
        if (target instanceof ServerPlayer serverPlayer) {
            Boolean testMode = ModConstants.TEST_DESTROYER_PLAYERS.get(serverPlayer.getUUID());
            if (testMode != null && testMode) {
                float basePerPiece = damage / 4.0f;
                float finalPerPiece = result / 4.0f;
                serverPlayer.sendSystemMessage(
                        Component.literal("[Разрушитель] ")
                                .withStyle(Style.EMPTY.withColor(0x00FF00))
                                .append(Component.literal(
                                        "Ур." + level + " | С каждой шмотки: "
                                                + String.format("%.2f", basePerPiece)
                                                + " → "
                                                + String.format("%.2f", finalPerPiece)
                                                + " прочности"
                                ).withStyle(Style.EMPTY.withColor(0xFFFFFF)))
                );
            }
        }

        return result;
    }
}