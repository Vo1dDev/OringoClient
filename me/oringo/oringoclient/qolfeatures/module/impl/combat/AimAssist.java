package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import java.util.Comparator;
import java.util.List;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.EntityUtils;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AimAssist extends Module {
   public NumberSetting fov = new NumberSetting("Fov", 60.0D, 30.0D, 180.0D, 1.0D);
   public NumberSetting speed = new NumberSetting("Max speed", 30.0D, 1.0D, 40.0D, 0.1D) {
      public void setValue(double value) {
         super.setValue(value);
         if (value < AimAssist.this.minSpeed.getValue()) {
            this.setValue(AimAssist.this.minSpeed.getValue());
         }

      }
   };
   public NumberSetting minSpeed = new NumberSetting("Min speed", 20.0D, 1.0D, 40.0D, 0.1D) {
      public void setValue(double value) {
         super.setValue(value);
         if (this.getValue() > AimAssist.this.speed.getValue()) {
            this.setValue(AimAssist.this.speed.getValue());
         }

      }
   };
   public NumberSetting range = new NumberSetting("Range", 5.0D, 0.0D, 6.0D, 0.1D);
   public BooleanSetting vertical = new BooleanSetting("Vertical", true);
   public BooleanSetting players = new BooleanSetting("Players", true);
   public BooleanSetting mobs = new BooleanSetting("Mobs", false);
   public BooleanSetting invisibles = new BooleanSetting("Invisibles", false);
   public BooleanSetting teams = new BooleanSetting("Teams", true);

   public AimAssist() {
      super("Aim Assist", Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.fov, this.range, this.minSpeed, this.speed, this.players, this.mobs, this.teams, this.invisibles, this.vertical});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (this.isToggled()) {
         Entity target = this.getTarget();
         if (target != null && mc.field_71476_x != null && mc.field_71476_x.field_72308_g != target) {
            Rotation rotation = this.getRotation(target);
            float yaw = mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(rotation.getYaw() - mc.field_71439_g.field_70177_z);
            float pitch = mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(rotation.getPitch() - mc.field_71439_g.field_70125_A);
            float diffY = (float)((double)(yaw - mc.field_71439_g.field_70177_z) / MathUtil.getRandomInRange(this.speed.getValue(), this.minSpeed.getValue()));
            float diffP = (float)((double)(pitch - mc.field_71439_g.field_70125_A) / MathUtil.getRandomInRange(this.speed.getValue(), this.minSpeed.getValue()));
            mc.field_71439_g.field_70177_z += diffY;
            if (this.vertical.isEnabled()) {
               mc.field_71439_g.field_70125_A += diffP;
            }
         }
      }

   }

   public Entity getTarget() {
      List<Entity> validEntities = mc.field_71441_e.func_175644_a(EntityLivingBase.class, (entity) -> {
         return this.isValid((EntityLivingBase)entity);
      });
      validEntities.sort(Comparator.comparingDouble((entity) -> {
         return (double)mc.field_71439_g.func_70032_d(entity);
      }));
      return !validEntities.isEmpty() ? (Entity)validEntities.get(0) : null;
   }

   private boolean isValid(EntityLivingBase entity) {
      if (entity != mc.field_71439_g && AntiBot.isValidEntity(entity) && (this.invisibles.isEnabled() || !entity.func_82150_aj()) && !(entity instanceof EntityArmorStand) && mc.field_71439_g.func_70685_l(entity) && !(entity.func_110143_aJ() <= 0.0F) && !((double)entity.func_70032_d(mc.field_71439_g) > this.range.getValue()) && !((double)Math.abs(MathHelper.func_76142_g(mc.field_71439_g.field_70177_z) - MathHelper.func_76142_g(this.getRotation(entity).getYaw())) > this.fov.getValue())) {
         if ((entity instanceof EntityMob || entity instanceof EntityAmbientCreature || entity instanceof EntityWaterMob || entity instanceof EntityAnimal || entity instanceof EntitySlime) && !this.mobs.isEnabled()) {
            return false;
         } else if (!(entity instanceof EntityPlayer) || (!EntityUtils.isTeam(entity) || !this.teams.isEnabled()) && this.players.isEnabled()) {
            return !(entity instanceof EntityVillager);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private Rotation getRotation(Entity entity) {
      if (entity != null) {
         Vec3 vec3 = mc.field_71439_g.func_174824_e(1.0F);
         Vec3 vec31 = PlayerUtils.getVectorForRotation(mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A);
         Vec3 vec32 = vec3.func_72441_c(vec31.field_72450_a, vec31.field_72448_b, vec31.field_72449_c);
         return RotationUtils.getRotations(RotationUtils.getClosestPointInAABB(vec32, entity.func_174813_aQ()));
      } else {
         return null;
      }
   }
}
