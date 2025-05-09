package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.MoveFlyingEvent;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Scaffold;
import me.oringo.oringoclient.qolfeatures.module.impl.player.AntiVoid;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.Aimbot;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.AntiNukebi;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.EntityUtils;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PacketUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
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
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Mouse;

public class KillAura extends Module {
   public static EntityLivingBase target;
   public BooleanSetting namesOnly = new BooleanSetting("Names only", false);
   public BooleanSetting middleClick = new BooleanSetting("Middle click to add", false);
   public BooleanSetting players = new BooleanSetting("Players", false);
   public BooleanSetting mobs = new BooleanSetting("Mobs", true);
   public BooleanSetting walls = new BooleanSetting("Through walls", true);
   public BooleanSetting teams = new BooleanSetting("Teams", true);
   public BooleanSetting toggleOnLoad = new BooleanSetting("Disable on join", true);
   public BooleanSetting toggleInGui = new BooleanSetting("No containers", true);
   public BooleanSetting onlySword = new BooleanSetting("Only swords", false);
   public BooleanSetting movementFix = new BooleanSetting("Movement fix", false);
   public BooleanSetting rotationSwing = new BooleanSetting("Swing on rotation", false);
   public BooleanSetting shovelSwap = new BooleanSetting("Shovel swap", false);
   public BooleanSetting attackOnly = new BooleanSetting("Click only", false);
   public BooleanSetting invisibles = new BooleanSetting("Invisibles", false);
   public ModeSetting sorting = new ModeSetting("Sorting", "Distance", new String[]{"Distance", "Health", "Hurt", "Hp reverse"});
   public ModeSetting rotationMode = new ModeSetting("Rotation mode", "Simple", new String[]{"Simple", "Smooth", "None"});
   public ModeSetting blockMode = new ModeSetting("Autoblock", "None", new String[]{"Vanilla", "Hypixel", "Fake", "None"});
   public ModeSetting namesonlyMode = new ModeSetting("Names mode", "Enemies", new String[]{"Friends", "Enemies"});
   public ModeSetting mode = new ModeSetting("Mode", "Single", new String[]{"Single", "Switch"});
   public NumberSetting range = new NumberSetting("Range", 4.2D, 2.0D, 6.0D, 0.1D) {
      public void setValue(double value) {
         super.setValue(value);
         if (this.getValue() > KillAura.this.rotationRange.getValue()) {
            this.setValue(KillAura.this.rotationRange.getValue());
         }

      }
   };
   public NumberSetting rotationRange = new NumberSetting("Rotation Range", 6.0D, 2.0D, 12.0D, 0.1D) {
      public void setValue(double value) {
         super.setValue(value);
         if (this.getValue() < KillAura.this.range.getValue()) {
            this.setValue(KillAura.this.range.getValue());
         }

      }
   };
   public NumberSetting fov = new NumberSetting("Fov", 360.0D, 30.0D, 360.0D, 1.0D);
   public NumberSetting maxRotation = new NumberSetting("Max rotation", 100.0D, 10.0D, 180.0D, 0.1D) {
      public boolean isHidden() {
         return !KillAura.this.rotationMode.is("Simple");
      }

      public void setValue(double value) {
         super.setValue(value);
         if (KillAura.this.minRotation.getValue() > this.getValue()) {
            this.setValue(KillAura.this.minRotation.getValue());
         }

      }
   };
   public NumberSetting minRotation = new NumberSetting("Min rotation", 60.0D, 5.0D, 180.0D, 0.1D) {
      public boolean isHidden() {
         return !KillAura.this.rotationMode.is("Simple");
      }

      public void setValue(double value) {
         super.setValue(value);
         if (this.getValue() > KillAura.this.maxRotation.getValue()) {
            this.setValue(KillAura.this.maxRotation.getValue());
         }

      }
   };
   public NumberSetting maxCps = new NumberSetting("Max CPS", 13.0D, 1.0D, 20.0D, 1.0D) {
      public void setValue(double value) {
         super.setValue(value);
         if (KillAura.this.minCps.getValue() > this.getValue()) {
            this.setValue(KillAura.this.minCps.getValue());
         }

      }
   };
   public NumberSetting minCps = new NumberSetting("Min CPS", 11.0D, 1.0D, 20.0D, 1.0D) {
      public void setValue(double value) {
         super.setValue(value);
         if (KillAura.this.maxCps.getValue() < this.getValue()) {
            this.setValue(KillAura.this.maxCps.getValue());
         }

      }
   };
   public NumberSetting smoothing = new NumberSetting("Smoothing", 12.0D, 1.0D, 20.0D, 0.1D) {
      public boolean isHidden() {
         return !KillAura.this.rotationMode.is("Smooth");
      }
   };
   public NumberSetting switchDelay = new NumberSetting("Switch delay", 100.0D, 0.0D, 250.0D, 1.0D, (aBoolean) -> {
      return !this.mode.is("Switch");
   });
   public static List<String> names = new ArrayList();
   private boolean wasDown;
   private boolean isBlocking;
   private int nextCps = 10;
   private int lastSlot = -1;
   private int targetIndex;
   private int attacks;
   private MilliTimer lastAttack = new MilliTimer();
   private MilliTimer switchDelayTimer = new MilliTimer();
   private MilliTimer blockDelay = new MilliTimer();
   public static final MilliTimer DISABLE = new MilliTimer();

