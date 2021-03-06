package me.basiqueevangelist.datapackify.trades

import com.google.gson.JsonObject
import me.basiqueevangelist.datapackify.Datapackify
import me.basiqueevangelist.datapackify.JsonUtils
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.map.MapIcon
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import net.minecraft.village.TradeOffers.*
import net.minecraft.village.VillagerType
import java.util.*

object VillagerTrades {
    var REGISTRY = FabricRegistryBuilder.createDefaulted(
        IOfferFactoryType::class.java,
        Identifier(Datapackify.NAMESPACE, "villager_trades"),
        Identifier(Datapackify.NAMESPACE, "empty")
    ).buildAndRegister()
    private val VILLAGER_TRADES = VillagerTradeManager()

    fun init() {
        register(Datapackify.NAMESPACE + "empty") { _ -> Factory { _, _ -> null } }
        register("minecraft:buy_for_one_emerald") { obj ->
            BuyForOneEmeraldFactory(
                JsonHelper.getItem(obj, "buy"),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        }
        register("minecraft:sell_item") { obj ->
            SellItemFactory(
                JsonUtils.makeItemStack(JsonUtils.get(obj, "sell")),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                JsonHelper.getFloat(obj, "multiplier", 0.05f)
            )
        }

        register("minecraft:sell_enchanted_tool") { obj ->
            val fac = SellEnchantedToolFactory(
                Items.STONE,
                JsonHelper.getInt(obj, "base_price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                JsonHelper.getFloat(obj, "multiplier", 0.05f)
            )
            (fac as MainItemSellAcc).setMainStack(JsonUtils.makeItemStack(JsonUtils.get(obj, "tool")))
            fac
        }
        register("minecraft:sell_map") { obj ->
            SellMapFactory(
                JsonHelper.getInt(obj, "price"),
                JsonUtils.getRegistryItem(Registry.STRUCTURE_FEATURE, JsonHelper.getString(obj, "structure")),
                getMapIconType(JsonHelper.getString(obj, "icon_type")),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        }
        register("minecraft:sell_suspicious_stew") { obj ->
            val fac = SellSuspiciousStewFactory(
                JsonUtils.getRegistryItem(Registry.STATUS_EFFECT, JsonHelper.getString(obj, "effect")),
                JsonHelper.getInt(obj, "duration"),
                JsonHelper.getInt(obj, "experience")
            )
            (fac as MultiplierAcc).setMultiplier(JsonHelper.getFloat(obj, "multiplier", 0.05f))
            fac
        }
        register("minecraft:process_item") { obj ->
            val fac = ProcessItemFactory(
                Items.STONE,
                JsonHelper.getInt(obj, "second_count"),
                JsonHelper.getInt(obj, "price"),
                Items.STONE,
                JsonHelper.getInt(obj, "sell_count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
            (fac as MultiplierAcc).setMultiplier(JsonHelper.getFloat(obj, "multiplier", 0.05f))
            (fac as SecondaryItemSellAcc).setSecondaryStack(JsonUtils.makeItemStack(JsonUtils.get(obj, "second_buy")))
            (fac as MainItemSellAcc).setMainStack( JsonUtils.makeItemStack(JsonUtils.get(obj, "sell")))
            fac
        }
        register("minecraft:type_aware_buy_for_one_emerald") { obj ->
            TypeAwareBuyForOneEmeraldFactory(
                JsonHelper.getInt(obj, "count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                typeAwareItemMap(JsonHelper.getObject(obj, "map"))
            )
        }
        register("minecraft:sell_potion_holding_item") { obj ->
            val fac = SellPotionHoldingItemFactory(
                JsonHelper.getItem(obj, "second_buy"),
                JsonHelper.getInt(obj, "second_count"),
                Items.STONE,
                JsonHelper.getInt(obj, "sell_count"),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
            (fac as MainItemSellAcc).setMainStack(JsonUtils.makeItemStack(JsonUtils.get(obj, "sell")))
            fac
        }
        register("minecraft:sell_dyed_armor") { obj ->
            SellDyedArmorFactory(
                JsonHelper.getItem(obj, "sell"),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        }
        register("minecraft:enchant_book") { obj ->
            EnchantBookFactory(
                JsonHelper.getInt(obj, "experience")
            )
        }
        register("datapackify:generic", GenericTradeOfferFactory::parse)

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(VILLAGER_TRADES)
    }

    private fun <T> register(name: String, fac: IOfferFactoryType<T>) where T : Factory {
        Registry.register(REGISTRY, Identifier(name), fac)
    }

    private fun getMapIconType(s: String): MapIcon.Type {
        return when (s) {
            "player" -> MapIcon.Type.PLAYER
            "frame" -> MapIcon.Type.FRAME
            "red_marker" -> MapIcon.Type.RED_MARKER
            "blue_marker" -> MapIcon.Type.BLUE_MARKER
            "target_x" -> MapIcon.Type.TARGET_X
            "target_point" -> MapIcon.Type.TARGET_POINT
            "player_off_map" -> MapIcon.Type.PLAYER_OFF_MAP
            "player_off_limits" -> MapIcon.Type.PLAYER_OFF_LIMITS
            "mansion" -> MapIcon.Type.MANSION
            "monument" -> MapIcon.Type.MONUMENT
            "banner_white" -> MapIcon.Type.BANNER_WHITE
            "banner_orange" -> MapIcon.Type.BANNER_ORANGE
            "banner_magenta" -> MapIcon.Type.BANNER_MAGENTA
            "banner_light_blue" -> MapIcon.Type.BANNER_LIGHT_BLUE
            "banner_yellow" -> MapIcon.Type.BANNER_YELLOW
            "banner_lime" -> MapIcon.Type.BANNER_LIME
            "banner_pink" -> MapIcon.Type.BANNER_PINK
            "banner_gray" -> MapIcon.Type.BANNER_GRAY
            "banner_light_gray" -> MapIcon.Type.BANNER_LIGHT_GRAY
            "banner_cyan" -> MapIcon.Type.BANNER_CYAN
            "banner_purple" -> MapIcon.Type.BANNER_PURPLE
            "banner_blue" -> MapIcon.Type.BANNER_BLUE
            "banner_brown" -> MapIcon.Type.BANNER_BROWN
            "banner_green" -> MapIcon.Type.BANNER_GREEN
            "banner_red" -> MapIcon.Type.BANNER_RED
            "banner_black" -> MapIcon.Type.BANNER_BLACK
            "red_x" -> MapIcon.Type.RED_X
            else -> throw IllegalArgumentException("Invalid map icon type $s")
        }
    }

    private fun typeAwareItemMap(obj: JsonObject): Map<VillagerType, Item>? {
        val map: MutableMap<VillagerType, Item> = HashMap()
        for ((key, value) in obj.entrySet()) {
            val res = JsonUtils.getRegistryItem(Registry.VILLAGER_TYPE, key)
            map[res] = JsonHelper.asItem(value, "item")
        }
        return map
    }
}
