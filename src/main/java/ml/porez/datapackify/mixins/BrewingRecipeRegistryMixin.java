package ml.porez.datapackify.mixins;

import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {
    /**
     * @reason Literally just removes all vanilla logic.
     * @author BasiqueEvangelist
     */
    @Overwrite
    public static void registerDefaults() { }
}
