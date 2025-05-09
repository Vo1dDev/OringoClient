package me.oringo.oringoclient.qolfeatures.module.impl.macro;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Mouse;

public class AOTVReturn extends Module {
   private StringSetting warp = new StringSetting("Warp command", "/warp forge");
   private StringSetting coords = new StringSetting("TP Coords", "0.5,167,-10.5;-23.5,180,-26.5;-64.5,212,-15.5;-33.5,244,-32.5");
   private BooleanSetting chat = new BooleanSetting("Open chat", true);
   private BooleanSetting middleClick = new BooleanSetting("Middle click", false);
   private NumberSetting delay = new NumberSetting("Delay", 2000.0D, 500.0D, 5000.0D, 1.0D);
   private ModeSetting mode = new ModeSetting("mode", "walk", new String[]{"jump", "walk"});
   private Thread instance = null;
   private Vec3 rotate = null;
   private boolean openChat = false;
   private boolean wasDown;
   private boolean isRunning;

   public AOTVReturn() {
      super("AOTV Return", Module.Category.OTHER);
      this.addSettings(new Setting[]{this.warp, this.mode, this.coords, this.chat, this.middleClick, this.delay});
   }

   public void onDisable() {
      this.isRunning = false;
      if (this.instance != null) {
         this.instance.stop();
      }

   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (this.openChat) {
         Minecraft.func_71410_x().func_147108_a(new GuiChat());
         this.openChat = false;
      }

      if (mc.field_71439_g != null && mc.field_71441_e != null && this.middleClick.isEnabled()) {
         if (Mouse.isButtonDown(2) && mc.field_71462_r == null) {
            if (!this.wasDown && mc.field_71476_x != null && mc.field_71476_x.field_72313_a == MovingObjectType.BLOCK) {
               BlockPos blockpos = mc.field_71476_x.func_178782_a();
               if (mc.field_71441_e.func_180495_p(blockpos).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  this.coords.setValue(this.coords.getValue() + (this.coords.getValue().length() > 0 ? ";" : "") + ((double)blockpos.func_177958_n() + 0.5D) + "," + ((double)blockpos.func_177956_o() + 0.5D) + "," + ((double)blockpos.func_177952_p() + 0.5D));
                  Notifications.showNotification("Oringo Client", "Added " + blockpos.func_177958_n() + " " + blockpos.func_177956_o() + " " + blockpos.func_177952_p() + " to coords!", 2500);
               }
            }

            this.wasDown = true;
         } else {
            this.wasDown = false;
         }

      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public void start(Runnable onFinish, boolean stop) {
      if (this.instance != null) {
         this.instance.stop();
      }

      this.isRunning = true;
      Minecraft mc = Minecraft.func_71410_x();
      (this.instance = new Thread(() -> {
         try {
            KeyBinding.func_74510_a(mc.field_71474_y.field_74312_F.func_151463_i(), false);
            mc.field_71439_g.func_71165_d("/l");
            Thread.sleep(5000L);
            mc.field_71439_g.func_71165_d("/skyblock");
            Thread.sleep(5000L);
            mc.field_71439_g.func_71165_d("/is");
            Thread.sleep((long)this.delay.getValue() * 3L);

            for(int ix = 0; ix < 9; ++ix) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(ix) != null && mc.field_71439_g.field_71071_by.func_70301_a(ix).func_82833_r().contains("Void")) {
                  mc.field_71439_g.field_71071_by.field_70461_c = ix;
                  break;
               }
            }

            mc.field_71439_g.func_71165_d(this.warp.getValue());
            Thread.sleep((long)this.delay.getValue() * 2L);
            if (OringoClient.mithrilMacro.drillnpc == null) {
               Iterator var11 = ((List)mc.field_71441_e.func_72910_y().stream().filter((entity) -> {
                  return entity instanceof EntityArmorStand;
               }).collect(Collectors.toList())).iterator();

               while(var11.hasNext()) {
                  Entity entityArmorStand = (Entity)var11.next();
                  if (entityArmorStand.func_145748_c_().func_150254_d().contains("§e§lDRILL MECHANIC§r")) {
                     OringoClient.mithrilMacro.drillnpc = (EntityArmorStand)entityArmorStand;
                     OringoClient.sendMessageWithPrefix("Mechanic");
                     break;
                  }
               }
            }

            String[] var12 = this.coords.getValue().split(";");
            int i = var12.length;

            for(int var6 = 0; var6 < i; ++var6) {
               String posx = var12[var6];
               if (OringoClient.mithrilMacro.drillnpc == null) {
                  Iterator var8 = ((List)mc.field_71441_e.func_72910_y().stream().filter((entity) -> {
                     return entity instanceof EntityArmorStand;
                  }).collect(Collectors.toList())).iterator();

                  while(var8.hasNext()) {
                     Entity entityArmorStandx = (Entity)var8.next();
                     if (entityArmorStandx.func_145748_c_().func_150254_d().contains("§e§lDRILL MECHANIC§r")) {
                        OringoClient.mithrilMacro.drillnpc = (EntityArmorStand)entityArmorStandx;
                        OringoClient.sendMessageWithPrefix("Mechanic");
                        break;
                     }
                  }
               }

               Thread.sleep((long)this.delay.getValue());
               String var15 = this.mode.getSelected();
               byte var17 = -1;
               switch(var15.hashCode()) {
               case 3273774:
                  if (var15.equals("jump")) {
                     var17 = 0;
                  }
                  break;
               case 3641801:
                  if (var15.equals("walk")) {
                     var17 = 1;
                  }
               }

               label91:
               switch(var17) {
               case 0:
                  if (mc.field_71439_g.field_70122_E) {
                     mc.field_71439_g.func_70664_aZ();
                  }

                  while(true) {
                     if (mc.field_71439_g.field_70122_E) {
                        break label91;
                     }

                     Thread.sleep(1L);
                  }
               case 1:
                  KeyBinding.func_74510_a(mc.field_71474_y.field_74366_z.func_151463_i(), true);
                  Thread.sleep(50L);
                  KeyBinding.func_74510_a(mc.field_71474_y.field_74366_z.func_151463_i(), false);
               }

               Thread.sleep((long)this.delay.getValue());
               this.rotate = new Vec3(Double.parseDouble(posx.split(",")[0]), Double.parseDouble(posx.split(",")[1]), Double.parseDouble(posx.split(",")[2]));
               Rotation rotation = RotationUtils.getRotations(this.rotate);
               mc.field_71439_g.field_70177_z = rotation.getYaw();
               mc.field_71439_g.field_70125_A = rotation.getPitch();
               Thread.sleep((long)this.delay.getValue());
               mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
               mc.field_71442_b.func_78769_a(mc.field_71439_g, mc.field_71441_e, mc.field_71439_g.func_70694_bm());
               mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
               this.rotate = null;
            }

            Thread.sleep((long)this.delay.getValue());
            String pos = this.coords.getValue().split(";")[this.coords.getValue().split(";").length - 1];

            for(i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null && mc.field_71439_g.field_71071_by.func_70301_a(i).func_82840_a(mc.field_71439_g, false).stream().anyMatch((line) -> {
                  return line.toLowerCase().contains("pickaxe") || line.toLowerCase().contains("gauntlet") || line.toLowerCase().contains("drill");
               })) {
                  mc.field_71439_g.field_71071_by.field_70461_c = i;
                  break;
               }
            }

            Thread.sleep((long)this.delay.getValue());
            if (mc.field_71439_g.func_70011_f(Double.parseDouble(pos.split(",")[0]), Double.parseDouble(pos.split(",")[1]), Double.parseDouble(pos.split(",")[2])) < 3.0D) {
               if (this.chat.isEnabled()) {
                  this.openChat = true;
               }

               if (onFinish != null) {
                  onFinish.run();
               }
            } else if (!stop) {
               (new Thread(() -> {
                  mc.field_71439_g.func_71165_d("/is");
                  this.start(onFinish, false);
               })).start();
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }

         this.isRunning = false;
      })).start();
   }
}
