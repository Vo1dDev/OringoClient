package me.oringo.oringoclient.ui.hud.impl;

import java.awt.Color;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.impl.render.InventoryDisplay;
import me.oringo.oringoclient.ui.hud.DraggableComponent;
import me.oringo.oringoclient.ui.hud.HudVec;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.StencilUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import me.oringo.oringoclient.utils.shader.BlurUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class InventoryHUD extends DraggableComponent {
   public static final InventoryHUD inventoryHUD = new InventoryHUD();

   public InventoryHUD() {
      this.setSize(182.0D, (double)(80 - (Fonts.robotoMedium.getHeight() - 4)));
      this.setPosition(OringoClient.inventoryHUDModule.x.getValue(), OringoClient.inventoryHUDModule.y.getValue());
   }

   public HudVec drawScreen() {
      GL11.glPushMatrix();
      super.drawScreen();
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int blur = 0;
      double x = this.x;
      double y = this.y;
      String var7 = OringoClient.inventoryHUDModule.blurStrength.getSelected();
      byte var8 = -1;
      switch(var7.hashCode()) {
      case 76596:
         if (var7.equals("Low")) {
            var8 = 0;
         }
         break;
      case 2249154:
         if (var7.equals("High")) {
            var8 = 1;
         }
      }

      switch(var8) {
      case 0:
         blur = 7;
         break;
      case 1:
         blur = 15;
      }

      ScaledResolution resolution = new ScaledResolution(mc);
      StencilUtils.initStencil();
      StencilUtils.bindWriteStencilBuffer();
      RenderUtils.drawRoundedRect2(x, y + (double)Fonts.robotoMedium.getHeight() - 4.0D, 182.0D, (double)(80 - (Fonts.robotoMedium.getHeight() - 4)), 5.0D, Color.white.getRGB());
      StencilUtils.bindReadStencilBuffer(1);
      BlurUtils.renderBlurredBackground((float)blur, (float)resolution.func_78326_a(), (float)resolution.func_78328_b(), 0.0F, 0.0F, (float)scaledResolution.func_78326_a(), (float)scaledResolution.func_78328_b());
      StencilUtils.uninitStencil();
      this.drawBorderedRoundedRect((float)x, (float)y + (float)Fonts.robotoMedium.getHeight() - 4.0F, 182.0F, (float)(80 - (Fonts.robotoMedium.getHeight() - 4)), 5.0F, 2.5F);
      Fonts.robotoMediumBold.drawSmoothCenteredString("Inventory", (float)x + 90.0F, (float)y + (float)Fonts.robotoMedium.getHeight(), Color.white.getRGB());
      GlStateManager.func_179091_B();
      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      RenderHelper.func_74520_c();

      for(int i = 9; i < 36; ++i) {
         if (i % 9 == 0) {
            y += 20.0D;
         }

         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != null) {
            mc.func_175599_af().func_180450_b(stack, (int)x + 2 + 20 * (i % 9), (int)y);
            this.renderItemOverlayIntoGUI(stack, x + 2.0D + (double)(20 * (i % 9)), y);
         }
      }

      RenderHelper.func_74518_a();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179101_C();
      GlStateManager.func_179084_k();
      GL11.glPopMatrix();
      InventoryDisplay display = OringoClient.inventoryHUDModule;
      display.x.set(this.x);
      display.y.set(this.y);
      return new HudVec(x + this.width, y + this.height);
   }

   private void drawBorderedRoundedRect(float x, float y, float width, float height, float radius, float linewidth) {
      RenderUtils.drawRoundedRect((double)x, (double)y, (double)(x + width), (double)(y + height), (double)radius, (new Color(21, 21, 21, 50)).getRGB());
      if (this.isHovered() && mc.field_71462_r instanceof GuiChat) {
         RenderUtils.drawOutlinedRoundedRect(x, y, width, height, radius, linewidth, Color.white.getRGB());
      } else {
         RenderUtils.drawGradientOutlinedRoundedRect(x, y, width, height, radius, linewidth, OringoClient.clickGui.getColor(0).getRGB(), OringoClient.clickGui.getColor(3).getRGB(), OringoClient.clickGui.getColor(6).getRGB(), OringoClient.clickGui.getColor(9).getRGB());
      }

   }

   public void renderItemOverlayIntoGUI(ItemStack itemStack, double x, double y) {
      if (itemStack != null) {
         if (itemStack.field_77994_a != 1) {
            String s = String.valueOf(itemStack.field_77994_a);
            if (itemStack.field_77994_a < 1) {
               s = EnumChatFormatting.RED + String.valueOf(itemStack.field_77994_a);
            }

            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            GlStateManager.func_179084_k();
            Fonts.robotoMediumBold.drawSmoothStringWithShadow(s, x + 19.0D - 2.0D - Fonts.robotoMediumBold.getStringWidth(s), y + 6.0D + 3.0D, Color.white.getRGB());
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
         }

         if (itemStack.func_77973_b().showDurabilityBar(itemStack)) {
            double health = itemStack.func_77973_b().getDurabilityForDisplay(itemStack);
            int j = (int)Math.round(13.0D - health * 13.0D);
            int i = (int)Math.round(255.0D - health * 255.0D);
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            GlStateManager.func_179090_x();
            GlStateManager.func_179118_c();
            GlStateManager.func_179084_k();
            Tessellator tessellator = Tessellator.func_178181_a();
            WorldRenderer worldrenderer = tessellator.func_178180_c();
            this.draw(worldrenderer, x + 2.0D, y + 13.0D, 13, 2, 0, 0, 0, 255);
            this.draw(worldrenderer, x + 2.0D, y + 13.0D, 12, 1, (255 - i) / 4, 64, 0, 255);
            this.draw(worldrenderer, x + 2.0D, y + 13.0D, j, 1, 255 - i, i, 0, 255);
            GlStateManager.func_179141_d();
            GlStateManager.func_179098_w();
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
         }
      }

   }

   private void draw(WorldRenderer p_draw_1_, double p_draw_2_, double p_draw_3_, int p_draw_4_, int p_draw_5_, int p_draw_6_, int p_draw_7_, int p_draw_8_, int p_draw_9_) {
      p_draw_1_.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      p_draw_1_.func_181662_b(p_draw_2_ + 0.0D, p_draw_3_ + 0.0D, 0.0D).func_181669_b(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).func_181675_d();
      p_draw_1_.func_181662_b(p_draw_2_ + 0.0D, p_draw_3_ + (double)p_draw_5_, 0.0D).func_181669_b(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).func_181675_d();
      p_draw_1_.func_181662_b(p_draw_2_ + (double)p_draw_4_, p_draw_3_ + (double)p_draw_5_, 0.0D).func_181669_b(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).func_181675_d();
      p_draw_1_.func_181662_b(p_draw_2_ + (double)p_draw_4_, p_draw_3_ + 0.0D, 0.0D).func_181669_b(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).func_181675_d();
      Tessellator.func_178181_a().func_78381_a();
   }
}
