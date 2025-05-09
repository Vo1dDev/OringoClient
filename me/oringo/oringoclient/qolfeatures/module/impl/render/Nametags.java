package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.awt.Color;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.AntiBot;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Nametags extends Module {
   public Nametags() {
      super("Nametags", Module.Category.RENDER);
   }

   @SubscribeEvent
   public void onRender(Pre<EntityLivingBase> event) {
      if (this.isToggled() && AntiBot.isValidEntity(event.entity) && event.entity instanceof EntityPlayer && event.entity != mc.field_71439_g && event.entity.func_70032_d(mc.field_71439_g) < 100.0F) {
         event.setCanceled(true);
         GlStateManager.func_179092_a(516, 0.1F);
         String name = event.entity.func_70005_c_();
         double x = event.x;
         double y = event.y;
         double z = event.z;
         float f = Math.max(1.4F, event.entity.func_70032_d(mc.field_71439_g) / 10.0F);
         float scale = 0.016666668F * f;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)x + 0.0F, (float)y + event.entity.field_70131_O + 0.5F, (float)z);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(-mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(mc.func_175598_ae().field_78732_j, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179152_a(-scale, -scale, scale);
         GlStateManager.func_179140_f();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179097_i();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         float textWidth = (float)Math.max(Fonts.robotoMediumBold.getStringWidth(name) / 2.0D, 30.0D);
         GlStateManager.func_179090_x();
         RenderUtils.drawRect(-textWidth - 3.0F, (float)(Fonts.robotoMediumBold.getHeight() + 3), textWidth + 3.0F, -3.0F, (new Color(20, 20, 20, 80)).getRGB());
         RenderUtils.drawRect(-textWidth - 3.0F, (float)(Fonts.robotoMediumBold.getHeight() + 3), (float)((double)(textWidth + 3.0F) * (MathUtil.clamp((double)(event.entity.func_110143_aJ() / event.entity.func_110138_aP()), 1.0D, 0.0D) - 0.5D) * 2.0D), (float)(Fonts.robotoMediumBold.getHeight() + 2), OringoClient.clickGui.getColor(event.entity.func_145782_y()).getRGB());
         GlStateManager.func_179098_w();
         Fonts.robotoMediumBold.drawSmoothString(name, -Fonts.robotoMediumBold.getStringWidth(name) / 2.0D, 0.0F, Color.WHITE.getRGB());
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         Fonts.robotoMediumBold.drawSmoothString(name, -Fonts.robotoMediumBold.getStringWidth(name) / 2.0D, 0.0F, Color.WHITE.getRGB());
         GlStateManager.func_179145_e();
         GlStateManager.func_179084_k();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179121_F();
      }

   }
}
