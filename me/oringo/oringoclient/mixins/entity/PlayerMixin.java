package me.oringo.oringoclient.mixins.entity;

import me.oringo.oringoclient.OringoClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({EntityPlayer.class})
public abstract class PlayerMixin extends EntityLivingBaseMixin {
   @Shadow
   public PlayerCapabilities field_71075_bZ;
   @Shadow
   private int field_71072_f;
   @Shadow
   public InventoryPlayer field_71071_by;
   @Shadow
   protected float field_71102_ce;
   @Shadow
   public float field_71106_cc;
   @Shadow
   public int field_71068_ca;
   @Shadow
   public int field_71067_cb;
   @Shadow
   public float eyeHeight;
   @Shadow
   protected float field_71108_cd;
   @Shadow
   protected FoodStats field_71100_bB;
   @Shadow
   public float field_71079_bU;
   @Shadow
   public float field_71082_cx;
   @Shadow
   public EntityFishHook field_71104_cf;
   private boolean wasSprinting;

   @Shadow
   public abstract void func_71029_a(StatBase var1);

   @Shadow
   public abstract void func_71020_j(float var1);

   @Shadow
   public abstract FoodStats func_71024_bL();

   @Shadow
   public abstract void func_71059_n(Entity var1);

   @Shadow
   public abstract ItemStack func_70694_bm();

   @Shadow
   public abstract ItemStack func_71045_bC();

   @Shadow
   public abstract void func_71028_bD();

   @Shadow
   protected void func_70626_be() {
   }

   @Shadow
   public abstract boolean func_71039_bw();

   @Shadow
   public abstract ItemStack func_71011_bu();

   @Shadow
   protected abstract String func_145776_H();

   @Shadow
   protected abstract boolean func_70041_e_();

   @Shadow
   public boolean func_70094_T() {
      return false;
   }

   @Shadow
   public abstract boolean func_70097_a(DamageSource var1, float var2);

   @Shadow
   protected abstract void func_70088_a();

   @Shadow
   public void func_70612_e(float strafe, float forward) {
   }

   @Shadow
   public abstract void func_71000_j(double var1, double var3, double var5);

   @Shadow
   public abstract void func_70071_h_();

   @Shadow
   public abstract float func_70689_ay();

   @Shadow
   public abstract EnumStatus func_180469_a(BlockPos var1);

   public float func_70111_Y() {
      return OringoClient.hitboxes.isToggled() ? (float)OringoClient.hitboxes.expand.getValue() : 0.1F;
   }
}
