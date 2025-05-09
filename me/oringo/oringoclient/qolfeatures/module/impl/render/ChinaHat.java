package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.awt.Color;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ChinaHat extends Module {
   public NumberSetting radius = new NumberSetting("Radius", 0.7D, 0.5D, 1.0D, 0.01D);
   public NumberSetting height = new NumberSetting("Height", 0.3D, 0.10000000149011612D, 0.699999988079071D, 0.01D);
   public NumberSetting pos = new NumberSetting("Position", 0.1D, -0.5D, 0.5D, 0.01D);
   public NumberSetting rotation = new NumberSetting("Rotation", 5.0D, 0.0D, 5.0D, 0.1D);
   public NumberSetting angles = new NumberSetting("Angles", 8.0D, 4.0D, 90.0D, 1.0D);
   public BooleanSetting firstPerson = new BooleanSetting("Show in first person", false);
   public BooleanSetting shade = new BooleanSetting("Shade", true);
   public ModeSetting colorMode = new ModeSetting("Color mode", "Rainbow", new String[]{"Rainbow", "Gradient", "Single", "Theme"});
   public NumberSetting red = new NumberSetting("Red", 0.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Single");
      }
   };
   public NumberSetting green = new NumberSetting("Green", 80.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Single");
      }
   };
   public NumberSetting blue = new NumberSetting("Blue", 255.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Single");
      }
   };
   public NumberSetting redGradient1 = new NumberSetting("Red 1", 255.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Gradient");
      }
   };
   public NumberSetting greenGradient1 = new NumberSetting("Green 1", 0.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Gradient");
      }
   };
   public NumberSetting blueGradient1 = new NumberSetting("Blue 1", 255.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Gradient");
      }
   };
   public NumberSetting redGradient2 = new NumberSetting("Red 2", 90.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Gradient");
      }
   };
   public NumberSetting greenGradient2 = new NumberSetting("Green 2", 10.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Gradient");
      }
   };
   public NumberSetting blueGradient2 = new NumberSetting("Blue 2", 255.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !ChinaHat.this.colorMode.is("Gradient");
      }
   };

   public ChinaHat() {
      super("China hat", 0, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.radius, this.height, this.firstPerson, this.rotation, this.pos, this.angles, this.shade, this.colorMode, this.red, this.green, this.blue, this.redGradient1, this.greenGradient1, this.blueGradient1, this.redGradient2, this.greenGradient2, this.blueGradient2});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (this.isToggled() && (mc.field_71474_y.field_74320_O != 0 || this.firstPerson.isEnabled())) {
         this.drawChinaHat(mc.field_71439_g, event.partialTicks);
      }

   }

   private void drawChinaHat(EntityLivingBase entity, float partialTicks) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      if (this.shade.isEnabled()) {
         GL11.glShadeModel(7425);
      }

      GL11.glDisable(3553);
      GL11.glDisable(2884);
      GlStateManager.func_179140_f();
      GL11.glTranslated(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)partialTicks - mc.func_175598_ae().field_78730_l, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)partialTicks - mc.func_175598_ae().field_78731_m + (double)entity.field_70131_O + (entity.func_70093_af() ? this.pos.getValue() - 0.23000000417232513D : this.pos.getValue()), entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)partialTicks - mc.func_175598_ae().field_78728_n);
      GL11.glRotatef((float)((double)((float)entity.field_70173_aa + partialTicks) * this.rotation.getValue()) - 90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-(mc.field_71439_g.field_70758_at + (mc.field_71439_g.field_70759_as - mc.field_71439_g.field_70758_at) * partialTicks), 0.0F, 1.0F, 0.0F);
      double radius = this.radius.getValue();
      GL11.glLineWidth(2.0F);
      GL11.glBegin(2);

      for(int i = 0; (double)i <= this.angles.getValue(); ++i) {
         Color color = this.getColor((double)i, (double)((int)this.angles.getValue()), false);
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.5F);
         GL11.glVertex3d(Math.cos((double)i * 3.141592653589793D / (this.angles.getValue() / 2.0D)) * radius, 0.0D, Math.sin((double)i * 3.141592653589793D / (this.angles.getValue() / 2.0D)) * radius);
      }

      GL11.glEnd();
      GL11.glBegin(6);
      Color c1 = this.getColor(0.0D, this.angles.getValue(), true);
      GL11.glColor4f((float)c1.getRed() / 255.0F, (float)c1.getGreen() / 255.0F, (float)c1.getBlue() / 255.0F, 0.8F);
      GL11.glVertex3d(0.0D, this.height.getValue(), 0.0D);

      for(int i = 0; (double)i <= this.angles.getValue(); ++i) {
         Color color = this.getColor((double)i, (double)((int)this.angles.getValue()), false);
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.3F);
         GL11.glVertex3d(Math.cos((double)i * 3.141592653589793D / (this.angles.getValue() / 2.0D)) * radius, 0.0D, Math.sin((double)i * 3.141592653589793D / (this.angles.getValue() / 2.0D)) * radius);
      }

      GL11.glVertex3d(0.0D, this.height.getValue(), 0.0D);
      GL11.glEnd();
      GL11.glShadeModel(7424);
      GL11.glEnable(2884);
      GlStateManager.func_179117_G();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glPopMatrix();
   }

   public Color getColor(double i, double max, boolean first) {
      String var6 = this.colorMode.getSelected();
      byte var7 = -1;
      switch(var6.hashCode()) {
      case -1656737386:
         if (var6.equals("Rainbow")) {
            var7 = 0;
         }
         break;
      case 80774569:
         if (var6.equals("Theme")) {
            var7 = 2;
         }
         break;
      case 154295120:
         if (var6.equals("Gradient")) {
            var7 = 1;
         }
      }

      switch(var7) {
      case 0:
         return Color.getHSBColor((float)(i / max), 0.65F, 1.0F);
      case 1:
         if (first) {
            return new Color((int)this.redGradient1.getValue(), (int)this.greenGradient1.getValue(), (int)this.blueGradient1.getValue());
         }

         return new Color((int)this.redGradient2.getValue(), (int)this.greenGradient2.getValue(), (int)this.blueGradient2.getValue());
      case 2:
         double c = i / max * 10.0D;
         if (i > max / 2.0D) {
            c = 10.0D - c;
         }

         return OringoClient.clickGui.getColor((int)c);
      default:
         return first ? (new Color((int)this.red.getValue(), (int)this.green.getValue(), (int)this.blue.getValue())).darker().darker() : new Color((int)this.red.getValue(), (int)this.green.getValue(), (int)this.blue.getValue());
      }
   }
}
