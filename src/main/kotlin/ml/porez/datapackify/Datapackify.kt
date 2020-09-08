package ml.porez.datapackify

import ml.porez.datapackify.trades.VillagerTrades
import net.fabricmc.api.ModInitializer

public object Datapackify : ModInitializer {
    const val NAMESPACE = "datapackify"

    override fun onInitialize() {
        VillagerTrades.init();
    }
}