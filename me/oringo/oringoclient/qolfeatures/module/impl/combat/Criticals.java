package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.mixins.entity.PlayerSPAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PacketUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
   public static final NumberSetting delay = new NumberSetting("Delay", 500.0D, 0.0D, 2000.0D, 50.0D);
   public static final NumberSetting hurtTime = new NumberSetting("Hurt time", 2.0D, 0.0D, 10.0D, 1.0D);
   public static final ModeSetting mode = new ModeSetting("Mode", "Hypixel", new String[]{"Hypixel", "Hypixel 2"});
   private C02PacketUseEntity attack;
   private int ticks = 0;
   private float[] offsets = new float[]{0.0625F, 0.03125F};
   private MilliTimer timer = new MilliTimer();

   public Criticals() {
      super("Criticals", Module.Category.COMBAT);
      this.addSettings(new Setting[]{mode, delay, hurtTime});
   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (this.isToggled() && this.attack != null && !OringoClient.speed.isToggled()) {
         String var2 = mode.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1419855161:
            if (var2.equals("Hypixel 2")) {
               var3 = 0;
            }
            break;
         case -1248403467:
            if (var2.equals("Hypixel")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            if (mc.field_71439_g.field_70122_E && event.onGround && this.attack.func_149564_a(mc.field_71441_e) instanceof EntityLivingBase && (double)((EntityLivingBase)this.attack.func_149564_a(mc.field_71441_e)).field_70737_aN <= hurtTime.getValue()) {
               mc.func_147114_u().func_147298_b().func_179290_a(new C04PacketPlayerPosition(((PlayerSPAccessor)mc.field_71439_g).getLastReportedPosX(), ((PlayerSPAccessor)mc.field_71439_g).getLastReportedPosY() + (double)this.offsets[0] + MathUtil.getRandomInRange(0.0D, 0.0010000000474974513D), ((PlayerSPAccessor)mc.field_71439_g).getLastReportedPosZ(), false));
               mc.func_147114_u().func_147298_b().func_179290_a(new C04PacketPlayerPosition(((PlayerSPAccessor)mc.field_71439_g).getLastReportedPosX(), ((PlayerSPAccessor)mc.field_71439_g).getLastReportedPosY() + (double)this.offsets[1] + MathUtil.getRandomInRange(0.0D, 0.0010000000474974513D), ((PlayerSPAccessor)mc.field_71439_g).getLastReportedPosZ(), false));
               PacketUtils.sendPacketNoEvent(this.attack);
               this.attack = null;
               OringoClient.sendMessageWithPrefix("Hypixel");
            } else {
               this.attack = null;
            }
            break;
         case 1:
            if (mc.field_71439_g.field_70122_E && this.attack != null && event.onGround && this.attack.func_149564_a(mc.field_71441_e) instanceof EntityLivingBase && (double)((EntityLivingBase)this.attack.func_149564_a(mc.field_71441_e)).field_70737_aN <= hurtTime.getValue()) {
               switch(this.ticks++) {
               case 0:
               case 1:
                  event.y += (double)this.offsets[this.ticks - 1] + MathUtil.getRandomInRange(0.0D, 0.0010000000474974513D);
                  event.setOnGround(false);
                  OringoClient.sendMessageWithPrefix("Hypixel 2");
                  break;
               case 2:
                  PacketUtils.sendPacketNoEvent(this.attack);
                  this.ticks = 0;
                  this.attack = null;
               }
            } else {
               this.ticks = 0;
               this.attack = null;
            }
         }
      }

   }

   public void onEnable() {
      this.attack = null;
      this.ticks = 0;
   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (this.isToggled() && !OringoClient.speed.isToggled() && event.packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.packet).func_149565_c() == Action.ATTACK && ((C02PacketUseEntity)event.packet).func_149564_a(mc.field_71441_e) instanceof EntityLivingBase && (double)((EntityLivingBase)((C02PacketUseEntity)event.packet).func_149564_a(mc.field_71441_e)).field_70737_aN <= hurtTime.getValue() && this.timer.hasTimePassed((long)delay.getValue())) {
         this.attack = (C02PacketUseEntity)event.packet;
         event.setCanceled(true);
         this.timer.reset();
      }

   }
}
