package me.basiqueevangelist.datapackify.trades

import com.google.gson.JsonObject
import me.basiqueevangelist.datapackify.JsonUtils
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.JsonHelper
import net.minecraft.village.TradeOffer
import net.minecraft.village.TradeOffers
import java.util.*

class GenericTradeOfferFactory(
    val offer: TradeOffer
) : TradeOffers.Factory {
    override fun create(entity: Entity?, random: Random?): TradeOffer? {
        return offer
    }

    companion object {
        fun parse(obj: JsonObject): GenericTradeOfferFactory {
            val firstBuy = JsonUtils.makeItemStack(JsonUtils.get(obj, "firstBuy"))
            val secondBuy = if (obj.has("secondBuy")) {
                JsonUtils.makeItemStack(JsonUtils.get(obj, "secondBuy"))
            } else {
                ItemStack.EMPTY
            }
            val sell = JsonUtils.makeItemStack(JsonUtils.get(obj, "sell"))
            val uses = JsonHelper.getInt(obj, "uses", 0)
            val maxUses = JsonHelper.getInt(obj, "uses")
            val xp = JsonHelper.getInt(obj, "xp")
            val priceMultiplier = JsonHelper.getFloat(obj, "price_multiplier")
            val demand = JsonHelper.getInt(obj, "demand", 0)
            return GenericTradeOfferFactory(TradeOffer(firstBuy, secondBuy, sell, uses, maxUses, xp, priceMultiplier, demand))
        }
    }
}