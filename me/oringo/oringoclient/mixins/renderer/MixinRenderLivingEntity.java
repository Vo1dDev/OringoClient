package me.oringo.oringoclient.mixins.renderer;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.RenderEntityEvent;
import me.oringo.oringoclient.events.RenderLayersEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RendererLivingEntity.class})
public abstract class MixinRenderLivingEntity extends RenderMixin {
   @Shadow
   protected ModelBase field_77045_g;
   @Shadow
   protected boolean field_177098_i;
   @Shadow
   @Final
   private static Logger field_147923_a;

   @Shadow
   protected abstract <T extends EntityLivingBase> float func_77040_d(T var1, float var2);

   @Shadow
   protected abstract float func_77034_a(float var1, float var2, float var3);

   @Shadow
   protected abstract <T extends EntityLivingBase> void func_77039_a(T var1, double var2, double var4, double var6);

   @Shadow
   protected abstract <T extends EntityLivingBase> float func_77044_a(T var1, float var2);

   @Shadow
   protected abstract <T extends EntityLivingBase> void func_77043_a(T var1, float var2, float var3, float var4);

   @Shadow
   protected abstract <T extends EntityLivingBase> void func_77041_b(T var1, float var2);

   @Shadow
   protected abstract <T extends EntityLivingBase> boolean func_177088_c(T var1);

   @Shadow
   protected abstract void func_180565_e();

   @Shadow
   protected abstract <T extends EntityLivingBase> boolean func_177090_c(T var1, float var2);

   @Shadow
   public abstract void func_76986_a(Entity var1, double var2, double var4, double var6, float var8, float var9);

   @Shadow
   protected abstract void func_177091_f();

   @Shadow
   protected abstract <T extends EntityLivingBase> void func_77036_a(T var1, float var2, float var3, float var4, float var5, float var6, float var7);

   @Shadow
   protected abstract <T extends EntityLivingBase> void func_177093_a(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

   @Redirect(
      method = {"doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;renderModel(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V",
   ordinal = 1
)
   )
   private <T extends EntityLivingBase> void onDoRender(RendererLivingEntity instance, T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
      if (!MinecraftForge.EVENT_BUS.post(new RenderEntityEvent(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_))) {
         this.func_77036_a(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
      }

   }

   @Inject(
      method = {"renderLayers"},
      at = {@At("RETURN")},
      cancellable = true
   )
   protected void renderLayersPost(EntityLivingBase entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new RenderLayersEvent(entitylivingbaseIn, p_177093_2_, p_177093_3_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_, this.field_77045_g))) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"preRenderCallback"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <T extends EntityLivingBase> void onPreRenderCallback(T entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
      if (OringoClient.giants.isToggled() && OringoClient.giants.mobs.isEnabled() && (!(entitylivingbaseIn instanceof EntityArmorStand) || OringoClient.giants.armorStands.isEnabled())) {
         GlStateManager.func_179139_a(OringoClient.giants.scale.getValue(), OringoClient.giants.scale.getValue(), OringoClient.giants.scale.getValue());
      }

   }
}
