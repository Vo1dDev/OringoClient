package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import java.util.HashMap;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiBot extends Module {
   private static AntiBot antiBot;
   private static final ModeSetting mode = new ModeSetting("Mode", "Hypixel", new String[]{"Hypixel"});
   private static final BooleanSetting ticksInvis = new BooleanSetting("Invis ticks check", true, (aBoolean) -> {
      return !mode.is("Hypixel");
   });
   private static final BooleanSetting tabTicks = new BooleanSetting("Tab ticks check", false, (aBoolean) -> {
      return !mode.is("Hypixel");
   });
   private static final BooleanSetting npcCheck = new BooleanSetting("NPC check", true, (aBoolean) -> {
      return !mode.is("Hypixel");
   });
   private static final HashMap<Integer, AntiBot.EntityData> entityData = new HashMap();

   public AntiBot() {
      super("Anti Bot", Module.Category.COMBAT);
      this.addSettings(new Setting[]{mode, ticksInvis, tabTicks, npcCheck});
   }

   public static AntiBot getAntiBot() {
      if (antiBot == null) {
         antiBot = new AntiBot();
      }

      return antiBot;
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      AntiBot.EntityData data = (AntiBot.EntityData)entityData.get(event.entity.func_145782_y());
      if (data == null) {
         entityData.put(event.entity.func_145782_y(), new AntiBot.EntityData(event.entity));
      } else {
         ((AntiBot.EntityData)entityData.get(event.entity.func_145782_y())).update();
      }

   }

   public static boolean isValidEntity(Entity entity) {
      if (antiBot.isToggled() && entity instanceof EntityPlayer && entity != mc.field_71439_g) {
         AntiBot.EntityData data = (AntiBot.EntityData)entityData.get(entity.func_145782_y());
         if (data != null && mode.is("Hypixel")) {
            if (tabTicks.isEnabled() && data.getTabTicks() < 150) {
               return false;
            }

            if (ticksInvis.isEnabled() && data.getTicksExisted() - data.getTicksInvisible() < 150) {
               return false;
            }

            return !npcCheck.isEnabled() || !SkyblockUtils.isNPC(entity);
         }
      }

      return true;
   }

   @SubscribeEvent
   public void onWorldJOin(WorldJoinEvent event) {
      entityData.clear();
   }

   private static class EntityData {
      private int ticksInvisible;
      private int tabTicks;
      private final Entity entity;

      public EntityData(Entity entity) {
         this.entity = entity;
         this.update();
      }

      public int getTabTicks() {
         return this.tabTicks;
      }

      public int getTicksInvisible() {
         return this.ticksInvisible;
      }

      public int getTicksExisted() {
         return this.entity.field_70173_aa;
      }

      public void update() {
         if (this.entity instanceof EntityPlayer && AntiBot.mc.func_147114_u() != null && AntiBot.mc.func_147114_u().func_175102_a(this.entity.func_110124_au()) != null) {
            ++this.tabTicks;
         }

         if (this.entity.func_82150_aj()) {
            ++this.ticksInvisible;
         }

      }
   }
}
