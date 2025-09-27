package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.Holder
import net.minecraft.world.item.crafting.display.DisplayContentsFactory
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

// A basic transformer for block states
interface ForBlockStates<T> : DisplayContentsFactory<T> {
    // Delegate methods
    fun forState(block: Holder<Block>): T {
        return this.forState(block.value())
    }

    fun forState(block: Block): T {
        return this.forState(block.defaultBlockState())
    }

    // The block state to take in and transform to the desired output
    fun forState(state: BlockState): T
}
