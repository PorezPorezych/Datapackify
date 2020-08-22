package ml.porez.datapackify;

import com.google.gson.JsonObject;
import net.minecraft.village.TradeOffers;

public interface IOfferFactoryType<T extends TradeOffers.Factory> {
    T deserialize(JsonObject factory);
}
