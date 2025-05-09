package me.oringo.oringoclient.qolfeatures.module.impl.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.mixins.item.ItemToolAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.EntityUtils;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class InvManager extends Module {
   public NumberSetting delay = new NumberSetting("Delay", 30.0D, 0.0D, 300.0D, 1.0D);
   public BooleanSetting dropTrash = new BooleanSetting("Drop trash", true);
   public BooleanSetting autoArmor = new BooleanSetting("Auto Armor", false);
   public BooleanSetting middleClick = new BooleanSetting("Middle click to drop", false);
   public ModeSetting trashItems = new ModeSetting("Trash items", "Skyblock", new String[]{"Skyblock", "Skywars", "Custom"});
   public ModeSetting mode = new ModeSetting("Mode", "Inv open", new String[]{"Inv open", "Always"});
   private MilliTimer delayTimer = new MilliTimer();
   private boolean wasPressed;
   public NumberSetting swordSlot = new NumberSetting("Sword slot", 0.0D, 0.0D, 9.0D, 1.0D);
   public NumberSetting blockSlot = new NumberSetting("Block slot", 0.0D, 0.0D, 9.0D, 1.0D);
   public NumberSetting gappleSlot = new NumberSetting("Gapple slot", 0.0D, 0.0D, 9.0D, 1.0D);
   public NumberSetting pickaxeSlot = new NumberSetting("Pickaxe slot", 0.0D, 0.0D, 9.0D, 1.0D);
   public NumberSetting axeSlot = new NumberSetting("Axe slot", 0.0D, 0.0D, 9.0D, 1.0D);
   public NumberSetting shovelSlot = new NumberSetting("Shovel slot", 0.0D, 0.0D, 9.0D, 1.0D);
   public NumberSetting bowSlot = new NumberSetting("Bow slot", 0.0D, 0.0D, 9.0D, 1.0D);
   private List<String> dropSkyblock = Arrays.asList("Training Weight", "Healing Potion", "Beating Heart", "Premium Flesh", "Mimic Fragment", "Enchanted Rotten Flesh", "Machine Gun Bow", "Enchanted Bone", "Defuse Kit", "Enchanted Ice", "Diamond Atom", "Silent Death", "Cutlass", "Soulstealer Bow", "Sniper Bow", "Optical Lens", "Tripwire Hook", "Button", "Carpet", "Lever", "Journal Entry", "Sign", "Zombie Commander", "Zombie Lord", "Skeleton Master, Skeleton Grunt, Skeleton Lord, Zombie Soldier", "Zombie Knight", "Heavy", "Super Heavy", "Undead", "Bouncy", "Skeletor", "Trap", "Inflatable Jerry");
   private List<String> dropSkywars = Arrays.asList("Egg", "Snowball", "Poison", "Lava", "Steak", "Enchanting", "Poison");
   public static List<String> dropCustom = new ArrayList();

   public InvManager() {
      super("Inventory Manager", 0, Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.mode, this.delay, this.dropTrash, this.trashItems, this.middleClick, this.autoArmor, this.swordSlot, this.pickaxeSlot, this.axeSlot, this.shovelSlot, this.blockSlot});
   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (this.isToggled() && (mc.field_71462_r instanceof GuiInventory || mc.field_71462_r == null && this.mode.is("Always") && KillAura.target == null) && !OringoClient.scaffold.isToggled()) {
         if (this.autoArmor.isEnabled()) {
            this.getBestArmor();
         }

         if (this.dropTrash.isEnabled()) {
            this.dropTrash();
         }

         if (this.swordSlot.getValue() != 0.0D) {
            this.getBestSword();
         }

         this.getBestTools();
         if (this.blockSlot.getValue() != 0.0D) {
            this.getBestBlock();
         }
      } else {
         this.delayTimer.reset();
      }

   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (event.packet instanceof C02PacketUseEntity || event.packet instanceof C08PacketPlayerBlockPlacement) {
         this.delayTimer.reset();
      }

   }

   private EntityPlayer getClosestPlayer(double distance) {
      Stream var10000 = mc.field_71441_e.field_73010_i.stream().filter((entityPlayer) -> {
         return entityPlayer != mc.field_71439_g && !EntityUtils.isTeam(entityPlayer) && !SkyblockUtils.isNPC(entityPlayer) && (double)mc.field_71439_g.func_70032_d(entityPlayer) < distance;
      });
      EntityPlayerSP var10001 = mc.field_71439_g;
      var10001.getClass();
      List<EntityPlayer> players = (List)var10000.sorted(Comparator.comparingDouble(var10001::func_70032_d)).collect(Collectors.toList());
      return !players.isEmpty() ? (EntityPlayer)players.get(0) : null;
   }

   @SubscribeEvent
   public void onTooltip(ItemTooltipEvent event) {
      if (Mouse.isButtonDown(2) && mc.field_71462_r instanceof GuiInventory && this.middleClick.isEnabled()) {
         if (!this.wasPressed) {
            this.wasPressed = true;
            String name = ChatFormatting.stripFormatting(event.itemStack.func_82833_r());
            if (dropCustom.contains(name)) {
               dropCustom.remove(name);
               Notifications.showNotification("Oringo Client", "Removed " + name + " from custom drop list", 2000);
            } else {
               dropCustom.add(name);
               Notifications.showNotification("Oringo Client", "Added " + ChatFormatting.AQUA + name + ChatFormatting.RESET + " to custom drop list", 2000);
            }

            save();
         }
      } else {
         this.wasPressed = false;
      }

   }

   public void dropTrash() {
      Iterator var1 = mc.field_71439_g.field_71069_bz.field_75151_b.iterator();

      while(true) {
         while(true) {
            Slot slot;
            do {
               do {
                  if (!var1.hasNext()) {
                     return;
                  }

                  slot = (Slot)var1.next();
               } while(!slot.func_75216_d());
            } while(!this.canInteract());

            if (this.trashItems.getSelected().equals("Custom")) {
               if (dropCustom.contains(ChatFormatting.stripFormatting(slot.func_75211_c().func_82833_r()))) {
                  this.drop(slot.field_75222_d);
               }
            } else if (this.trashItems.getSelected().equals("Skyblock") && this.dropSkyblock.stream().anyMatch((a) -> {
               return a.contains(ChatFormatting.stripFormatting(slot.func_75211_c().func_82833_r()));
            })) {
               this.drop(slot.field_75222_d);
            } else if (this.trashItems.getSelected().equals("Skywars") && this.dropSkywars.stream().anyMatch((a) -> {
               return a.contains(ChatFormatting.stripFormatting(slot.func_75211_c().func_82833_r()));
            })) {
               this.drop(slot.field_75222_d);
            }
         }
      }
   }

   public void getBestArmor() {
      int i;
      ItemStack stack;
      for(i = 5; i < 9; ++i) {
         if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d() && this.canInteract()) {
            stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
            if (!isBestArmor(stack, i)) {
               this.drop(i);
            }
         }
      }

      for(i = 9; i < 45; ++i) {
         if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d() && this.canInteract()) {
            stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
            if (stack.func_77973_b() instanceof ItemArmor) {
               if (isBestArmor(stack, i)) {
                  this.shiftClick(i);
               } else {
                  this.drop(i);
               }
            }
         }
      }

   }

   public static boolean isBestArmor(ItemStack armor, int slot) {
      if (!(armor.func_77973_b() instanceof ItemArmor)) {
         return false;
      } else {
         for(int i = 5; i < 45; ++i) {
            if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
               ItemStack is = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
               if (is.func_77973_b() instanceof ItemArmor && (getProtection(is) > getProtection(armor) && slot < 9 || slot >= 9 && getProtection(is) >= getProtection(armor) && slot != i) && ((ItemArmor)is.func_77973_b()).field_77881_a == ((ItemArmor)armor.func_77973_b()).field_77881_a) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static float getProtection(ItemStack stack) {
      float prot = 0.0F;
      if (stack.func_77973_b() instanceof ItemArmor) {
         ItemArmor armor = (ItemArmor)stack.func_77973_b();
         prot = (float)((double)prot + (double)armor.field_77879_b + (double)((100 - armor.field_77879_b) * EnchantmentHelper.func_77506_a(Enchantment.field_180310_c.field_77352_x, stack)) * 0.0075D);
         prot = (float)((double)prot + (double)EnchantmentHelper.func_77506_a(Enchantment.field_77327_f.field_77352_x, stack) / 100.0D);
         prot = (float)((double)prot + (double)EnchantmentHelper.func_77506_a(Enchantment.field_77329_d.field_77352_x, stack) / 100.0D);
         prot = (float)((double)prot + (double)EnchantmentHelper.func_77506_a(Enchantment.field_92091_k.field_77352_x, stack) / 100.0D);
         prot = (float)((double)prot + (double)EnchantmentHelper.func_77506_a(Enchantment.field_77347_r.field_77352_x, stack) / 50.0D);
         prot = (float)((double)prot + (double)EnchantmentHelper.func_77506_a(Enchantment.field_180308_g.field_77352_x, stack) / 100.0D);
         prot = (float)((double)prot + (double)stack.func_77958_k() / 1000.0D);
      }

      return prot;
   }

   public void getBestSword() {
      for(int i = 9; i < 45; ++i) {
         if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d() && this.canInteract()) {
            ItemStack stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
            if (stack.func_77973_b() instanceof ItemSword) {
               if (this.isBestSword(stack, i)) {
                  if (this.getHotbarID((int)this.swordSlot.getValue()) != i) {
                     this.numberClick(i, (int)this.swordSlot.getValue() - 1);
                  }
               } else {
                  this.drop(i);
               }
            }
         }
      }

   }

   public boolean isBestSword(ItemStack sword, int slot) {
      if (!(sword.func_77973_b() instanceof ItemSword)) {
         return false;
      } else {
         for(int i = 9; i < 45; ++i) {
            if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
               ItemStack is = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
               if (is.func_77973_b() instanceof ItemSword && (getToolDamage(is) > getToolDamage(sword) && slot == this.getHotbarID((int)this.swordSlot.getValue()) || slot != this.getHotbarID((int)this.swordSlot.getValue()) && getToolDamage(is) >= getToolDamage(sword) && slot != i)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public void getBestTools() {
      for(int i = 9; i < 45; ++i) {
         if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d() && this.canInteract()) {
            ItemStack stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
            if (stack.func_77973_b() instanceof ItemTool && this.getToolHotbarSlot(stack) != 0) {
               if (this.isBestTool(stack, i)) {
                  if (this.getHotbarID(this.getToolHotbarSlot(stack)) != i) {
                     this.numberClick(i, this.getToolHotbarSlot(stack) - 1);
                  }
               } else {
                  this.drop(i);
               }
            }
         }
      }

   }

   public boolean isBestTool(ItemStack tool, int slot) {
      if (!(tool.func_77973_b() instanceof ItemTool)) {
         return false;
      } else {
         for(int i = 9; i < 45; ++i) {
            if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
               ItemStack is = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
               if (this.getToolHotbarSlot(is) != 0) {
                  if (tool.func_77973_b() instanceof ItemAxe && is.func_77973_b() instanceof ItemAxe) {
                     if (getMaterial(is) > getMaterial(tool) && slot == this.getHotbarID((int)this.axeSlot.getValue()) || slot != this.getHotbarID((int)this.axeSlot.getValue()) && getToolDamage(is) >= getToolDamage(tool) && slot != i) {
                        return false;
                     }
                  } else if (tool.func_77973_b() instanceof ItemPickaxe && is.func_77973_b() instanceof ItemPickaxe) {
                     if (getMaterial(is) > getMaterial(tool) && slot == this.getHotbarID((int)this.pickaxeSlot.getValue()) || slot != this.getHotbarID((int)this.pickaxeSlot.getValue()) && getToolDamage(is) >= getToolDamage(tool) && slot != i) {
                        return false;
                     }
                  } else if (tool.func_77973_b() instanceof ItemSpade && is.func_77973_b() instanceof ItemSpade && (getMaterial(is) > getMaterial(tool) && slot == this.getHotbarID((int)this.shovelSlot.getValue()) || slot != this.getHotbarID((int)this.pickaxeSlot.getValue()) && getToolDamage(is) >= getToolDamage(tool) && slot != i)) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   public int getToolHotbarSlot(ItemStack tool) {
      if (tool != null && tool.func_77973_b() instanceof ItemTool) {
         String var2 = ((ItemToolAccessor)tool.func_77973_b()).getToolClass();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -903145309:
            if (var2.equals("shovel")) {
               var3 = 2;
            }
            break;
         case -578028723:
            if (var2.equals("pickaxe")) {
               var3 = 0;
            }
            break;
         case 97038:
            if (var2.equals("axe")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            return (int)this.pickaxeSlot.getValue();
         case 1:
            return (int)this.axeSlot.getValue();
         case 2:
            return (int)this.shovelSlot.getValue();
         default:
            return 0;
         }
      } else {
         return 0;
      }
   }

   public static float getMaterial(ItemStack item) {
      return item.func_77973_b() instanceof ItemTool ? (float)((double)((ItemTool)item.func_77973_b()).func_150913_i().func_77996_d() + (double)EnchantmentHelper.func_77506_a(Enchantment.field_77349_p.field_77352_x, item) * 0.75D) : 0.0F;
   }

   public void getBestBlock() {
      for(int i = 9; i < 45; ++i) {
         if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d() && this.canInteract()) {
            ItemStack stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
            if (stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).field_150939_a.func_149730_j() && this.isBestBlock(stack, i) && this.getHotbarID((int)this.blockSlot.getValue()) != i) {
               this.numberClick(i, (int)this.blockSlot.getValue() - 1);
            }
         }
      }

   }

   public boolean isBestBlock(ItemStack stack, int slot) {
      if (stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).field_150939_a.func_149730_j()) {
         for(int i = 9; i < 45; ++i) {
            if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
               ItemStack is = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
               if (is.func_77973_b() instanceof ItemBlock && ((ItemBlock)is.func_77973_b()).field_150939_a.func_149730_j() && is.field_77994_a >= stack.field_77994_a && slot != this.getHotbarID((int)this.blockSlot.getValue()) && slot != i) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public int getHotbarID(int hotbarNumber) {
      return hotbarNumber + 35;
   }

   public static int getBowDamage(ItemStack bow) {
      return EnchantmentHelper.func_77506_a(Enchantment.field_77345_t.field_77352_x, bow) + EnchantmentHelper.func_77506_a(Enchantment.field_77343_v.field_77352_x, bow) * 2;
   }

   public static float getToolDamage(ItemStack tool) {
      float damage = 0.0F;
      if (tool != null && (tool.func_77973_b() instanceof ItemTool || tool.func_77973_b() instanceof ItemSword)) {
         if (tool.func_77973_b() instanceof ItemSword) {
            damage += 4.0F;
            ++damage;
         } else if (tool.func_77973_b() instanceof ItemAxe) {
            damage += 3.0F;
         } else if (tool.func_77973_b() instanceof ItemPickaxe) {
            damage += 2.0F;
         } else if (tool.func_77973_b() instanceof ItemSpade) {
            ++damage;
         }

         damage += tool.func_77973_b() instanceof ItemTool ? ((ItemTool)tool.func_77973_b()).func_150913_i().func_78000_c() : ((ItemSword)tool.func_77973_b()).func_150931_i();
         damage = (float)((double)damage + 1.25D * (double)EnchantmentHelper.func_77506_a(Enchantment.field_180314_l.field_77352_x, tool));
         damage = (float)((double)damage + (double)EnchantmentHelper.func_77506_a(Enchantment.field_180314_l.field_77352_x, tool) * 0.5D);
      }

      return damage;
   }

   private boolean canInteract() {
      return this.delayTimer.hasTimePassed((long)this.delay.getValue());
   }

   private static void save() {
      try {
         DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("config/OringoClient/InventoryManager.cfg"));
         dataOutputStream.writeInt(dropCustom.size());
         Iterator var1 = dropCustom.iterator();

         while(var1.hasNext()) {
            String s = (String)var1.next();
            dataOutputStream.writeUTF(s);
         }

         dataOutputStream.close();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public void shiftClick(int slot) {
      this.delayTimer.reset();
      KillAura.DISABLE.reset();
      mc.field_71442_b.func_78753_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, 0, 1, mc.field_71439_g);
   }

   public void numberClick(int slot, int button) {
      this.delayTimer.reset();
      KillAura.DISABLE.reset();
      mc.field_71442_b.func_78753_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, button, 2, mc.field_71439_g);
   }

   public void drop(int slot) {
      this.delayTimer.reset();
      KillAura.DISABLE.reset();
      mc.field_71442_b.func_78753_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, 1, 4, mc.field_71439_g);
   }
}
