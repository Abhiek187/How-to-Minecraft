package org.abhiek.how_to_minecraft.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.block.state.BlockState

// Our inputs are a BlockState and an ItemStack
data class RightClickBlockInput(val state: BlockState, val stack: ItemStack): RecipeInput {
    // Method to get an item from a specific slot. We have one stack and no concept of slots, so we just assume
    // that slot 0 holds our item, and throw on any other slot. (Taken from SingleRecipeInput#getItem.)
    override fun getItem(slot: Int): ItemStack {
        require(slot == 0) { "No item for index $slot" }
        return this.stack
    }

    // The slot size our input requires. Again, we don't really have a concept of slots, so we just return 1
    // because we have one item stack involved. Inputs with multiple items should return the actual count here.
    override fun size(): Int {
        return 1
    }
}
