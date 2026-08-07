package com.infinitybackpack.client.renderer;

import com.infinitybackpack.dynamite.CustomPrimedTnt;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.state.BlockState;

public class CustomPrimedTntRenderer extends TntRenderer {
    private final BlockRenderDispatcher blockRenderer;

    public CustomPrimedTntRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(PrimedTnt entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity instanceof CustomPrimedTnt custom && custom.isCannonProjectile()) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.5F, 0.0F);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            BlockState blockState = custom.getBlockState();
            this.blockRenderer.renderSingleBlock(blockState, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();

            if (custom.isCustomNameVisible() && custom.getCustomName() != null) {
                renderNameTag(custom, custom.getCustomName(), poseStack, buffer, packedLight, partialTick);
            }
        } else {
            super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        }
    }
}