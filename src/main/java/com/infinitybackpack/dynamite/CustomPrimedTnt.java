package com.infinitybackpack.dynamite;

import com.infinitybackpack.registry.ModBlocks;
import com.infinitybackpack.registry.ModEntities;
import com.infinitybackpack.InfinityBackpackMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomPrimedTnt extends PrimedTnt {
    private static final EntityDataAccessor<Integer> DATA_DYNAMITE_TYPE = SynchedEntityData.defineId(CustomPrimedTnt.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_CANNON = SynchedEntityData.defineId(CustomPrimedTnt.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(CustomPrimedTnt.class, EntityDataSerializers.INT);

    private Vec3 cannonVelocity;

    public CustomPrimedTnt(EntityType<? extends CustomPrimedTnt> entityType, Level level) {
        super(entityType, level);
    }

    public CustomPrimedTnt(Level level, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(ModEntities.CUSTOM_PRIMED_TNT, level);
        this.setPos(x, y, z);
        double d = level.random.nextDouble() * 6.2831854820251465;
        this.setDeltaMovement(-Math.sin(d) * 0.02, 0.2F, -Math.cos(d) * 0.02);
        this.setFuse(200);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DYNAMITE_TYPE, 0);
        builder.define(DATA_IS_CANNON, false);
        builder.define(DATA_LIFETIME, 400);
    }

    public void setDynamiteType(DynamiteType type) {
        this.entityData.set(DATA_DYNAMITE_TYPE, type.ordinal());
        this.setCustomName(this.getGradientName(type));
        this.setCustomNameVisible(true);
    }

    public DynamiteType getDynamiteType() {
        return DynamiteType.values()[this.entityData.get(DATA_DYNAMITE_TYPE)];
    }

    public void setCannonProjectile(boolean value) {
        this.entityData.set(DATA_IS_CANNON, value);
        if (value) {
            this.setNoGravity(true);
            this.noPhysics = true;
            this.cannonVelocity = this.getDeltaMovement();
            this.entityData.set(DATA_LIFETIME, 400);
        }
    }

    public boolean isCannonProjectile() {
        return this.entityData.get(DATA_IS_CANNON);
    }

    public BlockState getBlockState() {
        return switch (getDynamiteType()) {
            case VANILLA -> Blocks.TNT.defaultBlockState();
            case DYNAMITE_A -> ModBlocks.DYNAMITE_A_BLOCK.defaultBlockState();
            case DYNAMITE_B -> ModBlocks.DYNAMITE_B_BLOCK.defaultBlockState();
            case DYNAMITE_B2 -> ModBlocks.DYNAMITE_B2_BLOCK.defaultBlockState();
            case DYNAMITE_C4 -> ModBlocks.DYNAMITE_C4_BLOCK.defaultBlockState();
            case SHOCKWAVE -> ModBlocks.SHOCKWAVE_BLOCK.defaultBlockState();
            case STILLER -> ModBlocks.STILLER_BLOCK.defaultBlockState();
            case RELIABLE_STILLER -> ModBlocks.RELIABLE_STILLER_BLOCK.defaultBlockState();
        };
    }

    @Override
    public boolean isPushable() {
        return !isCannonProjectile();
    }

    @Override
    public Component getName() {
        return getGradientName(getDynamiteType());
    }

    private Component getGradientName(DynamiteType type) {
        String displayName;
        int[] gradient;
        switch (type) {
            case VANILLA -> { displayName = "ТНТ"; gradient = new int[]{0xFF0000, 0xFFFFFF}; }
            case DYNAMITE_A -> { displayName = "Динамит А"; gradient = new int[]{0xFFAA00, 0xFF4500}; }
            case DYNAMITE_B -> { displayName = "Динамит Б"; gradient = new int[]{0x9400D3, 0xFF1493}; }
            case DYNAMITE_B2 -> { displayName = "Динамит Б2"; gradient = new int[]{0xFF0000, 0x8B0000}; }
            case DYNAMITE_C4 -> { displayName = "С4 ВзРыВчАтКа"; gradient = new int[]{0xFF1493, 0x00FFFF}; }
            case SHOCKWAVE -> { displayName = "Разрывная волна"; gradient = new int[]{0xFF0000, 0xFF1493}; }
            case STILLER -> { displayName = "Стиллер"; gradient = new int[]{0xC71585, 0xFF1493}; }
            case RELIABLE_STILLER -> { displayName = "Надёжный стиллер"; gradient = new int[]{0x00FFFF, 0x008B8B}; }
            default -> { displayName = "Динамит"; gradient = new int[]{0xFFFFFF, 0xFFFFFF}; }
        }

        MutableComponent result = Component.empty();
        int len = displayName.length();
        for (int i = 0; i < len; i++) {
            float ratio = (float) i / (len - 1);
            int color = interpolateColor(gradient[0], gradient[1], ratio);
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color));
            result.append(Component.literal(String.valueOf(displayName.charAt(i))).withStyle(style));
        }
        return result;
    }

    private static int interpolateColor(int start, int end, float ratio) {
        int r1 = (start >> 16) & 0xFF, g1 = (start >> 8) & 0xFF, b1 = start & 0xFF;
        int r2 = (end >> 16) & 0xFF, g2 = (end >> 8) & 0xFF, b2 = end & 0xFF;
        int r = Math.round(r1 + (r2 - r1) * ratio);
        int g = Math.round(g1 + (g2 - g1) * ratio);
        int b = Math.round(b1 + (b2 - b1) * ratio);
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public void push(double x, double y, double z) {
        if (!this.isCannonProjectile()) {
            super.push(x, y, z);
        }
    }

    @Override
    public void tick() {
        if (this.entityData.get(DATA_IS_CANNON)) {
            tickCannonProjectile();
            return;
        }

        this.updateInWaterStateAndDoFluidPushing();

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            Vec3 vec = this.getDeltaMovement();
            this.setDeltaMovement(vec.x * 0.7, vec.y * -0.5, vec.z * 0.7);
        }

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse <= 0) {
            if (!this.level().isClientSide) {
                explode();
            }
            this.discard();
        } else {
            if (this.level().isClientSide && this.random.nextInt(4) == 0) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    private boolean collidesWithBlock(Vec3 movement) {
        AABB box = this.getBoundingBox().move(movement);
        return this.level().getBlockCollisions(this, box).iterator().hasNext();
    }

    private void tickCannonProjectile() {
        if (this.cannonVelocity != null) {
            this.setDeltaMovement(this.cannonVelocity);
        }

        Vec3 movement = this.getDeltaMovement();

        if (collidesWithBlock(movement)) {
            if (!this.level().isClientSide) {
                boolean inWater = this.level().getBlockState(this.blockPosition()).getFluidState().is(FluidTags.WATER);
                if (inWater && getDynamiteType() != DynamiteType.SHOCKWAVE) {
                    // Не взрываемся в воде (кроме Shockwave), просто исчезаем
                } else {
                    explode();
                }
            }
            this.discard();
            return;
        }

        this.move(MoverType.SELF, movement);

        int lifetime = this.entityData.get(DATA_LIFETIME) - 1;
        this.entityData.set(DATA_LIFETIME, lifetime);
        if (lifetime <= 0) {
            if (!this.level().isClientSide) {
                boolean inWater = this.level().getBlockState(this.blockPosition()).getFluidState().is(FluidTags.WATER);
                if (inWater && getDynamiteType() != DynamiteType.SHOCKWAVE) {
                    // Не взрываемся в воде по таймауту
                } else {
                    explode();
                }
            }
            this.discard();
            return;
        }

        if (this.level().isClientSide && this.random.nextInt(4) == 0) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    private void explode() {
        DynamiteType type = getDynamiteType();

        switch (type) {
            case VANILLA -> explodeVanillaLike(DynamiteType.VANILLA);
            case DYNAMITE_A, DYNAMITE_B -> explodeVanillaLike(type);
            case DYNAMITE_B2 -> explodeCube();
            case DYNAMITE_C4 -> explodeC4();
            case SHOCKWAVE -> explodeShockwave();
            case STILLER -> explodeStiller(0.5f);
            case RELIABLE_STILLER -> explodeStiller(0.75f);
            default -> {
                this.level().explode(this, this.getX(), this.getY(0.0625), this.getZ(), 4.0f, Level.ExplosionInteraction.TNT);
            }
        }
    }

    private boolean isBlockedByWater() {
        Level level = this.level();
        BlockPos pos = this.blockPosition();
        if (!level.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
            return false;
        }
        BlockState stateAtPos = level.getBlockState(pos);
        if (stateAtPos.isSolid() && !stateAtPos.is(Blocks.WATER) && !stateAtPos.is(Blocks.BUBBLE_COLUMN) && !stateAtPos.is(Blocks.LAVA)) {
            return false;
        }
        return true;
    }

    private void explodeWithDamage(float power, DynamiteType type) {
        Level level = this.level();
        double x = this.getX();
        double y = this.getY(0.0625);
        double z = this.getZ();
        Vec3 center = new Vec3(x, y, z);

        float maxDist = power * 2.0f;
        AABB box = new AABB(x - maxDist, y - maxDist, z - maxDist, x + maxDist, y + maxDist, z + maxDist);

        List<LivingEntity> allTargets = level.getEntitiesOfClass(LivingEntity.class, box);
        boolean[] wasInvulnerable = new boolean[allTargets.size()];

        // 1. Полностью блокируем ванильный урон для всех сущностей
        for (int i = 0; i < allTargets.size(); i++) {
            LivingEntity living = allTargets.get(i);
            wasInvulnerable[i] = living.isInvulnerable();
            living.setInvulnerable(true);
        }

        // 2. Взрываем блоки ванильно (сущности не получат урон)
        level.explode(null, x, y, z, power, Level.ExplosionInteraction.TNT);

        // 3. Восстанавливаем флаг и наносим ручной урон только открытым целям
        if (!level.isClientSide) {
            for (int i = 0; i < allTargets.size(); i++) {
                LivingEntity living = allTargets.get(i);
                living.setInvulnerable(wasInvulnerable[i]);

                if (!living.isAlive()) continue;
                if (living.distanceToSqr(x, y, z) >= maxDist * maxDist) continue;

                Vec3 eyePos = living.getEyePosition(1.0f);
                if (isBlockedByBlocks(level, center, eyePos)) continue;

                double dist = Math.sqrt(living.distanceToSqr(x, y, z));
                float damage = (float) ((1.0 - dist / maxDist) * 20.0 * (power / 4.0));

                if (type == DynamiteType.DYNAMITE_B) {
                    damage *= 0.5f;
                }

                DamageSource source = level.damageSources().explosion(this, null);
                living.hurt(source, damage);
            }
        }
    }

    private boolean isBlockedByBlocks(Level level, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start);
        double length = direction.length();
        if (length < 0.1) return false;

        Vec3 step = direction.normalize().scale(0.3);
        int steps = (int) (length / 0.3) + 1;

        Vec3 current = start;
        for (int i = 0; i < steps; i++) {
            BlockPos pos = BlockPos.containing(current);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                return true;
            }
            current = current.add(step);
        }
        return false;
    }

    private void explodeVanillaLike(DynamiteType type) {
        if (isBlockedByWater()) {
            return;
        }
        explodeWithDamage(type.getBaseRadius(), type);
    }

    private void explodeCube() {
        Level level = this.level();
        if (level.isClientSide) return;

        if (isBlockedByWater()) {
            return;
        }

        BlockPos center = this.blockPosition();
        int radius = 12;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (!state.isAir() && !isProtectedBlock(state)) {
                        level.destroyBlock(pos, false);
                    }
                }
            }
        }

        level.explode(null, this.getX(), this.getY(0.0625), this.getZ(), 0.0f, Level.ExplosionInteraction.NONE);
    }

    private void explodeC4() {
        Level level = this.level();
        if (level.isClientSide) return;

        if (isBlockedByWater()) {
            return;
        }

        explodeWithDamage(getDynamiteType().getBaseRadius(), getDynamiteType());

        BlockPos center = this.blockPosition();
        int radius = 4;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;

                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.OBSIDIAN)) {
                        Block.popResource(level, pos, new ItemStack(Items.OBSIDIAN));
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (state.is(Blocks.CRYING_OBSIDIAN)) {
                        level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    } else if (state.is(Blocks.ANCIENT_DEBRIS)) {
                        level.setBlock(pos, Blocks.CRYING_OBSIDIAN.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private void explodeShockwave() {
        Level level = this.level();
        if (level.isClientSide) return;

        explodeWithDamage(getDynamiteType().getBaseRadius(), getDynamiteType());

        BlockPos center = this.blockPosition();
        int radius = 4;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;

                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.OBSIDIAN)) {
                        Block.popResource(level, pos, new ItemStack(Items.OBSIDIAN));
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (state.is(Blocks.CRYING_OBSIDIAN)) {
                        Block.popResource(level, pos, new ItemStack(Items.CRYING_OBSIDIAN));
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (state.is(Blocks.ANCIENT_DEBRIS)) {
                        level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private void explodeStiller(float successChance) {
        Level level = this.level();
        if (level.isClientSide) return;

        BlockPos center = this.blockPosition();
        int radius = 2;
        boolean foundSpawner = false;

        for (int x = -radius; x <= radius && !foundSpawner; x++) {
            for (int y = -radius; y <= radius && !foundSpawner; y++) {
                for (int z = -radius; z <= radius && !foundSpawner; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;

                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.SPAWNER)) {
                        foundSpawner = true;
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof SpawnerBlockEntity spawner) {
                            ItemStack spawnerStack = new ItemStack(Items.SPAWNER);
                            CompoundTag tag = spawner.saveWithoutMetadata(level.registryAccess());

                            if (level.random.nextFloat() > successChance) {
                                if (tag.contains("SpawnData")) {
                                    CompoundTag spawnData = tag.getCompound("SpawnData");
                                    if (spawnData.contains("entity")) {
                                        CompoundTag entity = spawnData.getCompound("entity");
                                        entity.putString("id", "minecraft:pig");
                                    } else {
                                        CompoundTag entity = new CompoundTag();
                                        entity.putString("id", "minecraft:pig");
                                        spawnData.put("entity", entity);
                                    }
                                } else {
                                    CompoundTag spawnData = new CompoundTag();
                                    CompoundTag entity = new CompoundTag();
                                    entity.putString("id", "minecraft:pig");
                                    spawnData.put("entity", entity);
                                    tag.put("SpawnData", spawnData);
                                }
                            }

                            BlockItem.setBlockEntityData(spawnerStack, blockEntity.getType(), tag);
                            Block.popResource(level, pos, spawnerStack);
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        level.explode(null, this.getX(), this.getY(0.0625), this.getZ(), 0.0f, Level.ExplosionInteraction.NONE);
    }

    private boolean isProtectedBlock(BlockState state) {
        return state.is(Blocks.OBSIDIAN) ||
                state.is(Blocks.CRYING_OBSIDIAN) ||
                state.is(Blocks.ANCIENT_DEBRIS) ||
                state.is(Blocks.BEDROCK) ||
                state.is(Blocks.BARRIER) ||
                state.is(Blocks.END_PORTAL_FRAME) ||
                state.is(Blocks.END_PORTAL) ||
                state.is(Blocks.NETHER_PORTAL) ||
                state.is(Blocks.COMMAND_BLOCK) ||
                state.is(Blocks.REPEATING_COMMAND_BLOCK) ||
                state.is(Blocks.CHAIN_COMMAND_BLOCK) ||
                state.is(Blocks.STRUCTURE_BLOCK) ||
                state.is(Blocks.JIGSAW);
    }
}