package me.oringo.oringoclient.mixins.item;

import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemTool.class})
public interface ItemToolAccessor {
   @Accessor("toolClass")
   String getToolClass();
}
