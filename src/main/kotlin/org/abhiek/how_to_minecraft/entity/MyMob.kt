package org.abhiek.how_to_minecraft.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.damagesource.DamageContainer

class MyMob(type: EntityType<out MyMob>, level: Level): Mob(type, level) {
    override fun getMainArm(): HumanoidArm {
        // Get the attribute map of our entity
        val attributes = this.attributes
        // Get the max health of our entity
        // val maxHealth = attributes.getValue(Attributes.MAX_HEALTH)
        // Alternatively:
        val maxHealth = this.maxHealth
        // Setting the max health must either be done by getting the AttributeInstance and calling #setBaseValue, or by
        // adding an attribute modifier.
        // attributes.getInstance(Attributes.MAX_HEALTH)?.baseValue = 50.0
        println("MyLivingEntity attributes: $attributes")
        println("MyLivingEntity max health: $maxHealth")

        return HumanoidArm.LEFT // lefty
    }

    override fun onDamageTaken(damageContainer: DamageContainer) {
        // Perform post-attack behavior, only called if damage > 0
        super.onDamageTaken(damageContainer)

        println("Original damage: ${damageContainer.originalDamage}")
        println("New damage: ${damageContainer.newDamage}")
        println("Damage source: ${damageContainer.source.entity?.type}")
    }
}
