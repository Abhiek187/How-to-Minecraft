package org.abhiek.how_to_minecraft.loot_table

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer
import net.minecraft.world.level.storage.loot.functions.LootItemFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Consumer

// We extend LootPoolSingletonContainer since we have a "finite" set of drops.
// Some of this code is adapted from NestedLootTable.
class EntityLootEntry private constructor(
    // A Holder for the entity type we want to roll the other table for
    private val entity: Holder<EntityType<*>>,
    weight: Int,
    quality: Int,
    conditions: MutableList<LootItemCondition>,
    functions: MutableList<LootItemFunction>
): LootPoolSingletonContainer(weight, quality, conditions, functions) {
    // This is where the magic happens. To add an item stack, we generally call #accept on the consumer.
    // However, in this case, we let #getRandomItems do that for us.
    public override fun createItemStack(consumer: Consumer<ItemStack>, context: LootContext) {
        // Get the entity's loot table.
        // If it doesn't exist, an empty loot table will be returned, so null-checking is not necessary.
        val table = context.level.server.reloadableRegistries().getLootTable(
            entity.value().defaultLootTable.get()
        )
        // Use the raw version here, because vanilla does it too. :P
        // #getRandomItemsRaw calls consumer#accept for us on the results of the roll.
        table.getRandomItems(context, consumer)
    }

    override fun getType(): LootPoolEntryType {
        return ENTITY_LOOT
    }

    companion object {
        // This is placed as a constant in EntityLootEntry
        val CODEC: MapCodec<EntityLootEntry> =
            RecordCodecBuilder.mapCodec { inst ->
                // Add our own fields
                inst.group(
                    // A value referencing an entity type id
                    BuiltInRegistries.ENTITY_TYPE.holderByNameCodec().fieldOf("entity")
                        .forGetter { e -> e.entity }
                )
                    // Add common fields: weight, display, conditions, and functions
                    .and(
                        singletonFields<EntityLootEntry?>(
                            inst
                        )
                    )
                    .apply(inst, ::EntityLootEntry)
            }

        val LOOT_POOL_ENTRY_TYPES: DeferredRegister<LootPoolEntryType> =
            DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, HowToMinecraft.ID)

        val ENTITY_LOOT: LootPoolEntryType by LOOT_POOL_ENTRY_TYPES.register("entity_loot") { ->
            LootPoolEntryType(CODEC)
        }

        // Static builder method, accepting our custom parameters and combining them with a lambda
        // that supplies the values common to all entry types.
        fun entityLoot(entity: Holder<EntityType<*>>): Builder<*> {
            // Use the static simpleBuilder() method defined in LootPoolSingletonContainer
            return simpleBuilder { weight, quality, conditions, functions ->
                EntityLootEntry(
                    entity,
                    weight,
                    quality,
                    conditions,
                    functions
                )
            }
        }
    }
}
