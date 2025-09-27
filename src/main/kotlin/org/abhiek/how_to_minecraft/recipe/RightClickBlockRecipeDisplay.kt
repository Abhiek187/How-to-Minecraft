package org.abhiek.how_to_minecraft.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

// A simple recipe display
data class RightClickBlockRecipeDisplay(
    val inputState: SlotDisplay,
    val inputItem: SlotDisplay,
    val mResult: SlotDisplay,  // implements RecipeDisplay#result
    val mCraftingStation: SlotDisplay // implements RecipeDisplay#craftingStation
) : RecipeDisplay {
    override fun type(): RecipeDisplay.Type<out RecipeDisplay> {
        // Return the registered type from below
        return RIGHT_CLICK_BLOCK_RECIPE_DISPLAY
    }

    override fun result(): SlotDisplay {
        return mResult
    }

    override fun craftingStation(): SlotDisplay {
        return mCraftingStation
    }

    companion object {
        val MAP_CODEC: MapCodec<RightClickBlockRecipeDisplay> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                SlotDisplay.CODEC.fieldOf("inputState")
                    .forGetter(RightClickBlockRecipeDisplay::inputState),
                SlotDisplay.CODEC.fieldOf("inputState")
                    .forGetter(RightClickBlockRecipeDisplay::inputItem),
                SlotDisplay.CODEC.fieldOf("result")
                    .forGetter(RightClickBlockRecipeDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station")
                    .forGetter(RightClickBlockRecipeDisplay::craftingStation)
            ).apply(instance, ::RightClickBlockRecipeDisplay)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, RightClickBlockRecipeDisplay> = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            RightClickBlockRecipeDisplay::inputState,
            SlotDisplay.STREAM_CODEC,
            RightClickBlockRecipeDisplay::inputItem,
            SlotDisplay.STREAM_CODEC,
            RightClickBlockRecipeDisplay::mResult,
            SlotDisplay.STREAM_CODEC,
            RightClickBlockRecipeDisplay::mCraftingStation,
            ::RightClickBlockRecipeDisplay
        )

        val RECIPE_DISPLAY_TYPES: DeferredRegister<RecipeDisplay.Type<*>> = DeferredRegister.create(
            BuiltInRegistries.RECIPE_DISPLAY,
            HowToMinecraft.ID
        )
        val RIGHT_CLICK_BLOCK_RECIPE_DISPLAY: RecipeDisplay.Type<RightClickBlockRecipeDisplay> by
            RECIPE_DISPLAY_TYPES.register("right_click_block") { ->
                RecipeDisplay.Type(MAP_CODEC, STREAM_CODEC)
            }
    }
}
