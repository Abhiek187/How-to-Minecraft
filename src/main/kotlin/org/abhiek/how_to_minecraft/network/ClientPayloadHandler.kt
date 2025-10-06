package org.abhiek.how_to_minecraft.network

import net.neoforged.neoforge.network.handling.IPayloadContext

object ClientPayloadHandler {
    fun handleDataOnMain(data: MyData, context: IPayloadContext) {
        // Do something with the data, on the main thread
    }
}
