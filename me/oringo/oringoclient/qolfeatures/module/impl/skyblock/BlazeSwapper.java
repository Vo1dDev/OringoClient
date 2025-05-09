package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import java.util.List;
import me.oringo.oringoclient.events.PreAttackEvent;
import me.oringo.oringoclient.mixins.PlayerControllerAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PacketUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlazeSwapper extends Module {
   public static final BooleanSetting ghost = new BooleanSetting("Ghost", false);
   private static final String[] ashNames = new String[]{"Firedust", "Kindlebane", "Pyrochaos"};
   private static final String[] spiritNames = new String[]{"Mawdredge", "Twilight", "Deathripper"};
   private final MilliTimer delay = new MilliTimer();

   public BlazeSwapper() {
      super("Blaze Swapper", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{ghost});
   }

   @SubscribeEvent
   public void onAttack(PreAttackEvent event) {
      if (this.isToggled() && (event.entity instanceof EntitySkeleton && ((EntitySkeleton)event.entity).func_82202_m() == 1 || event.entity instanceof EntityBlaze || event.entity instanceof EntityPigZombie)) {
         List<EntityArmorStand> armorStands = mc.field_71441_e.func_175647_a(EntityArmorStand.class, event.entity.func_174813_aQ().func_72314_b(0.1D, 2.0D, 0.1D), (entity) -> {
            String text = entity.func_145748_c_().func_150260_c().toLowerCase();
            return text.contains("spirit") || text.contains("ashen") || text.contains("auric") || text.contains("crystal");
         });
         if (!armorStands.isEmpty()) {
            EntityArmorStand armorStand = (EntityArmorStand)armorStands.get(0);
            BlazeSwapper.Type type = this.getType(armorStand.func_145748_c_().func_150260_c());
            if (type != BlazeSwapper.Type.NONE) {
               int slot = getSlot(type);
               if (slot != -1) {
                  swap(slot);
                  if (this.delay.hasTimePassed(500L) && mc.field_71439_g.field_71071_by.func_70301_a(slot).func_77973_b() instanceof ItemSword && !((ItemSword)mc.field_71439_g.field_71071_by.func_70301_a(slot).func_77973_b()).func_150932_j().equals(type.material)) {
                     mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71071_by.func_70301_a(slot)));
                     this.delay.reset();
                  }
               }
            }
         }
      }

   }

   private BlazeSwapper.Type getType(String name) {
      BlazeSwapper.Type[] var2 = BlazeSwapper.Type.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BlazeSwapper.Type type = var2[var4];
         if (name.toLowerCase().contains(type.toString().toLowerCase())) {
            return type;
         }
      }

      return BlazeSwapper.Type.NONE;
   }

   private static void swap(int slot) {
      if (ghost.isEnabled()) {
         if (((PlayerControllerAccessor)mc.field_71442_b).getCurrentPlayerItem() != slot) {
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
            ((PlayerControllerAccessor)mc.field_71442_b).setCurrentPlayerItem(-1);
         }
      } else {
         PlayerUtils.swapToSlot(slot);
      }

   }

   private static int getSlot(BlazeSwapper.Type type) {
      return type == BlazeSwapper.Type.NONE ? -1 : PlayerUtils.getHotbar((stack) -> {
         boolean flag = false;
         String[] var3 = type != BlazeSwapper.Type.ASHEN && type != BlazeSwapper.Type.AURIC ? spiritNames : ashNames;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String name = var3[var5];
            if (stack.func_82833_r().contains(name)) {
               flag = true;
               break;
            }
         }

         return stack.func_77973_b() instanceof ItemSword && flag;
      });
   }

   public static enum Type {
      ASHEN("STONE"),
      AURIC("GOLD"),
      CRYSTAL("EMERALD"),
      SPIRIT("IRON"),
      NONE("NONE");

      public String material;

      private Type(String material) {
         this.material = material;
      }
   }
}
