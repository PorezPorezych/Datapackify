package me.basiqueevangelist.datapackify

import com.google.gson.JsonElement
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringNbtReader
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry

fun interface ItemStackTransformer {
    fun transform(data: JsonElement, stack: ItemStack)
}

object ItemStackTransformers : ItemStackTransformer {
    val REGISTRY = FabricRegistryBuilder.createSimple(ItemStackTransformer::class.java, Identifier(Datapackify.NAMESPACE, "item_stack_transformers")).buildAndRegister()

    val RAW_NBT = Registry.register(REGISTRY, Identifier("nbt"), ItemStackTransformer { data, stack ->
        val newTag = try {
            StringNbtReader.parse(JsonHelper.asString(data, "minecraft:nbt"))
        } catch (cse: CommandSyntaxException) {
            throw IllegalArgumentException("Could not parse NBT tag: $cse")
        }

        stack.orCreateTag.copyFrom(newTag)
    })

    val DURABILITY = Registry.register(REGISTRY, Identifier("durability"), ItemStackTransformer { data, stack ->
         stack.damage = stack.maxDamage - JsonHelper.asInt(data, "minecraft:durability")
    })

    override fun transform(data: JsonElement, stack: ItemStack) {
        for ((idStr, trData) in JsonHelper.asObject(data, "<item stack>").entrySet()) {
            if (idStr == "item" || idStr == "count")
                continue

            val id = Identifier(idStr)
            val transf = REGISTRY.getOrEmpty(id).orElseThrow {
                IllegalArgumentException("Invalid NBT transformer '$id'")
            }
            transf.transform(trData, stack)
        }
    }
}
