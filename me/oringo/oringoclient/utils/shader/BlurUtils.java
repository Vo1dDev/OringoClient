package me.oringo.oringoclient.utils.shader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.util.Matrix4f;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class BlurUtils {
   private static HashMap<Float, BlurUtils.OutputStuff> blurOutput = new HashMap();
   private static HashMap<Float, Long> lastBlurUse = new HashMap();
   private static HashSet<Float> requestedBlurs = new HashSet();
   private static int fogColour = 0;
   private static boolean registered = false;
   private static Framebuffer blurOutputHorz = null;

   public static void registerListener() {
      if (!registered) {
         registered = true;
         MinecraftForge.EVENT_BUS.register(new BlurUtils());
      }

   }

   public static void processBlurs() {
      long currentTime = System.currentTimeMillis();

      float blur;
      BlurUtils.OutputStuff output;
      for(Iterator var2 = requestedBlurs.iterator(); var2.hasNext(); blurBackground(output, blur)) {
         blur = (Float)var2.next();
         lastBlurUse.put(blur, currentTime);
         int width = Minecraft.func_71410_x().field_71443_c;
         int height = Minecraft.func_71410_x().field_71440_d;
         output = (BlurUtils.OutputStuff)blurOutput.computeIfAbsent(blur, (k) -> {
            Framebuffer fb = new Framebuffer(width, height, false);
            fb.func_147607_a(9728);
            return new BlurUtils.OutputStuff(fb, (Shader)null, (Shader)null);
         });
         if (output.framebuffer.field_147621_c != width || output.framebuffer.field_147618_d != height) {
            output.framebuffer.func_147613_a(width, height);
            if (output.blurShaderHorz != null) {
               output.blurShaderHorz.func_148045_a(createProjectionMatrix(width, height));
            }

            if (output.blurShaderVert != null) {
               output.blurShaderVert.func_148045_a(createProjectionMatrix(width, height));
            }
         }
      }

      Set<Float> remove = new HashSet();
      Iterator var8 = lastBlurUse.entrySet().iterator();

      Entry entry;
      while(var8.hasNext()) {
         entry = (Entry)var8.next();
         if (currentTime - (Long)entry.getValue() > 30000L) {
            remove.add(entry.getKey());
         }
      }

      var8 = blurOutput.entrySet().iterator();

      while(var8.hasNext()) {
         entry = (Entry)var8.next();
         if (remove.contains(entry.getKey())) {
            ((BlurUtils.OutputStuff)entry.getValue()).framebuffer.func_147608_a();
            ((BlurUtils.OutputStuff)entry.getValue()).blurShaderHorz.func_148044_b();
            ((BlurUtils.OutputStuff)entry.getValue()).blurShaderVert.func_148044_b();
         }
      }

      lastBlurUse.keySet().removeAll(remove);
      blurOutput.keySet().removeAll(remove);
      requestedBlurs.clear();
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onScreenRender(Pre event) {
      if (event.type == ElementType.ALL) {
         processBlurs();
      }

      Minecraft.func_71410_x().func_147110_a().func_147610_a(false);
   }

   private static Matrix4f createProjectionMatrix(int width, int height) {
      Matrix4f projMatrix = new Matrix4f();
      projMatrix.setIdentity();
      projMatrix.m00 = 2.0F / (float)width;
      projMatrix.m11 = 2.0F / (float)(-height);
      projMatrix.m22 = -0.0020001999F;
      projMatrix.m33 = 1.0F;
      projMatrix.m03 = -1.0F;
      projMatrix.m13 = 1.0F;
      projMatrix.m23 = -1.0001999F;
      return projMatrix;
   }

   private static void blurBackground(BlurUtils.OutputStuff output, float blurFactor) {
      if (OpenGlHelper.func_148822_b() && OpenGlHelper.func_153193_b()) {
         int width = Minecraft.func_71410_x().field_71443_c;
         int height = Minecraft.func_71410_x().field_71440_d;
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         GlStateManager.func_179130_a(0.0D, (double)width, (double)height, 0.0D, 1000.0D, 3000.0D);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179096_D();
         GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
         if (blurOutputHorz == null) {
            blurOutputHorz = new Framebuffer(width, height, false);
            blurOutputHorz.func_147607_a(9728);
         }

         if (blurOutputHorz != null && output != null) {
            if (blurOutputHorz.field_147621_c != width || blurOutputHorz.field_147618_d != height) {
               blurOutputHorz.func_147613_a(width, height);
               Minecraft.func_71410_x().func_147110_a().func_147610_a(false);
            }

            if (output.blurShaderHorz == null) {
               try {
                  output.blurShaderHorz = new Shader(Minecraft.func_71410_x().func_110442_L(), "blur", output.framebuffer, blurOutputHorz);
                  output.blurShaderHorz.func_148043_c().func_147991_a("BlurDir").func_148087_a(1.0F, 0.0F);
                  output.blurShaderHorz.func_148045_a(createProjectionMatrix(width, height));
               } catch (Exception var6) {
               }
            }

            if (output.blurShaderVert == null) {
               try {
                  output.blurShaderVert = new Shader(Minecraft.func_71410_x().func_110442_L(), "blur", blurOutputHorz, output.framebuffer);
                  output.blurShaderVert.func_148043_c().func_147991_a("BlurDir").func_148087_a(0.0F, 1.0F);
                  output.blurShaderVert.func_148045_a(createProjectionMatrix(width, height));
               } catch (Exception var5) {
               }
            }

            if (output.blurShaderHorz != null && output.blurShaderVert != null) {
               if (output.blurShaderHorz.func_148043_c().func_147991_a("Radius") == null) {
                  return;
               }

               output.blurShaderHorz.func_148043_c().func_147991_a("Radius").func_148090_a(blurFactor);
               output.blurShaderVert.func_148043_c().func_147991_a("Radius").func_148090_a(blurFactor);
               GL11.glPushMatrix();
               GL30.glBindFramebuffer(36008, Minecraft.func_71410_x().func_147110_a().field_147616_f);
               GL30.glBindFramebuffer(36009, output.framebuffer.field_147616_f);
               GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, output.framebuffer.field_147621_c, output.framebuffer.field_147618_d, 16384, 9728);
               output.blurShaderHorz.func_148042_a(0.0F);
               output.blurShaderVert.func_148042_a(0.0F);
               GlStateManager.func_179126_j();
               GL11.glPopMatrix();
            }

            Minecraft.func_71410_x().func_147110_a().func_147610_a(false);
         }
      }
   }

   public static void renderBlurredBackground(float blurStrength, float screenWidth, float screenHeight, float x, float y, float blurWidth, float blurHeight) {
      if (OpenGlHelper.func_148822_b() && OpenGlHelper.func_153193_b()) {
         if (!((double)blurStrength < 0.5D)) {
            requestedBlurs.add(blurStrength);
            if (!blurOutput.isEmpty()) {
               BlurUtils.OutputStuff out = (BlurUtils.OutputStuff)blurOutput.get(blurStrength);
               if (out == null) {
                  out = (BlurUtils.OutputStuff)blurOutput.values().iterator().next();
               }

               float uMin = x / screenWidth;
               float uMax = (x + blurWidth) / screenWidth;
               float vMin = (screenHeight - y) / screenHeight;
               float vMax = (screenHeight - y - blurHeight) / screenHeight;
               GlStateManager.func_179132_a(false);
               RenderUtils.drawRect(x, y, x + blurWidth, y + blurHeight, fogColour);
               out.framebuffer.func_147612_c();
               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               drawTexturedRect(x, y, blurWidth, blurHeight, uMin, uMax, vMin, vMax, 9728);
               out.framebuffer.func_147606_d();
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179117_G();
            }
         }
      }
   }

   public static void renderBlurredBackground(float blurStrength) {
      ScaledResolution res = new ScaledResolution(Minecraft.func_71410_x());
      renderBlurredBackground(blurStrength, (float)res.func_78326_a(), (float)res.func_78328_b(), 0.0F, 0.0F, (float)res.func_78326_a(), (float)res.func_78328_b());
   }

   public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
      GlStateManager.func_179147_l();
      GL14.glBlendFuncSeparate(770, 771, 1, 771);
      drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);
      GlStateManager.func_179084_k();
   }

   public static void drawTexturedRectNoBlend(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
      GlStateManager.func_179098_w();
      GL11.glTexParameteri(3553, 10241, filter);
      GL11.glTexParameteri(3553, 10240, filter);
      Tessellator tessellator = Tessellator.func_178181_a();
      WorldRenderer worldrenderer = tessellator.func_178180_c();
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      worldrenderer.func_181662_b((double)x, (double)(y + height), 0.0D).func_181673_a((double)uMin, (double)vMax).func_181675_d();
      worldrenderer.func_181662_b((double)(x + width), (double)(y + height), 0.0D).func_181673_a((double)uMax, (double)vMax).func_181675_d();
      worldrenderer.func_181662_b((double)(x + width), (double)y, 0.0D).func_181673_a((double)uMax, (double)vMin).func_181675_d();
      worldrenderer.func_181662_b((double)x, (double)y, 0.0D).func_181673_a((double)uMin, (double)vMin).func_181675_d();
      tessellator.func_78381_a();
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
   }

   private static class OutputStuff {
      public Framebuffer framebuffer;
      public Shader blurShaderHorz = null;
      public Shader blurShaderVert = null;

      public OutputStuff(Framebuffer framebuffer, Shader blurShaderHorz, Shader blurShaderVert) {
         this.framebuffer = framebuffer;
         this.blurShaderHorz = blurShaderHorz;
         this.blurShaderVert = blurShaderVert;
      }
   }
}
