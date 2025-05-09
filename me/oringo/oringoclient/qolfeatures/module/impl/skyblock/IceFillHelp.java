package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IceFillHelp extends Module {
   public NumberSetting slowdown = new NumberSetting("Ice slowdown", 0.15D, 0.05D, 1.0D, 0.05D);
   public BooleanSetting noIceSlip = new BooleanSetting("No ice slip", true);
   public BooleanSetting autoStop = new BooleanSetting("Auto stop", true);

   public IceFillHelp() {
      super("Ice Fill Helper", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.autoStop, this.slowdown, this.noIceSlip});
   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (this.isToggled() && mc.field_71439_g.field_70122_E) {
         BlockPos currentPos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.4D, mc.field_71439_g.field_70161_v);
         if (mc.field_71441_e.func_180495_p(currentPos).func_177230_c() == Blocks.field_150432_aD) {
            event.setZ(event.getZ() * this.slowdown.getValue());
            event.setX(event.getX() * this.slowdown.getValue());
            BlockPos nextPos = new BlockPos(mc.field_71439_g.field_70165_t + event.getX(), mc.field_71439_g.field_70163_u - 0.4D, mc.field_71439_g.field_70161_v + event.getZ());
            if (this.autoStop.isEnabled() && !currentPos.equals(nextPos) && mc.field_71441_e.func_180495_p(nextPos).func_177230_c() == Blocks.field_150432_aD) {
               event.setZ(0.0D);
               event.setX(0.0D);
            }
         }

         if (this.noIceSlip.isEnabled()) {
            Blocks.field_150403_cj.field_149765_K = 0.6F;
            Blocks.field_150432_aD.field_149765_K = 0.6F;
         }

      }
   }

   public void onDisable() {
      Blocks.field_150403_cj.field_149765_K = 0.98F;
      Blocks.field_150432_aD.field_149765_K = 0.98F;
   }
}
