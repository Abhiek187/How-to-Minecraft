package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.Holder
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.network.PacketDistributor
import java.util.stream.Stream

// Server resource listener so it can be reloaded when recipes are
class ServerRightClickBlockRecipeInputs(
    val recipeManager: RecipeManager,
    override var inputStates: Set<BlockState> = HashSet(),
    override var inputItems: Set<Holder<Item>> = HashSet()
) : ResourceManagerReloadListener, RightClickBlockRecipeInputs {
    // Set inputs here as #apply is fired synchronously based on listener registration order.
    // Recipes are always applied first.
    override fun onResourceManagerReload(manager: ResourceManager) {
        // Should never be null
//        ServerLifecycleHooks.getCurrentServer() ?: return

        // Populate inputs
        val inputStates = HashSet<BlockState>()
        val inputItems = HashSet<Holder<Item>>()

        this.recipeManager.recipeMap().byType(RightClickBlockRecipe.RIGHT_CLICK_BLOCK_TYPE)
            .forEach { holder ->
                val recipe = holder.value()
                inputStates.add(recipe.inputState)
                inputItems.addAll(recipe.inputItem.values)
            }

        this.inputStates = inputStates
        this.inputItems = inputItems
    }

    fun syncToClient(players: Stream<ServerPlayer>) {
        val payload = ClientboundRightClickBlockRecipesPayload(this.inputStates, this.inputItems)
        players.forEach { player ->
            PacketDistributor.sendToPlayer(player, payload)
        }
    }
}
