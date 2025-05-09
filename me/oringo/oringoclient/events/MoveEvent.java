package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MoveEvent extends Event {
   private double x;
   private double y;
   private double z;

   public MoveEvent setY(double y) {
      this.y = y;
      return this;
   }

   public MoveEvent setX(double x) {
      this.x = x;
      return this;
   }

   public MoveEvent setZ(double z) {
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

   public MoveEvent stop() {
      return this.setMotion(0.0D, 0.0D, 0.0D);
   }

   public MoveEvent setMotion(double x, double y, double z) {
      this.x = x;
      this.z = z;
      this.y = y;
      return this;
   }

   public MoveEvent(double x, double y, double z) {
      this.x = x;
      this.z = z;
      this.y = y;
   }
}
