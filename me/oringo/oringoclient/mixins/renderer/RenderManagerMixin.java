package me.oringo.oringoclient.mixins.renderer;

import java.awt.Color;
import me.oringo.oringoclient.qolfeatures.module.impl.macro.AutoSumoBot;
import me.oringo.oringoclient.qolfeatures.module.impl.other.AntiNicker;
import me.oringo.oringoclient.utils.MobRenderUtils;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderManager.class})
public abstract class RenderManagerMixin {
   @Inject(
      method = {"doRenderEntity"},
      at = {@At("HEAD")}
   )
   public void doRenderEntityPre(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_, CallbackInfoReturnable<Boolean> cir) {
      if (entity.equals(AutoSumoBot.target)) {
         MobRenderUtils.setColor(new Color(255, 0, 0, 80));
      }

      if (AntiNicker.nicked.contains(entity.func_110124_au())) {
         RenderUtils.enableChams();
         MobRenderUtils.setColor(new Color(255, 0, 0, 80));
      }

   }

   @Inject(
      method = {"doRenderEntity"},
      at = {@At("RETURN")}
   )
   public void doRenderEntityPost(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_, CallbackInfoReturnable<Boolean> cir) {
      if (entity.equals(AutoSumoBot.target)) {
         MobRenderUtils.unsetColor();
      }

      if (AntiNicker.nicked.contains(entity.func_110124_au())) {
         RenderUtils.disableChams();
         MobRenderUtils.unsetColor();
      }

   }
}
