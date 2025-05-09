package me.oringo.oringoclient.mixins;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Disabler;
import me.oringo.oringoclient.utils.PacketUtils;
import me.oringo.oringoclient.utils.TimerUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetworkManager.class})
public abstract class Packets {
   private static Vec3 initialPosition;

   @Inject(
      method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
      if (packet instanceof C03PacketPlayer && (Disabler.timerSemi.isEnabled() && !((C03PacketPlayer)packet).func_149466_j() || OringoClient.mc.field_71439_g == null || (float)OringoClient.mc.field_71439_g.field_70173_aa < 80.0F * TimerUtil.getTimer().field_74278_d)) {
         if (OringoClient.disabler.isToggled()) {
            Disabler.wasEnabled = true;
            callbackInfo.cancel();
            return;
         }

         Disabler.wasEnabled = false;
      }

      if (!PacketUtils.noEvent.contains(packet) && MinecraftForge.EVENT_BUS.post(new PacketSentEvent(packet))) {
         callbackInfo.cancel();
      }

   }

   @Inject(
      method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void onSendPacketPost(Packet<?> packet, CallbackInfo callbackInfo) {
      if (!PacketUtils.noEvent.contains(packet) && MinecraftForge.EVENT_BUS.post(new PacketSentEvent.Post(packet))) {
         callbackInfo.cancel();
      }

      PacketUtils.noEvent.remove(packet);
   }

   @Inject(
      method = {"sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;[Lio/netty/util/concurrent/GenericFutureListener;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendPacket2(Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>[] listeners, CallbackInfo ci) {
      if (packetIn instanceof C03PacketPlayer && (OringoClient.mc.field_71439_g == null || (float)OringoClient.mc.field_71439_g.field_70173_aa < 80.0F * TimerUtil.getTimer().field_74278_d)) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"channelRead0"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onChannelReadHead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
      if (packet instanceof S01PacketJoinGame) {
         MinecraftForge.EVENT_BUS.post(new WorldJoinEvent());
      }

      if (!PacketUtils.noEvent.contains(packet) && MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent(packet, context))) {
         callbackInfo.cancel();
      }

   }

   @Inject(
      method = {"channelRead0"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void onPost(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
      if (!PacketUtils.noEvent.contains(packet) && MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent.Post(packet, context))) {
         callbackInfo.cancel();
      }

      PacketUtils.noEvent.remove(packet);
   }
}
