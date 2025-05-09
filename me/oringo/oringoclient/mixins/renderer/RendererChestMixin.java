package me.oringo.oringoclient.mixins.renderer;

import me.oringo.oringoclient.events.RenderChestEvent;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntityChestRenderer.class})
public class RendererChestMixin {
   @Inject(
      method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityChest;DDDFI)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onDrawChest(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new RenderChestEvent.Pre(te, x, y, z, partialTicks, destroyStage))) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityChest;DDDFI)V"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void onDrawChestPost(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new RenderChestEvent.Post(te, x, y, z, partialTicks, destroyStage))) {
         ci.cancel();
      }

   }
}
