package ml.porez.datapackify.mixins;

import com.google.gson.JsonObject;
import ml.porez.datapackify.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
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