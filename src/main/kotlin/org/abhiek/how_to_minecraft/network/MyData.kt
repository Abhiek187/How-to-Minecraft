package org.abhiek.how_to_minecraft.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.abhiek.how_to_minecraft.HowToMinecraft

data class MyData(val name: String, val age: Int): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    companion object {
        val TYPE = CustomPacketPayload.Type<MyData>(
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "my_data")
        )

        // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
        // 'name' will be encoded and decoded as a string
        // 'age' will be encoded and decoded as an integer
        // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
        val STREAM_CODEC: StreamCodec<ByteBuf, MyData> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MyData::name,
            ByteBufCodecs.VAR_INT,
            MyData::age,
            ::MyData
        )
    }
}
