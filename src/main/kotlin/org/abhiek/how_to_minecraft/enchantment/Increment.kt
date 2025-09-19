package org.abhiek.how_to_minecraft.enchantment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

// Define an example data-bearing record
data class Increment(val value: Int) {
    fun add(x: Int): Int {
        return this.value + x
    }

    companion object {
        val CODEC: Codec<Increment> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("value").forGetter(Increment::value)
            ).apply(instance) { value ->
                Increment(value)
            }
        }

        val ENCHANTMENT_COMPONENT_TYPES: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(
            Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
            HowToMinecraft.ID
        )

        val INCREMENT: DataComponentType<Increment> by ENCHANTMENT_COMPONENT_TYPES.registerComponentType(
            "increment"
        ) { builder ->
            builder.persistent(CODEC)
        }
    }
}
