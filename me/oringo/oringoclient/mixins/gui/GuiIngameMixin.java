package me.oringo.oringoclient.mixins.gui;

import me.oringo.oringoclient.events.ScoreboardRenderEvent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public abstract class GuiIngameMixin {
   @Shadow
   public abstract FontRenderer func_175179_f();

   @Inject(
      method = {"renderScoreboard"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderScoreboard(ScoreObjective s, ScaledResolution score, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new ScoreboardRenderEvent(s, score))) {
         ci.cancel();
      }

   }
}
