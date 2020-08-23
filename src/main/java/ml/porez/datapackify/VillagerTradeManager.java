package ml.porez.datapackify;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class VillagerTradeManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger("Datapackify/VillagerTradeManager");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public VillagerTradeManager() {
        super(GSON, "villager_trades");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Datapackify.NAMESPACE, "villager_trades");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        Map<VillagerProfession, Map<Integer, List<TradeOffers.Factory>>> trades = new HashMap<>();
        for (VillagerProfession prof : Registry.VILLAGER_PROFESSION) {
            trades.put(prof, new Int2ObjectOpenHashMap<>());
        }
        for (Map.Entry<Identifier, JsonElement> el : loader.entrySet()) {
            LOGGER.debug("Got {}", el.getKey());
            try {
                Identifier profId = new Identifier(
                    JsonHelper.getString(JsonHelper.asObject(el.getValue(), "<file>"),
                        "profession")
                );
                Optional<VillagerProfession> profession = Registry.VILLAGER_PROFESSION.getOrEmpty(profId);
                if (!profession.isPresent()) {
                    throw new IllegalArgumentException("Invalid profession " + profId.toString());
                }
                Map.Entry<Integer, TradeOffers.Factory[]> e = parseFile(el.getValue());
                if (trades.get(profession.get()).containsKey(e.getKey()))
                    trades.get(profession.get()).get(e.getKey()).addAll(Arrays.asList(e.getValue()));
                else
                    trades.get(profession.get()).put(e.getKey(), new ArrayList<>(Arrays.asList(e.getValue())));
            } catch (IllegalArgumentException | JsonParseException e) {
                LOGGER.error("Encountered error while parsing {}: {}", el.getKey(), e);
                e.printStackTrace();
            }
        }
        LOGGER.info("Loaded {} villager trades.", trades.size());
        Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> finalMap = new HashMap<>();
        for (Map.Entry<VillagerProfession, Map<Integer, List<TradeOffers.Factory>>> el : trades.entrySet()) {
            Int2ObjectMap<TradeOffers.Factory[]> profMap = new Int2ObjectOpenHashMap<>();
            for (Map.Entry<Integer, List<TradeOffers.Factory>> profEl : el.getValue().entrySet()) {
                profMap.put(profEl.getKey().intValue(), profEl.getValue().toArray(new TradeOffers.Factory[0]));
            }
            finalMap.put(el.getKey(), profMap);
        }
        TradeOffers.PROFESSION_TO_LEVELED_TRADE = finalMap;
    }

    private Map.Entry<Integer, TradeOffers.Factory[]> parseFile(JsonElement file) {
        JsonObject obj = JsonHelper.asObject(file, "<file>");
        int careerLevel = JsonHelper.getInt(obj, "career_level");
        JsonArray trades = JsonHelper.getArray(obj, "trades");
        TradeOffers.Factory[] procTrades = new TradeOffers.Factory[trades.size()];
        int i = 0;
        for (JsonElement trade : trades) {
            procTrades[i++] = parseTrade(trade);
        }

        return new Map.Entry<Integer, TradeOffers.Factory[]>() {
            @Override
            public Integer getKey() {
                return careerLevel;
            }

            @Override
            public TradeOffers.Factory[] getValue() {
                return procTrades;
            }

            @Override
            public TradeOffers.Factory[] setValue(TradeOffers.Factory[] factories) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private TradeOffers.Factory parseTrade(JsonElement trade) {
        JsonObject obj = JsonHelper.asObject(trade, "trade");
        Identifier type = new Identifier(JsonHelper.getString(obj, "type"));
        Optional<IOfferFactoryType> res = Datapackify.TRADE_OFFERS.getOrEmpty(type);
        if (!res.isPresent()) {
            throw new IllegalArgumentException("Invalid factory type " + type.toString());
        }
        return res.get().deserialize(obj);
    }
}
