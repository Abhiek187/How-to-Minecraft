package org.abhiek.how_to_minecraft.gui

import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState

// Other parameters can be added, but this is the minimum required to implement all methods
data class ExampleRenderState(
    val x0: Int, // the left X
    val x1: Int, // the right X
    val y0: Int, // the top Y
    val y1: Int, // the bottom Y
    val scale: Float, // the scale factor when drawing to the picture
    val scissorArea: ScreenRectangle?, // the rendering area
    val bounds: ScreenRectangle? // the bounds of the element
): PictureInPictureRenderState {
    // Additional constructors
    constructor(x: Int, y: Int, width: Int, height: Int, scissorArea: ScreenRectangle?): this(
        x,  // x0
        x + width,  // x1
        y,  // y0
        y + height,  // y1
        1f,  // scale
        scissorArea,
        PictureInPictureRenderState.getBounds(x, y, x + width, y + height, scissorArea)
    )

    override fun x0() = x0
    override fun x1() = x1
    override fun y0() = y0
    override fun y1() = y1
    override fun scale() = scale
    override fun scissorArea() = scissorArea
    override fun bounds() = bounds
}
