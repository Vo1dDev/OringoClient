package me.oringo.oringoclient.mixins.renderer;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({RendererLivingEntity.class})
public interface RendererLivingAccessor {
   @Invoker("renderModel")
   <T extends EntityLivingBase> void renderModel(T var1, float var2, float var3, float var4, float var5, float var6, float var7);

   @Invoker("renderLayers")
   void renderLayers(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);
}
