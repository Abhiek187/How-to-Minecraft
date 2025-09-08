package org.abhiek.how_to_minecraft.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle

// First four parameters are self-explanatory. The SpriteSet parameter is provided by the
// ParticleProvider, see below. You may also add additional parameters as needed, e.g. xSpeed/ySpeed/zSpeed.
class MyParticle(level: ClientLevel, x: Double, y: Double, z: Double, private val spriteSet: SpriteSet):
    TextureSheetParticle(level, x, y, z) {
    init {
        // Our particle floats in midair now, because why not?
        this.gravity = 0f

        // We set the initial sprite here since ticking is not guaranteed to set the sprite
        // before the render method is called.
        this.setSpriteFromAge(spriteSet)
    }

    override fun tick() {
        // Set the sprite for the current particle age, i.e. advance the animation.
        this.setSpriteFromAge(spriteSet)
        // Let super handle further movement. You may replace this with your own movement if needed.
        // You may also override move() if you only want to modify the built-in movement.
        super.tick()
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }
}
