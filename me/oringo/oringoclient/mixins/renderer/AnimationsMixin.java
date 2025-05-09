package me.oringo.oringoclient.mixins.renderer;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {ItemRenderer.class},
   priority = 1
)
public abstract class AnimationsMixin {
   @Shadow
   private float field_78451_d;
   @Shadow
   private float field_78454_c;
   @Shadow
   @Final
   private Minecraft field_78455_a;
   @Shadow
   private ItemStack field_78453_b;

   @Shadow
   protected abstract void func_178101_a(float var1, float var2);

   @Shadow
   protected abstract void func_178109_a(AbstractClientPlayer var1);

   @Shadow
   protected abstract void func_178110_a(EntityPlayerSP var1, float var2);

   @Shadow
   protected abstract void func_178097_a(AbstractClientPlayer var1, float var2, float var3, float var4);

   @Shadow
   protected abstract void func_178104_a(AbstractClientPlayer var1, float var2);

   @Shadow
   protected abstract void func_178105_d(float var1);

   @Shadow
   public abstract void func_178099_a(EntityLivingBase var1, ItemStack var2, TransformType var3);

   @Shadow
   protected abstract void func_178095_a(AbstractClientPlayer var1, float var2, float var3);

   @Shadow
   protected abstract void func_178098_a(float var1, AbstractClientPlayer var2);

