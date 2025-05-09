package me.oringo.oringoclient.utils;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;

public class MovementUtils {
   public static MilliTimer strafeTimer = new MilliTimer();

   public static float getSpeed() {
      return (float)Math.sqrt(OringoClient.mc.field_71439_g.field_70159_w * OringoClient.mc.field_71439_g.field_70159_w + OringoClient.mc.field_71439_g.field_70179_y * OringoClient.mc.field_71439_g.field_70179_y);
   }

   public static float getSpeed(double x, double z) {
      return (float)Math.sqrt(x * x + z * z);
   }

   public static void strafe() {
      strafe((double)getSpeed());
   }

   public static boolean isMoving() {
      return OringoClient.mc.field_71439_g.field_70701_bs != 0.0F || OringoClient.mc.field_71439_g.field_70702_br != 0.0F;
   }

   public static boolean hasMotion() {
      return OringoClient.mc.field_71439_g.field_70159_w != 0.0D && OringoClient.mc.field_71439_g.field_70179_y != 0.0D && OringoClient.mc.field_71439_g.field_70181_x != 0.0D;
   }

   public static boolean isOnGround(double height) {
      return !OringoClient.mc.field_71441_e.func_72945_a(OringoClient.mc.field_71439_g, OringoClient.mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, -height, 0.0D)).isEmpty();
   }

   public static void strafe(double speed) {
      if (isMoving()) {
         double yaw = getDirection();
         OringoClient.mc.field_71439_g.field_70159_w = -Math.sin(yaw) * speed;
         OringoClient.mc.field_71439_g.field_70179_y = Math.cos(yaw) * speed;
         strafeTimer.reset();
      }
   }

   public static void strafe(float speed, float yaw) {
      if (isMoving() && strafeTimer.hasTimePassed(150L)) {
         OringoClient.mc.field_71439_g.field_70159_w = -Math.sin(Math.toRadians((double)yaw)) * (double)speed;
         OringoClient.mc.field_71439_g.field_70179_y = Math.cos(Math.toRadians((double)yaw)) * (double)speed;
         strafeTimer.reset();
      }
   }

   public static void forward(double length) {
      double yaw = Math.toRadians((double)OringoClient.mc.field_71439_g.field_70177_z);
      OringoClient.mc.field_71439_g.func_70107_b(OringoClient.mc.field_71439_g.field_70165_t + -Math.sin(yaw) * length, OringoClient.mc.field_71439_g.field_70163_u, OringoClient.mc.field_71439_g.field_70161_v + Math.cos(yaw) * length);
   }

   public static double getDirection() {
      return Math.toRadians((double)getYaw());
   }

   public static void setMotion(MoveEvent em, double speed) {
      double forward = (double)OringoClient.mc.field_71439_g.field_71158_b.field_78900_b;
      double strafe = (double)OringoClient.mc.field_71439_g.field_71158_b.field_78902_a;
      float yaw = (KillAura.target == null || !OringoClient.killAura.movementFix.isEnabled()) && !OringoClient.targetStrafe.isUsing() ? OringoClient.mc.field_71439_g.field_70177_z : RotationUtils.getRotations(KillAura.target).getYaw();
      if (forward == 0.0D && strafe == 0.0D) {
         OringoClient.mc.field_71439_g.field_70159_w = 0.0D;
         OringoClient.mc.field_71439_g.field_70179_y = 0.0D;
         if (em != null) {
            em.setX(0.0D);
            em.setZ(0.0D);
         }
      } else {
         if (forward != 0.0D) {
            if (strafe > 0.0D) {
               yaw += (float)(forward > 0.0D ? -45 : 45);
            } else if (strafe < 0.0D) {
               yaw += (float)(forward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (forward > 0.0D) {
               forward = 1.0D;
            } else if (forward < 0.0D) {
               forward = -1.0D;
            }
         }

         double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
         double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
         OringoClient.mc.field_71439_g.field_70159_w = forward * speed * cos + strafe * speed * sin;
         OringoClient.mc.field_71439_g.field_70179_y = forward * speed * sin - strafe * speed * cos;
         if (em != null) {
            em.setX(OringoClient.mc.field_71439_g.field_70159_w);
            em.setZ(OringoClient.mc.field_71439_g.field_70179_y);
         }
      }

   }

   public static float getYaw() {
      float yaw = (KillAura.target == null || !OringoClient.killAura.movementFix.isEnabled()) && !OringoClient.targetStrafe.isUsing() ? OringoClient.mc.field_71439_g.field_70177_z : RotationUtils.getRotations(KillAura.target).getYaw();
      if (OringoClient.mc.field_71439_g.field_70701_bs < 0.0F) {
         yaw += 180.0F;
      }

      float forward = 1.0F;
      if (OringoClient.mc.field_71439_g.field_70701_bs < 0.0F) {
         forward = -0.5F;
      } else if (OringoClient.mc.field_71439_g.field_70701_bs > 0.0F) {
         forward = 0.5F;
      }

      if (OringoClient.mc.field_71439_g.field_70702_br > 0.0F) {
         yaw -= 90.0F * forward;
      }

      if (OringoClient.mc.field_71439_g.field_70702_br < 0.0F) {
         yaw += 90.0F * forward;
      }

      return yaw;
   }
}
