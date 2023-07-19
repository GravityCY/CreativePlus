package me.gravityio.creativeplus.api.nbt.frame.living.mob.passive.merchant;

import net.minecraft.village.TradeOfferList;

public interface MerchantFrame {
    TradeOfferList getTradeOffers();

    void setTradeOffers(TradeOfferList tradeOffers);
}
