package org.abhiek.how_to_minecraft.gui

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import org.abhiek.how_to_minecraft.block.ModBlocks.EXAMPLE_BLOCK
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

// In MyMenu, an AbstractContainerMenu subclass
class MyMenuExtra(
    containerId: Int,
    playerInventory: Inventory,
    extraData: FriendlyByteBuf,
    private val access: ContainerLevelAccess = ContainerLevelAccess.NULL,
    dataMultiple: ContainerData = SimpleContainerData(3)
): AbstractContainerMenu(MY_MENU_EXTRA, containerId) {
    companion object {
        val REGISTER: DeferredRegister<MenuType<*>> = DeferredRegister.create(
            BuiltInRegistries.MENU,
            HowToMinecraft.ID
        )
        val MY_MENU_EXTRA: MenuType<MyMenuExtra> by REGISTER.register("my_menu_extra") { ->
            IMenuTypeExtension.create(::MyMenuExtra)
        }
    }

    init {
        // Check if the ContainerData size is some fixed value
        checkContainerDataCount(dataMultiple, 3)

        // Add data slots for handled integers
        this.addDataSlots(dataMultiple)
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, EXAMPLE_BLOCK.get())
    }

    override fun quickMoveStack(player: Player, quickMovedSlotIndex: Int): ItemStack {
        // The quick moved slot stack
        var quickMovedStack = ItemStack.EMPTY
        // The quick moved slot
        val quickMovedSlot = this.slots[quickMovedSlotIndex]

        // If the slot is in the valid range and the slot is not empty
        if (quickMovedSlot.hasItem()) {
            // Get the raw stack to move
            val rawStack = quickMovedSlot.item
            // Set the slot stack to a copy of the raw stack
            quickMovedStack = rawStack.copy()

            /*
            The following quick move logic can be simplified to if in data inventory,
            try to move to player inventory/hotbar and vice versa for containers
            that cannot transform data (e.g. chests).
            */

            // If the quick move was performed on the data inventory result slot
            if (quickMovedSlotIndex == 0) {
                // Try to move the result slot into the player inventory/hotbar
                if (!this.moveItemStackTo(rawStack, 5, 41, true)) {
                    // If cannot move, no longer quick move
                    return ItemStack.EMPTY
                }

                // Perform logic on result slot quick move
                quickMovedSlot.onQuickCraft(rawStack, quickMovedStack)
            } else if (quickMovedSlotIndex in 5..<41) {
                // Try to move the inventory/hotbar slot into the data inventory input slots
                if (!this.moveItemStackTo(rawStack, 1, 5, false)) {
                    // If cannot move and in player inventory slot, try to move to hotbar
                    if (quickMovedSlotIndex < 32) {
                        if (!this.moveItemStackTo(rawStack, 32, 41, false)) {
                            // If cannot move, no longer quick move
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(rawStack, 5, 32, false)) {
                        // If cannot move, no longer quick move
                        return ItemStack.EMPTY
                    }
                }
            } else if (!this.moveItemStackTo(rawStack, 5, 41, false)) {
                // If cannot move, no longer quick move
                return ItemStack.EMPTY
            }

            if (rawStack.isEmpty) {
                // If the raw stack has completely moved out of the slot, set the slot to the empty stack
                quickMovedSlot.setByPlayer(ItemStack.EMPTY)
            } else {
                // Otherwise, notify the slot that the stack count has changed
                quickMovedSlot.setChanged()
            }

            /*
            The following if statement and Slot#onTake call can be removed if the
            menu does not represent a container that can transform stacks (e.g.
            chests).
            */
            if (rawStack.count == quickMovedStack.count) {
                // If the raw stack was not able to be moved to another slot, no longer quick move
                return ItemStack.EMPTY
            }
            // Execute logic on what to do post move with the remaining stack
            quickMovedSlot.onTake(player, rawStack)
        }

        return quickMovedStack // return the slot stack
    }
}
