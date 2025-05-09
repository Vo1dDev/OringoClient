package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class UpdateRenderEvent extends Event {
   public float partialTicks;

   public UpdateRenderEvent(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public static class Post extends UpdateRenderEvent {
      public Post(float partialTicks) {
         super(partialTicks);
      }
   }

   public static class Pre extends UpdateRenderEvent {
      public Pre(float partialTicks) {
         super(partialTicks);
      }
   }
}
