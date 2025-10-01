package org.abhiek.how_to_minecraft.gui

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.SlotItemHandler
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import org.abhiek.how_to_minecraft.block.ModBlocks.EXAMPLE_BLOCK
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

class MyMenu(
    containerId: Int,
    playerInventory: Inventory,
    private val access: ContainerLevelAccess = ContainerLevelAccess.NULL,
    dataSingle: DataSlot = DataSlot.standalone(),
    dataInventory: IItemHandler = ItemStackHandler(10)
): AbstractContainerMenu(MY_MENU, containerId) {
    companion object {
        val REGISTER: DeferredRegister<MenuType<*>> = DeferredRegister.create(
            BuiltInRegistries.MENU,
            HowToMinecraft.ID
        )
        val MY_MENU: MenuType<MyMenu> by REGISTER.register("my_menu") { ->
            MenuType(::MyMenu, FeatureFlags.DEFAULT_FLAGS)
        }
    }

    init {
        // Add data slots for handled integers
        this.addDataSlot(dataSingle)

        // Check if the data inventory size is some fixed value
        val dataSize = dataInventory.slots
        check(dataSize >= 10) {
            throw IllegalArgumentException("Container size $dataSize is smaller than expected 10")
        }

        // Then, add slots for data inventory
        // If you are using a subtype of slot, make sure any data
        // used on the server is also available on the client.

        // Create the inventory by looping through the positions and
        // adding the slots.

        // Two rows
        for (j in 0..<2) {
            // Five columns
            for (i in 0..<5) {
                // Add for each slot in the data inventory
                this.addSlot(
                    SlotItemHandler(
                        // The inventory
                        dataInventory,
                        // The index of the data inventory this slot represents:
                        // rowIndex * columnCount + columnIndex
                        j * 5 + i,
                        // The x position relative to leftPos
                        // Vanilla slots are 18 units by default
                        // startX + columnIndex * slotRenderWidth
                        44 + i * 18,
                        // The y position relative to topPos
                        // Vanilla slots are 18 units by default
                        // startY + rowIndex * slotRenderHeight
                        20 + j * 18
                    )
                )
            }
        }

        // Add slots for player inventory (all 27 + 9 hotbar slots)
        // If you want to customize the 9x3 + 9 grid to something else,
        // loop through like above
        this.addStandardInventorySlots(
            playerInventory,
            // The starting x position relative to leftPos
            8,
            // The starting y position relative to topPos
            84
        )
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, EXAMPLE_BLOCK.get())
    }

    // Assume we have a data inventory of size 5
    // The inventory has 4 inputs (index 1 - 4) which outputs to a result slot (index 0)
    // We also have the 27 player inventory slots and the 9 hotbar slots
    // As such, the actual slots are indexed like so:
    //   - Data Inventory: Result (0), Inputs (1 - 4)
    //   - Player Inventory (5 - 31)
    //   - Player Hotbar (32 - 40)
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
