package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.Holder
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

// Packet stores data in an instance class.
// Present on both server and client to do initial matching.
interface RightClickBlockRecipeInputs {
    val inputStates: Set<BlockState>
    val inputItems: Set<Holder<Item>>

    fun test(state: BlockState?, stack: ItemStack): Boolean {
        return this.inputStates.contains(state) && this.inputItems.contains(stack.itemHolder)
    }
}
