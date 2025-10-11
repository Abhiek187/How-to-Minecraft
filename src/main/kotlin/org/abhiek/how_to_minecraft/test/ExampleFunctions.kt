package org.abhiek.how_to_minecraft.test

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
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
object ExampleFunctions {
    // Here is our example function
    fun exampleTest(helper: GameTestHelper) {
        // Do stuff
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
