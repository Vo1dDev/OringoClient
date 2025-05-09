package me.oringo.oringoclient.utils;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.mixins.MinecraftAccessor;
import net.minecraft.util.Timer;

public class TimerUtil {
   public static void setSpeed(float speed) {
      getTimer().field_74278_d = speed;
   }

   public static void resetSpeed() {
      setSpeed(1.0F);
   }

   public static Timer getTimer() {
      return ((MinecraftAccessor)OringoClient.mc).getTimer();
   }
}
