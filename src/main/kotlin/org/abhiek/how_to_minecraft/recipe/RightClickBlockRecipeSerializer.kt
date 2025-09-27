package org.abhiek.how_to_minecraft.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

// The generic parameter is our recipe class.
// Note: This assumes that simple RightClickBlockRecipe#getInputState, #getInputItem and #getResult getters
// are available, which were omitted from the code above.
class RightClickBlockRecipeSerializer: RecipeSerializer<RightClickBlockRecipe> {
    // Return our map codec
    override fun codec(): MapCodec<RightClickBlockRecipe> {
        return CODEC
    }

    // Return our stream codec
    @Deprecated("Deprecated in Java")
    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, RightClickBlockRecipe> {
        return STREAM_CODEC
    }

    companion object {
        val CODEC: MapCodec<RightClickBlockRecipe> =
            RecordCodecBuilder.mapCodec { inst ->
                inst.group(
                    BlockState.CODEC.fieldOf("state")
                        .forGetter(RightClickBlockRecipe::inputState),
                    Ingredient.CODEC.fieldOf("ingredient")
                        .forGetter(RightClickBlockRecipe::inputItem),
                    ItemStack.CODEC.fieldOf("result")
                        .forGetter(RightClickBlockRecipe::result)
                ).apply(inst, ::RightClickBlockRecipe)
            }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, RightClickBlockRecipe> = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
            RightClickBlockRecipe::inputState,
            Ingredient.CONTENTS_STREAM_CODEC,
            RightClickBlockRecipe::inputItem,
            ItemStack.STREAM_CODEC,
            RightClickBlockRecipe::result,
            ::RightClickBlockRecipe
        )

        val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER,
            HowToMinecraft.ID
        )

        val RIGHT_CLICK_BLOCK: RecipeSerializer<RightClickBlockRecipe> by RECIPE_SERIALIZERS.register(
            "right_click_block",
            ::RightClickBlockRecipeSerializer
        )
    }
}
