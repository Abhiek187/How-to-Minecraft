package org.abhiek.how_to_minecraft.item

import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.Consumable
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
}
