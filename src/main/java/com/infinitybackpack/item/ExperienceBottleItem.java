package com.infinitybackpack.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperienceBottleItem extends Item {
    private final String displayName;
    private final int[] nameGradient;
    private final int experienceAmount;
    private final int levelEquivalent;

    public ExperienceBottleItem(Properties properties, String displayName, int[] nameGradient, int experienceAmount, int levelEquivalent) {
        super(properties);
        this.displayName = displayName;
        this.nameGradient = nameGradient;
        this.experienceAmount = experienceAmount;
        this.levelEquivalent = levelEquivalent;
    }

    @Override
    public Component getName(ItemStack stack) {
        return applyMirrorGradient(displayName, nameGradient[0], nameGradient[1]);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("В пузырьке " + experienceAmount + " опыта (" + levelEquivalent + " ур.)")
                .withStyle(Style.EMPTY.withColor(0xAAAAAA)));
        tooltipComponents.add(Component.literal("Киньте пузырёк, чтобы получить опыт")
                .withStyle(Style.EMPTY.withColor(0xAAAAAA)));
        tooltipComponents.add(Component.literal("или починить инструмент с Починкой")
                .withStyle(Style.EMPTY.withColor(0xAAAAAA)));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            boolean repaired = false;

            ItemStack toolToRepair = (hand == InteractionHand.MAIN_HAND)
                    ? player.getOffhandItem()
                    : player.getMainHandItem();

            if (!toolToRepair.isEmpty() && toolToRepair.isDamageableItem()) {
                ItemEnchantments enchants = toolToRepair.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                boolean hasMending = false;
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
                    if (entry.getKey().is(Enchantments.MENDING)) {
                        hasMending = true;
                        break;
                    }
                }

                if (hasMending && toolToRepair.isDamaged()) {
                    // КПД 80% ТОЛЬКО для починки
                    int usableXP = (int) (this.experienceAmount * 0.8f);
                    int damage = toolToRepair.getDamageValue();
                    int maxRepair = usableXP * 2; // Mending: 1 XP = 2 прочности
                    int actualRepair = Math.min(maxRepair, damage);
                    int spentXP = (actualRepair + 1) / 2; // округление вверх

                    toolToRepair.setDamageValue(damage - actualRepair);

                    int remainingXP = usableXP - spentXP;
                    if (remainingXP > 0) {
                        player.giveExperiencePoints(remainingXP);
                    }

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.3f, 1.5f);

                    repaired = true;
                }
            }

            if (!repaired) {
                // Нет Починки, инструмент цел, или не держит инструмент — даём 100% опыта
                player.giveExperiencePoints(this.experienceAmount);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.PLAYERS,
                        0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
            }

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static Component applyMirrorGradient(String text, int darkColor, int brightColor) {
        MutableComponent result = Component.empty();
        int len = text.length();
        if (len == 0) return result;
        if (len == 1) return Component.literal(text).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(brightColor)));

        for (int i = 0; i < len; i++) {
            float ratio = (float) i / (len - 1);
            int color;
            if (ratio <= 0.5f) {
                color = interpolateColor(darkColor, brightColor, ratio * 2.0f);
            } else {
                color = interpolateColor(brightColor, darkColor, (ratio - 0.5f) * 2.0f);
            }
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }
        return result;
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