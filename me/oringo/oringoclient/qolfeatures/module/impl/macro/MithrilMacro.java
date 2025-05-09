package me.oringo.oringoclient.qolfeatures.module.impl.macro;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.EntityUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class MithrilMacro extends Module {
   private Minecraft mc = Minecraft.func_71410_x();
   private BlockPos target = null;
   private BlockPos test = null;
   private Vec3 targetRotation = null;
   private Vec3 targetRotation2 = null;
   private ArrayList<Float> yaw = new ArrayList();
   private ArrayList<Float> pitch = new ArrayList();
   private boolean stopLoop = false;
   private int ticksTargeting = 0;
   private int ticksMining = 0;
   private int ticks = 0;
   private int ticksSeen = 0;
   private int shouldReconnect = -1;
   public EntityArmorStand drillnpc;
   private int lastKey = -1;
   private int timeLeft = 0;
   private int pause = 0;
   private BooleanSetting drillRefuel = new BooleanSetting("Drill Refuel", false);
   private NumberSetting rotations = new NumberSetting("Rotations", 10.0D, 1.0D, 20.0D, 1.0D);
   private NumberSetting accuracyChecks = new NumberSetting("Accuracy", 5.0D, 3.0D, 10.0D, 1.0D);
   private NumberSetting maxBreakTime = new NumberSetting("Max break time", 160.0D, 40.0D, 400.0D, 1.0D);
   private NumberSetting quickBreak = new NumberSetting("Block skip progress", 0.9D, 0.0D, 1.0D, 0.1D);
   private NumberSetting panic = new NumberSetting("Auto leave", 100.0D, 0.0D, 200.0D, 1.0D);
   private BooleanSetting titanium = new BooleanSetting("Prioritize titanium", true);
   private BooleanSetting sneak = new BooleanSetting("Sneak", false);
   private BooleanSetting under = new BooleanSetting("Mine under", false);
   private BooleanSetting autoAbility = new BooleanSetting("Auto ability", true);
   private NumberSetting moreMovement = new NumberSetting("Head movements", 5.0D, 0.0D, 50.0D, 1.0D);
   private NumberSetting walking = new NumberSetting("Walking %", 0.1D, 0.0D, 5.0D, 0.1D);
   private NumberSetting walkingTime = new NumberSetting("Walking ticks", 5.0D, 0.0D, 60.0D, 1.0D);
   private ModeSetting mode = new ModeSetting("Target", "Clay", new String[]{"Clay", "Prismarine", "Wool", "Blue", "Gold"});

   public MithrilMacro() {
      super("Mithril Macro", 0, Module.Category.OTHER);
      this.addSettings(new Setting[]{this.rotations, this.drillRefuel, this.accuracyChecks, this.titanium, this.sneak, this.quickBreak, this.maxBreakTime, this.autoAbility, this.under, this.panic, this.moreMovement, this.walking, this.walkingTime, this.mode});
   }

   @SubscribeEvent
   public void onLoad(Load event) {
      this.drillnpc = null;
      if (this.isToggled()) {
         this.setToggled(false);
         if (OringoClient.aotvReturn.isToggled()) {
            OringoClient.aotvReturn.start(() -> {
               this.setToggled(true);
            }, false);
         }
      }

   }

   @SubscribeEvent
   public void onWorldRender(RenderWorldLastEvent event) {
      if (this.isToggled()) {
         if (this.target != null) {
            RenderUtils.blockBox(this.target, Color.CYAN);
         }

         if (this.targetRotation != null) {
            RenderUtils.miniBlockBox(this.targetRotation, Color.GREEN);
         }

         if (this.targetRotation2 != null) {
            RenderUtils.miniBlockBox(this.targetRotation2, Color.RED);
         }

      }
   }

   @SubscribeEvent
   public void reconnect(ClientTickEvent event) {
      if (this.mc.field_71462_r instanceof GuiDisconnected && this.shouldReconnect < 0 && this.isToggled()) {
         this.shouldReconnect = 250;
         this.setToggled(false);
      }

      if (this.shouldReconnect-- == 0) {
         this.mc.func_147108_a(new GuiConnecting(new GuiMainMenu(), this.mc, new ServerData("Hypixel", "play.Hypixel.net", false)));
         (new Thread(() -> {
            try {
               Thread.sleep(15000L);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }

            if (this.mc.field_71439_g != null && OringoClient.aotvReturn.isToggled()) {
               OringoClient.aotvReturn.start(() -> {
                  this.setToggled(true);
               }, false);
            }

         })).start();
      }

   }

   public void onEnable() {
      this.ticksSeen = 0;
      this.ticksMining = 0;
      this.ticksTargeting = 0;
      if (this.autoAbility.isEnabled() && this.mc.field_71439_g.func_70694_bm() != null) {
         this.mc.field_71442_b.func_78769_a(this.mc.field_71439_g, this.mc.field_71441_e, this.mc.field_71439_g.func_70694_bm());
      }

   }

   @SubscribeEvent
   public void onChat(ClientChatReceivedEvent event) {
      if (this.isToggled()) {
         String message = event.message.func_150254_d();
         if (this.drillRefuel.isEnabled() && ChatFormatting.stripFormatting(message).startsWith("Your") && ChatFormatting.stripFormatting(message).endsWith("Refuel it by talking to a Drill Mechanic!") && this.drillnpc != null) {
            this.setToggled(false);
            (new Thread(() -> {
               try {
                  int[] var1 = new int[]{this.mc.field_71474_y.field_74351_w.func_151463_i(), this.mc.field_71474_y.field_74370_x.func_151463_i(), this.mc.field_71474_y.field_74368_y.func_151463_i(), this.mc.field_71474_y.field_74366_z.func_151463_i(), this.mc.field_71474_y.field_74311_E.func_151463_i(), this.mc.field_71474_y.field_74312_F.func_151463_i()};
                  int var2 = var1.length;

                  for(int var3 = 0; var3 < var2; ++var3) {
                     int a = var1[var3];
                     KeyBinding.func_74510_a(a, false);
                  }

                  Thread.sleep(500L);
                  this.mc.field_71442_b.func_78768_b(this.mc.field_71439_g, this.drillnpc);
                  Thread.sleep(2500L);
                  if (this.mc.field_71439_g.field_71070_bA instanceof ContainerChest && ((ContainerChest)this.mc.field_71439_g.field_71070_bA).func_85151_d().func_145748_c_().func_150260_c().contains("Drill Anvil")) {
                     int i;
                     Slot slot;
                     for(i = 0; i < this.mc.field_71439_g.field_71070_bA.field_75151_b.size(); ++i) {
                        slot = this.mc.field_71439_g.field_71070_bA.func_75139_a(i);
                        if (slot.func_75216_d() && slot.func_75211_c().func_82833_r().contains("Drill") && slot.func_75211_c().func_77973_b() == Items.field_179562_cC) {
                           this.mc.field_71442_b.func_78753_a(this.mc.field_71439_g.field_71070_bA.field_75152_c, slot.field_75222_d, 0, 1, this.mc.field_71439_g);
                           break;
                        }
                     }

                     Thread.sleep(500L);

                     for(i = 0; i < this.mc.field_71439_g.field_71070_bA.field_75151_b.size(); ++i) {
                        slot = this.mc.field_71439_g.field_71070_bA.func_75139_a(i);
                        if (slot.func_75216_d() && (slot.func_75211_c().func_82833_r().contains("Volta") || slot.func_75211_c().func_82833_r().contains("Oil Barrel") || slot.func_75211_c().func_82833_r().contains("Biofuel"))) {
                           this.mc.field_71442_b.func_78753_a(this.mc.field_71439_g.field_71070_bA.field_75152_c, slot.field_75222_d, 0, 1, this.mc.field_71439_g);
                           break;
                        }
                     }

                     Thread.sleep(500L);
                     this.mc.field_71442_b.func_78753_a(this.mc.field_71439_g.field_71070_bA.field_75152_c, 22, 0, 0, this.mc.field_71439_g);
                     Thread.sleep(250L);
                     this.mc.field_71442_b.func_78753_a(this.mc.field_71439_g.field_71070_bA.field_75152_c, 13, 0, 1, this.mc.field_71439_g);
                     Thread.sleep(250L);
                     this.mc.field_71439_g.func_71053_j();
                  }

                  Thread.sleep(2500L);
                  this.setToggled(true);
                  KeyBinding.func_74510_a(this.mc.field_71474_y.field_74312_F.func_151463_i(), true);
                  this.mc.func_147108_a(new GuiChat());
               } catch (InterruptedException var5) {
                  var5.printStackTrace();
               }

            })).start();
         }

         if (ChatFormatting.stripFormatting(event.message.func_150260_c()).equals("Mining Speed Boost is now available!") && this.autoAbility.isEnabled() && this.mc.field_71439_g.func_70694_bm() != null) {
            OringoClient.sendMessageWithPrefix("Auto ability");
            this.mc.field_71442_b.func_78769_a(this.mc.field_71439_g, this.mc.field_71441_e, this.mc.field_71439_g.func_70694_bm());
         }

         if (ChatFormatting.stripFormatting(event.message.func_150260_c()).equals("Oh no! Your Pickonimbus 2000 broke!")) {
            (new Thread(() -> {
               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
               }

               for(int i = 0; i < 9; ++i) {
                  if (this.mc.field_71439_g.field_71071_by.func_70301_a(i) != null && this.mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().contains("Pickonimbus")) {
                     this.mc.field_71439_g.field_71071_by.field_70461_c = i;
                     break;
                  }
               }

            })).start();
         }

      }
   }

   public void onDisable() {
      KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74312_F.func_151463_i(), false);
      KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74311_E.func_151463_i(), false);
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S08PacketPlayerPosLook && this.isToggled()) {
         this.pause = 200;
         this.target = null;
         KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74312_F.func_151463_i(), false);
         KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74311_E.func_151463_i(), false);
         int[] var2 = new int[]{this.mc.field_71474_y.field_74351_w.func_151463_i(), this.mc.field_71474_y.field_74370_x.func_151463_i(), this.mc.field_71474_y.field_74368_y.func_151463_i(), this.mc.field_71474_y.field_74366_z.func_151463_i()};
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int a = var2[var4];
            KeyBinding.func_74510_a(a, false);
         }
      }

   }

   private boolean isPickaxe(ItemStack itemStack) {
      return itemStack != null && (itemStack.func_82833_r().contains("Pickaxe") || itemStack.func_77973_b() == Items.field_179562_cC || itemStack.func_82833_r().contains("Gauntlet"));
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      --this.pause;
      if (this.isToggled() && !(this.mc.field_71462_r instanceof GuiContainer) && !(this.mc.field_71462_r instanceof GuiEditSign) && this.pause < 1) {
         ++this.ticks;
         if (this.mc.field_71439_g != null && this.mc.field_71439_g.func_70694_bm() != null && this.mc.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemMap) {
            this.setToggled(false);
            this.mc.field_71439_g.func_71165_d("/l");
         }

         if (this.mc.field_71441_e != null) {
            if (this.drillnpc == null && this.drillRefuel.isEnabled()) {
               Iterator var8 = ((List)this.mc.field_71441_e.func_72910_y().stream().filter((entityx) -> {
                  return entityx instanceof EntityArmorStand;
               }).collect(Collectors.toList())).iterator();

               Entity entityArmorStand;
               do {
                  if (!var8.hasNext()) {
                     this.setToggled(false);
                     OringoClient.aotvReturn.start(() -> {
                        this.setToggled(true);
                     }, false);
                     return;
                  }

                  entityArmorStand = (Entity)var8.next();
               } while(!entityArmorStand.func_145748_c_().func_150254_d().contains("§e§lDRILL MECHANIC§r"));

               OringoClient.mithrilMacro.drillnpc = (EntityArmorStand)entityArmorStand;
               OringoClient.sendMessageWithPrefix("Mechanic");
               return;
            }

            if (!this.isPickaxe(this.mc.field_71439_g.func_70694_bm())) {
               for(int i = 0; i < 9; ++i) {
                  if (this.isPickaxe(this.mc.field_71439_g.field_71071_by.func_70301_a(i))) {
                     PlayerUtils.swapToSlot(i);
                  }
               }
            }

            if (this.timeLeft-- <= 0) {
               int[] keybinds = new int[]{this.mc.field_71474_y.field_74351_w.func_151463_i(), this.mc.field_71474_y.field_74370_x.func_151463_i(), this.mc.field_71474_y.field_74368_y.func_151463_i(), this.mc.field_71474_y.field_74366_z.func_151463_i(), this.mc.field_71474_y.field_74370_x.func_151463_i(), this.mc.field_71474_y.field_74368_y.func_151463_i(), this.mc.field_71474_y.field_74366_z.func_151463_i(), this.mc.field_71474_y.field_74368_y.func_151463_i(), this.mc.field_71474_y.field_74368_y.func_151463_i()};
               if (this.lastKey != -1) {
                  KeyBinding.func_74510_a(this.lastKey, false);
                  KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74311_E.func_151463_i(), this.sneak.isEnabled());
               }

               if ((double)(new Random()).nextFloat() < this.walking.getValue() / 100.0D) {
                  this.lastKey = keybinds[(new Random()).nextInt(keybinds.length)];
                  KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74311_E.func_151463_i(), true);
                  KeyBinding.func_74510_a(this.lastKey, true);
                  this.timeLeft = (int)this.walkingTime.getValue();
               }
            } else {
               KeyBinding.func_74510_a(this.lastKey, true);
               KeyBinding.func_74510_a(this.mc.field_71474_y.field_74311_E.func_151463_i(), true);
            }

            if (this.mc.field_71476_x != null && this.mc.field_71476_x.field_72313_a == MovingObjectType.ENTITY) {
               Entity entity = this.mc.field_71476_x.field_72308_g;
               if (entity instanceof EntityPlayer && !EntityUtils.isTeam((EntityLivingBase)entity)) {
                  SkyblockUtils.click();
                  this.pause = 5;
                  return;
               }
            }

            if (this.mc.field_71441_e.field_73010_i.stream().anyMatch((playerEntity) -> {
               return !playerEntity.equals(this.mc.field_71439_g) && playerEntity.func_70032_d(this.mc.field_71439_g) < 10.0F && EntityUtils.isTeam(playerEntity) && (!playerEntity.func_82150_aj() || !(playerEntity.field_70163_u - this.mc.field_71439_g.field_70163_u > 5.0D));
            })) {
               ++this.ticksSeen;
            } else {
               this.ticksSeen = 0;
            }

            boolean inDwarven = SkyblockUtils.anyTab("Dwarven Mines");
            if (this.panic.getValue() <= (double)this.ticksSeen && this.panic.getValue() != 0.0D || !inDwarven) {
               this.setToggled(false);
               if (OringoClient.aotvReturn.isToggled()) {
                  OringoClient.aotvReturn.start(() -> {
                     this.setToggled(true);
                  }, false);
               }

               this.ticksSeen = 0;
               OringoClient.sendMessageWithPrefix(!inDwarven ? "Not in dwarven" : "You have been seen by " + ((EntityPlayer)this.mc.field_71441_e.field_73010_i.stream().filter((playerEntity) -> {
                  return !playerEntity.equals(this.mc.field_71439_g) && playerEntity.func_70032_d(this.mc.field_71439_g) < 10.0F && EntityUtils.isTeam(playerEntity);
               }).findFirst().get()).func_70005_c_());
               return;
            }

            if (this.target == null) {
               if (!this.findTarget()) {
                  OringoClient.sendMessageWithPrefix("No possible target found");
               }

               return;
            }

            if (this.mc.field_71476_x != null && this.mc.field_71476_x.field_72313_a == MovingObjectType.ENTITY) {
               if (this.ticksTargeting++ == 40) {
                  this.setToggled(false);
                  if (OringoClient.aotvReturn.isToggled()) {
                     OringoClient.aotvReturn.start(() -> {
                        this.setToggled(true);
                     }, false);
                  }

                  return;
               }
            } else {
               this.ticksTargeting = 0;
            }

            KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74312_F.func_151463_i(), true);
            if (this.sneak.isEnabled() || this.timeLeft != 0) {
               KeyBinding.func_74510_a(Minecraft.func_71410_x().field_71474_y.field_74311_E.func_151463_i(), true);
            }

            if (this.mc.field_71476_x.field_72313_a == MovingObjectType.BLOCK && this.mc.field_71462_r != null && !(this.mc.field_71462_r instanceof GuiContainer) && this.ticks % 2 == 0) {
               SkyblockUtils.click();
            }

            if (!this.yaw.isEmpty() && (this.stopLoop || !this.isTitanium(this.target))) {
               this.mc.field_71439_g.field_70177_z = (Float)this.yaw.get(0);
               this.mc.field_71439_g.field_70125_A = (Float)this.pitch.get(0);
               this.yaw.remove(0);
               this.pitch.remove(0);
               if (this.yaw.isEmpty() && this.isBlockVisible(this.target) && this.moreMovement.getValue() != 0.0D) {
                  this.stopLoop = false;
                  Vec3 targetRotationTemp = this.targetRotation;
                  this.targetRotation = this.getRandomVisibilityLine(this.target);
                  this.targetRotation2 = this.targetRotation;
                  this.getRotations(false);
                  this.targetRotation = targetRotationTemp;
                  return;
               }

               if (this.moreMovement.getValue() == 0.0D) {
                  this.targetRotation2 = null;
               }

               if (this.stopLoop) {
                  return;
               }
            }

            if (this.mc.field_71441_e.func_180495_p(this.target).func_177230_c().equals(Blocks.field_150357_h)) {
               if (!this.findTarget()) {
               }

               return;
            }

            if (this.mc.field_71476_x.field_72313_a != MovingObjectType.BLOCK) {
               if (!this.findTarget()) {
               }

               return;
            }

            BlockPos pos = this.mc.field_71476_x.func_178782_a();
            if (!pos.equals(this.target)) {
               if (!this.findTarget()) {
               }

               return;
            }

            if (this.quickBreak.getValue() != 0.0D && !this.isTitanium(this.target) && OringoClient.getBlockBreakProgress().values().stream().anyMatch((progress) -> {
               return progress.func_180246_b().equals(this.target);
            }) && OringoClient.getBlockBreakProgress().values().stream().anyMatch((progress) -> {
               return progress.func_180246_b().equals(this.target) && progress.func_73106_e() == (int)(this.quickBreak.getValue() * 10.0D);
            })) {
               this.findTarget();
            }

            if ((double)(this.ticksMining++) == this.maxBreakTime.getValue()) {
               OringoClient.sendMessageWithPrefix("Mining one block took too long");
               this.findTarget();
            }
         }
      }

   }

   private void getRotations(boolean stop) {
      Vec3 lookVec = this.mc.field_71439_g.func_70040_Z().func_178787_e(this.mc.field_71439_g.func_174824_e(0.0F));
      if (!this.yaw.isEmpty()) {
         this.yaw.clear();
         this.pitch.clear();
      }

      double max = (this.rotations.getValue() + 1.0D) * (stop ? 1.0D : this.moreMovement.getValue());

      for(int i = 0; (double)i < max; ++i) {
         Vec3 target = new Vec3(lookVec.field_72450_a + (this.targetRotation.field_72450_a - lookVec.field_72450_a) / max * (double)i, lookVec.field_72448_b + (this.targetRotation.field_72448_b - lookVec.field_72448_b) / max * (double)i, lookVec.field_72449_c + (this.targetRotation.field_72449_c - lookVec.field_72449_c) / max * (double)i);
         Rotation rotation = RotationUtils.getRotations(target);
         this.yaw.add(rotation.getYaw());
         this.pitch.add(rotation.getPitch());
      }

      this.stopLoop = stop;
   }

   private boolean findTarget() {
      ArrayList<BlockPos> blocks = new ArrayList();

      for(int x = -5; x < 6; ++x) {
         for(int y = -5; y < 6; ++y) {
            for(int z = -5; z < 6; ++z) {
               blocks.add(new BlockPos(this.mc.field_71439_g.field_70165_t + (double)x, this.mc.field_71439_g.field_70163_u + (double)y, this.mc.field_71439_g.field_70161_v + (double)z));
            }
         }
      }

      BlockPos sortingCenter = this.target != null ? this.target : this.mc.field_71439_g.func_180425_c();
      Optional<BlockPos> any = blocks.stream().filter((pos) -> {
         return !pos.equals(this.target);
      }).filter(this::matchesMode).filter((pos) -> {
         return this.mc.field_71439_g.func_70011_f((double)pos.func_177958_n(), (double)((float)pos.func_177956_o() - this.mc.field_71439_g.func_70047_e()), (double)pos.func_177952_p()) < 5.5D;
      }).filter(this::isBlockVisible).min(Comparator.comparingDouble((pos) -> {
         return this.isTitanium(pos) && this.titanium.isEnabled() ? 0.0D : this.getDistance(pos, sortingCenter, 0.6D);
      }));
      if (any.isPresent()) {
         this.target = (BlockPos)any.get();
         this.targetRotation2 = null;
         this.targetRotation = this.getRandomVisibilityLine((BlockPos)any.get());
         this.getRotations(true);
      } else {
         any = blocks.stream().filter((pos) -> {
            return !pos.equals(this.target);
         }).filter(this::matchesAny).filter((pos) -> {
            return this.mc.field_71439_g.func_70011_f((double)pos.func_177958_n(), (double)((float)pos.func_177956_o() - this.mc.field_71439_g.func_70047_e()), (double)pos.func_177952_p()) < 5.5D;
         }).filter(this::isBlockVisible).min(Comparator.comparingDouble((pos) -> {
            return this.isTitanium(pos) && this.titanium.isEnabled() ? 0.0D : this.getDistance(pos, sortingCenter, 0.6D);
         }));
         if (any.isPresent()) {
            this.target = (BlockPos)any.get();
            this.targetRotation2 = null;
            this.targetRotation = this.getRandomVisibilityLine((BlockPos)any.get());
            this.getRotations(true);
         }
      }

      this.ticksMining = 0;
      return any.isPresent();
   }

   private double getDistance(BlockPos pos1, BlockPos pos2, double multiY) {
      double deltaX = (double)(pos1.func_177958_n() - pos2.func_177958_n());
      double deltaY = (double)(pos1.func_177956_o() - pos2.func_177956_o()) * multiY;
      double deltaZ = (double)(pos1.func_177952_p() - pos2.func_177952_p());
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   private boolean isBlockVisible(BlockPos pos) {
      return this.getRandomVisibilityLine(pos) != null;
   }

   private Vec3 getRandomVisibilityLine(BlockPos pos) {
      List<Vec3> lines = new ArrayList();

      for(int x = 0; (double)x < this.accuracyChecks.getValue(); ++x) {
         for(int y = 0; (double)y < this.accuracyChecks.getValue(); ++y) {
            for(int z = 0; (double)z < this.accuracyChecks.getValue(); ++z) {
               Vec3 target = new Vec3((double)pos.func_177958_n() + (double)x / this.accuracyChecks.getValue(), (double)pos.func_177956_o() + (double)y / this.accuracyChecks.getValue(), (double)pos.func_177952_p() + (double)z / this.accuracyChecks.getValue());
               this.test = new BlockPos(target.field_72450_a, target.field_72448_b, target.field_72449_c);
               MovingObjectPosition movingObjectPosition = this.mc.field_71441_e.func_147447_a(this.mc.field_71439_g.func_174824_e(0.0F), target, true, false, true);
               if (movingObjectPosition != null) {
                  BlockPos obj = movingObjectPosition.func_178782_a();
                  if (obj.equals(this.test) && this.mc.field_71439_g.func_70011_f(target.field_72450_a, target.field_72448_b - (double)this.mc.field_71439_g.func_70047_e(), target.field_72449_c) < 4.5D && (this.under.isEnabled() || Math.abs(this.mc.field_71439_g.field_70163_u - target.field_72448_b) > 1.3D)) {
                     lines.add(target);
                  }
               }
            }
         }
      }

      return lines.isEmpty() ? null : (Vec3)lines.get((new Random()).nextInt(lines.size()));
   }

   private boolean isTitanium(BlockPos pos) {
      IBlockState state = this.mc.field_71441_e.func_180495_p(pos);
      return state.func_177230_c() == Blocks.field_150348_b && ((EnumType)state.func_177229_b(BlockStone.field_176247_a)).equals(EnumType.DIORITE_SMOOTH);
   }

   private boolean matchesMode(BlockPos pos) {
      IBlockState state = this.mc.field_71441_e.func_180495_p(pos);
      if (this.isTitanium(pos)) {
         return true;
      } else {
         String var3 = this.mode.getSelected();
         byte var4 = -1;
         switch(var3.hashCode()) {
         case 2073722:
            if (var3.equals("Blue")) {
               var4 = 3;
            }
            break;
         case 2102913:
            if (var3.equals("Clay")) {
               var4 = 0;
            }
            break;
         case 2225280:
            if (var3.equals("Gold")) {
               var4 = 4;
            }
            break;
         case 2702037:
            if (var3.equals("Wool")) {
               var4 = 2;
            }
            break;
         case 2076269454:
            if (var3.equals("Prismarine")) {
               var4 = 1;
            }
         }

         switch(var4) {
         case 0:
            return state.func_177230_c().equals(Blocks.field_150406_ce) || state.func_177230_c().equals(Blocks.field_150325_L) && ((EnumDyeColor)state.func_177229_b(BlockColored.field_176581_a)).equals(EnumDyeColor.GRAY);
         case 1:
            return state.func_177230_c().equals(Blocks.field_180397_cI);
         case 2:
            return state.func_177230_c().equals(Blocks.field_150325_L) && ((EnumDyeColor)state.func_177229_b(BlockColored.field_176581_a)).equals(EnumDyeColor.LIGHT_BLUE);
         case 3:
            return state.func_177230_c().equals(Blocks.field_150325_L) && ((EnumDyeColor)state.func_177229_b(BlockColored.field_176581_a)).equals(EnumDyeColor.LIGHT_BLUE) || state.func_177230_c().equals(Blocks.field_180397_cI);
         case 4:
            return state.func_177230_c().equals(Blocks.field_150340_R);
         default:
            return false;
         }
      }
   }

   private boolean matchesAny(BlockPos pos) {
      IBlockState state = this.mc.field_71441_e.func_180495_p(pos);
      return state.func_177230_c().equals(Blocks.field_150325_L) && state.func_177228_b().entrySet().stream().anyMatch((entry) -> {
         return entry.toString().contains("lightBlue");
      }) || state.func_177230_c().equals(Blocks.field_180397_cI) || state.func_177230_c().equals(Blocks.field_150406_ce) || state.func_177230_c().equals(Blocks.field_150325_L) && state.func_177228_b().entrySet().stream().anyMatch((entry) -> {
         return entry.toString().contains("gray");
      }) || this.isTitanium(pos);
   }
}
