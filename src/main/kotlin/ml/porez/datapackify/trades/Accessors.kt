package ml.porez.datapackify.trades

import net.minecraft.item.ItemStack

interface MainItemSellAcc {
    fun setMainStack(`is`: ItemStack?)
}

interface SecondaryItemSellAcc {
    fun setSecondaryStack(`is`: ItemStack?)
}

interface MultiplierAcc {
    fun setMultiplier(mul: Float)
}