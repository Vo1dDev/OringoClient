package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import me.oringo.oringoclient.utils.EntityUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TerminatorAura extends Module {
   public NumberSetting range = new NumberSetting("Range", 15.0D, 5.0D, 30.0D, 1.0D);
   public NumberSetting delay = new NumberSetting("Use delay", 3.0D, 1.0D, 10.0D, 1.0D);
   public ModeSetting mode = new ModeSetting("Mode", "Swap", new String[]{"Swap", "Held"});
   public ModeSetting button = new ModeSetting("Mouse", "Right", new String[]{"Left", "Right"});
   public BooleanSetting bossLock = new BooleanSetting("Boss Lock", true);
   public BooleanSetting inDungeon = new BooleanSetting("only Dungeon", true);
   public BooleanSetting teamCheck = new BooleanSetting("Teamcheck", false);
   public StringSetting customItem = new StringSetting("Custom Item");
   public static EntityLivingBase target;
   private static boolean attack;
   private static ArrayList<EntityLivingBase> attackedMobs = new ArrayList();

   public TerminatorAura() {
      super("Terminator Aura", 0, Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.delay, this.range, this.button, this.mode, this.customItem, this.bossLock, this.inDungeon, this.teamCheck});
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (KillAura.target == null && !Aimbot.attack && this.isToggled() && (double)mc.field_71439_g.field_70173_aa % this.delay.getValue() == 0.0D && (SkyblockUtils.inDungeon || !this.inDungeon.isEnabled())) {
         boolean hasTerm = mc.field_71439_g.func_70694_bm() != null && (mc.field_71439_g.func_70694_bm().func_82833_r().contains("Juju") || mc.field_71439_g.func_70694_bm().func_82833_r().contains("Terminator") || !this.customItem.getValue().equals("") && mc.field_71439_g.func_70694_bm().func_82833_r().contains(this.customItem.getValue()));
         if (this.mode.getSelected().equals("Swap")) {
            for(int i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null && (mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().contains("Juju") || mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().contains("Terminator") || !this.customItem.is("") && mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().contains(this.customItem.getValue()))) {
                  hasTerm = true;
                  break;
               }
            }
         }

         if (hasTerm) {
            target = this.getTarget(target);
            if (target != null) {
               attack = true;
               Rotation angles = RotationUtils.getBowRotation(target);
               event.yaw = angles.getYaw();
               event.pitch = angles.getPitch();
            }

         }
      }
   }

   @SubscribeEvent
   public void onUpdatePost(MotionUpdateEvent.Post event) {
      if (attack) {
         int held = mc.field_71439_g.field_71071_by.field_70461_c;
         if (this.mode.getSelected().equals("Swap")) {
            for(int i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null && (mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().contains("Juju") || mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().contains("Terminator") || !this.customItem.is("") && mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().contains(this.customItem.getValue()))) {
                  mc.field_71439_g.field_71071_by.field_70461_c = i;
                  break;
               }
            }
         }

         PlayerUtils.syncHeldItem();
         this.click();
         mc.field_71439_g.field_71071_by.field_70461_c = held;
         PlayerUtils.syncHeldItem();
         attack = false;
      }
   }

   private EntityLivingBase getTarget(EntityLivingBase lastTarget) {
      if (this.bossLock.isEnabled() && lastTarget != null && SkyblockUtils.isMiniboss(lastTarget) && lastTarget.func_110143_aJ() > 0.0F && !lastTarget.field_70128_L && lastTarget.func_70685_l(mc.field_71439_g) && (double)lastTarget.func_70032_d(mc.field_71439_g) < this.range.getValue()) {
         return lastTarget;
      } else {
         Stream var10000 = mc.field_71441_e.func_72910_y().stream().filter((entityx) -> {
            return entityx instanceof EntityLivingBase;
         }).filter((entityx) -> {
            return this.isValid((EntityLivingBase)entityx);
         });
         EntityPlayerSP var10001 = mc.field_71439_g;
         var10001.getClass();
         List<Entity> validTargets = (List)var10000.sorted(Comparator.comparingDouble(var10001::func_70032_d)).sorted(Comparator.comparing((entityx) -> {
            return RotationUtils.getRotationDifference(RotationUtils.getRotations(lastTarget != null ? lastTarget : (EntityLivingBase)entityx), RotationUtils.getRotations((EntityLivingBase)entityx));
         }).reversed()).collect(Collectors.toList());
         Iterator var3 = validTargets.iterator();
         if (var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            attackedMobs.add((EntityLivingBase)entity);
            (new Thread(() -> {
               try {
                  Thread.sleep(350L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
               }

               attackedMobs.remove(entity);
            })).start();
            return (EntityLivingBase)entity;
         } else {
            return null;
         }
      }
   }

   private void click() {
      String var1 = this.button.getSelected();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case 2364455:
         if (var1.equals("Left")) {
            var2 = 0;
         }
         break;
      case 78959100:
         if (var1.equals("Right")) {
            var2 = 1;
         }
      }

      switch(var2) {
      case 0:
         mc.field_71439_g.func_71038_i();
         break;
      case 1:
         mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
      }

   }

   private boolean isValid(EntityLivingBase entity) {
      if (entity != mc.field_71439_g && !(entity instanceof EntityArmorStand) && mc.field_71439_g.func_70685_l(entity) && !(entity.func_110143_aJ() <= 0.0F) && !((double)entity.func_70032_d(mc.field_71439_g) > this.range.getValue()) && (!(entity instanceof EntityPlayer) && !(entity instanceof EntityBat) && !(entity instanceof EntityZombie) && !(entity instanceof EntityGiantZombie) || !entity.func_82150_aj()) && !entity.func_70005_c_().equals("Dummy") && !entity.func_70005_c_().startsWith("Decoy")) {
         return !attackedMobs.contains(entity) && !(entity instanceof EntityBlaze) && (!EntityUtils.isTeam(entity) || !this.teamCheck.isEnabled());
      } else {
         return false;
      }
   }
}
