package org.abhiek.how_to_minecraft.entity

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.ItemStack
import org.abhiek.how_to_minecraft.HowToMinecraft
import org.abhiek.how_to_minecraft.entity.MyMobModel.Companion.MY_LAYER

// The generic type in the superclass should be set to what entity you want to render.
// If you wanted to enable rendering for any entity, you'd use Entity.
// You'd also use an EntityRenderState that fits your use case. More on this below.
// In our constructor, we just forward to super.
// Plugging in our custom render state class as the generic type.
// Also, we need to implement RenderLayerParent. Some existing renderers, such as LivingEntityRenderer, do this for you.
// For LivingEntityRenderer, the super constructor requires a "base" model and a shadow radius to be supplied.
class MyMobRenderer(context: EntityRendererProvider.Context):
    LivingEntityRenderer<Mob, MyMobRenderState, MyMobModel>(
        context,
        MyMobModel(context.bakeLayer(MY_LAYER)), 0.5f
) {
    // Tell the render engine how to create a new entity render state
    override fun createRenderState(): MyMobRenderState {
        return MyMobRenderState()
    }

    // Update the render state by copying the needed values from the passed entity to the passed state
    override fun extractRenderState(entity: Mob, state: MyMobRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)

        // Extract and store any additional values in the state here
        state.stackInHand = ItemStack.EMPTY
    }

    // Actually render the entity.
    // Calling super will handle leash and name tag rendering for you, if applicable.
    override fun render(
        state: MyMobRenderState,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(state, poseStack, bufferSource, packedLight)
        // Do your own rendering here
    }

    // getTextureLocation is an abstract method in LivingEntityRenderer that we need to override.
    // The texture path is relative to textures/entity, so in this example, the texture should be located at
    // assets/how_to_minecraft/textures/entity/example_mob.png. The texture will then be supplied to and used by the model.
    override fun getTextureLocation(state: MyMobRenderState): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example_mob")
    }
}
