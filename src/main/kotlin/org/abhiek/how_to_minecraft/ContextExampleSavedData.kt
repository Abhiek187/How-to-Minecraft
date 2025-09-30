package org.abhiek.how_to_minecraft

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType

// For some saved data implementation
class ContextExampleSavedData(
    private val ctx: Context,
    private val val1: Int? = null,
    private val val2: Block? = null
): SavedData() {
    fun foo() {
        // Change data in saved data
        // Call set dirty if data changes
        this.setDirty()
    }

    companion object {
        val ID = SavedDataType(
            // The identifier of the saved data
            // Used as the path within the level's `data` folder
            "example",
            // The initial constructor
            ::ContextExampleSavedData
        ) { ctx ->
            // The codec used to serialize the data
            RecordCodecBuilder.create { instance ->
                instance.group(
                    RecordCodecBuilder.point(ctx),
                    Codec.INT.fieldOf("val1").forGetter { sd -> sd.val1 },
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("val2")
                        .forGetter { sd -> sd.val2 }
                ).apply(instance, ::ContextExampleSavedData)
            }
        }
    }
}
