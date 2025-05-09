package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PlayerUpdateEvent;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Trial extends Module {
   public static final NumberSetting count = new NumberSetting("Points", 20.0D, 5.0D, 100.0D, 1.0D);
   private static final List<Vec3> vecs = new ArrayList();

   public Trial() {
      super("Trail", Module.Category.RENDER);
      this.addSettings(new Setting[]{count});
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.isToggled()) {
         vecs.add(new Vec3(mc.field_71439_g.field_70169_q, mc.field_71439_g.field_70167_r + 0.1D, mc.field_71439_g.field_70166_s));

         while((double)vecs.size() > count.getValue()) {
            vecs.remove(0);
         }
      }

   }

   @SubscribeEvent
   public void onRenderWorld(RenderWorldLastEvent event) {
      if (this.isToggled() && !vecs.isEmpty() && mc.field_71439_g != null && mc.func_175598_ae() != null) {
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(3042);
         GL11.glLineWidth(2.5F);
         GL11.glDisable(3553);
         GL11.glDisable(2884);
         GL11.glShadeModel(7425);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glBegin(3);
         int index = 0;
         Iterator var4 = vecs.iterator();

         while(true) {
            Color color;
            while(var4.hasNext()) {
               Vec3 vec = (Vec3)var4.next();
               boolean isFirst = index == 0;
               ++index;
               color = OringoClient.clickGui.getColor(index);
               GL11.glColor3f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F);
               if (isFirst && vecs.size() > 2) {
                  Vec3 newVec = (Vec3)vecs.get(1);
                  GL11.glVertex3d(this.interpolate(vec.field_72450_a, newVec.field_72450_a, event.partialTicks) - mc.func_175598_ae().field_78730_l, this.interpolate(vec.field_72448_b, newVec.field_72448_b, event.partialTicks) - mc.func_175598_ae().field_78731_m, this.interpolate(vec.field_72449_c, newVec.field_72449_c, event.partialTicks) - mc.func_175598_ae().field_78728_n);
               } else {
                  GL11.glVertex3d(vec.field_72450_a - mc.func_175598_ae().field_78730_l, vec.field_72448_b - mc.func_175598_ae().field_78731_m, vec.field_72449_c - mc.func_175598_ae().field_78728_n);
               }
            }

            color = OringoClient.clickGui.getColor(index);
            GL11.glColor3f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F);
            GL11.glVertex3d(mc.field_71439_g.field_70169_q + (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * (double)event.partialTicks - mc.func_175598_ae().field_78730_l, mc.field_71439_g.field_70167_r + (mc.field_71439_g.field_70163_u - mc.field_71439_g.field_70167_r) * (double)event.partialTicks - mc.func_175598_ae().field_78731_m + 0.1D, mc.field_71439_g.field_70166_s + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (double)event.partialTicks - mc.func_175598_ae().field_78728_n);
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glShadeModel(7424);
            GL11.glEnable(2884);
            GL11.glDisable(2848);
            GL11.glDisable(2881);
            GL11.glDisable(3042);
            break;
         }
      }

   }

   @SubscribeEvent
   public void onWorldJoin(WorldJoinEvent event) {
      vecs.clear();
   }

   private double interpolate(double prev, double newPos, float partialTicks) {
      return prev + (newPos - prev) * (double)partialTicks;
   }

   private boolean hasMoved() {
      return mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s != 0.0D || mc.field_71439_g.field_70163_u - mc.field_71439_g.field_70167_r != 0.0D || mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q != 0.0D;
   }
}
