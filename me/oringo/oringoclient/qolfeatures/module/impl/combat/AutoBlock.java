package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoBlock extends Module {
   public ModeSetting mode = new ModeSetting("Mode", "Hypixel", new String[]{"Hypixel", "Vanilla"});
   public NumberSetting blockTime = new NumberSetting("Block time", 500.0D, 50.0D, 2000.0D, 50.0D);
   public BooleanSetting players = new BooleanSetting("Players", true);
   public BooleanSetting mobs = new BooleanSetting("Mobs", false);
   public BooleanSetting onDamage = new BooleanSetting("on Damage", true);
   public BooleanSetting noSlow = new BooleanSetting("No Slow", false);
   public MilliTimer blockTimer = new MilliTimer();
   private boolean isBlocking;

   public AutoBlock() {
      super("AutoBlock", Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.mode, this.blockTime, this.players, this.mobs, this.onDamage, this.noSlow});
   }

   @SubscribeEvent
   public void onAttacK(AttackEntityEvent event) {
      if (this.isToggled() && !OringoClient.killAura.isToggled()) {
         if (event.entityPlayer == mc.field_71439_g && (event.target instanceof EntityPlayer && this.players.isEnabled() || !(event.target instanceof EntityPlayer) && this.mobs.isEnabled()) || event.target == mc.field_71439_g && this.onDamage.isEnabled()) {
            this.blockTimer.reset();
            if (event.entityPlayer == mc.field_71439_g && (!MovementUtils.isMoving() || this.mode.is("Vanilla")) && this.isBlocking) {
               this.stopBlocking();
            }
         }

      }
   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent.Post event) {
      if (this.isToggled() && !OringoClient.killAura.isToggled()) {
         if (!this.blockTimer.hasTimePassed((long)this.blockTime.getValue())) {
            if ((!this.isBlocking || this.mode.is("Hypixel")) && this.canBlock()) {
               this.startBlocking();
            }
         } else if (this.isBlocking) {
            this.stopBlocking();
         }

      }
   }

   public boolean canBlock() {
      return mc.field_71439_g.func_70694_bm() != null && mc.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemSword;
   }

   private void startBlocking() {
      mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
      this.isBlocking = true;
   }

   private void stopBlocking() {
      if (this.isBlocking) {
         mc.func_147114_u().func_147298_b().func_179290_a(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, EnumFacing.DOWN));
         this.isBlocking = false;
      }

   }

   public boolean isBlocking() {
      return OringoClient.autoBlock.canBlock() && this.isBlocking && !this.noSlow.isEnabled();
   }
}
