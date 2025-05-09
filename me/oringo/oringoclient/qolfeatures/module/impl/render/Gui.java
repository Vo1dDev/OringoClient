package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import me.oringo.oringoclient.ui.gui.ClickGUI;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import me.oringo.oringoclient.utils.font.MinecraftFontRenderer;
import me.oringo.oringoclient.utils.shader.BlurUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;

public class Gui extends Module {
   public ClickGUI clickGUI = new ClickGUI();
   public ModeSetting colorMode = new ModeSetting("Mode", "Color shift", new String[]{"Rainbow", "Color shift", "Astolfo", "Pulse", "Custom"});
   public NumberSetting redCustom = new NumberSetting("Red", 0.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Custom") && !this.colorMode.is("Pulse");
   });
   public NumberSetting greenCustom = new NumberSetting("Green", 80.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Custom") && !this.colorMode.is("Pulse");
   });
   public NumberSetting blueCustom = new NumberSetting("Blue", 255.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Custom") && !this.colorMode.is("Pulse");
   });
   public NumberSetting redShift1 = new NumberSetting("Red 1 ", 0.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Color shift");
   });
   public NumberSetting greenShift1 = new NumberSetting("Green 1 ", 255.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Color shift");
   });
   public NumberSetting blueShift1 = new NumberSetting("Blue 1 ", 110.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Color shift");
   });
   public NumberSetting redShift2 = new NumberSetting("Red 2 ", 255.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Color shift");
   });
   public NumberSetting greenShift2 = new NumberSetting("Green 2 ", 175.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Color shift");
   });
   public NumberSetting blueShift2 = new NumberSetting("Blue 2 ", 255.0D, 0.0D, 255.0D, 1.0D, (aBoolean) -> {
      return !this.colorMode.is("Color shift");
   });
   public NumberSetting shiftSpeed = new NumberSetting("Shift Speed ", 1.0D, 0.1D, 5.0D, 0.1D, (aBoolean) -> {
      return !this.colorMode.is("Color shift") && !this.colorMode.is("Pulse") && !this.colorMode.is("Astolfo");
   });
   public NumberSetting rgbSpeed = new NumberSetting("Rainbow Speed", 2.5D, 0.1D, 5.0D, 0.1D, (aBoolean) -> {
      return !this.colorMode.is("Rainbow");
   });
   public ModeSetting blur = new ModeSetting("Blur strength", "Low", new String[]{"None", "Low", "High"});
   public BooleanSetting scaleGui = new BooleanSetting("Scale gui", false);
   public BooleanSetting arrayList = new BooleanSetting("ArrayList", true);
   public BooleanSetting disableNotifs = new BooleanSetting("Disable notifications", false);
   public BooleanSetting arrayBlur = new BooleanSetting("Array background", true);
   public BooleanSetting arrayOutline = new BooleanSetting("Array line", true);
   public BooleanSetting waterMark = new BooleanSetting("Watermark", true);
   public BooleanSetting hsb = new BooleanSetting("HSB ", true, (aBoolean) -> {
      return !this.colorMode.is("Color shift");
   });
   public static final StringSetting commandPrefix = new StringSetting("Prefix", ".", 1);
   public static final StringSetting clientName = new StringSetting("Client Name", "", 20);

   public Gui() {
      super("Click Gui", 54, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.colorMode, this.hsb, this.rgbSpeed, this.shiftSpeed, this.redCustom, this.greenCustom, this.blueCustom, this.redShift1, this.greenShift1, this.blueShift1, this.redShift2, this.greenShift2, this.blueShift2, commandPrefix, this.blur, this.waterMark, clientName, this.arrayList, this.arrayOutline, this.arrayBlur, this.disableNotifs, this.scaleGui});
   }

   public Color getColor() {
      return this.getColor(0);
   }

   public Color getColor(int index) {
      String var2 = this.colorMode.getSelected();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1656737386:
         if (var2.equals("Rainbow")) {
            var3 = 1;
         }
         break;
      case 77474681:
         if (var2.equals("Pulse")) {
            var3 = 2;
         }
         break;
      case 961091784:
         if (var2.equals("Astolfo")) {
            var3 = 3;
         }
         break;
      case 1188757861:
         if (var2.equals("Color shift")) {
            var3 = 0;
         }
      }

      switch(var3) {
      case 0:
         float location = (float)((Math.cos(((double)index * 450.0D + (double)System.currentTimeMillis() * this.shiftSpeed.getValue()) / 1000.0D) + 1.0D) * 0.5D);
         if (!this.hsb.isEnabled()) {
            return new Color((int)(this.redShift1.getValue() + (this.redShift2.getValue() - this.redShift1.getValue()) * (double)location), (int)(this.greenShift1.getValue() + (this.greenShift2.getValue() - this.greenShift1.getValue()) * (double)location), (int)(this.blueShift1.getValue() + (this.blueShift2.getValue() - this.blueShift1.getValue()) * (double)location));
         }

         float[] c1 = Color.RGBtoHSB((int)this.redShift1.getValue(), (int)this.greenShift1.getValue(), (int)this.blueShift1.getValue(), (float[])null);
         float[] c2 = Color.RGBtoHSB((int)this.redShift2.getValue(), (int)this.greenShift2.getValue(), (int)this.blueShift2.getValue(), (float[])null);
         return Color.getHSBColor(c1[0] + (c2[0] - c1[0]) * location, c1[1] + (c2[1] - c1[1]) * location, c1[2] + (c2[2] - c1[2]) * location);
      case 1:
         return Color.getHSBColor((float)(((double)index * 100.0D + (double)System.currentTimeMillis() * this.rgbSpeed.getValue()) / 5000.0D % 1.0D), 0.8F, 1.0F);
      case 2:
         Color baseColor = new Color((int)this.redCustom.getValue(), (int)this.greenCustom.getValue(), (int)this.blueCustom.getValue(), 255);
         return RenderUtils.interpolateColor(baseColor, baseColor.darker().darker(), (float)((Math.sin(((double)index * 450.0D + (double)System.currentTimeMillis() * this.shiftSpeed.getValue()) / 1000.0D) + 1.0D) * 0.5D));
      case 3:
         float pos = (float)((Math.cos(((double)index * 450.0D + (double)System.currentTimeMillis() * this.shiftSpeed.getValue()) / 1000.0D) + 1.0D) * 0.5D);
         return Color.getHSBColor(0.5F + 0.4F * pos, 0.6F, 1.0F);
      default:
         return new Color((int)this.redCustom.getValue(), (int)this.greenCustom.getValue(), (int)this.blueCustom.getValue(), 255);
      }
   }

   @SubscribeEvent
   public void onRender(Post event) {
      if (event.type == ElementType.CROSSHAIRS) {
         if (this.waterMark.isEnabled()) {
            if (clientName.getValue().length() == 0) {
               Fonts.tahomaBold.drawSmoothString("ringo", (double)(Fonts.tahomaBold.drawSmoothString("O", 5.0D, 5.0F, this.getColor().getRGB()) + 1.0F), 5.0F, Color.white.getRGB());
            } else {
               float f = Fonts.tahomaBold.drawSmoothString(String.valueOf(clientName.getValue().charAt(0)), 5.0D, 5.0F, this.getColor().getRGB()) + 1.0F;
               if (clientName.getValue().length() > 1) {
                  Fonts.tahomaBold.drawSmoothString(clientName.getValue().substring(1), (double)f, 5.0F, Color.white.getRGB());
               }
            }
         }

         if (this.arrayList.isEnabled()) {
            MinecraftFontRenderer font = Fonts.tahomaSmall;
            GL11.glPushMatrix();
            ScaledResolution resolution = new ScaledResolution(mc);
            List<Module> list = (List)OringoClient.modules.stream().filter((modulex) -> {
               return modulex.getCategory() != Module.Category.RENDER && modulex.getCategory() != Module.Category.KEYBINDS && (modulex.isToggled() || modulex.toggledTime.getTimePassed() <= 250L);
            }).sorted(Comparator.comparingDouble((modulex) -> {
               return font.getStringWidth(modulex.getName());
            })).collect(Collectors.toList());
            Collections.reverse(list);
            float y = 2.0F;
            int x = list.size();
            Iterator var7 = list.iterator();

            float height;
            while(var7.hasNext()) {
               Module module = (Module)var7.next();
               --x;
               GL11.glPushMatrix();
               float width = (float)(font.getStringWidth(module.getName()) + 5.0D);
               float translatedWidth = width * Math.max(Math.min(module.isToggled() ? (250.0F - (float)module.toggledTime.getTimePassed()) / 250.0F : (float)module.toggledTime.getTimePassed() / 250.0F, 1.0F), 0.0F);
               GL11.glTranslated((double)translatedWidth, 0.0D, 0.0D);
               height = (float)(font.getHeight() + 5);
               if (this.arrayBlur.isEnabled()) {
                  for(float i = 0.0F; i < 3.0F; i += 0.5F) {
                     RenderUtils.drawRect((float)(resolution.func_78326_a() - 1) - width - i, y + i, (float)resolution.func_78326_a(), y + ((float)font.getHeight() + 5.0F) * Math.max(Math.min(module.isToggled() ? (float)module.toggledTime.getTimePassed() / 250.0F : (250.0F - (float)module.toggledTime.getTimePassed()) / 250.0F, 1.0F), 0.0F) + i, (new Color(20, 20, 20, 40)).getRGB());
                  }

                  BlurUtils.renderBlurredBackground(10.0F, (float)resolution.func_78326_a() - translatedWidth, (float)resolution.func_78328_b(), (float)(resolution.func_78326_a() - 1) - width, y, width, height);
                  RenderUtils.drawRect((float)(resolution.func_78326_a() - 1) - width, y, (float)(resolution.func_78326_a() - 1), y + height, (new Color(19, 19, 19, 70)).getRGB());
               }

               font.drawSmoothCenteredString(module.getName(), (float)(resolution.func_78326_a() - 1) - width / 2.0F + 0.4F, y + height / 2.0F - (float)font.getHeight() / 2.0F + 0.5F, (new Color(20, 20, 20)).getRGB());
               font.drawSmoothCenteredString(module.getName(), (float)(resolution.func_78326_a() - 1) - width / 2.0F, y + height / 2.0F - (float)font.getHeight() / 2.0F, this.getColor(x).getRGB(), this.getColor(x - 1).getRGB());
               y += (float)(font.getHeight() + 5) * Math.max(Math.min(module.isToggled() ? (float)module.toggledTime.getTimePassed() / 250.0F : (250.0F - (float)module.toggledTime.getTimePassed()) / 250.0F, 1.0F), 0.0F);
               GL11.glPopMatrix();
            }

            x = list.size();
            y = 2.0F;
            if (this.arrayOutline.isEnabled()) {
               Tessellator tessellator = Tessellator.func_178181_a();
               WorldRenderer worldrenderer = tessellator.func_178180_c();
               GlStateManager.func_179147_l();
               GlStateManager.func_179090_x();
               GlStateManager.func_179120_a(770, 771, 1, 0);
               GlStateManager.func_179103_j(7425);
               worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);

               for(Iterator var16 = list.iterator(); var16.hasNext(); y += height) {
                  Module module = (Module)var16.next();
                  --x;
                  height = ((float)font.getHeight() + 5.0F) * Math.max(Math.min(module.isToggled() ? (float)module.toggledTime.getTimePassed() / 250.0F : (250.0F - (float)module.toggledTime.getTimePassed()) / 250.0F, 1.0F), 0.0F);
                  addVertex((float)(resolution.func_78326_a() - 1), y, (float)resolution.func_78326_a(), y + height, this.getColor(x - 1).getRGB(), this.getColor(x).getRGB());
               }

               tessellator.func_78381_a();
               GlStateManager.func_179103_j(7424);
            }

            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
         }
      }

   }

   public static void addVertex(float left, float top, float right, float bottom, int color, int color2) {
      float f3;
      if (left < right) {
         f3 = left;
         left = right;
         right = f3;
      }

      if (top < bottom) {
         f3 = top;
         top = bottom;
         bottom = f3;
      }

      f3 = (float)(color >> 24 & 255) / 255.0F;
      float f = (float)(color >> 16 & 255) / 255.0F;
      float f1 = (float)(color >> 8 & 255) / 255.0F;
      float f2 = (float)(color & 255) / 255.0F;
      float ff3 = (float)(color2 >> 24 & 255) / 255.0F;
      float ff = (float)(color2 >> 16 & 255) / 255.0F;
      float ff1 = (float)(color2 >> 8 & 255) / 255.0F;
      float ff2 = (float)(color2 & 255) / 255.0F;
      Tessellator tessellator = Tessellator.func_178181_a();
      WorldRenderer worldrenderer = tessellator.func_178180_c();
      worldrenderer.func_181662_b((double)left, (double)bottom, 0.0D).func_181666_a(ff, ff1, ff2, ff3).func_181675_d();
      worldrenderer.func_181662_b((double)right, (double)bottom, 0.0D).func_181666_a(ff, ff1, ff2, ff3).func_181675_d();
      worldrenderer.func_181662_b((double)right, (double)top, 0.0D).func_181666_a(f, f1, f2, f3).func_181675_d();
      worldrenderer.func_181662_b((double)left, (double)top, 0.0D).func_181666_a(f, f1, f2, f3).func_181675_d();
   }

   public float getHeight() {
      if (!this.arrayList.isEnabled()) {
         return 0.0F;
      } else {
         List<Module> list = (List)OringoClient.modules.stream().filter((modulex) -> {
            return modulex.getCategory() != Module.Category.RENDER && modulex.getCategory() != Module.Category.KEYBINDS && (modulex.isToggled() || modulex.toggledTime.getTimePassed() <= 250L);
         }).sorted(Comparator.comparingDouble((modulex) -> {
            return Fonts.tahomaSmall.getStringWidth(modulex.getName());
         })).collect(Collectors.toList());
         float y = 3.0F;

         Module module;
         for(Iterator var3 = list.iterator(); var3.hasNext(); y += ((float)Fonts.tahomaSmall.getHeight() + 5.0F) * Math.max(Math.min(module.isToggled() ? (float)module.toggledTime.getTimePassed() / 250.0F : (250.0F - (float)module.toggledTime.getTimePassed()) / 250.0F, 1.0F), 0.0F)) {
            module = (Module)var3.next();
         }

         return y;
      }
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (this.isToggled()) {
         mc.func_147108_a(this.clickGUI);
         this.toggle();
      }

      if (mc.field_71462_r instanceof ClickGUI) {
         KeyBinding.func_74510_a(mc.field_71474_y.field_74351_w.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74351_w));
         KeyBinding.func_74510_a(mc.field_71474_y.field_74368_y.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74368_y));
         KeyBinding.func_74510_a(mc.field_71474_y.field_74370_x.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74370_x));
         KeyBinding.func_74510_a(mc.field_71474_y.field_74366_z.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74366_z));
         KeyBinding.func_74510_a(mc.field_71474_y.field_74314_A.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74314_A));
         KeyBinding.func_74510_a(mc.field_71474_y.field_151444_V.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_151444_V));
         KeyBinding.func_74510_a(mc.field_71474_y.field_74311_E.func_151463_i(), GameSettings.func_100015_a(mc.field_71474_y.field_74311_E));
      }

   }
}
