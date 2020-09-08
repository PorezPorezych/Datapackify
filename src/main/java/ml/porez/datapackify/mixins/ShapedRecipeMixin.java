package ml.porez.datapackify.mixins;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.porez.datapackify.JsonUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    /**
     * @reason Literally just replaces it with JsonUtils.makeItemStack
     * @author BasiqueEvangelist
     */
    @Overwrite
    public static ItemStack getItemStack(JsonObject json) {
        return JsonUtils.INSTANCE.makeItemStack(json);
    }
}