package me.oringo.oringoclient.events;

import me.oringo.oringoclient.utils.Rotation;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MotionUpdateEvent extends Event {
   public float yaw;
   public float pitch;
   public double x;
   public double y;
   public double z;
   public boolean onGround;
   public boolean sprinting;
   public boolean sneaking;

   protected MotionUpdateEvent(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean sprinting, boolean sneaking) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.onGround = onGround;
      this.sneaking = sneaking;
      this.sprinting = sprinting;
   }

   public MotionUpdateEvent setPosition(double x, double y, double z) {
      this.x = x;
      this.z = z;
      this.y = y;
      return this;
   }

   public MotionUpdateEvent setRotation(float yaw, float pitch) {
      this.pitch = pitch;
      this.yaw = yaw;
      return this;
   }

   public MotionUpdateEvent setRotation(Rotation rotation) {
      return this.setRotation(rotation.getYaw(), rotation.getPitch());
   }

   public MotionUpdateEvent setPosition(Vec3 vec3) {
      return this.setPosition(vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c);
   }

   public Rotation getRotation() {
      return new Rotation(this.yaw, this.pitch);
   }

   public Vec3 getPosition() {
      return new Vec3(this.x, this.y, this.z);
   }

   public MotionUpdateEvent setY(double y) {
      this.y = y;
      return this;
   }

   public MotionUpdateEvent setX(double x) {
      this.x = x;
      return this;
   }

   public MotionUpdateEvent setZ(double z) {
      this.z = z;
      return this;
   }

   public MotionUpdateEvent setYaw(float yaw) {
      this.yaw = yaw;
      return this;
   }

   public MotionUpdateEvent setPitch(float pitch) {
      this.pitch = pitch;
      return this;
   }

   public MotionUpdateEvent setOnGround(boolean onGround) {
      this.onGround = onGround;
      return this;
   }

   public MotionUpdateEvent setSneaking(boolean sneaking) {
      this.sneaking = sneaking;
      return this;
   }

   public MotionUpdateEvent setSprinting(boolean sprinting) {
      this.sprinting = sprinting;
      return this;
   }

   public boolean isPre() {
      return this instanceof MotionUpdateEvent.Pre;
   }

   @Cancelable
   public static class Post extends MotionUpdateEvent {
      public Post(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean sprinting, boolean sneaking) {
         super(x, y, z, yaw, pitch, onGround, sprinting, sneaking);
      }

      public Post(MotionUpdateEvent event) {
         super(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround, event.sprinting, event.sneaking);
      }
   }

   @Cancelable
   public static class Pre extends MotionUpdateEvent {
      public Pre(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean sprinting, boolean sneaking) {
         super(x, y, z, yaw, pitch, onGround, sprinting, sneaking);
      }
   }
}
