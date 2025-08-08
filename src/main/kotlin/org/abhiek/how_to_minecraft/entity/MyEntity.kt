package org.abhiek.how_to_minecraft.entity

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class MyEntity(type: EntityType<out MyEntity>, level: Level) : Entity(type, level) {
    companion object {
        // The generic type must match the one of the second parameter below.
        val MY_DATA: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            // The class of the entity
            MyEntity::class.java,
            // The entity data accessor type
            EntityDataSerializers.INT
        )
    }

    var data: Int = this.getEntityData().get(MY_DATA)

    // Useful to spawn entities programmatically
    constructor(type: EntityType<out MyEntity>, level: Level, x: Double, y: Double, z: Double) : this(type, level) {
        this.setPos(x, y, z)
    }

    protected override fun readAdditionalSaveData(input: ValueInput) {
        this.data = input.getIntOr("my_data", 0)
    }

    protected override fun addAdditionalSaveData(output: ValueOutput) {
        output.putInt("my_data", this.data)
    }

    protected override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        // Our default value is 0
        builder.define(MY_DATA, 0)
    }

    // Whether the entity was damaged or not
    override fun hurtServer(level: ServerLevel, damageSource: DamageSource, amount: Float): Boolean {
        return true
    }
}
