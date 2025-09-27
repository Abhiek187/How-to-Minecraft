package org.abhiek.how_to_minecraft.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

// An implementation for an item stack output
class BlockStateStackContentsFactory private constructor() : ForBlockStates<ItemStack> {
    override fun forState(state: BlockState): ItemStack {
        return ItemStack(state.block)
    }

    companion object {
        // Singleton instance
        val INSTANCE = BlockStateStackContentsFactory()
    }
}
