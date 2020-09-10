package ml.porez.datapackify.potionrecipes

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

object PotionRecipes {
    private val MANAGER = BrewingRecipeManager()

    fun init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MANAGER)
    }
}