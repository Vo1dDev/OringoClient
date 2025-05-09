package me.oringo.oringoclient.mixins.gui;

import java.awt.Color;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiButton.class})
public abstract class MixinGuiButton extends GuiMixin {
   @Shadow
   public boolean field_146125_m;
   @Shadow
   @Final
   protected static ResourceLocation field_146122_a;
   @Shadow
   public int field_146121_g;
   @Shadow
   protected boolean field_146123_n;
   @Shadow
   public int field_146128_h;
   @Shadow
   public int field_146129_i;
   @Shadow
   public int field_146120_f;
   @Shadow
   public int packedFGColour;
   @Shadow
   public boolean field_146124_l;
   @Shadow
   public String field_146126_j;

   @Shadow
   public abstract int func_146117_b();

   @Shadow
   protected abstract int func_146114_a(boolean var1);

   @Shadow
   protected abstract void func_146119_b(Minecraft var1, int var2, int var3);

   @Inject(
      method = {"drawButton"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void drawButton(Minecraft mc, int mouseX, int mouseY, CallbackInfo callbackInfo) {
      if (this.field_146125_m && OringoClient.interfaces.customButtons.isEnabled()) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_146123_n = mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179112_b(770, 771);

         for(float i = 0.0F; i < 2.0F; i = (float)((double)i + 0.5D)) {
            RenderUtils.drawRoundedRect((double)((float)this.field_146128_h - i), (double)((float)this.field_146129_i + i), (double)((float)(this.field_146128_h + this.field_146120_f) - i), (double)((float)(this.field_146129_i + this.field_146121_g) + i), 2.0D, (new Color(21, 21, 21, 30)).getRGB());
         }

         RenderUtils.drawRoundedRect((double)this.field_146128_h, (double)this.field_146129_i, (double)(this.field_146128_h + this.field_146120_f), (double)(this.field_146129_i + this.field_146121_g), 2.0D, (new Color(21, 21, 21, 180)).getRGB());
         this.drawGradientRect(0.0F, 255);
         this.func_146119_b(mc, mouseX, mouseY);
         Fonts.robotoMediumBold.drawSmoothCenteredString(this.field_146126_j, (float)this.field_146128_h + (float)this.field_146120_f / 2.0F, (float)this.field_146129_i + (float)(this.field_146121_g - Fonts.robotoMediumBold.getHeight()) / 2.0F, this.field_146123_n ? Color.white.getRGB() : (new Color(200, 200, 200)).getRGB());
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         callbackInfo.cancel();
      }

   }

   public void drawGradientRect(float expand, int opacity) {
      if (OringoClient.interfaces.buttonLine.is("Wave")) {
         this.start2ColorDraw();
         float prevPos = (float)this.field_146128_h;

         for(int i = 1; i < 11; ++i) {
            float pos = (float)this.field_146128_h + (float)i * 0.1F * (float)this.field_146120_f;
            if (OringoClient.interfaces.lineLocation.is("Top")) {
               this.addVertexes(prevPos - expand, (float)this.field_146129_i - expand, pos + expand, (float)this.field_146129_i + 1.5F + expand, RenderUtils.applyOpacity(OringoClient.clickGui.getColor(i), opacity).getRGB(), RenderUtils.applyOpacity(OringoClient.clickGui.getColor(i + 1), opacity).getRGB());
            } else {
               this.addVertexes(prevPos - expand, (float)this.field_146129_i - expand + (float)this.field_146121_g - 1.5F, pos + expand, (float)(this.field_146129_i + this.field_146121_g) + expand, RenderUtils.applyOpacity(OringoClient.clickGui.getColor(i), opacity).getRGB(), RenderUtils.applyOpacity(OringoClient.clickGui.getColor(i + 1), opacity).getRGB());
            }

            prevPos = pos;
         }

         this.end2ColorDraw();
      } else if (OringoClient.interfaces.buttonLine.is("Single")) {
         if (OringoClient.interfaces.lineLocation.is("Top")) {
            RenderUtils.drawRect((float)this.field_146128_h - expand, (float)this.field_146129_i - expand, (float)(this.field_146128_h + this.field_146120_f) + expand, (float)this.field_146129_i + 1.5F + expand, RenderUtils.applyOpacity(OringoClient.clickGui.getColor(), opacity).getRGB());
         } else {
            RenderUtils.drawRect((float)this.field_146128_h - expand, (float)this.field_146129_i - expand + (float)this.field_146121_g - 1.5F, (float)(this.field_146128_h + this.field_146120_f) + expand, (float)(this.field_146129_i + this.field_146121_g) + expand, RenderUtils.applyOpacity(OringoClient.clickGui.getColor(), opacity).getRGB());
         }
      }

   }

   public void start2ColorDraw() {
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179103_j(7425);
      WorldRenderer worldrenderer = Tessellator.func_178181_a().func_178180_c();
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
   }

   public void end2ColorDraw() {
      Tessellator.func_178181_a().func_78381_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public void addVertexes(float left, float top, float right, float bottom, int color, int color2) {
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
      worldrenderer.func_181662_b((double)right, (double)bottom, 0.0D).func_181666_a(f, f1, f2, f3).func_181675_d();
      worldrenderer.func_181662_b((double)right, (double)top, 0.0D).func_181666_a(f, f1, f2, f3).func_181675_d();
      worldrenderer.func_181662_b((double)left, (double)top, 0.0D).func_181666_a(ff, ff1, ff2, ff3).func_181675_d();
   }
}
