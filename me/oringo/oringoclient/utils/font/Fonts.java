package me.oringo.oringoclient.utils.font;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Fonts {
   private static Map<String, Font> fontCache = new HashMap();
   public static MinecraftFontRenderer robotoMedium;
   public static MinecraftFontRenderer robotoMediumBold;
   public static MinecraftFontRenderer robotoBig;
   public static MinecraftFontRenderer robotoSmall;
   public static MinecraftFontRenderer tahoma;
   public static MinecraftFontRenderer tahomaBold;
   public static MinecraftFontRenderer tahomaSmall;
   public static MinecraftFontRenderer nameTagFont;

   private Fonts() {
   }

   private static Font getFont(String location, int size) {
      Font font;
      try {
         if (fontCache.containsKey(location)) {
            font = ((Font)fontCache.get(location)).deriveFont(0, (float)size);
         } else {
            InputStream is = Minecraft.func_71410_x().func_110442_L().func_110536_a(new ResourceLocation("oringoclient", "fonts/" + location)).func_110527_b();
            font = Font.createFont(0, is);
            fontCache.put(location, font);
            font = font.deriveFont(0, (float)size);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         System.out.println("Error loading font");
         font = new Font("default", 0, size);
      }

      return font;
   }

   public static void bootstrap() {
      robotoSmall = new MinecraftFontRenderer(getFont("roboto.ttf", 18), true, false);
      robotoMedium = new MinecraftFontRenderer(getFont("roboto.ttf", 19), true, false);
      robotoBig = new MinecraftFontRenderer(getFont("robotoMedium.ttf", 20), true, false);
      robotoMediumBold = new MinecraftFontRenderer(getFont("robotoMedium.ttf", 19), true, false);
      tahomaBold = new MinecraftFontRenderer(getFont("TAHOMAB0.TTF", 22), true, false);
      tahoma = new MinecraftFontRenderer(getFont("TAHOMA_0.TTF", 22), true, false);
      tahomaSmall = robotoMediumBold;
      nameTagFont = new MinecraftFontRenderer(getFont("robotoMedium.ttf", 38), true, false);
   }
}
