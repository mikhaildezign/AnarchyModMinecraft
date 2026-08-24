package com.infinitybackpack.command;

import com.infinitybackpack.registry.ModConstants;
import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.registry.ModMenus;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ModCommands {

    public static void init() {
        registerExpCommand();
        registerElytraTestCommand();
        registerFilterCommand();
        registerTestCriticalCommand();
        registerTestImpenetrableCommand();
    }

    private static void registerExpCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("exp")
                    .executes(context -> {
                        if (context.getSource().getEntity() instanceof ServerPlayer player) {
                            player.openMenu(new SimpleMenuProvider(
                                    (syncId, inv, p) -> new com.infinitybackpack.screen.ExpExchangeMenu(syncId, inv),
                                    Component.literal("Обмен опыта")
                            ));
                            return 1;
                        }
                        return 0;
                    })
            );
        });
    }

    private static void registerElytraTestCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("elytratest")
                    .requires(source -> source.hasPermission(0))
                    .executes(context -> {
                        if (context.getSource().getEntity() instanceof ServerPlayer player) {
                            Vec3 motion = player.getDeltaMovement();
                            double hSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
                            boolean flying = player.isFallFlying();
                            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
                            String itemName = chest.isEmpty() ? "none" : chest.getItem().toString();

                            player.sendSystemMessage(Component.literal(
                                    "FallFlying: " + flying + " | Item: " + itemName + " | H-Speed: " + String.format("%.3f", hSpeed)
                            ));
                            return 1;
                        }
                        return 0;
                    }));
        });
    }

    private static void registerFilterCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("filter")
                    .then(Commands.literal("check")
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                ItemStack tool = player.getMainHandItem();

                                if (ModItems.getFilterLevel(tool) <= 0) {
                                    player.displayClientMessage(
                                            Component.literal("В вашем инструменте нет зачарования Фильтр!")
                                                    .withStyle(Style.EMPTY.withColor(0xFF0000)), false);
                                    return 0;
                                }

                                List<Item> filters = ModItems.getPlayerFilterItems(player);
                                if (filters.isEmpty()) {
                                    player.displayClientMessage(
                                            Component.literal("У вас нет отключённых предметов для выпадения.")
                                                    .withStyle(Style.EMPTY.withColor(0xAAAAAA)), false);
                                } else {
                                    for (Item item : filters) {
                                        MutableComponent itemName = Component.literal(item.getDefaultInstance().getHoverName().getString());
                                        Component msg = Component.literal("У вас отключён/а для выпадение - ")
                                                .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                                                .append(itemName.withStyle(Style.EMPTY.withColor(0xFF0000)))
                                                .append(Component.literal("!").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
                                        player.displayClientMessage(msg, false);
                                    }
                                }
                                return 1;
                            }))
                    .then(Commands.literal("set")
                            .then(Commands.argument("item", StringArgumentType.greedyString())
                                    .suggests((context, builder) -> {
                                        String remaining = builder.getRemaining().toLowerCase();
                                        for (Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
                                            String translated = item.getDefaultInstance().getHoverName().getString();
                                            if (translated.toLowerCase().startsWith(remaining)) {
                                                builder.suggest(translated);
                                            }
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> {
                                        ServerPlayer player = context.getSource().getPlayerOrException();
                                        ItemStack tool = player.getMainHandItem();

                                        if (ModItems.getFilterLevel(tool) <= 0) {
                                            player.displayClientMessage(
                                                    Component.literal("В вашем инструменте нет зачарования Фильтр!")
                                                            .withStyle(Style.EMPTY.withColor(0xFF0000)), false);
                                            return 0;
                                        }

                                        String input = StringArgumentType.getString(context, "item");
                                        Item targetItem = ModItems.findItemByTranslatedName(input);

                                        if (targetItem == null) {
                                            player.displayClientMessage(
                                                    Component.literal("Предмет не найден!")
                                                            .withStyle(Style.EMPTY.withColor(0xFF0000)), false);
                                            return 0;
                                        }

                                        boolean removed = ModItems.togglePlayerFilterItem(player, targetItem);
                                        MutableComponent itemName = Component.literal(targetItem.getDefaultInstance().getHoverName().getString());

                                        if (removed) {
                                            Component msg = Component.literal("Вы включили для выпадение - ")
                                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                                                    .append(itemName.withStyle(Style.EMPTY.withColor(0x00FF00)))
                                                    .append(Component.literal("!").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
                                            player.displayClientMessage(msg, false);
                                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                                    SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 2.0f, 1.0f);
                                        } else {
                                            Component msg = Component.literal("Вы отключили для выпадение - ")
                                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                                                    .append(itemName.withStyle(Style.EMPTY.withColor(0xFF0000)))
                                                    .append(Component.literal("!").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
                                            player.displayClientMessage(msg, false);
                                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                                    SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 2.0f, 1.0f);
                                        }
                                        return 1;
                                    }))));
        });
    }

    private static void registerTestCriticalCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("testcritical")
                    .requires(source -> source.hasPermission(0))
                    .executes(context -> {
                        if (context.getSource().getEntity() instanceof ServerPlayer player) {
                            boolean enabled = ModConstants.TEST_CRITICAL_PLAYERS.getOrDefault(player.getUUID(), false);
                            ModConstants.TEST_CRITICAL_PLAYERS.put(player.getUUID(), !enabled);
                            player.sendSystemMessage(Component.literal(
                                    "Режим теста Критический: " + (!enabled ? "ВКЛЮЧЕН" : "ВЫКЛЮЧЕН")
                            ).withStyle(Style.EMPTY.withColor(!enabled ? 0x00FF00 : 0xFF0000)));
                            return 1;
                        }
                        return 0;
                    }));
        });
    }

    private static void registerTestImpenetrableCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("impenetrabletest")
                    .requires(source -> source.hasPermission(0))
                    .executes(context -> {
                        if (context.getSource().getEntity() instanceof ServerPlayer player) {
                            boolean enabled = ModConstants.TEST_IMPENETRABLE_PLAYERS.getOrDefault(player.getUUID(), false);
                            ModConstants.TEST_IMPENETRABLE_PLAYERS.put(player.getUUID(), !enabled);
                            player.sendSystemMessage(Component.literal(
                                    "Режим теста Непробиваемый: " + (!enabled ? "ВКЛЮЧЕН" : "ВЫКЛЮЧЕН")
                            ).withStyle(Style.EMPTY.withColor(!enabled ? 0x00FF00 : 0xFF0000)));
                            return 1;
                        }
                        return 0;
                    }));
        });
    }
}