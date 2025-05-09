package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.gui.ClickGUI;
import me.oringo.oringoclient.utils.MilliTimer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PopupAnimation extends Module {
   public static MilliTimer animationTimer = new MilliTimer();
   public static MilliTimer lastGuiTimer = new MilliTimer();
   public static BooleanSetting clickGui = new BooleanSetting("Click Gui", true);
   public static BooleanSetting inventory = new BooleanSetting("Inventory", false);
   public static BooleanSetting chests = new BooleanSetting("Chests", false);
   public static NumberSetting startSize = new NumberSetting("Starting size", 0.75D, 0.01D, 1.0D, 0.01D);
   public static NumberSetting time = new NumberSetting("Time", 200.0D, 0.0D, 1000.0D, 10.0D);

   public PopupAnimation() {
      super("Popup Animation", Module.Category.RENDER);
      this.setToggled(true);
      this.addSettings(new Setting[]{clickGui, inventory, chests, startSize, time});
   }

   public static float getScaling() {
      return !animationTimer.hasTimePassed((long)time.getValue()) ? (float)((double)animationTimer.getTimePassed() / time.getValue() * (1.0D - startSize.getValue()) + startSize.getValue()) : 1.0F;
   }

   public static boolean shouldScale(GuiScreen gui) {
      return OringoClient.popupAnimation.isToggled() && (gui instanceof ClickGUI && clickGui.isEnabled() || gui instanceof GuiInventory && inventory.isEnabled() || gui instanceof GuiChest && chests.isEnabled());
   }

   @SubscribeEvent
   public void onTick(RenderWorldLastEvent event) {
      if (mc.field_71462_r != null) {
         lastGuiTimer.reset();
      }

   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      if (mc.field_71462_r == null && lastGuiTimer.hasTimePassed(150L)) {
         animationTimer.reset();
      }

   }

   public static void doScaling() {
      float scaling = getScaling();
      ScaledResolution res = new ScaledResolution(mc);
      GL11.glTranslated((double)res.func_78326_a() / 2.0D, (double)res.func_78328_b() / 2.0D, 0.0D);
      GL11.glScaled((double)scaling, (double)scaling, 1.0D);
      GL11.glTranslated((double)(-res.func_78326_a()) / 2.0D, (double)(-res.func_78328_b()) / 2.0D, 0.0D);
   }
}
