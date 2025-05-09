package me.oringo.oringoclient.mixins.packet;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({C02PacketUseEntity.class})
public interface C02Accessor {
   @Accessor
   void setEntityId(int var1);

   @Accessor
   void setAction(Action var1);
}
