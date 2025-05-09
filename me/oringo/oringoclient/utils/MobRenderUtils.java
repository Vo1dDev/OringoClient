package me.oringo.oringoclient.utils;

import java.awt.Color;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class MobRenderUtils {
   private static final DynamicTexture empty = new DynamicTexture(16, 16);
   protected static final FloatBuffer brightnessBuffer = GLAllocation.func_74529_h(4);

   public static void setColor(Color color) {
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179098_w();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_77478_a);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176093_u);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_77478_a);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179098_w();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, OpenGlHelper.field_176094_t);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_176092_v);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176080_A, OpenGlHelper.field_176092_v);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176076_D, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      brightnessBuffer.position(0);
      brightnessBuffer.put((float)color.getRed() / 255.0F);
      brightnessBuffer.put((float)color.getGreen() / 255.0F);
      brightnessBuffer.put((float)color.getBlue() / 255.0F);
      brightnessBuffer.put((float)color.getAlpha() / 255.0F);
      brightnessBuffer.flip();
      GL11.glTexEnv(8960, 8705, brightnessBuffer);
      GlStateManager.func_179138_g(OpenGlHelper.field_176096_r);
      GlStateManager.func_179098_w();
      GlStateManager.func_179144_i(empty.func_110552_b());
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_77476_b);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   public static void unsetColor() {
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179098_w();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, OpenGlHelper.field_77478_a);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176093_u);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, OpenGlHelper.field_77478_a);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176079_G, OpenGlHelper.field_176093_u);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176086_J, 770);
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, 5890);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179138_g(OpenGlHelper.field_176096_r);
      GlStateManager.func_179090_x();
      GlStateManager.func_179144_i(0);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.field_176095_s);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176099_x, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176081_B, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176082_C, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176098_y, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176097_z, OpenGlHelper.field_176091_w);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176077_E, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176085_I, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.field_176078_F, 5890);
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   static {
      int[] aint = empty.func_110565_c();

      for(int i = 0; i < 256; ++i) {
         aint[i] = -1;
      }

      empty.func_110564_a();
   }
}
