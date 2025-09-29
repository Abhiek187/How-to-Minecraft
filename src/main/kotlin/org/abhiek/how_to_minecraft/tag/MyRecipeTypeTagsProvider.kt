package org.abhiek.how_to_minecraft.tag

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagAppender
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagEntry
import net.minecraft.tags.TagKey
import net.minecraft.world.item.crafting.RecipeType
import org.abhiek.how_to_minecraft.HowToMinecraft
import java.util.concurrent.CompletableFuture

// Get parameters from the `GatherDataEvent`s
class MyRecipeTypeTagsProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
): TagsProvider<RecipeType<*>>(output, Registries.RECIPE_TYPE, lookupProvider, HowToMinecraft.ID) {
    companion object {
        val MY_TAG: TagKey<RecipeType<*>> = TagKey.create(
            // The registry key. The type of the registry must match the generic type of the tag.
            Registries.RECIPE_TYPE,
            // The location of the tag. This example will put our tag at data/how_to_minecraft/tags/blocks/example_tag.json.
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example_tag")
        )
        val SMELTERS: TagKey<RecipeType<*>> = TagKey.create(
            Registries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "smelters")
        )
        val CRAFTERS: TagKey<RecipeType<*>> = TagKey.create(
            Registries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "crafters")
        )
        val SMITHERS: TagKey<RecipeType<*>> = TagKey.create(
            Registries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "smithers")
        )
    }

    // Let's assume the following TagKey<RecipeType<?>> SMELTERS, CRAFTERS, SMITHERS
    override fun addTags(lookupProvider: HolderLookup.Provider) {
        // Create a TagBuilder for `ResourceLocation`s
        this.getOrCreateRawBuilder(MY_TAG)
            // Add entries
            .addElement(
                ResourceLocation.fromNamespaceAndPath("minecraft", "crafting")
            )
            .addElement(
                ResourceLocation.fromNamespaceAndPath("minecraft", "smelting")
            )
            // Add optional entries that will be ignored if absent
            .addOptionalElement(
                ResourceLocation.fromNamespaceAndPath("minecraft", "blasting")
            )
            // Add a tag entry
            .addTag(SMELTERS.location())
            // Add an optional tag entry that will be ignored if absent
            .addOptionalTag(CRAFTERS.location())
            // Set the replace property to true
            .replace()
            // Set the replace property back to false
            .replace(false)
            // Remove entries
            .removeElement(
                ResourceLocation.fromNamespaceAndPath("minecraft", "campfire_cooking")
            )
            // Remove a tag entry
            .removeTag(SMITHERS.location())

        // Create the TagAppender for `ResourceLocation`s
        this.tag(MY_TAG)
            // Replace property info
            .replace()
            // Handle any optional elements that may not be present
            .addOptional(ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example_type"))
            // Can take in a TagKey
            .addOptionalTag(CRAFTERS)
            // Map to ResourceKey (KeyTagProvider)
            .map(ResourceKey<*>::location)
            .add(BuiltInRegistries.RECIPE_TYPE.getResourceKey(RecipeType.CRAFTING).orElseThrow())
            // Map to direct object (IntrinsicHolderTagsProvider)
            .map { type: RecipeType<*> -> BuiltInRegistries.RECIPE_TYPE.getResourceKey(type).orElseThrow() }
            .add(RecipeType.SMELTING)
            .addTag(SMELTERS)
            .remove(RecipeType.CAMPFIRE_COOKING)
            .remove(SMITHERS)
    }

    private fun tag(tag: TagKey<RecipeType<*>>): TagAppender<ResourceLocation, RecipeType<*>> {
        // Create the builder
        val builder = this.getOrCreateRawBuilder(tag)

        // Generate the appender (can use TagAppender#forBuilder) instead
        return object : TagAppender<ResourceLocation, RecipeType<*>> {
            override fun add(element: ResourceLocation): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.addElement(element)
                return this
            }

            override fun addOptional(element: ResourceLocation): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.addOptionalElement(element)
                return this
            }

            override fun addTag(tag: TagKey<RecipeType<*>>): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.addTag(tag.location())
                return this
            }

            override fun addOptionalTag(tag: TagKey<RecipeType<*>>): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.addOptionalTag(tag.location())
                return this
            }

            // For situations where you cannot access the current entry object
            override fun add(entry: TagEntry): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.add(entry)
                return this
            }

            override fun replace(value: Boolean): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.replace(value)
                return this
            }

            override fun remove(element: ResourceLocation): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.removeElement(element)
                return this
            }

            override fun remove(tag: TagKey<RecipeType<*>>): TagAppender<ResourceLocation, RecipeType<*>> {
                builder.removeTag(tag.location())
                return this
            }
        }
    }
}
