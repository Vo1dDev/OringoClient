package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MovementUtils;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeCam extends Module {
   private EntityOtherPlayerMP playerEntity;
   public NumberSetting speed = new NumberSetting("Speed", 3.0D, 0.1D, 5.0D, 0.1D);
   public BooleanSetting tracer = new BooleanSetting("Show tracer", false);

   public FreeCam() {
      super("FreeCam", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.speed, this.tracer});
   }

   public void onEnable() {
      if (mc.field_71441_e != null) {
         this.playerEntity = new EntityOtherPlayerMP(mc.field_71441_e, mc.field_71439_g.func_146103_bH());
         this.playerEntity.func_82149_j(mc.field_71439_g);
         this.playerEntity.field_70122_E = mc.field_71439_g.field_70122_E;
         mc.field_71441_e.func_73027_a(-2137, this.playerEntity);
      }

   }

   public void onDisable() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && this.playerEntity != null) {
         mc.field_71439_g.field_70145_X = false;
         mc.field_71439_g.func_70107_b(this.playerEntity.field_70165_t, this.playerEntity.field_70163_u, this.playerEntity.field_70161_v);
         mc.field_71441_e.func_73028_b(-2137);
         this.playerEntity = null;
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
      }
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      if (this.isToggled()) {
         mc.field_71439_g.field_70145_X = true;
         mc.field_71439_g.field_70143_R = 0.0F;
         mc.field_71439_g.field_70122_E = false;
         mc.field_71439_g.field_71075_bZ.field_75100_b = false;
         mc.field_71439_g.field_70181_x = 0.0D;
         if (!MovementUtils.isMoving()) {
            mc.field_71439_g.field_70179_y = 0.0D;
            mc.field_71439_g.field_70159_w = 0.0D;
         }

         double speed = this.speed.getValue() * 0.1D;
         mc.field_71439_g.field_70747_aH = (float)speed;
         EntityPlayerSP var10000;
         if (mc.field_71474_y.field_74314_A.func_151470_d()) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x += speed * 3.0D;
         }

         if (mc.field_71474_y.field_74311_E.func_151470_d()) {
            var10000 = mc.field_71439_g;
            var10000.field_70181_x -= speed * 3.0D;
         }
      }

   }

   @SubscribeEvent
   public void onRenderWorld(RenderWorldLastEvent event) {
      if (this.isToggled() && this.playerEntity != null && this.tracer.isEnabled()) {
         RenderUtils.tracerLine(this.playerEntity, event.partialTicks, 1.0F, OringoClient.clickGui.getColor());
      }

   }

   @SubscribeEvent
   public void onWorldChange(Load event) {
      if (this.isToggled()) {
         this.toggle();
      }

   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (this.isToggled() && event.packet instanceof C03PacketPlayer) {
         event.setCanceled(true);
      }

   }
}
