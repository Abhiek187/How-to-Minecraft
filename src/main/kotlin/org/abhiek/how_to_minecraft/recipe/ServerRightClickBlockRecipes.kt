package org.abhiek.how_to_minecraft.recipe

import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.AddServerReloadListenersEvent
import net.neoforged.neoforge.event.OnDatapackSyncEvent
import org.abhiek.how_to_minecraft.HowToMinecraft

// Handling the recipe instance depending on side
@EventBusSubscriber(modid = HowToMinecraft.ID)
object ServerRightClickBlockRecipes {
    var inputs: ServerRightClickBlockRecipeInputs? = null

    @SubscribeEvent
    fun addListener(event: AddServerReloadListenersEvent) {
        // Register server reload listener
        inputs = ServerRightClickBlockRecipeInputs(event.serverResources.recipeManager)
        event.addListener(
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "recipes"),
            inputs!!
        )
    }

    @SubscribeEvent
    fun datapackSync(event: OnDatapackSyncEvent) {
        // Send to client
        inputs?.syncToClient(event.relevantPlayers)
    }
}
