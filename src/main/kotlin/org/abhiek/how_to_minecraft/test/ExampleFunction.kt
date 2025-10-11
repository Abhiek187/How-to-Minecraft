package org.abhiek.how_to_minecraft.test

import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType
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
//    fun exampleTest(helper: GameTestHelper) {
//        // Given an apple
//        // When the player right-clicks a dirt block
//        // Then it transforms into a diamond
//        val level = helper.level
//        val pos = BlockPos(1, 1, 1)
//
//        // Place a dirt block to right-click on
//        helper.setBlock(pos, Blocks.DIRT)
//
//        // Create a fake player holding an apple
//        val player = helper.makeMockPlayer(GameType.DEFAULT_MODE)
////        val hand = InteractionHand.MAIN_HAND
////        val stack = ItemStack(Items.APPLE)
//        val hand = player.usedItemHand
//        val stack = Items.APPLE.defaultInstance
//        player.setItemInHand(hand, stack)
//
//        // Build a hit result representing the player right-clicking the dirt block
//        val hitResult = BlockHitResult(
//            Vec3.atCenterOf(pos),
//            Direction.UP,
//            pos,
//            false
//        )
//
//        // Simulate right-clicking the block with the apple
////        player.interactOn(level.getBlockEntity(pos), hand)
////        player.interact(level.getBlockEntity(pos)?.blockState?.block, hand, hitResult)
//
//        // Wait one tick for any scheduled updates
//        helper.runAfterDelay(1) {
//            val blockAfter = level.getBlockState(pos).block
//            if (blockAfter != Blocks.DIAMOND_BLOCK) {
//                helper.fail(Component.literal("Expected dirt to become diamond block, but got: ${blockAfter.name}"))
//            } else {
//                helper.succeed()
//            }
//        }
//    }
    fun exampleTest(helper: GameTestHelper) {
        // Test area / block position inside the structure
        val pos = BlockPos(1, 1, 1)

        // Place dirt that should transform
        helper.setBlock(pos, Blocks.DIRT)

        // Cast GameTest level to a ServerLevel (GameTestHelper.level is a server-level instance)
        val serverLevel = helper.level as ServerLevel

        // Create a Forge FakePlayer (a real ServerPlayer subclass)
        val profile = GameProfile(UUID.randomUUID(), "test_player")
        val fakePlayer = FakePlayerFactory.get(serverLevel, profile)

        // Position the fake player near the block (optional but helpful)
        fakePlayer.teleportTo(pos.x.toDouble(), (pos.y + 1).toDouble(), pos.z.toDouble())

        // Equip the apple in main hand
        val hand = InteractionHand.MAIN_HAND
        val stack = ItemStack(Items.APPLE)
        fakePlayer.setItemInHand(hand, stack)

        // Build the BlockHitResult: center of the block, clicking the top face
        val hitResult = BlockHitResult(
            Vec3.atCenterOf(pos),
            Direction.UP,
            pos,
            false
        )

        // Call the exact useItemOn signature you showed (ServerPlayer, Level, ItemStack, InteractionHand, BlockHitResult)
        val result = fakePlayer.gameMode.useItemOn(
            fakePlayer,
            serverLevel,
            stack,
            hand,
            hitResult
        )

        // Optionally log result (useful for debugging)
        if (result != InteractionResult.SUCCESS) {
            // Not necessarily a test failure — but good to know
            println("useItemOn returned: $result")
        }

        // Wait a tick for any block updates and then assert
        helper.runAfterDelay(1) {
            val blockAfter = serverLevel.getBlockState(pos).block
            if (blockAfter != Blocks.DIAMOND_BLOCK) {
                helper.fail(
                    Component.literal(
                        "Expected dirt to become diamond block, but got: ${blockAfter.descriptionId}"
                    )
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
