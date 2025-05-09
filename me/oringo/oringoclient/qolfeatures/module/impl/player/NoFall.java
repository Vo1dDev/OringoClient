package me.oringo.oringoclient.qolfeatures.module.impl.player;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.mixins.packet.C03Accessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Disabler;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall extends Module {
   public ModeSetting mode = new ModeSetting("Mode", "Hypixel", new String[]{"Hypixel", "Packet", "NoGround"});
   public ModeSetting hypixelSpoofMode = new ModeSetting("Spoof mode", "Fall", new String[]{"Always", "Fall"}) {
      public boolean isHidden() {
         return !NoFall.this.mode.is("Hypixel");
      }
   };

   public NoFall() {
      super("NoFall", Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.mode, this.hypixelSpoofMode});
   }

   public boolean isToggled() {
      return super.isToggled();
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onUpdate(MotionUpdateEvent event) {
      if (this.isToggled()) {
         String var2 = this.mode.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1911998296:
            if (var2.equals("Packet")) {
               var3 = 1;
            }
            break;
         case -1248403467:
            if (var2.equals("Hypixel")) {
               var3 = 0;
            }
         }

         switch(var3) {
         case 0:
            if (((double)mc.field_71439_g.field_70143_R > 1.5D || this.hypixelSpoofMode.is("Always")) && Disabler.wasEnabled) {
               event.setOnGround(true);
            }
            break;
         case 1:
            if (mc.field_71439_g.field_70143_R > 1.5F) {
               mc.func_147114_u().func_147298_b().func_179290_a(new C03PacketPlayer(true));
               mc.field_71439_g.field_70143_R = 0.0F;
            }
         }
      }

   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (event.packet instanceof C03PacketPlayer && this.isToggled()) {
         String var2 = this.mode.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case 370287304:
            if (var2.equals("NoGround")) {
               var3 = 0;
            }
         default:
            switch(var3) {
            case 0:
               ((C03Accessor)event.packet).setOnGround(false);
            }
         }
      }

   }

   public void onEnable() {
      if (!OringoClient.disabler.isToggled()) {
         Notifications.showNotification("Disabler not enabled", 3000, Notifications.NotificationType.WARNING);
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
   }
}
