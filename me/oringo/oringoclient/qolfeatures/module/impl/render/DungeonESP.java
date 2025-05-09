package me.oringo.oringoclient.qolfeatures.module.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.RenderLayersEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MobRenderUtils;
import me.oringo.oringoclient.utils.OutlineUtils;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.item.ItemBow;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonESP extends Module {
   public BooleanSetting bat = new BooleanSetting("Bat ESP", true);
   public BooleanSetting starred = new BooleanSetting("Starred ESP", true);
   public BooleanSetting enderman = new BooleanSetting("Show endermen", true);
   public BooleanSetting miniboss = new BooleanSetting("Miniboss ESP", true);
   public BooleanSetting bowWarning = new BooleanSetting("Bow warning", false);
   public ModeSetting mode = new ModeSetting("Mode", "2D", new String[]{"Outline", "2D", "Chams", "Box", "Tracers"});
   public NumberSetting opacity = new NumberSetting("Opacity", 255.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !DungeonESP.this.mode.is("Chams");
      }
   };
   private static final Color starredColor = new Color(245, 81, 66);
   private static final Color batColor = new Color(139, 69, 19);
   private static final Color saColor = new Color(75, 0, 130);
   private static final Color laColor = new Color(34, 139, 34);
   private static final Color aaColor = new Color(97, 226, 255);
   private HashMap<Entity, Color> starredMobs = new HashMap();
   private Entity lastRendered;

   public DungeonESP() {
      super("Dungeon ESP", 0, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.mode, this.opacity, this.bat, this.starred, this.enderman, this.miniboss});
   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent.Pre event) {
      if (mc.field_71439_g.field_70173_aa % 20 == 0 && SkyblockUtils.inDungeon) {
         this.starredMobs.clear();
         Iterator var2 = ((List)mc.field_71441_e.field_72996_f.stream().filter((entityx) -> {
            return entityx instanceof EntityLivingBase;
         }).collect(Collectors.toList())).iterator();

         while(true) {
            while(true) {
               Entity entity;
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  entity = (Entity)var2.next();
               } while(this.starredMobs.containsKey(entity));

               if (entity instanceof EntityBat && !entity.func_82150_aj() && this.bat.isEnabled()) {
                  this.starredMobs.put(entity, batColor);
               } else {
                  if (this.starred.isEnabled()) {
                     if (entity instanceof EntityEnderman && entity.func_70005_c_().equals("Dinnerbone")) {
                        entity.func_82142_c(false);
                        if (this.enderman.isEnabled()) {
                           this.starredMobs.put(entity, starredColor);
                        }
                        continue;
                     }

                     if (entity instanceof EntityArmorStand && entity.func_70005_c_().contains("✯")) {
                        List<Entity> possibleMobs = mc.field_71441_e.func_72839_b(entity, entity.func_174813_aQ().func_72314_b(0.1D, 3.0D, 0.1D));
                        if (!possibleMobs.isEmpty() && !SkyblockUtils.isMiniboss((Entity)possibleMobs.get(0)) && !this.starredMobs.containsKey(possibleMobs.get(0))) {
                           this.starredMobs.put(possibleMobs.get(0), starredColor);
                        }
                        continue;
                     }
                  }

                  if (this.miniboss.isEnabled() && entity instanceof EntityOtherPlayerMP && SkyblockUtils.isMiniboss(entity)) {
                     String var4 = entity.func_70005_c_();
                     byte var5 = -1;
                     switch(var4.hashCode()) {
                     case -662331259:
                        if (var4.equals("Shadow Assassin")) {
                           var5 = 1;
                        }
                        break;
                     case -658070465:
                        if (var4.equals("Diamond Guy")) {
                           var5 = 2;
                        }
                        break;
                     case 1317990878:
                        if (var4.equals("Lost Adventurer")) {
                           var5 = 0;
                        }
                     }

                     switch(var5) {
                     case 0:
                        this.starredMobs.put(entity, laColor);
                        break;
                     case 1:
                        entity.func_82142_c(false);
                        this.starredMobs.put(entity, saColor);
                        break;
                     case 2:
                        this.starredMobs.put(entity, aaColor);
                     }

                     if (this.bowWarning.isEnabled() && ((EntityOtherPlayerMP)entity).func_70694_bm() != null && ((EntityOtherPlayerMP)entity).func_70694_bm().func_77973_b() instanceof ItemBow) {
                        this.drawBowWarning();
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onRenderWorld(RenderWorldLastEvent event) {
      if (this.isToggled() && SkyblockUtils.inDungeon && (this.mode.is("2D") || this.mode.is("Box") || this.mode.is("Tracers"))) {
         this.starredMobs.forEach((entity, color) -> {
            String var4 = this.mode.getSelected();
            byte var5 = -1;
            switch(var4.hashCode()) {
            case 1618:
               if (var4.equals("2D")) {
                  var5 = 0;
               }
               break;
            case 66987:
               if (var4.equals("Box")) {
                  var5 = 1;
               }
               break;
            case 597252646:
               if (var4.equals("Tracers")) {
                  var5 = 2;
               }
            }

            switch(var5) {
            case 0:
               RenderUtils.draw2D(entity, event.partialTicks, 1.0F, color);
               break;
            case 1:
               RenderUtils.entityESPBox(entity, event.partialTicks, color);
               break;
            case 2:
               RenderUtils.tracerLine(entity, event.partialTicks, 1.5F, color);
            }

         });
      }
   }

   @SubscribeEvent
   public void onEntityRender(RenderLayersEvent event) {
      if (this.isToggled() && SkyblockUtils.inDungeon && this.mode.is("Outline")) {
         if (this.starredMobs.containsKey(event.entity)) {
            OutlineUtils.outlineESP(event, (Color)this.starredMobs.get(event.entity));
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onRenderPre(Pre<EntityLivingBase> event) {
      if (this.isToggled() && SkyblockUtils.inDungeon && this.mode.is("Chams")) {
         if (this.starredMobs.containsKey(event.entity)) {
            MobRenderUtils.setColor(RenderUtils.applyOpacity((Color)this.starredMobs.get(event.entity), (int)this.opacity.getValue()));
            RenderUtils.enableChams();
            this.lastRendered = event.entity;
         }

      }
   }

   @SubscribeEvent
   public void onRenderPost(net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
      if (this.lastRendered == event.entity) {
         this.lastRendered = null;
         RenderUtils.disableChams();
         MobRenderUtils.unsetColor();
      }

   }

   private void drawBowWarning() {
      mc.field_71456_v.func_175178_a((String)null, ChatFormatting.DARK_RED + "Bow", 0, 20, 0);
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      this.starredMobs.clear();
   }
}
