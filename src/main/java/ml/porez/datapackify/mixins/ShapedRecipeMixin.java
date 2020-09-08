package ml.porez.datapackify.mixins;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
     * @reason Needs to remove condition.
     * @author BasiqueEvangelist
     */
    @Overwrite
    public static ItemStack getItemStack(JsonObject json) {
        String iname = JsonHelper.getString(json, "item");
        Item it = Registry.ITEM.getOrEmpty(new Identifier(iname)).orElseThrow(() -> new JsonSyntaxException("Invalid item '" + iname + "'"));
        ItemStack is = new ItemStack(it);
        is.setCount(JsonHelper.getInt(json, "count", 1));
        if (json.has("data")) {
            String tag = JsonHelper.getString(json, "data");
            try {
                is.setTag(new StringNbtReader(new StringReader(tag)).parseCompoundTag());
            }
            catch (CommandSyntaxException cse) {
                throw new IllegalArgumentException(cse);
            }
        }
        return is;
    }
}