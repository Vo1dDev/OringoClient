package me.oringo.oringoclient.mixins.entity;

import java.util.Iterator;
import java.util.List;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.commands.CommandHandler;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.events.MoveFlyingEvent;
import me.oringo.oringoclient.events.MoveHeadingEvent;
import me.oringo.oringoclient.events.PlayerUpdateEvent;
import me.oringo.oringoclient.events.StepEvent;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Scaffold;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Speed;
import me.oringo.oringoclient.utils.MovementUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {EntityPlayerSP.class},
   priority = 1
)
public abstract class PlayerSPMixin extends AbstractClientPlayerMixin {
   @Shadow
   public MovementInput field_71158_b;
   @Shadow
   @Final
   public NetHandlerPlayClient field_71174_a;
   @Shadow
   public float field_71086_bY;
   @Shadow
   public float field_71164_i;
   @Shadow
   public float field_71163_h;
   @Shadow
   public float field_71155_g;
   @Shadow
   public float field_71154_f;
   @Shadow
   private boolean field_175171_bO;
   @Shadow
   private float field_175165_bM;
   @Shadow
   private double field_175172_bI;
   @Shadow
   private double field_175166_bJ;
   @Shadow
   private double field_175167_bK;
   @Shadow
   private float field_175164_bL;
   @Shadow
   private int field_175168_bP;
   @Shadow
   private boolean field_175170_bN;
   @Shadow
   public int field_71157_e;
   @Shadow
   protected int field_71156_d;
   @Shadow
   protected Minecraft field_71159_c;

   @Shadow
   public abstract void func_70031_b(boolean var1);

   @Shadow
   public abstract boolean func_70093_af();

   @Shadow
   public abstract void func_71009_b(Entity var1);

   @Shadow
   public abstract void func_71047_c(Entity var1);

   @Shadow
   public abstract void func_71064_a(StatBase var1, int var2);

   @Shadow
   protected abstract boolean func_175160_A();

   @Shadow
   public abstract void func_85030_a(String var1, float var2, float var3);

   @Shadow
   public abstract boolean func_70097_a(DamageSource var1, float var2);

   @Shadow
   public abstract boolean func_70613_aW();

   @Shadow
   public abstract void func_70078_a(Entity var1);

