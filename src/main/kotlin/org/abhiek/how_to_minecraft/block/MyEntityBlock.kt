package org.abhiek.how_to_minecraft.block

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.abhiek.how_to_minecraft.gui.MyMenu

class MyEntityBlock(properties: Properties): Block(properties), EntityBlock {
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

    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider {
        return SimpleMenuProvider(
            { containerId, playerInventory, _ ->
                MyMenu(containerId, playerInventory)
            },
            Component.translatable("menu.title.how_to_minecraft.my_menu")
        )
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide && player is ServerPlayer) {
            player.openMenu(state.getMenuProvider(level, pos))
        }

        return InteractionResult.SUCCESS
    }
}
