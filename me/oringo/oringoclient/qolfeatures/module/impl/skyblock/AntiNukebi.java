package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiNukebi extends Module {
   public static final BooleanSetting attack = new BooleanSetting("Attack with aura", true);
   public static final BooleanSetting tracer = new BooleanSetting("Tracer", true);
   public static final NumberSetting timeOut = new NumberSetting("Timeout", 100.0D, 10.0D, 250.0D, 1.0D);
   public static final NumberSetting distance = new NumberSetting("Distance", 10.0D, 5.0D, 20.0D, 1.0D);
   private static final List<EntityArmorStand> attackedList = new ArrayList();
   public static EntityArmorStand currentNukebi;
   private final MilliTimer timeoutTimer = new MilliTimer();

   public AntiNukebi() {
      super("AntiNukekubi", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{distance, attack, timeOut, tracer});
   }

   private void reset() {
      currentNukebi = null;
      attackedList.clear();
   }

   public void onDisable() {
      this.reset();
   }

   @SubscribeEvent
   public void onWorldJoin(WorldJoinEvent event) {
      this.reset();
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onMotionUpdate(MotionUpdateEvent.Pre event) {
      if (this.isToggled()) {
         if (currentNukebi == null || this.timeoutTimer.hasTimePassed((long)(timeOut.getValue() * 50.0D)) || currentNukebi.field_70128_L || !currentNukebi.func_70685_l(mc.field_71439_g)) {
            currentNukebi = null;
            Iterator var2 = ((List)mc.field_71441_e.field_72996_f.stream().filter((entityx) -> {
               return entityx instanceof EntityArmorStand && isNukebi((EntityArmorStand)entityx) && !attackedList.contains(entityx) && (double)entityx.func_70032_d(mc.field_71439_g) < distance.getValue() && ((EntityArmorStand)entityx).func_70685_l(mc.field_71439_g) && Math.hypot(entityx.field_70165_t - entityx.field_70169_q, entityx.field_70161_v - entityx.field_70166_s) < 0.1D;
            }).collect(Collectors.toList())).iterator();
            if (var2.hasNext()) {
               Entity entity = (Entity)var2.next();
               EntityArmorStand armorStand = (EntityArmorStand)entity;
               currentNukebi = armorStand;
               this.timeoutTimer.reset();
               attackedList.add(armorStand);
            }
         }

         if (currentNukebi != null) {
            Rotation angle = RotationUtils.getRotations(currentNukebi.field_70165_t, currentNukebi.field_70163_u + 0.85D, currentNukebi.field_70161_v);
            event.setRotation(angle);
         }
      }

   }

   @SubscribeEvent
   public void onWorldRender(RenderWorldLastEvent event) {
      if (this.isToggled() && currentNukebi != null) {
         RenderUtils.tracerLine(currentNukebi, event.partialTicks, 1.0F, Color.white);
      }

   }

   public static boolean isNukebi(EntityArmorStand entity) {
      return entity.func_82169_q(3) != null && entity.func_82169_q(3).serializeNBT().func_74775_l("tag").func_74775_l("SkullOwner").func_74775_l("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWIwNzU5NGUyZGYyNzM5MjFhNzdjMTAxZDBiZmRmYTExMTVhYmVkNWI5YjIwMjllYjQ5NmNlYmE5YmRiYjRiMyJ9fX0=");
   }
}
