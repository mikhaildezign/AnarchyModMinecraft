package com.infinitybackpack.item;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class CustomElytraItem extends ElytraItem implements FabricElytraItem {
    private final String displayName;
    private final int[] nameGradient;
    private final List<Component> tooltipLines;
    private final boolean armored;
    private final boolean unbreakable;
    private final boolean jet;

    public CustomElytraItem(Properties properties, String displayName, int[] nameGradient,
                            List<Component> tooltipLines, boolean armored, boolean unbreakable, boolean jet) {
        super(properties);
        this.displayName = displayName;
        this.nameGradient = nameGradient;
        this.tooltipLines = tooltipLines;
        this.armored = armored;
        this.unbreakable = unbreakable;
        this.jet = jet;
    }

    public boolean isArmored() { return armored; }
    public boolean isUnbreakable() { return unbreakable; }
    public boolean isJet() { return jet; }

    @Override
    public boolean useCustomElytra(LivingEntity entity, ItemStack chestStack, boolean tickElytra) {
        // Нерушимые элитры летают всегда, остальные — пока не сломались
        boolean canFly = unbreakable || chestStack.getDamageValue() < chestStack.getMaxDamage() - 1;
        if (!canFly) {
            return false;
        }

        if (tickElytra) {
            if (unbreakable) {
                // Для нерушимых не наносим урон, но шлём game event как ванилла
                int nextRoll = entity.getFallFlyingTicks() + 1;
                if (!entity.level().isClientSide && nextRoll % 10 == 0) {
                    entity.gameEvent(GameEvent.ELYTRA_GLIDE);
                }
            } else {
                // Обычный тик с износом
                doVanillaElytraTick(entity, chestStack);
            }
        }

        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent result = Component.empty();
        int len = displayName.length();
        for (int i = 0; i < len; i++) {
            float ratio = (float) i / (len - 1);
            int color = interpolateColor(nameGradient[0], nameGradient[1], ratio);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(displayName.charAt(i))).withStyle(style));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("Особенности:")
                .withStyle(Style.EMPTY.withColor(0x555555)));
        tooltipComponents.addAll(this.tooltipLines);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        if (unbreakable) return false;
        return super.isValidRepairItem(toRepair, repair);
    }

    private static int interpolateColor(int start, int end, float ratio) {
        int r1 = (start >> 16) & 0xFF, g1 = (start >> 8) & 0xFF, b1 = start & 0xFF;
        int r2 = (end >> 16) & 0xFF, g2 = (end >> 8) & 0xFF, b2 = end & 0xFF;
        int r = Math.round(r1 + (r2 - r1) * ratio);
        int g = Math.round(g1 + (g2 - g1) * ratio);
        int b = Math.round(b1 + (b2 - b1) * ratio);
        return (r << 16) | (g << 8) | b;
    }
}