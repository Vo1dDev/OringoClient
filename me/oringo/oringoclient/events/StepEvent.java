package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class StepEvent extends Event {
   private double height;

   public StepEvent(double height) {
      this.height = height;
   }

   public double getHeight() {
      return this.height;
   }

   public void setHeight(double height) {
      this.height = height;
   }

   public static class Post extends StepEvent {
      public Post(double height) {
         super(height);
      }
   }

   public static class Pre extends StepEvent {
      public Pre(double height) {
         super(height);
      }
   }
}
