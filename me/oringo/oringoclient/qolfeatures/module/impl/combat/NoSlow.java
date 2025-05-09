package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PacketUtils;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module {
   public NumberSetting eatingSlowdown = new NumberSetting("Eating slow", 1.0D, 0.2D, 1.0D, 0.1D);
   public NumberSetting swordSlowdown = new NumberSetting("Sword slow", 1.0D, 0.2D, 1.0D, 0.1D);
   public NumberSetting bowSlowdown = new NumberSetting("Bow slow", 1.0D, 0.2D, 1.0D, 0.1D);
   public ModeSetting mode = new ModeSetting("Mode", "Hypixel", new String[]{"Hypixel", "Vanilla"});
   private final MilliTimer blockDelay = new MilliTimer();

   public NoSlow() {
      super("NoSlow", 0, Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.mode, this.swordSlowdown, this.bowSlowdown, this.eatingSlowdown});
   }

   @SubscribeEvent
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S30PacketWindowItems && mc.field_71439_g != null && this.isToggled() && this.mode.is("Hypixel") && mc.field_71439_g.func_71039_bw() && mc.field_71439_g.func_71011_bu().func_77973_b() instanceof ItemSword) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void unUpdate(MotionUpdateEvent.Post event) {
      if (this.isToggled() && mc.field_71439_g.func_71039_bw() && this.mode.is("Hypixel")) {
         if (this.blockDelay.hasTimePassed(250L) && mc.field_71439_g.func_71011_bu().func_77973_b() instanceof ItemSword) {
            mc.field_71439_g.field_71174_a.func_147297_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
            mc.field_71439_g.field_71174_a.func_147297_a(new C0BPacketEntityAction(mc.field_71439_g, Action.STOP_SPRINTING));
            mc.field_71439_g.field_71174_a.func_147297_a(new C0BPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
            this.blockDelay.reset();
         }

         PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
      }

   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (this.isToggled() && this.mode.is("Hypixel") && event.packet instanceof C08PacketPlayerBlockPlacement && ((C08PacketPlayerBlockPlacement)event.packet).func_149574_g() != null && ((C08PacketPlayerBlockPlacement)event.packet).func_149574_g().func_77973_b() instanceof ItemSword) {
         this.blockDelay.reset();
      }

   }
}
