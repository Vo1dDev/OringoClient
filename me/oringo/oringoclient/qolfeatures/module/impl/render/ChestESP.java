package me.oringo.oringoclient.qolfeatures.module.impl.render;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.RenderChestEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestESP extends Module {
   public BooleanSetting tracer = new BooleanSetting("Tracer", true);
   private boolean hasRendered;

   public ChestESP() {
      super("ChestESP", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.tracer});
   }

   @SubscribeEvent
   public void onRender(RenderWorldLastEvent event) {
      if (this.isToggled() && this.tracer.isEnabled()) {
         Iterator var2 = ((List)mc.field_71441_e.field_147482_g.stream().filter((tileEntity) -> {
            return tileEntity instanceof TileEntityChest;
         }).collect(Collectors.toList())).iterator();

         while(var2.hasNext()) {
            TileEntity tileEntityChest = (TileEntity)var2.next();
            RenderUtils.tracerLine((double)tileEntityChest.func_174877_v().func_177958_n() + 0.5D, (double)tileEntityChest.func_174877_v().func_177956_o() + 0.5D, (double)tileEntityChest.func_174877_v().func_177952_p() + 0.5D, OringoClient.clickGui.getColor());
         }

      }
   }

   @SubscribeEvent
   public void onRenderChest(RenderChestEvent event) {
      if (this.isToggled()) {
         if (event.isPre() && event.getChest() == mc.field_71441_e.func_175625_s(event.getChest().func_174877_v())) {
            RenderUtils.enableChams();
            this.hasRendered = true;
         } else if (this.hasRendered) {
            RenderUtils.disableChams();
            this.hasRendered = false;
         }
      }

   }
}
