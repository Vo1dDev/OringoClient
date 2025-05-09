package me.oringo.oringoclient.mixins.entity;

import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayer.class})
public abstract class AbstractClientPlayerMixin extends PlayerMixin {
   private static ResourceLocation getCape(String uuid) {
      return (ResourceLocation)OringoClient.capes.get(DigestUtils.sha256Hex(uuid));
   }

   @Inject(
      method = {"getLocationCape"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
      ResourceLocation minecons = getCape(((AbstractClientPlayer)this).func_110124_au().toString());
      if (minecons != null) {
         cir.setReturnValue(minecons);
      }

   }
}
