package com.infinitybackpack.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> IMPENETRABLE = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "impenetrable")
    );

    public static final ResourceKey<Enchantment> MAGNETISM = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "magnetism")
    );

    public static final ResourceKey<Enchantment> DRILL = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "drill")
    );

    public static final ResourceKey<Enchantment> AUTOSMELT = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "autosmelt")
    );

    public static final ResourceKey<Enchantment> UNBREAKABLE_ENCHANT = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "unbreakable")
    );

    public static final ResourceKey<Enchantment> FILTER = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "filter")
    );

    public static final ResourceKey<Enchantment> CRITICAL = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "critical")
    );

    public static void init() {
        // ResourceKey регистрируются лениво через JSON, здесь только объявления
    }
}