package ml.porez.datapackify.mixins;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
    @Inject(method = "craftRecipe(Lnet/minecraft/recipe/Recipe;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;increment(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void craftRecipe(Recipe<?> r, CallbackInfo cb, ItemStack input, ItemStack recipeOutput, ItemStack output) {
        output.increment(recipeOutput.getCount() - 1);
    }

    @Inject(method = "canAcceptRecipeOutput(Lnet/minecraft/recipe/Recipe;)Z", at=@At(value = "INVOKE", target="Lnet/minecraft/item/ItemStack;isItemEqualIgnoreDamage(Lnet/minecraft/item/ItemStack;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void canAcceptRecipeOutput(Recipe<?> recipe, CallbackInfoReturnable<Boolean> cir, ItemStack recipeOut, ItemStack furnaceOut) {
        if (furnaceOut.getCount() + recipeOut.getCount() > furnaceOut.getMaxCount())
            cir.setReturnValue(false);
    }
}