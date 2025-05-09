package me.oringo.oringoclient.qolfeatures.module.impl.movement;

import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.events.MoveHeadingEvent;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.mixins.MinecraftAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.MovementUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.TimerUtil;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Flight extends Module {
   public static ModeSetting mode = new ModeSetting("Mode", "Vanilla", new String[]{"Hypixel Slime", "Vanilla", "Hypixel"});
   public static NumberSetting speed = new NumberSetting("Speed", 1.0D, 0.1D, 5.0D, 0.1D, (aBoolean) -> {
      return mode.is("Hypixel");
   });
   public static NumberSetting time = new NumberSetting("Disabler timer", 1200.0D, 250.0D, 2500.0D, 1.0D) {
      public boolean isHidden() {
         return !Flight.mode.is("Hypixel Slime");
      }
   };
   public static NumberSetting timerSpeed = new NumberSetting("Timer Speed", 1.0D, 0.1D, 3.0D, 0.1D);
   public static NumberSetting autoDisable = new NumberSetting("Auto disable", 1500.0D, 0.0D, 5000.0D, 50.0D) {
      public boolean isHidden() {
         return !Flight.mode.is("Vanilla");
      }
   };
   public static NumberSetting test = new NumberSetting("Test", 1.0D, 0.1D, 10.0D, 0.1D);
   public static BooleanSetting autoDisableHypixel = new BooleanSetting("Disable on flag", true, (aBoolean) -> {
      return !mode.is("Hypixel");
   });
   public static BooleanSetting timerBoost = new BooleanSetting("Timer boost", true, (aBoolean) -> {
      return !mode.is("Hypixel");
   });
   public MilliTimer disablerTimer = new MilliTimer();
   public MilliTimer autoDisableTimer = new MilliTimer();
   private boolean isFlying;
   private boolean placed;
   private double distance;
   private int flyingTicks;
   private int stage;
   private int ticks;

   public Flight() {
      super("Flight", 0, Module.Category.MOVEMENT);
      this.addSettings(new Setting[]{speed, mode, timerSpeed, time, test, autoDisableHypixel, autoDisable});
   }

   public void onDisable() {
      if (mode.is("Hypixel")) {
         if (this.distance > 4.0D) {
            Notifications.showNotification(String.format("Distance flown: %.1f", this.distance), 4000, Notifications.NotificationType.INFO);
         }

         if (mc.field_71439_g != null) {
            mc.field_71439_g.field_70159_w = 0.0D;
            mc.field_71439_g.field_70179_y = 0.0D;
         }
      } else if (mc.field_71439_g != null) {
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
      }

      this.isFlying = false;
      ((MinecraftAccessor)mc).getTimer().field_74278_d = 1.0F;
   }

   public void onEnable() {
      this.isFlying = this.placed = false;
      this.distance = (double)(this.flyingTicks = this.stage = this.ticks = 0);
      this.autoDisableTimer.reset();
      if (mode.is("Hypixel") && mc.field_71439_g != null) {
         if (!mc.field_71439_g.field_70122_E) {
            this.setToggled(false);
         } else {
            mc.field_71439_g.func_70664_aZ();
            mc.field_71439_g.field_70181_x = 0.41999998688697815D;
         }
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (this.isToggled()) {
         String var2 = mode.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1248403467:
            if (var2.equals("Hypixel")) {
               var3 = 0;
            }
            break;
         case 376026813:
            if (var2.equals("Hypixel Slime")) {
               var3 = 1;
            }
            break;
         case 1897755483:
            if (var2.equals("Vanilla")) {
               var3 = 2;
            }
         }

         switch(var3) {
         case 0:
            if (this.isFlying) {
               event.setY(0.0D);
               mc.field_71439_g.field_70181_x = 0.0D;
            }

            if (this.flyingTicks > 2) {
               event.setMotion(event.getX() * test.getValue(), event.getY(), event.getZ() * test.getValue());
            } else {
               event.setX(0.0D).setZ(0.0D);
            }
            break;
         case 1:
            if (mc.field_71439_g.field_71075_bZ.field_75101_c) {
               if (mc.field_71439_g.field_70173_aa % 6 == 0 || !this.isFlying || this.disablerTimer.hasTimePassed((long)time.getValue() - 150L)) {
                  PlayerCapabilities capabilities = new PlayerCapabilities();
                  capabilities.field_75101_c = true;
                  capabilities.field_75100_b = false;
                  mc.func_147114_u().func_147298_b().func_179290_a(new C13PacketPlayerAbilities(capabilities));
                  capabilities.field_75100_b = true;
                  mc.func_147114_u().func_147298_b().func_179290_a(new C13PacketPlayerAbilities(capabilities));
                  this.isFlying = true;
                  this.disablerTimer.reset();
               }
            } else if (this.disablerTimer.hasTimePassed((long)time.getValue())) {
               if (this.isFlying) {
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  this.isFlying = false;
                  ((MinecraftAccessor)mc).getTimer().field_74278_d = 1.0F;
               }
               break;
            }
         case 2:
            if (mode.is("Vanilla") && this.autoDisableTimer.hasTimePassed((long)autoDisable.getValue()) && autoDisable.getValue() != 0.0D) {
               this.setToggled(false);
               return;
            }

            TimerUtil.setSpeed((float)timerSpeed.getValue());
            event.setY(0.0D);
            MovementUtils.setMotion(event, speed.getValue());
            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               event.setY(speed.getValue());
            }

            if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               event.setY(speed.getValue() * -1.0D);
            }
         }
      }

   }

   @SubscribeEvent
   public void onMoveHeading(MoveHeadingEvent e) {
      if (this.isToggled() && mode.is("Hypixel") && this.isFlying) {
         e.setOnGround(true);
      }

   }

   @SubscribeEvent(
      receiveCanceled = true,
      priority = EventPriority.HIGHEST
   )
   public void onPacket(PacketReceivedEvent event) {
      if (this.isToggled() && mode.is("Hypixel") && event.packet instanceof S08PacketPlayerPosLook) {
         if (this.isFlying && this.flyingTicks >= 5) {
            if (autoDisableHypixel.isEnabled()) {
               this.setToggled(false);
            }
         } else {
            this.isFlying = true;
         }
      }

   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (this.isToggled()) {
         String var2 = mode.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1248403467:
            if (var2.equals("Hypixel")) {
               var3 = 0;
            }
         default:
            switch(var3) {
            case 0:
               if (!this.placed) {
                  event.setPitch(90.0F);
                  if (!PlayerUtils.isOnGround(1.0D)) {
                     this.placeBlock();
                     if (!this.placed) {
                        this.setToggled(false);
                     }

                     event.setOnGround(true);
                  }
               } else {
                  double timer = timerSpeed.getValue();
                  if (!mc.field_71439_g.func_70644_a(Potion.field_76424_c)) {
                     timer = 0.699999988079071D;
                  }

                  TimerUtil.setSpeed((float)timer);
                  if (!this.isFlying) {
                     ++this.stage;
                     event.setOnGround(false);
                     if (this.stage > 1 && this.stage < 5) {
                        event.setY(event.y - 0.2D);
                     }
                  } else {
                     ++this.flyingTicks;
                     this.distance += Math.hypot(mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q, mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s);
                  }
               }
            }
         }
      }

   }

   private void placeBlock() {
      int slot = -1;

      int prev;
      for(prev = 0; prev < 9; ++prev) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(prev);
         if (stack != null && stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).field_150939_a.func_149730_j()) {
            slot = prev;
            break;
         }
      }

      if (slot != -1) {
         prev = mc.field_71439_g.field_71071_by.field_70461_c;
         PlayerUtils.swapToSlot(slot);
         Vec3 vec3 = mc.field_71439_g.func_174824_e(1.0F);
         Vec3 vec31 = PlayerUtils.getVectorForRotation(0.0F, 90.0F);
         Vec3 vec32 = vec3.func_72441_c(vec31.field_72450_a * (double)mc.field_71442_b.func_78757_d(), vec31.field_72448_b * (double)mc.field_71442_b.func_78757_d(), vec31.field_72449_c * (double)mc.field_71442_b.func_78757_d());
         MovingObjectPosition rayrace = mc.field_71441_e.func_147447_a(vec3, vec32, false, true, true);
         if (rayrace != null) {
            Vec3 hitVec = rayrace.field_72307_f;
            BlockPos hitPos = rayrace.func_178782_a();
            float f = (float)(hitVec.field_72450_a - (double)hitPos.func_177958_n());
            float f1 = (float)(hitVec.field_72448_b - (double)hitPos.func_177956_o());
            float f2 = (float)(hitVec.field_72449_c - (double)hitPos.func_177952_p());
            mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(rayrace.func_178782_a(), rayrace.field_178784_b.func_176745_a(), mc.field_71439_g.func_70694_bm(), f, f1, f2));
            mc.func_147114_u().func_147298_b().func_179290_a(new C0APacketAnimation());
            mc.field_71439_g.func_70694_bm().func_179546_a(mc.field_71439_g, mc.field_71441_e, hitPos, rayrace.field_178784_b, f, f1, f2);
            this.placed = true;
         }

         PlayerUtils.swapToSlot(prev);
      } else {
         Notifications.showNotification("Oringo client", "No blocks found", 2000);
      }

   }

   public boolean isFlying() {
      return this.isToggled() && (!mode.is("Hypixel Slime") || !this.disablerTimer.hasTimePassed((long)time.getValue()));
   }
}
