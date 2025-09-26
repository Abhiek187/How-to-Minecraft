package org.abhiek.how_to_minecraft

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.context.ContextKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent
import net.neoforged.neoforge.common.damagesource.DamageContainer
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.event.AnvilUpdateEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent
import org.abhiek.how_to_minecraft.block.ModBlocks
import org.abhiek.how_to_minecraft.block.MyBlockEntityRenderer
import org.abhiek.how_to_minecraft.data_map.ExampleData.Companion.EXAMPLE_DATA
import org.abhiek.how_to_minecraft.entity.ModEntities
import org.abhiek.how_to_minecraft.entity.MyMobModel
import org.abhiek.how_to_minecraft.entity.MyMobModel.Companion.MY_LAYER
import org.abhiek.how_to_minecraft.entity.MyMobRenderer
import org.abhiek.how_to_minecraft.entity.MyRenderLayer
import org.abhiek.how_to_minecraft.item.ModItems
import org.abhiek.how_to_minecraft.particle.MyParticleProvider
import org.abhiek.how_to_minecraft.particle.MyParticleTypes
import org.abhiek.how_to_minecraft.provider.ExampleModelProvider
import org.abhiek.how_to_minecraft.provider.MyEquipmentInfoProvider

// Subscribe to all events at once: MOD_BUS.register(EventHandler)
@EventBusSubscriber(modid = HowToMinecraft.ID)
object EventHandler {
    /*
    Events:
    - https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.21.x-neoforge/net/neoforged/neoforge/event/package-summary.html
    - Client side: https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.21.x-neoforge/net/neoforged/neoforge/client/event/package-summary.html
     */
    @SubscribeEvent
    private fun onLoad(event: PlayerEvent.PlayerLoggedInEvent) {
        debugMenu(HowToMinecraft.LOGGER, event.entity)
    }

    // Heals an entity by half a heart every time they jump.
    @SubscribeEvent(priority = EventPriority.HIGH)
    private fun onLivingJump(event: LivingJumpEvent) {
        val entity = event.entity
        println("event entity: $entity")
        println("is logical client? ${entity.level().isClientSide}")

        // Only heal on the server side
        if (!entity.level().isClientSide) {
            entity.heal(1f)
            println("Entity block position: ${entity.blockPosition()}")
            println("Example block state: ${
                entity.level().getBlockState(entity.blockPosition())
            }")
            // .get() is required since EXAMPLE_BLOCK is deferred
            println("Example block default state: ${ModBlocks.EXAMPLE_BLOCK.get().defaultBlockState()}")
        }

        if (entity is Player) {
            debugMenu(HowToMinecraft.LOGGER, entity)
        }

        val serverLevel = event.entity.level()
        if (serverLevel !is ServerLevel) return
        val recipes = serverLevel.recipeAccess()
        // Construct a RecipeInput, as required by the recipe. For example, construct a CraftingInput for a crafting recipe.
        // The parameters are width, height and items, respectively.
        val input = CraftingInput.of(1, 1, listOf(ItemStack(Items.DIAMOND_BLOCK)))
        // The generic wildcard on the recipe holder should then extend CraftingRecipe.
        // This allows for more type safety later on.
        val optional = recipes.getRecipeFor(
            // The recipe type to get the recipe for. In our case, we use the crafting type.
            RecipeType.CRAFTING,
            // Our recipe input
            input,
            // Our level context
            serverLevel
        )

        // Use ItemStack.EMPTY as a fallback
        val result = optional
            .map { obj -> obj.value() }
            .map { recipe -> recipe.assemble(input, serverLevel.registryAccess()) }
            .orElse(ItemStack.EMPTY)
        println("How to make a diamond block: $result")

        // Like before, pass the desired recipe type
        val craftingRecipes = recipes.recipeMap().byType(RecipeType.CRAFTING)
        println("${craftingRecipes.size} crafting recipes")
        val smeltingRecipes = recipes.recipeMap().byType(RecipeType.SMELTING)
        println("${smeltingRecipes.size} smelting recipes")
    }

