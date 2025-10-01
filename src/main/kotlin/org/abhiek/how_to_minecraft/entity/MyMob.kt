package org.abhiek.how_to_minecraft.entity

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.damagesource.DamageContainer
import org.abhiek.how_to_minecraft.gui.MyMenu

class MyMob(type: EntityType<out MyMob>, private val level: Level):
    Mob(type, level), MenuProvider {
    override fun getMainArm(): HumanoidArm {
        // Get the attribute map of our entity
        val attributes = this.attributes
        // Get the max health of our entity
        // val maxHealth = attributes.getValue(Attributes.MAX_HEALTH)
        // Alternatively:
        val maxHealth = this.maxHealth
        // Setting the max health must either be done by getting the AttributeInstance and calling #setBaseValue, or by
        // adding an attribute modifier.
        // attributes.getInstance(Attributes.MAX_HEALTH)?.baseValue = 50.0
        println("MyMob attributes: $attributes")
        println("MyMob max health: $maxHealth")

        return HumanoidArm.LEFT // lefty
    }

    override fun onDamageTaken(damageContainer: DamageContainer) {
        // Perform post-attack behavior, only called if damage > 0
        super.onDamageTaken(damageContainer)

        println("Original damage: ${damageContainer.originalDamage}")
        println("New damage: ${damageContainer.newDamage}")
        println("Damage source: ${damageContainer.source.entity?.type}")
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        if (!this.level.isClientSide && player is ServerPlayer) {
            player.openMenu(this)
        }

        return InteractionResult.SUCCESS
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return MyMenu(containerId, playerInventory)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.title.how_to_minecraft.my_menu")
    }
}
