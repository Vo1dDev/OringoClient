package me.oringo.oringoclient.mixins.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Render.class})
public abstract class RenderMixin {
   @Shadow
   public <T extends Entity> void func_76986_a(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
   }
}
