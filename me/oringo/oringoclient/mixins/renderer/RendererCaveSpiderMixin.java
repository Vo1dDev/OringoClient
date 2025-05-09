package me.oringo.oringoclient.mixins.renderer;

import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCaveSpider;
import net.minecraft.entity.monster.EntityCaveSpider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderCaveSpider.class})
public class RendererCaveSpiderMixin {
   @Inject(
      method = {"preRenderCallback(Lnet/minecraft/entity/monster/EntityCaveSpider;F)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <T extends EntityCaveSpider> void onPreRenderCallback(T entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
      if (OringoClient.giants.isToggled() && OringoClient.giants.mobs.isEnabled()) {
         GlStateManager.func_179139_a(OringoClient.giants.scale.getValue(), OringoClient.giants.scale.getValue(), OringoClient.giants.scale.getValue());
      }

   }
}
