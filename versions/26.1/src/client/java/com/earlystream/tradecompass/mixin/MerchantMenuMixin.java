package com.earlystream.tradecompass.mixin;

import com.earlystream.tradecompass.capture.TradeCaptureService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin {
    @Inject(method = "setOffers", at = @At("TAIL"))
    private void tradecompass$captureOffers(MerchantOffers offers, CallbackInfo ci) {
        TradeCaptureService.captureMenu(Minecraft.getInstance(), (MerchantMenu) (Object) this, offers);
    }
}
