package org.abhiek.how_to_minecraft.recipe

import net.minecraft.world.level.Level

object RightClickBlockRecipes {
    // Make proxy method to access properly
    fun inputs(level: Level): RightClickBlockRecipeInputs? {
        return if (level.isClientSide) {
            ClientRightClickBlockRecipes.inputs
        } else {
            ServerRightClickBlockRecipes.inputs
        }
    }
}
