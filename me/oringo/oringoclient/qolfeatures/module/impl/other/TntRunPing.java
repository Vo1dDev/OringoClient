package me.oringo.oringoclient.qolfeatures.module.impl.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.channel.ChannelHandlerContext;
import java.util.Arrays;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TntRunPing extends Module {
   NumberSetting ping = new NumberSetting("Ping", 2000.0D, 1.0D, 2000.0D, 1.0D);

   public TntRunPing() {
      super("TNT Run ping", 0, Module.Category.OTHER);
      this.addSettings(new Setting[]{this.ping});
   }

   @SubscribeEvent
   public void onPacket(PacketReceivedEvent event) {
      if (this.isToggled()) {
         try {
            ScoreObjective objective = mc.field_71439_g.func_96123_co().func_96539_a(1);
            if (!Arrays.asList("TNT RUN", "PVP RUN").contains(ChatFormatting.stripFormatting(objective.func_96678_d()))) {
               return;
            }
         } catch (Exception var6) {
            return;
         }

         if (event.packet instanceof S22PacketMultiBlockChange && ((S22PacketMultiBlockChange)event.packet).func_179844_a().length <= 10) {
            event.setCanceled(true);
            BlockUpdateData[] var7 = ((S22PacketMultiBlockChange)event.packet).func_179844_a();
            int var3 = var7.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               BlockUpdateData changedBlock = var7[var4];
               this.threadBreak(event.context, changedBlock.func_180090_a(), changedBlock.func_180088_c());
            }
         }

         if (event.packet instanceof S23PacketBlockChange) {
            if (OringoClient.stop.contains(((S23PacketBlockChange)event.packet).func_179827_b())) {
               event.setCanceled(true);
            }

            if (!Minecraft.func_71410_x().field_71441_e.func_180495_p(((S23PacketBlockChange)event.packet).func_179827_b()).func_177230_c().equals(Blocks.field_150325_L) && ((S23PacketBlockChange)event.packet).func_180728_a().func_177230_c().equals(Blocks.field_150350_a)) {
               event.setCanceled(true);
               this.threadBreak(event.context, ((S23PacketBlockChange)event.packet).func_179827_b(), ((S23PacketBlockChange)event.packet).func_180728_a());
            }
         }

      }
   }

   private void threadBreak(ChannelHandlerContext context, BlockPos pos, IBlockState state) {
      if (this.isToggled()) {
         Minecraft.func_71410_x().field_71441_e.func_175656_a(pos, Blocks.field_150325_L.func_176223_P());
         (new Thread(() -> {
            OringoClient.stop.add(pos);

            for(int i = 0; i < 10; ++i) {
               try {
                  Thread.sleep((long)((double)((long)this.ping.getValue()) / 10.0D));
               } catch (InterruptedException var6) {
                  var6.printStackTrace();
               }

               try {
                  mc.func_147114_u().func_147294_a(new S25PacketBlockBreakAnim(pos.hashCode(), pos, i));
               } catch (Exception var5) {
                  var5.printStackTrace();
               }
            }

            OringoClient.stop.remove(pos);
            Minecraft.func_71410_x().field_71441_e.func_175656_a(pos, state);
         })).start();
      }
   }
}
