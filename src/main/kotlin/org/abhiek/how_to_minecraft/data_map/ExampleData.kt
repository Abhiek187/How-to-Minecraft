package org.abhiek.how_to_minecraft.data_map

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.datamaps.DataMapType
import org.abhiek.how_to_minecraft.HowToMinecraft

data class ExampleData(
    val amount: Float,
    val chance: Float
) {
    companion object {
        val CODEC: Codec<ExampleData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("amount").forGetter(ExampleData::amount),
                Codec.floatRange(0f, 1f).fieldOf("chance").forGetter(ExampleData::chance)
            ).apply(instance, ::ExampleData)
        }

        // In this example, we register the data map for the minecraft:item registry, hence we use Item as the generic.
        // Adjust the types accordingly if you want to create a data map for a different registry.
        val EXAMPLE_DATA: DataMapType<Item, ExampleData> = DataMapType.builder(
            // The ID of the data map. Data map files for this data map will be located at
            // how_to_minecraft/data_maps/item/example_data.json.
            ResourceLocation.fromNamespaceAndPath(
                HowToMinecraft.ID,
                "example_data"
            ),
            // The registry to register the data map for.
            Registries.ITEM,
            // The codec of the data map entries.
            CODEC
        ).synced(
            // The codec used for syncing. May be identical to the normal codec, but may also be
            // a codec with fewer fields, omitting parts of the object that are not required on the client.
            CODEC,
            // Whether the data map is mandatory or not. Marking a data map as mandatory will disconnect clients
            // that are missing the data map on their side; this includes vanilla clients.
            false
        ).build()
    }
}
