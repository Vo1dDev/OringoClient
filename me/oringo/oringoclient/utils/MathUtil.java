package me.oringo.oringoclient.utils;

import java.util.Random;

public class MathUtil {
   private static final Random rand = new Random();

   private boolean isEven(double num) {
      return !this.isOdd(num);
   }

   private boolean isOdd(double num) {
      return !this.isEven(num);
   }

   public static double getRandomInRange(double max, double min) {
      return min + (max - min) * (double)rand.nextFloat();
   }

   public static double clamp(double num, double max, double min) {
      if (max < min) {
         double temp = max;
         max = min;
         min = temp;
      }

      return Math.max(Math.min(max, num), min);
   }

   public static int clamp(int num, int max, int min) {
      if (max < min) {
         int temp = max;
         max = min;
         min = temp;
      }

      return Math.max(Math.min(max, num), min);
   }

   public static float clamp(float num, float max, float min) {
      if (max < min) {
         float temp = max;
         max = min;
         min = temp;
      }

      return Math.max(Math.min(max, num), min);
   }

   public static double hypot(double a, double b) {
      return Math.sqrt(a * a + b * b);
   }
}
