package ml.porez.datapackify;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerType;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class VillagerTradeFactories {
    public static void register_all() {
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier(Datapackify.NAMESPACE, "empty"), (obj) ->
            (entity, random) -> null
        );
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "buy_for_one_emerald"), (obj) ->
            new TradeOffers.BuyForOneEmeraldFactory(
                getItem(JsonHelper.getString(obj, "buy")),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        );
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "sell_item"), (obj) ->
            new TradeOffers.SellItemFactory(
                getItemStack(get(obj, "sell")),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                JsonHelper.getFloat(obj, "multiplier", 0.05F)
            )
        );
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "sell_enchanted_tool"), (obj) -> {
            TradeOffers.SellEnchantedToolFactory fac = new TradeOffers.SellEnchantedToolFactory(
                Items.STONE,
                JsonHelper.getInt(obj, "base_price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                JsonHelper.getFloat(obj, "multiplier", 0.05F)
            );
            fac.tool = getItemStack(get(obj, "tool"));
            return fac;
        });
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "sell_map"), (obj) ->
            new TradeOffers.SellMapFactory(
                JsonHelper.getInt(obj, "price"),
                getStructureFeature(JsonHelper.getString(obj, "structure")),
                getMapIconType(JsonHelper.getString(obj, "icon_type")),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        );
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "sell_suspicious_stew"), (obj) -> {
            TradeOffers.SellSuspiciousStewFactory fac = new TradeOffers.SellSuspiciousStewFactory(
                getStatusEffect(JsonHelper.getString(obj, "effect")),
                JsonHelper.getInt(obj, "duration"),
                JsonHelper.getInt(obj, "experience")
            );
            fac.multiplier = JsonHelper.getFloat(obj, "multiplier", 0.05F);
            return fac;
        });
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "process_item"), (obj) -> {
            TradeOffers.ProcessItemFactory fac = new TradeOffers.ProcessItemFactory(
                Items.STONE,
                JsonHelper.getInt(obj, "second_count"),
                JsonHelper.getInt(obj, "price"),
                Items.STONE,
                JsonHelper.getInt(obj, "sell_count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            );
            fac.multiplier = JsonHelper.getFloat(obj, "multiplier", 0.05F);
            fac.secondBuy = getItemStack(get(obj, "second_buy"));
            fac.sell = getItemStack(get(obj, "sell"));
            return fac;
        });
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "type_aware_buy_for_one_emerald"), (obj) ->
            new TradeOffers.TypeAwareBuyForOneEmeraldFactory(
                JsonHelper.getInt(obj, "count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                typeAwareItemMap(JsonHelper.getObject(obj, "map"))
            )
        );
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "sell_potion_holding_item"), (obj) -> {
            TradeOffers.SellPotionHoldingItemFactory fac = new TradeOffers.SellPotionHoldingItemFactory(
                getItem(JsonHelper.getString(obj, "second_buy")),
                JsonHelper.getInt(obj, "second_count"),
                Items.STONE,
                JsonHelper.getInt(obj, "sell_count"),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            );
            fac.sell = getItemStack(get(obj, "sell"));
            return fac;
        });
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "sell_dyed_armor"), (obj) ->
            new TradeOffers.SellDyedArmorFactory(
                getItem(JsonHelper.getString(obj, "sell")),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        );
        Registry.register(Datapackify.TRADE_OFFERS, new Identifier("minecraft", "enchant_book"), (obj) ->
            new TradeOffers.EnchantBookFactory(
                JsonHelper.getInt(obj, "experience")
            )
        );
    }

    private static MapIcon.Type getMapIconType(String s) {
        switch (s) {
            case "player":
                return MapIcon.Type.PLAYER;
            case "frame":
                return MapIcon.Type.FRAME;
            case "red_marker":
                return MapIcon.Type.RED_MARKER;
            case "blue_marker":
                return MapIcon.Type.BLUE_MARKER;
            case "target_x":
                return MapIcon.Type.TARGET_X;
            case "target_point":
                return MapIcon.Type.TARGET_POINT;
            case "player_off_map":
                return MapIcon.Type.PLAYER_OFF_MAP;
            case "player_off_limits":
                return MapIcon.Type.PLAYER_OFF_LIMITS;
            case "mansion":
                return MapIcon.Type.MANSION;
            case "monument":
                return MapIcon.Type.MONUMENT;
            case "banner_white":
                return MapIcon.Type.BANNER_WHITE;
            case "banner_orange":
                return MapIcon.Type.BANNER_ORANGE;
            case "banner_magenta":
                return MapIcon.Type.BANNER_MAGENTA;
            case "banner_light_blue":
                return MapIcon.Type.BANNER_LIGHT_BLUE;
            case "banner_yellow":
                return MapIcon.Type.BANNER_YELLOW;
            case "banner_lime":
                return MapIcon.Type.BANNER_LIME;
            case "banner_pink":
                return MapIcon.Type.BANNER_PINK;
            case "banner_gray":
                return MapIcon.Type.BANNER_GRAY;
            case "banner_light_gray":
                return MapIcon.Type.BANNER_LIGHT_GRAY;
            case "banner_cyan":
                return MapIcon.Type.BANNER_CYAN;
            case "banner_purple":
                return MapIcon.Type.BANNER_PURPLE;
            case "banner_blue":
                return MapIcon.Type.BANNER_BLUE;
            case "banner_brown":
                return MapIcon.Type.BANNER_BROWN;
            case "banner_green":
                return MapIcon.Type.BANNER_GREEN;
            case "banner_red":
                return MapIcon.Type.BANNER_RED;
            case "banner_black":
                return MapIcon.Type.BANNER_BLACK;
            case "red_x":
                return MapIcon.Type.RED_X;
            default:
                throw new IllegalArgumentException("Invalid map icon type " + s);
        }
    }

    private static JsonElement get(JsonObject object, String element) {
        if (object.has(element)) {
            return object.get(element);
        } else {
            throw new JsonSyntaxException("Missing " + element + "");
        }
    }

    private static Map<VillagerType, Item> typeAwareItemMap(JsonObject obj) {
        Map<VillagerType, Item> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            Optional<VillagerType> res = Registry.VILLAGER_TYPE.getOrEmpty(new Identifier(e.getKey()));
            if (!res.isPresent()) {
                throw new IllegalArgumentException("Invalid villager type " + e.getKey());
            }
            map.put(res.get(), getItem(JsonHelper.asString(e.getValue(), "item")));
        }
        return map;
    }

    private static Item getItem(String name) {
        Optional<Item> res = Registry.ITEM.getOrEmpty(new Identifier(name));
        if (!res.isPresent()) {
            throw new IllegalArgumentException("Invalid item " + name);
        }
        return res.get();
    }

    private static StatusEffect getStatusEffect(String name) {
        Optional<StatusEffect> res = Registry.STATUS_EFFECT.getOrEmpty(new Identifier(name));
        if (!res.isPresent()) {
            throw new IllegalArgumentException("Invalid status effect " + name);
        }
        return res.get();
    }

    private static StructureFeature<?> getStructureFeature(String name) {
        Optional<StructureFeature<?>> res = Registry.STRUCTURE_FEATURE.getOrEmpty(new Identifier(name));
        if (!res.isPresent()) {
            throw new IllegalArgumentException("Invalid structure feature " + name);
        }
        return res.get();
    }

    private static ItemStack getItemStack(JsonElement el) {
        if (el.isJsonPrimitive())
            return new ItemStack(getItem(el.getAsString()));
        else {
            JsonObject obj = el.getAsJsonObject();
            return new ItemStack(
                getItem(JsonHelper.getString(obj, "item")),
                JsonHelper.getInt(obj, "count")
            );
        }
    }
}
