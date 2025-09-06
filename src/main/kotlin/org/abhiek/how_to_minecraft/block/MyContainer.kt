package org.abhiek.how_to_minecraft.block

import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

// Deprecated: Use ItemStackHandler instead of Container
class MyContainer: Container {
    private val items: NonNullList<ItemStack> =
        NonNullList.withSize(
            // The size of the list, i.e. the amount of slots in our container
            27,
            // The default value to be used in place of where you'd use null in normal lists
            ItemStack.EMPTY
        )

    override fun getContainerSize(): Int {
        // The amount of slots in our container
        return 27
    }

    override fun isEmpty(): Boolean {
        // Whether the container is considered empty
        return this.items.stream().allMatch { obj -> obj.isEmpty }
    }

    // Return the item stack in the specified slot
    override fun getItem(slot: Int): ItemStack {
        return this.items[slot]
    }

    // Call this when changes are done to the container, i.e. when item stacks are added, modified, or removed.
    // For example, you could call BlockEntity#setChanged here.
    override fun setChanged() {
    }

    // Remove the specified amount of items from the given slot, returning the stack that was just removed.
    // We defer to ContainerHelper here, which does this as expected for us.
    // However, we must call #setChanged manually.
    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val stack = ContainerHelper.removeItem(this.items, slot, amount)
        this.setChanged()
        return stack
    }

    // Remove all items from the specified slot, returning the stack that was just removed.
    // We again defer to ContainerHelper here, and we again have to call #setChanged manually.
    override fun removeItemNoUpdate(slot: Int): ItemStack {
        val stack = ContainerHelper.takeItem(this.items, slot)
        this.setChanged()
        return stack
    }

    // Set the given item stack in the given slot. Limit to the max stack size of the container first.
    override fun setItem(slot: Int, stack: ItemStack) {
        stack.limitSize(this.getMaxStackSize(stack))
        this.items[slot] = stack
        this.setChanged()
    }

    // Whether the container is considered "still valid" for the given player. For example, chests and
    // similar blocks check if the player is still within a given distance of the block here.
    override fun stillValid(player: Player): Boolean {
        return true
    }

    // Clear the internal storage, setting all slots to empty again
    override fun clearContent() {
        items.clear()
        this.setChanged()
    }
}
