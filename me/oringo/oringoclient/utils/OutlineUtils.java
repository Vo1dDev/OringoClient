package me.oringo.oringoclient.utils;

import java.awt.Color;
import me.oringo.oringoclient.events.RenderLayersEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public class OutlineUtils {
   public static void outlineESP(EntityLivingBase entity, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor, ModelBase modelBase, Color color) {
      Minecraft mc = Minecraft.func_71410_x();
      boolean fancyGraphics = mc.field_71474_y.field_74347_j;
      float gamma = mc.field_71474_y.field_74333_Y;
      mc.field_71474_y.field_74347_j = false;
      mc.field_71474_y.field_74333_Y = 100000.0F;
      GlStateManager.func_179117_G();
      setColor(color);
      renderOne(2.0F);
      modelBase.func_78088_a(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
      setColor(color);
      renderTwo();
      modelBase.func_78088_a(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
      setColor(color);
      renderThree();
      modelBase.func_78088_a(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
      setColor(color);
      renderFour(color);
      modelBase.func_78088_a(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
      setColor(color);
      renderFive();
      setColor(Color.WHITE);
      mc.field_71474_y.field_74347_j = fancyGraphics;
      mc.field_71474_y.field_74333_Y = gamma;
   }

   public static void outlineESP(RenderLayersEvent event, Color color) {
      Minecraft mc = Minecraft.func_71410_x();
      boolean fancyGraphics = mc.field_71474_y.field_74347_j;
      float gamma = mc.field_71474_y.field_74333_Y;
      mc.field_71474_y.field_74347_j = false;
      mc.field_71474_y.field_74333_Y = 100000.0F;
      GlStateManager.func_179117_G();
      setColor(color);
      renderOne(3.0F);
      event.modelBase.func_78088_a(event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
      setColor(color);
      renderTwo();
      event.modelBase.func_78088_a(event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
      setColor(color);
      renderThree();
      event.modelBase.func_78088_a(event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
      setColor(color);
      renderFour(color);
      event.modelBase.func_78088_a(event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
      setColor(color);
      renderFive();
      setColor(Color.WHITE);
      mc.field_71474_y.field_74347_j = fancyGraphics;
      mc.field_71474_y.field_74333_Y = gamma;
   }

   public static void renderOne(float lineWidth) {
      checkSetupFBO();
      GL11.glPushAttrib(1048575);
      GL11.glDisable(3008);
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(lineWidth);
      GL11.glEnable(2848);
      GL11.glEnable(2960);
      GL11.glClear(1024);
      GL11.glClearStencil(15);
      GL11.glStencilFunc(512, 1, 15);
      GL11.glStencilOp(7681, 7681, 7681);
      GL11.glPolygonMode(1032, 6913);
   }

   public static void renderTwo() {
      GL11.glStencilFunc(512, 0, 15);
      GL11.glStencilOp(7681, 7681, 7681);
      GL11.glPolygonMode(1032, 6914);
   }

   public static void renderThree() {
      GL11.glStencilFunc(514, 1, 15);
      GL11.glStencilOp(7680, 7680, 7680);
      GL11.glPolygonMode(1032, 6913);
   }

   public static void renderFour(Color color) {
      setColor(color);
      GL11.glDepthMask(false);
      GL11.glDisable(2929);
      GL11.glEnable(10754);
      GL11.glPolygonOffset(1.0F, -2000000.0F);
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
   }

   public static void renderFive() {
      GL11.glPolygonOffset(1.0F, 2000000.0F);
      GL11.glDisable(10754);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(2960);
      GL11.glDisable(2848);
      GL11.glHint(3154, 4352);
      GL11.glEnable(3042);
      GL11.glEnable(2896);
      GL11.glEnable(3553);
      GL11.glEnable(3008);
      GL11.glPopAttrib();
   }

   public static void setColor(Color color) {
      GL11.glColor4d((double)((float)color.getRed() / 255.0F), (double)((float)color.getGreen() / 255.0F), (double)((float)color.getBlue() / 255.0F), (double)((float)color.getAlpha() / 255.0F));
   }

   public static void checkSetupFBO() {
      Framebuffer fbo = Minecraft.func_71410_x().func_147110_a();
      if (fbo != null && fbo.field_147624_h > -1) {
         setupFBO(fbo);
         fbo.field_147624_h = -1;
      }

   }

   private static void setupFBO(Framebuffer fbo) {
      EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.field_147624_h);
      int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
      EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
      EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.func_71410_x().field_71443_c, Minecraft.func_71410_x().field_71440_d);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
   }
}
