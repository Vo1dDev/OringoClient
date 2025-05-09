package me.oringo.oringoclient.ui.hud.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import me.oringo.oringoclient.qolfeatures.module.impl.render.TargetHUD;
import me.oringo.oringoclient.ui.hud.DraggableComponent;
import me.oringo.oringoclient.ui.hud.HudVec;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.StencilUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import me.oringo.oringoclient.utils.shader.BlurUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public class TargetComponent extends DraggableComponent {
   public static final TargetComponent INSTANCE = new TargetComponent();
   private float lastHp = 0.8F;
   private static EntityLivingBase lastEntity;
   private static final MilliTimer resetTimer = new MilliTimer();
   private static final MilliTimer startTimer = new MilliTimer();

   public TargetComponent() {
      this.setSize(150.0D, 50.0D);
      MinecraftForge.EVENT_BUS.register(this);
   }

   public HudVec drawScreen() {
      return this.drawScreen((EntityLivingBase)(KillAura.target == null && mc.field_71462_r instanceof GuiChat ? mc.field_71439_g : KillAura.target));
   }

   public HudVec drawScreen(EntityLivingBase entity) {
      if (entity != null) {
         if (lastEntity == null) {
            startTimer.reset();
         }

         resetTimer.reset();
      }

      if (resetTimer.hasTimePassed(750L) || entity != null) {
         lastEntity = entity;
      }

      if (lastEntity != null) {
         super.drawScreen();
         double x = this.getX();
         double y = this.getY();
         GL11.glPushMatrix();
         int blur = 0;
         String var7 = TargetHUD.getInstance().blurStrength.getSelected();
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
         GL11.glPushMatrix();
         scale(x + 75.0D, y + 25.0D, 0.0D);
         StencilUtils.initStencil();
         StencilUtils.bindWriteStencilBuffer();
         RenderUtils.drawRoundedRect2(x, y, 150.0D, 50.0D, 5.0D, Color.black.getRGB());
         StencilUtils.bindReadStencilBuffer(1);
         GL11.glPopMatrix();
         BlurUtils.renderBlurredBackground((float)blur, (float)resolution.func_78326_a(), (float)resolution.func_78328_b(), 0.0F, 0.0F, (float)resolution.func_78326_a(), (float)resolution.func_78328_b());
         StencilUtils.uninitStencil();
         scale(x + 75.0D, y + 25.0D, 0.0D);
         float hp = this.lastHp + (getHp() - this.lastHp) / (7.0F * ((float)Minecraft.func_175610_ah() / 20.0F));
         if (Math.abs(hp - this.lastHp) < 0.001F) {
            this.lastHp = hp;
         }

         if (mc.field_71462_r instanceof GuiChat && this.isHovered()) {
            RenderUtils.drawBorderedRoundedRect((float)x, (float)y, 150.0F, 50.0F, 5.0F, 2.0F, (new Color(21, 21, 21, 52)).getRGB(), Color.white.getRGB());
         } else {
            RenderUtils.drawRoundedRect2(x, y, 150.0D, 50.0D, 5.0D, (new Color(21, 21, 21, 52)).getRGB());
         }

         Fonts.robotoBig.drawSmoothStringWithShadow(ChatFormatting.stripFormatting(lastEntity.func_70005_c_()), x + 5.0D, y + 6.0D, OringoClient.clickGui.getColor(0).brighter().getRGB());
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var9 = false;

         try {
            EntityLivingBase var10000 = lastEntity;
            var10000.field_70163_u += 1000.0D;
            GuiInventory.func_147046_a((int)(x + 130.0D), (int)(y + 40.0D), (int)(35.0D / Math.max((double)lastEntity.field_70131_O, 1.5D)), 20.0F, 10.0F, lastEntity);
            var10000 = lastEntity;
            var10000.field_70163_u -= 1000.0D;
         } catch (Exception var11) {
         }

         Fonts.robotoMediumBold.drawSmoothStringWithShadow((double)((int)(lastEntity.func_70032_d(mc.field_71439_g) * 10.0F)) / 10.0D + "m", x + 5.0D, y + 11.0D + (double)Fonts.robotoMediumBold.getHeight(), (new Color(231, 231, 231)).getRGB());
         String text = String.format("%.1f", getHp() * 100.0F) + "%";
         RenderUtils.drawRoundedRect(x + 10.0D, y + 42.0D, x + 140.0D, y + 46.0D, 2.0D, Color.HSBtoRGB(0.0F, 0.0F, 0.1F));
         if ((double)hp > 0.05D) {
            RenderUtils.drawRoundedRect(x + 10.0D, y + 42.0D, x + (double)(140.0F * hp), y + 46.0D, 2.0D, OringoClient.clickGui.getColor(0).getRGB());
         }

         Fonts.robotoMediumBold.drawSmoothStringWithShadow(text, x + 75.0D - Fonts.robotoMediumBold.getStringWidth(text) / 2.0D, y + 33.0D, (new Color(231, 231, 231)).getRGB());
         this.lastHp = hp;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
      }

      TargetHUD.getInstance().x.set(this.x);
      TargetHUD.getInstance().y.set(this.y);
      return new HudVec(this.x + this.getWidth(), this.y + this.getHeight());
   }

   private static float getHp() {
      return lastEntity == null ? 0.0F : MathUtil.clamp(lastEntity.func_110143_aJ() / lastEntity.func_110138_aP(), 1.0F, 0.0F);
   }

   private static void scale(double x, double y, double startingSize) {
      new ScaledResolution(mc);
      if (resetTimer.hasTimePassed(550L)) {
         RenderUtils.doScale(x, y, (double)(750L - resetTimer.getTimePassed()) / 200.0D);
      } else if (!startTimer.hasTimePassed(200L)) {
         RenderUtils.doScale(x, y, (double)startTimer.getTimePassed() / 200.0D * (1.0D - startingSize + startingSize));
      }

   }
}
