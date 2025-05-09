package me.oringo.oringoclient.qolfeatures.module.impl.movement;

import me.oringo.oringoclient.events.PostGuiOpenEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Disabler;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GuiMove extends Module {
   private BooleanSetting rotate = new BooleanSetting("Rotate", true);
   private BooleanSetting drag = new BooleanSetting("Alt drag", true) {
      public boolean isHidden() {
         return !GuiMove.this.rotate.isEnabled();
      }
   };
   private BooleanSetting hideTerminalGui = new BooleanSetting("Hide terminals", false);
   private NumberSetting sensivity = new NumberSetting("Sensivity", 1.5D, 0.1D, 3.0D, 0.01D, (aBoolean) -> {
      return !this.rotate.isEnabled();
   });
   public static final KeyBinding[] binds;

   public GuiMove() {
      super("InvMove", Module.Category.MOVEMENT);
      this.addSettings(new Setting[]{this.hideTerminalGui, this.rotate, this.sensivity, this.drag});
   }

   public boolean isToggled() {
      return super.isToggled();
   }

   public void onDisable() {
      if (mc.field_71462_r != null) {
         KeyBinding[] var1 = binds;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            KeyBinding bind = var1[var3];
            KeyBinding.func_74510_a(bind.func_151463_i(), false);
         }
      }

   }

   @SubscribeEvent
   public void onGUi(PostGuiOpenEvent event) {
      if (!(event.gui instanceof GuiChat) && this.isToggled() && Disabler.wasEnabled) {
         KeyBinding[] var2 = binds;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            KeyBinding bind = var2[var4];
            KeyBinding.func_74510_a(bind.func_151463_i(), GameSettings.func_100015_a(bind));
         }
      }

   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (mc.field_71462_r != null && !(mc.field_71462_r instanceof GuiChat) && this.isToggled() && Disabler.wasEnabled) {
         KeyBinding[] var2 = binds;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            KeyBinding bind = var2[var4];
            KeyBinding.func_74510_a(bind.func_151463_i(), GameSettings.func_100015_a(bind));
         }

         if ((mc.field_71462_r instanceof GuiContainer || mc.field_71462_r instanceof ICrafting) && this.rotate.isEnabled()) {
            mc.field_71417_B.func_74374_c();
            float f = mc.field_71474_y.field_74341_c * 0.6F + 0.2F;
            f = (float)((double)f * this.sensivity.getValue());
            float f1 = f * f * f * 8.0F;
            float f2 = (float)mc.field_71417_B.field_74377_a * f1;
            float f3 = (float)mc.field_71417_B.field_74375_b * f1;
            int i = 1;
            if (mc.field_71474_y.field_74338_d) {
               i = -1;
            }

            if (Keyboard.isKeyDown(56) && Mouse.isButtonDown(2) && this.drag.isEnabled()) {
               Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 6);
               mc.func_71364_i();
               Mouse.setGrabbed(false);
            }

            mc.field_71439_g.func_70082_c(f2, f3 * (float)i);
         }
      }

   }

   public boolean shouldHideGui(ContainerChest chest) {
      return SkyblockUtils.isTerminal(chest.func_85151_d().func_70005_c_()) && this.isToggled() && this.hideTerminalGui.isEnabled();
   }

   static {
      binds = new KeyBinding[]{mc.field_71474_y.field_74311_E, mc.field_71474_y.field_74314_A, mc.field_71474_y.field_151444_V, mc.field_71474_y.field_74351_w, mc.field_71474_y.field_74368_y, mc.field_71474_y.field_74370_x, mc.field_71474_y.field_74366_z};
   }
}
