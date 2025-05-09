package me.oringo.oringoclient.qolfeatures.module.impl.movement;

import me.oringo.oringoclient.qolfeatures.module.Module;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class SafeWalk extends Module {
   public SafeWalk() {
      super("Eagle", 0, Module.Category.MOVEMENT);
   }

   public void onDisable() {
      KeyBinding.func_74510_a(mc.field_71474_y.field_74311_E.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74311_E));
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null && this.isToggled() && mc.field_71462_r == null) {
         BlockPos BP = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.5D, mc.field_71439_g.field_70161_v);
         if (mc.field_71441_e.func_180495_p(BP).func_177230_c() == Blocks.field_150350_a && mc.field_71441_e.func_180495_p(BP.func_177977_b()).func_177230_c() == Blocks.field_150350_a && mc.field_71439_g.field_70122_E && mc.field_71439_g.field_71158_b.field_78900_b < 0.1F) {
            KeyBinding.func_74510_a(mc.field_71474_y.field_74311_E.func_151463_i(), true);
         } else {
            KeyBinding.func_74510_a(mc.field_71474_y.field_74311_E.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74311_E));
         }

      }
   }
}
