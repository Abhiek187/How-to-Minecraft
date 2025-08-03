package org.abhiek.how_to_minecraft.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks =
        DeferredRegister.createBlocks(HowToMinecraft.ID)
    // Alternatively:
//    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(
//        // The registry we want to use.
//        // Minecraft's registries can be found in BuiltInRegistries, NeoForge's registries can be found in NeoForgeRegistries.
//        // Mods may also add their own registries, refer to the individual mod's documentation or source code for where to find them.
//        BuiltInRegistries.BLOCK,
//        // Our mod id.
//        HowToMinecraft.ID
//    )

    // If you get an "overload resolution ambiguity" error, include the arrow at the start of the closure.
    // A block is created for the world, but a separate BlockItem needs to be created to have it appear in your inventory.
    // Using commands: /setblock ~ ~ ~ how_to_minecraft:example_block
//    val EXAMPLE_BLOCK: Block by BLOCKS.register("example_block") { registryName ->
//        Block(
//            BlockBehaviour.Properties.of()
//                // Required
//                .setId(ResourceKey.create(Registries.BLOCK, registryName))
//                // Stone = 1.5, dirt = 0.5, obsidian = 50, bedrock = -1 (unbreakable)
//                .destroyTime(2.0f)
//                // Stone = 6.0, dirt = 0.5, obsidian = 1,200, bedrock = 3,600,000 🤯
//                .explosionResistance(10.0f)
//                // Default: SoundType.STONE
//                .sound(SoundType.GRAVEL)
//                // 0-15, glowstone = 15, torch = 14
//                .lightLevel { state -> 7 }
//                // Default: 0.6, ice = 0.98 (the opposite of how friction works IRL)
//                .friction(0.6f)
//        )
//    }
    // Alternatively:
    // = --> DeferredBlock<Block>, by --> Block
    val EXAMPLE_BLOCK: DeferredBlock<Block> = BLOCKS.registerSimpleBlock(
        "example_block",
        // ::Block by default
        BlockBehaviour.Properties.of()
            // setId called automatically
            .lightLevel { 15 }
            .strength(3.0f)
    )
}
