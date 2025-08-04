package org.abhiek.how_to_minecraft

import net.minecraft.client.Minecraft
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent
import org.abhiek.how_to_minecraft.block.ModBlocks
import org.abhiek.how_to_minecraft.item.ModItems

// Subscribe to all events at once: MOD_BUS.register(EventHandler)
@EventBusSubscriber(modid = HowToMinecraft.ID)
object EventHandler {
    /*
    Events:
    - https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.21.x-neoforge/net/neoforged/neoforge/event/package-summary.html
    - Client side: https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.21.x-neoforge/net/neoforged/neoforge/client/event/package-summary.html
     */
    @SubscribeEvent
    private fun onLoad(event: EntityJoinLevelEvent) {
        debugMenu(HowToMinecraft.LOGGER, event.entity)
    }

    // Heals an entity by half a heart every time they jump.
    @SubscribeEvent(priority = EventPriority.HIGH)
    private fun onLivingJump(event: LivingJumpEvent) {
        val entity = event.entity
        println("event entity: $entity")
        println("is logical client? ${entity.level().isClientSide}")

        // Only heal on the server side
        if (!entity.level().isClientSide) {
            entity.heal(1f)
            println("Entity block position: ${entity.blockPosition()}")
            println("Example block state: ${
                entity.level().getBlockState(entity.blockPosition())
            }")
            // .get() is required since EXAMPLE_BLOCK is deferred
            println("Example block default state: ${ModBlocks.EXAMPLE_BLOCK.get().defaultBlockState()}")
        }

        debugMenu(HowToMinecraft.LOGGER, entity)
    }

    @SubscribeEvent
    fun buildContents(event: BuildCreativeModeTabContentsEvent) {
        // Add items to the creative menu
        if (event.tabKey == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.EXAMPLE_ITEM)
            // Accepts an ItemLike. This assumes that MY_BLOCK has a corresponding item.
            event.accept(ModBlocks.EXAMPLE_BLOCK)
        }
    }

    @SubscribeEvent
    fun onClick(event: InputEvent.InteractionKeyMappingTriggered) {
        println("Click event!")
        val minecraft = Minecraft.getInstance()

        println("Hit result Type: ${minecraft.hitResult?.type}, Location: ${minecraft.hitResult?.location}")
        println("Hand: ${event.hand}")
        println("Is left click? ${event.isAttack}")
        println("Is middle click? ${event.isPickBlock}")
        println("Is right click? ${event.isUseItem}")
        println("Is canceled? ${event.isCanceled}")
        println("Key mapping Key: ${event.keyMapping.key}, Name: ${event.keyMapping.name}, Category: ${
            event.keyMapping.category
        }")
    }
}
