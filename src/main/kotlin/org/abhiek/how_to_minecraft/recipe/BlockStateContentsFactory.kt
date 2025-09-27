package org.abhiek.how_to_minecraft.recipe

import net.minecraft.world.level.block.state.BlockState

// An implementation for a block state output
class BlockStateContentsFactory private constructor() : ForBlockStates<BlockState> {
    override fun forState(state: BlockState): BlockState {
        return state
    }

    companion object {
        // Singleton instance
        val INSTANCE = BlockStateContentsFactory()
    }
}
