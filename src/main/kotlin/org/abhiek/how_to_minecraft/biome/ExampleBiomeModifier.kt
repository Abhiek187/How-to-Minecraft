package org.abhiek.how_to_minecraft.biome

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.world.level.biome.Biome
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

data class ExampleBiomeModifier(val biomes: HolderSet<Biome>, val value: Int): BiomeModifier {
    companion object {
        private val BIOME_MODIFIERS: DeferredRegister<MapCodec<out BiomeModifier>> =
            DeferredRegister.create(
                NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                HowToMinecraft.ID
            )

        val EXAMPLE_BIOME_MODIFIER: MapCodec<ExampleBiomeModifier> by
            BIOME_MODIFIERS.register("example_biome_modifier") { ->
                RecordCodecBuilder.mapCodec { instance ->
                    instance.group(
                        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ExampleBiomeModifier::biomes),
                        Codec.INT.fieldOf("value").forGetter(ExampleBiomeModifier::value)
                    ).apply(instance, ::ExampleBiomeModifier)
                }
            }
    }

    override fun modify(
        biome: Holder<Biome>,
        phase: BiomeModifier.Phase,
        builder: ModifiableBiomeInfo.BiomeInfo.Builder
    ) {
        // Pick the phase that best matches what your want to modify
        println("Biome modifier: ${biome.value()}")
        when (phase) {
            // Modify the 'builder', checking any information about the biome itself
            BiomeModifier.Phase.BEFORE_EVERYTHING -> println("Phase: BEFORE_EVERYTHING")
            BiomeModifier.Phase.ADD -> println("Phase: ADD")
            BiomeModifier.Phase.REMOVE -> println("Phase: REMOVE")
            BiomeModifier.Phase.MODIFY -> println("Phase: MODIFY")
            BiomeModifier.Phase.AFTER_EVERYTHING -> println("Phase: AFTER_EVERYTHING")
        }
    }

    override fun codec(): MapCodec<out BiomeModifier> {
        return EXAMPLE_BIOME_MODIFIER
    }
}
