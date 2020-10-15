package me.basiqueevangelist.datapackify

import me.basiqueevangelist.datapackify.potionrecipes.PotionRecipes
import me.basiqueevangelist.datapackify.trades.VillagerTrades
import net.fabricmc.api.ModInitializer

object Datapackify : ModInitializer {
    const val NAMESPACE = "datapackify"

    override fun onInitialize() {
        VillagerTrades.init()
        PotionRecipes.init()
    }
}