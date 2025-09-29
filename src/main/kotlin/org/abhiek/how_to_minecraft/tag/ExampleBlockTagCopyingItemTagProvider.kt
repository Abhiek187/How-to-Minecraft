package org.abhiek.how_to_minecraft.tag

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider
import org.abhiek.how_to_minecraft.HowToMinecraft
import java.util.concurrent.CompletableFuture

class ExampleBlockTagCopyingItemTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    // Obtained from BlockTagsProvider#contentsGetter
    blockTags: CompletableFuture<TagLookup<Block>>
): BlockTagCopyingItemTagProvider(output, lookupProvider, blockTags, HowToMinecraft.ID) {
    companion object {
        val EXAMPLE_BLOCK_TAG: TagKey<Block> = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example_block_tag")
        )
        val EXAMPLE_ITEM_TAG: TagKey<Item> = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example_item_tag")
        )
    }

    override fun addTags(lookupProvider: HolderLookup.Provider) {
        // Assuming types TagKey<Block> and TagKey<Item> for the two parameters
        this.copy(EXAMPLE_BLOCK_TAG, EXAMPLE_ITEM_TAG)
    }
}
