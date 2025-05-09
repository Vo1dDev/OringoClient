package me.oringo.oringoclient.mixins.gui;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.StencilUtils;
import me.oringo.oringoclient.utils.TimerUtil;
import me.oringo.oringoclient.utils.font.Fonts;
import me.oringo.oringoclient.utils.shader.BlurUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {GuiNewChat.class},
   priority = 1
)
public abstract class GuiNewChatMixin extends GuiMixin {
   @Shadow
   @Final
   private Minecraft field_146247_f;
   @Shadow
   @Final
   private List<ChatLine> field_146253_i;
   @Shadow
   private int field_146250_j;
   @Shadow
   private boolean field_146251_k;

   @Shadow
   public abstract int func_146232_i();

   @Shadow
   public abstract boolean func_146241_e();

   @Shadow
   public abstract float func_146244_h();

   @Shadow
   public abstract int func_146228_f();

   @Inject(
      method = {"drawChat"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void drawChat(int updateCounter, CallbackInfo ci) {
      if (OringoClient.interfaces.customChat.isEnabled() && OringoClient.interfaces.isToggled()) {
         if (this.field_146247_f.field_71474_y.field_74343_n != EnumChatVisibility.HIDDEN) {
            ScaledResolution scaledresolution = new ScaledResolution(this.field_146247_f);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GlStateManager.func_179109_b(0.0F, (float)(scaledresolution.func_78328_b() - 60), 0.0F);
            int maxLineCount = this.func_146232_i();
            boolean isChatOpen = false;
            int j = 0;
            int lineCount = this.field_146253_i.size();
            int fontHeight = OringoClient.interfaces.customChatFont.isEnabled() ? Fonts.robotoBig.getHeight() + 3 : this.field_146247_f.field_71466_p.field_78288_b;
            if (lineCount > 0) {
               if (this.func_146241_e()) {
                  isChatOpen = true;
               }

               float scale = this.func_146244_h();
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b(2.0F, 20.0F, 0.0F);
               GlStateManager.func_179152_a(scale, scale, 1.0F);
               int scaledWidth = MathHelper.func_76123_f((float)this.func_146228_f() / scale);
               float x = 0.0F;
               float y = 0.0F;
               boolean render = false;

               int l2;
               ChatLine chatline;
               for(l2 = 0; l2 + this.field_146250_j < this.field_146253_i.size() && l2 < maxLineCount; ++l2) {
                  chatline = (ChatLine)this.field_146253_i.get(l2 + this.field_146250_j);
                  if (chatline != null && (updateCounter - chatline.func_74540_b() < 200 || isChatOpen)) {
                     render = true;
                     if (!isChatOpen && updateCounter - chatline.func_74540_b() > 195) {
                        float percent = 1.0F - ((float)(updateCounter - chatline.func_74540_b()) + TimerUtil.getTimer().field_74281_c - 195.0F) / 5.0F;
                        percent = MathUtil.clamp(percent, 0.0F, 1.0F);
                        y -= (float)fontHeight * percent;
                     } else {
                        y -= (float)fontHeight;
                     }
                  }
               }

               if (render) {
                  int blur = 0;
                  String var21 = OringoClient.interfaces.blurStrength.getSelected();
                  byte var23 = -1;
                  switch(var21.hashCode()) {
                  case 76596:
                     if (var21.equals("Low")) {
                        var23 = 0;
                     }
                     break;
                  case 2249154:
                     if (var21.equals("High")) {
                        var23 = 1;
                     }
                  }

                  switch(var23) {
                  case 0:
                     blur = 7;
                     break;
                  case 1:
                     blur = 25;
                  }

                  if (blur > 0) {
                     for(float i = 0.5F; i < 3.0F; i += 0.5F) {
                        RenderUtils.drawRoundedRect((double)(x + i - 2.0F), (double)(y + i), (double)(x + (float)scaledWidth + 4.0F + i), (double)(1.0F + i), 5.0D, (new Color(20, 20, 20, 40)).getRGB());
                     }
                  }

                  StencilUtils.initStencil();
                  StencilUtils.bindWriteStencilBuffer();
                  RenderUtils.drawRoundedRect((double)(x - 2.0F), (double)y, (double)(x + (float)scaledWidth + 4.0F), 1.0D, 5.0D, Color.white.getRGB());
                  GL11.glPopMatrix();
                  GL11.glPopMatrix();
                  StencilUtils.bindReadStencilBuffer(1);
                  BlurUtils.renderBlurredBackground((float)blur, (float)scaledresolution.func_78326_a(), (float)scaledresolution.func_78328_b(), 0.0F, 0.0F, (float)scaledresolution.func_78326_a(), (float)scaledresolution.func_78328_b());
                  GL11.glPushMatrix();
                  GlStateManager.func_179109_b(0.0F, (float)(scaledresolution.func_78328_b() - 60), 0.0F);
                  GL11.glPushMatrix();
                  GlStateManager.func_179109_b(2.0F, 20.0F, 0.0F);
                  GlStateManager.func_179152_a(scale, scale, 1.0F);
                  RenderUtils.drawRoundedRect((double)(x - 2.0F), (double)y, (double)(x + (float)scaledWidth + 4.0F), 1.0D, 5.0D, (new Color(20, 20, 20, 60)).getRGB());
               }

               int opacity;
               int j3;
               for(l2 = 0; l2 + this.field_146250_j < this.field_146253_i.size() && l2 < maxLineCount; ++l2) {
                  chatline = (ChatLine)this.field_146253_i.get(l2 + this.field_146250_j);
                  if (chatline != null) {
                     j3 = updateCounter - chatline.func_74540_b();
                     if (j3 < 200 || isChatOpen) {
                        ++j;
                        int left = 0;
                        opacity = -l2 * fontHeight;
                        String text = chatline.func_151461_a().func_150254_d();
                        GlStateManager.func_179147_l();
                        if (OringoClient.interfaces.customChatFont.isEnabled()) {
                           Fonts.robotoBig.drawSmoothStringWithShadow(text, (double)((float)left), (double)((float)((double)opacity - ((double)fontHeight - 2.3D))), Color.white.getRGB());
                        } else {
                           this.field_146247_f.field_71466_p.func_175063_a(text, (float)left, (float)(opacity - (fontHeight - 1)), 16777215);
                        }

                        GlStateManager.func_179118_c();
                        GlStateManager.func_179084_k();
                     }
                  }
               }

               if (render) {
                  StencilUtils.uninitStencil();
               }

               if (isChatOpen) {
                  GlStateManager.func_179109_b(-3.0F, 0.0F, 0.0F);
                  fontHeight = this.field_146247_f.field_71466_p.field_78288_b;
                  l2 = lineCount * fontHeight + lineCount;
                  int i3 = j * fontHeight + j;
                  j3 = this.field_146250_j * i3 / lineCount;
                  int k1 = i3 * i3 / l2;
                  if (l2 != i3) {
                     opacity = j3 > 0 ? 170 : 96;
                     int l3 = this.field_146251_k ? 13382451 : 3355562;
                     func_73734_a(0, -j3, 2, -j3 - k1, l3 + (opacity << 24));
                     func_73734_a(2, -j3, 1, -j3 - k1, 13421772 + (opacity << 24));
                  }
               }

               GlStateManager.func_179121_F();
            }
         }

         ci.cancel();
      }

   }

   @Overwrite
   public IChatComponent func_146236_a(int p_146236_1_, int p_146236_2_) {
      if (!this.func_146241_e()) {
         return null;
      } else {
         ScaledResolution scaledresolution = new ScaledResolution(this.field_146247_f);
         int i = scaledresolution.func_78325_e();
         float f = this.func_146244_h();
         int j = p_146236_1_ / i - 3;
         int k = p_146236_2_ / i - 27;
         if (OringoClient.interfaces.isToggled() && OringoClient.interfaces.customChat.isEnabled()) {
            k -= 12;
         }

         j = MathHelper.func_76141_d((float)j / f);
         k = MathHelper.func_76141_d((float)k / f);
         if (j >= 0 && k >= 0) {
            int l = Math.min(this.func_146232_i(), this.field_146253_i.size());
            if (j <= MathHelper.func_76141_d((float)this.func_146228_f() / this.func_146244_h()) && k < this.getHeight() * l + l) {
               int i1 = k / this.getHeight() + this.field_146250_j;
               if (i1 >= 0 && i1 < this.field_146253_i.size()) {
                  ChatLine chatline = (ChatLine)this.field_146253_i.get(i1);
                  int j1 = 0;
                  Iterator var12 = chatline.func_151461_a().iterator();

                  IChatComponent ichatcomponent;
                  do {
                     do {
                        if (!var12.hasNext()) {
                           return null;
                        }

                        ichatcomponent = (IChatComponent)var12.next();
                     } while(!(ichatcomponent instanceof ChatComponentText));

                     j1 = (int)((double)j1 + (OringoClient.interfaces.customChatFont.isEnabled() && OringoClient.interfaces.isToggled() && OringoClient.interfaces.customChat.isEnabled() ? Fonts.robotoBig.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).func_150265_g(), false)) : (double)this.field_146247_f.field_71466_p.func_78256_a(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).func_150265_g(), false))));
                  } while(j1 <= j);

                  return ichatcomponent;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   private int getHeight() {
      return OringoClient.interfaces.customChatFont.isEnabled() && OringoClient.interfaces.customChat.isEnabled() && OringoClient.interfaces.isToggled() ? Fonts.robotoBig.getHeight() + 3 : this.field_146247_f.field_71466_p.field_78288_b;
   }

   @Redirect(
      method = {"setChatLine"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/GuiUtilRenderComponents;func_178908_a(Lnet/minecraft/util/IChatComponent;ILnet/minecraft/client/gui/FontRenderer;ZZ)Ljava/util/List;"
)
   )
   private List<IChatComponent> onFunc(IChatComponent k, int s1, FontRenderer chatcomponenttext, boolean l, boolean chatcomponenttext2) {
      return OringoClient.interfaces.customChatFont.isEnabled() && OringoClient.interfaces.isToggled() && OringoClient.interfaces.customChat.isEnabled() ? this.wrapToLen(k, s1, chatcomponenttext) : GuiUtilRenderComponents.func_178908_a(k, s1, chatcomponenttext, l, chatcomponenttext2);
   }

   private List<IChatComponent> wrapToLen(IChatComponent p_178908_0_, int p_178908_1_, FontRenderer p_178908_2_) {
      int i = 0;
      IChatComponent ichatcomponent = new ChatComponentText("");
      List<IChatComponent> list = Lists.newArrayList();
      List<IChatComponent> list1 = Lists.newArrayList(p_178908_0_);

      for(int j = 0; j < list1.size(); ++j) {
         IChatComponent ichatcomponent1 = (IChatComponent)list1.get(j);
         String s = ichatcomponent1.func_150261_e();
         boolean flag = false;
         String s5;
         if (s.contains("\n")) {
            int k = s.indexOf(10);
            s5 = s.substring(k + 1);
            s = s.substring(0, k + 1);
            ChatComponentText chatcomponenttext = new ChatComponentText(s5);
            chatcomponenttext.func_150255_a(ichatcomponent1.func_150256_b().func_150232_l());
            list1.add(j + 1, chatcomponenttext);
            flag = true;
         }

         String s4 = GuiUtilRenderComponents.func_178909_a(ichatcomponent1.func_150256_b().func_150218_j() + s, false);
         s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
         double i1 = Fonts.robotoBig.getStringWidth(s5);
         ChatComponentText chatcomponenttext1 = new ChatComponentText(s5);
         chatcomponenttext1.func_150255_a(ichatcomponent1.func_150256_b().func_150232_l());
         if ((double)i + i1 > (double)p_178908_1_) {
            String s2 = Fonts.robotoBig.trimStringToWidth(s4, p_178908_1_ - i, false);
            String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;
            if (s3 != null && s3.length() > 0) {
               int l = s2.lastIndexOf(" ");
               if (l >= 0 && Fonts.robotoBig.getStringWidth(s4.substring(0, l)) > 0.0D) {
                  s2 = s4.substring(0, l);
                  s3 = s4.substring(l);
               } else if (i > 0 && !s4.contains(" ")) {
                  s2 = "";
                  s3 = s4;
               }

               s3 = FontRenderer.func_78282_e(s2) + s3;
               ChatComponentText chatcomponenttext2 = new ChatComponentText(s3);
               chatcomponenttext2.func_150255_a(ichatcomponent1.func_150256_b().func_150232_l());
               list1.add(j + 1, chatcomponenttext2);
            }

            i1 = Fonts.robotoBig.getStringWidth(s2);
            chatcomponenttext1 = new ChatComponentText(s2);
            chatcomponenttext1.func_150255_a(ichatcomponent1.func_150256_b().func_150232_l());
            flag = true;
         }

         if ((double)i + i1 <= (double)p_178908_1_) {
            i = (int)((double)i + i1);
            ichatcomponent.func_150257_a(chatcomponenttext1);
         } else {
            flag = true;
         }

         if (flag) {
            list.add(ichatcomponent);
            i = 0;
            ichatcomponent = new ChatComponentText("");
         }
      }

      list.add(ichatcomponent);
      return list;
   }
}
