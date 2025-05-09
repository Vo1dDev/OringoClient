package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.BlockChangeEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AutoS1 extends Module {
   private boolean clicked;
   private boolean clickedButton;
   private static BlockPos clickPos;

   public AutoS1() {
      super("Auto SS", 0, Module.Category.SKYBLOCK);
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (mc.field_71439_g != null && SkyblockUtils.inDungeon && this.isToggled() && SkyblockUtils.inP3) {
         if (mc.field_71439_g.func_174824_e(0.0F).func_72438_d(new Vec3(309.0D, 121.0D, 290.0D)) < 5.5D && !this.clicked && mc.field_71441_e.func_180495_p(new BlockPos(309, 121, 290)).func_177230_c() == Blocks.field_150430_aB) {
            this.clickBlock(new BlockPos(309, 121, 290));
            this.clicked = true;
            this.clickedButton = false;
         }

         if (clickPos != null && mc.field_71439_g.func_70011_f((double)clickPos.func_177958_n(), (double)((float)clickPos.func_177956_o() - mc.field_71439_g.func_70047_e()), (double)clickPos.func_177952_p()) < 5.5D && !this.clickedButton && mc.field_71441_e.func_180495_p(clickPos).func_177230_c() == Blocks.field_150430_aB) {
            for(int i = 0; i < 20; ++i) {
               this.clickBlock(clickPos);
            }

            clickPos = null;
            this.clickedButton = true;
            OringoClient.sendMessageWithPrefix("Clicked!");
         }

      }
   }

   @SubscribeEvent
   public void onPacket(BlockChangeEvent event) {
      if (this.clicked && !this.clickedButton && SkyblockUtils.inP3 && event.state.func_177230_c() == Blocks.field_180398_cJ && event.pos.func_177958_n() == 310 && event.pos.func_177956_o() >= 120 && event.pos.func_177956_o() <= 123 && event.pos.func_177952_p() >= 291 && event.pos.func_177952_p() <= 294) {
         clickPos = new BlockPos(event.pos.func_177958_n() - 1, event.pos.func_177956_o(), event.pos.func_177952_p());
      }

   }

   @SubscribeEvent
   public void onWorldChange(Load event) {
      this.clicked = false;
      clickPos = null;
      this.clickedButton = false;
   }

   private void clickBlock(BlockPos hitPos) {
      Vec3 hitVec = new Vec3(0.0D, 0.0D, 0.0D);
      float f = (float)(hitVec.field_72450_a - (double)hitPos.func_177958_n());
      float f1 = (float)(hitVec.field_72448_b - (double)hitPos.func_177956_o());
      float f2 = (float)(hitVec.field_72449_c - (double)hitPos.func_177952_p());
      mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(hitPos, EnumFacing.func_176733_a((double)mc.field_71439_g.field_70177_z).func_176745_a(), mc.field_71439_g.field_71071_by.func_70448_g(), f, f1, f2));
   }
}
