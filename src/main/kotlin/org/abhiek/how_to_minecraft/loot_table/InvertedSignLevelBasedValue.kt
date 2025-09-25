package org.abhiek.how_to_minecraft.loot_table

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.enchantment.LevelBasedValue
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

data class InvertedSignLevelBasedValue(val base: LevelBasedValue): LevelBasedValue {
    // Perform our operation
    override fun calculate(level: Int): Float {
        return -this.base.calculate(level)
    }

    // Unlike NumberProviders, we don't return the registered type, instead we return the codec directly.
    override fun codec(): MapCodec<out InvertedSignLevelBasedValue> {
        return CODEC
    }

    companion object {
        val CODEC: MapCodec<InvertedSignLevelBasedValue> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                LevelBasedValue.CODEC.fieldOf("base").forGetter(InvertedSignLevelBasedValue::base)
            ).apply(inst, ::InvertedSignLevelBasedValue)
        }

        val LEVEL_BASED_VALUES: DeferredRegister<MapCodec<out LevelBasedValue>> =
            DeferredRegister.create(
                Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE,
                HowToMinecraft.ID
            )

        val INVERTED_SIGN: MapCodec<InvertedSignLevelBasedValue> by
            LEVEL_BASED_VALUES.register("inverted_sign") { -> CODEC }
    }
}
