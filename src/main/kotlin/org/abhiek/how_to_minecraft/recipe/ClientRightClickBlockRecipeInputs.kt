package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.Holder
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState

// Client implementation to hold the inputs
class ClientRightClickBlockRecipeInputs(
    override val inputStates: Set<BlockState>,
    override val inputItems: Set<Holder<Item>>
): RightClickBlockRecipeInputs
