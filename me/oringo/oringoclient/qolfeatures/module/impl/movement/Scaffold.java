package me.oringo.oringoclient.qolfeatures.module.impl.movement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MathUtil;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.MovementUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.TimerUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold extends Module {
   public static final NumberSetting distance = new NumberSetting("Range", 4.5D, 1.0D, 4.5D, 0.1D);
   public static final NumberSetting timer = new NumberSetting("Timer", 1.0D, 0.1D, 3.0D, 0.05D);
   public static final NumberSetting towerTimer = new NumberSetting("Tower timer", 1.0D, 0.1D, 3.0D, 0.05D);
   public static final NumberSetting maxDelay = new NumberSetting("Max delay", 1.0D, 0.0D, 4.0D, 1.0D) {
      public void setValue(double value) {
         super.setValue(value);
         if (this.getValue() < Scaffold.minDelay.getValue()) {
            this.setValue(Scaffold.minDelay.getValue());
         }

      }
   };
   public static final NumberSetting minDelay = new NumberSetting("Min delay", 1.0D, 0.0D, 4.0D, 1.0D) {
      public void setValue(double value) {
         super.setValue(value);
         if (this.getValue() > Scaffold.maxDelay.getValue()) {
            this.setValue(Scaffold.maxDelay.getValue());
         }

      }
   };
   public static final NumberSetting test = new NumberSetting("Test", 5.15D, 5.0D, 5.5D, 0.05D);
   public static final BooleanSetting safeWalk = new BooleanSetting("Safe walk", true);
   public static final BooleanSetting disableSpeed = new BooleanSetting("Disable speed", true);
   public static final BooleanSetting disableAura = new BooleanSetting("Disable aura", true);
   public static final BooleanSetting safe = new BooleanSetting("Safe", true);
   public static final ModeSetting tower = new ModeSetting("Tower", "None", new String[]{"None", "Hypixel"});
   public static final ModeSetting sprint = new ModeSetting("Sprint", "Semi", new String[]{"None", "Semi", "Sprint"});
   private int ticks;
   private MilliTimer timer1 = new MilliTimer();
   private MilliTimer slowdowntimer = new MilliTimer();
   private int blocksPlaced;
   boolean flag;

   public Scaffold() {
      super("Scaffold", Module.Category.MOVEMENT);
      this.addSettings(new Setting[]{distance, minDelay, maxDelay, timer, towerTimer, tower, sprint, safeWalk, disableSpeed, disableAura, safe});
   }

   public void onEnable() {
      if (mc.field_71439_g != null) {
         TimerUtil.setSpeed((float)timer.getValue());
         this.ticks = 0;
      }

   }

   public void onDisable() {
      if (mc.field_71439_g != null) {
         TimerUtil.setSpeed(1.0F);
         mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
      }

   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent event) {
      if (this.isToggled()) {
         int selectedSlot;
         MovingObjectPosition rayrace;
         if (event.isPre()) {
            event.setYaw(MovementUtils.getYaw() + 180.0F).setPitch(81.0F);
            this.flag = true;

            for(selectedSlot = 81; selectedSlot > 72; --selectedSlot) {
               rayrace = rayTrace(event.yaw, (float)selectedSlot);
               if (rayrace != null) {
                  this.flag = false;
                  event.setPitch((float)((double)selectedSlot + MathUtil.getRandomInRange(0.1D, -0.1D)));
                  break;
               }
            }

            if (this.flag && !safe.isEnabled()) {
               BlockPos pos = this.getClosestBlock();
               if (pos != null) {
                  Rotation rotation = RotationUtils.getRotations(RotationUtils.getClosestPointInAABB(mc.field_71439_g.func_174824_e(1.0F), mc.field_71441_e.func_180495_p(pos).func_177230_c().func_180646_a(mc.field_71441_e, pos)));
                  MovingObjectPosition position = rayTrace(rotation);
                  if (position != null) {
                     event.setRotation(rotation);
                  }
               }
            }

            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               TimerUtil.setSpeed((float)towerTimer.getValue());
               if (tower.is("Hypixel")) {
                  if (!mc.field_71439_g.func_70644_a(Potion.field_76430_j) && PlayerUtils.isOnGround(0.3D)) {
                     mc.field_71439_g.field_70181_x = 0.38999998569488525D;
                  }

                  mc.field_71439_g.func_70637_d(false);
               }
            } else {
               this.timer1.reset();
               this.slowdowntimer.reset();
               this.blocksPlaced = 0;
               TimerUtil.setSpeed((float)timer.getValue());
            }
         } else {
            selectedSlot = this.getBlock();
            if (selectedSlot == -1) {
               return;
            }

            mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(selectedSlot));
            if (this.ticks <= 0) {
               rayrace = rayTrace(event.getRotation());
               if (rayrace != null && rayrace.field_72313_a == MovingObjectType.BLOCK && mc.field_71441_e.func_180495_p(rayrace.func_178782_a()).func_177230_c().func_149730_j()) {
                  if (mc.field_71474_y.field_74314_A.func_151470_d() && tower.is("Hypixel")) {
                     if (!PlayerUtils.isInsideBlock()) {
                        this.placeBlock();
                     }
                  } else if (mc.field_71442_b.func_178890_a(mc.field_71439_g, mc.field_71441_e, mc.field_71439_g.field_71071_by.func_70301_a(selectedSlot), rayrace.func_178782_a(), rayrace.field_178784_b, rayrace.field_72307_f)) {
                     mc.field_71439_g.func_71038_i();
                  }

                  ++this.blocksPlaced;
                  if (!this.flag) {
                     this.ticks = (int)(minDelay.getValue() + (double)(new Random()).nextInt((int)(maxDelay.getValue() - minDelay.getValue() + 1.0D)));
                  } else {
                     this.ticks = Math.max(2, (int)(minDelay.getValue() + (double)(new Random()).nextInt((int)(maxDelay.getValue() - minDelay.getValue() + 1.0D))));
                  }

                  if (mc.field_71439_g.field_71071_by.func_70301_a(selectedSlot) != null && mc.field_71439_g.field_71071_by.func_70301_a(selectedSlot).field_77994_a <= 0) {
                     mc.field_71439_g.field_71071_by.func_70304_b(selectedSlot);
                  }
               }
            }

            --this.ticks;
         }
      }

   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
   }

   private int getBlock() {
      int current = -1;
      int stackSize = 0;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != null && stackSize < stack.field_77994_a && stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).field_150939_a.func_149730_j()) {
            stackSize = stack.field_77994_a;
            current = i;
         }
      }

      return current;
   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (this.isToggled() && !sprint.is("Sprint")) {
         double speed = sprint.is("Semi") ? 0.2575D : 0.225D;
         double x = MathUtil.clamp(event.getX(), speed, -speed);
         double z = MathUtil.clamp(event.getZ(), speed, -speed);
         event.setX(x).setZ(z);
      }

   }

   private BlockPos getClosestBlock() {
      ArrayList<Vec3> posList = new ArrayList();
      int range = (int)Math.ceil(distance.getValue());

      for(int x = -range; x <= range; ++x) {
         for(int y = -range + 2; y < 0; ++y) {
            for(int z = -range; z <= range; ++z) {
               Vec3 vec = (new Vec3((double)x, (double)y, (double)z)).func_72441_c(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
               BlockPos pos = new BlockPos(vec);
               if (mc.field_71441_e.func_180495_p(pos).func_177230_c().func_149730_j()) {
                  posList.add(vec);
               }
            }
         }
      }

      if (posList.isEmpty()) {
         return null;
      } else {
         posList.sort(Comparator.comparingDouble((posx) -> {
            return mc.field_71439_g.func_70011_f(posx.field_72450_a, posx.field_72448_b + 1.0D, posx.field_72449_c);
         }));
         return new BlockPos((Vec3)posList.get(0));
      }
   }

   private static MovingObjectPosition rayTrace(Rotation rotation) {
      return rayTrace(rotation.getYaw(), rotation.getPitch());
   }

   private static MovingObjectPosition rayTrace(float yaw, float pitch) {
      Vec3 vec3 = mc.field_71439_g.func_174824_e(1.0F);
      Vec3 vec31 = PlayerUtils.getVectorForRotation(yaw, pitch);
      Vec3 vec32 = vec3.func_72441_c(vec31.field_72450_a * (double)mc.field_71442_b.func_78757_d(), vec31.field_72448_b * (double)mc.field_71442_b.func_78757_d(), vec31.field_72449_c * (double)mc.field_71442_b.func_78757_d());
      return mc.field_71441_e.func_72933_a(vec3, vec32);
   }

   private void placeBlock() {
      MovingObjectPosition rayrace = rayTrace(0.0F, 90.0F);
      if (rayrace != null) {
         Vec3 hitVec = rayrace.field_72307_f;
         BlockPos hitPos = rayrace.func_178782_a();
         float f = (float)(hitVec.field_72450_a - (double)hitPos.func_177958_n());
         float f1 = (float)(hitVec.field_72448_b - (double)hitPos.func_177956_o());
         float f2 = (float)(hitVec.field_72449_c - (double)hitPos.func_177952_p());
         mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(rayrace.func_178782_a(), rayrace.field_178784_b.func_176745_a(), mc.field_71439_g.func_70694_bm(), f, f1, f2));
         mc.func_147114_u().func_147298_b().func_179290_a(new C0APacketAnimation());
         mc.field_71439_g.func_70694_bm().func_179546_a(mc.field_71439_g, mc.field_71441_e, hitPos, rayrace.field_178784_b, f, f1, f2);
      }

   }

   private static class BlockPlaceData {
      public final BlockPos pos;
      public final BlockPos targetPos;

      public BlockPlaceData(BlockPos pos, BlockPos targetPos) {
         this.pos = pos;
         this.targetPos = targetPos;
      }
   }
}
