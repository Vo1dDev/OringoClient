package me.oringo.oringoclient.mixins.gui;

import me.oringo.oringoclient.events.GuiChatEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {GuiChat.class},
   priority = 1
)
public abstract class GuiChatMixin extends GuiScreenMixin {
   @Inject(
      method = {"drawScreen"},
      at = {@At("RETURN")}
   )
   public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new GuiChatEvent.DrawChatEvent(mouseX, mouseY))) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"keyTyped"},
      at = {@At("RETURN")}
   )
   public void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new GuiChatEvent.KeyTyped(keyCode, typedChar))) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("RETURN")}
   )
   public void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new GuiChatEvent.MouseClicked(mouseX, mouseY, mouseButton))) {
         ci.cancel();
      }

   }

   protected void func_146286_b(int mouseX, int mouseY, int state) {
      MinecraftForge.EVENT_BUS.post(new GuiChatEvent.MouseReleased(mouseX, mouseY, state));
   }

   @Inject(
      method = {"onGuiClosed"},
      at = {@At("RETURN")}
   )
   public void onGuiClosed(CallbackInfo ci) {
      MinecraftForge.EVENT_BUS.post(new GuiChatEvent.Closed());
   }
}
