package me.oringo.oringoclient.mixins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GuiScreen.class})
public abstract class GuiScreenMixin extends GuiMixin implements GuiYesNoCallback {
   @Shadow
   public Minecraft field_146297_k;
   @Shadow
   public int field_146295_m;
   @Shadow
   public int field_146294_l;

   @Shadow
   protected void func_146286_b(int mouseX, int mouseY, int state) {
   }

   @Shadow
   public abstract void func_73863_a(int var1, int var2, float var3);
}
