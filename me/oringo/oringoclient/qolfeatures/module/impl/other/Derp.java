package me.oringo.oringoclient.qolfeatures.module.impl.other;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.utils.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Derp extends Module {
   private ArrayList<Packet<?>> packets = new ArrayList();

   public Derp() {
      super("Derp", Module.Category.OTHER);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (this.isToggled() && this.packets.isEmpty()) {
         event.yaw = (float)((new Random()).nextInt(181) * (mc.field_71439_g.field_70173_aa % 2 == 0 ? -1 : 1));
         event.pitch = (float)((new Random()).nextInt(181) - 90);
      }
   }

   @SubscribeEvent
   public void onUpdatePost(MotionUpdateEvent.Post e) {
      if (!this.packets.isEmpty()) {
         Iterator var2 = this.packets.iterator();

         while(var2.hasNext()) {
            Packet<?> packet = (Packet)var2.next();
            PacketUtils.sendPacketNoEvent(packet);
         }

         this.packets.clear();
      }
   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (this.isToggled()) {
         if (event.packet instanceof C02PacketUseEntity || event.packet instanceof C08PacketPlayerBlockPlacement || event.packet instanceof C07PacketPlayerDigging || event.packet instanceof C0APacketAnimation || event.packet instanceof C01PacketChatMessage || event.packet instanceof C09PacketHeldItemChange) {
            this.packets.add(event.packet);
            event.setCanceled(true);
         }

      }
   }
}
