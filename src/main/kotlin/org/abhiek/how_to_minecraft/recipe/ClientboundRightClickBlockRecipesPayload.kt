package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.Holder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState

// A basic packet class, must be registered
data class ClientboundRightClickBlockRecipesPayload(
    val inputStates: Set<BlockState>,
    val inputItems: Set<Holder<Item>>
): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        TODO("Not yet implemented")
    }
}
