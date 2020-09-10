package ml.porez.datapackify

import ml.porez.datapackify.potionrecipes.PotionRecipes
import ml.porez.datapackify.trades.VillagerTrades
import net.fabricmc.api.ModInitializer

object Datapackify : ModInitializer {
    const val NAMESPACE = "datapackify"

    override fun onInitialize() {
        VillagerTrades.init()
        PotionRecipes.init()
    }
}