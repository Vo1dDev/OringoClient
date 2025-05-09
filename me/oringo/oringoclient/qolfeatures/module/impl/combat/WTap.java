package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.mixins.entity.PlayerSPAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WTap extends Module {
   public ModeSetting mode = new ModeSetting("mode", "Packet", new String[]{"Packet", "Extra Packet"});
   public BooleanSetting bow = new BooleanSetting("Bow", true);

   public WTap() {
      super("WTap", Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.mode, this.bow});
   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (this.isToggled() && (event.packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.packet).func_149565_c() == Action.ATTACK || this.bow.isEnabled() && event.packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)event.packet).func_180762_c() == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && mc.field_71439_g.func_70694_bm() != null && mc.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemBow)) {
         String var2 = this.mode.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case 486059736:
            if (var2.equals("Extra Packet")) {
               var3 = 0;
            }
         }

         switch(var3) {
         case 0:
            for(int i = 0; i < 4; ++i) {
               mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING));
               mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING));
            }

            return;
         default:
            if (mc.field_71439_g.func_70051_ag()) {
               mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING));
         }
      }

   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent.Post event) {
      if (this.isToggled() && (event.packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.packet).func_149565_c() == Action.ATTACK || this.bow.isEnabled() && event.packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)event.packet).func_180762_c() == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && mc.field_71439_g.func_70694_bm() != null && mc.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemBow) && !mc.field_71439_g.func_70051_ag()) {
         ((PlayerSPAccessor)mc.field_71439_g).setServerSprintState(false);
      }

   }
}
