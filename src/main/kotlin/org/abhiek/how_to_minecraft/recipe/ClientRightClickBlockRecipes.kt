package org.abhiek.how_to_minecraft.recipe

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut
import net.neoforged.neoforge.network.handling.IPayloadContext

object ClientRightClickBlockRecipes {
    var inputs: ClientRightClickBlockRecipeInputs? = null

    // Handling the sent packet
    fun handle(data: ClientboundRightClickBlockRecipesPayload, context: IPayloadContext) {
        // Do something with the data, on the main thread
        inputs = ClientRightClickBlockRecipeInputs(data.inputStates, data.inputItems)
    }

    @SubscribeEvent
    fun clientLogOut(event: LoggingOut) {
        // Clear the stored inputs on world log out
        inputs = null
    }
}
