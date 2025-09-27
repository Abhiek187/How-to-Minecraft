package org.abhiek.how_to_minecraft.recipe

import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.context.ContextMap
import net.minecraft.world.item.crafting.display.DisplayContentsFactory
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.stream.Stream

data class BlockStateSlotDisplay(val state: BlockState): SlotDisplay {
    companion object {
        val CODEC: MapCodec<BlockStateSlotDisplay> = BlockState.CODEC.fieldOf("state")
            .xmap(::BlockStateSlotDisplay, BlockStateSlotDisplay::state)
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BlockStateSlotDisplay> =
            StreamCodec.composite(
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
                BlockStateSlotDisplay::state,
                ::BlockStateSlotDisplay
            )

        val SLOT_DISPLAY_TYPES: DeferredRegister<SlotDisplay.Type<*>> = DeferredRegister.create(
            BuiltInRegistries.SLOT_DISPLAY,
            HowToMinecraft.ID
        )
        val BLOCK_STATE_SLOT_DISPLAY: SlotDisplay.Type<BlockStateSlotDisplay> by
            SLOT_DISPLAY_TYPES.register("block_state") { ->
                SlotDisplay.Type(CODEC, STREAM_CODEC)
            }
    }

    override fun <T> resolve(context: ContextMap, factory: DisplayContentsFactory<T>): Stream<T> {
        return when (factory) {
            // Check for our contents factory and transform if necessary
            is ForBlockStates<T> -> Stream.of(factory.forState(this.state))
            // If you want the contents to be handled differently depending on contents display
            // then you can case on other displays like so
            is DisplayContentsFactory.ForStacks<T> -> Stream.of(factory.forStack(state.block.asItem()))
            // If no factories match, then do not return anything in the transformed stream
            else -> Stream.empty()
        }
    }

    override fun type(): SlotDisplay.Type<out SlotDisplay> {
        // Return the registered type from below
        return BLOCK_STATE_SLOT_DISPLAY
    }
}
