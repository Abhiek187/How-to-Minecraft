package org.abhiek.how_to_minecraft.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.Vec3

// Assumes the existence of MyBlockEntity as a subclass of BlockEntity.
// Add the constructor parameter for the lambda below. You may also use it to get some context
// to be stored in local fields, such as the entity renderer dispatcher, if needed.
// BER = BlockEntityRenderer
class MyBlockEntityRenderer(context: BlockEntityRendererProvider.Context): BlockEntityRenderer<MyBlockEntity> {
    // This method is called every frame in order to render the block entity. Parameters are:
    // - blockEntity:   The block entity instance being rendered. Uses the generic type passed to the super interface.
    // - partialTick:   The amount of time, in fractions of a tick (0.0 to 1.0), that has passed since the last tick
    // - poseStack:     The pose stack to render to
    // - bufferSource:  The buffer source to get vertex buffers from
    // - packedLight:   The light value of the block entity
    // - packedOverlay: The current overlay value of the block entity, usually OverlayTexture.NO_OVERLAY
    // - cameraPos:     The position of the renderer's camera
    override fun render(
        blockEntity: MyBlockEntity,
        partialTick: Float,
        stack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        cameraPos: Vec3
    ) {
        // Do the rendering here
    }
}
