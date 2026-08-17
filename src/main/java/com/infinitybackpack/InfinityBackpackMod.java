package com.infinitybackpack;

import com.infinitybackpack.item.CustomPotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.infinitybackpack.item.StasisItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.joml.Vector3f;
import java.util.concurrent.CopyOnWriteArrayList;
import com.infinitybackpack.item.InfinityPickaxeItem;
import com.infinitybackpack.item.InfinityShovelItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tiers;
import java.util.UUID;
import net.minecraft.network.chat.MutableComponent;
import java.util.ArrayList;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import java.util.Map;
import java.util.HashMap;
import com.infinitybackpack.network.ToggleAutoSmeltPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.contents.TranslatableContents;
import com.infinitybackpack.item.SunBootsItem;
import com.infinitybackpack.item.InfinityArmorItem;
import net.minecraft.world.item.enchantment.Enchantments;
import java.util.Map;
import com.infinitybackpack.block.TntCannonBlock;
import com.infinitybackpack.block.TntCannonBlockEntity;
import com.infinitybackpack.item.CustomElytraItem;
import com.infinitybackpack.item.InfinityTalismanItem;
import com.infinitybackpack.item.SnowballClumpItem;
import com.infinitybackpack.item.SnowballClumpProjectile;
import com.infinitybackpack.item.SunHelmetItem;
import com.infinitybackpack.item.TntCannonBlockItem;
import com.infinitybackpack.dynamite.CustomPrimedTnt;
import com.infinitybackpack.dynamite.CustomTntBlock;
import com.infinitybackpack.dynamite.DynamiteType;
import com.infinitybackpack.item.DynamiteBlockItem;
import com.infinitybackpack.item.ExplosiveMaterialItem;
import com.infinitybackpack.item.GoldenPickaxeJakeItem;
import com.infinitybackpack.item.InfinityBackpackItem;
import com.infinitybackpack.screen.BackpackMenu;
import com.infinitybackpack.screen.ExpExchangeMenu;
import com.infinitybackpack.screen.TntCannonMenu;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Optional;

public class InfinityBackpackMod implements ModInitializer {
    public static final String MOD_ID = "infinitybackpack";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Map<UUID, List<String>> PLAYER_FILTERS = new HashMap<>();
    private static final Map<java.util.UUID, Long> lastUnbreakableWarnTick = new HashMap<>();
    private static final Map<java.util.UUID, BlockPos> lastUnbreakableWarnPos = new HashMap<>();

