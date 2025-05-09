package me.oringo.oringoclient.mixins.renderer;

import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderPlayer.class})
public abstract class MixinRenderPlayer extends MixinRenderLivingEntity {
   @Inject(
      method = {"preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},
      at = {@At("HEAD")}
   )
   public void onPreRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
      if (OringoClient.giants.isToggled() && OringoClient.giants.players.isEnabled()) {
         GlStateManager.func_179139_a(OringoClient.giants.scale.getValue(), OringoClient.giants.scale.getValue(), OringoClient.giants.scale.getValue());
      }

   }
}
