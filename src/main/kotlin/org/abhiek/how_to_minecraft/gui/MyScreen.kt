package org.abhiek.how_to_minecraft.gui

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.client.settings.KeyModifier
import org.abhiek.how_to_minecraft.HowToMinecraft
import org.lwjgl.glfw.GLFW

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

    override fun keyPressed(key: Int, scancode: Int, mods: Int): Boolean {
        val inputKey = InputConstants.getKey(key, scancode)
        println("keyPressed key=$key, scancode=$scancode, mods=$mods, inputKey=$inputKey")
        if (EXAMPLE_MAPPING.value.isActiveAndMatches(inputKey)) {
            // Execute logic to perform on key press here
            println("Pressed P on MyScreen")
            return true
        }
        return super.keyPressed(key, scancode, mods)
    }

    override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
        val mouseKey = InputConstants.Type.MOUSE.getOrCreate(button)
        println("mouseClicked x=$x, y=$y, button=$button, mouseKey=$mouseKey")
        if (EXAMPLE_MAPPING_2.value.isActiveAndMatches(mouseKey)) {
            // Execute logic to perform on mouse click here
            println("Clicked on MyScreen")
            return true
        }
        return super.mouseClicked(x, y, button)
    }

    companion object {
        // Key mapping is lazily initialized so it doesn't exist until it is registered
        val EXAMPLE_MAPPING = lazy {
            KeyMapping(
                // Will be localized using this translation key
                "key.${HowToMinecraft.ID}.example1",
                // Default mapping is on the keyboard
                InputConstants.Type.KEYSYM,
                // Default key is P (GLFW = Graphics Library Framework)
                // LWJGL = Lightweight Java Game Library
                GLFW.GLFW_KEY_P,
                // Mapping will be in the misc category
                "key.categories.misc"
            )
        }
        val EXAMPLE_MAPPING_2 = lazy {
            KeyMapping(
                "key.${HowToMinecraft.ID}.example2",
                // Mapping can only be used when a screen is open
                KeyConflictContext.GUI,
                // Default mapping is on the mouse
                InputConstants.Type.MOUSE,
                // Default mouse input is the left mouse button
                GLFW.GLFW_MOUSE_BUTTON_LEFT,
                // Mapping will be in the new example category
                "key.categories.${HowToMinecraft.ID}.examplecategory"
            )
        }
        val EXAMPLE_MAPPING_3 = lazy {
            KeyMapping(
                "key.${HowToMinecraft.ID}.example3",
                KeyConflictContext.UNIVERSAL,
                // Default mapping requires shift to be held down
                KeyModifier.SHIFT,
                // Default mapping is on the keyboard
                InputConstants.Type.KEYSYM,
                // Default key is G
                GLFW.GLFW_KEY_G,
                "key.categories.misc"
            )
        }
    }
}
