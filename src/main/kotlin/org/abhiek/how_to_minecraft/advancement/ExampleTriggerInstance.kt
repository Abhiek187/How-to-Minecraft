package org.abhiek.how_to_minecraft.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.*

data class ExampleTriggerInstance(
    private val mPlayer: Optional<ContextAwarePredicate>,
    val predicate: ItemPredicate
): SimpleInstance {
    companion object {
        val TRIGGER_TYPES: DeferredRegister<CriterionTrigger<*>> =
            DeferredRegister.create(Registries.TRIGGER_TYPE, HowToMinecraft.ID)

        val EXAMPLE_TRIGGER: ExampleCriterionTrigger by
            TRIGGER_TYPES.register("example") { ->
                ExampleCriterionTrigger()
            }

        val CODEC: Codec<ExampleTriggerInstance> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                        .forGetter(ExampleTriggerInstance::player),
                    ItemPredicate.CODEC.fieldOf("item")
                        .forGetter(ExampleTriggerInstance::predicate)
                ).apply(instance, ::ExampleTriggerInstance)
            }

        fun instance(player: ContextAwarePredicate, item: ItemPredicate): Criterion<ExampleTriggerInstance?> {
            return EXAMPLE_TRIGGER.createCriterion(
                ExampleTriggerInstance(Optional.of(player), item)
            )
        }
    }

    override fun player(): Optional<ContextAwarePredicate> {
        return this.mPlayer
    }

    // This method is unique for each instance and is as such not overridden.
    // The parameter may be whatever you need to properly match, for example, this could also be a LivingEntity.
    // If you need no context other than the player, this may also take no parameters at all.
    fun matches(stack: ItemStack): Boolean {
        // Since ItemPredicate matches a stack, we use a stack as the input here.
        return this.predicate.test(stack)
    }
}