    @SubscribeEvent
    fun buildContents(event: BuildCreativeModeTabContentsEvent) {
        // Add items to the creative menu
        when (event.tabKey) {
            CreativeModeTabs.BUILDING_BLOCKS -> {
                event.accept(ModBlocks.EXAMPLE_BLOCK)
            }
            CreativeModeTabs.FUNCTIONAL_BLOCKS -> {
                event.accept(ModBlocks.MY_BLOCK_1)
                event.accept(ModBlocks.MY_BLOCK_2)
            }
            CreativeModeTabs.TOOLS_AND_UTILITIES -> {
                event.accept(ModItems.EQUIPPABLE)
            }
            CreativeModeTabs.COMBAT -> {
                event.accept(ModItems.COPPER_SWORD)
                event.accept(ModItems.COPPER_HELMET)
            }
            CreativeModeTabs.FOOD_AND_DRINKS -> {
                event.accept(ModItems.CONSUMABLE)
                event.accept(ModItems.FOOD)
            }
            CreativeModeTabs.INGREDIENTS -> {
                event.accept(ModItems.EXAMPLE_ITEM)
            }
            CreativeModeTabs.SPAWN_EGGS -> {
                event.accept(ModItems.MY_MOB_SPAWN_EGG)
            }
        }
    }

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

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent.Client) {
        // Alternative to creating JSON files manually
//        event.createProvider { output ->
//            MyEquipmentInfoProvider(output)
//        }
        event.createProvider(::MyEquipmentInfoProvider)
        event.createProvider(::ExampleModelProvider)
    }

    @SubscribeEvent
    fun decreaseArmor(event: LivingIncomingDamageEvent) {
        // We only apply this decrease to players and leave zombies etc. unchanged
        if (event.entity is Player) {
            // Add our reduction modifier callback
            event.container.addModifier(
                // The reduction to target. See the DamageContainer.Reduction enum for possible values.
                DamageContainer.Reduction.ARMOR
            ) { _, baseReduction ->
                // The modification to perform. Gets the damage container and the base reduction as inputs,
                // and outputs the new reduction. Both input and output reductions are floats.
                baseReduction * 0.5f
            }
        }
    }

    // Default attributes are required for living entities
    @SubscribeEvent
    fun createDefaultAttributes(event: EntityAttributeCreationEvent) {
        event.put(
            // Your entity type
            ModEntities.MY_MOB,
            // An AttributeSupplier. This is typically created by calling LivingEntity#createLivingAttributes,
            // setting your values on it, and calling #build. You can also create the AttributeSupplier from scratch
            // if you want, see the source of LivingEntity#createLivingAttributes for an example.
            // LivingEntity.createLivingAttributes()
            Mob.createMobAttributes()
                // Add an attribute with its default value
                //.add(Attributes.MAX_HEALTH)
                // Add an attribute with a non-default value
                .add(Attributes.MAX_HEALTH, 50.0)
                // Build the AttributeSupplier
                .build()
        )
    }

    @SubscribeEvent // on the mod event bus
    fun modifyDefaultAttributes(event: EntityAttributeModificationEvent) {
        // If villagers don't have the armor attribute already, we add it
        if (!event.has(EntityType.VILLAGER, Attributes.ARMOR)) {
            event.add(
                // The EntityType to add the attribute for
                EntityType.VILLAGER,
                // The Holder<Attribute> to add to the EntityType. Can also be a custom attribute.
                Attributes.ARMOR,
                // The attribute value to add.
                // Can be omitted, if so, the attribute's default value will be used instead.
                10.0
            )
        }
    }

    // Renderers are required for living & block entities
    @SubscribeEvent
    fun registerEntityRenderers(event: RegisterRenderers) {
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
    fun registerDataMapTypes(event: RegisterDataMapTypesEvent) {
        event.register(EXAMPLE_DATA)
    }

    @SubscribeEvent
    fun itemPickup(event: ItemEntityPickupEvent.Post) {
        val stack = event.currentStack
        // Get a Holder<Item> via ItemStack#getItemHolder
        val holder = stack.itemHolder
        // Get the data from the holder
        val data = holder.getData(EXAMPLE_DATA)
        if (data != null) {
            // The values are present, so let's do something with them!
            val player = event.player
            if (player.level().getRandom().nextFloat() > data.chance) {
                player.heal(data.amount)
            }
        }
    }

    // This example allows repairing a stone pickaxe with a full stack of dirt, consuming half the stack, for 3 levels.
    @SubscribeEvent
    fun onAnvilUpdate(event: AnvilUpdateEvent) {
        val left = event.left
        val right = event.right
        if (left.`is`(Items.STONE_PICKAXE) && right.`is`(Items.DIRT) && right.count >= 64) {
            event.output = ItemStack(Items.STONE_PICKAXE)
            event.materialCost = 32
            event.xpCost = 3
        }
    }
}
