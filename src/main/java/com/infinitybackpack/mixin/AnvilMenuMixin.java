package com.infinitybackpack.mixin;

import com.infinitybackpack.registry.ModItems;
import com.infinitybackpack.registry.ModEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow @Final private DataSlot cost;
    @Shadow private int repairItemCountCost;

    private AnvilMenuMixin(MenuType<?> type, int id, Inventory inventory, ContainerLevelAccess access) {
        super(type, id, inventory, access);
    }

    // === РЕЦЕПТЫ РУН + ЗЕЛИЙ ===
    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void onCreateResult(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu)(Object)this;
        ItemStack left = menu.getSlot(0).getItem();
        ItemStack right = menu.getSlot(1).getItem();

        // --- РУНА БЕССМЕРТИЕ ---
        if ((left.is(Items.TOTEM_OF_UNDYING) || left.is(ModItems.INFINITY_TALISMAN)) && right.is(ModItems.IMMORTALITY_RUNE)) {
            if (!hasRune(left, "immortality")) {
                ItemStack result = applyRune(left.copy(), "immortality");
                menu.getSlot(2).set(result);
                this.cost.set(10);
                this.repairItemCountCost = 1;
                ci.cancel();
                return;
            }
        }

        // --- РУНА ВОССТАНОВЛЕНИЕ ---
        if ((left.is(Items.TOTEM_OF_UNDYING) || left.is(ModItems.INFINITY_TALISMAN)) && right.is(ModItems.RECOVERY_RUNE)) {
            if (!hasRune(left, "recovery")) {
                ItemStack result = applyRune(left.copy(), "recovery");
                menu.getSlot(2).set(result);
                this.cost.set(10);
                this.repairItemCountCost = 1;
                ci.cancel();
                return;
            }
        }

        if (isVanillaStrengthII(left) && isVanillaStrengthII(right)) {
            int count = Math.min(left.getCount(), right.getCount());
            menu.getSlot(2).set(new ItemStack(ModItems.ENHANCED_STRENGTH_3MIN, count));
            this.cost.set(10);
            this.repairItemCountCost = count;
            ci.cancel();
            return;
        }

        if (left.is(ModItems.ENHANCED_STRENGTH_3MIN) && right.is(ModItems.ENHANCED_STRENGTH_3MIN)) {
            int count = Math.min(left.getCount(), right.getCount());
            menu.getSlot(2).set(new ItemStack(ModItems.ENHANCED_STRENGTH_6MIN, count));
            this.cost.set(30);
            this.repairItemCountCost = count;
            ci.cancel();
            return;
        }

        if (isVanillaSwiftnessII(left) && isVanillaSwiftnessII(right)) {
            int count = Math.min(left.getCount(), right.getCount());
            menu.getSlot(2).set(new ItemStack(ModItems.ENHANCED_SWIFTNESS_3MIN, count));
            this.cost.set(10);
            this.repairItemCountCost = count;
            ci.cancel();
            return;
        }

        if (left.is(ModItems.ENHANCED_SWIFTNESS_3MIN) && right.is(ModItems.ENHANCED_SWIFTNESS_3MIN)) {
            int count = Math.min(left.getCount(), right.getCount());
            menu.getSlot(2).set(new ItemStack(ModItems.ENHANCED_SWIFTNESS_6MIN, count));
            this.cost.set(30);
            this.repairItemCountCost = count;
            ci.cancel();
            return;
        }
    }

    // === ПРАВИЛЬНОЕ УМЕНЬШЕНИЕ СЛОТОВ ===
    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void onTake(Player player, ItemStack stack, CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu)(Object)this;
        ItemStack result = menu.getSlot(2).getItem();
        if (result.isEmpty()) return;

        // --- РУНЫ ---
        if (hasRune(result, "immortality") || hasRune(result, "recovery")) {
            ItemStack left = this.inputSlots.getItem(0);
            ItemStack right = this.inputSlots.getItem(1);

            if (!left.isEmpty()) {
                left.shrink(1);
                this.inputSlots.setItem(0, left.isEmpty() ? ItemStack.EMPTY : left);
            }
            if (!right.isEmpty()) {
                right.shrink(1);
                this.inputSlots.setItem(1, right.isEmpty() ? ItemStack.EMPTY : right);
            }

            if (!player.getAbilities().instabuild) {
                player.giveExperienceLevels(-this.cost.get());
            }

            this.access.execute((world, pos) -> world.levelEvent(1030, pos, 0));
            ci.cancel();
            return;
        }

        if (!isEnhancedPotion(result)) return;

        int count = result.getCount();

        ItemStack left = this.inputSlots.getItem(0);
        if (!left.isEmpty()) {
            left.shrink(count);
            this.inputSlots.setItem(0, left.isEmpty() ? ItemStack.EMPTY : left);
        } else {
            this.inputSlots.setItem(0, ItemStack.EMPTY);
        }

        ItemStack right = this.inputSlots.getItem(1);
        if (!right.isEmpty()) {
            right.shrink(count);
            this.inputSlots.setItem(1, right.isEmpty() ? ItemStack.EMPTY : right);
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }

        this.access.execute((world, pos) -> world.levelEvent(1030, pos, 0));

        ci.cancel();
    }

    // === СТАРАЯ ЛОГИКА ЗАЧАРОВАНИЙ ===
    @Inject(method = "createResult", at = @At("RETURN"))
    private void preventLevelUp(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu)(Object)this;
        if (!menu.getSlot(2).hasItem()) return;

        ItemStack result = menu.getSlot(2).getItem();
        ItemStack left = menu.getSlot(0).getItem();
        ItemStack right = menu.getSlot(1).getItem();

        if (hasEnchantmentLevel(result, ModEnchantments.DRILL, 2)) {
            if (!hasEnchantmentLevel(left, ModEnchantments.DRILL, 2) && !hasEnchantmentLevel(right, ModEnchantments.DRILL, 2)) {
                setEnchantmentLevel(result, ModEnchantments.DRILL, 1);
            }
        }

        if (hasEnchantmentLevel(result, ModEnchantments.IMPENETRABLE, 2)) {
            if (!hasEnchantmentLevel(left, ModEnchantments.IMPENETRABLE, 2) && !hasEnchantmentLevel(right, ModEnchantments.IMPENETRABLE, 2)) {
                setEnchantmentLevel(result, ModEnchantments.IMPENETRABLE, 1);
            }
        }

        if (hasEnchantment(result, ModEnchantments.AUTOSMELT) && hasEnchantment(result, Enchantments.SILK_TOUCH)) {
            removeEnchantment(result, Enchantments.SILK_TOUCH);
        }
    }

    // === ХЕЛПЕРЫ ДЛЯ РУН ===
    private boolean hasRune(ItemStack stack, String runeType) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return false;
        CompoundTag tag = customData.copyTag();
        // Новый формат — список
        if (tag.contains("RuneTypes", Tag.TAG_LIST)) {
            ListTag list = tag.getList("RuneTypes", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                if (list.getString(i).equals(runeType)) return true;
            }
            return false;
        }
        // Старый формат — обратная совместимость
        if (tag.contains("RuneType", Tag.TAG_STRING)) {
            return tag.getString("RuneType").equals(runeType);
        }
        return false;
    }

    private ItemStack applyRune(ItemStack stack, String runeType) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        ListTag list = new ListTag();

        // Миграция старого формата
        if (tag.contains("RuneType", Tag.TAG_STRING)) {
            list.add(StringTag.valueOf(tag.getString("RuneType")));
            tag.remove("RuneType");
        }

        if (tag.contains("RuneTypes", Tag.TAG_LIST)) {
            ListTag existing = tag.getList("RuneTypes", Tag.TAG_STRING);
            for (int i = 0; i < existing.size(); i++) {
                list.add(existing.get(i));
            }
        }

        boolean alreadyHas = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(runeType)) {
                alreadyHas = true;
                break;
            }
        }
        if (!alreadyHas) {
            list.add(StringTag.valueOf(runeType));
        }
        tag.put("RuneTypes", list);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    // === ХЕЛПЕРЫ ДЛЯ ЗЕЛИЙ ===
    private boolean isVanillaStrengthII(ItemStack stack) {
        if (!stack.is(Items.POTION)) return false;
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return false;
        return contents.potion().isPresent() && contents.potion().get().is(Potions.STRONG_STRENGTH);
    }

    private boolean isVanillaSwiftnessII(ItemStack stack) {
        if (!stack.is(Items.POTION)) return false;
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return false;
        return contents.potion().isPresent() && contents.potion().get().is(Potions.STRONG_SWIFTNESS);
    }

    private boolean isEnhancedPotion(ItemStack stack) {
        return stack.is(ModItems.ENHANCED_STRENGTH_3MIN) ||
                stack.is(ModItems.ENHANCED_STRENGTH_6MIN) ||
                stack.is(ModItems.ENHANCED_SWIFTNESS_3MIN) ||
                stack.is(ModItems.ENHANCED_SWIFTNESS_6MIN);
    }

    // === ХЕЛПЕРЫ ДЛЯ ЗАЧАРОВАНИЙ ===
    private static boolean hasEnchantmentLevel(ItemStack stack, ResourceKey key, int level) {
        return getEnchantmentLevel(stack, key) == level;
    }

    private static int getEnchantmentLevel(ItemStack stack, ResourceKey key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    private static boolean hasEnchantment(ItemStack stack, ResourceKey key) {
        return getEnchantmentLevel(stack, key) > 0;
    }

    private static void removeEnchantment(ItemStack stack, ResourceKey key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                mutable.removeIf(h -> h.is(key));
                stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stored);
                mutable.removeIf(h -> h.is(key));
                stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
    }

    private static void setEnchantmentLevel(ItemStack stack, ResourceKey key, int level) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                mutable.set(entry.getKey(), level);
                stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (entry.getKey().is(key)) {
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stored);
                mutable.set(entry.getKey(), level);
                stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
                return;
            }
        }
    }
}