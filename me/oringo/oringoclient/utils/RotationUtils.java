package me.oringo.oringoclient.utils;

import java.util.Random;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.mixins.entity.PlayerSPAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtils {
   public static float lastLastReportedPitch;

   private RotationUtils() {
   }

   public static Rotation getClosestRotation(AxisAlignedBB aabb) {
      return getRotations(getClosestPointInAABB(OringoClient.mc.field_71439_g.func_174824_e(1.0F), aabb));
   }

   public static Rotation getClosestRotation(AxisAlignedBB aabb, float offset) {
      return getClosestRotation(aabb.func_72314_b((double)(-offset), (double)(-offset), (double)(-offset)));
   }

   public static Rotation getRotations(EntityLivingBase target) {
      return getRotations(target.field_70165_t, target.field_70163_u + (double)target.func_70047_e() / 2.0D, target.field_70161_v);
   }

   public static Rotation getRotations(EntityLivingBase target, float random) {
      return getRotations(target.field_70165_t + (double)((float)((new Random()).nextInt(3) - 1) * random * (new Random()).nextFloat()), target.field_70163_u + (double)target.func_70047_e() / 2.0D + (double)((float)((new Random()).nextInt(3) - 1) * random * (new Random()).nextFloat()), target.field_70161_v + (double)((float)((new Random()).nextInt(3) - 1) * random * (new Random()).nextFloat()));
   }

   public static double getRotationDifference(Rotation a, Rotation b) {
      return Math.hypot((double)getAngleDifference(a.getYaw(), b.getYaw()), (double)getAngleDifference(a.getPitch(), b.getPitch()));
   }

   public static Rotation getRotations(Vec3 vec3) {
      return getRotations(vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c);
   }

   public static Rotation getScaffoldRotations(BlockPos position) {
      double direction = MovementUtils.getDirection();
      double posX = -Math.sin(direction) * 0.5D;
      double posZ = Math.cos(direction) * 0.5D;
      double x = (double)position.func_177958_n() - OringoClient.mc.field_71439_g.field_70165_t - posX;
      double y = (double)position.func_177956_o() - OringoClient.mc.field_71439_g.field_70167_r - (double)OringoClient.mc.field_71439_g.func_70047_e();
      double z = (double)position.func_177952_p() - OringoClient.mc.field_71439_g.field_70161_v - posZ;
      double distance = Math.hypot(x, z);
      float yaw = (float)(Math.atan2(z, x) * 180.0D / 3.141592653589793D - 90.0D);
      float pitch = (float)(-(Math.atan2(y, distance) * 180.0D / 3.141592653589793D));
      return new Rotation(yaw, pitch);
   }

   public static Rotation getRotations(double posX, double posY, double posZ) {
      double x = posX - OringoClient.mc.field_71439_g.field_70165_t;
      double y = posY - (OringoClient.mc.field_71439_g.field_70163_u + (double)OringoClient.mc.field_71439_g.func_70047_e());
      double z = posZ - OringoClient.mc.field_71439_g.field_70161_v;
      double dist = (double)MathHelper.func_76133_a(x * x + z * z);
      float yaw = (float)(Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F;
      float pitch = (float)(-(Math.atan2(y, dist) * 180.0D / 3.141592653589793D));
      return new Rotation(yaw, pitch);
   }

   public static Rotation getSmoothRotation(Rotation current, Rotation target, float smooth) {
      return new Rotation(current.getYaw() + (target.getYaw() - current.getYaw()) / smooth, current.getPitch() + (target.getPitch() - current.getPitch()) / smooth);
   }

   public static Rotation getLastReportedRotation() {
      return new Rotation(((PlayerSPAccessor)OringoClient.mc.field_71439_g).getLastReportedYaw(), ((PlayerSPAccessor)OringoClient.mc.field_71439_g).getLastReportedPitch());
   }

   public static Rotation getPlayerRotation() {
      return new Rotation(OringoClient.mc.field_71439_g.field_70177_z, OringoClient.mc.field_71439_g.field_70125_A);
   }

   public static Rotation getLimitedRotation(Rotation currentRotation, Rotation targetRotation, float turnSpeed) {
      return new Rotation(currentRotation.getYaw() + MathHelper.func_76131_a(getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw()), -turnSpeed, turnSpeed), currentRotation.getPitch() + MathHelper.func_76131_a(getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch()), -turnSpeed, turnSpeed));
   }

   public static float getAngleDifference(float a, float b) {
      return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
   }

   public static Rotation getBowRotation(Entity entity) {
      double xDelta = (entity.field_70165_t - entity.field_70142_S) * 0.4D;
      double zDelta = (entity.field_70161_v - entity.field_70136_U) * 0.4D;
      double d = (double)OringoClient.mc.field_71439_g.func_70032_d(entity);
      d -= d % 0.8D;
      double xMulti = d / 0.8D * xDelta;
      double zMulti = d / 0.8D * zDelta;
      double x = entity.field_70165_t + xMulti - OringoClient.mc.field_71439_g.field_70165_t;
      double z = entity.field_70161_v + zMulti - OringoClient.mc.field_71439_g.field_70161_v;
      double y = OringoClient.mc.field_71439_g.field_70163_u + (double)OringoClient.mc.field_71439_g.func_70047_e() - (entity.field_70163_u + (double)entity.func_70047_e());
      double dist = (double)OringoClient.mc.field_71439_g.func_70032_d(entity);
      float yaw = (float)Math.toDegrees(Math.atan2(z, x)) - 90.0F;
      double d1 = (double)MathHelper.func_76133_a(x * x + z * z);
      float pitch = (float)(-(Math.atan2(y, d1) * 180.0D / 3.141592653589793D)) + (float)dist * 0.11F;
      return new Rotation(yaw, -pitch);
   }

   public static Vec3 getClosestPointInAABB(Vec3 vec3, AxisAlignedBB aabb) {
      return new Vec3(clamp(aabb.field_72340_a, aabb.field_72336_d, vec3.field_72450_a), clamp(aabb.field_72338_b, aabb.field_72337_e, vec3.field_72448_b), clamp(aabb.field_72339_c, aabb.field_72334_f, vec3.field_72449_c));
   }

   private static double clamp(double min, double max, double value) {
      return Math.max(min, Math.min(max, value));
   }
}
