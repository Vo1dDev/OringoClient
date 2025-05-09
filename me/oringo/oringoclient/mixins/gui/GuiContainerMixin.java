package me.oringo.oringoclient.mixins.gui;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.impl.render.PopupAnimation;
import me.oringo.oringoclient.utils.font.Fonts;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiContainer.class})
public abstract class GuiContainerMixin extends GuiScreenMixin {
   @Shadow
   public Container field_147002_h;

   @Inject(
      method = {"drawScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onDrawScreen(int k1, int slot, float i1, CallbackInfo ci) {
      if (this.field_147002_h instanceof ContainerChest && OringoClient.guiMove.shouldHideGui((ContainerChest)this.field_147002_h)) {
         this.field_146297_k.field_71415_G = true;
         this.field_146297_k.field_71417_B.func_74372_a();
         ScaledResolution res = new ScaledResolution(this.field_146297_k);
         Fonts.robotoBig.drawSmoothCenteredStringWithShadow("In terminal!", (double)(res.func_78326_a() / 2), (double)(res.func_78328_b() / 2), OringoClient.clickGui.getColor().getRGB());
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         MinecraftForge.EVENT_BUS.post(new BackgroundDrawnEvent((GuiContainer)this));
         ci.cancel();
      }
   }

   @Inject(
      method = {"drawScreen"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerBackgroundLayer(FII)V"
)},
      cancellable = true
   )
   public void onDrawBackground(int k1, int slot, float i1, CallbackInfo ci) {
      if (PopupAnimation.shouldScale((GuiScreen)this)) {
         GL11.glPushMatrix();
         PopupAnimation.doScaling();
      }

   }

   @Inject(
      method = {"drawScreen"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void onDrawScreenPost(int k1, int slot, float i1, CallbackInfo ci) {
      if (PopupAnimation.shouldScale((GuiScreen)this)) {
         GL11.glPopMatrix();
      }

   }
}
