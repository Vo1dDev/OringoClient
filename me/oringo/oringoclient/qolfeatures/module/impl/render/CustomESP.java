package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomESP extends Module {
   public static Map<String, Color> names = new HashMap();
   public ModeSetting mode = new ModeSetting("Mode", "2D", new String[]{"2D", "Box", "Tracers"});

   public CustomESP() {
      super("Custom ESP", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.mode});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (this.isToggled()) {
         Iterator var2 = mc.field_71441_e.func_175644_a(EntityArmorStand.class, (entityx) -> {
            return entityx.func_70068_e(mc.field_71439_g) < 10000.0D;
         }).iterator();

         while(true) {
            label48:
            while(var2.hasNext()) {
               Entity entity = (Entity)var2.next();
               Iterator var4 = names.entrySet().iterator();

               while(var4.hasNext()) {
                  Entry<String, Color> entry = (Entry)var4.next();
                  if (entity.func_145748_c_().func_150260_c().toLowerCase().contains((CharSequence)entry.getKey())) {
                     List<Entity> entities = mc.field_71441_e.func_72839_b(entity, entity.func_174813_aQ().func_72314_b(0.0D, 2.0D, 0.0D));
                     if (!entities.isEmpty()) {
                        Color color = (Color)entry.getValue();
                        Entity toRender = (Entity)entities.get(0);
                        String var9 = this.mode.getSelected();
                        byte var10 = -1;
                        switch(var9.hashCode()) {
                        case 1618:
                           if (var9.equals("2D")) {
                              var10 = 0;
                           }
                           break;
                        case 66987:
                           if (var9.equals("Box")) {
                              var10 = 1;
                           }
                           break;
                        case 597252646:
                           if (var9.equals("Tracers")) {
                              var10 = 2;
                           }
                        }

                        switch(var10) {
                        case 0:
                           RenderUtils.draw2D(toRender, event.partialTicks, 1.0F, color);
                           continue label48;
                        case 1:
                           RenderUtils.entityESPBox(toRender, event.partialTicks, color);
                           continue label48;
                        case 2:
                           RenderUtils.tracerLine(toRender, event.partialTicks, 1.0F, color);
                        }
                     }
                     break;
                  }
               }
            }

            return;
         }
      }
   }
}
