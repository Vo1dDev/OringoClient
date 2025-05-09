package com.jagrosh.discordipc.entities;

import java.util.function.Consumer;

public class Callback {
   private final Consumer<Packet> success;
   private final Consumer<String> failure;

   public Callback() {
      this((Consumer)((Consumer)null), (Consumer)null);
   }

   public Callback(Consumer<Packet> success) {
      this((Consumer)success, (Consumer)null);
   }

   public Callback(Consumer<Packet> success, Consumer<String> failure) {
      this.success = success;
      this.failure = failure;
   }

   /** @deprecated */
   @Deprecated
   public Callback(Runnable success, Consumer<String> failure) {
      this((p) -> {
         success.run();
      }, failure);
   }

   /** @deprecated */
   @Deprecated
   public Callback(Runnable success) {
      this((Consumer)((p) -> {
         success.run();
      }), (Consumer)null);
   }

   public boolean isEmpty() {
      return this.success == null && this.failure == null;
   }

   public void succeed(Packet packet) {
      if (this.success != null) {
         this.success.accept(packet);
      }

   }

   public void fail(String message) {
      if (this.failure != null) {
         this.failure.accept(message);
      }

   }
}
