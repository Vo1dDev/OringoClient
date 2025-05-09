package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PacketUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AutoRogueSword extends Module {
   public NumberSetting clicks = new NumberSetting("Clicks", 50.0D, 1.0D, 1000.0D, 10.0D);
   public NumberSetting delay = new NumberSetting("Delay", 29.0D, 0.0D, 100.0D, 1.0D);
   public BooleanSetting onlyDung = new BooleanSetting("Only dungeon", false);
   private final MilliTimer time = new MilliTimer();

   public AutoRogueSword() {
      super("Auto Rogue", 0, Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.clicks, this.delay, this.onlyDung});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (mc.field_71439_g != null && (SkyblockUtils.inDungeon || !this.onlyDung.isEnabled()) && this.isToggled()) {
         if (this.time.hasTimePassed((long)this.delay.getValue())) {
            for(int i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null && mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().toLowerCase().contains("rogue sword")) {
                  PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(i));

                  for(int x = 0; (double)x < this.clicks.getValue(); ++x) {
                     mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71071_by.func_70301_a(i)));
                  }

                  PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
                  this.time.reset();
                  break;
               }
            }
         }

      }
   }
}