   @Inject(
      method = {"sendChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onSenChatMessage(String message, CallbackInfo ci) {
      if (CommandHandler.handle(message)) {
         ci.cancel();
      }

   }

   @Overwrite
   public void func_175161_p() {
      MotionUpdateEvent event = new MotionUpdateEvent.Pre(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v, this.field_70177_z, this.field_70125_A, this.field_70122_E, this.func_70051_ag(), this.func_70093_af());
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         boolean flag = event.sprinting;
         if (flag != this.field_175171_bO) {
            if (flag) {
               this.field_71174_a.func_147297_a(new C0BPacketEntityAction((EntityPlayerSP)this, Action.START_SPRINTING));
            } else {
               this.field_71174_a.func_147297_a(new C0BPacketEntityAction((EntityPlayerSP)this, Action.STOP_SPRINTING));
            }

            this.field_175171_bO = flag;
         }

         boolean flag1 = event.sneaking;
         if (flag1 != this.field_175170_bN) {
            if (flag1) {
               this.field_71174_a.func_147297_a(new C0BPacketEntityAction((EntityPlayerSP)this, Action.START_SNEAKING));
            } else {
               this.field_71174_a.func_147297_a(new C0BPacketEntityAction((EntityPlayerSP)this, Action.STOP_SNEAKING));
            }

            this.field_175170_bN = flag1;
         }

         if (this.func_175160_A()) {
            double d0 = event.x - this.field_175172_bI;
            double d1 = event.y - this.field_175166_bJ;
            double d2 = event.z - this.field_175167_bK;
            double d3 = (double)(event.yaw - this.field_175164_bL);
            double d4 = (double)(event.pitch - this.field_175165_bM);
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.field_175168_bP >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;
            if (this.field_70154_o == null) {
               if (flag2 && flag3) {
                  this.field_71174_a.func_147297_a(new C06PacketPlayerPosLook(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
               } else if (flag2) {
                  this.field_71174_a.func_147297_a(new C04PacketPlayerPosition(event.x, event.y, event.z, event.onGround));
               } else if (flag3) {
                  this.field_71174_a.func_147297_a(new C05PacketPlayerLook(event.yaw, event.pitch, event.onGround));
               } else {
                  this.field_71174_a.func_147297_a(new C03PacketPlayer(event.onGround));
               }
            } else {
               this.field_71174_a.func_147297_a(new C06PacketPlayerPosLook(this.field_70159_w, -999.0D, this.field_70179_y, event.yaw, event.pitch, event.onGround));
               flag2 = false;
            }

            ++this.field_175168_bP;
            if (flag2) {
               this.field_175172_bI = event.x;
               this.field_175166_bJ = event.y;
               this.field_175167_bK = event.z;
               this.field_175168_bP = 0;
            }

            PlayerUtils.lastGround = event.onGround;
            RotationUtils.lastLastReportedPitch = this.field_175165_bM;
            if (flag3) {
               this.field_175164_bL = event.yaw;
               this.field_175165_bM = event.pitch;
            }
         }

         MinecraftForge.EVENT_BUS.post(new MotionUpdateEvent.Post(event));
      }
   }

   public void func_70664_aZ() {
      this.field_70181_x = (double)this.func_175134_bD();
      if (this.func_82165_m(Potion.field_76430_j.field_76415_H)) {
         this.field_70181_x += (double)((float)(this.func_70660_b(Potion.field_76430_j).func_76458_c() + 1) * 0.1F);
      }

      if (this.func_70051_ag() && MovementUtils.isMoving()) {
         float f = (OringoClient.sprint.isToggled() && OringoClient.sprint.omni.isEnabled() ? MovementUtils.getYaw() : (OringoClient.killAura.isToggled() && KillAura.target != null && OringoClient.killAura.movementFix.isEnabled() ? RotationUtils.getRotations(KillAura.target).getYaw() : this.field_70177_z)) * 0.017453292F;
         this.field_70159_w -= (double)(MathHelper.func_76126_a(f) * 0.2F);
         this.field_70179_y += (double)(MathHelper.func_76134_b(f) * 0.2F);
      }

      this.field_70160_al = true;
      ForgeHooks.onLivingJump((EntityPlayerSP)this);
      this.func_71029_a(StatList.field_75953_u);
      if (this.func_70051_ag()) {
         this.func_71020_j(0.8F);
      } else {
         this.func_71020_j(0.2F);
      }

   }

   public void func_70060_a(float strafe, float forward, float friction) {
      MoveFlyingEvent event = new MoveFlyingEvent(forward, strafe, friction, this.field_70177_z);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         strafe = event.getStrafe();
         forward = event.getForward();
         friction = event.getFriction();
         float f = strafe * strafe + forward * forward;
         if (f >= 1.0E-4F) {
            f = MathHelper.func_76129_c(f);
            if (f < 1.0F) {
               f = 1.0F;
            }

            f = friction / f;
            strafe *= f;
            forward *= f;
            float yaw = event.getYaw();
            float f1 = MathHelper.func_76126_a(yaw * 3.1415927F / 180.0F);
            float f2 = MathHelper.func_76134_b(yaw * 3.1415927F / 180.0F);
            this.field_70159_w += (double)(strafe * f2 - forward * f1);
            this.field_70179_y += (double)(forward * f2 + strafe * f1);
         }

      }
   }

   public void superMoveEntityWithHeading(float strafe, float forward, boolean onGround, float friction2Multi) {
      double d0;
      float f3;
      if (this.func_70613_aW()) {
         float f5;
         float f6;
         if (!this.func_70090_H() || (Entity)this instanceof EntityPlayer && this.field_71075_bZ.field_75100_b) {
            if (this.func_180799_ab() && (!((Entity)this instanceof EntityPlayer) || !this.field_71075_bZ.field_75100_b)) {
               d0 = this.field_70163_u;
               this.func_70060_a(strafe, forward, 0.02F);
               this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
               this.field_70159_w *= 0.5D;
               this.field_70181_x *= 0.5D;
               this.field_70179_y *= 0.5D;
               this.field_70181_x -= 0.02D;
               if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + d0, this.field_70179_y)) {
                  this.field_70181_x = 0.30000001192092896D;
               }
            } else {
               float f4 = 0.91F;
               if (onGround) {
                  f4 = this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c().field_149765_K * 0.91F;
               }

               float f = 0.16277136F / (f4 * f4 * f4);
               if (onGround) {
                  f5 = this.func_70689_ay() * f;
               } else {
                  f5 = this.field_70747_aH;
               }

               this.func_70060_a(strafe, forward, f5);
               f4 = 0.91F;
               if (onGround) {
                  f4 = this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c().field_149765_K * friction2Multi;
               }

               if (this.func_70617_f_()) {
                  f6 = 0.15F;
                  this.field_70159_w = MathHelper.func_151237_a(this.field_70159_w, (double)(-f6), (double)f6);
                  this.field_70179_y = MathHelper.func_151237_a(this.field_70179_y, (double)(-f6), (double)f6);
                  this.field_70143_R = 0.0F;
                  if (this.field_70181_x < -0.15D) {
                     this.field_70181_x = -0.15D;
                  }

                  boolean flag = this.func_70093_af() && (Entity)this instanceof EntityPlayer;
                  if (flag && this.field_70181_x < 0.0D) {
                     this.field_70181_x = 0.0D;
                  }
               }

               this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
               if (this.field_70123_F && this.func_70617_f_()) {
                  this.field_70181_x = 0.2D;
               }

               if (this.field_70170_p.field_72995_K && (!this.field_70170_p.func_175667_e(new BlockPos((int)this.field_70165_t, 0, (int)this.field_70161_v)) || !this.field_70170_p.func_175726_f(new BlockPos((int)this.field_70165_t, 0, (int)this.field_70161_v)).func_177410_o())) {
                  if (this.field_70163_u > 0.0D) {
                     this.field_70181_x = -0.1D;
                  } else {
                     this.field_70181_x = 0.0D;
                  }
               } else {
                  this.field_70181_x -= 0.08D;
               }

               this.field_70181_x *= 0.9800000190734863D;
               this.field_70159_w *= (double)f4;
               this.field_70179_y *= (double)f4;
            }
         } else {
            d0 = this.field_70163_u;
            f5 = 0.8F;
            f6 = 0.02F;
            f3 = (float)EnchantmentHelper.func_180318_b((Entity)this);
            if (f3 > 3.0F) {
               f3 = 3.0F;
            }

            if (!this.field_70122_E) {
               f3 *= 0.5F;
            }

            if (f3 > 0.0F) {
               f5 += (0.54600006F - f5) * f3 / 3.0F;
               f6 += (this.func_70689_ay() * 1.0F - f6) * f3 / 3.0F;
            }

            this.func_70060_a(strafe, forward, f6);
            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= (double)f5;
            this.field_70181_x *= 0.800000011920929D;
            this.field_70179_y *= (double)f5;
            this.field_70181_x -= 0.02D;
            if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + d0, this.field_70179_y)) {
               this.field_70181_x = 0.30000001192092896D;
            }
         }
      }

      this.field_70722_aY = this.field_70721_aZ;
      d0 = this.field_70165_t - this.field_70169_q;
      double d3 = this.field_70161_v - this.field_70166_s;
      f3 = MathHelper.func_76133_a(d0 * d0 + d3 * d3) * 4.0F;
      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      this.field_70721_aZ += (f3 - this.field_70721_aZ) * 0.4F;
      this.field_70754_ba += this.field_70721_aZ;
   }

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pushOutOfBlocks(double d2, double f, double blockpos, CallbackInfoReturnable<Boolean> cir) {
      cir.setReturnValue(false);
   }

   @Redirect(
      method = {"onLivingUpdate"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;isUsingItem()Z"
)
   )
   public boolean isUsingItem(EntityPlayerSP instance) {
      if (OringoClient.noSlow.isToggled()) {
         return false;
      } else {
         return instance.func_71039_bw() || OringoClient.autoBlock.isBlocking();
      }
   }

   public boolean func_70094_T() {
      return false;
   }

   @Inject(
      method = {"onLivingUpdate"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/AbstractClientPlayer;onLivingUpdate()V"
)},
      cancellable = true
   )
   public void onLivingUpdate(CallbackInfo ci) {
      if (OringoClient.sprint.omni.isEnabled() && OringoClient.sprint.isToggled()) {
         if (!MovementUtils.isMoving() || this.func_70093_af() || !((float)this.func_71024_bL().func_75116_a() > 6.0F) && !this.field_71075_bZ.field_75101_c) {
            if (this.func_70051_ag()) {
               this.func_70031_b(false);
            }
         } else if (!this.func_70051_ag()) {
            this.func_70031_b(true);
         }
      }

      if (OringoClient.speed.isToggled() && !Speed.isDisabled() && (float)this.func_71024_bL().func_75116_a() > 6.0F && !this.func_70051_ag()) {
         this.func_70031_b(true);
      }

      if (OringoClient.scaffold.isToggled()) {
         if (Scaffold.sprint.is("None") && this.func_70051_ag()) {
            this.func_70031_b(false);
         } else if (!this.func_70051_ag() && MovementUtils.isMoving()) {
            this.func_70031_b(true);
         }
      }

      if (OringoClient.noSlow.isToggled() && (this.func_71039_bw() || OringoClient.autoBlock.isBlocking())) {
         EnumAction action = this.func_70694_bm().func_77973_b().func_77661_b(this.func_70694_bm());
         MovementInput var10000;
         if (action == EnumAction.BLOCK) {
            var10000 = this.field_71158_b;
            var10000.field_78900_b = (float)((double)var10000.field_78900_b * OringoClient.noSlow.swordSlowdown.getValue());
            var10000 = this.field_71158_b;
            var10000.field_78902_a = (float)((double)var10000.field_78902_a * OringoClient.noSlow.swordSlowdown.getValue());
         } else if (action == EnumAction.BOW) {
            var10000 = this.field_71158_b;
            var10000.field_78900_b = (float)((double)var10000.field_78900_b * OringoClient.noSlow.bowSlowdown.getValue());
            var10000 = this.field_71158_b;
            var10000.field_78902_a = (float)((double)var10000.field_78902_a * OringoClient.noSlow.bowSlowdown.getValue());
         } else if (action != EnumAction.NONE) {
            var10000 = this.field_71158_b;
            var10000.field_78900_b = (float)((double)var10000.field_78900_b * OringoClient.noSlow.eatingSlowdown.getValue());
            var10000 = this.field_71158_b;
            var10000.field_78902_a = (float)((double)var10000.field_78902_a * OringoClient.noSlow.eatingSlowdown.getValue());
         }
      }

      if (OringoClient.freeCam.isToggled()) {
         this.field_70145_X = true;
      }

   }

   @Inject(
      method = {"onUpdate"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;isRiding()Z"
)},
      cancellable = true
   )
   private void onUpdate(CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new PlayerUpdateEvent())) {
         ci.cancel();
      }

   }

   public void func_70612_e(float strafe, float forward) {
      MoveHeadingEvent event = new MoveHeadingEvent(this.field_70122_E);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         double d0 = this.field_70165_t;
         double d1 = this.field_70163_u;
         double d2 = this.field_70161_v;
         if (this.field_71075_bZ.field_75100_b && this.field_70154_o == null) {
            double d3 = this.field_70181_x;
            float f = this.field_70747_aH;
            this.field_70747_aH = this.field_71075_bZ.func_75093_a() * (float)(this.func_70051_ag() ? 2 : 1);
            super.func_70612_e(strafe, forward);
            this.field_70181_x = d3 * 0.6D;
            this.field_70747_aH = f;
         } else {
            this.superMoveEntityWithHeading(strafe, forward, event.isOnGround(), event.getFriction2Multi());
         }

         this.func_71000_j(this.field_70165_t - d0, this.field_70163_u - d1, this.field_70161_v - d2);
      }
   }

   public void func_70091_d(double x, double y, double z) {
      MoveEvent event = new MoveEvent(x, y, z);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         x = event.getX();
         y = event.getY();
         z = event.getZ();
         if (this.field_70145_X) {
            this.func_174826_a(this.func_174813_aQ().func_72317_d(x, y, z));
            this.doResetPositionToBB();
         } else {
            this.field_70170_p.field_72984_F.func_76320_a("move");
            double d0 = this.field_70165_t;
            double d1 = this.field_70163_u;
            double d2 = this.field_70161_v;
            if (this.field_70134_J) {
               this.field_70134_J = false;
               x *= 0.25D;
               y *= 0.05000000074505806D;
               z *= 0.25D;
               this.field_70159_w = 0.0D;
               this.field_70181_x = 0.0D;
               this.field_70179_y = 0.0D;
            }

            double d3 = x;
            double d4 = y;
            double d5 = z;
            boolean flag = (this.field_70122_E && this.func_70093_af() || PlayerUtils.isOnGround(1.0D) && OringoClient.scaffold.isToggled() && Scaffold.safeWalk.isEnabled()) && (Entity)this instanceof EntityPlayer;
            if (flag) {
               double d6;
               for(d6 = 0.05D; x != 0.0D && this.field_70170_p.func_72945_a((Entity)this, this.func_174813_aQ().func_72317_d(x, -1.0D, 0.0D)).isEmpty(); d3 = x) {
                  if (x < d6 && x >= -d6) {
                     x = 0.0D;
                  } else if (x > 0.0D) {
                     x -= d6;
                  } else {
                     x += d6;
                  }
               }

               for(; z != 0.0D && this.field_70170_p.func_72945_a((Entity)this, this.func_174813_aQ().func_72317_d(0.0D, -1.0D, z)).isEmpty(); d5 = z) {
                  if (z < d6 && z >= -d6) {
                     z = 0.0D;
                  } else if (z > 0.0D) {
                     z -= d6;
                  } else {
                     z += d6;
                  }
               }

               for(; x != 0.0D && z != 0.0D && this.field_70170_p.func_72945_a((Entity)this, this.func_174813_aQ().func_72317_d(x, -1.0D, z)).isEmpty(); d5 = z) {
                  if (x < d6 && x >= -d6) {
                     x = 0.0D;
                  } else if (x > 0.0D) {
                     x -= d6;
                  } else {
                     x += d6;
                  }

                  d3 = x;
                  if (z < d6 && z >= -d6) {
                     z = 0.0D;
                  } else if (z > 0.0D) {
                     z -= d6;
                  } else {
                     z += d6;
                  }
               }
            }

            List<AxisAlignedBB> list1 = this.field_70170_p.func_72945_a((Entity)this, this.func_174813_aQ().func_72321_a(x, y, z));
            AxisAlignedBB axisalignedbb = this.func_174813_aQ();

            AxisAlignedBB axisalignedbb1;
            for(Iterator var23 = list1.iterator(); var23.hasNext(); y = axisalignedbb1.func_72323_b(this.func_174813_aQ(), y)) {
               axisalignedbb1 = (AxisAlignedBB)var23.next();
            }

            this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, y, 0.0D));
            boolean flag1 = this.field_70122_E || d4 != y && d4 < 0.0D;

            AxisAlignedBB axisalignedbb13;
            Iterator var57;
            for(var57 = list1.iterator(); var57.hasNext(); x = axisalignedbb13.func_72316_a(this.func_174813_aQ(), x)) {
               axisalignedbb13 = (AxisAlignedBB)var57.next();
            }

            this.func_174826_a(this.func_174813_aQ().func_72317_d(x, 0.0D, 0.0D));

            for(var57 = list1.iterator(); var57.hasNext(); z = axisalignedbb13.func_72322_c(this.func_174813_aQ(), z)) {
               axisalignedbb13 = (AxisAlignedBB)var57.next();
            }

            this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, 0.0D, z));
            if (this.field_70138_W > 0.0F && flag1 && (d3 != x || d5 != z)) {
               double d11 = x;
               double d7 = y;
               double d8 = z;
               AxisAlignedBB axisalignedbb3 = this.func_174813_aQ();
               this.func_174826_a(axisalignedbb);
               StepEvent.Pre stepEvent = new StepEvent.Pre((double)this.field_70138_W);
               MinecraftForge.EVENT_BUS.post(stepEvent);
               y = stepEvent.getHeight();
               List<AxisAlignedBB> list = this.field_70170_p.func_72945_a((Entity)this, this.func_174813_aQ().func_72321_a(d3, y, d5));
               AxisAlignedBB axisalignedbb4 = this.func_174813_aQ();
               AxisAlignedBB axisalignedbb5 = axisalignedbb4.func_72321_a(d3, 0.0D, d5);
               double d9 = y;

               AxisAlignedBB axisalignedbb6;
               for(Iterator var37 = list.iterator(); var37.hasNext(); d9 = axisalignedbb6.func_72323_b(axisalignedbb5, d9)) {
                  axisalignedbb6 = (AxisAlignedBB)var37.next();
               }

               axisalignedbb4 = axisalignedbb4.func_72317_d(0.0D, d9, 0.0D);
               double d15 = d3;

               AxisAlignedBB axisalignedbb7;
               for(Iterator var39 = list.iterator(); var39.hasNext(); d15 = axisalignedbb7.func_72316_a(axisalignedbb4, d15)) {
                  axisalignedbb7 = (AxisAlignedBB)var39.next();
               }

               axisalignedbb4 = axisalignedbb4.func_72317_d(d15, 0.0D, 0.0D);
               double d16 = d5;

               AxisAlignedBB axisalignedbb8;
               for(Iterator var41 = list.iterator(); var41.hasNext(); d16 = axisalignedbb8.func_72322_c(axisalignedbb4, d16)) {
                  axisalignedbb8 = (AxisAlignedBB)var41.next();
               }

               axisalignedbb4 = axisalignedbb4.func_72317_d(0.0D, 0.0D, d16);
               AxisAlignedBB axisalignedbb14 = this.func_174813_aQ();
               double d17 = y;

               AxisAlignedBB axisalignedbb9;
               for(Iterator var44 = list.iterator(); var44.hasNext(); d17 = axisalignedbb9.func_72323_b(axisalignedbb14, d17)) {
                  axisalignedbb9 = (AxisAlignedBB)var44.next();
               }

               axisalignedbb14 = axisalignedbb14.func_72317_d(0.0D, d17, 0.0D);
               double d18 = d3;

               AxisAlignedBB axisalignedbb10;
               for(Iterator var46 = list.iterator(); var46.hasNext(); d18 = axisalignedbb10.func_72316_a(axisalignedbb14, d18)) {
                  axisalignedbb10 = (AxisAlignedBB)var46.next();
               }

               axisalignedbb14 = axisalignedbb14.func_72317_d(d18, 0.0D, 0.0D);
               double d19 = d5;

               AxisAlignedBB axisalignedbb11;
               for(Iterator var48 = list.iterator(); var48.hasNext(); d19 = axisalignedbb11.func_72322_c(axisalignedbb14, d19)) {
                  axisalignedbb11 = (AxisAlignedBB)var48.next();
               }

               axisalignedbb14 = axisalignedbb14.func_72317_d(0.0D, 0.0D, d19);
               double d20 = d15 * d15 + d16 * d16;
               double d10 = d18 * d18 + d19 * d19;
               if (d20 > d10) {
                  x = d15;
                  z = d16;
                  y = -d9;
                  this.func_174826_a(axisalignedbb4);
               } else {
                  x = d18;
                  z = d19;
                  y = -d17;
                  this.func_174826_a(axisalignedbb14);
               }

               AxisAlignedBB axisalignedbb12;
               for(Iterator var52 = list.iterator(); var52.hasNext(); y = axisalignedbb12.func_72323_b(this.func_174813_aQ(), y)) {
                  axisalignedbb12 = (AxisAlignedBB)var52.next();
               }

               this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, y, 0.0D));
               if (d11 * d11 + d8 * d8 >= x * x + z * z) {
                  x = d11;
                  y = d7;
                  z = d8;
                  this.func_174826_a(axisalignedbb3);
               } else {
                  MinecraftForge.EVENT_BUS.post(new StepEvent.Post(1.0D + y));
               }
            }

            this.field_70170_p.field_72984_F.func_76319_b();
            this.field_70170_p.field_72984_F.func_76320_a("rest");
            this.doResetPositionToBB();
            this.field_70123_F = d3 != x || d5 != z;
            this.field_70124_G = d4 != y;
            this.field_70122_E = this.field_70124_G && d4 < 0.0D;
            this.field_70132_H = this.field_70123_F || this.field_70124_G;
            int i = MathHelper.func_76128_c(this.field_70165_t);
            int j = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
            int k = MathHelper.func_76128_c(this.field_70161_v);
            BlockPos blockpos = new BlockPos(i, j, k);
            Block block1 = this.field_70170_p.func_180495_p(blockpos).func_177230_c();
            if (block1.func_149688_o() == Material.field_151579_a) {
               Block block = this.field_70170_p.func_180495_p(blockpos.func_177977_b()).func_177230_c();
               if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate) {
                  block1 = block;
                  blockpos = blockpos.func_177977_b();
               }
            }

            this.func_180433_a(y, this.field_70122_E, block1, blockpos);
            if (d3 != x) {
               this.field_70159_w = 0.0D;
            }

            if (d5 != z) {
               this.field_70179_y = 0.0D;
            }

            if (d4 != y) {
               block1.func_176216_a(this.field_70170_p, (Entity)this);
            }

            if (this.func_70041_e_() && !flag && this.field_70154_o == null) {
               double d12 = this.field_70165_t - d0;
               double d13 = this.field_70163_u - d1;
               double d14 = this.field_70161_v - d2;
               if (block1 != Blocks.field_150468_ap) {
                  d13 = 0.0D;
               }

               if (block1 != null && this.field_70122_E) {
                  block1.func_176199_a(this.field_70170_p, blockpos, (Entity)this);
               }

               this.field_70140_Q = (float)((double)this.field_70140_Q + (double)MathHelper.func_76133_a(d12 * d12 + d14 * d14) * 0.6D);
               this.field_82151_R = (float)((double)this.field_82151_R + (double)MathHelper.func_76133_a(d12 * d12 + d13 * d13 + d14 * d14) * 0.6D);
               if (this.field_82151_R > (float)this.getNextStepDistance() && block1.func_149688_o() != Material.field_151579_a) {
                  this.setNextStepDistance((int)this.field_82151_R + 1);
                  if (this.func_70090_H()) {
                     float f = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w * 0.20000000298023224D + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y * 0.20000000298023224D) * 0.35F;
                     if (f > 1.0F) {
                        f = 1.0F;
                     }

                     this.func_85030_a(this.func_145776_H(), f, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
                  }

                  this.func_180429_a(blockpos, block1);
               }
            }

            try {
               this.func_145775_I();
            } catch (Throwable var54) {
               CrashReport crashreport = CrashReport.func_85055_a(var54, "Checking entity block collision");
               CrashReportCategory crashreportcategory = crashreport.func_85058_a("Entity being checked for collision");
               this.func_85029_a(crashreportcategory);
               throw new ReportedException(crashreport);
            }

            boolean flag2 = this.func_70026_G();
            if (this.field_70170_p.func_147470_e(this.func_174813_aQ().func_72331_e(0.001D, 0.001D, 0.001D))) {
               this.func_70081_e(1);
               if (!flag2) {
                  this.plusPlusFire();
                  if (this.getFire() == 0) {
                     this.func_70015_d(8);
                  }
               }
            } else if (this.getFire() <= 0) {
               this.SetFire(-this.field_70174_ab);
            }

            if (flag2 && this.getFire() > 0) {
               this.func_85030_a("random.fizz", 0.7F, 1.6F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
               this.SetFire(-this.field_70174_ab);
            }

            this.field_70170_p.field_72984_F.func_76319_b();
         }

      }
   }

   @Inject(
      method = {"updateEntityActionState"},
      at = {@At("RETURN")}
   )
   public void onUpdateAction(CallbackInfo ci) {
      if (OringoClient.speed.isToggled() && !Speed.isDisabled() && MovementUtils.isMoving()) {
         this.field_70703_bu = false;
      }

   }

   public void func_71059_n(Entity targetEntity) {
      if (ForgeHooks.onPlayerAttackTarget((EntityPlayer)this, targetEntity) && targetEntity.func_70075_an() && !targetEntity.func_85031_j((EntityPlayer)this)) {
         float f = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
         int i = 0;
         float f1 = 0.0F;
         if (targetEntity instanceof EntityLivingBase) {
            f1 = EnchantmentHelper.func_152377_a(this.func_70694_bm(), ((EntityLivingBase)targetEntity).func_70668_bt());
         } else {
            f1 = EnchantmentHelper.func_152377_a(this.func_70694_bm(), EnumCreatureAttribute.UNDEFINED);
         }

         int i = i + EnchantmentHelper.func_77501_a((EntityPlayer)this);
         if (this.func_70051_ag()) {
            ++i;
         }

         if (f > 0.0F || f1 > 0.0F) {
            boolean flag = this.field_70143_R > 0.0F && !this.field_70122_E && !this.func_70617_f_() && !this.func_70090_H() && !this.func_70644_a(Potion.field_76440_q) && this.field_70154_o == null && targetEntity instanceof EntityLivingBase;
            if (flag && f > 0.0F) {
               f *= 1.5F;
            }

            f += f1;
            boolean flag1 = false;
            int j = EnchantmentHelper.func_90036_a((EntityPlayer)this);
            if (targetEntity instanceof EntityLivingBase && j > 0 && !targetEntity.func_70027_ad()) {
               flag1 = true;
               targetEntity.func_70015_d(1);
            }

            double d0 = targetEntity.field_70159_w;
            double d1 = targetEntity.field_70181_x;
            double d2 = targetEntity.field_70179_y;
            boolean flag2 = targetEntity.func_70097_a(DamageSource.func_76365_a((EntityPlayer)this), f);
            if (flag2) {
               if (i > 0) {
                  targetEntity.func_70024_g((double)(-MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F) * (float)i * 0.5F));
                  if (!OringoClient.sprint.isToggled() || !OringoClient.sprint.keep.isEnabled()) {
                     this.field_70159_w *= 0.6D;
                     this.field_70179_y *= 0.6D;
                     this.func_70031_b(false);
                  }
               }

               if (targetEntity instanceof EntityPlayerMP && targetEntity.field_70133_I) {
                  ((EntityPlayerMP)targetEntity).field_71135_a.func_147359_a(new S12PacketEntityVelocity(targetEntity));
                  targetEntity.field_70133_I = false;
                  targetEntity.field_70159_w = d0;
                  targetEntity.field_70181_x = d1;
                  targetEntity.field_70179_y = d2;
               }

               if (flag) {
                  this.func_71009_b(targetEntity);
               }

               if (f1 > 0.0F) {
                  this.func_71047_c(targetEntity);
               }

               if (f >= 18.0F) {
                  this.func_71029_a(AchievementList.field_75999_E);
               }

               this.func_130011_c(targetEntity);
               if (targetEntity instanceof EntityLivingBase) {
                  EnchantmentHelper.func_151384_a((EntityLivingBase)targetEntity, (EntityPlayer)this);
               }

               EnchantmentHelper.func_151385_b((EntityPlayer)this, targetEntity);
               ItemStack itemstack = this.func_71045_bC();
               Entity entity = targetEntity;
               if (targetEntity instanceof EntityDragonPart) {
                  IEntityMultiPart ientitymultipart = ((EntityDragonPart)targetEntity).field_70259_a;
                  if (ientitymultipart instanceof EntityLivingBase) {
                     entity = (EntityLivingBase)ientitymultipart;
                  }
               }

               if (itemstack != null && entity instanceof EntityLivingBase) {
                  itemstack.func_77961_a((EntityLivingBase)entity, (EntityPlayer)this);
                  if (itemstack.field_77994_a <= 0) {
                     this.func_71028_bD();
                  }
               }

               if (targetEntity instanceof EntityLivingBase) {
                  this.func_71064_a(StatList.field_75951_w, Math.round(f * 10.0F));
                  if (j > 0) {
                     targetEntity.func_70015_d(j * 4);
                  }
               }

               this.func_71020_j(0.3F);
            } else if (flag1) {
               targetEntity.func_70066_B();
            }
         }
      }

   }
}
