package me.oringo.oringoclient.qolfeatures.module.impl.other;

import java.util.Random;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerBeamer extends Module {
   public NumberSetting beamer = new NumberSetting("Packets", 10.0D, 1.0D, 50.0D, 1.0D);
   public NumberSetting randomSend = new NumberSetting("Send ticks", 0.0D, 0.0D, 100.0D, 1.0D);
   public BooleanSetting start = new BooleanSetting("Start Breaking", true);
   public ModeSetting mode = new ModeSetting("Mode", "Sync", new String[]{"Sync", "Async"});
   private int i = 0;

   public ServerBeamer() {
      super("Server Beamer", Module.Category.OTHER);
      this.addSettings(new Setting[]{this.beamer, this.randomSend, this.mode, this.start});
   }

   @SubscribeEvent
   public void onUpdate(PacketSentEvent event) {
      if (this.isToggled() && event.packet instanceof C0FPacketConfirmTransaction && this.mode.is("Sync")) {
         this.beam();
      }
   }

   @SubscribeEvent
   public void onMotion(MotionUpdateEvent.Pre event) {
      if (this.isToggled()) {
         if (this.randomSend.getValue() != 0.0D && (double)(this.i++) % this.randomSend.getValue() == 0.0D) {
            this.beam();
         }

         if (this.mode.is("Async")) {
            this.beam();
         }
      }
   }

   private void beam() {
      for(int i = 0; (double)i < this.beamer.getValue(); ++i) {
         BlockPos pos = new BlockPos((new Random()).nextInt(10000) * 16, 255, (new Random()).nextInt(10000) * 16);
         mc.func_147114_u().func_147298_b().func_179290_a(new C07PacketPlayerDigging(this.start.isEnabled() ? Action.START_DESTROY_BLOCK : Action.STOP_DESTROY_BLOCK, pos, EnumFacing.func_176733_a((double)mc.field_71439_g.field_70177_z)));
      }

   }
}
