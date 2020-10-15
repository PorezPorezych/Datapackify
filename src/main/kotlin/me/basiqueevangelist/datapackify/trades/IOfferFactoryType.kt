package me.basiqueevangelist.datapackify.trades;

import com.google.gson.JsonObject;
import net.minecraft.village.TradeOffers;

fun interface IOfferFactoryType<T> where T : TradeOffers.Factory {
    fun deserialize(factory: JsonObject): T
}