package org.abhiek.how_to_minecraft

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
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
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent
import org.abhiek.how_to_minecraft.block.ModBlocks
import org.abhiek.how_to_minecraft.data_map.ExampleData.Companion.EXAMPLE_DATA
import org.abhiek.how_to_minecraft.entity.ModEntities
import org.abhiek.how_to_minecraft.item.ModItems
import org.abhiek.how_to_minecraft.recipe.*

/* Subscribe to all events at once: MOD_BUS.register(EventHandler):
 * https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.21.x-neoforge/net/neoforged/neoforge/event/package-summary.html
 */
@EventBusSubscriber(modid = HowToMinecraft.ID)
object EventHandler {
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
    fun gatherData(event: GatherDataEvent.Client) {
        // Alternative to creating JSON files manually
//        event.createProvider { output ->
//            MyEquipmentInfoProvider(output)
//        }
//        event.createProvider(::MyEquipmentInfoProvider)
//        event.createProvider(::ExampleModelProvider)
        event.createProvider(MyRecipeProvider::Runner)
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

    @SubscribeEvent
    fun useItemOnBlock(event: UseItemOnBlockEvent) {
        println("UseItemOnBlockEvent usePhase: ${event.usePhase}")
        // Skip if we are not in the block-dictated phase of the event. See the event's javadocs for details.
        if (event.usePhase != UseItemOnBlockEvent.UsePhase.BLOCK) return
        // Get parameters to check input first
        val level = event.level
        val pos = event.pos
        val blockState = level.getBlockState(pos)
        val itemStack = event.itemStack
        println("Using an item ($itemStack) on a block ($blockState)")

        // Check if the input can result in a recipe on both sides
        // Right-click dirt with an apple in hand --> convert dirt block into a diamond
        if (RightClickBlockRecipes.inputs(level)?.test(blockState, itemStack) != true) return

        // If so, make sure on server before checking recipe
        if (!level.isClientSide() && level is ServerLevel) {
            // Create an input and query the recipe
            val input = RightClickBlockInput(blockState, itemStack)
            val optional = level.recipeAccess().getRecipeFor(
                // The recipe type
                RightClickBlockRecipe.RIGHT_CLICK_BLOCK_TYPE,
                input,
                level
            )
            val result = optional
                .map(RecipeHolder<RightClickBlockRecipe>::value)
                .map { e -> e.assemble(input, level.registryAccess()) }
                .orElse(ItemStack.EMPTY)

            // If there is a result, break the block and drop the result in the world.
            if (!result.isEmpty) {
                level.removeBlock(pos, false)
                val entity = ItemEntity(
                    level,
                    // Center of pos
                    pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                    result
                )
                level.addFreshEntity(entity)
            }
        }

        // Cancel the event to stop the interaction pipeline regardless of side.
        // Already made sure that there could be a result.
        event.cancelWithResult(InteractionResult.SUCCESS_SERVER)
    }

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playBidirectional(
            ClientboundRightClickBlockRecipesPayload.TYPE,
            ClientboundRightClickBlockRecipesPayload.STREAM_CODEC
        ) { _, _ ->
//            @Suppress("UnstableApiUsage")
//            ServerPayloadHandler.handle(payload, context)
        }
    }
}
