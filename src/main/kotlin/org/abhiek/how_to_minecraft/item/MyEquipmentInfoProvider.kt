package org.abhiek.how_to_minecraft.item

import net.minecraft.client.resources.model.EquipmentClientInfo
import net.minecraft.client.resources.model.EquipmentClientInfo.Dyeable
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID
import org.abhiek.how_to_minecraft.HowToMinecraft
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class MyEquipmentInfoProvider(output: PackOutput) : DataProvider {
    private val path = output.createPathProvider(
        PackOutput.Target.RESOURCE_PACK,
        "equipment"
    )

    private fun add(registrar: BiConsumer<ResourceLocation, EquipmentClientInfo>) {
        registrar.accept(
            // Must match Equippable#assetId
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "copper"),
            EquipmentClientInfo.builder()
                // For humanoid head, chest, and feet
                .addLayers(
                    EquipmentClientInfo.LayerType.HUMANOID,
                    // Base texture
                    EquipmentClientInfo.Layer(
                        // The relative texture of the armor
                        // Points to assets/how_to_minecraft/textures/entity/equipment/humanoid/copper/outer.png
                        ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "copper/outer"),
                        Optional.empty(),
                        false
                    ),
                    // Overlay texture
                    EquipmentClientInfo.Layer(
                        // The overlay texture
                        // Points to assets/how_to_minecraft/textures/entity/equipment/humanoid/copper/outer_overlay.png
                        ResourceLocation.fromNamespaceAndPath(
                            HowToMinecraft.ID,
                            "copper/outer_overlay"
                        ),
                        // An RGB value (always opaque color)
                        // When not specified, set to 0 (meaning transparent or invisible)
                        Optional.of(Dyeable(Optional.of(0x7683DE))),
                        false
                    )
                )
                // For humanoid legs
                .addLayers(
                    EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                    EquipmentClientInfo.Layer(
                        // Points to assets/how_to_minecraft/textures/entity/equipment/humanoid_leggings/copper/inner.png
                        ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "copper/inner"),
                        Optional.empty(),
                        false
                    ),
                    EquipmentClientInfo.Layer(
                        // Points to assets/how_to_minecraft/textures/entity/equipment/humanoid_leggings/copper/inner_overlay.png
                        ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "copper/inner_overlay"),
                        Optional.of(Dyeable(Optional.of(0x7683DE))),
                        false
                    )
                )
                // For wolf armor
                .addLayers(
                    EquipmentClientInfo.LayerType.WOLF_BODY,
                    // Base texture
                    EquipmentClientInfo.Layer(
                        // Points to assets/how_to_minecraft/textures/entity/equipment/wolf_body/copper/wolf.png
                        ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "copper/wolf"),
                        Optional.empty(),
                        // When true, uses the texture passed into the layer renderer instead
                        true
                    )
                )
                // For horse armor
                .addLayers(
                    EquipmentClientInfo.LayerType.HORSE_BODY,
                    // Base texture
                    EquipmentClientInfo.Layer(
                        // Points to assets/how_to_minecraft/textures/entity/equipment/horse_body/copper/horse.png
                        ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "copper/horse"),
                        Optional.empty(),
                        true
                    )
                )
                .build()
        )
    }

    override fun run(cache: CachedOutput): CompletableFuture<*> {
        val map = mapOf<ResourceLocation, EquipmentClientInfo>()
        this.add { name, _ ->
            check(map.contains(name)) {
                "Tried to register equipment client info twice for id: $name"
            }
        }
        return DataProvider.saveAll(
            cache,
            EquipmentClientInfo.CODEC,
            this.path,
            map
        )
    }

    override fun getName() = "Equipment Client Infos: $MOD_ID"
}

@SubscribeEvent
fun gatherData(event: GatherDataEvent.Client) {
    event.createProvider { output ->
        MyEquipmentInfoProvider(output)
    }
}