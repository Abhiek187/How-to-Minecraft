package org.abhiek.how_to_minecraft.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.abhiek.how_to_minecraft.HowToMinecraft

class MyScreen(title: Component): Screen(title) {
    override fun init() {
        super.init()

        // Add widgets and precomputed values
        val editBox = EditBox(
            Minecraft.getInstance().font,
            200,
            50,
            Component.translatable("gui.${HowToMinecraft.ID}.my_screen.edit_box")
        )
        this.addRenderableWidget(editBox)
        this.addRenderableWidget(
            Button
                .builder(Component.translatable("gui.${HowToMinecraft.ID}.my_screen.button")) {
                    println("You typed: ${editBox.value}")
                }
                .bounds(100, 100, 50, 20)
                .build()
        )
    }

    override fun tick() {
        super.tick()

        // Execute some logic every frame
    }

    // mouseX and mouseY indicate the scaled coordinates of where the cursor is in on the screen
    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Submit things on the background stratum
        this.renderTransparentBackground(graphics)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Submit things before widgets

        // Then the widgets if this is a direct child of the Screen
        super.render(graphics, mouseX, mouseY, partialTick)

        // Submit things after widgets

        // Set the tooltip to be added above everything in this method
        graphics.setTooltipForNextFrame(
            Component.translatable("gui.${HowToMinecraft.ID}.my_screen.tooltip"),
            200,
            200
        )
    }

    override fun onClose() {
        // Stop any handlers here

        // Call last in case it interferes with the override
        super.onClose()
    }

    override fun removed() {
        // Reset initial states here

        // Call last in case it interferes with the override
        super.removed()
    }
}
