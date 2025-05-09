package me.oringo.oringoclient.utils;

import net.minecraft.util.MathHelper;

public class Rotation {
   private float yaw;
   private float pitch;

   public Rotation(float yaw, float pitch) {
      this.pitch = pitch;
      this.yaw = yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public Rotation setPitch(float pitch) {
      this.pitch = pitch;
      return this;
   }

   public Rotation setYaw(float yaw) {
      this.yaw = yaw;
      return this;
   }

   public Rotation wrap() {
      this.yaw = MathHelper.func_76142_g(this.yaw);
      this.pitch = MathHelper.func_76142_g(this.pitch);
      return this;
   }
}
