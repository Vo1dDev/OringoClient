package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MoveFlyingEvent extends Event {
   private float friction;
   private float yaw;
   private float forward;
   private float strafe;

   public float getYaw() {
      return this.yaw;
   }

   public float getForward() {
      return this.forward;
   }

   public float getFriction() {
      return this.friction;
   }

   public float getStrafe() {
      return this.strafe;
   }

   public MoveFlyingEvent setYaw(float yaw) {
      this.yaw = yaw;
      return this;
   }

   public MoveFlyingEvent setForward(float forward) {
      this.forward = forward;
      return this;
   }

   public MoveFlyingEvent setFriction(float friction) {
      this.friction = friction;
      return this;
   }

   public MoveFlyingEvent setStrafe(float strafe) {
      this.strafe = strafe;
      return this;
   }

   public MoveFlyingEvent(float forward, float strafe, float friction, float yaw) {
      this.forward = forward;
      this.friction = friction;
      this.strafe = strafe;
      this.yaw = yaw;
   }
}
