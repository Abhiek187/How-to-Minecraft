package org.abhiek.how_to_minecraft.item

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.component.Consumable
import net.minecraft.world.item.equipment.ArmorMaterial
import net.minecraft.world.item.equipment.ArmorType
import net.minecraft.world.item.equipment.EquipmentAssets
import net.minecraft.world.item.equipment.Equippable
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import org.abhiek.how_to_minecraft.block.ModBlocks
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(HowToMinecraft.ID)

    // /give @s how_to_minecraft:example_item <count>
    val EXAMPLE_ITEM: Item by ITEMS.registerItem(
        "example_item",
        ::ExampleItem, // if ::Item, use registerSimpleItem
        Item.Properties()
            // Max items per slot
            .stacksTo(64)
            // 0 = no durability
            .durability(0)
            //.fireResistant()
            // COMMON = white, UNCOMMON = yellow, RARE = aqua, EPIC 😎 = light purple
            .rarity(Rarity.COMMON)
    )

    val EXAMPLE_BLOCK_ITEM: BlockItem by ITEMS.registerSimpleBlockItem(
        ModBlocks.EXAMPLE_BLOCK
    )

    // Consumables can be food or potions
    val CONSUMABLE: Item by ITEMS.registerSimpleItem(
        "consumable",
        Item.Properties().component(
            DataComponents.CONSUMABLE,
            Consumable.builder()
                // Spend 2 seconds, or 40 ticks, to consume
                .consumeSeconds(2f)
                // Sets the animation to play while consuming
                .animation(ItemUseAnimation.BLOCK)
                // Play sound while consuming every tick
                .sound(SoundEvents.ARMOR_EQUIP_CHAIN)
                // Play sound once finished consuming
                .soundAfterConsume(SoundEvents.BREEZE_WIND_CHARGE_BURST)
                // Show particles while eating
                .hasConsumeParticles(true)
                .build()
        )
    )

    val FOOD: Item by ITEMS.registerSimpleItem(
        "food",
        Item.Properties().food(
            FoodProperties.Builder()
                // Hearts healed = nutrition / 2
                // Hunger healed = min(2 * nutrition * saturationModifier, playerNutrition)
                .nutrition(3)
                // Higher = tastier 😋
                // Carrot = 0.3
                // Raw Cod = 0.1
                // Cooked Chicken = 0.6
                // Cooked Beef = 0.8
                // Golden Apple = 1.2
                .saturationModifier(0.3f)
                // When set, the food can always be eaten even with a full hunger bar
                .alwaysEdible()
                .build()
        )
    )

    // We place copper somewhere between stone and iron
    val COPPER_MATERIAL = ToolMaterial(
        // The tag that determines what blocks this material cannot break
        BlockTags.INCORRECT_FOR_STONE_TOOL,
        // Determines the durability of the material
        // Stone = 131, iron = 250
        200,
        // Determines the mining speed of the material. Unused by swords.
        // Stone = 4, iron = 6
        5f,
        // Determines the attack damage bonus. Different tools use this differently.
        // For example, swords do (getAttackDamageBonus() + 4) damage.
        // Stone = 1 (5 damage), iron = 2 (6 damage)
        1.5f,
        // How good enchantments on the tool will be
        // Gold = 22
        20,
        // What items can repair this material
        Tags.Items.INGOTS_COPPER
    )

    val COPPER_SWORD: Item by ITEMS.registerItem("copper_sword") { props ->
        // The item properties: sword, axe, pickaxe, shovel, or hoe
        Item(props.sword(
            // The material to use
            COPPER_MATERIAL,
            // The type-specific attack damage bonus
            // sword = 3, shovel = 1.5, pickaxe = 1, varies for axes and hoes
            3f,
            // The type-specific attack speed modifier. player = 4, total = player + speed
            // sword = -2.4, shovel = -3, pickaxe = -2.8, varies for axes and hoes
            -2.4f
        ))
    }

    val COPPER_ARMOR_MATERIAL = ArmorMaterial(
        // The durability multiplier of the armor material
        // HELMET = 11
        // CHESTPLATE = 16
        // LEGGINGS = 15
        // BOOTS = 13
        // BODY = 16
        15,
        // The defense value (or the number of half-armors on the bar) per armor type
        mapOf(
            ArmorType.BOOTS to 2,
            ArmorType.LEGGINGS to 4,
            ArmorType.CHESTPLATE to 6,
            ArmorType.HELMET to 2,
            ArmorType.BODY to 4
        ),
        // Enchantment value, gold = 25
        20,
        // Sound when equipping armor
        SoundEvents.ARMOR_EQUIP_GENERIC,
        // Additional defense, > 0 for diamond and netherite
        0f,
        // Knockback resistance, > 0 for netherite
        0f,
        // Items that can repair this armor
        Tags.Items.INGOTS_COPPER,
        // The resource key of the EquipmentClientInfo JSON discussed below
        // Points to assets/how_to_minecraft/equipment/copper.json
        ResourceKey.create(
            EquipmentAssets.ROOT_ID,
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "copper")
        )
    )

    val COPPER_HELMET: Item by ITEMS.registerItem("copper_helmet") { props ->
        // Can also do horse armor, wolf armor, etc.
        Item(props.humanoidArmor(
            // The material to use
            COPPER_ARMOR_MATERIAL,
            // The type of armor to create
            ArmorType.HELMET
        ))
    }

    val EQUIPPABLE: Item by ITEMS.registerSimpleItem(
        "equippable",
        Item.Properties().component(
            DataComponents.EQUIPPABLE,
            // Sets the slot that this item can be equipped to
            Equippable.builder(EquipmentSlot.HEAD)
                // Default = SoundEvents#ARMOR_EQUIP_GENERIC
                .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                // Points to assets/how_to_minecraft/equipment/equippable.json
                .setAsset(ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "equippable")
                ))
                // Texture when equipped
                // Points to assets/minecraft/textures/equippable.png
                .setCameraOverlay(ResourceLocation.withDefaultNamespace("equippable"))
                // When not set, any entity can equip this item
                .setAllowedEntities(EntityType.ZOMBIE)
                // Whether the item can be equipped when dispensed from a dispenser
                // Default = true
                .setDispensable(true)
                // Whether the item can be swapped off the player during a quick equip
                // Default = true
                .setSwappable(false)
                // Whether the item should be damaged when attacked (for equipment typically)
                // Must also be a damageable item
                // Default = true
                .setDamageOnHurt(false)
                // Whether the item can be equipped onto another entity on interaction (e.g., right click)
                // Default = false
                .setEquipOnInteract(true)
                // When true, an item with the SHEAR_REMOVE_ARMOR item ability can remove the equipped item
                // Default = false
                .setCanBeSheared(true)
                // Default = SoundEvents#SHEARS_SNIP
                .setShearingSound(SoundEvents.SADDLE_UNEQUIP)
                .build()
        )
    )
}
