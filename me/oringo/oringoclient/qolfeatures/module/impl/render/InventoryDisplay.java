package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.events.GuiChatEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.hud.DraggableComponent;
import me.oringo.oringoclient.ui.hud.impl.InventoryHUD;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InventoryDisplay extends Module {
   public NumberSetting x = new NumberSetting("X1234", 0.0D, -100000.0D, 100000.0D, 1.0E-5D, (a) -> {
      return true;
   });
   public NumberSetting y = new NumberSetting("Y1234", 0.0D, -100000.0D, 100000.0D, 1.0E-5D, (a) -> {
      return true;
   });
   public ModeSetting blurStrength = new ModeSetting("Blur Strength", "Low", new String[]{"None", "Low", "High"});

   public InventoryDisplay() {
      super("Inventory HUD", 0, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.x, this.y, this.blurStrength});
   }

   @SubscribeEvent
   public void onRender(Post event) {
      if (this.isToggled() && event.type.equals(ElementType.HOTBAR) && mc.field_71439_g != null) {
         InventoryHUD.inventoryHUD.drawScreen();
      }

   }

   @SubscribeEvent
   public void onChatEvent(GuiChatEvent event) {
      if (this.isToggled()) {
         DraggableComponent component = InventoryHUD.inventoryHUD;
         if (event instanceof GuiChatEvent.MouseClicked) {
            if (component.isHovered(event.mouseX, event.mouseY)) {
               component.startDragging();
            }
         } else if (event instanceof GuiChatEvent.MouseReleased) {
            component.stopDragging();
         } else if (event instanceof GuiChatEvent.Closed) {
            component.stopDragging();
         } else if (event instanceof GuiChatEvent.DrawChatEvent) {
         }

      }
   }
}
