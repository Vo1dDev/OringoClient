package me.oringo.oringoclient.qolfeatures.module.impl.movement;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.events.StepEvent;
import me.oringo.oringoclient.mixins.MinecraftAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Step extends Module {
   public ModeSetting mode = new ModeSetting("Mode", "NCP", new String[]{"NCP"});
   public NumberSetting timer = new NumberSetting("Timer", 0.4D, 0.1D, 1.0D, 0.1D);
   private boolean isStepping;
   private int stage;

   public Step() {
      super("Step", Module.Category.MOVEMENT);
      this.addSettings(new Setting[]{this.mode, this.timer});
   }

   @SubscribeEvent
   public void onStep(StepEvent event) {
      if (this.isToggled() && !OringoClient.speed.isToggled() && !mc.field_71439_g.field_71158_b.field_78901_c && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab()) {
         if (event instanceof StepEvent.Post && (double)PlayerUtils.getJumpMotion() < 1.0D) {
            if (event.getHeight() > 0.87D) {
               double[] var2 = new double[]{event.getHeight() * 0.42D, event.getHeight() * 0.75D};
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  double offset = var2[var4];
                  mc.func_147114_u().func_147298_b().func_179290_a(new C04PacketPlayerPosition(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + offset, mc.field_71439_g.field_70161_v, false));
               }

               if (this.timer.getValue() != 1.0D) {
                  ((MinecraftAccessor)mc).getTimer().field_74278_d = (float)this.timer.getValue();
                  this.isStepping = true;
               }
            }
         } else if (mc.field_71439_g.field_70122_E) {
            event.setHeight(1.0D);
         }
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (this.isStepping && this.mode.is("NCP")) {
         this.isStepping = false;
         ((MinecraftAccessor)mc).getTimer().field_74278_d = 1.0F;
      }

   }
}
