package me.oringo.oringoclient.mixins.packet;

import net.minecraft.network.play.client.C0EPacketClickWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({C0EPacketClickWindow.class})
public interface C0EAccessor {
   @Accessor("windowID")
   void setWindowID(int var1);
}
