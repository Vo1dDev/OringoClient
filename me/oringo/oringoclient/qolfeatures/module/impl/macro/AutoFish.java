package me.oringo.oringoclient.qolfeatures.module.impl.macro;

import me.oringo.oringoclient.events.PlayerUpdateEvent;
import me.oringo.oringoclient.mixins.entity.EntityFishHookAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.MilliTimer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFish extends Module {
   private final MilliTimer hookDelay = new MilliTimer();
   private static int nextDelay;
   private static int waterticks;
   private boolean hook;

   public AutoFish() {
      super("Auto Fish", Module.Category.OTHER);
      this.addSettings(new Setting[0]);
   }

   public void onEnable() {
      super.onEnable();
   }

   @SubscribeEvent
   public void onTick(PlayerUpdateEvent event) {
      if (this.isToggled()) {
         if (mc.field_71439_g.field_71104_cf != null && (!((EntityFishHookAccessor)mc.field_71439_g.field_71104_cf).inGround() || !(MathUtil.hypot(mc.field_71439_g.field_71104_cf.field_70179_y, mc.field_71439_g.field_71104_cf.field_70159_w) < 0.001D))) {
            if (mc.field_71439_g.field_71104_cf.func_70090_H() || mc.field_71439_g.field_71104_cf.func_180799_ab()) {
               ++waterticks;
               if (!this.hook && waterticks > 75 && MathUtil.hypot(mc.field_71439_g.field_71104_cf.field_70179_y, mc.field_71439_g.field_71104_cf.field_70159_w) < 0.001D && mc.field_71439_g.field_71104_cf.field_70163_u - mc.field_71439_g.field_71104_cf.field_70167_r < -0.04D) {
                  this.hook = true;
                  nextDelay = (int)MathUtil.getRandomInRange(75.0D, 200.0D);
                  if (this.hookDelay.hasTimePassed((long)nextDelay)) {
                     this.hookDelay.reset();
                  }
               }

               if (this.hook && this.hookDelay.hasTimePassed((long)nextDelay)) {
                  mc.field_71442_b.func_78769_a(mc.field_71439_g, mc.field_71441_e, mc.field_71439_g.func_70694_bm());
                  mc.field_71439_g.field_71104_cf = null;
                  this.hookDelay.reset();
                  waterticks = 0;
                  this.hook = false;
               }
            }
         } else {
            if (this.hookDelay.hasTimePassed(300L)) {
               mc.field_71442_b.func_78769_a(mc.field_71439_g, mc.field_71441_e, mc.field_71439_g.func_70694_bm());
               this.hookDelay.reset();
            }

            this.hook = false;
            waterticks = 0;
         }
      }

   }
}
