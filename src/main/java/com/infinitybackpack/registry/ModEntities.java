package com.infinitybackpack.registry;

import com.infinitybackpack.dynamite.CustomPrimedTnt;
import com.infinitybackpack.item.SnowballClumpProjectile;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<CustomPrimedTnt> CUSTOM_PRIMED_TNT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "custom_primed_tnt"),
            EntityType.Builder.of((EntityType<CustomPrimedTnt> type, net.minecraft.world.level.Level level) -> new CustomPrimedTnt(type, level), MobCategory.MISC)
                    .fireImmune()
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(8)
                    .updateInterval(10)
                    .build("infinitybackpack:custom_primed_tnt")
    );

    public static final EntityType<SnowballClumpProjectile> SNOWBALL_CLUMP_PROJECTILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "snowball_clump"),
            EntityType.Builder.of((EntityType<SnowballClumpProjectile> type, net.minecraft.world.level.Level level) -> new SnowballClumpProjectile(type, level), MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("infinitybackpack:snowball_clump")
    );

    public static void init() {}
}