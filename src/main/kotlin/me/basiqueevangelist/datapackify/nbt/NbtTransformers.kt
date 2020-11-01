package me.basiqueevangelist.datapackify.nbt

import com.google.gson.JsonElement
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.basiqueevangelist.datapackify.Datapackify
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringNbtReader
import net.minecraft.predicate.NumberRange
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

fun interface NbtTransformer {
    fun transform(data: JsonElement, tag: CompoundTag)
}

object NbtTransformers {
    val REGISTRY = FabricRegistryBuilder.createSimple(NbtTransformer::class.java, Identifier(Datapackify.NAMESPACE, "nbt_transformers")).buildAndRegister()

    val RAW_NBT = Registry.register(REGISTRY, Identifier("nbt"), NbtTransformer { data: JsonElement, tag: CompoundTag ->
        val newTag = try {
            StringNbtReader.parse(data.asString)
        } catch (cse: CommandSyntaxException) {
            throw IllegalArgumentException("Could not parse NBT tag: $cse")
        }

        tag.copyFrom(newTag)
    })

    val DAMAGE = Registry.register(REGISTRY, Identifier("damage"), NbtTransformer { data, tag ->
        tag.putInt("Damage", data.asInt)
    })
}
