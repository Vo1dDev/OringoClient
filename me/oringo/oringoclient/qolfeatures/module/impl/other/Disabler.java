package me.oringo.oringoclient.qolfeatures.module.impl.other;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PlayerUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Disabler extends Module {
   public static boolean wasEnabled = false;
   public static final BooleanSetting first = new BooleanSetting("First2", true, (aBoolean) -> {
      return true;
   });
   public static final BooleanSetting timerSemi = new BooleanSetting("Timer semi", false);

   public Disabler() {
      super("Disabler", Module.Category.OTHER);
      this.addSettings(new Setting[]{timerSemi, first});
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent e) {
      if (!mc.field_71439_g.func_70093_af() && !mc.field_71439_g.func_70115_ae() && this.isToggled() && (OringoClient.sprint.omni.isEnabled() && OringoClient.sprint.isToggled() || OringoClient.derp.isToggled() || OringoClient.killAura.isToggled() || OringoClient.speed.isToggled())) {
         mc.func_147114_u().func_147297_a(new C0BPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
         mc.func_147114_u().func_147297_a(new C0BPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
      }

   }

   public void onEnable() {
      Notifications.showNotification("Disabler will work after lobby change", 2000, Notifications.NotificationType.WARNING);
   }
}