   public KillAura() {
      super("Kill Aura", 0, Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.mode, this.switchDelay, this.range, this.rotationRange, this.minCps, this.maxCps, this.sorting, this.rotationMode, this.smoothing, this.maxRotation, this.minRotation, this.fov, this.blockMode, this.players, this.mobs, this.invisibles, this.teams, this.rotationSwing, this.movementFix, this.namesOnly, this.namesonlyMode, this.middleClick, this.attackOnly, this.walls, this.toggleInGui, this.toggleOnLoad, this.onlySword, this.shovelSwap});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null && this.middleClick.isEnabled()) {
         if (Mouse.isButtonDown(2) && mc.field_71462_r == null) {
            if (mc.field_147125_j != null && !this.wasDown && !(mc.field_147125_j instanceof EntityArmorStand) && mc.field_147125_j instanceof EntityLivingBase) {
               String name = ChatFormatting.stripFormatting(mc.field_147125_j.func_70005_c_());
               if (!names.contains(name)) {
                  names.add(name);
                  Notifications.showNotification("Oringo Client", "Added " + ChatFormatting.AQUA + name + ChatFormatting.RESET + " to name sorting", 2000);
               } else {
                  names.remove(name);
                  Notifications.showNotification("Oringo Client", "Removed " + ChatFormatting.AQUA + name + ChatFormatting.RESET + " from name sorting", 2000);
               }
            }

            this.wasDown = true;
         } else {
            this.wasDown = false;
         }

      }
   }

   public void onEnable() {
      this.attacks = 0;
   }

   public void onDisable() {
      target = null;
      this.isBlocking = false;
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public void onMovePre(MotionUpdateEvent.Pre event) {
      if (!AntiVoid.isBlinking() && (!OringoClient.scaffold.isToggled() || !Scaffold.disableAura.isEnabled()) && DISABLE.hasTimePassed(100L) && this.isToggled() && !Aimbot.attack && (!this.onlySword.isEnabled() || mc.field_71439_g.func_70694_bm() != null && mc.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemSword)) {
         target = this.getTarget();
         if (!this.attackOnly.isEnabled() || mc.field_71474_y.field_74312_F.func_151470_d()) {
            if (target != null) {
               Rotation angles = RotationUtils.getRotations(target, 0.2F);
               String var3;
               byte var4;
               if (!OringoClient.speed.isToggled()) {
                  var3 = this.rotationMode.getSelected();
                  var4 = -1;
                  switch(var3.hashCode()) {
                  case -1818419758:
                     if (var3.equals("Simple")) {
                        var4 = 2;
                     }
                     break;
                  case -1814666802:
                     if (var3.equals("Smooth")) {
                        var4 = 1;
                     }
                     break;
                  case 2433880:
                     if (var3.equals("None")) {
                        var4 = 0;
                     }
                  }

                  switch(var4) {
                  case 0:
                  default:
                     break;
                  case 1:
                     event.setRotation(RotationUtils.getSmoothRotation(RotationUtils.getLastReportedRotation(), angles, (float)this.smoothing.getValue()));
                     break;
                  case 2:
                     event.setRotation(RotationUtils.getLimitedRotation(RotationUtils.getLastReportedRotation(), angles, (float)(this.minRotation.getValue() + Math.abs(this.maxRotation.getValue() - this.minRotation.getValue()) * (double)(new Random()).nextFloat())));
                  }
               }

               event.setPitch(MathUtil.clamp(event.pitch, 90.0F, -90.0F));
               var3 = this.blockMode.getSelected();
               var4 = -1;
               switch(var3.hashCode()) {
               case -1248403467:
                  if (var3.equals("Hypixel")) {
                     var4 = 3;
                  }
                  break;
               case 2182005:
                  if (var3.equals("Fake")) {
                     var4 = 1;
                  }
                  break;
               case 2433880:
                  if (var3.equals("None")) {
                     var4 = 0;
                  }
                  break;
               case 1897755483:
                  if (var3.equals("Vanilla")) {
                     var4 = 2;
                  }
               }

               switch(var4) {
               case 0:
               case 1:
               case 2:
               case 3:
               }

               if (this.shovelSwap.isEnabled() && target instanceof EntityPlayer && this.hasDiamondArmor((EntityPlayer)target)) {
                  this.lastSlot = mc.field_71439_g.field_71071_by.field_70461_c;

                  for(int i = 0; i < 9; ++i) {
                     if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null && mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemSpade) {
                        PlayerUtils.swapToSlot(i);
                        this.isBlocking = false;
                        break;
                     }
                  }
               }
            }

         }
      } else {
         target = null;
      }
   }

   @SubscribeEvent
   public void onMoveFlying(MoveFlyingEvent event) {
      if (this.isToggled() && target != null && this.movementFix.isEnabled()) {
         event.setYaw(RotationUtils.getRotations(target).getYaw());
      }

   }

   private boolean hasDiamondArmor(EntityPlayer player) {
      for(int i = 1; i < 5; ++i) {
         if (player.func_71124_b(i) != null && player.func_71124_b(i).func_77973_b() instanceof ItemArmor && ((ItemArmor)player.func_71124_b(i).func_77973_b()).func_82812_d() == ArmorMaterial.DIAMOND) {
            return true;
         }
      }

      return false;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onMovePost(MotionUpdateEvent.Post event) {
      if (this.attackOnly.isEnabled() && !mc.field_71474_y.field_74312_F.func_151470_d()) {
         this.attacks = 0;
      } else {
         if (target != null && (double)mc.field_71439_g.func_70032_d(target) < Math.max(this.rotationRange.getValue(), this.range.getValue()) && this.attacks > 0) {
            String var2 = this.blockMode.getSelected();
            byte var3 = -1;
            switch(var2.hashCode()) {
            case -1248403467:
               if (var2.equals("Hypixel")) {
                  var3 = 2;
               }
               break;
            case 2182005:
               if (var2.equals("Fake")) {
                  var3 = 1;
               }
               break;
            case 2433880:
               if (var2.equals("None")) {
                  var3 = 0;
               }
               break;
            case 1897755483:
               if (var2.equals("Vanilla")) {
                  var3 = 3;
               }
            }

            switch(var3) {
            case 0:
            case 1:
            case 2:
            default:
               break;
            case 3:
               this.stopBlocking();
            }

            for(; this.attacks > 0; --this.attacks) {
               mc.field_71439_g.func_71038_i();
               if ((double)mc.field_71439_g.func_70032_d(target) < this.range.getValue() && (RotationUtils.getRotationDifference(RotationUtils.getRotations(target), RotationUtils.getLastReportedRotation()) < 15.0D || this.rotationMode.is("None") || OringoClient.speed.isToggled() || AntiNukebi.currentNukebi != null && AntiNukebi.attack.isEnabled())) {
                  mc.field_71442_b.func_78764_a(mc.field_71439_g, target);
                  if (this.switchDelayTimer.hasTimePassed((long)this.switchDelay.getValue())) {
                     ++this.targetIndex;
                     this.switchDelayTimer.reset();
                  }
               }
            }

            if (mc.field_71439_g.func_70694_bm() != null && mc.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemSword) {
               var2 = this.blockMode.getSelected();
               var3 = -1;
               switch(var2.hashCode()) {
               case -1248403467:
                  if (var2.equals("Hypixel")) {
                     var3 = 3;
                  }
                  break;
               case 2182005:
                  if (var2.equals("Fake")) {
                     var3 = 1;
                  }
                  break;
               case 2433880:
                  if (var2.equals("None")) {
                     var3 = 0;
                  }
                  break;
               case 1897755483:
                  if (var2.equals("Vanilla")) {
                     var3 = 2;
                  }
               }

               switch(var3) {
               case 0:
               case 1:
               default:
                  break;
               case 2:
                  if (!this.isBlocking) {
                     this.startBlocking();
                  }
                  break;
               case 3:
                  if (this.blockDelay.hasTimePassed(250L)) {
                     this.startBlocking();
                     mc.field_71439_g.field_71174_a.func_147297_a(new C0BPacketEntityAction(mc.field_71439_g, Action.STOP_SPRINTING));
                     mc.field_71439_g.field_71174_a.func_147297_a(new C0BPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
                     this.blockDelay.reset();
                  }
               }
            }
         } else {
            this.attacks = 0;
         }

         if (this.shovelSwap.isEnabled() && this.lastSlot != -1) {
            PlayerUtils.swapToSlot(this.lastSlot);
            this.lastSlot = -1;
         }

      }
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S08PacketPlayerPosLook) {
         DISABLE.reset();
      }

   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (this.isToggled() && target != null && this.lastAttack.hasTimePassed((long)(1000 / this.nextCps)) && (double)mc.field_71439_g.func_70032_d(target) < (this.rotationSwing.isEnabled() ? this.getRotationRange() : this.range.getValue())) {
         this.nextCps = (int)(this.minCps.getValue() + Math.abs(this.maxCps.getValue() - this.minCps.getValue()) * (double)(new Random()).nextFloat());
         this.lastAttack.reset();
         ++this.attacks;
      }

   }

   private EntityLivingBase getTarget() {
      if ((!(mc.field_71462_r instanceof GuiContainer) || !this.toggleInGui.isEnabled()) && mc.field_71441_e != null) {
         List<Entity> validTargets = (List)mc.field_71441_e.func_72910_y().stream().filter((entity) -> {
            return entity instanceof EntityLivingBase;
         }).filter((entity) -> {
            return this.isValid((EntityLivingBase)entity);
         }).sorted(Comparator.comparingDouble((e) -> {
            return (double)e.func_70032_d(mc.field_71439_g);
         })).collect(Collectors.toList());
         String var2 = this.sorting.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -2137395588:
            if (var2.equals("Health")) {
               var3 = 0;
            }
            break;
         case -800305174:
            if (var2.equals("Hp reverse")) {
               var3 = 2;
            }
            break;
         case 2261039:
            if (var2.equals("Hurt")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            validTargets.sort(Comparator.comparingDouble((e) -> {
               return (double)((EntityLivingBase)e).func_110143_aJ();
            }));
            break;
         case 1:
            validTargets.sort(Comparator.comparing((e) -> {
               return ((EntityLivingBase)e).field_70737_aN;
            }));
            break;
         case 2:
            validTargets.sort(Comparator.comparingDouble((e) -> {
               return (double)((EntityLivingBase)e).func_110143_aJ();
            }).reversed());
         }

         if (!validTargets.isEmpty()) {
            if (this.targetIndex >= validTargets.size()) {
               this.targetIndex = 0;
            }

            var2 = this.mode.getSelected();
            var3 = -1;
            switch(var2.hashCode()) {
            case -1818398616:
               if (var2.equals("Single")) {
                  var3 = 1;
               }
               break;
            case -1805606060:
               if (var2.equals("Switch")) {
                  var3 = 0;
               }
            }

            switch(var3) {
            case 0:
               return (EntityLivingBase)validTargets.get(this.targetIndex);
            case 1:
               return (EntityLivingBase)validTargets.get(0);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean isValid(EntityLivingBase entity) {
      if (entity != mc.field_71439_g && AntiBot.isValidEntity(entity) && (this.invisibles.isEnabled() || !entity.func_82150_aj()) && !(entity instanceof EntityArmorStand) && (mc.field_71439_g.func_70685_l(entity) || this.walls.isEnabled()) && !(entity.func_110143_aJ() <= 0.0F) && !((double)entity.func_70032_d(mc.field_71439_g) > (target != null && target != entity ? this.range.getValue() : Math.max(this.rotationRange.getValue(), this.range.getValue()))) && !(RotationUtils.getRotationDifference(RotationUtils.getRotations(entity), RotationUtils.getPlayerRotation()) > this.fov.getValue())) {
         if (this.namesOnly.isEnabled()) {
            boolean flag = names.contains(ChatFormatting.stripFormatting(entity.func_70005_c_()));
            if (this.namesonlyMode.is("Enemies") || flag) {
               return this.namesonlyMode.is("Enemies") && flag;
            }
         }

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

   private double getRotationRange() {
      return Math.max(this.rotationRange.getValue(), this.range.getValue());
   }

   private void startBlocking() {
      PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
      this.isBlocking = true;
   }

   private void stopBlocking() {
      if (this.isBlocking) {
         mc.func_147114_u().func_147298_b().func_179290_a(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, EnumFacing.DOWN));
         this.isBlocking = false;
      }

   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      if (this.isToggled() && this.toggleOnLoad.isEnabled()) {
         this.toggle();
      }

   }
}
