package org.abhiek.how_to_minecraft.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.neoforged.neoforge.common.crafting.ICustomIngredient
import net.neoforged.neoforge.common.crafting.IngredientType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.stream.Stream

class MinEnchantedIngredient(
    private val tag: TagKey<Item>,
    private val enchantments: MutableMap<Holder<Enchantment>, Int>
): ICustomIngredient {
    // Check if the passed ItemStack matches our ingredient by verifying the item is in the tag
    // and by testing for presence of all required enchantments with at least the required level.
    override fun test(stack: ItemStack): Boolean {
        return stack.`is`(tag) && enchantments.keys
            .stream()
            .allMatch { enchantment ->
                EnchantmentHelper.getEnchantmentsForCrafting(stack).getLevel(enchantment) >=
                        (enchantments[enchantment] ?: Int.MAX_VALUE)
            }
    }

    // Determines whether this ingredient performs NBT or data component matching (false) or not (true).
    // Also determines whether a stream codec is used for syncing, more on this later.
    // We query enchantments on the stack, therefore our ingredient is not simple.
    override fun isSimple(): Boolean {
        return false
    }

    override fun getType(): IngredientType<*> {
        return MIN_ENCHANTED
    }

    // Returns a stream of items that match this ingredient. Mostly for display purposes.
    // There's a few good practices to follow here:
    // - Always include at least one item, to prevent accidental recognition as empty.
    // - Include each accepted Item at least once.
    // - If #isSimple is true, this should be exact and contain every item that matches.
    //   If not, this should be as exact as possible, but doesn't need to be super accurate.
    // In our case, we use all items in the tag.
    override fun items(): Stream<Holder<Item>> {
        return BuiltInRegistries.ITEM.getOrThrow(tag).stream()
    }

    companion object {
        // The codec for serializing the ingredient
        val CODEC: MapCodec<MinEnchantedIngredient> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                TagKey.codec(Registries.ITEM).fieldOf("tag")
                    .forGetter { e -> e.tag },
                Codec.unboundedMap(Enchantment.CODEC, Codec.INT)
                    .optionalFieldOf("enchantments", mapOf())
                    .forGetter { e -> e.enchantments }
            ).apply(inst, ::MinEnchantedIngredient)
        }

        // Create a stream codec from the regular codec. In some cases, it might make sense to define
        // a new stream codec from scratch.
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MinEnchantedIngredient> =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec())

        val INGREDIENT_TYPES: DeferredRegister<IngredientType<*>> = DeferredRegister.create(
            NeoForgeRegistries.Keys.INGREDIENT_TYPES,
            HowToMinecraft.ID
        )

        val MIN_ENCHANTED: IngredientType<MinEnchantedIngredient> by INGREDIENT_TYPES.register("min_enchanted") { ->
            // The stream codec parameter is optional, a stream codec will be created from the codec
            // using ByteBufCodecs#fromCodec or #fromCodecWithRegistries if the stream codec isn't specified.
            IngredientType(CODEC, STREAM_CODEC)
        }
    }
}
