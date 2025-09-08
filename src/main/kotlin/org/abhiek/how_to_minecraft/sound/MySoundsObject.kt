package org.abhiek.how_to_minecraft.sound

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredRegister
import org.abhiek.how_to_minecraft.HowToMinecraft
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object MySoundsObject {
    val SOUND_EVENTS: DeferredRegister<SoundEvent> =
        DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, HowToMinecraft.ID)

    // All vanilla sounds use variable range events.
    val MY_SOUND: SoundEvent by SOUND_EVENTS.register(
        "my_sound",
        // Takes in the registry name
        SoundEvent::createVariableRangeEvent
    )

    // There is a currently unused method to register fixed range (= non-attenuating) events as well:
    val MY_FIXED_SOUND: SoundEvent by SOUND_EVENTS.register(
        "my_fixed_sound"
    ) { registryName ->
        // 16 is the default range of sounds. Be aware that due to OpenAL limitations,
        // values above 16 have no effect and will be capped to 16.
        SoundEvent.createFixedRangeEvent(registryName, 16f)
    }
}
