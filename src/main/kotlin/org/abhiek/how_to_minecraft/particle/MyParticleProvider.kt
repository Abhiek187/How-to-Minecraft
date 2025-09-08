package org.abhiek.how_to_minecraft.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType

// The generic type of ParticleProvider must match the type of the particle type this provider is for.
// The registration function passes a SpriteSet, so we accept that and store it for further use.
class MyParticleProvider(private val spriteSet: SpriteSet): ParticleProvider<SimpleParticleType> {
    // This is where the magic happens. We return a new particle each time this method is called!
    // The type of the first parameter matches the generic type passed to the super interface.
    override fun createParticle(
        type: SimpleParticleType, level: ClientLevel,
        x: Double, y: Double, z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double
    ): Particle {
        // We don't use the type and speed, and pass in everything else. You may of course use them if needed.
        return MyParticle(level, x, y, z, spriteSet)
    }
}
