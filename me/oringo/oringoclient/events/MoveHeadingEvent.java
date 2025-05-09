package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MoveHeadingEvent extends Event {
   private boolean onGround;
   private float friction2Multi = 0.91F;

   public MoveHeadingEvent(boolean onGround) {
      this.onGround = onGround;
   }

   public MoveHeadingEvent setOnGround(boolean onGround) {
      this.onGround = onGround;
      return this;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void setFriction2Multi(float friction2Multi) {
      this.friction2Multi = friction2Multi;
   }

   public float getFriction2Multi() {
      return this.friction2Multi;
   }
}
