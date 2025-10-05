package org.abhiek.how_to_minecraft.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.abhiek.how_to_minecraft.HowToMinecraft

class MyContainerScreen(menu: MyMenu, playerInventory: Inventory, title: Component)
    : AbstractContainerScreen<MyMenu>(menu, playerInventory, title) {
    private val label = Component.translatable("gui.${HowToMinecraft.ID}.my_container_screen.label")
    private val labelX = 20
    private val labelY = 20

    init {
        this.titleLabelX = 10
        this.inventoryLabelX = 10

        // If the 'imageHeight' is changed, 'inventoryLabelY' must also be
        // changed as the value depends on the 'imageHeight' value.
    }

    override fun containerTick() {
        super.containerTick()

        // Tick things here
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Submits the widgets and labels to be rendered
        super.render(graphics, mouseX, mouseY, partialTick)

        // This method is added by the container screen to submit
        // the tooltip of the hovered slot in the tooltip stratum.
        this.renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        // Submits the background texture. 'leftPos' and 'topPos' should
        // already represent the top left corner of where the texture
        // should be rendered as it was precomputed from the 'imageWidth'
        // and 'imageHeight'. The two zeros represent the integer u/v
        // coordinates inside the PNG file, whose size is represented by
        // the last two integers (typically 256 x 256).
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND_LOCATION,
            this.leftPos, this.topPos,
            0f, 0f,
            this.imageWidth, this.imageHeight,
            256, 256
        )
    }

    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.renderLabels(graphics, mouseX, mouseY)

        // The color is an ARGB value
        // The final boolean renders the drop shadow when true
        graphics.drawString(
            this.font,
            this.label,
            this.labelX,
            this.labelY,
            0xFF404040.toInt(),
            false
        )
    }

    // The location of the background texture (assets/<namespace>/<path>)
    companion object {
        val BACKGROUND_LOCATION: ResourceLocation = ResourceLocation.fromNamespaceAndPath(
            HowToMinecraft.ID,
            "textures/gui/container/my_container_screen.png"
        )
    }
}
