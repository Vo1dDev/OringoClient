package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.oringo.oringoclient.events.BlockBoundsEvent;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.MoveEvent;
import me.oringo.oringoclient.events.PreAttackEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class DojoHelper extends Module {
   private int jumpStage;
   private int ticks;
   private static boolean inTenacity;
   private static boolean inMastery;
   private static final HashMap<Entity, Long> shot = new HashMap();
   public static final BooleanSetting hideZombies = new BooleanSetting("Hide bad zombies", true);
   public static final BooleanSetting swordSwap = new BooleanSetting("Auto sword swap", true);
   public static final BooleanSetting tenacity = new BooleanSetting("Tenacity float", true);
   public static final BooleanSetting masteryAimbot = new BooleanSetting("Mastery aimbot", true);
   public static final BooleanSetting wTap = new BooleanSetting("W tap", true, (a) -> {
      return !masteryAimbot.isEnabled();
   });
   public static final NumberSetting time = new NumberSetting("Time", 0.3D, 0.1D, 5.0D, 0.05D, (a) -> {
      return !masteryAimbot.isEnabled();
   });
   public static final NumberSetting bowCharge = new NumberSetting("Bow charge", 0.6D, 0.1D, 1.0D, 0.1D, (a) -> {
      return !masteryAimbot.isEnabled();
   });
   public static final ModeSetting color = new ModeSetting("Color", (a) -> {
      return !masteryAimbot.isEnabled();
   }, "Yellow", new String[]{"Red", "Yellow", "Green"});

   public DojoHelper() {
      super("Dojo Helper", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{hideZombies, swordSwap, tenacity, masteryAimbot, wTap, time, bowCharge, color});
   }

   @SubscribeEvent
   public void onPlayerUpdate(MotionUpdateEvent event) {
      if (this.isToggled()) {
         float f2;
         if (masteryAimbot.isEnabled() && inMastery && mc.field_71439_g.func_70694_bm() != null && mc.field_71439_g.func_70694_bm().func_77973_b() == Items.field_151031_f) {
            if (!mc.field_71439_g.func_71039_bw()) {
               mc.field_71442_b.func_78769_a(mc.field_71439_g, mc.field_71441_e, mc.field_71439_g.func_70694_bm());
            }

            KeyBinding.func_74510_a(mc.field_71474_y.field_74313_G.func_151463_i(), true);
            Pattern pattern = Pattern.compile("\\d:\\d\\d\\d");
            Entity target = null;
            double time = 100.0D;
            shot.entrySet().removeIf((entry) -> {
               return System.currentTimeMillis() - (Long)entry.getValue() > 5000L;
            });
            Iterator var6 = ((List)mc.field_71441_e.field_72996_f.stream().filter((e) -> {
               return e instanceof EntityArmorStand && getColor(e.func_70005_c_()) && !shot.containsKey(e) && pattern.matcher(e.func_70005_c_()).find();
            }).sorted(Comparator.comparingDouble((entityx) -> {
               return this.getPriority(ChatFormatting.stripFormatting(entityx.func_70005_c_()));
            })).collect(Collectors.toList())).iterator();
            if (var6.hasNext()) {
               Entity entity = (Entity)var6.next();
               Rotation rotation = RotationUtils.getRotations(entity.func_174791_d().func_72441_c(0.0D, 4.0D, 0.0D));
               target = entity;
               time = this.getPriority(ChatFormatting.stripFormatting(entity.func_70005_c_()));
               event.setRotation(rotation);
            }

            if (mc.field_71439_g.func_71039_bw() && mc.field_71439_g.func_71011_bu().func_77973_b() == Items.field_151031_f && target != null && !event.isPre()) {
               ItemBow bow = (ItemBow)mc.field_71439_g.func_71011_bu().func_77973_b();
               int i = bow.func_77626_a(mc.field_71439_g.func_71011_bu()) - mc.field_71439_g.func_71052_bv();
               f2 = (float)i / 20.0F;
               f2 = (f2 * f2 + f2 * 2.0F) / 3.0F;
               if ((double)f2 >= bowCharge.getValue() && time <= DojoHelper.time.getValue()) {
                  if (wTap.isEnabled()) {
                     mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, Action.STOP_SPRINTING));
                     mc.func_147114_u().func_147298_b().func_179290_a(new C0BPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
                  }

                  mc.field_71442_b.func_78766_c(mc.field_71439_g);
                  shot.put(target, System.currentTimeMillis());
               }
            }
         }

         if (event.isPre()) {
            if (tenacity.isEnabled() && inTenacity) {
               if (this.jumpStage == 0) {
                  event.setPitch(90.0F);
                  if (PlayerUtils.isLiquid(0.01F) && mc.field_71439_g.field_70122_E) {
                     MovingObjectPosition rayrace = PlayerUtils.rayTrace(0.0F, 90.0F, 4.5F);
                     if (rayrace != null) {
                        int held = mc.field_71439_g.field_71071_by.field_70461_c;
                        PlayerUtils.swapToSlot(8);
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
                        Vec3 hitVec = rayrace.field_72307_f;
                        BlockPos hitPos = rayrace.func_178782_a();
                        float f = (float)(hitVec.field_72450_a - (double)hitPos.func_177958_n());
                        float f1 = (float)(hitVec.field_72448_b - (double)hitPos.func_177956_o());
                        f2 = (float)(hitVec.field_72449_c - (double)hitPos.func_177952_p());
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(rayrace.func_178782_a(), rayrace.field_178784_b.func_176745_a(), mc.field_71439_g.func_70694_bm(), f, f1, f2));
                        mc.func_147114_u().func_147298_b().func_179290_a(new C0APacketAnimation());
                        PlayerUtils.swapToSlot(held);
                     }

                     mc.field_71439_g.func_70664_aZ();
                     this.jumpStage = 1;
                  }
               } else if (this.jumpStage == 1) {
                  if (PlayerUtils.isLiquid(0.5F) && mc.field_71439_g.field_70181_x < 0.0D) {
                     this.jumpStage = 2;
                  }
               } else if (this.jumpStage == 2) {
                  this.ticks %= 40;
                  ++this.ticks;
                  if (this.ticks == 40) {
                     event.setY(event.y - 0.20000000298023224D);
                  } else if (this.ticks == 39) {
                     event.setY(event.y - 0.10000000149011612D);
                  } else if (this.ticks == 38) {
                     event.setY(event.y - 0.07999999821186066D);
                     event.setX(event.x + 0.20000000298023224D);
                     event.setZ(event.z + 0.20000000298023224D);
                  }
               }
            } else {
               this.ticks = this.jumpStage = 0;
            }
         }
      }

   }

   private double getPriority(String name) {
      double timeLeft = 100000.0D;
      name = name.replaceAll(":", ".");
      timeLeft = Double.parseDouble(name);
      return timeLeft;
   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (this.isToggled() && tenacity.isEnabled() && inTenacity && this.jumpStage == 2) {
         event.stop();
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
      }

   }

   private static boolean getColor(String name) {
      if (color.is("Red")) {
         return name.startsWith("§c§l");
      } else if (color.is("Green")) {
         return name.startsWith("§a§l");
      } else {
         return color.is("Yellow") ? name.startsWith("§e§l") : false;
      }
   }

   @SubscribeEvent
   public void onBlockBounds(BlockBoundsEvent event) {
      if (event.block == Blocks.field_150353_l && inTenacity && this.isToggled() && tenacity.isEnabled()) {
         event.aabb = new AxisAlignedBB((double)event.pos.func_177958_n(), (double)event.pos.func_177956_o(), (double)event.pos.func_177952_p(), (double)(event.pos.func_177958_n() + 1), (double)(event.pos.func_177956_o() + 1), (double)(event.pos.func_177952_p() + 1));
      }

   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (this.isToggled()) {
         inTenacity = SkyblockUtils.hasLine("Challenge: Tenacity");
         inMastery = SkyblockUtils.hasLine("Challenge: Mastery");
         if (hideZombies.isEnabled() && mc.field_71439_g != null && mc.field_71441_e != null && mc.field_71439_g.func_96123_co() != null && SkyblockUtils.hasLine("Challenge: Force")) {
            Iterator var2 = (new ArrayList(mc.field_71441_e.field_72996_f)).iterator();

            while(var2.hasNext()) {
               Entity entity = (Entity)var2.next();
               if (entity instanceof EntityZombie && ((EntityZombie)entity).func_82169_q(3) != null && ((EntityZombie)entity).func_82169_q(3).func_77973_b() == Items.field_151024_Q) {
                  entity.field_70163_u = -100.0D;
                  entity.field_70137_T = -100.0D;
               }

               if (entity instanceof EntityArmorStand && entity.func_145748_c_().func_150260_c().startsWith("§c-")) {
                  entity.field_70163_u = -100.0D;
                  entity.field_70137_T = -100.0D;
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onLeftClick(PreAttackEvent event) {
      if (this.isToggled() && swordSwap.isEnabled()) {
         this.left(event.entity);
      }

   }

   public void left(Entity target) {
      if (SkyblockUtils.hasLine("Challenge: Discipline") && target instanceof EntityZombie && ((EntityZombie)target).func_82169_q(3) != null) {
         Item item = ((EntityZombie)target).func_82169_q(3).func_77973_b();
         if (Items.field_151024_Q.equals(item)) {
            this.pickItem((stack) -> {
               return stack.func_77973_b() == Items.field_151041_m;
            });
         } else if (Items.field_151169_ag.equals(item)) {
            this.pickItem((stack) -> {
               return stack.func_77973_b() == Items.field_151010_B;
            });
         } else if (Items.field_151161_ac.equals(item)) {
            this.pickItem((stack) -> {
               return stack.func_77973_b() == Items.field_151048_u;
            });
         } else if (Items.field_151028_Y.equals(item)) {
            this.pickItem((stack) -> {
               return stack.func_77973_b() == Items.field_151040_l;
            });
         }
      }

   }

   private void pickItem(Predicate<ItemStack> predicate) {
      int slot = PlayerUtils.getHotbar(predicate);
      if (slot != -1) {
         PlayerUtils.swapToSlot(slot);
      }

   }
}
