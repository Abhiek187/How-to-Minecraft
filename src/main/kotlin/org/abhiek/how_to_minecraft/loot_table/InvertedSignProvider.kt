package org.abhiek.how_to_minecraft.loot_table

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.util.context.ContextKey
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

// We accept another number provider as our base
data class InvertedSignProvider(val base: NumberProvider): NumberProvider {
    // Return a float value. Use the context and the record parameters as needed.
    override fun getFloat(context: LootContext): Float {
        return -this.base.getFloat(context)
    }

    // Return an int value. Use the context and the record parameters as needed.
    // Overriding this is optional, the default implementation will round the result of #getFloat.
    override fun getInt(context: LootContext): Int {
        return -this.base.getInt(context)
    }

    // Return a set of the loot context params used by this provider. See below for more information.
    // Since we have a base value, we just defer to the base.
    override fun getReferencedContextParams(): MutableSet<ContextKey<*>> {
        return this.base.referencedContextParams
    }

    override fun getType(): LootNumberProviderType {
        return INVERTED_SIGN
    }

    companion object {
        val CODEC: MapCodec<InvertedSignProvider> = RecordCodecBuilder.mapCodec { inst ->
                inst.group(
                    NumberProviders.CODEC.fieldOf("base").forGetter(InvertedSignProvider::base)
                ).apply(inst, ::InvertedSignProvider)
            }

        val LOOT_NUMBER_PROVIDER_TYPES: DeferredRegister<LootNumberProviderType> = DeferredRegister.create(
            Registries.LOOT_NUMBER_PROVIDER_TYPE,
            HowToMinecraft.ID
        )

        val INVERTED_SIGN: LootNumberProviderType by LOOT_NUMBER_PROVIDER_TYPES.register("inverted_sign") { ->
            LootNumberProviderType(CODEC)
        }
    }
}
