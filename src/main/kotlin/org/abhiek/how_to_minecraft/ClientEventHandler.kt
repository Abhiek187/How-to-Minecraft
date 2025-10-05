package org.abhiek.how_to_minecraft

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.context.ContextKey
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.*
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent
import org.abhiek.how_to_minecraft.block.ModBlocks
import org.abhiek.how_to_minecraft.block.MyBlockEntityRenderer
import org.abhiek.how_to_minecraft.entity.ModEntities
import org.abhiek.how_to_minecraft.entity.MyMobModel
import org.abhiek.how_to_minecraft.entity.MyMobModel.Companion.MY_LAYER
import org.abhiek.how_to_minecraft.entity.MyMobRenderer
import org.abhiek.how_to_minecraft.entity.MyRenderLayer
import org.abhiek.how_to_minecraft.gui.ExampleRenderState
import org.abhiek.how_to_minecraft.gui.ExampleRenderer
import org.abhiek.how_to_minecraft.gui.MyContainerScreen
import org.abhiek.how_to_minecraft.gui.MyMenu
import org.abhiek.how_to_minecraft.particle.MyParticleProvider
import org.abhiek.how_to_minecraft.particle.MyParticleTypes
import org.abhiek.how_to_minecraft.recipe.ClientRightClickBlockRecipes
import org.abhiek.how_to_minecraft.recipe.ClientboundRightClickBlockRecipesPayload
import org.abhiek.how_to_minecraft.recipe.RightClickBlockRecipe

/* For client-specific events only:
 * https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.21.x-neoforge/net/neoforged/neoforge/client/event/package-summary.html
 */
@EventBusSubscriber(modid = HowToMinecraft.ID, value = [Dist.CLIENT])
object ClientEventHandler {
    @SubscribeEvent
    fun onClick(event: InputEvent.InteractionKeyMappingTriggered) {
        println("Click event!")
        val minecraft = Minecraft.getInstance()

        println("Hit result Type: ${minecraft.hitResult?.type}, Location: ${minecraft.hitResult?.location}")
        println("Hand: ${event.hand}")
        println("Is left click? ${event.isAttack}")
        println("Is middle click? ${event.isPickBlock}")
        println("Is right click? ${event.isUseItem}")
        println("Is canceled? ${event.isCanceled}")
        println("Key mapping Key: ${event.keyMapping.key}, Name: ${event.keyMapping.name}, Category: ${
            event.keyMapping.category
        }")
    }

    // Renderers are required for living & block entities
    @SubscribeEvent
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(ModEntities.MY_MOB, ::MyMobRenderer)

        event.registerBlockEntityRenderer(
            // The block entity type to register the renderer for
            ModBlocks.MY_BLOCK_ENTITY,
            // A function of BlockEntityRendererProvider.Context to BlockEntityRenderer
            ::MyBlockEntityRenderer
        )
    }

    @SubscribeEvent
    fun registerRenderStateModifiers(event: RegisterRenderStateModifiersEvent) {
        val EXAMPLE_CONTEXT = ContextKey<String>(
            // The id of your context key. Used for distinguishing between keys internally.
            ResourceLocation.fromNamespaceAndPath(HowToMinecraft.ID, "example_context")
        )

//        event.registerEntityModifier(
//            // A TypeToken for the renderer. It is REQUIRED for this to be instantiated as an anonymous class
//            // (i.e., with {} at the end) and to have explicit generic parameters, due to generics nonsense.
//            object: TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, *>>() {}
//        ) { _, state ->
//            // The modifier itself. This is a BiConsumer of the entity and the entity render state.
//            // Exact generic types are inferred from the generics in the renderer class used.
//            state.setRenderData(EXAMPLE_CONTEXT, "Hello World!")
//        }
        // Overload of the above method that accepts a Class<?>.
        // This should ONLY be used for renderers without any generics, such as PlayerRenderer.
        event.registerEntityModifier(PlayerRenderer::class.java) { _, state ->
            state.setRenderData(EXAMPLE_CONTEXT, "Hello World!")
        }
    }

    @SubscribeEvent
    fun registerLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        // Add our layer here
        event.registerLayerDefinition(MY_LAYER, MyMobModel::createBodyLayer)
    }

    @SubscribeEvent
    fun addLayers(event: EntityRenderersEvent.AddLayers) {
        // Add a layer to every single entity type.
        for (entityType in event.entityTypes) {
            // Get our renderer.
            val renderer = event.getRenderer(entityType)
            // We check if our render layer is supported by the renderer.
            // If you want a more general-purpose render layer, you will need to work with wildcard generics.
            if (renderer is MyMobRenderer) {
                // Add the layer to the renderer. Like above, construct a new MyRenderLayer.
                // The EntityModelSet can be retrieved from the event through #getEntityModels.
                renderer.addLayer(MyRenderLayer(renderer, event.entityModels))
            }
        }
    }

    @SubscribeEvent
    fun registerBlockColorHandlers(event: RegisterColorHandlersEvent.Block) {
        // Parameters are the block's state, the level the block is in, the block's position, and the tint index.
        // The level and position may be null.
        event.register(
            { _, _, _, _ ->
                // Replace with your own calculation. See the BlockColors class for vanilla references.
                // Colors are in ARGB format. Generally, if the tint index is -1, it means that no tinting
                // should take place and a default value should be used instead.
                0xFFFFFFFF.toInt()
            },
            // A varargs of blocks to apply the tinting to
            ModBlocks.EXAMPLE_BLOCK.get()
        )
    }

    @SubscribeEvent
    fun registerColorResolvers(event: RegisterColorHandlersEvent.ColorResolvers) {
        // Parameters are the current biome, the block's X position, and the block's Z position.
        event.register { _, _, _ ->
            // Replace with your own calculation. See the BiomeColors class for vanilla references.
            // Colors are in ARGB format.
            0xFFFFFFFF.toInt()
        }
    }

    @SubscribeEvent
    fun registerParticleProviders(event: RegisterParticleProvidersEvent) {
        // There are multiple ways to register providers, all differing in the functional type they provide in the
        // second parameter. For example, #registerSpriteSet represents a Function<SpriteSet, ParticleProvider<?>>:
        event.registerSpriteSet(MyParticleTypes.MY_PARTICLE, ::MyParticleProvider)
        // Other methods include #registerSprite, which is essentially a Supplier<TextureSheetParticle>,
        // and #registerSpecial, which maps to a Supplier<Particle>. See the source code of the event for further info.
    }

    @SubscribeEvent
    fun registerSearchCategories(event: RegisterRecipeBookSearchCategoriesEvent) {
        event.register(
            // The search category
            RightClickBlockRecipe.RIGHT_CLICK_BLOCK_SEARCH_CATEGORY,
            // All recipe categories within the search category as varargs
            RightClickBlockRecipe.RIGHT_CLICK_BLOCK_CATEGORY
        )
    }

    @SubscribeEvent
    fun register(event: RegisterClientPayloadHandlersEvent) {
        event.register(
            ClientboundRightClickBlockRecipesPayload.TYPE,
            ClientRightClickBlockRecipes::handle
        )
    }

    @SubscribeEvent
    fun registerPip(event: RegisterPictureInPictureRenderersEvent) {
        event.register(
            // The PiP render state class
            ExampleRenderState::class.java,
            // A factory that takes in the `MultiBufferSource.BufferSource` and returns the PiP renderer
            ::ExampleRenderer
        )
    }

    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(
            MyMenu.MY_MENU,
            ::MyContainerScreen
        )
    }
}
