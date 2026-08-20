package com.infinitybackpack.registry;

import com.infinitybackpack.screen.BackpackMenu;
import com.infinitybackpack.screen.ExpExchangeMenu;
import com.infinitybackpack.screen.TntCannonMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
    public static final MenuType<BackpackMenu> BACKPACK_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "backpack"),
            new MenuType<>(BackpackMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<TntCannonMenu> TNT_CANNON_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "tnt_cannon"),
            new MenuType<>(TntCannonMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType<ExpExchangeMenu> EXP_EXCHANGE_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "exp_exchange"),
            new MenuType<>(ExpExchangeMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static void init() {}
}