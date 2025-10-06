package org.abhiek.how_to_minecraft

import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.level.LightLayer
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.neoforged.fml.loading.FMLLoader
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import kotlin.math.max

/**
 * Log similar information shown in the debug (F3) screen
 */
fun debugMenu(logger: Logger, player: Player) {
    logger.debug("===F3 Menu===")
    val level = player.level()
    val blockPosition = player.blockPosition()

    // Left side
    val minecraftVersion = FMLLoader.versionInfo().mcVersion
    val isPhysicalClient = DIST.isClient
    val dimension = level.dimension().location()

    val currentPosition = player.position()
    val compassDirection = player.direction.serializedName
    val coordinateDirection = player.direction.axisDirection
    val coordinateAxis = player.direction.axis.name
    val yaw = round(player.rotationVector.x, places = 1)
    val pitch = round(player.rotationVector.y, places = 1)
    val skyLight = level.getBrightness(LightLayer.SKY, blockPosition)
    val blockLight = level.getBrightness(LightLayer.BLOCK, blockPosition)
    val biome = level.getBiome(blockPosition).key?.location()

    logger.debug("Minecraft {}", minecraftVersion)
    logger.debug("{} server", if (isPhysicalClient) "Integrated" else "\"vanilla\"")
    logger.debug(dimension)

    logger.debug("XYZ: {} / {} / {}",
        round(currentPosition.x, places = 5),
        round(currentPosition.y, places = 5),
        round(currentPosition.z, places = 5)
    )
    logger.debug("Facing {} ({} {}) ({} / {})",
        compassDirection, coordinateDirection, coordinateAxis, yaw, pitch)
    logger.debug("Client Light: {} ({} sky, {} block)",
        max(skyLight, blockLight), skyLight, blockLight)
    logger.debug("Biome: {}", biome)

    // Right side
    val javaVersion = System.getProperty("java.version")
    val hitResult = if (isPhysicalClient) Minecraft.getInstance().hitResult else null

    logger.debug("Java: {}", javaVersion)

    if (hitResult?.type == HitResult.Type.BLOCK) {
        val blockHitResult = hitResult as BlockHitResult
        val blockHitPosition = blockHitResult.blockPos
        val blockName = BuiltInRegistries.BLOCK.getKey(
            level.getBlockState(blockHitPosition).block
        )

        logger.debug("Targeted Block: {}, {}, {}",
            blockHitPosition.x, blockHitPosition.y, blockHitPosition.z)
        logger.debug(blockName)
    } else if (hitResult?.type == HitResult.Type.ENTITY) {
        val entityHitResult = hitResult as EntityHitResult
        val entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entityHitResult.entity.type)

        logger.debug("Targeted Entity")
        logger.debug(entityName)
    }

    // Bonus
    val enabledFeatures = level.enabledFeatures()
    val requiredFeatures = FeatureFlagSet.of(HowToMinecraft.EXPERIMENTAL)
    logger.debug("Experimental feature flag enabled? {}", requiredFeatures.isSubsetOf(enabledFeatures))

    logger.debug("===End F3 Menu===")
}

private fun round(num: Number, places: Int): String = String.format("%.${places}f", num)
