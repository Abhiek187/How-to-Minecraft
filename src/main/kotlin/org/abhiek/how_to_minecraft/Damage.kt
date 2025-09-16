package org.abhiek.how_to_minecraft

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity

object Damage {
    val EXAMPLE_DAMAGE: ResourceKey<DamageType> = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example")
    )

    fun exampleDamage(causer: Entity): DamageSource {
        return DamageSource(
            causer.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(EXAMPLE_DAMAGE),
            causer
        )

        // The second parameter is the amount of damage, in half hearts
        // entity.hurt(exampleDamage(player), 10)
    }
}
