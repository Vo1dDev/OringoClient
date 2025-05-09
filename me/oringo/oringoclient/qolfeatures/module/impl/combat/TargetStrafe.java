package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import java.awt.Color;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MoveFlyingEvent;
import me.oringo.oringoclient.events.MoveStateUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.GuiMove;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Speed;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.RotationUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class TargetStrafe extends Module {
   public ModeSetting mode = new ModeSetting("Mode", "Normal", new String[]{"Normal", "Back"});
   public NumberSetting distance = new NumberSetting("Distance", 2.0D, 1.0D, 4.0D, 0.1D);
   public BooleanSetting controllable = new BooleanSetting("Controllable", true);
   public BooleanSetting jumpOnly = new BooleanSetting("Space only", true);
   public BooleanSetting thirdPerson = new BooleanSetting("Third person", false);
   public BooleanSetting smart = new BooleanSetting("Smart", true);
   public BooleanSetting liquidCheck = new BooleanSetting("Liquid check", false, (aBoolean) -> {
      return !this.smart.isEnabled();
   });
   public BooleanSetting voidCheck = new BooleanSetting("Void check", true, (aBoolean) -> {
      return !this.smart.isEnabled();
   });
   private MilliTimer strafeDelay = new MilliTimer();
   private int prev = -1;
   double lastx;
   double lasty;
   private float strafe = 1.0F;

   public TargetStrafe() {
      super("Target Strafe", Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.mode, this.distance, this.thirdPerson, this.smart, this.voidCheck, this.liquidCheck, this.controllable, this.jumpOnly});
   }

   public boolean isUsing() {
      return KillAura.target != null && this.isToggled() && (mc.field_71474_y.field_74314_A.func_151470_d() || !this.jumpOnly.isEnabled()) && (OringoClient.speed.isToggled() && !Speed.isDisabled() || OringoClient.fly.isFlying());
   }

   @SubscribeEvent
   public void onMove(MoveStateUpdateEvent event) {
      if (this.isUsing() && (mc.field_71462_r == null || ((GuiMove)Module.getModule(GuiMove.class)).isToggled())) {
         if (this.thirdPerson.isEnabled()) {
            if (this.prev == -1) {
               this.prev = mc.field_71474_y.field_74320_O;
            }

            mc.field_71474_y.field_74320_O = 1;
         }

         if (this.controllable.isEnabled() && (mc.field_71474_y.field_74366_z.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d())) {
            if (mc.field_71474_y.field_74370_x.func_151470_d()) {
               this.strafe = 1.0F;
            }

            if (mc.field_71474_y.field_74366_z.func_151470_d()) {
               this.strafe = -1.0F;
            }
         } else if (this.strafeDelay.hasTimePassed(200L)) {
            if (!mc.field_71439_g.field_70123_F && (!this.smart.isEnabled() || OringoClient.fly.isFlying() || (!this.voidCheck.isEnabled() || !((double)mc.field_71439_g.field_70143_R < 2.5D) || !PlayerUtils.isFall(6.0F, (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * 2.5D, (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * 2.5D)) && (!this.liquidCheck.isEnabled() || mc.field_71439_g.func_180799_ab() || mc.field_71439_g.func_70090_H() || !PlayerUtils.isLiquid(3.0F, (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * 2.5D, (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * 2.5D)))) {
               if (this.mode.is("Back")) {
                  Entity entity = KillAura.target;
                  float yaw = (entity.field_70177_z - 90.0F) % 360.0F;
                  double x = Math.cos((double)yaw * 3.141592653589793D / 180.0D) * this.distance.getValue() + entity.field_70165_t;
                  double z = Math.sin((double)yaw * 3.141592653589793D / 180.0D) * this.distance.getValue() + entity.field_70161_v;
                  if (this.getDistance(x, z, mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70161_v) > 0.2D && this.getDistance(x, z, mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70161_v) > this.getDistance(this.lastx, this.lasty, x, z)) {
                     this.strafe = -this.strafe;
                     this.strafeDelay.reset();
                  }

                  this.lastx = mc.field_71439_g.field_70165_t;
                  this.lasty = mc.field_71439_g.field_70161_v;
               }
            } else {
               this.strafe = -this.strafe;
               this.strafeDelay.reset();
            }
         }

         if (this.getDistance(KillAura.target) <= this.distance.getValue() + 2.0D || this.controllable.isEnabled() && (mc.field_71474_y.field_74366_z.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d())) {
            event.setStrafe(this.strafe);
         }

         event.setForward(!mc.field_71474_y.field_74368_y.func_151470_d() ? (float)(this.getDistance(KillAura.target) <= this.distance.getValue() ? 0 : 1) : -1.0F);
      } else if (this.thirdPerson.isEnabled() && this.prev != -1) {
         mc.field_71474_y.field_74320_O = this.prev;
         this.prev = -1;
      }

   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (this.isUsing()) {
         Entity entity = KillAura.target;
         float partialTicks = event.partialTicks;
         GL11.glPushMatrix();
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glDisable(3553);
         GL11.glDisable(2884);
         GL11.glDisable(2929);
         GL11.glShadeModel(7425);
         GlStateManager.func_179140_f();
         GL11.glTranslated(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)partialTicks - mc.func_175598_ae().field_78730_l, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)partialTicks - mc.func_175598_ae().field_78731_m + 0.1D, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)partialTicks - mc.func_175598_ae().field_78728_n);
         double radius = this.distance.getValue();
         GL11.glLineWidth(4.0F);
         GL11.glBegin(2);
         int angles = 90;

         for(int i = 0; i <= angles; ++i) {
            Color color = Color.white;
            GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
            GL11.glVertex3d(Math.cos((double)i * 3.141592653589793D / ((double)angles / 2.0D)) * radius, 0.0D, Math.sin((double)i * 3.141592653589793D / ((double)angles / 2.0D)) * radius);
         }

         GL11.glEnd();
         GL11.glPopMatrix();
         GL11.glShadeModel(7424);
         GL11.glEnable(2929);
         GL11.glEnable(2884);
         GlStateManager.func_179117_G();
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glDisable(2848);
      }

   }

   double getDistance(double x, double z, double x1, double z1) {
      double x2 = x1 - x;
      double z2 = z1 - z;
      return Math.sqrt(x2 * x2 + z2 * z2);
   }

   double getDistance(Entity entity) {
      return Math.hypot(entity.field_70165_t - mc.field_71439_g.field_70165_t, entity.field_70161_v - mc.field_71439_g.field_70161_v);
   }

   @SubscribeEvent
   public void onMoveFly(MoveFlyingEvent event) {
      if (this.isUsing()) {
         event.setYaw(RotationUtils.getRotations(KillAura.target).getYaw());
      }

   }
}
