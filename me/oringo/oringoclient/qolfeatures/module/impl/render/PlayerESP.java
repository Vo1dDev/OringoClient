package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.awt.Color;
import java.util.Iterator;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.RenderLayersEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.AntiBot;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MobRenderUtils;
import me.oringo.oringoclient.utils.OutlineUtils;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerESP extends Module {
   public ModeSetting mode = new ModeSetting("Mode", "2D", new String[]{"Outline", "2D", "Chams", "Box", "Tracers"});
   public NumberSetting opacity = new NumberSetting("Opacity", 255.0D, 0.0D, 255.0D, 1.0D) {
      public boolean isHidden() {
         return !PlayerESP.this.mode.is("Chams");
      }
   };
   private EntityPlayer lastRendered;

   public PlayerESP() {
      super("PlayerESP", 0, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.mode, this.opacity});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (this.isToggled() && (this.mode.getSelected().equals("2D") || this.mode.getSelected().equals("Box") || this.mode.getSelected().equals("Tracers"))) {
         Color color = OringoClient.clickGui.getColor();
         Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

         while(var3.hasNext()) {
            EntityPlayer entityPlayer = (EntityPlayer)var3.next();
            if (this.isValidEntity(entityPlayer) && entityPlayer != mc.field_71439_g) {
               String var5 = this.mode.getSelected();
               byte var6 = -1;
               switch(var5.hashCode()) {
               case 1618:
                  if (var5.equals("2D")) {
                     var6 = 0;
                  }
                  break;
               case 66987:
                  if (var5.equals("Box")) {
                     var6 = 1;
                  }
                  break;
               case 597252646:
                  if (var5.equals("Tracers")) {
                     var6 = 2;
                  }
               }

               switch(var6) {
               case 0:
                  RenderUtils.draw2D(entityPlayer, event.partialTicks, 1.0F, color);
                  break;
               case 1:
                  RenderUtils.entityESPBox(entityPlayer, event.partialTicks, color);
                  break;
               case 2:
                  RenderUtils.tracerLine(entityPlayer, event.partialTicks, 1.0F, color);
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onRender(RenderLayersEvent event) {
      Color color = OringoClient.clickGui.getColor();
      if (this.isToggled() && event.entity instanceof EntityPlayer && this.isValidEntity((EntityPlayer)event.entity) && event.entity != mc.field_71439_g && this.mode.getSelected().equals("Outline")) {
         OutlineUtils.outlineESP(event, color);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onRenderLiving(Pre event) {
      if (this.lastRendered != null) {
         this.lastRendered = null;
         RenderUtils.disableChams();
         MobRenderUtils.unsetColor();
      }

      if (event.entity instanceof EntityOtherPlayerMP && this.mode.getSelected().equals("Chams") && this.isToggled()) {
         Color color = RenderUtils.applyOpacity(OringoClient.clickGui.getColor(event.entity.func_145782_y()), (int)this.opacity.getValue());
         RenderUtils.enableChams();
         MobRenderUtils.setColor(color);
         this.lastRendered = (EntityPlayer)event.entity;
      }
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onRenderLivingPost(net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre event) {
      if (event.entity == this.lastRendered) {
         this.lastRendered = null;
         RenderUtils.disableChams();
         MobRenderUtils.unsetColor();
      }

   }

   private boolean isValidEntity(EntityPlayer player) {
      return AntiBot.isValidEntity(player) && player.func_110143_aJ() > 0.0F && !player.field_70128_L;
   }
}
