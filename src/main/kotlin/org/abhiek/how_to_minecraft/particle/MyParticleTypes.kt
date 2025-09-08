package org.abhiek.how_to_minecraft.particle

import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object MyParticleTypes {
    val PARTICLE_TYPES: DeferredRegister<ParticleType<*>> =
        DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, HowToMinecraft.ID)

    // The easiest way to add new particle types is reusing vanilla's SimpleParticleType.
    // Implementing a custom ParticleType is also possible, see below.
    // /particle how_to_minecraft:my_particle
    val MY_PARTICLE: SimpleParticleType by
        PARTICLE_TYPES.register(
            // The name of the particle type.
            "my_particle"
        ) { ->
            // The supplier. The boolean parameter denotes whether setting the Particles option in the
            // video settings to Minimal will affect this particle type or not; this is false for
            // most vanilla particles, but true for e.g. explosions, campfire smoke, or squid ink.
            SimpleParticleType(false)
        }
}
