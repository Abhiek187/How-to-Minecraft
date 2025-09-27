package org.abhiek.how_to_minecraft.recipe

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture

// Construct the provider to run
open class MyRecipeProvider protected constructor(
    provider: HolderLookup.Provider,
    output: RecipeOutput
): RecipeProvider(provider, output) {
    protected override fun buildRecipes() {
        // Our constructor parameters. This example adds the ever-popular dirt -> diamond conversion.
        RightClickBlockRecipeBuilder(
            ItemStack(Items.DIAMOND),
            Blocks.DIRT.defaultBlockState(),
            Ingredient.of(Items.APPLE)
        )
            .unlockedBy("has_apple", this.has(Items.APPLE))
            .save(output)
        // other recipe builders here
    }

    // The runner to add to the data generator
    // Get the parameters from the `GatherDataEvent`s.
    class Runner(
        output: PackOutput,
        lookupProvider: CompletableFuture<HolderLookup.Provider>
    ): RecipeProvider.Runner(output, lookupProvider) {
        override fun createRecipeProvider(provider: HolderLookup.Provider, output: RecipeOutput): RecipeProvider {
            return MyRecipeProvider(provider, output)
        }

        override fun getName(): String {
            return "MyRecipeProvider"
        }
    }
}
