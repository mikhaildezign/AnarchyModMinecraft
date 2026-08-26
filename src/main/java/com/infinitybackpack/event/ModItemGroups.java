package com.infinitybackpack.event;

import com.infinitybackpack.registry.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class ModItemGroups {

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(ModItems.INFINITY_BACKPACK);
            content.accept(ModItems.EXPLOSIVE_MATERIAL);
            content.accept(ModItems.DYNAMITE_A_ITEM);
            content.accept(ModItems.DYNAMITE_B_ITEM);
            content.accept(ModItems.DYNAMITE_B2_ITEM);
            content.accept(ModItems.DYNAMITE_C4_ITEM);
            content.accept(ModItems.SHOCKWAVE_ITEM);
            content.accept(ModItems.STILLER_ITEM);
            content.accept(ModItems.RELIABLE_STILLER_ITEM);
            content.accept(ModItems.GOLDEN_PICKAXE_JAKE);
            content.accept(ModItems.TNT_CANNON_ITEM);
            content.accept(ModItems.ARMORED_ELYTRA);
            content.accept(ModItems.UNBREAKABLE_ELYTRA);
            content.accept(ModItems.JET_ELYTRA);
            content.accept(ModItems.JET_ARMORED_ELYTRA);
            content.accept(ModItems.JET_UNBREAKABLE_ELYTRA);
            content.accept(ModItems.COMBINED_ELYTRA);
            content.accept(ModItems.EXP_BOTTLE_15);
            content.accept(ModItems.EXP_BOTTLE_30);
            content.accept(ModItems.EXP_BOTTLE_50);
            content.accept(ModItems.EXP_BOTTLE_100);
            content.accept(ModItems.INFINITY_TALISMAN);
            content.accept(ModItems.SNOWBALL_CLUMP_ITEM);
            content.accept(ModItems.SUN_HELMET);
            content.accept(ModItems.INFINITY_HELMET);
            content.accept(ModItems.INFINITY_CHESTPLATE);
            content.accept(ModItems.INFINITY_LEGGINGS);
            content.accept(ModItems.INFINITY_BOOTS);
            content.accept(ModItems.SUN_BOOTS);
            content.accept(ModItems.INFINITY_PICKAXE);
            content.accept(ModItems.INFINITY_SHOVEL);
            content.accept(ModItems.STASIS);
            content.accept(ModItems.ENHANCED_STRENGTH_3MIN);
            content.accept(ModItems.ENHANCED_STRENGTH_6MIN);
            content.accept(ModItems.ENHANCED_SWIFTNESS_3MIN);
            content.accept(ModItems.ENHANCED_SWIFTNESS_6MIN);
            content.accept(ModItems.WINNER_POTION);
            content.accept(ModItems.IMMORTALITY_RUNE);
            content.accept(ModItems.RECOVERY_RUNE);
            content.accept(ModItems.SNOWMAN);
        });
    }
}