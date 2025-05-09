package me.oringo.oringoclient.qolfeatures.module.impl.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestStealer extends Module {
   private MilliTimer timer = new MilliTimer();
   public NumberSetting delay = new NumberSetting("Delay", 100.0D, 30.0D, 200.0D, 1.0D);
   public BooleanSetting close = new BooleanSetting("Auto close", true);
   public BooleanSetting nameCheck = new BooleanSetting("Name check", true);
   public BooleanSetting stealTrash = new BooleanSetting("Steal trash", false);

   public ChestStealer() {
      super("Chest stealer", 0, Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.delay, this.nameCheck, this.stealTrash, this.close});
   }

   @SubscribeEvent
   public void onGui(BackgroundDrawnEvent event) {
      if (event.gui instanceof GuiChest && this.isToggled()) {
         Container container = ((GuiChest)event.gui).field_147002_h;
         if (container instanceof ContainerChest && (!this.nameCheck.isEnabled() || ChatFormatting.stripFormatting(((ContainerChest)container).func_85151_d().func_145748_c_().func_150254_d()).equals("Chest") || ChatFormatting.stripFormatting(((ContainerChest)container).func_85151_d().func_145748_c_().func_150254_d()).equals("LOW"))) {
            int i;
            Item item;
            for(i = 0; i < ((ContainerChest)container).func_85151_d().func_70302_i_(); ++i) {
               if (container.func_75139_a(i).func_75216_d() && this.timer.hasTimePassed((long)this.delay.getValue())) {
                  item = container.func_75139_a(i).func_75211_c().func_77973_b();
                  if (this.stealTrash.isEnabled() || item instanceof ItemEnderPearl || item instanceof ItemTool || item instanceof ItemArmor || item instanceof ItemBow || item instanceof ItemPotion || item == Items.field_151032_g || item instanceof ItemAppleGold || item instanceof ItemSword || item instanceof ItemBlock) {
                     mc.field_71442_b.func_78753_a(container.field_75152_c, i, 0, 1, mc.field_71439_g);
                     this.timer.reset();
                     return;
                  }
               }
            }

            i = 0;

            while(true) {
               if (i >= ((ContainerChest)container).func_85151_d().func_70302_i_()) {
                  if (this.close.isEnabled()) {
                     mc.field_71439_g.func_71053_j();
                  }
                  break;
               }

               if (container.func_75139_a(i).func_75216_d()) {
                  item = container.func_75139_a(i).func_75211_c().func_77973_b();
                  if (this.stealTrash.isEnabled() || item instanceof ItemEnderPearl || item instanceof ItemTool || item instanceof ItemArmor || item instanceof ItemBow || item instanceof ItemPotion || item == Items.field_151032_g || item instanceof ItemAppleGold || item instanceof ItemSword || item instanceof ItemBlock) {
                     return;
                  }
               }

               ++i;
            }
         }
      }

   }
}
