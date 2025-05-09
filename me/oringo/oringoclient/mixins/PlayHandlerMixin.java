package me.oringo.oringoclient.mixins;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.world.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {NetHandlerPlayClient.class},
   priority = 1
)
public abstract class PlayHandlerMixin {
   @Shadow
   private Minecraft field_147299_f;
   @Shadow
   private WorldClient field_147300_g;
   @Shadow
   private boolean field_147309_h;
   @Shadow
   @Final
   private NetworkManager field_147302_e;

   @Inject(
      method = {"handleExplosion"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void handleExplosion(S27PacketExplosion packetIn, CallbackInfo ci) {
      if (OringoClient.velocity.isToggled() || OringoClient.speed.isToggled()) {
         PacketThreadUtil.func_180031_a(packetIn, OringoClient.mc.func_147114_u(), this.field_147299_f);
         Explosion explosion = new Explosion(this.field_147299_f.field_71441_e, (Entity)null, packetIn.func_149148_f(), packetIn.func_149143_g(), packetIn.func_149145_h(), packetIn.func_149146_i(), packetIn.func_149150_j());
         explosion.func_77279_a(true);
         boolean shouldTakeKB = OringoClient.velocity.skyblockKB.isEnabled() && (Minecraft.func_71410_x().field_71439_g.func_180799_ab() || SkyblockUtils.getDisplayName(Minecraft.func_71410_x().field_71439_g.func_70694_bm()).contains("Bonzo's Staff") || SkyblockUtils.getDisplayName(Minecraft.func_71410_x().field_71439_g.func_70694_bm()).contains("Jerry-chine Gun"));
         if ((shouldTakeKB || OringoClient.velocity.hModifier.getValue() != 0.0D || OringoClient.velocity.vModifier.getValue() != 0.0D) && !OringoClient.speed.isToggled()) {
            EntityPlayerSP var10000 = this.field_147299_f.field_71439_g;
            var10000.field_70159_w += (double)packetIn.func_149149_c() * (shouldTakeKB ? 1.0D : OringoClient.velocity.hModifier.getValue());
            var10000 = this.field_147299_f.field_71439_g;
            var10000.field_70181_x += (double)packetIn.func_149144_d() * (shouldTakeKB ? 1.0D : OringoClient.velocity.vModifier.getValue());
            var10000 = this.field_147299_f.field_71439_g;
            var10000.field_70179_y += (double)packetIn.func_149147_e() * (shouldTakeKB ? 1.0D : OringoClient.velocity.hModifier.getValue());
         }

         ci.cancel();
      }

   }

   @Inject(
      method = {"handleEntityVelocity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void handleEntityVelocity(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
      if (OringoClient.velocity.isToggled() || OringoClient.speed.isToggled()) {
         PacketThreadUtil.func_180031_a(packetIn, OringoClient.mc.func_147114_u(), this.field_147299_f);
         Entity entity = this.field_147300_g.func_73045_a(packetIn.func_149412_c());
         if (entity != null) {
            if (!entity.equals(OringoClient.mc.field_71439_g)) {
               entity.func_70016_h((double)packetIn.func_149411_d() / 8000.0D, (double)packetIn.func_149410_e() / 8000.0D, (double)packetIn.func_149409_f() / 8000.0D);
            } else {
               boolean shouldTakeKB = OringoClient.velocity.skyblockKB.isEnabled() && (Minecraft.func_71410_x().field_71439_g.func_180799_ab() || SkyblockUtils.getDisplayName(Minecraft.func_71410_x().field_71439_g.func_70694_bm()).contains("Bonzo's Staff") || SkyblockUtils.getDisplayName(Minecraft.func_71410_x().field_71439_g.func_70694_bm()).contains("Jerry-chine Gun"));
               if ((shouldTakeKB || OringoClient.velocity.hModifier.getValue() != 0.0D || OringoClient.velocity.vModifier.getValue() != 0.0D) && !OringoClient.speed.isToggled()) {
                  entity.func_70016_h((double)packetIn.func_149411_d() * (shouldTakeKB ? 1.0D : OringoClient.velocity.hModifier.getValue()) / 8000.0D, (double)packetIn.func_149410_e() * (shouldTakeKB ? 1.0D : OringoClient.velocity.vModifier.getValue()) / 8000.0D, (double)packetIn.func_149409_f() * (shouldTakeKB ? 1.0D : OringoClient.velocity.hModifier.getValue()) / 8000.0D);
               }
            }
         }

         ci.cancel();
      }

   }
}
