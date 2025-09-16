package org.abhiek.how_to_minecraft

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.neoforged.neoforge.common.conditions.ICondition
import net.neoforged.neoforge.common.conditions.ICondition.IContext
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

data class XorCondition(val first: ICondition, val second: ICondition): ICondition {
    override fun test(context: IContext): Boolean {
        return this.first.test(context) xor this.second.test(context)
    }

    override fun codec(): MapCodec<out ICondition> {
        return CODEC
    }

    companion object {
        val CODEC: MapCodec<XorCondition> =
            RecordCodecBuilder.mapCodec { inst ->
                inst.group(
                    ICondition.CODEC.fieldOf("first").forGetter(XorCondition::first),
                    ICondition.CODEC.fieldOf("second").forGetter(XorCondition::second)
                ).apply(inst) { first, second ->
                    XorCondition(first, second)
                }
            }

        val CONDITION_CODECS: DeferredRegister<MapCodec<out ICondition>> =
            DeferredRegister.create(
                NeoForgeRegistries.Keys.CONDITION_CODECS,
                HowToMinecraft.ID
            )

        val XOR: MapCodec<XorCondition> by CONDITION_CODECS.register("xor") { ->
            CODEC
        }
    }
}
