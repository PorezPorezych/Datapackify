package ml.porez.datapackify.trades

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import ml.porez.datapackify.Datapackify
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.map.MapIcon
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import net.minecraft.village.TradeOffers.*
import net.minecraft.village.VillagerType
import net.minecraft.world.gen.feature.StructureFeature
import java.util.*

object VillagerTrades {
    var REGISTRY = FabricRegistryBuilder.createDefaulted(IOfferFactoryType::class.java, Identifier(Datapackify.NAMESPACE, "villager_trades"), Identifier(Datapackify.NAMESPACE, "empty")).buildAndRegister()
    private val VILLAGER_TRADES = VillagerTradeManager()

    fun init() {


        register(Datapackify.NAMESPACE + "empty") { _ -> Factory { _, _ -> null } }
        register("minecraft:buy_for_one_emerald") { obj ->
            BuyForOneEmeraldFactory(
                    getItem(JsonHelper.getString(obj, "buy")),
                    JsonHelper.getInt(obj, "price"),
                    JsonHelper.getInt(obj, "max_uses"),
                    JsonHelper.getInt(obj, "experience")
            )
        }
        register("minecraft:sell_item") { obj ->
            SellItemFactory(
                    getItemStack(get(obj, "sell")),
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
            fac.tool = getItemStack(get(obj, "tool"))
            fac
        }
        register("minecraft:sell_map") { obj ->
            SellMapFactory(
                    JsonHelper.getInt(obj, "price"),
                    getStructureFeature(JsonHelper.getString(obj, "structure")),
                    getMapIconType(JsonHelper.getString(obj, "icon_type")),
                    JsonHelper.getInt(obj, "max_uses"),
                    JsonHelper.getInt(obj, "experience")
            )
        }
        register("minecraft:sell_suspicious_stew") { obj ->
            val fac = SellSuspiciousStewFactory(
                    getStatusEffect(JsonHelper.getString(obj, "effect")),
                    JsonHelper.getInt(obj, "duration"),
                    JsonHelper.getInt(obj, "experience")
            )
            fac.multiplier = JsonHelper.getFloat(obj, "multiplier", 0.05f)
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
            fac.multiplier = JsonHelper.getFloat(obj, "multiplier", 0.05f)
            fac.secondBuy = getItemStack(get(obj, "second_buy"))
            fac.sell = getItemStack(get(obj, "sell"))
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
                    getItem(JsonHelper.getString(obj, "second_buy")),
                    JsonHelper.getInt(obj, "second_count"),
                    Items.STONE,
                    JsonHelper.getInt(obj, "sell_count"),
                    JsonHelper.getInt(obj, "price"),
                    JsonHelper.getInt(obj, "max_uses"),
                    JsonHelper.getInt(obj, "experience")
            )
            fac.sell = getItemStack(get(obj, "sell"))
            fac
        }
        register("minecraft:sell_dyed_armor") { obj ->
            SellDyedArmorFactory(
                    getItem(JsonHelper.getString(obj, "sell")),
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

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(VILLAGER_TRADES)
    }

    private fun <T> register(name: String, fac: IOfferFactoryType<T>) where T: Factory {
        Registry.register(REGISTRY, Identifier(name), fac);
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

    private operator fun get(o: JsonObject, element: String): JsonElement {
        return if (o.has(element)) {
            o[element]
        } else {
            throw JsonSyntaxException("Missing $element")
        }
    }

    private fun typeAwareItemMap(obj: JsonObject): kotlin.collections.Map<VillagerType, Item>? {
        val map: MutableMap<VillagerType, Item> = HashMap()
        for ((key, value) in obj.entrySet()) {
            val res = Registry.VILLAGER_TYPE.getOrEmpty(Identifier(key))
            require(res.isPresent) { "Invalid villager type $key" }
            map[res.get()] = getItem(JsonHelper.asString(value, "item"))
        }
        return map
    }

    private fun getItem(name: String): Item {
        val res = Registry.ITEM.getOrEmpty(Identifier(name))
        require(res.isPresent) { "Invalid item $name" }
        return res.get()
    }

    private fun getStatusEffect(name: String): StatusEffect? {
        val res = Registry.STATUS_EFFECT.getOrEmpty(Identifier(name))
        require(res.isPresent) { "Invalid status effect $name" }
        return res.get()
    }

    private fun getStructureFeature(name: String): StructureFeature<*>? {
        val res = Registry.STRUCTURE_FEATURE.getOrEmpty(Identifier(name))
        require(res.isPresent) { "Invalid structure feature $name" }
        return res.get()
    }

    private fun getItemStack(el: JsonElement): ItemStack? {
        return if (el.isJsonPrimitive) ItemStack(getItem(el.asString)) else {
            val obj = el.asJsonObject
            ItemStack(
                    getItem(JsonHelper.getString(obj, "item")),
                    JsonHelper.getInt(obj, "count")
            )
        }
    }
}
