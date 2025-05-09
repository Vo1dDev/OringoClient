package me.oringo.oringoclient.qolfeatures.module.impl.player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.PacketUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiVoid extends Module {
   public NumberSetting fallDistance = new NumberSetting("Fall distance", 1.0D, 0.5D, 10.0D, 0.1D);
   public ModeSetting mode = new ModeSetting("Mode", "Blink", new String[]{"Flag", "Blink"});
   private static BooleanSetting disableFly = new BooleanSetting("Disable fly", true);
   private static final Queue<C03PacketPlayer> packetQueue = new ConcurrentLinkedQueue();
   private Vec3 lastPos = new Vec3(0.0D, 0.0D, 0.0D);
   private double motionY = 0.0D;

   public AntiVoid() {
      super("Anti Void", 0, Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.mode, this.fallDistance, disableFly});
   }

   @SubscribeEvent
   public void onMovePre(MotionUpdateEvent.Pre event) {
      if (this.isToggled() && (!disableFly.isEnabled() || !OringoClient.fly.isToggled()) && this.mode.is("Flag") && (double)mc.field_71439_g.field_70143_R > this.fallDistance.getValue() && PlayerUtils.isOverVoid()) {
         event.setPosition(mc.field_71439_g.field_70165_t + 100.0D, mc.field_71439_g.field_70163_u + 100.0D, mc.field_71439_g.field_70161_v + 100.0D);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPacket(PacketSentEvent event) {
      if (this.isToggled() && this.mode.is("Blink") && event.packet instanceof C03PacketPlayer && (!disableFly.isEnabled() || !OringoClient.fly.isToggled())) {
         if (PlayerUtils.isOverVoid()) {
            event.setCanceled(true);
            packetQueue.offer((C03PacketPlayer)event.packet);
            if ((double)mc.field_71439_g.field_70143_R > this.fallDistance.getValue()) {
               packetQueue.clear();
               mc.field_71439_g.field_70143_R = 0.0F;
               mc.field_71439_g.func_70107_b(this.lastPos.field_72450_a, this.lastPos.field_72448_b, this.lastPos.field_72449_c);
               mc.field_71439_g.func_70016_h(0.0D, this.motionY, 0.0D);
            }
         } else {
            this.lastPos = mc.field_71439_g.func_174791_d();
            this.motionY = mc.field_71439_g.field_70181_x;

            while(!packetQueue.isEmpty()) {
               PacketUtils.sendPacketNoEvent((Packet)packetQueue.poll());
            }
         }
      }

   }

   public static boolean isBlinking() {
      return !packetQueue.isEmpty();
   }

   public void onDisable() {
      packetQueue.clear();
   }

   @SubscribeEvent
   public void onRespawn(WorldJoinEvent event) {
      packetQueue.clear();
   }
}
