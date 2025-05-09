package me.oringo.oringoclient.mixins;

import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FontRenderer.class})
public abstract class NickHider {
   @Shadow
   protected abstract void func_78255_a(String var1, boolean var2);

   @Shadow
   public abstract int func_78256_a(String var1);

   @Shadow
   public abstract int func_78263_a(char var1);

   @Inject(
      method = {"renderStringAtPos"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderString(String text, boolean shadow, CallbackInfo ci) {
      if (OringoClient.nickHider.isToggled() && text.contains(OringoClient.mc.func_110432_I().func_111285_a()) && !OringoClient.mc.func_110432_I().func_111285_a().equals(OringoClient.nickHider.name.getValue())) {
         ci.cancel();
         this.func_78255_a(text.replaceAll(OringoClient.mc.func_110432_I().func_111285_a(), OringoClient.nickHider.name.getValue()), shadow);
      }

   }

   @Inject(
      method = {"getStringWidth"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void getStringWidth(String text, CallbackInfoReturnable<Integer> cir) {
      if (text != null && OringoClient.nickHider.isToggled() && text.contains(OringoClient.mc.func_110432_I().func_111285_a()) && !OringoClient.mc.func_110432_I().func_111285_a().equals(OringoClient.nickHider.name.getValue())) {
         cir.setReturnValue(this.func_78256_a(text.replaceAll(OringoClient.mc.func_110432_I().func_111285_a(), OringoClient.nickHider.name.getValue())));
      }

   }
}
