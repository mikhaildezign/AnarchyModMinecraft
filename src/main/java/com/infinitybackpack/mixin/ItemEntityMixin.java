package com.infinitybackpack.mixin;

import com.infinitybackpack.registry.ModConstants;
import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.registry.ModUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void onInit(Level level, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        System.out.println("[AUTOSMELT DEBUG] ItemEntity created: " + stack.getItem() + " x" + stack.getCount());

        if (level.isClientSide) {
            System.out.println("[AUTOSMELT DEBUG] Client side, skipping");
            return;
        }
        if (stack.isEmpty()) {
            System.out.println("[AUTOSMELT DEBUG] Stack empty, skipping");
            return;
        }

        ItemEntity self = (ItemEntity) (Object) this;
        boolean foundPlayer = false;

        for (var player : level.players()) {
            double dx = player.getX() - x;
            double dy = player.getY() - y;
            double dz = player.getZ() - z;
            double distSq = dx * dx + dy * dy + dz * dz;
            System.out.println("[AUTOSMELT DEBUG] Player " + player.getName().getString() + " distance^2 = " + distSq);

            if (distSq > 64.0) continue;

            ItemStack tool = player.getMainHandItem();
            System.out.println("[AUTOSMELT DEBUG] Tool in main hand: " + tool.getItem());

            int autoSmeltLevel = ModItems.getAutoSmeltLevel(tool);
            System.out.println("[AUTOSMELT DEBUG] AutoSmelt level = " + autoSmeltLevel);

            if (autoSmeltLevel <= 0) continue;

            CustomData customData = tool.get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.copyTag().getBoolean("AutoSmeltDisabled")) {
                System.out.println("[AUTOSMELT DEBUG] AutoSmelt is disabled on this tool, skipping");
                continue;
            }

            boolean isPickaxe = tool.is(ModConstants.PICKAXES_TAG);
            boolean isShovel = tool.is(ModConstants.SHOVELS_TAG);
            System.out.println("[AUTOSMELT DEBUG] isPickaxe = " + isPickaxe + ", isShovel = " + isShovel);

            if (!isPickaxe && !isShovel) continue;

            if (level instanceof ServerLevel serverLevel) {
                Optional<RecipeHolder<SmeltingRecipe>> recipeOpt = serverLevel.getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), serverLevel);

                System.out.println("[AUTOSMELT DEBUG] Recipe found = " + recipeOpt.isPresent());

                if (recipeOpt.isPresent()) {
                    ItemStack smelted = recipeOpt.get().value()
                            .getResultItem(serverLevel.registryAccess()).copy();
                    smelted.setCount(stack.getCount());

                    // Кирка — только руды; Лопата — только стекло (песок)
                    if (isPickaxe && !ModUtils.isOreSmeltingResult(smelted)) {
                        System.out.println("[AUTOSMELT DEBUG] Pickaxe: result is not an ore, skipping");
                        continue;
                    }
                    if (isShovel && !smelted.is(Items.GLASS)) {
                        System.out.println("[AUTOSMELT DEBUG] Shovel: result is not glass, skipping");
                        continue;
                    }

                    boolean shouldSmelt = false;
                    if (isPickaxe && !stack.is(Items.SAND) && !stack.is(Items.RED_SAND)) {
                        shouldSmelt = true;
                    } else if (isShovel && (stack.is(Items.SAND) || stack.is(Items.RED_SAND))) {
                        shouldSmelt = true;
                    }

                    System.out.println("[AUTOSMELT DEBUG] shouldSmelt = " + shouldSmelt);

                    if (shouldSmelt) {
                        self.setItem(smelted);
                        System.out.println("[AUTOSMELT DEBUG] SMELTED! New item: " + smelted.getItem());
                    }
                }
            }
            foundPlayer = true;
            break;
        }

        if (!foundPlayer) {
            System.out.println("[AUTOSMELT DEBUG] No suitable player found");
        }
    }
}