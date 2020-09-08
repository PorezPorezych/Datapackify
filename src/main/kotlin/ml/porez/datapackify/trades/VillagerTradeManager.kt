package ml.porez.datapackify.trades

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import ml.porez.datapackify.Datapackify
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
import kotlin.Pair;

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
        for (prof in Registry.VILLAGER_PROFESSION) {
            trades[prof] = Int2ObjectOpenHashMap<MutableList<TradeOffers.Factory>>()
        }
        for ((key, value) in loader) {
            LOGGER.debug("Got {}", key)
            try {
                val profId = Identifier(
                        JsonHelper.getString(JsonHelper.asObject(value, "<file>"),
                                "profession")
                )
                val profession = Registry.VILLAGER_PROFESSION.getOrEmpty(profId)
                kotlin.require(profession.isPresent) { "Invalid profession $profId" }
                val e = parseFile(value)
                if (trades[profession.get()]!!.containsKey(e.first)) trades[profession.get()]!![e.first]!!.addAll(Arrays.asList(*e.second)) else trades[profession.get()]!![e.first] = ArrayList(Arrays.asList(*e.second))
            } catch (e: IllegalArgumentException) {
                LOGGER.error("Encountered error while parsing {}: {}", key, e)
                e.printStackTrace()
            } catch (e: JsonParseException) {
                LOGGER.error("Encountered error while parsing {}: {}", key, e)
                e.printStackTrace()
            }
        }
        LOGGER.info("Loaded {} villager trades.", trades.size)
        val finalMap = HashMap<VillagerProfession, Int2ObjectMap<Array<TradeOffers.Factory>>>()
        for ((key, value) in trades) {
            val profMap = Int2ObjectOpenHashMap<Array<TradeOffers.Factory>>()
            for ((key1, value1) in value) {
                profMap[key1.toInt()] = value1.toTypedArray()
            }
            finalMap[key] = profMap
        }
        TradeOffers.PROFESSION_TO_LEVELED_TRADE = finalMap
    }

    private fun parseFile(file: JsonElement): Pair<Int, Array<TradeOffers.Factory>> {
        val obj = JsonHelper.asObject(file, "<file>")
        val key = JsonHelper.getInt(obj, "career_level")
        val trades = JsonHelper.getArray(obj, "trades")
        val value: Array<TradeOffers.Factory?> = arrayOfNulls(trades.size())
        var i = 0
        for (trade in trades) {
            value[i++] = parseTrade(trade)
        }
        return Pair(key, value as Array<TradeOffers.Factory>)
    }

    private fun parseTrade(trade: JsonElement): TradeOffers.Factory {
        val obj = JsonHelper.asObject(trade, "trade")
        val type = Identifier(JsonHelper.getString(obj, "type"))
        val res = VillagerTrades.REGISTRY.getOrEmpty(type)
        kotlin.require(res.isPresent) { "Invalid factory type $type" }
        return res.get().deserialize(obj)
    }
}