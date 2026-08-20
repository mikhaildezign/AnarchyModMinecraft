package com.infinitybackpack.registry;

import com.infinitybackpack.block.TntCannonBlock;
import com.infinitybackpack.dynamite.CustomTntBlock;
import com.infinitybackpack.dynamite.DynamiteType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Block DYNAMITE_A_BLOCK = registerBlock("dynamite_a",
            new CustomTntBlock(DynamiteType.DYNAMITE_A, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Block DYNAMITE_B_BLOCK = registerBlock("dynamite_b",
            new CustomTntBlock(DynamiteType.DYNAMITE_B, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Block DYNAMITE_B2_BLOCK = registerBlock("dynamite_b2",
            new CustomTntBlock(DynamiteType.DYNAMITE_B2, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Block DYNAMITE_C4_BLOCK = registerBlock("dynamite_c4",
            new CustomTntBlock(DynamiteType.DYNAMITE_C4, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Block SHOCKWAVE_BLOCK = registerBlock("shockwave",
            new CustomTntBlock(DynamiteType.SHOCKWAVE, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Block STILLER_BLOCK = registerBlock("stiller",
            new CustomTntBlock(DynamiteType.STILLER, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Block RELIABLE_STILLER_BLOCK = registerBlock("reliable_stiller",
            new CustomTntBlock(DynamiteType.RELIABLE_STILLER, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Block TNT_CANNON_BLOCK = registerBlock("tnt_cannon",
            new TntCannonBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name), block);
    }

    public static void init() {}
}