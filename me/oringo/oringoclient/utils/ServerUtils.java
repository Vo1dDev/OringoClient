package me.oringo.oringoclient.utils;

import me.oringo.oringoclient.OringoClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ServerUtils {
   public static ServerUtils instance = new ServerUtils();
   private static boolean onHypixel;

   @SubscribeEvent
   public void onPacketRecived(ClientConnectedToServerEvent event) {
      if (!event.isLocal) {
         onHypixel = OringoClient.mc.func_147104_D().field_78845_b.toLowerCase().contains("hypixel");
      }

   }

   @SubscribeEvent
   public void onDisconnect(ClientDisconnectionFromServerEvent event) {
      onHypixel = false;
      System.out.println("Detected leaving hypixel");
   }

   public static boolean isOnHypixel() {
      return onHypixel;
   }
}
