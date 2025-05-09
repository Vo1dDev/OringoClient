package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoClicker extends Module {
   public static final NumberSetting maxCps = new NumberSetting("Max CPS", 12.0D, 1.0D, 20.0D, 1.0D) {
      public void setValue(double value) {
         super.setValue(value);
         AutoClicker.minCps.setMax(AutoClicker.maxCps.getValue());
         if (AutoClicker.minCps.getValue() > AutoClicker.minCps.getMax()) {
            AutoClicker.minCps.setValue(AutoClicker.minCps.getMin());
         }

      }
   };
   public static final NumberSetting minCps = new NumberSetting("Min CPS", 10.0D, 1.0D, 20.0D, 1.0D) {
      public void setValue(double value) {
         super.setValue(value);
         AutoClicker.maxCps.setMin(AutoClicker.minCps.getValue());
         if (AutoClicker.maxCps.getValue() < AutoClicker.maxCps.getMin()) {
            AutoClicker.maxCps.setValue(AutoClicker.maxCps.getMin());
         }

      }
   };
   public static final ModeSetting mode = new ModeSetting("Mode", "Attack held", new String[]{"Key held", "Toggle", "Attack held"});
   private MilliTimer timer = new MilliTimer();
   private double nextDelay = 10.0D;

   public AutoClicker() {
      super("AutoClicker", Module.Category.COMBAT);
      this.addSettings(new Setting[]{minCps, maxCps, mode});
   }

   @SubscribeEvent
   public void onTick(RenderWorldLastEvent event) {
      if (this.isToggled() && mc.field_71439_g != null && this.isPressed() && !mc.field_71439_g.func_71039_bw() && mc.field_71462_r == null && this.timer.hasTimePassed((long)(1000.0D / this.nextDelay))) {
         this.timer.reset();
         this.nextDelay = MathUtil.getRandomInRange(maxCps.getValue(), minCps.getValue());
         SkyblockUtils.click();
      }

   }

   public boolean isPressed() {
      String var1 = mode.getSelected();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1784436876:
         if (var1.equals("Toggle")) {
            var2 = 1;
         }
         break;
      case 507226230:
         if (var1.equals("Key held")) {
            var2 = 0;
         }
      }

      switch(var2) {
      case 0:
         return super.isPressed();
      case 1:
         return this.isToggled();
      default:
         return mc.field_71474_y.field_74312_F.func_151470_d();
      }
   }

   public boolean isKeybind() {
      return !mode.is("Toggle");
   }
}
