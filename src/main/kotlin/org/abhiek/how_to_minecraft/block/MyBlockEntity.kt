package org.abhiek.how_to_minecraft.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class MyBlockEntity(pos: BlockPos, state: BlockState):
    BlockEntity(ModBlocks.MY_BLOCK_ENTITY, pos, state) {
    // This can be any value of any type you want, so long as you can somehow serialize it to the value I/O.
    // We will use an int for the sake of example.
    private var value = 0

    // Read values from the passed ValueInput here
    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        // Will default to 0 if absent. See the ValueIO article for more information.
        this.value = input.getIntOr("value", 0)
    }

    // Save values into the passed ValueOutput here
    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putInt("value", this.value)
    }

    override fun preRemoveSideEffects(pos: BlockPos, state: BlockState) {
        super.preRemoveSideEffects(pos, state)
        // Perform any remaining export logic on removal here
    }

    // Create an update tag here. For block entities with only a few fields, this can just call #saveWithoutMetadata.
    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return this.saveWithoutMetadata(registries)
    }

    // Handle a received update tag here. The default implementation calls #loadWithComponents here,
    // so you do not need to override this method if you don't plan to do anything beyond that.
    override fun handleUpdateTag(input: ValueInput) {
        super.handleUpdateTag(input)
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    override fun getUpdatePacket(): Packet<ClientGamePacketListener> {
        // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
        // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
        return ClientboundBlockEntityDataPacket.create(this)
    }

    // Optionally: Run some custom logic when the packet is received.
    // The super/default implementation forwards to #loadWithComponents.
    override fun onDataPacket(connection: Connection, input: ValueInput) {
        super.onDataPacket(connection, input)
        // Do whatever you need to do here
    }

    // The signature of this method matches the signature of the BlockEntityTicker functional interface
    companion object {
        val tick = BlockEntityTicker<MyBlockEntity> { level, pos, state, blockEntity ->
            // Whatever you want to do during ticking.
            // For example, you could change a crafting progress value or consume power here.
        }
    }
}
