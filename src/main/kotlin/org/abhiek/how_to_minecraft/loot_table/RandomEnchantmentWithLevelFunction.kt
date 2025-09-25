package org.abhiek.how_to_minecraft.loot_table

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.Util
import net.minecraft.core.HolderSet
import net.minecraft.core.RegistryCodecs
import net.minecraft.core.registries.Registries
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.Optional
import java.util.function.Function

// Code adapted from vanilla's EnchantRandomlyFunction class.
// LootItemConditionalFunction is an abstract class, not an interface, so we cannot use a record here.
class RandomEnchantmentWithLevelFunction(
    // Our context: an optional list of enchantments, and a level
    conditions: List<LootItemCondition>,
    val enchantments: Optional<HolderSet<Enchantment>>,
    val level: Int,
): LootItemConditionalFunction(conditions) {
    // Run our enchantment application logic. Most of this is copied from EnchantRandomlyFunction#run.
    override fun run(stack: ItemStack, context: LootContext): ItemStack {
        var stack = stack
        val random = context.random
        val stream = this.enchantments
            .map(HolderSet<Enchantment>::stream)
            .orElseGet {
                context.level.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).listElements()
                    .map(Function.identity())
            }
            .filter { e ->
                @Suppress("DEPRECATION")
                e.value().canEnchant(stack)
            }
            .toList()
        val optional = Util.getRandomSafe(stream, random)
        if (optional.isEmpty) {
            HowToMinecraft.LOGGER.warn("Couldn't find a compatible enchantment for {}", stack)
        } else {
            val enchantment = optional.get()
            if (stack.`is`(Items.BOOK)) {
                stack = ItemStack(Items.ENCHANTED_BOOK)
            }
            stack.enchant(
                enchantment,
                Mth.nextInt(random, enchantment.value().minLevel, enchantment.value().maxLevel)
            )
        }
        return stack
    }

    override fun getType(): LootItemFunctionType<out LootItemConditionalFunction?> {
        return RANDOM_ENCHANTMENT_WITH_LEVEL
    }

    companion object {
        // Our codec
        // #commonFields adds the conditions field
        val CODEC: MapCodec<RandomEnchantmentWithLevelFunction> = RecordCodecBuilder.mapCodec { inst ->
            commonFields(inst).and(
                inst.group(
                    RegistryCodecs.homogeneousList(Registries.ENCHANTMENT)
                        .optionalFieldOf("enchantments")
                        .forGetter { e -> e.enchantments },
                    Codec.INT.fieldOf("level").forGetter { e -> e.level }
                )
            ).apply(inst, ::RandomEnchantmentWithLevelFunction)
        }

        val LOOT_FUNCTION_TYPES: DeferredRegister<LootItemFunctionType<*>> = DeferredRegister.create(
            Registries.LOOT_FUNCTION_TYPE,
            HowToMinecraft.ID
        )

        val RANDOM_ENCHANTMENT_WITH_LEVEL: LootItemFunctionType<RandomEnchantmentWithLevelFunction> by
            LOOT_FUNCTION_TYPES.register("random_enchantment_with_level") { ->
                LootItemFunctionType(CODEC)
            }
    }
}
