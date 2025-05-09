package me.oringo.oringoclient.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.mixins.PlayerControllerAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PlayerUtils {
   public static boolean lastGround;

   private PlayerUtils() {
   }

   public static void swapToSlot(int slot) {
      OringoClient.mc.field_71439_g.field_71071_by.field_70461_c = slot;
      syncHeldItem();
   }

   public static void numberClick(int slot, int button) {
      OringoClient.mc.field_71442_b.func_78753_a(OringoClient.mc.field_71439_g.field_71069_bz.field_75152_c, slot, button, 2, OringoClient.mc.field_71439_g);
   }

   public static void shiftClick(int slot) {
      OringoClient.mc.field_71442_b.func_78753_a(OringoClient.mc.field_71439_g.field_71069_bz.field_75152_c, slot, 0, 1, OringoClient.mc.field_71439_g);
   }

   public static void drop(int slot) {
      OringoClient.mc.field_71442_b.func_78753_a(OringoClient.mc.field_71439_g.field_71069_bz.field_75152_c, slot, 1, 4, OringoClient.mc.field_71439_g);
   }

   public static int getHotbar(Predicate<ItemStack> predicate) {
      for(int i = 0; i < 9; ++i) {
         if (OringoClient.mc.field_71439_g.field_71071_by.func_70301_a(i) != null && predicate.test(OringoClient.mc.field_71439_g.field_71071_by.func_70301_a(i))) {
            return i;
         }
      }

      return -1;
   }

   public static <T extends Item> int getHotbar(Class<T> clazz) {
      return getHotbar((stack) -> {
         return clazz.isAssignableFrom(stack.func_77973_b().getClass());
      });
   }

   public static int getItem(String name) {
      List<Slot> slots = new ArrayList(OringoClient.mc.field_71439_g.field_71069_bz.field_75151_b);
      Collections.reverse(slots);
      Iterator var2 = slots.iterator();

      Slot slot;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         slot = (Slot)var2.next();
      } while(!slot.func_75216_d() || !slot.func_75211_c().func_82833_r().toLowerCase().contains(name.toLowerCase()));

      return slot.field_75222_d;
   }

   public static int getItem(Predicate<ItemStack> predicate) {
      List<Slot> slots = new ArrayList(OringoClient.mc.field_71439_g.field_71069_bz.field_75151_b);
      Collections.reverse(slots);
      Iterator var2 = slots.iterator();

      Slot slot;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         slot = (Slot)var2.next();
      } while(!slot.func_75216_d() || !predicate.test(slot.func_75211_c()));

      return slot.field_75222_d;
   }

   public static <T extends Item> int getItem(Class<T> clazz) {
      List<Slot> slots = new ArrayList(OringoClient.mc.field_71439_g.field_71069_bz.field_75151_b);
      Collections.reverse(slots);
      Iterator var2 = slots.iterator();

      Slot slot;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         slot = (Slot)var2.next();
      } while(!slot.func_75216_d() || !clazz.isAssignableFrom(slot.func_75211_c().func_77973_b().getClass()));

      return slot.field_75222_d;
   }

   public static boolean isInsideBlock() {
      for(int x = MathHelper.func_76128_c(OringoClient.mc.field_71439_g.func_174813_aQ().field_72340_a); x < MathHelper.func_76128_c(OringoClient.mc.field_71439_g.func_174813_aQ().field_72336_d) + 1; ++x) {
         for(int y = MathHelper.func_76128_c(OringoClient.mc.field_71439_g.func_174813_aQ().field_72338_b); y < MathHelper.func_76128_c(OringoClient.mc.field_71439_g.func_174813_aQ().field_72337_e) + 1; ++y) {
            for(int z = MathHelper.func_76128_c(OringoClient.mc.field_71439_g.func_174813_aQ().field_72339_c); z < MathHelper.func_76128_c(OringoClient.mc.field_71439_g.func_174813_aQ().field_72334_f) + 1; ++z) {
               Block block = OringoClient.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
               if (block != null && !(block instanceof BlockAir)) {
                  AxisAlignedBB boundingBox = block.func_180640_a(OringoClient.mc.field_71441_e, new BlockPos(x, y, z), OringoClient.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)));
                  if (boundingBox != null && OringoClient.mc.field_71439_g.func_174813_aQ().func_72326_a(boundingBox)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static Vec3 getVectorForRotation(float yaw, float pitch) {
      float f = MathHelper.func_76134_b(-yaw * 0.017453292F - 3.1415927F);
      float f1 = MathHelper.func_76126_a(-yaw * 0.017453292F - 3.1415927F);
      float f2 = -MathHelper.func_76134_b(-pitch * 0.017453292F);
      float f3 = MathHelper.func_76126_a(-pitch * 0.017453292F);
      return new Vec3((double)(f1 * f2), (double)f3, (double)(f * f2));
   }

   public static void syncHeldItem() {
      int slot = OringoClient.mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot != ((PlayerControllerAccessor)OringoClient.mc.field_71442_b).getCurrentPlayerItem()) {
         ((PlayerControllerAccessor)OringoClient.mc.field_71442_b).setCurrentPlayerItem(slot);
         PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
      }

   }

   public static float getJumpMotion() {
      float motionY = 0.42F;
      if (OringoClient.mc.field_71439_g.func_70644_a(Potion.field_76430_j)) {
         motionY += (float)(OringoClient.mc.field_71439_g.func_70660_b(Potion.field_76430_j).func_76458_c() + 1) * 0.1F;
      }

      return motionY;
   }

   public static float getFriction(boolean onGround) {
      float f4 = 0.91F;
      if (onGround) {
         f4 = OringoClient.mc.field_71439_g.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(OringoClient.mc.field_71439_g.field_70165_t), MathHelper.func_76128_c(OringoClient.mc.field_71439_g.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(OringoClient.mc.field_71439_g.field_70161_v))).func_177230_c().field_149765_K * 0.91F;
      }

      float f = 0.16277136F / (f4 * f4 * f4);
      float f5;
      if (onGround) {
         f5 = OringoClient.mc.field_71439_g.func_70689_ay() * f;
      } else {
         f5 = OringoClient.mc.field_71439_g.field_70747_aH;
      }

      return f5;
   }

   public static boolean isOnGround(double height) {
      return !OringoClient.mc.field_71441_e.func_72945_a(OringoClient.mc.field_71439_g, OringoClient.mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, -height, 0.0D)).isEmpty();
   }

   public static Vec3 getInterpolatedPos(float partialTicks) {
      return new Vec3(interpolate(OringoClient.mc.field_71439_g.field_70169_q, OringoClient.mc.field_71439_g.field_70165_t, partialTicks), interpolate(OringoClient.mc.field_71439_g.field_70167_r, OringoClient.mc.field_71439_g.field_70163_u, partialTicks) + 0.1D, interpolate(OringoClient.mc.field_71439_g.field_70166_s, OringoClient.mc.field_71439_g.field_70161_v, partialTicks));
   }

   public static double interpolate(double prev, double newPos, float partialTicks) {
      return prev + (newPos - prev) * (double)partialTicks;
   }

   public static boolean isFall(float distance) {
      return isFall(distance, 0.0D, 0.0D);
   }

   public static boolean isFall(float distance, double xOffset, double zOffset) {
      BlockPos block = new BlockPos(OringoClient.mc.field_71439_g.field_70165_t, OringoClient.mc.field_71439_g.field_70163_u, OringoClient.mc.field_71439_g.field_70161_v);
      if (!OringoClient.mc.field_71441_e.func_175667_e(block)) {
         return false;
      } else {
         AxisAlignedBB player = OringoClient.mc.field_71439_g.func_174813_aQ().func_72317_d(xOffset, 0.0D, zOffset);
         return OringoClient.mc.field_71441_e.func_72945_a(OringoClient.mc.field_71439_g, new AxisAlignedBB(player.field_72340_a, player.field_72338_b - (double)distance, player.field_72339_c, player.field_72336_d, player.field_72337_e, player.field_72334_f)).isEmpty();
      }
   }

   public static boolean isLiquid(float distance) {
      return isLiquid(distance, 0.0D, 0.0D);
   }

   public static boolean isLiquid(float distance, double xOffset, double zOffset) {
      BlockPos block = new BlockPos(OringoClient.mc.field_71439_g.field_70165_t, OringoClient.mc.field_71439_g.field_70163_u, OringoClient.mc.field_71439_g.field_70161_v);
      if (!OringoClient.mc.field_71441_e.func_175667_e(block)) {
         return false;
      } else {
         AxisAlignedBB player = OringoClient.mc.field_71439_g.func_174813_aQ().func_72317_d(xOffset, 0.0D, zOffset);
         return OringoClient.mc.field_71441_e.func_72953_d(new AxisAlignedBB(player.field_72340_a, player.field_72338_b - (double)distance, player.field_72339_c, player.field_72336_d, player.field_72337_e, player.field_72334_f));
      }
   }

   public static boolean isOverVoid() {
      return isOverVoid(0.0D, 0.0D);
   }

   public static boolean isOverVoid(double xOffset, double zOffset) {
      BlockPos block = new BlockPos(OringoClient.mc.field_71439_g.field_70165_t, OringoClient.mc.field_71439_g.field_70163_u, OringoClient.mc.field_71439_g.field_70161_v);
      if (!OringoClient.mc.field_71441_e.func_175667_e(block)) {
         return false;
      } else {
         AxisAlignedBB player = OringoClient.mc.field_71439_g.func_174813_aQ().func_72317_d(xOffset, 0.0D, zOffset);
         return OringoClient.mc.field_71441_e.func_72945_a(OringoClient.mc.field_71439_g, new AxisAlignedBB(player.field_72340_a, 0.0D, player.field_72339_c, player.field_72336_d, player.field_72337_e, player.field_72334_f)).isEmpty();
      }
   }

   public static MovingObjectPosition rayTrace(float yaw, float pitch, float distance) {
      Vec3 vec3 = OringoClient.mc.field_71439_g.func_174824_e(1.0F);
      Vec3 vec31 = getVectorForRotation(yaw, pitch);
      Vec3 vec32 = vec3.func_72441_c(vec31.field_72450_a * (double)distance, vec31.field_72448_b * (double)distance, vec31.field_72449_c * (double)distance);
      return OringoClient.mc.field_71441_e.func_147447_a(vec3, vec32, false, true, true);
   }
}
