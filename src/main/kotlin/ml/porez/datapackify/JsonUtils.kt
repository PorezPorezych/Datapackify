package ml.porez.datapackify

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import ml.porez.datapackify.trades.VillagerTrades
import net.minecraft.item.ItemStack
import net.minecraft.nbt.StringNbtReader
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry

object JsonUtils {
    fun get(obj: JsonObject, name: String): JsonElement {
        return obj.get(name) ?: throw IllegalArgumentException("Missing $name");
    }

    fun <T> getRegistryItem(reg: Registry<T>, id: String): T {
        return reg.getOrEmpty(Identifier(id)).orElseThrow {
            IllegalArgumentException("'$id' is not a member of registry ${reg.key.value}")
        }
    }

    fun makeItemStack(from: JsonElement): ItemStack {
        return if (from.isJsonPrimitive) ItemStack(JsonHelper.asItem(from, "<item stack>")) else {
            val obj = JsonHelper.asObject(from, "<item stack>")
            var ist = ItemStack(
                    JsonHelper.getItem(obj, "item"),
                    JsonHelper.getInt(obj, "count", 1)
            )
            if (obj.has("data")) {
                val tag = JsonHelper.getString(obj, "data")
                try {
                    ist.setTag(StringNbtReader(StringReader(tag)).parseCompoundTag())
                } catch (cse: CommandSyntaxException) {
                    throw java.lang.IllegalArgumentException(cse)
                }
            }
            ist
        }
    }
}