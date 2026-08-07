package com.infinitybackpack.block;

import com.infinitybackpack.InfinityBackpackMod;
import com.infinitybackpack.dynamite.CustomPrimedTnt;
import com.infinitybackpack.dynamite.CustomTntBlock;
import com.infinitybackpack.dynamite.DynamiteType;
import com.infinitybackpack.item.DynamiteBlockItem;
import com.infinitybackpack.screen.TntCannonMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TntCannonBlockEntity extends BlockEntity implements Container, MenuProvider {
    private ItemStack ammo = ItemStack.EMPTY;
    private int cooldown = 0;
    public static final int COOLDOWN_TICKS = 60;

    public TntCannonBlockEntity(BlockPos pos, BlockState state) {
        super(InfinityBackpackMod.TNT_CANNON_BLOCK_ENTITY, pos, state);
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) return;
        if (cooldown > 0) {
            cooldown--;
            setChanged();
        }
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, TntCannonBlockEntity blockEntity) {
        blockEntity.tick();
    }

    public boolean canShoot() {
        return cooldown <= 0 && !ammo.isEmpty();
    }

    public ItemStack extractAmmo() {
        if (!canShoot()) return ItemStack.EMPTY;
        cooldown = COOLDOWN_TICKS;
        ItemStack extracted = ammo.split(1);
        setChanged();
        return extracted;
    }

    public int getCooldown() {
        return cooldown;
    }

    public static boolean isValidAmmo(ItemStack stack) {
        if (stack.isEmpty()) return true;
        return stack.is(Items.TNT) || stack.getItem() instanceof DynamiteBlockItem;
    }

    public void tryShoot(BlockState state) {
        if (!canShoot()) return;

        ItemStack ammo = extractAmmo();
        if (ammo.isEmpty()) return;

        Direction facing = state.getValue(TntCannonBlock.FACING);
        Vec3 spawnPos = Vec3.atCenterOf(this.worldPosition).add(
                facing.getStepX() * 0.8,
                facing.getStepY() * 0.8,
                facing.getStepZ() * 0.8
        );

        CustomPrimedTnt projectile = new CustomPrimedTnt(this.level, spawnPos.x, spawnPos.y, spawnPos.z, null);

        DynamiteType type;
        if (ammo.is(Items.TNT)) {
            type = DynamiteType.VANILLA;
        } else if (ammo.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CustomTntBlock customBlock) {
            type = customBlock.getDynamiteType();
        } else {
            type = DynamiteType.VANILLA;
        }

        Vec3 velocity = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ()).scale(0.75);
        projectile.setDeltaMovement(velocity);
        projectile.setCannonProjectile(true);
        projectile.setDynamiteType(type);

        this.level.addFreshEntity(projectile);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!ammo.isEmpty()) {
            tag.put("Ammo", ammo.save(registries));
        }
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Ammo")) {
            ammo = ItemStack.parse(registries, tag.getCompound("Ammo")).orElse(ItemStack.EMPTY);
        } else {
            ammo = ItemStack.EMPTY;
        }
        cooldown = tag.getInt("Cooldown");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            if (this.level.getBlockState(this.worldPosition).isAir()) {
                Containers.dropContents(this.level, this.worldPosition, this);
            }
        }
        super.setRemoved();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("ТНТ-Пушка");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new TntCannonMenu(id, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return ammo.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? ammo : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != 0) return ItemStack.EMPTY;
        return ammo.split(amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        ItemStack copy = ammo.copy();
        ammo = ItemStack.EMPTY;
        return copy;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != 0) return;
        ammo = stack;
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && isValidAmmo(stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public void clearContent() {
        ammo = ItemStack.EMPTY;
    }
}