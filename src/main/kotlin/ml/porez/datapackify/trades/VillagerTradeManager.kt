package ml.porez.datapackify.trades

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import ml.porez.datapackify.Datapackify
import ml.porez.datapackify.mixins.TradeOffersAccessors
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler
import net.minecraft.util.registry.Registry
import net.minecraft.village.TradeOffers
import net.minecraft.village.VillagerProfession
import org.apache.logging.log4j.LogManager
import java.util.*

class VillagerTradeManager : JsonDataLoader(GSON, "villager_trades"), IdentifiableResourceReloadListener {
    companion object {
        private val LOGGER = LogManager.getLogger("Datapackify/VillagerTradeManager")
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    }

    override fun getFabricId(): Identifier? {
        return Identifier(Datapackify.NAMESPACE, "villager_trades")
    }

    override fun apply(loader: Map<Identifier?, JsonElement>, manager: ResourceManager?, profiler: Profiler?) {
        val trades = HashMap<VillagerProfession, Int2ObjectOpenHashMap<MutableList<TradeOffers.Factory>>>()
        val wanderingTrades = Int2ObjectOpenHashMap<MutableList<TradeOffers.Factory>>()

        for (prof in Registry.VILLAGER_PROFESSION) {
            trades[prof] = Int2ObjectOpenHashMap<MutableList<TradeOffers.Factory>>()
        }
        for ((key, value) in loader) {
            LOGGER.debug("Got {}", key)
            try {
                val obj = JsonHelper.asObject(value, "<file>")
                val e = parseFile(obj)
                val profId =
                    Identifier(
                        JsonHelper.getString(
                            obj,
                            "profession"
                        )
                    )

                val map = if (profId == Identifier("wandering_trader")) {
                    wanderingTrades
                } else {
                    val profession = Registry.VILLAGER_PROFESSION.getOrEmpty(profId).orElseThrow {
                        IllegalArgumentException("Invalid profession $profId")
                    }
                    trades[profession]!!
                }
                val careerLevel = JsonHelper.getInt(obj, "career_level")
                if (map.containsKey(careerLevel)) map[careerLevel]!!.addAll(
                    arrayListOf(*e)
                ) else map[careerLevel] = arrayListOf(*e)
            } catch (e: IllegalArgumentException) {
                LOGGER.error("Encountered error while parsing {}: {}", key, e)
                e.printStackTrace()
            } catch (e: JsonParseException) {
                LOGGER.error("Encountered error while parsing {}: {}", key, e)
                e.printStackTrace()
            }
        }
        LOGGER.info("Loaded ${trades.size + wanderingTrades.size} villager trades.")
        val finalMap = HashMap<VillagerProfession, Int2ObjectMap<Array<TradeOffers.Factory>>>()
        for ((key, value) in trades) {
            val profMap = Int2ObjectOpenHashMap<Array<TradeOffers.Factory>>()
            for ((key1, value1) in value) {
                profMap[key1.toInt()] = value1.toTypedArray()
            }
            finalMap[key] = profMap
        }
        TradeOffersAccessors.GlobalAcc.setTrades(finalMap)
        val finalWanderingMap = Int2ObjectOpenHashMap<Array<TradeOffers.Factory>>()
        for ((key, value) in wanderingTrades) {
            finalWanderingMap[key.toInt()] = value.toTypedArray()
        }
        TradeOffersAccessors.GlobalAcc.setWanderingTrades(finalWanderingMap)
    }

    private fun parseFile(obj: JsonObject): Array<TradeOffers.Factory> {
        val trades = JsonHelper.getArray(obj, "trades")
        val value: Array<TradeOffers.Factory?> = arrayOfNulls(trades.size())
        var i = 0
        for (trade in trades) {
            value[i++] = parseTrade(trade)
        }
        return value as Array<TradeOffers.Factory>
    }

    private fun parseTrade(trade: JsonElement): TradeOffers.Factory {
        val obj = JsonHelper.asObject(trade, "trade")
        val type = Identifier(JsonHelper.getString(obj, "type"))
        val res = VillagerTrades.REGISTRY.getOrEmpty(type)
        kotlin.require(res.isPresent) { "Invalid factory type $type" }
        return res.get().deserialize(obj)
    }
}