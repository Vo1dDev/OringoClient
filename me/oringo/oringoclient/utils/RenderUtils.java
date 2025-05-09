package me.oringo.oringoclient.utils;

import java.awt.Color;
import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class RenderUtils {
   public static void scissor(double x, double y, double width, double height) {
      ScaledResolution sr = new ScaledResolution(OringoClient.mc);
      double scale = (double)sr.func_78325_e();
      y = (double)sr.func_78328_b() - y;
      x *= scale;
      y *= scale;
      width *= scale;
      height *= scale;
      GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
   }

   public static void drawCircle(float x, float y, int start, int end, float radius, int color) {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glEnable(2881);
      GL11.glBlendFunc(770, 771);
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glColor4f(f1, f2, f3, f);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0F;
      y *= 2.0F;
      radius *= 2.0F;
      GL11.glBegin(9);

      for(int i = start; i <= end; ++i) {
         double x2 = Math.sin((double)i * 3.141592653589793D / 45.0D) * (double)radius;
         double y2 = Math.cos((double)i * 3.141592653589793D / 45.0D) * (double)radius;
         GL11.glVertex3d((double)x + x2, (double)y + y2, 0.0D);
      }

      GL11.glEnd();
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glDisable(2881);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
   }

   public static void drawRect(float left, float top, float right, float bottom, int color) {
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
      Tessellator tessellator = Tessellator.func_178181_a();
      WorldRenderer worldrenderer = tessellator.func_178180_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179131_c(f, f1, f2, f3);
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
      worldrenderer.func_181662_b((double)left, (double)bottom, 0.0D).func_181675_d();
      worldrenderer.func_181662_b((double)right, (double)bottom, 0.0D).func_181675_d();
      worldrenderer.func_181662_b((double)right, (double)top, 0.0D).func_181675_d();
      worldrenderer.func_181662_b((double)left, (double)top, 0.0D).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int internalColor, int borderColor) {
      enableGL2D();
      drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
      GL11.glPushMatrix();
      drawRect(x + width, y, x1 - width, y + width, borderColor);
      drawRect(x, y, x + width, y1, borderColor);
      drawRect(x1 - width, y, x1, y1, borderColor);
      drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
      GL11.glPopMatrix();
      disableGL2D();
   }

   public static void draw2D(Entity entityLiving, float partialTicks, float lineWidth, Color color) {
      Matrix4f mvMatrix = WorldToScreen.getMatrix(2982);
      Matrix4f projectionMatrix = WorldToScreen.getMatrix(2983);
      GL11.glPushAttrib(8192);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, (double)OringoClient.mc.field_71443_c, (double)OringoClient.mc.field_71440_d, 0.0D, -1.0D, 1.0D);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glDisable(2929);
      GL11.glBlendFunc(770, 771);
      GlStateManager.func_179098_w();
      GlStateManager.func_179132_a(true);
      GL11.glLineWidth(lineWidth);
      RenderManager renderManager = OringoClient.mc.func_175598_ae();
      AxisAlignedBB bb = entityLiving.func_174813_aQ().func_72317_d(-entityLiving.field_70165_t, -entityLiving.field_70163_u, -entityLiving.field_70161_v).func_72317_d(entityLiving.field_70142_S + (entityLiving.field_70165_t - entityLiving.field_70142_S) * (double)partialTicks, entityLiving.field_70137_T + (entityLiving.field_70163_u - entityLiving.field_70137_T) * (double)partialTicks, entityLiving.field_70136_U + (entityLiving.field_70161_v - entityLiving.field_70136_U) * (double)partialTicks).func_72317_d(-renderManager.field_78730_l, -renderManager.field_78731_m, -renderManager.field_78728_n);
      GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
      double[][] boxVertices = new double[][]{{bb.field_72340_a, bb.field_72338_b, bb.field_72339_c}, {bb.field_72340_a, bb.field_72337_e, bb.field_72339_c}, {bb.field_72336_d, bb.field_72337_e, bb.field_72339_c}, {bb.field_72336_d, bb.field_72338_b, bb.field_72339_c}, {bb.field_72340_a, bb.field_72338_b, bb.field_72334_f}, {bb.field_72340_a, bb.field_72337_e, bb.field_72334_f}, {bb.field_72336_d, bb.field_72337_e, bb.field_72334_f}, {bb.field_72336_d, bb.field_72338_b, bb.field_72334_f}};
      float minX = Float.MAX_VALUE;
      float minY = Float.MAX_VALUE;
      float maxX = -1.0F;
      float maxY = -1.0F;
      double[][] var13 = boxVertices;
      int var14 = boxVertices.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         double[] boxVertex = var13[var15];
         Vector2f screenPos = WorldToScreen.worldToScreen(new Vector3f((float)boxVertex[0], (float)boxVertex[1], (float)boxVertex[2]), mvMatrix, projectionMatrix, OringoClient.mc.field_71443_c, OringoClient.mc.field_71440_d);
         if (screenPos != null) {
            minX = Math.min(screenPos.x, minX);
            minY = Math.min(screenPos.y, minY);
            maxX = Math.max(screenPos.x, maxX);
            maxY = Math.max(screenPos.y, maxY);
         }
      }

      if (minX > 0.0F || minY > 0.0F || maxX <= (float)OringoClient.mc.field_71443_c || maxY <= (float)OringoClient.mc.field_71443_c) {
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
         GL11.glBegin(2);
         GL11.glVertex2f(minX, minY);
         GL11.glVertex2f(minX, maxY);
         GL11.glVertex2f(maxX, maxY);
         GL11.glVertex2f(maxX, minY);
         GL11.glEnd();
      }

      GL11.glEnable(2929);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
   }

   public static void tracerLine(Entity entity, float partialTicks, float lineWidth, Color color) {
      double x = entity.field_70169_q + (entity.field_70165_t - entity.field_70169_q) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78730_l;
      double y = entity.field_70167_r + (entity.field_70163_u - entity.field_70167_r) * (double)partialTicks + (double)(entity.field_70131_O / 2.0F) - Minecraft.func_71410_x().func_175598_ae().field_78731_m;
      double z = entity.field_70166_s + (entity.field_70161_v - entity.field_70166_s) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78728_n;
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glLineWidth(lineWidth);
      GL11.glDepthMask(false);
      setColor(color);
      GL11.glBegin(2);
      GL11.glVertex3d(0.0D, (double)Minecraft.func_71410_x().field_71439_g.func_70047_e(), 0.0D);
      GL11.glVertex3d(x, y, z);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
   }

   public static void drawTargetESP(EntityLivingBase target, Color color, float partialTicks) {
      GL11.glPushMatrix();
      float location = (float)((Math.sin((double)System.currentTimeMillis() * 0.005D) + 1.0D) * 0.5D);
      GlStateManager.func_179137_b(target.field_70142_S + (target.field_70165_t - target.field_70142_S) * (double)partialTicks - OringoClient.mc.func_175598_ae().field_78730_l, target.field_70137_T + (target.field_70163_u - target.field_70137_T) * (double)partialTicks - OringoClient.mc.func_175598_ae().field_78731_m + (double)(target.field_70131_O * location), target.field_70136_U + (target.field_70161_v - target.field_70136_U) * (double)partialTicks - OringoClient.mc.func_175598_ae().field_78728_n);
      enableGL2D();
      GL11.glShadeModel(7425);
      GL11.glDisable(2884);
      GL11.glLineWidth(3.0F);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glBegin(3);
      double cos = Math.cos((double)System.currentTimeMillis() * 0.005D);

      int i;
      double x;
      double z;
      for(i = 0; i <= 120; ++i) {
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
         x = Math.cos((double)i * 3.141592653589793D / 60.0D) * (double)target.field_70130_N;
         z = Math.sin((double)i * 3.141592653589793D / 60.0D) * (double)target.field_70130_N;
         GL11.glVertex3d(x, 0.15000000596046448D * cos, z);
      }

      GL11.glEnd();
      GL11.glDisable(2848);
      GL11.glBegin(5);

      for(i = 0; i <= 120; ++i) {
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.5F);
         x = Math.cos((double)i * 3.141592653589793D / 60.0D) * (double)target.field_70130_N;
         z = Math.sin((double)i * 3.141592653589793D / 60.0D) * (double)target.field_70130_N;
         GL11.glVertex3d(x, 0.15000000596046448D * cos, z);
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.2F);
         GL11.glVertex3d(x, -0.15000000596046448D * cos, z);
      }

      GL11.glEnd();
      GL11.glShadeModel(7424);
      GL11.glEnable(2884);
      disableGL2D();
      GL11.glPopMatrix();
   }

   public static void drawFace(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, AbstractClientPlayer target) {
      ResourceLocation skin = target.func_110306_p();
      if (skin != null) {
         Minecraft.func_71410_x().func_110434_K().func_110577_a(skin);
         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.func_152125_a(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
         GL11.glDisable(3042);
      }

   }

   public static void drawRectWith2Colors(float left, float top, float right, float bottom, int color, int color2) {
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
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179103_j(7425);
      GlStateManager.func_179131_c(f, f1, f2, f3);
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      worldrenderer.func_181662_b((double)left, (double)bottom, 0.0D).func_181666_a(ff, ff1, ff2, ff3).func_181675_d();
      worldrenderer.func_181662_b((double)right, (double)bottom, 0.0D).func_181666_a(f, f1, f2, f3).func_181675_d();
      worldrenderer.func_181662_b((double)right, (double)top, 0.0D).func_181666_a(f, f1, f2, f3).func_181675_d();
      worldrenderer.func_181662_b((double)left, (double)top, 0.0D).func_181666_a(ff, ff1, ff2, ff3).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static Color interpolateColor(Color color1, Color color2, float value) {
      return new Color((int)((float)color1.getRed() + (float)(color2.getRed() - color1.getRed()) * value), (int)((float)color1.getGreen() + (float)(color2.getGreen() - color1.getGreen()) * value), (int)((float)color1.getBlue() + (float)(color2.getBlue() - color1.getBlue()) * value));
   }

   public static Color applyOpacity(Color color, int opacity) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
   }

   public static void drawRoundedRect2(double x, double y, double width, double height, double radius, int color) {
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      double x1 = x + width;
      double y1 = y + height;
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0D;
      y *= 2.0D;
      x1 *= 2.0D;
      y1 *= 2.0D;
      GL11.glDisable(3553);
      GL11.glColor4f(f1, f2, f3, f);
      GL11.glEnable(2848);
      GL11.glBegin(9);

      int i;
      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glPopAttrib();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static void drawGradientRoundedRect(double x, double y, double x2, double y2, double radius, int color1, int color2) {
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      float f = (float)(color1 >> 24 & 255) / 255.0F;
      float f1 = (float)(color1 >> 16 & 255) / 255.0F;
      float f2 = (float)(color1 >> 8 & 255) / 255.0F;
      float f3 = (float)(color1 & 255) / 255.0F;
      float f4 = (float)(color2 >> 24 & 255) / 255.0F;
      float f5 = (float)(color2 >> 16 & 255) / 255.0F;
      float f6 = (float)(color2 >> 8 & 255) / 255.0F;
      float f7 = (float)(color2 & 255) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0D;
      y *= 2.0D;
      x2 *= 2.0D;
      y2 *= 2.0D;
      GL11.glShadeModel(7425);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glBegin(9);
      GL11.glColor4f(f1, f2, f3, f);

      int i;
      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y2 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      GL11.glColor4f(f5, f6, f7, f4);

      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x2 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y2 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x2 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glShadeModel(7424);
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glPopAttrib();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static void drawRoundedRect(double x, double y, double x2, double y2, double radius, int color) {
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0D;
      y *= 2.0D;
      x2 *= 2.0D;
      y2 *= 2.0D;
      GL11.glDisable(3553);
      GL11.glColor4f(f1, f2, f3, f);
      GL11.glEnable(2848);
      GL11.glBegin(9);

      int i;
      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y2 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x2 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y2 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x2 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glPopAttrib();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static void drawImage(ResourceLocation image, float x, float y, float width, float height, float alpha) {
      GL11.glPushMatrix();
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glDepthMask(false);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      OringoClient.mc.func_110434_K().func_110577_a(image);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GL11.glEnable(2929);
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
      float f = 1.0F / textureWidth;
      float f1 = 1.0F / textureHeight;
      Tessellator tessellator = Tessellator.func_178181_a();
      WorldRenderer worldrenderer = tessellator.func_178180_c();
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      worldrenderer.func_181662_b((double)x, (double)(y + height), 0.0D).func_181673_a((double)(u * f), (double)((v + height) * f1)).func_181675_d();
      worldrenderer.func_181662_b((double)(x + width), (double)(y + height), 0.0D).func_181673_a((double)((u + width) * f), (double)((v + height) * f1)).func_181675_d();
      worldrenderer.func_181662_b((double)(x + width), (double)y, 0.0D).func_181673_a((double)((u + width) * f), (double)(v * f1)).func_181675_d();
      worldrenderer.func_181662_b((double)x, (double)y, 0.0D).func_181673_a((double)(u * f), (double)(v * f1)).func_181675_d();
      tessellator.func_78381_a();
   }

   public static void enableGL2D() {
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glDepthMask(true);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glHint(3155, 4354);
   }

   public static void disableGL2D() {
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glEnable(2929);
      GL11.glDisable(2848);
      GL11.glHint(3154, 4352);
      GL11.glHint(3155, 4352);
   }

   public static void drawArc(float x, float y, float radius, int angleStart) {
      Tessellator tessellator = Tessellator.func_178181_a();
      WorldRenderer worldrenderer = tessellator.func_178180_c();
      worldrenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);
      GlStateManager.func_179137_b((double)x, (double)y, 0.0D);
      worldrenderer.func_181662_b(0.0D, 0.0D, 0.0D).func_181675_d();
      int points = 20;

      for(double i = 0.0D; i < (double)points; ++i) {
         double radians = Math.toRadians(i / (double)points * 90.0D + (double)angleStart);
         worldrenderer.func_181662_b((double)radius * Math.sin(radians), (double)radius * Math.cos(radians), 0.0D).func_181675_d();
      }

      tessellator.func_78381_a();
      GlStateManager.func_179137_b((double)(-x), (double)(-y), 0.0D);
   }

   public static void tracerLine(double x, double y, double z, Color color) {
      x -= Minecraft.func_71410_x().func_175598_ae().field_78730_l;
      y -= Minecraft.func_71410_x().func_175598_ae().field_78731_m;
      z -= Minecraft.func_71410_x().func_175598_ae().field_78728_n;
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.5F);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      setColor(color);
      GL11.glBegin(1);
      GL11.glVertex3d(0.0D, (double)Minecraft.func_71410_x().field_71439_g.func_70047_e(), 0.0D);
      GL11.glVertex3d(x, y, z);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(2848);
      GL11.glDisable(3042);
   }

   public static void drawLine(Vec3 pos1, Vec3 pos2, Color color) {
      GL11.glPushMatrix();
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.5F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      setColor(color);
      GL11.glTranslated(-Minecraft.func_71410_x().func_175598_ae().field_78730_l, -Minecraft.func_71410_x().func_175598_ae().field_78731_m, -Minecraft.func_71410_x().func_175598_ae().field_78728_n);
      GL11.glBegin(1);
      GL11.glVertex3d(pos1.field_72450_a, pos1.field_72448_b, pos1.field_72449_c);
      GL11.glVertex3d(pos2.field_72450_a, pos2.field_72448_b, pos2.field_72449_c);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   public static void setColor(Color c) {
      GL11.glColor4f((float)c.getRed() / 255.0F, (float)c.getGreen() / 255.0F, (float)c.getBlue() / 255.0F, (float)c.getAlpha() / 255.0F);
   }

   public static void entityESPBox(Entity entity, float partialTicks, Color color) {
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(1.5F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      setColor(color);
      RenderGlobal.func_181561_a(new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - entity.field_70165_t + (entity.field_70169_q + (entity.field_70165_t - entity.field_70169_q) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78730_l), entity.func_174813_aQ().field_72338_b - entity.field_70163_u + (entity.field_70167_r + (entity.field_70163_u - entity.field_70167_r) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78731_m), entity.func_174813_aQ().field_72339_c - entity.field_70161_v + (entity.field_70166_s + (entity.field_70161_v - entity.field_70166_s) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78728_n), entity.func_174813_aQ().field_72336_d - entity.field_70165_t + (entity.field_70169_q + (entity.field_70165_t - entity.field_70169_q) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78730_l), entity.func_174813_aQ().field_72337_e - entity.field_70163_u + (entity.field_70167_r + (entity.field_70163_u - entity.field_70167_r) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78731_m), entity.func_174813_aQ().field_72334_f - entity.field_70161_v + (entity.field_70166_s + (entity.field_70161_v - entity.field_70166_s) * (double)partialTicks - Minecraft.func_71410_x().func_175598_ae().field_78728_n)));
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
   }

   public static void blockBox(TileEntity block, Color color) {
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      setColor(color);
      RenderGlobal.func_181561_a(block.getRenderBoundingBox().func_72317_d(-OringoClient.mc.func_175598_ae().field_78730_l, -OringoClient.mc.func_175598_ae().field_78731_m, -OringoClient.mc.func_175598_ae().field_78728_n));
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
   }

   public static void blockBox(BlockPos block, Color color) {
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      setColor(color);
      RenderGlobal.func_181561_a(new AxisAlignedBB((double)block.func_177958_n() - Minecraft.func_71410_x().func_175598_ae().field_78730_l, (double)block.func_177956_o() - Minecraft.func_71410_x().func_175598_ae().field_78731_m, (double)block.func_177952_p() - Minecraft.func_71410_x().func_175598_ae().field_78728_n, (double)(block.func_177958_n() + 1) - Minecraft.func_71410_x().func_175598_ae().field_78730_l, (double)(block.func_177956_o() + 1) - Minecraft.func_71410_x().func_175598_ae().field_78731_m, (double)(block.func_177952_p() + 1) - Minecraft.func_71410_x().func_175598_ae().field_78728_n));
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
   }

   public static void doScale(double x, double y, double scale) {
      GL11.glTranslated(x, y, 0.0D);
      GL11.glScaled(scale, scale, scale);
      GL11.glTranslated(-x, -y, 0.0D);
   }

   public static void miniBlockBox(Vec3 block, Color color) {
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      setColor(color);
      Minecraft.func_71410_x().func_175598_ae();
      RenderGlobal.func_181561_a(new AxisAlignedBB(block.field_72450_a - 0.05D - Minecraft.func_71410_x().func_175598_ae().field_78730_l, block.field_72448_b - 0.05D - Minecraft.func_71410_x().func_175598_ae().field_78731_m, block.field_72449_c - 0.05D - Minecraft.func_71410_x().func_175598_ae().field_78728_n, block.field_72450_a + 0.05D - Minecraft.func_71410_x().func_175598_ae().field_78730_l, block.field_72448_b + 0.05D - Minecraft.func_71410_x().func_175598_ae().field_78731_m, block.field_72449_c + 0.05D - Minecraft.func_71410_x().func_175598_ae().field_78728_n));
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
   }

   public static void drawBorderedRoundedRect(float x, float y, float width, float height, float radius, float linewidth, int insideC, int borderC) {
      drawRoundedRect((double)x, (double)y, (double)(x + width), (double)(y + height), (double)radius, insideC);
      drawOutlinedRoundedRect(x, y, width, height, radius, linewidth, borderC);
   }

   public static void drawGradientOutlinedRoundedRect(float x, float y, float width, float height, float radius, float linewidth, int color1, int color2, int color3, int color4) {
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      double x1 = (double)(x + width);
      double y1 = (double)(y + height);
      float opacity1 = (float)(color1 >> 24 & 255) / 255.0F;
      float red1 = (float)(color1 >> 16 & 255) / 255.0F;
      float green1 = (float)(color1 >> 8 & 255) / 255.0F;
      float blue1 = (float)(color1 & 255) / 255.0F;
      float opacity2 = (float)(color2 >> 24 & 255) / 255.0F;
      float red2 = (float)(color2 >> 16 & 255) / 255.0F;
      float green2 = (float)(color2 >> 8 & 255) / 255.0F;
      float blue2 = (float)(color2 & 255) / 255.0F;
      float opacity3 = (float)(color3 >> 24 & 255) / 255.0F;
      float red3 = (float)(color3 >> 16 & 255) / 255.0F;
      float green3 = (float)(color3 >> 8 & 255) / 255.0F;
      float blue3 = (float)(color3 & 255) / 255.0F;
      float opacity4 = (float)(color4 >> 24 & 255) / 255.0F;
      float red4 = (float)(color4 >> 16 & 255) / 255.0F;
      float green4 = (float)(color4 >> 8 & 255) / 255.0F;
      float blue4 = (float)(color4 & 255) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0F;
      y *= 2.0F;
      x1 *= 2.0D;
      y1 *= 2.0D;
      GL11.glLineWidth(linewidth);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glShadeModel(7425);
      GL11.glBegin(2);
      GL11.glColor4f(red1, green1, blue1, opacity1);

      int i;
      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d((double)(x + radius) + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F), (double)(y + radius) + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F));
      }

      GL11.glColor4f(red2, green2, blue2, opacity2);

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d((double)(x + radius) + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F), y1 - (double)radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F));
      }

      GL11.glColor4f(red3, green3, blue3, opacity3);

      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x1 - (double)radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)radius, y1 - (double)radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)radius);
      }

      GL11.glColor4f(red4, green4, blue4, opacity4);

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x1 - (double)radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)radius, (double)(y + radius) + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)radius);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glShadeModel(7424);
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glPopAttrib();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static Vec3 getInterpolatedPos(float partialTicks) {
      return new Vec3(interpolate(OringoClient.mc.field_71439_g.field_70169_q, OringoClient.mc.field_71439_g.field_70165_t, partialTicks), interpolate(OringoClient.mc.field_71439_g.field_70167_r, OringoClient.mc.field_71439_g.field_70163_u, partialTicks) + 0.1D, interpolate(OringoClient.mc.field_71439_g.field_70166_s, OringoClient.mc.field_71439_g.field_70161_v, partialTicks));
   }

   public static double interpolate(double prev, double newPos, float partialTicks) {
      return prev + (newPos - prev) * (double)partialTicks;
   }

   public static void drawOutlinedRoundedRect(float x, float y, float width, float height, float radius, float linewidth, int color) {
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      double x1 = (double)(x + width);
      double y1 = (double)(y + height);
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0F;
      y *= 2.0F;
      x1 *= 2.0D;
      y1 *= 2.0D;
      GL11.glLineWidth(linewidth);
      GL11.glDisable(3553);
      GL11.glColor4f(f1, f2, f3, f);
      GL11.glEnable(2848);
      GL11.glBegin(2);

      int i;
      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d((double)(x + radius) + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F), (double)(y + radius) + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F));
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d((double)(x + radius) + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F), y1 - (double)radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)(radius * -1.0F));
      }

      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x1 - (double)radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)radius, y1 - (double)radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)radius);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x1 - (double)radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * (double)radius, (double)(y + radius) + Math.cos((double)i * 3.141592653589793D / 180.0D) * (double)radius);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glPopAttrib();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static void enableChams() {
      GL11.glEnable(32823);
      GlStateManager.func_179088_q();
      GlStateManager.func_179136_a(1.0F, -1000000.0F);
   }

   public static void disableChams() {
      GL11.glDisable(32823);
      GlStateManager.func_179136_a(1.0F, 1000000.0F);
      GlStateManager.func_179113_r();
   }

   public static void unForceColor() {
      MobRenderUtils.unsetColor();
   }

   public static void renderStarredNametag(Entity entityIn, String str, double x, double y, double z, int maxDistance) {
      double d0 = entityIn.func_70068_e(OringoClient.mc.func_175598_ae().field_78734_h);
      if (d0 <= (double)(maxDistance * maxDistance)) {
         FontRenderer fontrenderer = OringoClient.mc.func_175598_ae().func_78716_a();
         float f = 1.6F;
         float f1 = 0.016666668F * f;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)x + 0.0F, (float)y + entityIn.field_70131_O + 0.5F, (float)z);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(-OringoClient.mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(OringoClient.mc.func_175598_ae().field_78732_j, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179152_a(-f1, -f1, f1);
         GlStateManager.func_179140_f();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179097_i();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         int i = 0;
         fontrenderer.func_78276_b(str, -fontrenderer.func_78256_a(str) / 2, i, -1);
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         fontrenderer.func_78276_b(str, -fontrenderer.func_78256_a(str) / 2, i, -1);
         GlStateManager.func_179145_e();
         GlStateManager.func_179084_k();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179121_F();
      }

   }

   public static void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance) {
      double d0 = entityIn.func_70068_e(OringoClient.mc.func_175598_ae().field_78734_h);
      if (d0 <= (double)(maxDistance * maxDistance)) {
         FontRenderer fontrenderer = OringoClient.mc.func_175598_ae().func_78716_a();
         float f = 1.6F;
         float f1 = 0.016666668F * f;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)x + 0.0F, (float)y + entityIn.field_70131_O + 0.5F, (float)z);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(-OringoClient.mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(OringoClient.mc.func_175598_ae().field_78732_j, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179152_a(-f1, -f1, f1);
         GlStateManager.func_179140_f();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179097_i();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         Tessellator tessellator = Tessellator.func_178181_a();
         WorldRenderer worldrenderer = tessellator.func_178180_c();
         int i = 0;
         if (str.equals("deadmau5")) {
            i = -10;
         }

         int j = fontrenderer.func_78256_a(str) / 2;
         GlStateManager.func_179090_x();
         worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         worldrenderer.func_181662_b((double)(-j - 1), (double)(-1 + i), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         worldrenderer.func_181662_b((double)(-j - 1), (double)(8 + i), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         worldrenderer.func_181662_b((double)(j + 1), (double)(8 + i), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         worldrenderer.func_181662_b((double)(j + 1), (double)(-1 + i), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         tessellator.func_78381_a();
         GlStateManager.func_179098_w();
         fontrenderer.func_78276_b(str, -fontrenderer.func_78256_a(str) / 2, i, 553648127);
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         fontrenderer.func_78276_b(str, -fontrenderer.func_78256_a(str) / 2, i, -1);
         GlStateManager.func_179145_e();
         GlStateManager.func_179084_k();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179121_F();
      }

   }

   public static Color glColor(int hex) {
      float alpha = (float)(hex >> 24 & 255) / 256.0F;
      float red = (float)(hex >> 16 & 255) / 255.0F;
      float green = (float)(hex >> 8 & 255) / 255.0F;
      float blue = (float)(hex & 255) / 255.0F;
      GL11.glColor4f(red, green, blue, alpha);
      return new Color(red, green, blue, alpha);
   }
}
