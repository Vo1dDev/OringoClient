package me.oringo.oringoclient.mixins;

import net.minecraft.block.BlockCrops;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockCrops.class})
public abstract class BlockCropsMixin extends BlockMixin {
   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void init(CallbackInfo ci) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }
}
