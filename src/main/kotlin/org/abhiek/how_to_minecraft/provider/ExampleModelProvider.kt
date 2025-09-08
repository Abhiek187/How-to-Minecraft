package org.abhiek.how_to_minecraft.provider

import com.mojang.math.Quadrant
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.model.*
import net.minecraft.client.renderer.block.model.Variant
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition
import net.minecraft.client.renderer.block.model.multipart.Condition
import net.minecraft.client.renderer.item.properties.numeric.UseDuration
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.util.random.Weighted
import net.minecraft.util.random.WeightedList
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.abhiek.how_to_minecraft.HowToMinecraft
import org.abhiek.how_to_minecraft.block.ModBlocks
import org.abhiek.how_to_minecraft.item.ModItems
import java.util.Optional

class ExampleModelProvider(output: PackOutput): ModelProvider(output, HowToMinecraft.ID) {
    companion object {
        // Assumes there is a texture referenced as '#base'
        // Can be resolved by either specifying 'base' or 'all'
        val BASE: TextureSlot = TextureSlot.create("base", TextureSlot.ALL)

        // Assume there exists some model 'how_to_minecraft:example_block/example_template'
        val EXAMPLE_TEMPLATE = ModelTemplate(
            // The parent model location
            Optional.of(
                ModelLocationUtils.getModelLocation(ModBlocks.EXAMPLE_BLOCK.get(), "example_template")
            ),
            // The suffix to apply to the end of any model that uses this template
            Optional.of("_example"),
            // All texture slots that must be defined
            // Should be as specific as possible based on what's undefined in the parent model
            TextureSlot.PARTICLE,
            BASE
        )

        val EXAMPLE_TEMPLATE_PROVIDER: TexturedModel.Provider = TexturedModel.createDefault(
            // Block to texture mapping
            { block ->
                TextureMapping()
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block))
                    .put(BASE, TextureMapping.getBlockTexture(block, "_base"))
            },
            // The template to generate from
            EXAMPLE_TEMPLATE
        )
    }

    override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {
        // Placeholders, their usages should be replaced with real values. See above for how to use the model builder,
        // and below for the helpers the model builder offers.
        val block = ModBlocks.EXAMPLE_BLOCK.get()

        // Create a simple block model with the same texture on each side.
        // The texture must be located at assets/<namespace>/textures/block/<path>.png, where
        // <namespace> and <path> are the block's registry name's namespace and path, respectively.
        // Used by the majority of (full) blocks, such as planks, cobblestone or bricks.
        blockModels.createTrivialCube(block)

        // Overload that accepts a `TexturedModel.Provider` to use.
        blockModels.createTrivialBlock(block, EXAMPLE_TEMPLATE_PROVIDER)

        // Block items have a model generated automatically
        // But let's assume you want to generate a different item, such as a flat item
        blockModels.registerSimpleFlatItemModel(block)

        // Adds a log block model. Requires two textures at assets/<namespace>/textures/block/<path>.png and
        // assets/<namespace>/textures/block/<path>_top.png, referencing the side and top texture, respectively.
        // Note that the block input here is limited to RotatedPillarBlock, which is the class vanilla logs use.
        blockModels.woodProvider(block).log(block)

        // Like WoodProvider#logWithHorizontal. Used by quartz pillars and similar blocks.
        blockModels.createRotatedPillarWithHorizontalVariant(
            block,
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        )

        // Using the `ExtendedModelTemplate` to specify the render type to use.
        blockModels.createRotatedPillarWithHorizontalVariant(
            block,
            TexturedModel.COLUMN_ALT.updateTemplate { template ->
                template.extend().renderType("minecraft:cutout").build()
            },
            TexturedModel.COLUMN_HORIZONTAL_ALT.updateTemplate { template ->
                template.extend().renderType(this.mcLocation("cutout_mipped")).build()
            }
        )

        // Specifies a horizontally-rotatable block model with a side texture, a front texture, and a top texture.
        // The bottom will use the side texture as well. If you don't need the front or top texture,
        // just pass in the side texture twice. Used by e.g. furnaces and similar blocks.
        blockModels.createHorizontallyRotatedBlock(
            block,
            TexturedModel.ORIENTABLE_ONLY_TOP.updateTexture { mapping ->
                mapping.put(TextureSlot.SIDE, this.modLocation("block/example_texture_side"))
                    .put(TextureSlot.FRONT, this.modLocation("block/example_texture_front"))
                    .put(TextureSlot.TOP, this.modLocation("block/example_texture_top"))
            }
        )

        // Specifies a horizontally-rotatable block model that is attached to a face, e.g. for buttons.
        // Accounts for placing the block on the ground and on the ceiling, and rotates them accordingly.
        blockModels.familyWithExistingFullBlock(block).button(block)

        // Create a model to use for blockstatefiles
        val modelLoc = TexturedModel.CUBE.create(block, blockModels.modelOutput)

        // Create a common variant to transform
        val variant = Variant(modelLoc)

        // Basic single variant model
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(
                block,
                MultiVariant(
                    WeightedList.of(
                        Weighted(
                            // Set model
                            variant
                                // Set rotations around the x and y axes
                                .with(VariantMutator.X_ROT.withValue(Quadrant.R90))
                                .with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                                // Set a uvlock
                                .with(VariantMutator.UV_LOCK.withValue(true)),
                            // Set a weight
                            5
                        )
                    )
                )
            )
        )

        // Add one or multiple models based on the block state properties
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(
                block,
                // Create the basic multi-variant
                BlockModelGenerators.variant(variant)
            ).with(
                // Apply a property dispatch
                // Will mutate the variant based on the provided mutators
                PropertyDispatch.modify(BlockStateProperties.AXIS)
                    .select(Direction.Axis.Y, BlockModelGenerators.NOP)
                    .select(Direction.Axis.Z, BlockModelGenerators.X_ROT_90)
                    .select(Direction.Axis.X, BlockModelGenerators.X_ROT_90
                        .then(BlockModelGenerators.Y_ROT_90))
            )
        )

        // Generate a multipart
        blockModels.blockStateOutput.accept(
            MultiPartGenerator.multiPart(block)
                // Provide the base model
                .with(BlockModelGenerators.variant(variant))
                // Add conditions for variant to appear
                .with(
                    // Add conditions to apply
                    CombinedCondition(
                        CombinedCondition.Operation.OR,
                        listOf<Condition>(
                            // Where at least one of the conditions are true
                            BlockModelGenerators.condition()
                                .term(BlockStateProperties.FACING, Direction.NORTH, Direction.SOUTH)
                                .build(),
                            // Can nest as many conditions or groups as necessary
                            CombinedCondition(
                                CombinedCondition.Operation.AND,
                                mutableListOf<Condition>(
                                    BlockModelGenerators.condition()
                                        .term(BlockStateProperties.FACING, Direction.NORTH)
                                        .build()
                                )
                            )
                        )
                    ),
                    // Supply variant to mutate
                    BlockModelGenerators.variant(variant)
                )
        )

        // The most common item:
        // item/generated with the layer0 texture as the item name
        itemModels.generateFlatItem(ModItems.EXAMPLE_ITEM, ModelTemplates.FLAT_ITEM)

        // A bow-like item
        val bow = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(ModItems.EXAMPLE_ITEM))
        val pullingBow0 = ItemModelUtils.plainModel(
            itemModels.createFlatItemModel(
                ModItems.EXAMPLE_ITEM,
                "_pulling_0",
                ModelTemplates.BOW
            )
        )
        val pullingBow1 = ItemModelUtils.plainModel(
            itemModels.createFlatItemModel(
                ModItems.EXAMPLE_ITEM,
                "_pulling_1",
                ModelTemplates.BOW
            )
        )
        val pullingBow2 = ItemModelUtils.plainModel(
            itemModels.createFlatItemModel(
                ModItems.EXAMPLE_ITEM,
                "_pulling_2",
                ModelTemplates.BOW
            )
        )
        itemModels.itemModelOutput.accept(
            ModItems.EXAMPLE_ITEM,
            // Conditional model for item
            ItemModelUtils.conditional(
                // Checks if item is being used
                ItemModelUtils.isUsingItem(),
                // When true, select model based on use duration
                ItemModelUtils.rangeSelect(
                    UseDuration(false),
                    // Scalar to apply to the thresholds
                    0.05f,
                    pullingBow0,
                    // Threshold when 0.65
                    ItemModelUtils.override(pullingBow1, 0.65f),
                    // Threshold when 0.9
                    ItemModelUtils.override(pullingBow2, 0.9f)
                ),
                // When false, use the base bow model
                bow
            )
        )
    }
}
