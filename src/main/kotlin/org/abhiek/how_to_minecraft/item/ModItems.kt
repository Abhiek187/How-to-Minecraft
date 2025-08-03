package org.abhiek.how_to_minecraft.item

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
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
}
