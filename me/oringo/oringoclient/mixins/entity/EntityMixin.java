package me.oringo.oringoclient.mixins.entity;

import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow
   protected UUID field_96093_i;
   @Shadow
   public double field_70142_S;
   @Shadow
   protected Random field_70146_Z;
   @Shadow
   public int field_70174_ab;
   @Shadow
   public World field_70170_p;
   @Shadow
   protected boolean field_71087_bX;
   @Shadow
   public float field_70144_Y;
   @Shadow
   public float field_70125_A;
   @Shadow
   public int field_70173_aa;
   @Shadow
   public boolean field_70145_X;
   @Shadow
   public Entity field_70154_o;
   @Shadow
   public boolean field_70122_E;
   @Shadow
   public float field_70143_R;
   @Shadow
   public float field_70177_z;
   @Shadow
   public boolean field_70160_al;
   @Shadow
   public double field_70159_w;
   @Shadow
   public double field_70181_x;
   @Shadow
   public double field_70179_y;
   @Shadow
   private int field_70151_c;
   @Shadow
   public float field_70140_Q;
   @Shadow
   public float field_82151_R;
   @Shadow
   private int field_70150_b;
   @Shadow
   public double field_70165_t;
   @Shadow
   public double field_70163_u;
   @Shadow
   public double field_70161_v;
   @Shadow
   public boolean field_70132_H;
   @Shadow
   public boolean field_70123_F;
   @Shadow
   public boolean field_70124_G;
   @Shadow
   public float field_70138_W;
   @Shadow
   protected boolean field_70134_J;
   @Shadow
   public double field_70169_q;
   @Shadow
   public double field_70166_s;

   @Shadow
   public abstract float func_70111_Y();

   @Shadow
   public abstract boolean func_70094_T();

   @Shadow
   public abstract void func_70066_B();

   @Shadow
   public abstract void func_70015_d(int var1);

   @Shadow
   public abstract boolean func_70026_G();

   @Shadow
   protected abstract void func_70081_e(int var1);

   @Shadow
   public abstract AxisAlignedBB func_174813_aQ();

   @Shadow
   public abstract void func_70060_a(float var1, float var2, float var3);

   @Shadow
   public abstract UUID func_110124_au();

   @Shadow
   public abstract boolean equals(Object var1);

   @Shadow
   public abstract boolean func_70090_H();

   @Shadow
   public void func_70091_d(double x, double y, double z) {
   }

   @Shadow
   public abstract boolean func_70051_ag();

   @Shadow
   protected abstract boolean func_70083_f(int var1);

   @Shadow
   public abstract void func_85029_a(CrashReportCategory var1);

   @Shadow
   protected abstract void func_145775_I();

   @Shadow
   protected abstract void func_180429_a(BlockPos var1, Block var2);

   @Shadow
   public abstract void func_174826_a(AxisAlignedBB var1);

   @Shadow
   private void func_174829_m() {
   }

   @Shadow
   protected abstract void func_70088_a();

   @Shadow
   public abstract boolean func_180799_ab();

   @Shadow
   public abstract boolean func_70038_c(double var1, double var3, double var5);

   @Shadow
   public abstract void func_70095_a(boolean var1);

   public void doResetPositionToBB() {
      this.func_174829_m();
   }

   public void setNextStepDistance(int nextStepDistance) {
      this.field_70150_b = nextStepDistance;
   }

   public int getNextStepDistance() {
      return this.field_70150_b;
   }

   public int getFire() {
      return this.field_70151_c;
   }

   public void SetFire(int fire) {
      this.field_70151_c = fire;
   }

   public void plusPlusFire() {
      ++this.field_70151_c;
   }
}
