package ml.porez.datapackify.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import ml.porez.datapackify.trades.MainItemSellAcc;
import ml.porez.datapackify.trades.MultiplierAcc;
import ml.porez.datapackify.trades.SecondaryItemSellAcc;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

public class TradeOffersAccessors {
    @Mixin(TradeOffers.class)
    public interface GlobalAcc {
        @Accessor("PROFESSION_TO_LEVELED_TRADE")
        static void setTrades(Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> map) {
            throw new UnsupportedOperationException("Mixin failed to apply");
        }

        @Accessor("WANDERING_TRADER_TRADES")
        static void setWanderingTrades(Int2ObjectMap<TradeOffers.Factory[]> map) {
            throw new UnsupportedOperationException("Mixin failed to apply");
        }
    }

    @Mixin(TradeOffers.SellEnchantedToolFactory.class)
    public static class SellEnchantedToolFactoryMixin implements MainItemSellAcc {
        @Mutable @Shadow @Final private ItemStack tool;

        @Override
        public void setMainStack(ItemStack is) {
            this.tool = is;
        }
    }

    @Mixin(TradeOffers.SellSuspiciousStewFactory.class)
    public static class SellSuspiciousStewFactoryMixin implements MultiplierAcc {
        @Mutable @Shadow @Final private float multiplier;

        @Override
        public void setMultiplier(float mul) {
            this.multiplier = mul;
        }
    }

    @Mixin(TradeOffers.ProcessItemFactory.class)
    public static class ProcessItemFactoryMixin implements MultiplierAcc, MainItemSellAcc, SecondaryItemSellAcc {
        @Mutable @Shadow @Final private float multiplier;

        @Mutable @Shadow @Final private ItemStack sell;

        @Mutable @Shadow @Final private ItemStack secondBuy;

        @Override
        public void setMultiplier(float mul) {
            this.multiplier = mul;
        }

        @Override
        public void setMainStack(ItemStack is) {
            sell = is;
        }

        @Override
        public void setSecondaryStack(ItemStack is) {
            secondBuy = is;
        }
    }

    @Mixin(TradeOffers.SellPotionHoldingItemFactory.class)
    public static class SellPotionHoldingItemFactoryMixin implements MainItemSellAcc {
        @Mutable @Shadow @Final private ItemStack sell;

        @Override
        public void setMainStack(ItemStack is) {
            sell = is;
        }
    }
}
