package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class VelocityEvent extends Event {
   private double x;
   private double y;
   private double z;

   public VelocityEvent setY(double y) {
      this.y = y;
      return this;
   }

   public VelocityEvent setX(double x) {
      this.x = x;
      return this;
   }

   public VelocityEvent setZ(double z) {
      this.z = z;
      return this;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public VelocityEvent setMotion(double x, double y, double z) {
      this.x = x;
      this.z = z;
      this.y = y;
      return this;
   }

   public VelocityEvent(double x, double y, double z) {
      this.x = x;
      this.z = z;
      this.y = y;
   }
}
