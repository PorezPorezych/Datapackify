package me.basiqueevangelist.datapackify.nbt;

import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier

object ItemNbtConstructor {
    fun fromJson(obj: JsonObject): CompoundTag {
        val tag = CompoundTag()
        for ((idStr, data) in obj.entrySet()) {
            if (idStr == "item" || idStr == "count")
                continue

            val id = Identifier(idStr)
            val transf = NbtTransformers.REGISTRY.getOrEmpty(id).orElseThrow {
                IllegalArgumentException("Invalid NBT transformer '$id'")
            }
            transf.transform(data, tag)
        }
        return tag
    }
}
