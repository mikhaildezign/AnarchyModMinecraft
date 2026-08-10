package com.infinitybackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ShulkerBoxItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        var dispatcher = Minecraft.getInstance().getBlockRenderer();
        poseStack.pushPose();
        poseStack.translate(-0.5, -0.5, -0.5);
        dispatcher.renderSingleBlock(Blocks.LIME_SHULKER_BOX.defaultBlockState(), poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}