   @Overwrite
   public void func_78440_a(float partialTicks) {
      float f = 1.0F - (this.field_78451_d + (this.field_78454_c - this.field_78451_d) * partialTicks);
      AbstractClientPlayer abstractclientplayer = this.field_78455_a.field_71439_g;
      float f1 = abstractclientplayer.func_70678_g(partialTicks);
      float f2 = abstractclientplayer.field_70127_C + (abstractclientplayer.field_70125_A - abstractclientplayer.field_70127_C) * partialTicks;
      float f3 = abstractclientplayer.field_70126_B + (abstractclientplayer.field_70177_z - abstractclientplayer.field_70126_B) * partialTicks;
      this.func_178101_a(f2, f3);
      this.func_178109_a(abstractclientplayer);
      this.func_178110_a((EntityPlayerSP)abstractclientplayer, partialTicks);
      GlStateManager.func_179091_B();
      GlStateManager.func_179094_E();
      if (this.field_78453_b != null) {
         boolean shouldSpoofBlocking = KillAura.target != null && !OringoClient.killAura.blockMode.getSelected().equals("None") || !OringoClient.autoBlock.blockTimer.hasTimePassed((long)OringoClient.autoBlock.blockTime.getValue()) && OringoClient.autoBlock.canBlock();
         if (this.field_78453_b.func_77973_b() instanceof ItemMap) {
            this.func_178097_a(abstractclientplayer, f2, f, f1);
         } else if (abstractclientplayer.func_71052_bv() <= 0 && !shouldSpoofBlocking) {
            this.func_178105_d(f1);
            this.func_178096_b(f, f1);
         } else {
            EnumAction enumaction = this.field_78453_b.func_77975_n();
            if (shouldSpoofBlocking) {
               enumaction = EnumAction.BLOCK;
            }

            label87:
            switch(enumaction) {
            case NONE:
               this.func_178096_b(f, 0.0F);
               break;
            case EAT:
            case DRINK:
               this.func_178104_a(abstractclientplayer, partialTicks);
               this.func_178096_b(f, 0.0F);
               break;
            case BLOCK:
               if (OringoClient.animations.isToggled()) {
                  String var9 = OringoClient.animations.mode.getSelected();
                  byte var10 = -1;
                  switch(var9.hashCode()) {
                  case -2075965457:
                     if (var9.equals("long hit")) {
                        var10 = 3;
                     }
                     break;
                  case -1349088399:
                     if (var9.equals("custom")) {
                        var10 = 6;
                     }
                     break;
                  case -1170163028:
                     if (var9.equals("vertical spin")) {
                        var10 = 2;
                     }
                     break;
                  case -243033129:
                     if (var9.equals("helicopter")) {
                        var10 = 7;
                     }
                     break;
                  case 48570:
                     if (var9.equals("1.7")) {
                        var10 = 0;
                     }
                     break;
                  case 3452698:
                     if (var9.equals("push")) {
                        var10 = 5;
                     }
                     break;
                  case 3536962:
                     if (var9.equals("spin")) {
                        var10 = 1;
                     }
                     break;
                  case 94631204:
                     if (var9.equals("chill")) {
                        var10 = 4;
                     }
                  }

                  switch(var10) {
                  case 0:
                     this.func_178096_b(f, f1);
                     this.func_178103_d();
                     break label87;
                  case 1:
                  case 2:
                     this.func_178096_b(f, OringoClient.animations.showSwing.isEnabled() ? f1 : 0.0F);
                     this.func_178103_d();
                     break label87;
                  case 3:
                     this.func_178096_b(f, 0.0F);
                     this.func_178103_d();
                     float var19 = MathHelper.func_76126_a(MathHelper.func_76129_c(f1) * 3.1415927F);
                     GlStateManager.func_179109_b(-0.05F, 0.6F, 0.3F);
                     GlStateManager.func_179114_b(-var19 * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                     GlStateManager.func_179114_b(-var19 * 70.0F, 1.5F, -0.4F, -0.0F);
                     break label87;
                  case 4:
                     float f16 = MathHelper.func_76126_a(MathHelper.func_76129_c(f1) * 3.1415927F);
                     this.func_178096_b(f / 2.0F - 0.18F, 0.0F);
                     GL11.glRotatef(f16 * 60.0F / 2.0F, -f16 / 2.0F, -0.0F, -16.0F);
                     GL11.glRotatef(-f16 * 30.0F, 1.0F, f16 / 2.0F, -1.0F);
                     this.func_178103_d();
                     break label87;
                  case 5:
                     this.func_178096_b(f, -f1);
                     this.func_178103_d();
                     break label87;
                  case 6:
                     this.func_178096_b(OringoClient.animationCreator.blockProgress.isEnabled() ? f : 0.0F, OringoClient.animationCreator.swingProgress.isEnabled() ? f1 : 0.0F);
                     this.func_178103_d();
                     break label87;
                  case 7:
                     GlStateManager.func_179114_b((float)(System.currentTimeMillis() / 3L % 360L), 0.0F, 0.0F, -0.1F);
                     this.func_178096_b(f / 1.6F, 0.0F);
                     this.func_178103_d();
                  }
               } else {
                  this.func_178096_b(f, 0.0F);
                  this.func_178103_d();
               }
               break;
            case BOW:
               this.func_178096_b(f, 0.0F);
               this.func_178098_a(partialTicks, abstractclientplayer);
            }
         }

         this.func_178099_a(abstractclientplayer, this.field_78453_b, TransformType.FIRST_PERSON);
      } else if (!abstractclientplayer.func_82150_aj()) {
         this.func_178095_a(abstractclientplayer, f, f1);
      }

      GlStateManager.func_179121_F();
      GlStateManager.func_179101_C();
      RenderHelper.func_74518_a();
   }

   @Overwrite
   private void func_178096_b(float equipProgress, float swingProgress) {
      float size = (float)OringoClient.animations.size.getValue();
      float x = (float)OringoClient.animations.x.getValue();
      float y = (float)OringoClient.animations.y.getValue();
      float z = (float)OringoClient.animations.z.getValue();
      GlStateManager.func_179109_b(0.56F * x, -0.52F * y, -0.71999997F * z);
      GlStateManager.func_179109_b(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.func_179114_b(45.0F, 0.0F, 1.0F, 0.0F);
      float f = MathHelper.func_76126_a(swingProgress * swingProgress * 3.1415927F);
      float f1 = MathHelper.func_76126_a(MathHelper.func_76129_c(swingProgress) * 3.1415927F);
      GlStateManager.func_179114_b(f * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(0.4F * size, 0.4F * size, 0.4F * size);
   }

   @Overwrite
   private void func_178103_d() {
      float angle1 = 30.0F;
      float angle2 = -80.0F;
      float angle3 = 60.0F;
      float translateX = -0.5F;
      float translateY = 0.2F;
      float translateZ = 0.0F;
      float rotation1x = 0.0F;
      float rotation1y = 1.0F;
      float rotation1z = 0.0F;
      float rotation2x = 1.0F;
      float rotation2y = 0.0F;
      float rotation2z = 0.0F;
      String var13 = OringoClient.animations.mode.getSelected();
      byte var14 = -1;
      switch(var13.hashCode()) {
      case -1349088399:
         if (var13.equals("custom")) {
            var14 = 0;
         }
         break;
      case -1170163028:
         if (var13.equals("vertical spin")) {
            var14 = 1;
         }
         break;
      case 3536962:
         if (var13.equals("spin")) {
            var14 = 2;
         }
      }

      switch(var14) {
      case 0:
         angle1 = (float)OringoClient.animationCreator.angle1.getValue();
         angle2 = (float)OringoClient.animationCreator.angle2.getValue();
         angle3 = (float)OringoClient.animationCreator.angle3.getValue();
         translateX = (float)OringoClient.animationCreator.translateX.getValue();
         translateY = (float)OringoClient.animationCreator.translateY.getValue();
         translateZ = (float)OringoClient.animationCreator.translateZ.getValue();
         rotation1x = (float)OringoClient.animationCreator.rotation1x.getValue();
         rotation1y = (float)OringoClient.animationCreator.rotation1y.getValue();
         rotation1z = (float)OringoClient.animationCreator.rotation1z.getValue();
         rotation2x = (float)OringoClient.animationCreator.rotation2x.getValue();
         rotation2y = (float)OringoClient.animationCreator.rotation2y.getValue();
         rotation2z = (float)OringoClient.animationCreator.rotation2z.getValue();
         break;
      case 1:
         angle1 = (float)(System.currentTimeMillis() % 720L);
         angle1 /= 2.0F;
         rotation2x = 0.0F;
         angle2 = 0.0F;
         break;
      case 2:
         translateY = 0.8F;
         angle1 = 60.0F;
         angle2 = (float)(-System.currentTimeMillis() % 720L);
         angle2 /= 2.0F;
         rotation2z = 0.8F;
         angle3 = 30.0F;
      }

      GlStateManager.func_179109_b(translateX, translateY, translateZ);
      GlStateManager.func_179114_b(angle1, rotation1x, rotation1y, rotation1z);
      GlStateManager.func_179114_b(angle2, rotation2x, rotation2y, rotation2z);
      GlStateManager.func_179114_b(angle3, 0.0F, 1.0F, 0.0F);
   }
}
