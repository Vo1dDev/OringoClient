package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.awt.Color;
import java.util.concurrent.Callable;
import me.oringo.oringoclient.events.PostGuiOpenEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class NoRender extends Module {
   public NoRender() {
      super("NoRender", Module.Category.RENDER);
   }

   public void onEnable() {
      mc.field_71454_w = true;
   }

   public static void drawGui() {
      if (mc.field_71454_w && mc.field_71462_r != null) {
         mc.func_71364_i();
         if (mc.field_71441_e == null) {
            GlStateManager.func_179083_b(0, 0, mc.field_71443_c, mc.field_71440_d);
            GlStateManager.func_179128_n(5889);
            GlStateManager.func_179096_D();
            GlStateManager.func_179128_n(5888);
            GlStateManager.func_179096_D();
            mc.field_71460_t.func_78478_c();
         } else {
            GlStateManager.func_179092_a(516, 0.1F);
            mc.field_71460_t.func_78478_c();
         }

         final ScaledResolution scaledresolution = new ScaledResolution(mc);
         int i1 = scaledresolution.func_78326_a();
         int j1 = scaledresolution.func_78328_b();
         final int k1 = Mouse.getX() * i1 / mc.field_71443_c;
         final int l1 = j1 - Mouse.getY() * j1 / mc.field_71440_d - 1;
         GlStateManager.func_179086_m(256);
         RenderUtils.drawRect(0.0F, 0.0F, (float)i1, (float)j1, Color.black.getRGB());

         try {
            ForgeHooksClient.drawScreen(mc.field_71462_r, k1, l1, TimerUtil.getTimer().field_74281_c);
         } catch (Throwable var8) {
            CrashReport crashreport = CrashReport.func_85055_a(var8, "Rendering screen");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Screen render details");
            crashreportcategory.func_71500_a("Screen name", new Callable<String>() {
               public String call() throws Exception {
                  return NoRender.mc.field_71462_r.getClass().getCanonicalName();
               }
            });
            crashreportcategory.func_71500_a("Mouse location", new Callable<String>() {
               public String call() throws Exception {
                  return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", k1, l1, Mouse.getX(), Mouse.getY());
               }
            });
            crashreportcategory.func_71500_a("Screen size", new Callable<String>() {
               public String call() throws Exception {
                  return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", scaledresolution.func_78326_a(), scaledresolution.func_78328_b(), NoRender.mc.field_71443_c, NoRender.mc.field_71440_d, scaledresolution.func_78325_e());
               }
            });
            throw new ReportedException(crashreport);
         }
      }

   }

   @SubscribeEvent
   public void onPostGui(PostGuiOpenEvent event) {
      if (this.isToggled()) {
         mc.field_71454_w = true;
      }

   }

   public void onDisable() {
      mc.field_71454_w = false;
   }
}
