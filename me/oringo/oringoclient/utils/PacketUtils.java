package me.oringo.oringoclient.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import me.oringo.oringoclient.OringoClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;

public class PacketUtils {
   public static ArrayList<Packet<?>> noEvent = new ArrayList();

   public static void sendPacketNoEvent(Packet<?> packet) {
      noEvent.add(packet);
      OringoClient.mc.func_147114_u().func_147298_b().func_179290_a(packet);
   }

   public static C06PacketPlayerPosLook getResponse(S08PacketPlayerPosLook packet) {
      double x = packet.func_148932_c();
      double y = packet.func_148928_d();
      double z = packet.func_148933_e();
      float yaw = packet.func_148931_f();
      float pitch = packet.func_148930_g();
      if (packet.func_179834_f().contains(EnumFlags.X)) {
         x += OringoClient.mc.field_71439_g.field_70165_t;
      }

      if (packet.func_179834_f().contains(EnumFlags.Y)) {
         y += OringoClient.mc.field_71439_g.field_70163_u;
      }

      if (packet.func_179834_f().contains(EnumFlags.Z)) {
         z += OringoClient.mc.field_71439_g.field_70161_v;
      }

      if (packet.func_179834_f().contains(EnumFlags.X_ROT)) {
         pitch += OringoClient.mc.field_71439_g.field_70125_A;
      }

      if (packet.func_179834_f().contains(EnumFlags.Y_ROT)) {
         yaw += OringoClient.mc.field_71439_g.field_70177_z;
      }

      return new C06PacketPlayerPosLook(x, y, z, yaw % 360.0F, pitch % 360.0F, false);
   }

   public static String packetToString(Packet<?> packet) {
      StringBuilder postfix = new StringBuilder();
      boolean first = true;
      Field[] var3 = packet.getClass().getDeclaredFields();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Field field = var3[var5];
         field.setAccessible(true);

         try {
            postfix.append(first ? "" : ", ").append(field.get(packet));
         } catch (IllegalAccessException var8) {
            var8.printStackTrace();
         }

         first = false;
      }

      return packet.getClass().getSimpleName() + String.format("{%s}", postfix);
   }
}
