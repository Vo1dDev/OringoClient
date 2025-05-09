package me.oringo.oringoclient.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class BlockBoundsEvent extends Event {
   public AxisAlignedBB aabb;
   public Block block;
   public BlockPos pos;
   public Entity collidingEntity;

   public BlockBoundsEvent(Block block, AxisAlignedBB aabb, BlockPos pos, Entity collidingEntity) {
      this.aabb = aabb;
      this.block = block;
      this.pos = pos;
      this.collidingEntity = collidingEntity;
   }
}
