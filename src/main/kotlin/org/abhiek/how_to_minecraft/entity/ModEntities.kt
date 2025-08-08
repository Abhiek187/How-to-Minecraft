package org.abhiek.how_to_minecraft.entity

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModEntities {
    val ENTITY_TYPES: DeferredRegister.Entities = DeferredRegister.createEntities(HowToMinecraft.ID)

    val MY_ENTITY: EntityType<MyEntity> by ENTITY_TYPES.register("my_entity") { ->
        EntityType.Builder.of(
            ::MyEntity,
            // MISC = non-living entity, no spawn limit, but can't spawn naturally
            // isFriendly = false for MONSTER
            // isPersistent = true for CREATURE & MISC
            // despawnDistance = 64 for WATER_AMBIENT, 128 for all others
            MobCategory.MISC
        )
            // Default: 0.6f x 1.8f
            .sized(1.0f, 1.0f)
            // A multiplicative factor (scalar) used by mobs that spawn in varying sizes.
            // In vanilla, these are only slimes and magma cubes, both of which use 4.0f.
            .spawnDimensionsScale(4.0f)
            // The eye height, in blocks from the bottom of the size. Defaults to height * 0.85.
            // This must be called after #sized to have an effect.
            .eyeHeight(0.5f)
            // Disables the entity being summonable via /summon how_to_minecraft:my_entity
            // Can spawn using LevelWriter#addFreshEntity or EntityType#spawn
            .noSummon()
            // Prevents the entity from being saved to disk.
            .noSave()
            // Makes the entity fire immune.
            .fireImmune()
            // Makes the entity immune to damage from a certain block. Vanilla uses this to make
            // foxes immune to sweet berry bushes, withers and wither skeletons immune to wither roses,
            // and polar bears, snow golems and strays immune to powder snow.
            .immuneTo(Blocks.POWDER_SNOW)
            // Disables a rule in the spawn handler that limits the distance at which entities can spawn.
            // This means that no matter the distance to the player, this entity can spawn.
            // Vanilla enables this for pillagers and shulkers.
            .canSpawnFarFromPlayer()
            // The range in which the entity is kept loaded by the client, in chunks.
            // Vanilla values for this vary, but it's often something around 8 or 10. Defaults to 5.
            // Be aware that if this is greater than the client's chunk view distance,
            // then that chunk view distance is effectively used here instead.
            .clientTrackingRange(8)
            // How often update packets are sent for this entity, in once every x ticks. This is set to higher values
            // for entities that have predictable movement patterns, for example projectiles. Defaults to 3.
            .updateInterval(10)
            .build(
                ResourceKey.create(
                    Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "my_entity")
                )
            )
    }

    // Alternatively:
//    val MY_ENTITY: EntityType<MyEntity> by ENTITY_TYPES.registerEntityType(
//        "my_entity",
//        ::MyEntity,
//        MobCategory.MISC
//    )
//
//    val MY_ENTITY: EntityType<MyEntity> by ENTITY_TYPES.registerEntityType(
//        "my_entity", ::MyEntity, MobCategory.MISC
//    ) { builder ->
//        builder
//            .sized(2.0f, 2.0f)
//            .eyeHeight(1.5f)
//            .updateInterval(5)
//    }
}
