package me.oringo.oringoclient.mixins;

import java.util.List;
import me.oringo.oringoclient.events.BlockBoundsEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {Block.class},
   priority = 1
)
public abstract class BlockMixin {
   @Shadow
   public abstract void func_149676_a(float var1, float var2, float var3, float var4, float var5, float var6);

   @Shadow
   public abstract AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3);

   @Overwrite
   public void func_180638_a(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      BlockBoundsEvent event = new BlockBoundsEvent((Block)this, this.func_180640_a(worldIn, pos, state), pos, collidingEntity);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         if (event.aabb != null && mask.func_72326_a(event.aabb)) {
            list.add(event.aabb);
         }

      }
   }
}
