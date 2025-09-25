package org.abhiek.how_to_minecraft.loot_table

import com.google.common.collect.ImmutableSet
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.util.context.ContextKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

data class HasXpLevelCondition(val level: Int): LootItemCondition {
    // Evaluates the condition here. Get the required loot context parameters from the provided LootContext.
    // In our case, we want the KILLER_ENTITY to have at least our required level.
    override fun test(context: LootContext): Boolean {
        val entity: Entity? = context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY)
        return entity is Player && entity.experienceLevel >= level
    }

    // Tell the game what parameters we expect from the loot context. Used in validation.
    override fun getReferencedContextParams(): MutableSet<ContextKey<*>> {
        return ImmutableSet.of(LootContextParams.ATTACKING_ENTITY)
    }

    override fun getType(): LootItemConditionType {
        return MIN_XP_LEVEL
    }

    companion object {
        // Add the context we need for this condition. In our case, this will be the xp level the player must have.
        val CODEC: MapCodec<HasXpLevelCondition> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                Codec.INT.fieldOf("level").forGetter(HasXpLevelCondition::level)
            ).apply(inst, ::HasXpLevelCondition)
        }

        val LOOT_CONDITION_TYPES: DeferredRegister<LootItemConditionType> = DeferredRegister.create(
            Registries.LOOT_CONDITION_TYPE,
            HowToMinecraft.ID
        )

        val MIN_XP_LEVEL: LootItemConditionType by LOOT_CONDITION_TYPES.register("min_xp_level") { ->
            LootItemConditionType(CODEC)
        }
    }
}
