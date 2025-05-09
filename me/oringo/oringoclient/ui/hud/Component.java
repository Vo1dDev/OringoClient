package me.oringo.oringoclient.ui.hud;

import me.oringo.oringoclient.utils.MilliTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class Component {
   protected double x;
   protected double y;
   protected double width;
   protected double height;
   protected boolean hidden;
   protected MilliTimer hideTimer = new MilliTimer();
   protected static final Minecraft mc = Minecraft.func_71410_x();

   public void onTick() {
   }

   protected HudVec drawScreen() {
      return null;
   }

   public boolean isHovered(int mouseX, int mouseY) {
      return (double)mouseX > this.x && (double)mouseX < this.x + this.width && (double)mouseY > this.y && (double)mouseY < this.y + this.height;
   }

   public boolean isHovered() {
      return this.isHovered(getMouseX(), getMouseY());
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public Component setPosition(double x, double y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public Component setHidden(boolean hidden) {
      if (hidden != this.hidden) {
         this.hidden = hidden;
         this.hideTimer.reset();
      }

      return this;
   }

   public Component setSize(double width, double height) {
      this.width = width;
      this.height = height;
      return this;
   }

   protected static int getMouseX() {
      return Mouse.getX() * getScaledResolution().func_78326_a() / mc.field_71443_c;
   }

   protected static int getMouseY() {
      int height = getScaledResolution().func_78328_b();
      return height - Mouse.getY() * height / mc.field_71440_d - 1;
   }

   protected static ScaledResolution getScaledResolution() {
      return new ScaledResolution(mc);
   }

   public double getHeight() {
      return this.height;
   }

   public double getWidth() {
      return this.width;
   }

   public double getY() {
      return this.y;
   }

   public double getX() {
      return this.x;
   }
}
