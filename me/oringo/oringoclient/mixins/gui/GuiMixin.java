package me.oringo.oringoclient.mixins.gui;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Gui.class})
public abstract class GuiMixin {
   @Shadow
   public static void func_73734_a(int left, int top, int right, int bottom, int color) {
   }

   @Shadow
   protected abstract void func_73733_a(int var1, int var2, int var3, int var4, int var5, int var6);

   @Shadow
   public abstract void func_73729_b(int var1, int var2, int var3, int var4, int var5, int var6);
}
