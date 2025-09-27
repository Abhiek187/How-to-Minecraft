package org.abhiek.how_to_minecraft.recipe

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.block.state.BlockState

// Since we have exactly one of each input, we pass them to the constructor.
// Builders for recipe serializers that have ingredient lists of some sort would usually
// initialize an empty list and have #addIngredient or similar methods instead.
class RightClickBlockRecipeBuilder(
    result: ItemStack,
    private val inputState: BlockState,
    private val inputItem: Ingredient
): SimpleRecipeBuilder(result) {
    // Saves a recipe using the given RecipeOutput and key. This method is defined in the RecipeBuilder interface.
    override fun save(output: RecipeOutput, key: ResourceKey<Recipe<*>?>) {
        // Build the advancement.
        val advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
            .rewards(AdvancementRewards.Builder.recipe(key))
            .requirements(AdvancementRequirements.Strategy.OR)
        this.criteria.forEach(advancement::addCriterion)
        // Our factory parameters are the result, the block state, and the ingredient.
        val recipe = RightClickBlockRecipe(this.inputState, this.inputItem, this.result)
        // Pass the id, the recipe, and the recipe advancement into the RecipeOutput.
        output.accept(key, recipe, advancement.build(key.location().withPrefix("recipes/")))
    }
}
