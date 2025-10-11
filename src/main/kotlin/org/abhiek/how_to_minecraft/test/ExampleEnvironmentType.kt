package org.abhiek.how_to_minecraft.test

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.TestEnvironmentDefinition
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

data class ExampleEnvironmentType(val value1: Int, val value2: Boolean): TestEnvironmentDefinition {
    override fun setup(level: ServerLevel) {
        // Setup whatever is necessary here
    }

    override fun teardown(level: ServerLevel) {
        // Undo whatever was changed within the setup method
        // This should either return to default or the previous value
    }

    override fun codec(): MapCodec<ExampleEnvironmentType> {
        return EXAMPLE_ENVIRONMENT_CODEC
    }

    companion object {
        // Construct the map codec to register
        val CODEC: MapCodec<ExampleEnvironmentType> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("value1").forGetter(ExampleEnvironmentType::value1),
                Codec.BOOL.fieldOf("value2").forGetter(ExampleEnvironmentType::value2)
            ).apply(instance, ::ExampleEnvironmentType)
        }

        val TEST_ENVIRONMENT_DEFINITION_TYPES: DeferredRegister<MapCodec<out TestEnvironmentDefinition>> =
            DeferredRegister.create(
                BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE,
                HowToMinecraft.ID
            )

        val EXAMPLE_ENVIRONMENT_CODEC: MapCodec<ExampleEnvironmentType> by TEST_ENVIRONMENT_DEFINITION_TYPES.register(
            "example_environment_type"
        ) { -> CODEC }
    }
}
