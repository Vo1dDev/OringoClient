package me.oringo.oringoclient.mixins;

import me.oringo.oringoclient.events.BlockChangeEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Chunk.class})
public class MixinChunk {
   @Inject(
      method = {"setBlockState"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onBlockChange(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir) {
      if (MinecraftForge.EVENT_BUS.post(new BlockChangeEvent(pos, state))) {
         cir.setReturnValue(state);
      }

   }
}
