package com.infinitybackpack.client;

import com.infinitybackpack.network.StasisSyncPayload;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.registry.ModMenus;
import com.infinitybackpack.registry.ModEntities;
import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.client.renderer.CustomPrimedTntRenderer;
import com.infinitybackpack.client.renderer.ShulkerBoxItemRenderer;
import com.infinitybackpack.client.screen.BackpackScreen;
import com.infinitybackpack.client.screen.ExpExchangeScreen;
import com.infinitybackpack.client.screen.TntCannonScreen;
import com.infinitybackpack.network.ToggleAutoSmeltPayload;
import com.infinitybackpack.network.ToggleDrillPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public class InfinityBackpackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenus.BACKPACK_MENU, BackpackScreen::new);
        MenuScreens.register(ModMenus.TNT_CANNON_MENU, TntCannonScreen::new);
        MenuScreens.register(ModMenus.EXP_EXCHANGE_MENU, ExpExchangeScreen::new);

        EntityRendererRegistry.register(ModEntities.CUSTOM_PRIMED_TNT, CustomPrimedTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.SNOWBALL_CLUMP_PROJECTILE, context -> new ThrownItemRenderer<>(context, 1.5f, true));

        BuiltinItemRendererRegistry.INSTANCE.register(
                ModItems.INFINITY_BACKPACK,
                new ShulkerBoxItemRenderer()
        );

        ClientPlayNetworking.registerGlobalReceiver(StasisSyncPayload.TYPE, (payload, context) -> {
            com.infinitybackpack.registry.ModConstants.CLIENT_STASIS_ZONES.clear();
            com.infinitybackpack.registry.ModConstants.CLIENT_STASIS_ZONES.addAll(payload.zones());
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClientSide) {
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }

            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) {
                return InteractionResultHolder.pass(stack);
            }

            // Ctrl + ПКМ — Автоплавка
            if (Screen.hasControlDown() && !Screen.hasShiftDown()) {
                int autoSmeltLevel = ModItems.getAutoSmeltLevel(stack);
                if (autoSmeltLevel > 0) {
                    ClientPlayNetworking.send(new ToggleAutoSmeltPayload());
                    return InteractionResultHolder.success(stack);
                }
            }

            // Shift + ПКМ — Бур
            if (Screen.hasShiftDown() && !Screen.hasControlDown()) {
                int drillLevel = ModItems.getDrillLevel(stack);
                if (drillLevel > 0) {
                    ClientPlayNetworking.send(new ToggleDrillPayload());
                    return InteractionResultHolder.success(stack);
                }
            }

            return InteractionResultHolder.pass(stack);
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(ModItems.SUN_HELMET)) {
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

            if (stack.is(ModItems.SUN_BOOTS)) {
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