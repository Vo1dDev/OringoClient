package me.oringo.oringoclient.qolfeatures.module.impl.movement;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.events.MoveHeadingEvent;
import me.oringo.oringoclient.events.MoveStateUpdateEvent;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.mixins.MinecraftAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.MovementUtils;
import me.oringo.oringoclient.utils.TimerUtil;
import me.oringo.oringoclient.utils.font.Fonts;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends Module {
   public BooleanSetting stopOnDisable = new BooleanSetting("Stop on disable", true);
   public BooleanSetting disableOnFlag = new BooleanSetting("Disable on flag", true);
   public BooleanSetting sneak = new BooleanSetting("Sneak timer", true);
   public NumberSetting timer = new NumberSetting("Timer", 1.0D, 0.1D, 3.0D, 0.05D);
   public NumberSetting sneakTimer = new NumberSetting("SneakTimer", 1.0D, 0.1D, 3.0D, 0.05D, (aBoolean) -> {
      return !this.sneak.isEnabled();
   });
   private static MilliTimer disable = new MilliTimer();
   int airTicks;
   boolean canApplySpeed;

   public Speed() {
      super("Speed", Module.Category.MOVEMENT);
      this.addSettings(new Setting[]{this.stopOnDisable, this.disableOnFlag, this.sneak, this.sneakTimer, this.timer});
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (this.isToggled() && !isDisabled()) {
         ((MinecraftAccessor)mc).getTimer().field_74278_d = (float)(this.sneak.isEnabled() && mc.field_71474_y.field_74311_E.func_151470_d() ? this.sneakTimer.getValue() : this.timer.getValue());
         if (MovementUtils.isMoving()) {
            event.setYaw(MovementUtils.getYaw());
         }
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (this.isToggled() && !isDisabled()) {
         if (MovementUtils.isMoving()) {
            double multi = 1.0D;
            if (mc.field_71439_g.func_70644_a(Potion.field_76424_c) && this.canApplySpeed) {
               multi += (double)(0.015F * (float)(mc.field_71439_g.func_70660_b(Potion.field_76424_c).func_76458_c() + 1));
            }

            if (mc.field_71439_g.field_71075_bZ.func_75094_b() > 0.2F) {
               multi = 0.8999999761581421D;
            }

            EntityPlayerSP var10000 = mc.field_71439_g;
            var10000.field_70159_w *= multi;
            var10000 = mc.field_71439_g;
            var10000.field_70179_y *= multi;
         } else {
            mc.field_71439_g.field_70159_w = 0.0D;
            mc.field_71439_g.field_70179_y = 0.0D;
         }

         event.setX(mc.field_71439_g.field_70159_w).setZ(mc.field_71439_g.field_70179_y);
      }

   }

   @SubscribeEvent
   public void onUpdateMove(MoveStateUpdateEvent event) {
      if (this.isToggled() && !isDisabled()) {
         event.setSneak(false);
      }

   }

   @SubscribeEvent
   public void onMoveFlying(MoveHeadingEvent event) {
      if (this.isToggled() && MovementUtils.isMoving() && !isDisabled()) {
         if (mc.field_71439_g.field_70122_E) {
            this.jump();
            this.canApplySpeed = mc.field_71439_g.func_70644_a(Potion.field_76424_c);
            this.airTicks = 0;
         } else {
            ++this.airTicks;
            event.setOnGround(true);
            if (!mc.field_71439_g.func_70644_a(Potion.field_76424_c)) {
               if (!this.canApplySpeed) {
                  if ((double)mc.field_71439_g.field_70143_R < 0.4D && mc.field_71439_g.field_71075_bZ.func_75094_b() < 0.2F) {
                     event.setFriction2Multi(0.95F);
                  }
               } else {
                  event.setFriction2Multi(0.87F);
               }
            }
         }
      }

   }

   public static boolean isDisabled() {
      return OringoClient.scaffold.isToggled() && Scaffold.disableSpeed.isEnabled() || !disable.hasTimePassed(2000L);
   }

   private String getBPS() {
      double bps = Math.hypot(mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q, mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (double)TimerUtil.getTimer().field_74278_d * 20.0D;
      return String.format("%.2f", bps);
   }

   @SubscribeEvent
   public void onRender(Post event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null && this.isToggled()) {
         if (event.type == ElementType.ALL) {
            ScaledResolution resolution = new ScaledResolution(mc);
            Fonts.robotoMediumBold.drawSmoothCenteredStringWithShadow(this.getBPS(), 20.0D, (double)(resolution.func_78328_b() - 20), OringoClient.clickGui.getColor().getRGB());
         }

      }
   }

   public void onDisable() {
      if (TimerUtil.getTimer() != null) {
         ((MinecraftAccessor)mc).getTimer().field_74278_d = 1.0F;
         this.canApplySpeed = false;
      }

      if (mc.field_71439_g != null && this.stopOnDisable.isEnabled()) {
         mc.field_71439_g.field_70159_w = 0.0D;
         mc.field_71439_g.field_70179_y = 0.0D;
      }

   }

   public void onEnable() {
      this.airTicks = 0;
      if (!OringoClient.disabler.isToggled()) {
         Notifications.showNotification("Disabler not enabled", 3000, Notifications.NotificationType.WARNING);
      }

   }

   private void jump() {
      mc.field_71439_g.field_70181_x = 0.41999998688697815D;
      if (mc.field_71439_g.func_70051_ag()) {
         float f = MovementUtils.getYaw() * 0.017453292F;
         EntityPlayerSP var10000 = mc.field_71439_g;
         var10000.field_70159_w -= (double)(MathHelper.func_76126_a(f) * 0.2F);
         var10000 = mc.field_71439_g;
         var10000.field_70179_y += (double)(MathHelper.func_76134_b(f) * 0.2F);
      }

      mc.field_71439_g.field_70160_al = true;
      mc.field_71439_g.func_71029_a(StatList.field_75953_u);
      if (mc.field_71439_g.func_70051_ag()) {
         mc.field_71439_g.func_71020_j(0.8F);
      } else {
         mc.field_71439_g.func_71020_j(0.2F);
      }

   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S08PacketPlayerPosLook && this.disableOnFlag.isEnabled()) {
         if (!isDisabled() && this.isToggled()) {
            Notifications.showNotification("Oringo Client", "Disabled speed due to a flag", 1500);
            ((MinecraftAccessor)mc).getTimer().field_74278_d = 1.0F;
            this.canApplySpeed = false;
            if (mc.field_71439_g != null) {
               mc.field_71439_g.field_70159_w = 0.0D;
               mc.field_71439_g.field_70179_y = 0.0D;
            }
         }

         disable.reset();
      }

   }
}