    public static final ResourceKey IMPENETRABLE = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "impenetrable")
    );

    public static final ResourceKey MAGNETISM = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "magnetism")
    );

    public static final ResourceKey DRILL = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "drill")
    );

    public static final ResourceKey AUTOSMELT = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "autosmelt")
    );

    public static final ResourceKey UNBREAKABLE_ENCHANT = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "unbreakable")
    );

    public static final ResourceKey<Enchantment> FILTER = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "filter")
    );

    public static final TagKey<Item> PICKAXES_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "pickaxes"));
    public static final TagKey<Item> SHOVELS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "shovels"));
    public static final TagKey<Item> HOES_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "hoes"));
    public static final TagKey<Item> AXES_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "axes"));

    public static final MenuType BACKPACK_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "backpack"),
            new MenuType<>(BackpackMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType TNT_CANNON_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "tnt_cannon"),
            new MenuType<>(TntCannonMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final MenuType EXP_EXCHANGE_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "exp_exchange"),
            new MenuType<>(ExpExchangeMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static final Item INFINITY_BACKPACK = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "infinity_backpack"),
            new InfinityBackpackItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY))
    );

    public static final EntityType CUSTOM_PRIMED_TNT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "custom_primed_tnt"),
            EntityType.Builder.of((EntityType<CustomPrimedTnt> type, Level level) -> new CustomPrimedTnt(type, level), MobCategory.MISC)
                    .fireImmune()
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(8)
                    .updateInterval(10)
                    .build("infinitybackpack:custom_primed_tnt")
    );

    public static final EntityType SNOWBALL_CLUMP_PROJECTILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "snowball_clump"),
            EntityType.Builder.of((EntityType<SnowballClumpProjectile> type, Level level) -> new SnowballClumpProjectile(type, level), MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("infinitybackpack:snowball_clump")
    );

    public static final Block DYNAMITE_A_BLOCK = registerBlock("dynamite_a", new CustomTntBlock(DynamiteType.DYNAMITE_A, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Item DYNAMITE_A_ITEM = registerDynamiteItem("dynamite_a", DYNAMITE_A_BLOCK, "Динамит А",
            new int[]{0xFFAA00, 0xFF4500},
            List.of(
                    Component.literal(" — имеет в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("3 раза").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                            .append(Component.literal(" больший радиус взрыва.").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Block DYNAMITE_B_BLOCK = registerBlock("dynamite_b", new CustomTntBlock(DynamiteType.DYNAMITE_B, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Item DYNAMITE_B_ITEM = registerDynamiteItem("dynamite_b", DYNAMITE_B_BLOCK, "Динамит Б",
            new int[]{0x9400D3, 0xFF1493},
            List.of(
                    Component.literal(" — имеет в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("10 раз").withStyle(Style.EMPTY.withColor(0xFF69B4)))
                            .append(Component.literal(" больший радиус взрыва.").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Block DYNAMITE_B2_BLOCK = registerBlock("dynamite_b2", new CustomTntBlock(DynamiteType.DYNAMITE_B2, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Item DYNAMITE_B2_ITEM = registerDynamiteItem("dynamite_b2", DYNAMITE_B2_BLOCK, "Динамит Б2",
            new int[]{0xFF0000, 0x8B0000},
            List.of(
                    Component.literal(" — взрывает практически все блоки").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" в радиусе ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("12 блоков").withStyle(Style.EMPTY.withColor(0xFF0000)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Block DYNAMITE_C4_BLOCK = registerBlock("dynamite_c4", new CustomTntBlock(DynamiteType.DYNAMITE_C4, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Item DYNAMITE_C4_ITEM = registerDynamiteItem("dynamite_c4", DYNAMITE_C4_BLOCK, "С4 ВзРыВчАтКа",
            new int[]{0xFF1493, 0x00FFFF},
            List.of(
                    Component.literal(" — взрывает блоки ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("обсидиана").withStyle(Style.EMPTY.withColor(0x00FFFF)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Block SHOCKWAVE_BLOCK = registerBlock("shockwave", new CustomTntBlock(DynamiteType.SHOCKWAVE, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Item SHOCKWAVE_ITEM = registerDynamiteItem("shockwave", SHOCKWAVE_BLOCK, "Разрывная волна",
            new int[]{0xFF0000, 0xFF1493},
            List.of(
                    Component.literal(" — взрывает блоки в воде;").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" — взрывает блоки ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("обсидиана").withStyle(Style.EMPTY.withColor(0xFF1493)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Block STILLER_BLOCK = registerBlock("stiller", new CustomTntBlock(DynamiteType.STILLER, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Item STILLER_ITEM = registerDynamiteItem("stiller", STILLER_BLOCK, "Стиллер",
            new int[]{0xC71585, 0xFF1493},
            List.of(
                    Component.literal(" — после взрыва выпадает спавнер").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" с привязанным мобом шансом в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("50%").withStyle(Style.EMPTY.withColor(0xFF1493)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Block RELIABLE_STILLER_BLOCK = registerBlock("reliable_stiller", new CustomTntBlock(DynamiteType.RELIABLE_STILLER, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final Item RELIABLE_STILLER_ITEM = registerDynamiteItem("reliable_stiller", RELIABLE_STILLER_BLOCK, "Надёжный стиллер",
            new int[]{0x00FFFF, 0x008B8B},
            List.of(
                    Component.literal(" — после взрыва выпадает спавнер").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" с привязанным мобом шансом в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("75%").withStyle(Style.EMPTY.withColor(0x00FFFF)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Block TNT_CANNON_BLOCK = registerBlock("tnt_cannon", new TntCannonBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)));
    public static final Item TNT_CANNON_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "tnt_cannon"),
            new TntCannonBlockItem(TNT_CANNON_BLOCK, new Item.Properties())
    );

    public static final BlockEntityType TNT_CANNON_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "tnt_cannon"),
            BlockEntityType.Builder.of(TntCannonBlockEntity::new, TNT_CANNON_BLOCK).build()
    );

    public static final Item GOLDEN_PICKAXE_JAKE = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "golden_pickaxe_jake"),
            new GoldenPickaxeJakeItem(new Item.Properties().stacksTo(1).durability(1))
    );

    public static final Item EXPLOSIVE_MATERIAL = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "explosive_material"),
            new ExplosiveMaterialItem(new Item.Properties())
    );

    public static final Item EXP_BOTTLE_15 = registerExpBottle("exp_bottle_15", "Бутылёк с 15 ур. опыта", new int[]{0x006400, 0x00FF00}, 315, 15);
    public static final Item EXP_BOTTLE_30 = registerExpBottle("exp_bottle_30", "Бутылёк с 30 ур. опыта", new int[]{0xAA8800, 0xFFFF00}, 1395, 30);
    public static final Item EXP_BOTTLE_50 = registerExpBottle("exp_bottle_50", "Бутылёк с 50 ур. опыта", new int[]{0xCC5500, 0xFF8800}, 5345, 50);
    public static final Item EXP_BOTTLE_100 = registerExpBottle("exp_bottle_100", "Бутылёк с 100 ур. опыта", new int[]{0x006400, 0x00FF00}, 30971, 100);

    public static final Item SNOWBALL_CLUMP_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "snowball_clump"),
            new SnowballClumpItem(new Item.Properties().stacksTo(16))
    );

    private static final ItemAttributeModifiers SUN_HELMET_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_helmet_armor"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
            .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_helmet_toughness"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
            .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_helmet_knockback"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
            .build();

    private static final ItemAttributeModifiers SUN_BOOTS_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_boots_armor"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
            .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_boots_toughness"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
            .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_boots_knockback"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
            .build();

    public static final Item SUN_HELMET = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_helmet"),
            new SunHelmetItem(net.minecraft.world.item.ArmorMaterials.GOLD, new Item.Properties()
                    .durability(ArmorItem.Type.HELMET.getDurability(7))
                    .attributes(SUN_HELMET_ATTRIBUTES)
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(false)),
                    "Шлем солнца",
                    new int[]{0xDAA520, 0xFFFF00, 0xDAA520})
    );

    public static final Item SUN_BOOTS = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "sun_boots"),
            new SunBootsItem(net.minecraft.world.item.ArmorMaterials.GOLD, new Item.Properties()
                    .durability(ArmorItem.Type.BOOTS.getDurability(7))
                    .attributes(SUN_BOOTS_ATTRIBUTES)
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(false)),
                    "Ботинки солнца",
                    new int[]{0xDAA520, 0xFFFF00, 0xDAA520})
    );

    public static final Item INFINITY_HELMET = registerInfinityArmor("infinity_helmet",
            net.minecraft.world.item.ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET,
            "Шлем Infinity", new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.FIRE_PROTECTION, 5,
                    Enchantments.PROJECTILE_PROTECTION, 5,
                    Enchantments.RESPIRATION, 4,
                    Enchantments.AQUA_AFFINITY, 1,
                    Enchantments.BLAST_PROTECTION, 5,
                    Enchantments.PROTECTION, 5,
                    Enchantments.UNBREAKING, 5,
                    IMPENETRABLE, 2
            ));

    public static final Item INFINITY_CHESTPLATE = registerInfinityArmor("infinity_chestplate",
            net.minecraft.world.item.ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE,
            "Нагрудник Infinity", new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.BLAST_PROTECTION, 5,
                    Enchantments.PROJECTILE_PROTECTION, 5,
                    Enchantments.FIRE_PROTECTION, 5,
                    Enchantments.PROTECTION, 5,
                    Enchantments.UNBREAKING, 5,
                    IMPENETRABLE, 2
            ));

    public static final Item INFINITY_LEGGINGS = registerInfinityArmor("infinity_leggings",
            net.minecraft.world.item.ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS,
            "Поножи Infinity", new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.BLAST_PROTECTION, 5,
                    Enchantments.PROJECTILE_PROTECTION, 5,
                    Enchantments.FIRE_PROTECTION, 5,
                    Enchantments.PROTECTION, 5,
                    Enchantments.UNBREAKING, 5,
                    IMPENETRABLE, 2
            ));

    public static final Item INFINITY_BOOTS = registerInfinityArmor("infinity_boots",
            net.minecraft.world.item.ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS,
            "Ботинки Infinity", new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.BLAST_PROTECTION, 5,
                    Enchantments.PROJECTILE_PROTECTION, 5,
                    Enchantments.PROTECTION, 5,
                    Enchantments.FEATHER_FALLING, 4,
                    Enchantments.DEPTH_STRIDER, 4,
                    Enchantments.FIRE_PROTECTION, 5,
                    Enchantments.UNBREAKING, 5,
                    Enchantments.SOUL_SPEED, 4,
                    IMPENETRABLE, 2
            ));

    public static final Item INFINITY_PICKAXE = registerInfinityPickaxe("infinity_pickaxe", "Кирка Infinity",
            new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.EFFICIENCY, 10,
                    Enchantments.FORTUNE, 6,
                    Enchantments.UNBREAKING, 8,
                    Enchantments.MENDING, 1,
                    DRILL, 2,
                    MAGNETISM, 1,
                    AUTOSMELT, 1,
                    UNBREAKABLE_ENCHANT, 1,
                    FILTER, 1
            ));

    public static final Item INFINITY_SHOVEL = registerInfinityShovel("infinity_shovel", "Лопата Infinity",
            new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.EFFICIENCY, 10,
                    Enchantments.FORTUNE, 5,
                    Enchantments.UNBREAKING, 5,
                    Enchantments.MENDING, 1,
                    DRILL, 2,
                    MAGNETISM, 1,
                    UNBREAKABLE_ENCHANT, 1,
                    FILTER, 1
            ));

    public static final Item STASIS = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "stasis"),
            new StasisItem(new Item.Properties().stacksTo(64))
    );

    // === УЛУЧШЕННЫЕ ЗЕЛЬЯ ===
    public static final Holder<Potion> ENHANCED_STRENGTH_3MIN_POTION = registerPotion("enhanced_strength_3min",
            new Potion("enhanced_strength_3min", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 2)));
    public static final Item ENHANCED_STRENGTH_3MIN = registerPotionItem("enhanced_strength_3min", "Улучшенное зелье силы",
            new int[]{0x8B0000, 0xFF0000, 0x8B0000}, ENHANCED_STRENGTH_3MIN_POTION, 0xFFFF00);

    public static final Holder<Potion> ENHANCED_STRENGTH_6MIN_POTION = registerPotion("enhanced_strength_6min",
            new Potion("enhanced_strength_6min", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 7200, 2)));
    public static final Item ENHANCED_STRENGTH_6MIN = registerPotionItem("enhanced_strength_6min", "Улучшенное зелье силы",
            new int[]{0x8B0000, 0xFF0000, 0x8B0000}, ENHANCED_STRENGTH_6MIN_POTION, 0xFFFF00);

    public static final Holder<Potion> ENHANCED_SWIFTNESS_3MIN_POTION = registerPotion("enhanced_swiftness_3min",
            new Potion("enhanced_swiftness_3min", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 2)));
    public static final Item ENHANCED_SWIFTNESS_3MIN = registerPotionItem("enhanced_swiftness_3min", "Улучшенное зелье скорости",
            new int[]{0x00008B, 0x00BFFF, 0x00008B}, ENHANCED_SWIFTNESS_3MIN_POTION, 0x00BFFF);

    public static final Holder<Potion> ENHANCED_SWIFTNESS_6MIN_POTION = registerPotion("enhanced_swiftness_6min",
            new Potion("enhanced_swiftness_6min", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 7200, 2)));
    public static final Item ENHANCED_SWIFTNESS_6MIN = registerPotionItem("enhanced_swiftness_6min", "Улучшенное зелье скорости",
            new int[]{0x00008B, 0x00BFFF, 0x00008B}, ENHANCED_SWIFTNESS_6MIN_POTION, 0x00BFFF);

    public static final List<StasisZone> ACTIVE_STASIS_ZONES = new CopyOnWriteArrayList<>();

    private static final ItemAttributeModifiers TALISMAN_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "talisman_speed"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.OFFHAND)
            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "talisman_damage"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.OFFHAND)
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "talisman_armor"), 8.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
            .add(Attributes.MAX_HEALTH, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "talisman_health"), 4.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
            .build();

    public static final Item INFINITY_TALISMAN = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "infinity_talisman"),
            new InfinityTalismanItem(new Item.Properties()
                    .stacksTo(1)
                    .attributes(TALISMAN_ATTRIBUTES))
    );

    private static final ItemAttributeModifiers NETHERITE_ELYTRA_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "elytra_armor"), 8.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
            .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "elytra_toughness"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
            .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "elytra_knockback"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
            .build();

    public static final Item ARMORED_ELYTRA = registerElytra("armored_elytra",
            new Item.Properties().durability(592).attributes(NETHERITE_ELYTRA_ATTRIBUTES),
            "Броневые элитры", new int[]{0xFF8400, 0xFC3503},
            List.of(
                    Component.literal("— имеет свойства ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("незеритового нагрудника").withStyle(Style.EMPTY.withColor(0xFF8400)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))),
                    Component.literal("— позволяет ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("летать").withStyle(Style.EMPTY.withColor(0xFF8400)))
                            .append(Component.literal(" как обычная элитра;").withStyle(Style.EMPTY.withColor(0xFFFFFF))),
                    Component.literal("— возможно накладывать ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("зачарования").withStyle(Style.EMPTY.withColor(0xFF8400)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ), true, false, false);

    public static final Item UNBREAKABLE_ELYTRA = registerElytra("unbreakable_elytra",
            new Item.Properties().durability(592).component(DataComponents.UNBREAKABLE, new Unbreakable(false)),
            "Нерушимые элитры", new int[]{0xFF1493, 0x9400D3},
            List.of(), false, true, false);

    public static final Item JET_ELYTRA = registerElytra("jet_elytra",
            new Item.Properties().durability(592),
            "Реактивные элитры", new int[]{0x32CD32, 0x006400},
            List.of(
                    Component.literal("— имеет свойства стандартных ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("элитр").withStyle(Style.EMPTY.withColor(0x32CD32)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))),
                    Component.literal("— имеет скорость полёта ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("в 2 раза").withStyle(Style.EMPTY.withColor(0x32CD32)))
                            .append(Component.literal(" больше.").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ), false, false, true);

    public static final Item JET_ARMORED_ELYTRA = registerElytra("jet_armored_elytra",
            new Item.Properties().durability(592).attributes(NETHERITE_ELYTRA_ATTRIBUTES),
            "Реактивные броневые элитры", new int[]{0xFF8400, 0xFC3503},
            List.of(
                    Component.literal("— имеет свойства ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("реактивных элитр").withStyle(Style.EMPTY.withColor(0xFF8400)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFF8400))),
                    Component.literal("— имеет свойства ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("незеритового нагрудника").withStyle(Style.EMPTY.withColor(0xFF8400)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))),
                    Component.literal("— позволяет ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("летать").withStyle(Style.EMPTY.withColor(0xFF8400)))
                            .append(Component.literal(" как обычная элитра;").withStyle(Style.EMPTY.withColor(0xFFFFFF))),
                    Component.literal("— возможно накладывать ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("зачарования").withStyle(Style.EMPTY.withColor(0xFF8400)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ), true, false, true);

    public static final Item JET_UNBREAKABLE_ELYTRA = registerElytra("jet_unbreakable_elytra",
            new Item.Properties().durability(592).component(DataComponents.UNBREAKABLE, new Unbreakable(false)),
            "Реактивные нерушимые элитры", new int[]{0xFF1493, 0x9400D3},
            List.of(
                    Component.literal("— имеет свойства ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("реактивных элитр").withStyle(Style.EMPTY.withColor(0xFF1493)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ), false, true, true);

    public static final Item COMBINED_ELYTRA = registerElytra("combined_elytra",
            new Item.Properties().durability(592).attributes(NETHERITE_ELYTRA_ATTRIBUTES).component(DataComponents.UNBREAKABLE, new Unbreakable(false)),
            "Комбинированные элитры", new int[]{0xFF0000, 0x8B0000},
            List.of(
                    Component.literal("— имеет свойства ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("реактивных элитр").withStyle(Style.EMPTY.withColor(0xFF0000)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFF0000))),
                    Component.literal("— имеет свойства ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("броневых элитр").withStyle(Style.EMPTY.withColor(0xFF0000)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF))),
                    Component.literal("— полностью ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("нерушимы").withStyle(Style.EMPTY.withColor(0xFF0000)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ), true, true, true);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Infinity Backpack mod...");

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(INFINITY_BACKPACK);
            content.accept(EXPLOSIVE_MATERIAL);
            content.accept(DYNAMITE_A_ITEM);
            content.accept(DYNAMITE_B_ITEM);
            content.accept(DYNAMITE_B2_ITEM);
            content.accept(DYNAMITE_C4_ITEM);
            content.accept(SHOCKWAVE_ITEM);
            content.accept(STILLER_ITEM);
            content.accept(RELIABLE_STILLER_ITEM);
            content.accept(GOLDEN_PICKAXE_JAKE);
            content.accept(TNT_CANNON_ITEM);
            content.accept(ARMORED_ELYTRA);
            content.accept(UNBREAKABLE_ELYTRA);
            content.accept(JET_ELYTRA);
            content.accept(JET_ARMORED_ELYTRA);
            content.accept(JET_UNBREAKABLE_ELYTRA);
            content.accept(COMBINED_ELYTRA);
            content.accept(EXP_BOTTLE_15);
            content.accept(EXP_BOTTLE_30);
            content.accept(EXP_BOTTLE_50);
            content.accept(EXP_BOTTLE_100);
            content.accept(INFINITY_TALISMAN);
            content.accept(SNOWBALL_CLUMP_ITEM);
            content.accept(SUN_HELMET);
            content.accept(INFINITY_HELMET);
            content.accept(INFINITY_CHESTPLATE);
            content.accept(INFINITY_LEGGINGS);
            content.accept(INFINITY_BOOTS);
            content.accept(SUN_BOOTS);
            content.accept(INFINITY_PICKAXE);
            content.accept(INFINITY_SHOVEL);
            content.accept(STASIS);
            content.accept(ENHANCED_STRENGTH_3MIN);
            content.accept(ENHANCED_STRENGTH_6MIN);
            content.accept(ENHANCED_SWIFTNESS_3MIN);
            content.accept(ENHANCED_SWIFTNESS_6MIN);
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (!player.getMainHandItem().is(GOLDEN_PICKAXE_JAKE)) {
                return true;
            }

            if (state.is(Blocks.SPAWNER) && blockEntity instanceof SpawnerBlockEntity spawner) {
                if (!player.isCreative()) {
                    ItemStack stack = new ItemStack(Items.SPAWNER);
                    CompoundTag tag = spawner.saveWithoutMetadata(level.registryAccess());
                    BlockItem.setBlockEntityData(stack, BlockEntityType.MOB_SPAWNER, tag);
                    Block.popResource(level, pos, stack);

                    player.getMainHandItem().hurtAndBreak(1, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
                }

                level.removeBlock(pos, false);
                return false;
            }

            return true;
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide) return true;

            ItemStack tool = player.getMainHandItem();
            if (tool.isEmpty()) return true;

            int unbreakableLevel = getUnbreakableLevel(tool);
            if (unbreakableLevel <= 0) return true;

            int remaining = tool.getMaxDamage() - tool.getDamageValue();
            if (remaining > 50) return true;

            // Восстанавливаем прочность до 50, чтобы никогда не опустилась ниже
            tool.setDamageValue(tool.getMaxDamage() - 50);

            long currentTick = level.getGameTime();
            long lastTick = lastUnbreakableWarnTick.getOrDefault(player.getUUID(), 0L);
            BlockPos lastPos = lastUnbreakableWarnPos.get(player.getUUID());

            // Звук и сообщение только при новом нажатии ЛКМ (позиция другая или прошло > 3 тиков)
            if (!pos.equals(lastPos) || currentTick - lastTick > 3) {
                Component msg = Component.literal("Ваш инструмент почти ")
                        .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                        .append(Component.literal("сломан").withStyle(Style.EMPTY.withColor(0xFF0000)))
                        .append(Component.literal(", почините его!").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
                player.displayClientMessage(msg, true);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ANVIL_DESTROY, SoundSource.PLAYERS, 0.6f, 1.0f);

                lastUnbreakableWarnTick.put(player.getUUID(), currentTick);
                lastUnbreakableWarnPos.put(player.getUUID(), pos.immutable());
            }

            return false;
        });

        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide) return;

            ItemStack tool = player.getMainHandItem();
            int drillLevel = getDrillLevel(tool);
            if (drillLevel <= 0) return;
            if (tool.isEmpty() || !tool.isDamageableItem()) return;

            CustomData customData = tool.get(DataComponents.CUSTOM_DATA);
            boolean drillDisabled = false;
            if (customData != null) {
                drillDisabled = customData.copyTag().getBoolean("DrillDisabled");
            }
            if (drillDisabled) {
                return;
            }

            Direction facing = Direction.getNearest(
                    player.getLookAngle().x,
                    player.getLookAngle().y,
                    player.getLookAngle().z
            );

            int extraBlocks = 0;

            for (int d = 0; d < drillLevel; d++) {
                BlockPos layerCenter = pos.relative(facing, d);
                for (int o1 = -1; o1 <= 1; o1++) {
                    for (int o2 = -1; o2 <= 1; o2++) {
                        if (d == 0 && o1 == 0 && o2 == 0) continue;

                        BlockPos target = switch (facing.getAxis()) {
                            case X -> layerCenter.offset(0, o1, o2);
                            case Y -> layerCenter.offset(o1, 0, o2);
                            case Z -> layerCenter.offset(o1, o2, 0);
                        };

                        BlockState targetState = level.getBlockState(target);
                        if (targetState.isAir() || targetState.getDestroySpeed(level, target) < 0) continue;
                        if (!player.hasCorrectToolForDrops(targetState)) continue;

                        targetState.getBlock().playerDestroy(level, player, target, targetState, level.getBlockEntity(target), tool);
                        if (level instanceof ServerLevel serverLevel) {
                            targetState.spawnAfterBreak(serverLevel, target, tool, true);
                        }
                        level.removeBlock(target, false);
                        extraBlocks++;
                    }
                }
            }

            if (extraBlocks > 0 && level instanceof ServerLevel serverLevel) {
                int unbreakingLevel = getUnbreakingLevel(tool);
                int actualDamage;

                if (unbreakingLevel > 0) {
                    actualDamage = 0;
                    for (int i = 0; i < extraBlocks; i++) {
                        float avoidChance = (unbreakingLevel / (float) (unbreakingLevel + 1)) * 0.56f;
                        if (level.getRandom().nextFloat() >= avoidChance) {
                            actualDamage++;
                        }
                    }
                } else {
                    actualDamage = extraBlocks;
                }

                if (actualDamage > 0) {
                    tool.hurtAndBreak(actualDamage, player, EquipmentSlot.MAINHAND);
                }
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("exp")
                    .executes(context -> {
                        if (context.getSource().getEntity() instanceof ServerPlayer player) {
                            player.openMenu(new SimpleMenuProvider(
                                    (syncId, inv, p) -> new ExpExchangeMenu(syncId, inv),
                                    Component.literal("Обмен опыта")
                            ));
                            return 1;
                        }
                        return 0;
                    })
            );
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("elytratest")
                    .requires(source -> source.hasPermission(0))
                    .executes(context -> {
                        if (context.getSource().getEntity() instanceof ServerPlayer player) {
                            Vec3 motion = player.getDeltaMovement();
                            double hSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
                            boolean flying = player.isFallFlying();
                            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
                            String itemName = chest.isEmpty() ? "none" : chest.getItem().toString();

                            player.sendSystemMessage(Component.literal(
                                    "FallFlying: " + flying + " | Item: " + itemName + " | H-Speed: " + String.format("%.3f", hSpeed)
                            ));
                            return 1;
                        }
                        return 0;
                    }));
        });

        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> {
            if (enchantment.is(AUTOSMELT)) {
                if (target.is(PICKAXES_TAG) || target.is(Items.BOOK)) {
                    return TriState.TRUE;
                }
                return TriState.FALSE;
            }
            if (enchantment.is(FILTER)) {
                if (target.is(PICKAXES_TAG) || target.is(SHOVELS_TAG) || target.is(HOES_TAG) || target.is(AXES_TAG) || target.is(Items.BOOK)) {
                    return TriState.TRUE;
                }
                return TriState.FALSE;
            }
            return TriState.DEFAULT;
        });

        PayloadTypeRegistry.playC2S().register(ToggleAutoSmeltPayload.TYPE, ToggleAutoSmeltPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ToggleAutoSmeltPayload.TYPE, (payload, context) -> {
            ItemStack stack = context.player().getMainHandItem();
            if (stack.isEmpty()) return;

            int autoSmeltLevel = getAutoSmeltLevel(stack);
            if (autoSmeltLevel <= 0) return;

            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();
            boolean currentlyDisabled = tag.getBoolean("AutoSmeltDisabled");
            tag.putBoolean("AutoSmeltDisabled", !currentlyDisabled);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            boolean nowDisabled = !currentlyDisabled;
            Component message = nowDisabled
                    ? createDrillToggleMessage("Автоплавка выключена", 0x8B0000, 0xFF0000)
                    : createDrillToggleMessage("Автоплавка включена", 0x006400, 0x00FF00);
            context.player().displayClientMessage(message, true);

            context.player().level().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(),
                    SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, nowDisabled ? 0.5f : 1.5f);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("filter")
                    .then(Commands.literal("check")
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                ItemStack tool = player.getMainHandItem();

                                if (getFilterLevel(tool) <= 0) {
                                    player.displayClientMessage(
                                            Component.literal("В вашем инструменте нет зачарования Фильтр!")
                                                    .withStyle(Style.EMPTY.withColor(0xFF0000)), false);
                                    return 0;
                                }

                                List<Item> filters = getPlayerFilterItems(player);
                                if (filters.isEmpty()) {
                                    player.displayClientMessage(
                                            Component.literal("У вас нет отключённых предметов для выпадения.")
                                                    .withStyle(Style.EMPTY.withColor(0xAAAAAA)), false);
                                } else {
                                    for (Item item : filters) {
                                        MutableComponent itemName = Component.literal(item.getDefaultInstance().getHoverName().getString());
                                        Component msg = Component.literal("У вас отключён/а для выпадение - ")
                                                .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                                                .append(itemName.withStyle(Style.EMPTY.withColor(0xFF0000)))
                                                .append(Component.literal("!").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
                                        player.displayClientMessage(msg, false);
                                    }
                                }
                                return 1;
                            }))
                    .then(Commands.literal("set")
                            .then(Commands.argument("item", StringArgumentType.greedyString())
                                    .suggests((context, builder) -> {
                                        String remaining = builder.getRemaining().toLowerCase();
                                        for (Item item : BuiltInRegistries.ITEM) {
                                            String translated = item.getDefaultInstance().getHoverName().getString();
                                            if (translated.toLowerCase().startsWith(remaining)) {
                                                builder.suggest(translated);
                                            }
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> {
                                        ServerPlayer player = context.getSource().getPlayerOrException();
                                        ItemStack tool = player.getMainHandItem();

                                        if (getFilterLevel(tool) <= 0) {
                                            player.displayClientMessage(
                                                    Component.literal("В вашем инструменте нет зачарования Фильтр!")
                                                            .withStyle(Style.EMPTY.withColor(0xFF0000)), false);
                                            return 0;
                                        }

                                        String input = StringArgumentType.getString(context, "item");
                                        Item targetItem = findItemByTranslatedName(input);

                                        if (targetItem == null) {
                                            player.displayClientMessage(
                                                    Component.literal("Предмет не найден!")
                                                            .withStyle(Style.EMPTY.withColor(0xFF0000)), false);
                                            return 0;
                                        }

                                        boolean removed = togglePlayerFilterItem(player, targetItem);
                                        MutableComponent itemName = Component.literal(targetItem.getDefaultInstance().getHoverName().getString());

                                        if (removed) {
                                            Component msg = Component.literal("Вы включили для выпадение - ")
                                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                                                    .append(itemName.withStyle(Style.EMPTY.withColor(0x00FF00)))
                                                    .append(Component.literal("!").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
                                            player.displayClientMessage(msg, false);
                                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                                    SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 2.0f, 1.0f);
                                        } else {
                                            Component msg = Component.literal("Вы отключили для выпадение - ")
                                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF))
                                                    .append(itemName.withStyle(Style.EMPTY.withColor(0xFF0000)))
                                                    .append(Component.literal("!").withStyle(Style.EMPTY.withColor(0xFFFFFF)));
                                            player.displayClientMessage(msg, false);
                                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                                    SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 2.0f, 1.0f);
                                        }
                                        return 1;
                                    }))));
        });

        // === СЕРВЕРНЫЙ ТИК СТАНОВ ===
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                long currentTick = level.getGameTime();

                // Удаляем истёкшие зоны этого мира
                ACTIVE_STASIS_ZONES.removeIf(zone -> zone.level() == level && zone.isExpired(currentTick));

                // Спавним частицы для активных зон
                for (StasisZone zone : ACTIVE_STASIS_ZONES) {
                    if (zone.level() == level) spawnStasisParticles(zone);
                }

                // Накладываем/снимаем Замедление
                for (ServerPlayer player : level.players()) {
                    boolean shouldSlow = ACTIVE_STASIS_ZONES.stream()
                            .filter(z -> z.level() == level)
                            .anyMatch(z -> z.isInside(player.getX(), player.getY(), player.getZ()) && !z.activator().equals(player.getUUID()));

                    if (shouldSlow) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
                    } else {
                        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    }
                }
            }
        });
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, name), block);
    }

    private static Item registerDynamiteItem(String name, Block block, String displayName, int[] nameGradient, List tooltip) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new DynamiteBlockItem(block, new Item.Properties(), displayName, nameGradient, tooltip));
    }

    private static Item registerElytra(String name, Item.Properties properties, String displayName,
                                       int[] gradient, List tooltip, boolean armored, boolean unbreakable, boolean jet) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new CustomElytraItem(properties, displayName, gradient, tooltip, armored, unbreakable, jet));
    }

    private static Item registerExpBottle(String name, String displayName, int[] gradient, int exp, int level) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new com.infinitybackpack.item.ExperienceBottleItem(new Item.Properties(), displayName, gradient, exp, level));
    }

    private static Item registerInfinityArmor(String name, Holder mat,
                                              ArmorItem.Type type, String displayName, int[] gradient,
                                              Map enchants) {
        int durability = type.getDurability(37);
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new InfinityArmorItem(mat, type, new Item.Properties().stacksTo(1).durability(durability), displayName, gradient, enchants));
    }

    public static int getDrillLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(DRILL)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getUnbreakingLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(Enchantments.UNBREAKING)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getAutoSmeltLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(AUTOSMELT)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getUnbreakableLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(UNBREAKABLE_ENCHANT)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static String getEnchantmentId(Enchantment enchantment) {
        var contents = enchantment.description().getContents();
        if (contents instanceof TranslatableContents tc) {
            String key = tc.getKey();
            if (key.startsWith("enchantment.")) {
                String sub = key.substring("enchantment.".length());
                int dot = sub.indexOf('.');
                if (dot != -1) {
                    String result = sub.substring(0, dot) + ":" + sub.substring(dot + 1);
                    System.out.println("[DEBUG] Enchantment ID = '" + result + "'");
                    return result;
                }
            }
        }
        System.out.println("[DEBUG] Could not parse ID from enchantment description");
        return "";
    }

    public static boolean isMagnetism(Enchantment enchantment) {
        boolean result = "infinitybackpack:magnetism".equals(getEnchantmentId(enchantment));
        System.out.println("[DEBUG] isMagnetism = " + result);
        return result;
    }

    public static boolean isOreSmeltingResult(ItemStack stack) {
        return stack.is(Items.IRON_INGOT) || stack.is(Items.GOLD_INGOT) || stack.is(Items.COPPER_INGOT)
                || stack.is(Items.NETHERITE_SCRAP) || stack.is(Items.QUARTZ)
                || stack.is(Items.COAL) || stack.is(Items.REDSTONE) || stack.is(Items.LAPIS_LAZULI)
                || stack.is(Items.DIAMOND) || stack.is(Items.EMERALD);
    }

    private static Component createDrillToggleMessage(String text, int darkColor, int brightColor) {
        MutableComponent result = Component.empty();
        int len = text.length();
        int half = len / 2;
        for (int i = 0; i < len; i++) {
            float ratio;
            int color;
            if (i <= half) {
                ratio = half > 0 ? (float) i / half : 0f;
                color = interpolateColor(darkColor, brightColor, ratio);
            } else {
                ratio = (len - 1 - half) > 0 ? (float) (i - half) / (len - 1 - half) : 0f;
                color = interpolateColor(brightColor, darkColor, ratio);
            }
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(true);
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
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

    public static int getFilterLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(FILTER)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static List<Item> getPlayerFilterItems(ServerPlayer player) {
        List<Item> items = new ArrayList<>();
        List<String> ids = PLAYER_FILTERS.get(player.getUUID());
        if (ids == null) return items;
        for (String id : ids) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
            if (item != null) items.add(item);
        }
        return items;
    }

    public static boolean togglePlayerFilterItem(ServerPlayer player, Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        String idStr = id.toString();
        List<String> list = PLAYER_FILTERS.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
        if (list.contains(idStr)) {
            list.remove(idStr);
            if (list.isEmpty()) PLAYER_FILTERS.remove(player.getUUID());
            return true;
        } else {
            list.add(idStr);
            return false;
        }
    }

    public static boolean isItemFilteredForPlayer(ServerPlayer player, ItemStack drop) {
        List<String> list = PLAYER_FILTERS.get(player.getUUID());
        if (list == null) return false;
        String dropId = BuiltInRegistries.ITEM.getKey(drop.getItem()).toString();
        return list.contains(dropId);
    }

    private static Item findItemByTranslatedName(String name) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.getDefaultInstance().getHoverName().getString().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    private static Item registerInfinityPickaxe(String name, String displayName, int[] gradient, Map<ResourceKey<Enchantment>, Integer> enchants) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new InfinityPickaxeItem(Tiers.NETHERITE, new Item.Properties()
                        .attributes(PickaxeItem.createAttributes(Tiers.NETHERITE, 1.0F, -2.8F)),
                        displayName, gradient, enchants));
    }

    private static Item registerInfinityShovel(String name, String displayName, int[] gradient, Map<ResourceKey<Enchantment>, Integer> enchants) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new InfinityShovelItem(Tiers.NETHERITE, new Item.Properties()
                        .attributes(ShovelItem.createAttributes(Tiers.NETHERITE, 1.5F, -3.0F)),
                        displayName, gradient, enchants));
    }

    public static void addStasisZone(ServerLevel level, BlockPos center, long endTick, UUID activator) {
        ACTIVE_STASIS_ZONES.add(new StasisZone(level, center, endTick, activator));
    }

    public static boolean isPlayerInAnyStasis(ServerPlayer player) {
        for (StasisZone zone : ACTIVE_STASIS_ZONES) {
            if (zone.level() == player.serverLevel() && zone.isInside(player.getX(), player.getY(), player.getZ())) {
                return true;
            }
        }
        return false;
    }

    private static void spawnStasisParticles(StasisZone zone) {
        ServerLevel level = zone.level();
        BlockPos c = zone.center();
        int r = 15;
        DustParticleOptions white = new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);

        // 4 вертикальные линии по углам куба
        for (int y = -r; y <= r; y += 2) {
            level.sendParticles(white, c.getX() - r, c.getY() + y, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() + y, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() - r, c.getY() + y, c.getZ() + r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() + y, c.getZ() + r, 1, 0, 0, 0, 0);
        }

        // Горизонтальные линии сверху и снизу
        for (int i = -r; i <= r; i += 2) {
            level.sendParticles(white, c.getX() + i, c.getY() - r, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + i, c.getY() - r, c.getZ() + r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() - r, c.getY() - r, c.getZ() + i, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() - r, c.getZ() + i, 1, 0, 0, 0, 0);

            level.sendParticles(white, c.getX() + i, c.getY() + r, c.getZ() - r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + i, c.getY() + r, c.getZ() + r, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() - r, c.getY() + r, c.getZ() + i, 1, 0, 0, 0, 0);
            level.sendParticles(white, c.getX() + r, c.getY() + r, c.getZ() + i, 1, 0, 0, 0, 0);
        }
    }

    public record StasisZone(ServerLevel level, BlockPos center, long endTick, UUID activator) {
        public boolean isExpired(long currentTick) {
            return currentTick >= endTick;
        }
        public boolean isInside(double x, double y, double z) {
            return Math.abs(x - center.getX()) <= 15 && Math.abs(y - center.getY()) <= 15 && Math.abs(z - center.getZ()) <= 15;
        }
    }

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name), potion);
    }

    private static Item registerPotionItem(String name, String displayName, int[] gradient, Holder<Potion> potion, int color) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                new CustomPotionItem(new Item.Properties(), displayName, gradient, potion, color));
    }
}