package me.oringo.oringoclient.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class BlockChangeEvent extends Event {
   public BlockPos pos;
   public IBlockState state;

   public BlockChangeEvent(BlockPos pos, IBlockState state) {
      this.pos = pos;
      this.state = state;
   }
}
