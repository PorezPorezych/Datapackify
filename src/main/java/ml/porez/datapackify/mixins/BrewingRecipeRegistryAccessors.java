package ml.porez.datapackify.mixins;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public interface BrewingRecipeRegistryAccessors {
    @Accessor(value = "ITEM_RECIPES")
    public static List<BrewingRecipeRegistry.Recipe<Item>> getItemRecipes() {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }

    @Accessor(value = "POTION_RECIPES")
    public static List<BrewingRecipeRegistry.Recipe<Potion>> getPotionRecipes() {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }

    @Accessor(value = "POTION_TYPES")
    public static List<Ingredient> getPotionTypes() {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }

    @Accessor(value = "ITEM_RECIPES")
    public static void setItemRecipes(List<BrewingRecipeRegistry.Recipe<Item>> value) {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }

    @Accessor(value = "POTION_RECIPES")
    public static void setPotionRecipes(List<BrewingRecipeRegistry.Recipe<Potion>> value) {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }

    @Accessor(value = "POTION_TYPES")
    public static void setPotionTypes(List<Ingredient> value) {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }
}