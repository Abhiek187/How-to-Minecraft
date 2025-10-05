package org.abhiek.how_to_minecraft.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.gui.render.state.GuiRenderState
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import org.abhiek.how_to_minecraft.HowToMinecraft

// Takes in the buffers used to write the object to the picture
class ExampleRenderer(bufferSource: MultiBufferSource.BufferSource)
    : PictureInPictureRenderer<ExampleRenderState>(bufferSource) {
    override fun getRenderStateClass(): Class<ExampleRenderState> {
        // Returns the render state class
        return ExampleRenderState::class.java
    }

    override fun getTextureLabel(): String {
        // Can be any string, but should be unique
        // Prefix with mod id for greater clarity
        return "${HowToMinecraft.ID}: example picture-in-picture"
    }

    override fun renderToTexture(renderState: ExampleRenderState, pose: PoseStack) {
        // Modify pose if desired
        // Can push/pop if wanted, but a new `PoseStack` is created for writing to the picture
        pose.translate(10f, 10f, 10f)

        // Render the object to the screen
        val consumer = this.bufferSource.getBuffer(RenderType.lines())
        consumer.addVertex(10f, 10f, 10f)
            .setColor(0xFFFF0000.toInt())
            .setNormal(1f, 1f, 1f)
        consumer.addVertex(10f, 10f, 10f)
            .setColor(0xFF00FFFF.toInt())
            .setNormal(1f, 1f, 1f)
    }

    // Additional methods
    override fun blitTexture(renderState: ExampleRenderState, guiState: GuiRenderState) {
        // Submits the picture to the gui render state as a `BlitRenderState` by default
        // Override this if you want to modify the `BlitRenderState`
        // Should call `GuiRenderState#submitBlitToCurrentLayer`
        // Bounds can be `null`
        super.blitTexture(renderState, guiState)
    }

    override fun textureIsReadyToBlit(renderState: ExampleRenderState): Boolean {
        // When true, this reuses the already written-to picture instead of
        // constructing a new picture and writing to it using `renderToTexture`.
        // This should only be true if it is guaranteed that two elements will
        // be rendered *exactly* the same.
        return super.textureIsReadyToBlit(renderState)
    }

    override fun getTranslateY(scaledHeight: Int, guiScale: Int): Float {
        // Sets the initial offset the `PoseStack` is translated by in the Y direction.
        // Common implementations use `scaledHeight / 2f` to center the Y coordinate similar to X.
        return scaledHeight.toFloat()
    }

    override fun canBeReusedFor(state: ExampleRenderState, textureWidth: Int, textureHeight: Int): Boolean {
        // A NeoForge-added method used to check if this renderer can be reused on a subsequent frame.
        // When true, this will reuse the constructed state and renderer from the previous frame.
        // When false, a new renderer will be created.
        return super.canBeReusedFor(state, textureWidth, textureHeight)
    }
}
