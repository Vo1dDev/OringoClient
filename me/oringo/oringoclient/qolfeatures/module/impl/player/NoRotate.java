package me.oringo.oringoclient.qolfeatures.module.impl.player;

import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.mixins.NetPlayHandlerAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {
   public BooleanSetting keepMotion = new BooleanSetting("Keep motion", true);
   public BooleanSetting pitch = new BooleanSetting("0 pitch", false);

   public NoRotate() {
      super("No Rotate", 0, Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.keepMotion, this.pitch});
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = false
   )
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S08PacketPlayerPosLook && this.isToggled() && mc.field_71439_g != null && ((double)((S08PacketPlayerPosLook)event.packet).func_148930_g() != 0.0D || this.pitch.isEnabled())) {
         event.setCanceled(true);
         EntityPlayer entityplayer = mc.field_71439_g;
         double d0 = ((S08PacketPlayerPosLook)event.packet).func_148932_c();
         double d1 = ((S08PacketPlayerPosLook)event.packet).func_148928_d();
         double d2 = ((S08PacketPlayerPosLook)event.packet).func_148933_e();
         float f = ((S08PacketPlayerPosLook)event.packet).func_148931_f();
         float f1 = ((S08PacketPlayerPosLook)event.packet).func_148930_g();
         if (((S08PacketPlayerPosLook)event.packet).func_179834_f().contains(EnumFlags.X)) {
            d0 += entityplayer.field_70165_t;
         } else if (!this.keepMotion.isEnabled()) {
            entityplayer.field_70159_w = 0.0D;
         }

         if (((S08PacketPlayerPosLook)event.packet).func_179834_f().contains(EnumFlags.Y)) {
            d1 += entityplayer.field_70163_u;
         } else {
            entityplayer.field_70181_x = 0.0D;
         }

         if (((S08PacketPlayerPosLook)event.packet).func_179834_f().contains(EnumFlags.Z)) {
            d2 += entityplayer.field_70161_v;
         } else if (!this.keepMotion.isEnabled()) {
            entityplayer.field_70179_y = 0.0D;
         }

         if (((S08PacketPlayerPosLook)event.packet).func_179834_f().contains(EnumFlags.X_ROT)) {
            f1 += entityplayer.field_70125_A;
         }

         if (((S08PacketPlayerPosLook)event.packet).func_179834_f().contains(EnumFlags.Y_ROT)) {
            f += entityplayer.field_70177_z;
         }

         entityplayer.func_70107_b(d0, d1, d2);
         mc.func_147114_u().func_147298_b().func_179290_a(new C06PacketPlayerPosLook(entityplayer.field_70165_t, entityplayer.func_174813_aQ().field_72338_b, entityplayer.field_70161_v, f % 360.0F, f1 % 360.0F, false));
         if (!((NetPlayHandlerAccessor)mc.func_147114_u()).isDoneLoadingTerrain()) {
            mc.field_71439_g.field_70169_q = mc.field_71439_g.field_70165_t;
            mc.field_71439_g.field_70167_r = mc.field_71439_g.field_70163_u;
            mc.field_71439_g.field_70166_s = mc.field_71439_g.field_70161_v;
            mc.func_147108_a((GuiScreen)null);
            ((NetPlayHandlerAccessor)mc.func_147114_u()).setDoneLoadingTerrain(true);
         }
      }

   }
}
