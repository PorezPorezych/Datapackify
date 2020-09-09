package ml.porez.datapackify.potionrecipes

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import ml.porez.datapackify.Datapackify
import ml.porez.datapackify.JsonUtils
import ml.porez.datapackify.mixins.BrewingRecipeRegistryAccessors
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.item.Item
import net.minecraft.potion.Potion
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.recipe.Ingredient
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager

class BrewingRecipeManager : JsonDataLoader(GSON, "brewing_recipes"), IdentifiableResourceReloadListener {
    companion object {
        private val LOGGER = LogManager.getLogger("Datapackify/BrewingRecipeManager")
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

        private var PRESET_ITEM_RECIPES: List<BrewingRecipeRegistry.Recipe<Item>>? = null;
        private var PRESET_POTION_RECIPES: List<BrewingRecipeRegistry.Recipe<Potion>>? = null;
        private var PRESET_POTION_TYPES: List<Ingredient>? = null;
    }

    override fun apply(loader: MutableMap<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        if (PRESET_ITEM_RECIPES == null) {
            PRESET_ITEM_RECIPES = BrewingRecipeRegistryAccessors.getItemRecipes();
        }
        if (PRESET_POTION_RECIPES == null) {
            PRESET_POTION_RECIPES = BrewingRecipeRegistryAccessors.getPotionRecipes();
        }
        if (PRESET_POTION_TYPES == null) {
            PRESET_POTION_TYPES = BrewingRecipeRegistryAccessors.getPotionTypes();
        }

        val newItemRecipes = arrayListOf<BrewingRecipeRegistry.Recipe<Item>>()
        val newPotionRecipes = arrayListOf<BrewingRecipeRegistry.Recipe<Potion>>()

        for ((id, el) in loader) {
            try {
                val obj = JsonHelper.asObject(el, "<brewing recipe>");
                val type = JsonHelper.getString(obj, "type");
                when (type) {
                    "item" -> {
                        newItemRecipes.add(
                            BrewingRecipeRegistry.Recipe<Item>(
                                JsonHelper.getItem(obj, "input"),
                                Ingredient.fromJson(JsonUtils.get(obj, "ingredient")),
                                JsonHelper.getItem(obj, "output")
                            )
                        )
                    }
                    "potion" -> {
                        newPotionRecipes.add(
                            BrewingRecipeRegistry.Recipe<Potion>(
                                JsonUtils.getRegistryItem(Registry.POTION, JsonHelper.getString(obj, "input")),
                                Ingredient.fromJson(JsonUtils.get(obj, "ingredient")),
                                JsonUtils.getRegistryItem(Registry.POTION, JsonHelper.getString(obj, "output"))
                            )
                        )
                    }
                    else -> throw IllegalArgumentException("Invalid brewing recipe type $type")
                }
            } catch (e: IllegalArgumentException) {
                LOGGER.error("Encountered error while parsing {}: {}", id, e)
                e.printStackTrace()
            } catch (e: JsonParseException) {
                LOGGER.error("Encountered error while parsing {}: {}", id, e)
                e.printStackTrace()
            }
        }
        LOGGER.info("Loaded ${newItemRecipes.size} item recipes and ${newPotionRecipes.size} potion recipes")
        newItemRecipes.addAll(PRESET_ITEM_RECIPES!!)
        newPotionRecipes.addAll(PRESET_POTION_RECIPES!!)

        val potionTypes =
            arrayListOf<Ingredient>(Ingredient.fromTag(TagRegistry.item(Identifier("minecraft:potions"))))
        potionTypes.addAll(PRESET_POTION_TYPES!!)

        BrewingRecipeRegistryAccessors.setItemRecipes(newItemRecipes)
        BrewingRecipeRegistryAccessors.setPotionRecipes(newPotionRecipes)
        BrewingRecipeRegistryAccessors.setPotionTypes(potionTypes)
    }

    override fun getFabricId(): Identifier {
        return Identifier(Datapackify.NAMESPACE, "brewing_recipes")
    }
}
