package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.GuiChatEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.hud.impl.TargetComponent;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TargetHUD extends Module {
   public static TargetHUD instance = new TargetHUD();
   public ModeSetting blurStrength = new ModeSetting("Blur Strength", "Low", new String[]{"None", "Low", "High"});
   public BooleanSetting targetESP = new BooleanSetting("Target ESP", true);
   public NumberSetting x = new NumberSetting("X123", 0.0D, -100000.0D, 100000.0D, 1.0E-5D, (a) -> {
      return true;
   });
   public NumberSetting y = new NumberSetting("Y123", 0.0D, -100000.0D, 100000.0D, 1.0E-5D, (a) -> {
      return true;
   });

   public static TargetHUD getInstance() {
      return instance;
   }

   public TargetHUD() {
      super("Target HUD", Module.Category.RENDER);
      this.setToggled(true);
      this.addSettings(new Setting[]{this.targetESP, this.blurStrength, this.x, this.y});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (KillAura.target != null && KillAura.target.func_110143_aJ() > 0.0F && !KillAura.target.field_70128_L && this.targetESP.isEnabled() && this.isToggled()) {
         RenderUtils.drawTargetESP(KillAura.target, OringoClient.clickGui.getColor(), event.partialTicks);
      }

   }

   @SubscribeEvent
   public void onChatEvent(GuiChatEvent event) {
      if (this.isToggled()) {
         TargetComponent component = TargetComponent.INSTANCE;
         if (event instanceof GuiChatEvent.MouseClicked) {
            if (component.isHovered(event.mouseX, event.mouseY)) {
               component.startDragging();
            }
         } else if (event instanceof GuiChatEvent.MouseReleased || event instanceof GuiChatEvent.Closed) {
            component.stopDragging();
         }

      }
   }

   @SubscribeEvent
   public void onRender(Pre event) {
      if (this.isToggled() && event.type == ElementType.CROSSHAIRS) {
         TargetComponent.INSTANCE.drawScreen();
      }

   }
}
