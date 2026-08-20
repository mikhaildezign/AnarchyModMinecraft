package com.infinitybackpack.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModUtils {

    public static String getEnchantmentId(net.minecraft.world.item.enchantment.Enchantment enchantment) {
        var contents = enchantment.description().getContents();
        if (contents instanceof TranslatableContents tc) {
            String key = tc.getKey();
            if (key.startsWith("enchantment.")) {
                String sub = key.substring("enchantment.".length());
                int dot = sub.indexOf('.');
                if (dot != -1) {
                    String result = sub.substring(0, dot) + ":" + sub.substring(dot + 1);
                    System.out.println("[DEBUG] Enchantment ID = '" + result + "'");
                    return result;
                }
            }
        }
        System.out.println("[DEBUG] Could not parse ID from enchantment description");
        return "";
    }

    public static boolean isMagnetism(net.minecraft.world.item.enchantment.Enchantment enchantment) {
        boolean result = "infinitybackpack:magnetism".equals(getEnchantmentId(enchantment));
        System.out.println("[DEBUG] isMagnetism = " + result);
        return result;
    }

    public static boolean isOreSmeltingResult(ItemStack stack) {
        return stack.is(Items.IRON_INGOT) || stack.is(Items.GOLD_INGOT) || stack.is(Items.COPPER_INGOT)
                || stack.is(Items.NETHERITE_SCRAP) || stack.is(Items.QUARTZ)
                || stack.is(Items.COAL) || stack.is(Items.REDSTONE) || stack.is(Items.LAPIS_LAZULI)
                || stack.is(Items.DIAMOND) || stack.is(Items.EMERALD);
    }

    public static Component createDrillToggleMessage(String text, int darkColor, int brightColor) {
        MutableComponent result = Component.empty();
        int len = text.length();
        int half = len / 2;
        for (int i = 0; i < len; i++) {
            float ratio;
            int color;
            if (i <= half) {
                ratio = half > 0 ? (float) i / half : 0f;
                color = interpolateColor(darkColor, brightColor, ratio);
            } else {
                ratio = (len - 1 - half) > 0 ? (float) (i - half) / (len - 1 - half) : 0f;
                color = interpolateColor(brightColor, darkColor, ratio);
            }
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(true);
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }
        return result;
    }

    public static int interpolateColor(int start, int end, float ratio) {
        int r1 = (start >> 16) & 0xFF, g1 = (start >> 8) & 0xFF, b1 = start & 0xFF;
        int r2 = (end >> 16) & 0xFF, g2 = (end >> 8) & 0xFF, b2 = end & 0xFF;
        int r = Math.round(r1 + (r2 - r1) * ratio);
        int g = Math.round(g1 + (g2 - g1) * ratio);
        int b = Math.round(b1 + (b2 - b1) * ratio);
        return (r << 16) | (g << 8) | b;
    }
}