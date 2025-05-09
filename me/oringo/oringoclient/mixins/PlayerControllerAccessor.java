package me.oringo.oringoclient.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerControllerMP.class})
public interface PlayerControllerAccessor {
   @Accessor
   int getCurrentPlayerItem();

   @Accessor
   void setCurrentPlayerItem(int var1);
}
