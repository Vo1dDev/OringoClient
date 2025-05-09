package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.BlockBoundsEvent;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.mixins.MinecraftAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.font.Fonts;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Phase extends Module {
   private int ticks;
   public NumberSetting timer = new NumberSetting("Timer", 1.0D, 0.1D, 1.0D, 0.1D);
   public ModeSetting activate = new ModeSetting("Activate", "on Key", new String[]{"Auto", "on Key", "Always"});
   public BooleanSetting clip = new BooleanSetting("Autoclip", true);
   public BooleanSetting barrierClip = new BooleanSetting("Barrier clip", true);
   public BooleanSetting floatTrolling = new BooleanSetting("Float", true);
   public boolean isPhasing;
   public boolean wasPressed;
   public boolean canPhase;
   private double lastY;

   public Phase() {
      super("Stair Phase", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.timer, this.clip, this.activate, this.barrierClip, this.floatTrolling});
   }

   public void onDisable() {
      this.isPhasing = false;
   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         --this.ticks;
         if (this.isToggled()) {
            if (this.isPhasing) {
               ((MinecraftAccessor)mc).getTimer().field_74278_d = (float)this.timer.getValue();
            }

            if (mc.field_71439_g.field_70122_E) {
               this.lastY = mc.field_71439_g.field_70163_u;
            }

            if (this.lastY == mc.field_71439_g.field_70163_u && this.floatTrolling.isEnabled() && this.isPhasing) {
               mc.field_71439_g.field_70181_x = 0.0D;
               mc.field_71439_g.field_70122_E = true;
            }

            this.canPhase = mc.field_71439_g.field_70122_E && mc.field_71439_g.field_70124_G && isValidBlock(mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)).func_177230_c());
            if (!this.isPhasing && (!this.isKeybind() || this.isPressed() && !this.wasPressed) && mc.field_71439_g.field_70122_E && mc.field_71439_g.field_70124_G && isValidBlock(mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)).func_177230_c())) {
               this.isPhasing = true;
               this.ticks = 8;
            } else if (this.isPhasing && (!isInsideBlock() && this.ticks < 0 || this.isPressed() && !this.wasPressed && this.isKeybind())) {
               mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
               this.isPhasing = false;
               ((MinecraftAccessor)mc).getTimer().field_74278_d = 1.0F;
            }
         }

         this.wasPressed = this.isPressed();
      }
   }

   @SubscribeEvent
   public void onBlockBounds(BlockBoundsEvent event) {
      if (mc.field_71439_g != null && this.isToggled()) {
         if (event.block instanceof BlockBarrier && this.barrierClip.isEnabled() && (event.aabb != null && event.aabb.field_72337_e > mc.field_71439_g.func_174813_aQ().field_72338_b || mc.field_71474_y.field_74311_E.func_151470_d())) {
            event.setCanceled(true);
         }

         if ((this.isPhasing || this.activate.is("Always")) && event.collidingEntity == mc.field_71439_g && (event.aabb != null && event.aabb.field_72337_e > mc.field_71439_g.func_174813_aQ().field_72338_b || mc.field_71474_y.field_74311_E.func_151470_d() || this.ticks == 7 && this.clip.isEnabled())) {
            event.setCanceled(true);
         }

      }
   }

   @SubscribeEvent
   public void onRender(Post event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null && this.isToggled()) {
         if (this.canPhase && this.activate.is("on Key") && event.type == ElementType.HOTBAR) {
            ScaledResolution resolution = new ScaledResolution(mc);
            Fonts.robotoMediumBold.drawSmoothCenteredStringWithShadow("Phase usage detected", (double)((float)resolution.func_78326_a() / 2.0F), (double)((float)resolution.func_78328_b() - (float)resolution.func_78328_b() / 4.5F), OringoClient.clickGui.getColor().getRGB());
         }

      }
   }

   public static boolean isInsideBlock() {
      for(int x = MathHelper.func_76128_c(mc.field_71439_g.func_174813_aQ().field_72340_a); x < MathHelper.func_76128_c(mc.field_71439_g.func_174813_aQ().field_72336_d) + 1; ++x) {
         for(int y = MathHelper.func_76128_c(mc.field_71439_g.func_174813_aQ().field_72338_b); y < MathHelper.func_76128_c(mc.field_71439_g.func_174813_aQ().field_72337_e) + 1; ++y) {
            for(int z = MathHelper.func_76128_c(mc.field_71439_g.func_174813_aQ().field_72339_c); z < MathHelper.func_76128_c(mc.field_71439_g.func_174813_aQ().field_72334_f) + 1; ++z) {
               Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
               if (block != null && !(block instanceof BlockAir)) {
                  AxisAlignedBB boundingBox = block.func_180640_a(mc.field_71441_e, new BlockPos(x, y, z), mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)));
                  if (boundingBox != null && mc.field_71439_g.func_174813_aQ().func_72326_a(boundingBox)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean isKeybind() {
      return this.activate.is("on Key");
   }

   private static boolean isValidBlock(Block block) {
      return block instanceof BlockStairs || block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockWall || block == Blocks.field_150438_bZ || block instanceof BlockSkull;
   }
}
