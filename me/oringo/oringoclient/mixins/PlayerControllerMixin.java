package me.oringo.oringoclient.mixins;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PreAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerControllerMP.class})
public class PlayerControllerMixin {
   @Redirect(
      method = {"onPlayerDamageBlock"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)F"
)
   )
   public float onPlayerDamageBlock(Block instance, EntityPlayer playerIn, World worldIn, BlockPos pos) {
      float hardness = instance.func_180647_a(playerIn, worldIn, pos);
      if (OringoClient.fastBreak.isToggled()) {
         hardness = (float)((double)hardness * OringoClient.fastBreak.mineSpeed.getValue());
      }

      return hardness;
   }

   @Inject(
      method = {"attackEntity"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V"
)}
   )
   public void attackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new PreAttackEvent(targetEntity))) {
         ci.cancel();
      }

   }
}
