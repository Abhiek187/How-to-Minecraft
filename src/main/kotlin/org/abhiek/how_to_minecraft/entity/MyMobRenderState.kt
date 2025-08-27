package org.abhiek.how_to_minecraft.entity

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.item.ItemStack

class MyMobRenderState: LivingEntityRenderState() {
    lateinit var stackInHand: ItemStack
    val myAnimationState = AnimationState()
}
