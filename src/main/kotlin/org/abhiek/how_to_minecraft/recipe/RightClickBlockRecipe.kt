package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.*
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.*

// The generic parameter for Recipe<T> is our RightClickBlockInput from above.
// An in-code representation of our recipe data. This can be basically anything you want.
// Common things to have here is a processing time integer of some kind, or an experience reward.
// Note that we now use an ingredient instead of an item stack for the input.
class RightClickBlockRecipe(
    val inputState: BlockState,
    val inputItem: Ingredient,
    val result: ItemStack
) : Recipe<RightClickBlockInput> {
    private lateinit var info: PlacementInfo

    companion object {
        val RECIPE_BOOK_CATEGORIES: DeferredRegister<RecipeBookCategory> = DeferredRegister.create(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            HowToMinecraft.ID
        )
        val RIGHT_CLICK_BLOCK_CATEGORY: RecipeBookCategory by RECIPE_BOOK_CATEGORIES.register(
            "right_click_block"
        ) { ->
            RecipeBookCategory()
        }
        val RIGHT_CLICK_BLOCK_SEARCH_CATEGORY = object : ExtendedRecipeBookCategory {}

        val RECIPE_TYPES: DeferredRegister<RecipeType<*>> = DeferredRegister.create(
            Registries.RECIPE_TYPE,
            HowToMinecraft.ID
        )
        val RIGHT_CLICK_BLOCK_TYPE: RecipeType<RightClickBlockRecipe> by RECIPE_TYPES.register(
            "right_click_block",
            // Creates the recipe type, setting `toString` to the registry name of the type
            RecipeType<RightClickBlockRecipe>::simple
        )
    }

    // Check whether the given input matches this recipe. The first parameter matches the generic.
    // We check our blockstate and our item stack, and only return true if both match.
    // If we needed to check the dimensions of our input, we would also do so here.
    override fun matches(input: RightClickBlockInput, level: Level): Boolean {
        return this.inputState === input.state && this.inputItem.test(input.stack)
    }

    // Return the result of the recipe here, based on the given input. The first parameter matches the generic.
    // IMPORTANT: Always call .copy() if you use an existing result! If you don't, things can and will break,
    // as the result exists once per recipe, but the assembled stack is created each time the recipe is crafted.
    override fun assemble(input: RightClickBlockInput, registries: HolderLookup.Provider): ItemStack {
        return this.result.copy()
    }

    // When true, will prevent the recipe from being synced within the recipe book or awarded on use/unlock.
    // This should only be true if the recipe shouldn't appear in a recipe book, such as map extending.
    // Although this recipe takes in an input state, it could still be used in a custom recipe book using
    //   the methods below.
    override fun isSpecial(): Boolean {
        return true
    }

    override fun recipeBookCategory(): RecipeBookCategory {
        return RIGHT_CLICK_BLOCK_CATEGORY
    }

    override fun placementInfo(): PlacementInfo {
        // This delegate is in case the ingredient is not fully populated at this point in time
        // Tags and recipes are loaded at the same time, which is why this might be the case.
        if (!this::info.isInitialized) {
            // Use optional ingredient as the block state may have an item representation
            val ingredients = mutableListOf<Optional<Ingredient>>()
            val stateItem = this.inputState.block.asItem()
            ingredients.add(
                if (stateItem != Items.AIR) Optional.of(Ingredient.of(stateItem)) else Optional.empty()
            )
            ingredients.add(Optional.of(this.inputItem))

            // Create placement info
            this.info = PlacementInfo.createFromOptionals(ingredients)
        }

        return this.info
    }

    override fun display(): List<RecipeDisplay> {
        // You can have many different displays for the same recipe
        // But this example will only use one like the other recipes.
        return listOf(
            // Add our recipe display with the specified slots
            RightClickBlockRecipeDisplay(
                BlockStateSlotDisplay(this.inputState),
                this.inputItem.display(),
                SlotDisplay.ItemStackSlotDisplay(this.result),
                SlotDisplay.ItemSlotDisplay(Items.GRASS_BLOCK)
            )
        )
    }

    override fun getType(): RecipeType<out Recipe<RightClickBlockInput>> {
        return RIGHT_CLICK_BLOCK_TYPE
    }

    override fun getSerializer(): RecipeSerializer<out Recipe<RightClickBlockInput>> {
        return RightClickBlockRecipeSerializer.RIGHT_CLICK_BLOCK
    }
}
