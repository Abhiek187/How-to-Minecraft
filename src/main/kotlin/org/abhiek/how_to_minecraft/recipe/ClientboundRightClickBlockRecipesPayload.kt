package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.abhiek.how_to_minecraft.HowToMinecraft

// A basic packet class, must be registered
data class ClientboundRightClickBlockRecipesPayload(
    val inputStates: Set<BlockState>,
    val inputItems: Set<Holder<Item>>
): CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ClientboundRightClickBlockRecipesPayload>(
            ResourceLocation.fromNamespaceAndPath(
                HowToMinecraft.ID,
                "clientbound_right_click_block_recipes_payload"
            )
        )

        // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
        // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ClientboundRightClickBlockRecipesPayload> = StreamCodec.composite(
            ByteBufCodecs.collection(
                ::HashSet,
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
            ),
            ClientboundRightClickBlockRecipesPayload::inputStates,
            ByteBufCodecs.collection(
                ::HashSet,
                ByteBufCodecs.holder(
                    Registries.ITEM,
                    ByteBufCodecs.registry(Registries.ITEM)
                )
            ),
            ClientboundRightClickBlockRecipesPayload::inputItems,
            ::ClientboundRightClickBlockRecipesPayload
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}
