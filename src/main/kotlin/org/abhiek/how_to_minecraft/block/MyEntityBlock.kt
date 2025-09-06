package org.abhiek.how_to_minecraft.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class MyEntityBlock(properties: Properties) : Block(properties), EntityBlock {
    // Return a new instance of our block entity here
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return MyBlockEntity(pos, state)
    }

    override fun affectNeighborsAfterRemoval(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        movedByPiston: Boolean
    ) {
        // Handle whatever logic you want to execute on the surrounding neighbors
        Containers.updateNeighboursAfterDestroy(state, level, pos)
    }

    // Due to generics, an unchecked cast is necessary here
    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        // You can return different tickers here, depending on whatever factors you want. A common use case would be
        // to return different tickers on the client or server, only tick one side to begin with,
        // or only return a ticker for some blockstates (e.g. when using a "my machine is working" blockstate property).
        return if (type == ModBlocks.MY_BLOCK_ENTITY) {
            MyBlockEntity.tick as BlockEntityTicker<T>
        } else {
            null
        }
    }
}
