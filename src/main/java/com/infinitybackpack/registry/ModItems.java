package com.infinitybackpack.registry;

import net.minecraft.world.item.alchemy.Potion;
import com.infinitybackpack.item.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModItems {

    // === BACKPACK ===
    public static final Item INFINITY_BACKPACK = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "infinity_backpack"),
            new InfinityBackpackItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY))
    );

    // === EXPLOSIVES ===
    public static final Item EXPLOSIVE_MATERIAL = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "explosive_material"),
            new ExplosiveMaterialItem(new Item.Properties())
    );

    public static final Item DYNAMITE_A_ITEM = registerDynamiteItem("dynamite_a", ModBlocks.DYNAMITE_A_BLOCK, "Динамит А",
            new int[]{0xFFAA00, 0xFF4500},
            List.of(
                    Component.literal(" — имеет в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("3 раза").withStyle(Style.EMPTY.withColor(0xFFAA00)))
                            .append(Component.literal(" больший радиус взрыва.").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Item DYNAMITE_B_ITEM = registerDynamiteItem("dynamite_b", ModBlocks.DYNAMITE_B_BLOCK, "Динамит Б",
            new int[]{0x9400D3, 0xFF1493},
            List.of(
                    Component.literal(" — имеет в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("10 раз").withStyle(Style.EMPTY.withColor(0xFF69B4)))
                            .append(Component.literal(" больший радиус взрыва.").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Item DYNAMITE_B2_ITEM = registerDynamiteItem("dynamite_b2", ModBlocks.DYNAMITE_B2_BLOCK, "Динамит Б2",
            new int[]{0xFF0000, 0x8B0000},
            List.of(
                    Component.literal(" — взрывает практически все блоки").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" в радиусе ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("12 блоков").withStyle(Style.EMPTY.withColor(0xFF0000)))
                            .append(Component.literal(";").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Item DYNAMITE_C4_ITEM = registerDynamiteItem("dynamite_c4", ModBlocks.DYNAMITE_C4_BLOCK, "С4 ВзРыВчАтКа",
            new int[]{0xFF1493, 0x00FFFF},
            List.of(
                    Component.literal(" — взрывает блоки ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("обсидиана").withStyle(Style.EMPTY.withColor(0x00FFFF)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Item SHOCKWAVE_ITEM = registerDynamiteItem("shockwave", ModBlocks.SHOCKWAVE_BLOCK, "Разрывная волна",
            new int[]{0xFF0000, 0xFF1493},
            List.of(
                    Component.literal(" — взрывает блоки в воде;").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" — взрывает блоки ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("обсидиана").withStyle(Style.EMPTY.withColor(0xFF1493)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Item STILLER_ITEM = registerDynamiteItem("stiller", ModBlocks.STILLER_BLOCK, "Стиллер",
            new int[]{0xC71585, 0xFF1493},
            List.of(
                    Component.literal(" — после взрыва выпадает спавнер").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" с привязанным мобом шансом в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("50%").withStyle(Style.EMPTY.withColor(0xFF1493)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    public static final Item RELIABLE_STILLER_ITEM = registerDynamiteItem("reliable_stiller", ModBlocks.RELIABLE_STILLER_BLOCK, "Надёжный стиллер",
            new int[]{0x00FFFF, 0x008B8B},
            List.of(
                    Component.literal(" — после взрыва выпадает спавнер").withStyle(Style.EMPTY.withColor(0xFFFFFF)),
                    Component.literal(" с привязанным мобом шансом в ").withStyle(Style.EMPTY.withColor(0xFFFFFF))
                            .append(Component.literal("75%").withStyle(Style.EMPTY.withColor(0x00FFFF)))
                            .append(Component.literal(".").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
            ));

    // === TNT CANNON ===
    public static final Item TNT_CANNON_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "tnt_cannon"),
            new TntCannonBlockItem(ModBlocks.TNT_CANNON_BLOCK, new Item.Properties())
    );

    // === TOOLS & MISC ===
    public static final Item GOLDEN_PICKAXE_JAKE = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "golden_pickaxe_jake"),
            new GoldenPickaxeJakeItem(new Item.Properties().stacksTo(1).durability(1))
    );

    public static final Item SNOWBALL_CLUMP_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "snowball_clump"),
            new SnowballClumpItem(new Item.Properties().stacksTo(16))
    );

    // === EXP BOTTLES ===
    public static final Item EXP_BOTTLE_15 = registerExpBottle("exp_bottle_15", "Бутылёк с 15 ур. опыта", new int[]{0x006400, 0x00FF00}, 315, 15);
    public static final Item EXP_BOTTLE_30 = registerExpBottle("exp_bottle_30", "Бутылёк с 30 ур. опыта", new int[]{0xAA8800, 0xFFFF00}, 1395, 30);
    public static final Item EXP_BOTTLE_50 = registerExpBottle("exp_bottle_50", "Бутылёк с 50 ур. опыта", new int[]{0xCC5500, 0xFF8800}, 5345, 50);
    public static final Item EXP_BOTTLE_100 = registerExpBottle("exp_bottle_100", "Бутылёк с 100 ур. опыта", new int[]{0x006400, 0x00FF00}, 30971, 100);

    // === SUN ARMOR ===
    private static final ItemAttributeModifiers SUN_HELMET_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_helmet_armor"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
            .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_helmet_toughness"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
            .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_helmet_knockback"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
            .build();

    private static final ItemAttributeModifiers SUN_BOOTS_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_boots_armor"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
            .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_boots_toughness"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
            .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_boots_knockback"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET)
            .build();

    public static final Item SUN_HELMET = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_helmet"),
            new SunHelmetItem(ArmorMaterials.GOLD, new Item.Properties()
                    .durability(ArmorItem.Type.HELMET.getDurability(7))
                    .attributes(SUN_HELMET_ATTRIBUTES)
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(false)),
                    "Шлем солнца",
                    new int[]{0xDAA520, 0xFFFF00, 0xDAA520})
    );

    public static final Item SUN_BOOTS = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sun_boots"),
            new SunBootsItem(ArmorMaterials.GOLD, new Item.Properties()
                    .durability(ArmorItem.Type.BOOTS.getDurability(7))
                    .attributes(SUN_BOOTS_ATTRIBUTES)
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(false)),
                    "Ботинки солнца",
                    new int[]{0xDAA520, 0xFFFF00, 0xDAA520})
    );

    // === INFINITY ARMOR ===
    public static final Item INFINITY_HELMET = registerInfinityArmor("infinity_helmet",
            ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET,
            "Шлем Infinity", new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.FIRE_PROTECTION, 5,
                    Enchantments.PROJECTILE_PROTECTION, 5,
                    Enchantments.RESPIRATION, 4,
                    Enchantments.AQUA_AFFINITY, 1,
                    Enchantments.BLAST_PROTECTION, 5,
                    Enchantments.PROTECTION, 5,
                    Enchantments.UNBREAKING, 5,
                    ModEnchantments.IMPENETRABLE, 2
            ));

    public static final Item INFINITY_CHESTPLATE = registerInfinityArmor("infinity_chestplate",
            ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE,
            "Нагрудник Infinity", new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.BLAST_PROTECTION, 5,
                    Enchantments.PROJECTILE_PROTECTION, 5,
                    Enchantments.FIRE_PROTECTION, 5,
                    Enchantments.PROTECTION, 5,
                    Enchantments.UNBREAKING, 5,
                    ModEnchantments.IMPENETRABLE, 2
            ));

    public static final Item INFINITY_LEGGINGS = registerInfinityArmor("infinity_leggings",
            ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS,
            "Поножи Infinity", new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.BLAST_PROTECTION, 5,
                    Enchantments.PROJECTILE_PROTECTION, 5,
                    Enchantments.FIRE_PROTECTION, 5,
                    Enchantments.PROTECTION, 5,
                    Enchantments.UNBREAKING, 5,
                    ModEnchantments.IMPENETRABLE, 2
            ));

    public static final Item INFINITY_BOOTS = registerInfinityArmor("infinity_boots",
            ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS,
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
                    ModEnchantments.IMPENETRABLE, 2
            ));

    // === INFINITY TOOLS ===
    public static final Item INFINITY_PICKAXE = registerInfinityPickaxe("infinity_pickaxe", "Кирка Infinity",
            new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.EFFICIENCY, 10,
                    Enchantments.FORTUNE, 6,
                    Enchantments.UNBREAKING, 8,
                    Enchantments.MENDING, 1,
                    ModEnchantments.DRILL, 2,
                    ModEnchantments.MAGNETISM, 1,
                    ModEnchantments.AUTOSMELT, 1,
                    ModEnchantments.UNBREAKABLE_ENCHANT, 1,
                    ModEnchantments.FILTER, 1
            ));

    public static final Item INFINITY_SHOVEL = registerInfinityShovel("infinity_shovel", "Лопата Infinity",
            new int[]{0x006400, 0x00FF00, 0x006400},
            Map.of(
                    Enchantments.EFFICIENCY, 10,
                    Enchantments.FORTUNE, 5,
                    Enchantments.UNBREAKING, 5,
                    Enchantments.MENDING, 1,
                    ModEnchantments.DRILL, 2,
                    ModEnchantments.MAGNETISM, 1,
                    ModEnchantments.UNBREAKABLE_ENCHANT, 1,
                    ModEnchantments.FILTER, 1
            ));

    // === STASIS ===
    public static final Item STASIS = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "stasis"),
            new StasisItem(new Item.Properties().stacksTo(64))
    );

    // === POTIONS ===
    public static final Item ENHANCED_STRENGTH_3MIN = registerPotionItem("enhanced_strength_3min", "Улучшенное зелье силы",
            new int[]{0x8B0000, 0xFF0000, 0x8B0000}, ModPotions.ENHANCED_STRENGTH_3MIN, 0xE8A200);

    public static final Item ENHANCED_STRENGTH_6MIN = registerPotionItem("enhanced_strength_6min", "Улучшенное зелье силы",
            new int[]{0x8B0000, 0xFF0000, 0x8B0000}, ModPotions.ENHANCED_STRENGTH_6MIN, 0xE8A200);

    public static final Item ENHANCED_SWIFTNESS_3MIN = registerPotionItem("enhanced_swiftness_3min", "Улучшенное зелье скорости",
            new int[]{0x00008B, 0x00BFFF, 0x00008B}, ModPotions.ENHANCED_SWIFTNESS_3MIN, 0x008BE8);

    public static final Item ENHANCED_SWIFTNESS_6MIN = registerPotionItem("enhanced_swiftness_6min", "Улучшенное зелье скорости",
            new int[]{0x00008B, 0x00BFFF, 0x00008B}, ModPotions.ENHANCED_SWIFTNESS_6MIN, 0x008BE8);

    // === TALISMAN ===
    private static final ItemAttributeModifiers TALISMAN_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "talisman_speed"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.OFFHAND)
            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "talisman_damage"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.OFFHAND)
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "talisman_armor"), 8.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
            .add(Attributes.MAX_HEALTH, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "talisman_health"), 4.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.OFFHAND)
            .build();

    public static final Item INFINITY_TALISMAN = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "infinity_talisman"),
            new InfinityTalismanItem(new Item.Properties()
                    .stacksTo(1)
                    .attributes(TALISMAN_ATTRIBUTES))
    );

    // === ELYTRA ===
    private static final ItemAttributeModifiers NETHERITE_ELYTRA_ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "elytra_armor"), 8.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
            .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "elytra_toughness"), 3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
            .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "elytra_knockback"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST)
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

    // ========================================
    // ФАБРИКИ
    // ========================================

    private static Item registerDynamiteItem(String name, Block block, String displayName, int[] nameGradient, List<Component> tooltip) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name),
                new DynamiteBlockItem(block, new Item.Properties(), displayName, nameGradient, tooltip));
    }

    private static Item registerElytra(String name, Item.Properties properties, String displayName,
                                       int[] gradient, List<Component> tooltip, boolean armored, boolean unbreakable, boolean jet) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name),
                new CustomElytraItem(properties, displayName, gradient, tooltip, armored, unbreakable, jet));
    }

    private static Item registerExpBottle(String name, String displayName, int[] gradient, int exp, int level) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name),
                new com.infinitybackpack.item.ExperienceBottleItem(new Item.Properties(), displayName, gradient, exp, level));
    }

    private static Item registerInfinityArmor(String name, Holder<ArmorMaterial> mat,
                                              ArmorItem.Type type, String displayName, int[] gradient,
                                              Map<ResourceKey<Enchantment>, Integer> enchants) {
        int durability = type.getDurability(37);
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name),
                new InfinityArmorItem(mat, type, new Item.Properties().stacksTo(1).durability(durability), displayName, gradient, enchants));
    }

    private static Item registerInfinityPickaxe(String name, String displayName, int[] gradient, Map<ResourceKey<Enchantment>, Integer> enchants) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name),
                new InfinityPickaxeItem(Tiers.NETHERITE, new Item.Properties()
                        .attributes(PickaxeItem.createAttributes(Tiers.NETHERITE, 1.0F, -2.8F)),
                        displayName, gradient, enchants));
    }

    private static Item registerInfinityShovel(String name, String displayName, int[] gradient, Map<ResourceKey<Enchantment>, Integer> enchants) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name),
                new InfinityShovelItem(Tiers.NETHERITE, new Item.Properties()
                        .attributes(ShovelItem.createAttributes(Tiers.NETHERITE, 1.5F, -3.0F)),
                        displayName, gradient, enchants));
    }

    private static Item registerPotionItem(String name, String displayName, int[] gradient, Holder<Potion> potion, int color) {
        PotionContents contents = new PotionContents(Optional.of(potion), Optional.of(color), List.of());
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name),
                new CustomPotionItem(new Item.Properties().component(DataComponents.POTION_CONTENTS, contents), displayName, gradient, potion, color));
    }

    // ========================================
    // ХЕЛПЕРЫ ЗАЧАРОВАНИЙ / ФИЛЬТРОВ
    // ========================================

    public static int getDrillLevel(ItemStack stack) {
        return getEnchantmentLevel(stack, ModEnchantments.DRILL);
    }

    public static int getUnbreakingLevel(ItemStack stack) {
        return getEnchantmentLevel(stack, Enchantments.UNBREAKING);
    }

    public static int getAutoSmeltLevel(ItemStack stack) {
        return getEnchantmentLevel(stack, ModEnchantments.AUTOSMELT);
    }

    public static int getUnbreakableLevel(ItemStack stack) {
        return getEnchantmentLevel(stack, ModEnchantments.UNBREAKABLE_ENCHANT);
    }

    public static int getFilterLevel(ItemStack stack) {
        return getEnchantmentLevel(stack, ModEnchantments.FILTER);
    }

    public static int getImpenetrableLevel(ItemStack stack) {
        return getEnchantmentLevel(stack, ModEnchantments.IMPENETRABLE);
    }

    public static int getMagnetismLevel(ItemStack stack) {
        return getEnchantmentLevel(stack, ModEnchantments.MAGNETISM);
    }

    private static int getEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key) {
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

    public static boolean hasEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key, int level) {
        return getEnchantmentLevel(stack, key) == level;
    }

    public static boolean hasEnchantment(ItemStack stack, ResourceKey<Enchantment> key) {
        return getEnchantmentLevel(stack, key) > 0;
    }

    public static void removeEnchantment(ItemStack stack, ResourceKey<Enchantment> key) {
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

    public static void setEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key, int level) {
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

    // ========================================
    // ХЕЛПЕРЫ ФИЛЬТРОВ
    // ========================================

    public static List<Item> getPlayerFilterItems(ServerPlayer player) {
        List<Item> items = new ArrayList<>();
        List<String> ids = ModConstants.PLAYER_FILTERS.get(player.getUUID());
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
        List<String> list = ModConstants.PLAYER_FILTERS.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
        if (list.contains(idStr)) {
            list.remove(idStr);
            if (list.isEmpty()) ModConstants.PLAYER_FILTERS.remove(player.getUUID());
            return true;
        } else {
            list.add(idStr);
            return false;
        }
    }

    public static boolean isItemFilteredForPlayer(ServerPlayer player, ItemStack drop) {
        List<String> list = ModConstants.PLAYER_FILTERS.get(player.getUUID());
        if (list == null) return false;
        String dropId = BuiltInRegistries.ITEM.getKey(drop.getItem()).toString();
        return list.contains(dropId);
    }

    public static Item findItemByTranslatedName(String name) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.getDefaultInstance().getHoverName().getString().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public static void init() {}
}