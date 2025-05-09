package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TerminalAura extends Module {
   public static ArrayList<Entity> finishedTerms = new ArrayList();
   public static EntityArmorStand currentTerminal;
   public static long termTime = -1L;
   public static long ping = 300L;
   public static long pingAt = -1L;
   public static boolean pinged;
   public BooleanSetting onGroud = new BooleanSetting("Only ground", true);
   public NumberSetting reach = new NumberSetting("Terminal Reach", 6.0D, 2.0D, 6.0D, 0.1D);

   public TerminalAura() {
      super("Terminal Aura", 0, Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.reach, this.onGroud});
   }

   @SubscribeEvent
   public void onTick(MotionUpdateEvent.Post event) {
      if (mc.field_71439_g != null && this.isToggled() && SkyblockUtils.inDungeon) {
         if (currentTerminal != null && !this.isInTerminal() && System.currentTimeMillis() - termTime > ping * 2L) {
            finishedTerms.add(currentTerminal);
            currentTerminal = null;
         }

         if (mc.field_71439_g.field_70173_aa % 20 == 0 && !pinged) {
            mc.func_147114_u().func_147298_b().func_179290_a(new C16PacketClientStatus(EnumState.REQUEST_STATS));
            pinged = true;
            pingAt = System.currentTimeMillis();
         }

         if (currentTerminal == null && (mc.field_71439_g.field_70122_E || !this.onGroud.isEnabled()) && !this.isInTerminal() && !mc.field_71439_g.func_180799_ab()) {
            Iterator var2 = this.getValidTerminals().iterator();
            if (var2.hasNext()) {
               Entity entity = (Entity)var2.next();
               this.openTerminal((EntityArmorStand)entity);
            }
         }

      }
   }

   @SubscribeEvent
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S2EPacketCloseWindow && this.isInTerminal() && currentTerminal != null) {
         this.openTerminal(currentTerminal);
      }

      if (event.packet instanceof S37PacketStatistics && pinged) {
         pinged = false;
         ping = System.currentTimeMillis() - pingAt;
      }

   }

   @SubscribeEvent
   public void onSent(PacketSentEvent.Post event) {
      if (event.packet instanceof C0DPacketCloseWindow && this.isInTerminal() && currentTerminal != null) {
         this.openTerminal(currentTerminal);
      }

   }

   @SubscribeEvent
   public void onWorldChange(Load event) {
      finishedTerms.clear();
      currentTerminal = null;
      pinged = false;
      termTime = System.currentTimeMillis();
      ping = 300L;
      pingAt = -1L;
   }

   private List<Entity> getValidTerminals() {
      Stream var10000 = mc.field_71441_e.func_72910_y().stream().filter((entity) -> {
         return entity instanceof EntityArmorStand;
      }).filter((entity) -> {
         return entity.func_70005_c_().contains("CLICK HERE");
      }).filter((entity) -> {
         return this.getDistance((EntityArmorStand)entity) < this.reach.getValue() - 0.4D;
      }).filter((entity) -> {
         return !finishedTerms.contains(entity);
      });
      EntityPlayerSP var10001 = mc.field_71439_g;
      var10001.getClass();
      return (List)var10000.sorted(Comparator.comparingDouble(var10001::func_70032_d)).collect(Collectors.toList());
   }

   private void openTerminal(EntityArmorStand entity) {
      mc.field_71442_b.func_78768_b(mc.field_71439_g, entity);
      currentTerminal = entity;
      termTime = System.currentTimeMillis();
   }

   private double getDistance(EntityArmorStand terminal) {
      return RotationUtils.getClosestPointInAABB(mc.field_71439_g.func_174824_e(1.0F), terminal.func_174813_aQ()).func_72438_d(mc.field_71439_g.func_174824_e(1.0F));
   }

   private boolean isInTerminal() {
      if (mc.field_71439_g == null) {
         return false;
      } else {
         Container container = mc.field_71439_g.field_71070_bA;
         String name = "";
         if (container instanceof ContainerChest) {
            name = ((ContainerChest)container).func_85151_d().func_70005_c_();
         }

         return container instanceof ContainerChest && (name.contains("Correct all the panes!") || name.contains("Navigate the maze!") || name.contains("Click in order!") || name.contains("What starts with:") || name.contains("Select all the") || name.contains("Change all to same color!") || name.contains("Click the button on time!"));
      }
   }
}
