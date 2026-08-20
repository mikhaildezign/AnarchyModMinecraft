package com.infinitybackpack.registry;

import com.infinitybackpack.block.TntCannonBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final BlockEntityType<TntCannonBlockEntity> TNT_CANNON_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "tnt_cannon"),
            BlockEntityType.Builder.of(TntCannonBlockEntity::new, ModBlocks.TNT_CANNON_BLOCK).build()
    );

    public static void init() {}
}