package org.abhiek.how_to_minecraft.advancement

import com.mojang.serialization.Codec
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

class ExampleCriterionTrigger : SimpleCriterionTrigger<ExampleTriggerInstance>() {
    // This method is unique for each trigger and is as such not a method to override
    fun trigger(player: ServerPlayer, stack: ItemStack) {
        this.trigger(player) { triggerInstance ->
            // The condition checker method within the SimpleCriterionTrigger.SimpleInstance subclass
            triggerInstance.matches(stack)
        }
    }

    override fun codec(): Codec<ExampleTriggerInstance> {
        return ExampleTriggerInstance.CODEC
    }
}
