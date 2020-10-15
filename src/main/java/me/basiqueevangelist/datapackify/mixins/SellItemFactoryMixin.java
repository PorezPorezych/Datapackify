package me.basiqueevangelist.datapackify.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(TradeOffers.SellItemFactory.class)
public class SellItemFactoryMixin {
    @Shadow @Final private ItemStack sell;

    @Shadow @Final private int count;

    @Shadow @Final private int price;

    @Shadow @Final private int maxUses;

    @Shadow @Final private int experience;

    @Shadow @Final private float multiplier;

    /**
     * @reason Gave up trying to do this with Redirect.
     * @author BasiqueEvangelist
     */
    @Overwrite
    public TradeOffer create(Entity e, Random r) {
        ItemStack newIs = this.sell.copy();
        newIs.setCount(this.count * newIs.getCount());
        return new TradeOffer(new ItemStack(Items.EMERALD, price), newIs, maxUses, experience, multiplier);
    }
}
