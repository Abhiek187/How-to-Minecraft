package org.abhiek.how_to_minecraft.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.abhiek.how_to_minecraft.HowToMinecraft

class AckPayload: CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    companion object {
        val TYPE = CustomPacketPayload.Type<AckPayload>(
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "ack")
        )

        // Unit codec with no data to write
        val STREAM_CODEC: StreamCodec<ByteBuf, AckPayload> = StreamCodec.unit(AckPayload())
    }
}
