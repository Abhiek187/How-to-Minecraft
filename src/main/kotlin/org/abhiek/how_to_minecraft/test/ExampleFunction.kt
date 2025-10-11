package org.abhiek.how_to_minecraft.test

import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.util.FakePlayerFactory
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import java.util.UUID
import java.util.function.Consumer

/**
 * Test commands:
 * /test locate - locate test position
 * /test pos - get relative position
 * /test run <test_name> - run specified test (e.g., minecraft:always_pass)
 * /test runall is replaced with /test run how_to_minecraft:*
 * /test runclosest - run the closest test within 15 blocks
 * /test runthese - run tests within 200 blocks
 * /test runfailed - run failed tests
 * /test verify <test_name> - run a test multiple times
 */
object ExampleFunction {
    // Here is our example function
    fun exampleTest(helper: GameTestHelper) {
        // Given an apple
        // When the player right-clicks a dirt block
        // Then it transforms into a diamond
        val level = helper.level
        val pos = BlockPos(1, 1, 1)

        // Place a dirt block to right-click on
        helper.setBlock(pos, Blocks.DIRT)

        // Create a Forge FakePlayer (a real ServerPlayer subclass)
        val profile = GameProfile(UUID.randomUUID(), "test_player")
        val fakePlayer = FakePlayerFactory.get(level, profile)

        val hand = fakePlayer.usedItemHand
        val stack = Items.APPLE.defaultInstance
        fakePlayer.setItemInHand(hand, stack)

        // Build a hit result representing the player right-clicking the dirt block
        val hitResult = BlockHitResult(
            Vec3.atCenterOf(pos),
            Direction.UP,
            pos,
            false
        )
        println("Block position: $pos")
        // Where the block is positioned relative to the test instance block
        println("Block relative position: ${helper.relativePos(pos)}")
        // Where the block is using the world's coordinates
        println("Block absolute position: ${helper.absolutePos(pos)}")
        println("Fake player's position: ${fakePlayer.blockPosition()}")
        println("Fake player's relative position: ${helper.relativePos(fakePlayer.blockPosition())}")
        println("Fake player's absolute position: ${helper.absolutePos(fakePlayer.blockPosition())}")

        // Simulate right-clicking the block with the apple
        val result = fakePlayer.gameMode.useItemOn(
            fakePlayer,
            level,
            stack,
            hand,
            hitResult
        )

        // Optionally log result (useful for debugging)
        if (result != InteractionResult.SUCCESS) {
            // Not necessarily a test failure — but good to know
            println("useItemOn returned: $result")
        }

        // Wait one tick for any scheduled updates
        helper.runAfterDelay(1) {
            val blockAfter = level.getBlockState(pos).block
            if (blockAfter != Blocks.DIAMOND_BLOCK) {
                helper.fail(
                    Component.literal("Expected dirt to become diamond block, but got: ${blockAfter.descriptionId}")
                )
            } else {
                helper.succeed()
            }
        }
    }

    // Register our function for use
    val TEST_FUNCTION: DeferredRegister<Consumer<GameTestHelper>> = DeferredRegister.create(
        BuiltInRegistries.TEST_FUNCTION,
        HowToMinecraft.ID
    )

    val EXAMPLE_FUNCTION: DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> =
        TEST_FUNCTION.register("example_function") { ->
            Consumer { helper -> exampleTest(helper) }
        }
}
