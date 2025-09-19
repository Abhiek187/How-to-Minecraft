package org.abhiek.how_to_minecraft

import net.minecraft.client.Minecraft
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.abhiek.how_to_minecraft.block.ModBlocks
import org.abhiek.how_to_minecraft.enchantment.Increment
import org.abhiek.how_to_minecraft.entity.ModEntities
import org.abhiek.how_to_minecraft.item.ModItems
import org.abhiek.how_to_minecraft.particle.MyParticleTypes
import org.abhiek.how_to_minecraft.sound.MySoundsObject
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

/**
 * Main mod class.
 *
 * An example for blocks is in the `blocks` package of this mod.
 *
 * Runs on both the physical client and server.
 */
@Mod(HowToMinecraft.ID)
object HowToMinecraft {
    const val ID = "how_to_minecraft"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.info("Hello world!")

        // Register the KDeferredRegister to the mod-specific event bus
        // EVENT_BUS = game bus
        ModBlocks.BLOCKS.register(MOD_BUS)
        ModBlocks.BLOCK_ENTITY_TYPES.register(MOD_BUS)
        ModItems.ITEMS.register(MOD_BUS)
        ModEntities.ENTITY_TYPES.register(MOD_BUS)
        MyParticleTypes.PARTICLE_TYPES.register(MOD_BUS)
        MySoundsObject.SOUND_EVENTS.register(MOD_BUS)
        Increment.ENCHANTMENT_COMPONENT_TYPES.register(MOD_BUS)

        runForDist(clientTarget = {
            MOD_BUS.addListener(::onClientSetup)
            Minecraft.getInstance()
        }, serverTarget = {
            MOD_BUS.addListener(::onServerSetup)
            "test"
        })
        MOD_BUS.addListener(::onCommonSetup) // default event priority

        // Every block in Minecraft
//        for (entry in BuiltInRegistries.BLOCK.entrySet()) {
//            println("Block key: ${entry.key}, value: ${entry.value}")
//        }

        /**
         * Physical client: Minecraft frontend
         * Physical server: JAR file (dedicated server)
         * Logical client: game display
         * Logical server: game logic
         *
         * Multiplayer: all players (clients) connect to a server
         * Single player: the physical client spins up a logical client & server (no dedicated server)
         *
         * Level#isClientSide(): true if running on logical client
         */
        val isPhysicalClient = DIST.isClient // or FMLEnvironment.dist == Dist.CLIENT
        val isPhysicalServer = DIST.isDedicatedServer // or FMLEnvironment.dist == Dist.DEDICATED_SERVER
//        val logicalClient = LogicalSide.CLIENT
//        val logicalServer = LogicalSide.SERVER
        println("is physical client? $isPhysicalClient")
        println("is physical server? $isPhysicalServer")
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        val container = event.container
        val modId = container.modId
        val modInfo = container.modInfo
        val eventBus = container.eventBus
        val namespace = container.namespace
        LOGGER.info("Initializing client..., modId: $modId, modInfo: $modInfo, eventBus: $eventBus, namespace: $namespace")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        val container = event.container
        val modId = container.modId
        val modInfo = container.modInfo
        val eventBus = container.eventBus
        val namespace = container.namespace
        LOGGER.info("Server starting..., modId: $modId, modInfo: $modInfo, eventBus: $eventBus, namespace: $namespace")
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        val container = event.container
        val modId = container.modId
        val modInfo = container.modInfo
        val eventBus = container.eventBus
        val namespace = container.namespace
        LOGGER.info("Hello! This is working! modId: $modId, modInfo: $modInfo, eventBus: $eventBus, namespace: $namespace")
    }
}
