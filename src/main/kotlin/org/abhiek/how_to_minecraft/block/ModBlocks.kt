package org.abhiek.how_to_minecraft.block

import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks =
        DeferredRegister.createBlocks(HowToMinecraft.ID)
    // Alternatively:
//    val BLOCKS: DeferredRegister<Block?>? = DeferredRegister.create(
//        // The registry we want to use.
//        // Minecraft's registries can be found in BuiltInRegistries, NeoForge's registries can be found in NeoForgeRegistries.
//        // Mods may also add their own registries, refer to the individual mod's documentation or source code for where to find them.
//        BuiltInRegistries.BLOCK,
//        // Our mod id.
//        HowToMinecraft.ID
//    )

    // If you get an "overload resolution ambiguity" error, include the arrow at the start of the closure.
//    val EXAMPLE_BLOCK by REGISTRY.register("example_block") { ->
//        Block(
//            BlockBehaviour.Properties.of()
//                .lightLevel { 15 }
//                .strength(3.0f)
//        )
//    }
    val EXAMPLE_BLOCK by BLOCKS.registerSimpleBlock(
        "example_block",
        BlockBehaviour.Properties.of()
            .lightLevel { 15 }
            .strength(3.0f)
    )
}
