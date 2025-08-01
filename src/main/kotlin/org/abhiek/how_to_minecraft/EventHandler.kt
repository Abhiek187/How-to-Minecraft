package org.abhiek.how_to_minecraft

import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent

// Subscribe to all events at once: MOD_BUS.register(EventHandler)
@EventBusSubscriber(modid = HowToMinecraft.ID)
object EventHandler {
    // Heals an entity by half a heart every time they jump.
    @SubscribeEvent(priority = EventPriority.HIGH)
    private fun onLivingJump(event: LivingJumpEvent) {
        val entity = event.entity
        println("event entity: $entity")
        println("is logical client? ${entity.level().isClientSide}")

        // Only heal on the server side
        if (!entity.level().isClientSide) {
            entity.heal(1f)
        }
    }
}
