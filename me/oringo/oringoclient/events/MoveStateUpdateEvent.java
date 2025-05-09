package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MoveStateUpdateEvent extends Event {
   private float forward;
   private float strafe;
   private boolean jump;
   private boolean sneak;

   public MoveStateUpdateEvent(float forward, float strafe, boolean jump, boolean sneak) {
      this.forward = forward;
      this.jump = jump;
      this.strafe = strafe;
      this.sneak = sneak;
   }

   public float getStrafe() {
      return this.strafe;
   }

   public float getForward() {
      return this.forward;
   }

   public boolean isJump() {
      return this.jump;
   }

   public boolean isSneak() {
      return this.sneak;
   }

   public MoveStateUpdateEvent setStrafe(float strafe) {
      this.strafe = strafe;
      return this;
   }

   public MoveStateUpdateEvent setForward(float forward) {
      this.forward = forward;
      return this;
   }

   public MoveStateUpdateEvent setJump(boolean jump) {
      this.jump = jump;
      return this;
   }

   public MoveStateUpdateEvent setSneak(boolean sneak) {
      this.sneak = sneak;
      return this;
   }
}
