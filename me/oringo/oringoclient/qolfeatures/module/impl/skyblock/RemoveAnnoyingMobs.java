package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import java.util.ArrayList;
import java.util.Iterator;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class RemoveAnnoyingMobs extends Module {
   private Entity golemEntity;
   public static ArrayList<Entity> seraphs = new ArrayList();
   public BooleanSetting hidePlayers = new BooleanSetting("Hide players", false);

   public RemoveAnnoyingMobs() {
      super("Remove Mobs", 0, Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.hidePlayers});
   }

   @SubscribeEvent
   public void onUpdate(LivingUpdateEvent event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (this.isToggled()) {
            if (event.entity instanceof EntityPlayer && !event.entity.equals(Minecraft.func_71410_x().field_71439_g) && this.golemEntity != null && !this.golemEntity.field_70128_L && this.golemEntity.func_70032_d(event.entity) < 9.0F) {
               event.entity.field_70163_u = 999999.0D;
               event.entity.field_70137_T = 999999.0D;
               event.setCanceled(true);
            }

            if (!(event.entity instanceof EntityArmorStand) && !(event.entity instanceof EntityEnderman) && !(event.entity instanceof EntityGuardian) && !(event.entity instanceof EntityFallingBlock) && !event.entity.equals(Minecraft.func_71410_x().field_71439_g)) {
               Iterator var2 = seraphs.iterator();

               while(var2.hasNext()) {
                  Entity seraph = (Entity)var2.next();
                  if (event.entity.func_70032_d(seraph) < 5.0F) {
                     event.entity.field_70163_u = 999999.0D;
                     event.entity.field_70137_T = 999999.0D;
                     event.setCanceled(true);
                  }
               }
            }

            if (event.entity instanceof EntityOtherPlayerMP && this.hidePlayers.isEnabled()) {
               event.entity.field_70163_u = 999999.0D;
               event.entity.field_70137_T = 999999.0D;
               event.setCanceled(true);
            }

            if (event.entity.func_145748_c_().func_150254_d().contains("Endstone Protector")) {
               this.golemEntity = event.entity;
            }

            if (event.entity instanceof EntityCreeper && event.entity.func_82150_aj() && ((EntityCreeper)event.entity).func_110143_aJ() == 20.0F) {
               mc.field_71441_e.func_72900_e(event.entity);
            }

            if (event.entity instanceof EntityCreeper && event.entity.func_82150_aj() && ((EntityCreeper)event.entity).func_110143_aJ() != 20.0F) {
               event.entity.func_82142_c(false);
            }
         }

      }
   }

   @SubscribeEvent
   public void onWorldRender(ClientTickEvent event) {
      seraphs.clear();
      if (this.isToggled() && mc.field_71441_e != null) {
         Iterator var2 = mc.field_71441_e.func_72910_y().iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            if (entity.func_145748_c_().func_150254_d().contains("Voidgloom Seraph")) {
               seraphs.add(entity);
            }

            if (entity instanceof EntityFireworkRocket) {
               mc.field_71441_e.func_72900_e(entity);
            }

            if (entity instanceof EntityHorse) {
               mc.field_71441_e.func_72900_e(entity);
            }
         }

      }
   }
}
