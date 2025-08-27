package org.abhiek.how_to_minecraft.entity

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.layers.RenderLayer
import org.abhiek.how_to_minecraft.entity.MyMobModel.Companion.MY_LAYER

// The generic parameters need the proper types you used everywhere else up to this point.
class MyRenderLayer(renderer: MyMobRenderer, entityModelSet: EntityModelSet):
    RenderLayer<MyMobRenderState, MyMobModel>(renderer) {
    // Bake and store our layer definition, using the ModelLayerLocation from back when we registered the layer definition.
    // If applicable, you can also store multiple models this way and use them below.
    private val model = MyMobModel(entityModelSet.bakeLayer(MY_LAYER))

    override fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        renderState: MyMobRenderState,
        yRot: Float,
        xRot: Float
    ) {
        // Render the layer here. We have stored the entity model in a field, you probably want to use it in some way.
    }
}
