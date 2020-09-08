package ml.porez.datapackify.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import ml.porez.datapackify.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CookingRecipeSerializer.class)
public class CookingRecipeSerializerMixin {
    @Shadow @Final private int cookingTime;

    @Shadow @Final private CookingRecipeSerializer.RecipeFactory<?> recipeFactory;

    /**
     * @reason Needs to replace logic in middle
     * @author BasiqueEvangelist
     */
    @Overwrite
    public AbstractCookingRecipe read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");
        JsonElement el = JsonUtils.INSTANCE.get(json, "ingredient");
        if (!el.isJsonArray() && !el.isJsonObject()) {
            throw new JsonSyntaxException("ingredient must be array or object");
        }
        Ingredient in = Ingredient.fromJson(el);
        ItemStack result = JsonUtils.INSTANCE.makeItemStack(JsonUtils.INSTANCE.get(json, "result"));
        float exp = JsonHelper.getFloat(json, "experience", 0.F);
        int cookingTime = JsonHelper.getInt(json, "cookingtime", this.cookingTime);

        return this.recipeFactory.create(id, group, in, result, exp, cookingTime);
    }
}
