package com.infinitybackpack.client.renderer;

import com.infinitybackpack.InfinityBackpackMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            InfinityBackpackMod.MOD_ID, "textures/item/shulker_lime.png"
    );

    private ModelPart shulkerRoot;

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (shulkerRoot == null) {
            shulkerRoot = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER);
        }

        poseStack.pushPose();

        if (mode == ItemDisplayContext.GUI) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.625f, 0.625f, 0.625f);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(30));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(0));
            poseStack.translate(-0.5, -0.5, -0.5);
        }

        if (mode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            poseStack.translate(0.0, 0.9, 0.9);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(75));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(0));
            poseStack.mulPose(Direction.UP.getRotation());
            poseStack.scale(1.0F, -1.0F, -1.0F);
        } else if (mode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.scale(0.4f, 0.4f, 0.4f);
            poseStack.translate(0, 1, 0.1);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(0));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(225));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(0));
            poseStack.mulPose(Direction.UP.getRotation());
            poseStack.scale(1.0F, -1.0F, -1.0F);
        } else {
            // ← ДОБАВЛЕНО: уменьшение при выбрасывании (GROUND)
            if (mode == ItemDisplayContext.GROUND) {
                poseStack.scale(0.35f, 0.35f, 0.35f);
            }
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.scale(0.9995F, 0.9995F, 0.9995F);
            poseStack.mulPose(Direction.UP.getRotation());
            poseStack.scale(1.0F, -1.0F, -1.0F);
            poseStack.translate(0.0D, -1.0D, 0.0D);
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        shulkerRoot.getChild("base").render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF);
        shulkerRoot.getChild("lid").render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF);

        poseStack.popPose();
    }
}