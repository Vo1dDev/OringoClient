package me.oringo.oringoclient.qolfeatures.module.impl.other;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.events.PlayerUpdateEvent;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.PacketUtils;
import me.oringo.oringoclient.utils.TickTimer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class Blink extends Module {
   public BooleanSetting onlyPos = new BooleanSetting("Only pos packets", false);
   public BooleanSetting pulse = new BooleanSetting("Pulse", false);
   public NumberSetting pulseTicks = new NumberSetting("Pulse ticks", 10.0D, 1.0D, 100.0D, 1.0D) {
      public boolean isHidden() {
         return !Blink.this.pulse.isEnabled();
      }
   };
   private Queue<Packet<?>> packets = new ConcurrentLinkedQueue();
   private TickTimer timer = new TickTimer();

   public Blink() {
      super("Blink", Module.Category.OTHER);
      this.addSettings(new Setting[]{this.onlyPos, this.pulse, this.pulseTicks});
   }

   public void onEnable() {
      this.timer.reset();
   }

   @SubscribeEvent
   public void onDisconnect(ClientDisconnectionFromServerEvent event) {
      this.packets.clear();
      if (this.isToggled()) {
         this.setToggled(false);
      }

   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      this.timer.updateTicks();
      if (this.timer.passed((int)this.pulseTicks.getValue()) && this.pulse.isEnabled()) {
         this.sendPackets();
         this.timer.reset();
      }

   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (this.isToggled() && mc.field_71439_g == null) {
         this.packets.clear();
         this.setToggled(false);
      } else {
         if (this.isToggled() && (event.packet instanceof C03PacketPlayer || !this.onlyPos.isEnabled())) {
            event.setCanceled(true);
            this.packets.offer(event.packet);
         }

      }
   }

   @SubscribeEvent
   public void onWorld(WorldJoinEvent event) {
      this.packets.clear();
      if (this.isToggled()) {
         this.toggle();
      }

   }

   private void sendPackets() {
      if (mc.func_147114_u() != null) {
         while(!this.packets.isEmpty()) {
            PacketUtils.sendPacketNoEvent((Packet)this.packets.poll());
         }
      }

   }

   public void onDisable() {
      this.sendPackets();
   }
}
