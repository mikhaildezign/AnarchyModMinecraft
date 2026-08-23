package com.infinitybackpack.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class ModPotions {
    public static final Holder<Potion> ENHANCED_STRENGTH_3MIN = registerPotion("enhanced_strength_3min",
            new Potion("enhanced_strength_3min", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 2)));

    public static final Holder<Potion> ENHANCED_STRENGTH_6MIN = registerPotion("enhanced_strength_6min",
            new Potion("enhanced_strength_6min", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 7200, 2)));

    public static final Holder<Potion> ENHANCED_SWIFTNESS_3MIN = registerPotion("enhanced_swiftness_3min",
            new Potion("enhanced_swiftness_3min", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 2)));

    public static final Holder<Potion> ENHANCED_SWIFTNESS_6MIN = registerPotion("enhanced_swiftness_6min",
            new Potion("enhanced_swiftness_6min", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 7200, 2)));

    public static final Holder<Potion> WINNER_POTION = registerPotion("winner_potion",
            new Potion("winner_potion",
                    new MobEffectInstance(MobEffects.DAMAGE_BOOST, 9600, 2),
                    new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 9600, 2),
                    new MobEffectInstance(MobEffects.INVISIBILITY, 9600, 0),
                    new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1),
                    new MobEffectInstance(MobEffects.REGENERATION, 600, 1),
                    new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1),
                    new MobEffectInstance(MobEffects.HEAL, 1, 1)
            ));

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name), potion);
    }

    public static void init() {}
}