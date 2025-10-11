package org.abhiek.how_to_minecraft.test

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestInstance
import net.minecraft.gametest.framework.TestData
import net.minecraft.gametest.framework.TestEnvironmentDefinition
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

class ExampleTestInstance(
    val value1: Int,
    val value2: Boolean,
    info: TestData<Holder<TestEnvironmentDefinition>>
): GameTestInstance(info) {
    override fun run(helper: GameTestHelper) {
        // Run whatever game test commands you want
        // helper.assertBlockPresent(MY_BLOCK_1.get(), 0, 0, 0)

        // Make sure you have some way to succeed
        helper.succeedIf {}
    }

    override fun codec(): MapCodec<ExampleTestInstance> {
        return EXAMPLE_INSTANCE_CODEC
    }

    override fun typeDescription(): MutableComponent {
        // Provides a description about what this test is supposed to be
        // Should use a translatable component
        return Component.literal("Example Test Instance")
    }

    companion object {
        // Register our test instance for use
        val TEST_INSTANCE: DeferredRegister<MapCodec<out GameTestInstance>> = DeferredRegister.create(
            BuiltInRegistries.TEST_INSTANCE_TYPE,
            HowToMinecraft.ID
        )

        val EXAMPLE_INSTANCE_CODEC: MapCodec<ExampleTestInstance> by TEST_INSTANCE.register(
            "example_test_instance"
        ) { ->
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codec.INT.fieldOf("value1").forGetter { test -> test.value1 },
                    Codec.BOOL.fieldOf("value2").forGetter { test -> test.value2 },
                    TestData.CODEC.forGetter { obj -> obj.info() }
                ).apply(instance, ::ExampleTestInstance)
            }
        }
    }
}
