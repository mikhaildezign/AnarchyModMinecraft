package com.infinitybackpack.client;

import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.client.renderer.CustomPrimedTntRenderer;
import com.infinitybackpack.client.screen.BackpackScreen;
import com.infinitybackpack.client.screen.ExpExchangeScreen;
import com.infinitybackpack.client.screen.TntCannonScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class InfinityBackpackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(InfinityBackpackMod.BACKPACK_MENU, BackpackScreen::new);
        MenuScreens.register(InfinityBackpackMod.TNT_CANNON_MENU, TntCannonScreen::new);
        MenuScreens.register(InfinityBackpackMod.EXP_EXCHANGE_MENU, ExpExchangeScreen::new);

        EntityRendererRegistry.register(InfinityBackpackMod.CUSTOM_PRIMED_TNT, CustomPrimedTntRenderer::new);
        EntityRendererRegistry.register(InfinityBackpackMod.SNOWBALL_CLUMP_PROJECTILE, context -> new ThrownItemRenderer<>(context, 1.5f, true));

        // Тултип шлема солнца — убираем стандартные атрибуты, оставляем зачарования + наше описание
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(InfinityBackpackMod.SUN_HELMET)) {
                lines.removeIf(line -> {
                    String s = line.getString();
                    if (s.contains("Когда надето") || s.contains("Когда надевается")
                            || s.contains("When on") || s.contains("When equipped")) {
                        return true;
                    }
                    if (s.startsWith("+") || s.contains("Скорость добычи под водой")
                            || s.contains("Бонус к кислороду")) {
                        return true;
                    }
                    return false;
                });

                lines.add(Component.literal("Особенности:")
                        .withStyle(Style.EMPTY.withColor(0x555555)));
                lines.add(Component.literal("— имеет свойства ")
                        .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                        .append(Component.literal("незеритового шлема").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                        .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
                lines.add(Component.literal("— полностью ")
                        .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                        .append(Component.literal("неразрушим").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                        .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
                lines.add(Component.literal("— возможно накладывать ")
                        .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                        .append(Component.literal("зачарования").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                        .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
            }

            if (stack.is(InfinityBackpackMod.SUN_BOOTS)) {
                lines.removeIf(line -> {
                    String s = line.getString();
                    if (s.contains("Когда обуто") || s.contains("Когда надето")
                            || s.contains("Когда надевается")
                            || s.contains("When on") || s.contains("When equipped")) {
                        return true;
                    }
                    if (s.startsWith("+") || s.startsWith("-")) {
                        return true;
                    }
                    return false;
                });

                lines.add(Component.literal("Особенности:")
                        .withStyle(Style.EMPTY.withColor(0x555555)));
                lines.add(Component.literal("— имеет свойства ")
                        .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                        .append(Component.literal("незеритовых ботинок").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                        .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
                lines.add(Component.literal("— полностью ")
                        .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                        .append(Component.literal("неразрушимы").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                        .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
                lines.add(Component.literal("— возможно накладывать ")
                        .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                        .append(Component.literal("зачарования").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                        .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF))));
            }
        });
    }
}