package org.abhiek.how_to_minecraft.entity

import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import org.abhiek.how_to_minecraft.HowToMinecraft

// The ModelPart passed here is the root of our baked model.
// The super constructor call can optionally specify a RenderType.
class MyMobModel(root: ModelPart): EntityModel<MyMobRenderState>(root) {
    // Storing specific model parts as fields for use below.
    // Store the head part for use below.
    private val head = root.getChild("head")
    // Bake the animation for the model
    // Pass in whatever 'ModelPart' that the animation is applied to
    // It should cover all referenced bones
    private val example = EXAMPLE_ANIMATION.get().bake(root)

    companion object {
        // Our ModelLayerLocation
        val MY_LAYER = ModelLayerLocation(
            // Should be the name of the entity this layer belongs to.
            // May be more generic if this layer can be used on multiple entities.
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example_mob"),
            // The name of the layer itself. Should be main for the entity's base model,
            // and a more descriptive name (e.g. "wings") for more specific layers.
            "main"
        )
        // Create and store a reference to the animation holder.
        val EXAMPLE_ANIMATION = Model.getAnimation(
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example")
        )

        // A static method in which we create our layer definition. createBodyLayer() is the name
        // most vanilla models use. If you have multiple layers, you will have multiple of these static methods.
        fun createBodyLayer(): LayerDefinition {
            // Create our mesh.
            val mesh = MeshDefinition()
            // The mesh initially contains no object other than the root, which is invisible (has a size of 0x0x0).
            val root = mesh.root
            // We add a head part.
            val head = root.addOrReplaceChild(
                // The name of the part.
                "head",
                // The CubeListBuilder we want to add.
                CubeListBuilder.create()
                    // The UV coordinates to use within the texture. Texture binding itself is explained below.
                    // In this example, we start at U=10, V=20.
                    .texOffs(10, 20)
                    // Add our cube. May be called multiple times to add multiple cubes.
                    // This is relative to the parent part. For the root part, it is relative to the entity's position.
                    // Be aware that the y-axis is flipped, i.e. "up" is subtractive and "down" is additive.
                    .addBox(
                        // The top-left-back corner of the cube, relative to the parent object's position.
                        -5f, -5f, -5f,
                        // The size of the cube.
                        10f, 10f, 10f
                    )
                    // Call texOffs and addBox again to add another cube.
                    .texOffs(30, 40)
                    .addBox(-1f, -1f, -1f, 1f, 1f, 1f)
                    // Various overloads of addBox() are available, which allow for additional operations
                    // such as texture mirroring, texture scaling, specifying the directions to be rendered,
                    // and a global scale to all cubes, known as a CubeDeformation.
                    // This example uses the latter, please check the usages of the individual methods for more examples.
                    .texOffs(50, 60)
                    .addBox(5f, 5f, 5f, 4f, 4f, 4f,
                        CubeDeformation(1.2f)),
                // The initial positioning to apply to all elements of the CubeListBuilder. Besides PartPose#offset,
                // PartPose#offsetAndRotation is also available. This can be reused across multiple PartDefinitions.
                // This may not be used by all models. For example, making custom armor layers will use the associated
                // player (or other humanoid) renderer's PartPose instead to have the armor "snap" to the player model.
                PartPose.offset(0f, 8f, 0f)
            )
            // We can now add children to any PartDefinition, thus creating a hierarchy.
//            val part1 = root.addOrReplaceChild()
//            val part2 = head.addOrReplaceChild()
//            val part3 = part1.addOrReplaceChild()
            // At the end, we create a LayerDefinition from the MeshDefinition.
            // The two integers are the expected dimensions of the texture; 64x32 in our example.
            return LayerDefinition.create(mesh, 64, 32)
        }
    }

    // Use this method to update the model rotations, visibility etc. from the render state. If you change the
    // generic parameter of the EntityModel superclass, this parameter type changes with it.
    override fun setupAnim(state: MyMobRenderState) {
        // Calling super to reset all values to default.
        super.setupAnim(state)

        // Change the model parts.
//        head.visible = state.myBoolean
//        head.xRot = state.myXRotation
//        head.yRot = state.myYRotation
//        head.zRot = state.myZRotation
        this.example.apply(
            // Get the animation state to use from your EntityRenderState.
            state.myAnimationState,
            // Your entity age, in ticks.
            state.ageInTicks
        )
        // A specialized version of apply(), designed for walking animations.
        this.example.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1f, 1f)
        // A version of apply() that only applies the first frame of animation.
        this.example.applyStatic()
    }
}
