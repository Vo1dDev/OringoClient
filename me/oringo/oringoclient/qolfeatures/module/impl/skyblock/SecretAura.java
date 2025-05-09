package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import com.mojang.authlib.properties.Property;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Iterator;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SecretAura extends Module {
   public NumberSetting reach = new NumberSetting("Reach", 5.0D, 2.0D, 6.0D, 0.1D);
   public StringSetting item = new StringSetting("Item");
   public BooleanSetting cancelChest = new BooleanSetting("Cancel chests", true);
   public BooleanSetting clickedCheck = new BooleanSetting("Clicked check", true);
   public BooleanSetting rotation = new BooleanSetting("Rotation", false);
   public static ArrayList<BlockPos> clicked = new ArrayList();
   public static boolean inBoss;
   private boolean sent;

   public SecretAura() {
      super("Secret Aura", 0, Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.reach, this.item, this.rotation, this.cancelChest, this.clickedCheck});
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onUpdatePre(MotionUpdateEvent.Pre event) {
      if (mc.field_71439_g != null && this.isToggled() && SkyblockUtils.inDungeon && this.rotation.isEnabled()) {
         Vec3i vec3i = new Vec3i(10, 10, 10);
         Iterator var3 = BlockPos.func_177980_a((new BlockPos(mc.field_71439_g.func_180425_c())).func_177971_a(vec3i), new BlockPos(mc.field_71439_g.func_180425_c().func_177973_b(vec3i))).iterator();

         while(var3.hasNext()) {
            BlockPos blockPos = (BlockPos)var3.next();
            if (this.isValidBlock(blockPos) && mc.field_71439_g.func_70011_f((double)blockPos.func_177958_n(), (double)((float)blockPos.func_177956_o() - mc.field_71439_g.func_70047_e()), (double)blockPos.func_177952_p()) < this.reach.getValue()) {
               Rotation floats = RotationUtils.getRotations(new Vec3((double)blockPos.func_177958_n() + 0.5D, (double)blockPos.func_177956_o() + 0.5D, (double)blockPos.func_177952_p() + 0.5D));
               event.yaw = floats.getYaw();
               event.pitch = floats.getPitch();
               break;
            }
         }
      }

   }

   @SubscribeEvent
   public void onTick(MotionUpdateEvent.Post event) {
      if (mc.field_71439_g != null && this.isToggled() && SkyblockUtils.inDungeon) {
         Vec3i vec3i = new Vec3i(10, 10, 10);
         Iterator var3 = BlockPos.func_177980_a((new BlockPos(mc.field_71439_g.func_180425_c())).func_177971_a(vec3i), new BlockPos(mc.field_71439_g.func_180425_c().func_177973_b(vec3i))).iterator();

         while(var3.hasNext()) {
            BlockPos blockPos = (BlockPos)var3.next();
            if (this.isValidBlock(blockPos) && mc.field_71439_g.func_70011_f((double)blockPos.func_177958_n(), (double)((float)blockPos.func_177956_o() - mc.field_71439_g.func_70047_e()), (double)blockPos.func_177952_p()) < this.reach.getValue()) {
               this.interactWithBlock(blockPos);
               if (this.rotation.isEnabled()) {
                  break;
               }
            }
         }
      }

   }

   private boolean isValidBlock(BlockPos blockPos) {
      Block block = mc.field_71441_e.func_180495_p(blockPos).func_177230_c();
      if (block == Blocks.field_150465_bP) {
         TileEntitySkull tileEntity = (TileEntitySkull)mc.field_71441_e.func_175625_s(blockPos);
         if (tileEntity.func_145904_a() == 3 && tileEntity.func_152108_a() != null && tileEntity.func_152108_a().getProperties() != null) {
            Property property = (Property)SkyblockUtils.firstOrNull(tileEntity.func_152108_a().getProperties().get("textures"));
            return property != null && property.getValue().equals("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRkYjRhZGZhOWJmNDhmZjVkNDE3MDdhZTM0ZWE3OGJkMjM3MTY1OWZjZDhjZDg5MzQ3NDlhZjRjY2U5YiJ9fX0=") && (!clicked.contains(blockPos) || !this.clickedCheck.isEnabled());
         }
      }

      return (block == Blocks.field_150442_at || block == Blocks.field_150486_ae || block == Blocks.field_150447_bR) && (!clicked.contains(blockPos) || !this.clickedCheck.isEnabled());
   }

   private void interactWithBlock(BlockPos pos) {
      for(int i = 0; i < 9; ++i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null && mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r().toLowerCase().contains(this.item.getValue().toLowerCase())) {
            int holding = mc.field_71439_g.field_71071_by.field_70461_c;
            mc.field_71439_g.field_71071_by.field_70461_c = i;
            if (mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150442_at && !inBoss) {
               mc.field_71442_b.func_178890_a(mc.field_71439_g, mc.field_71441_e, mc.field_71439_g.field_71071_by.func_70448_g(), pos, EnumFacing.func_176733_a((double)mc.field_71439_g.field_70177_z), new Vec3(0.0D, 0.0D, 0.0D));
            }

            mc.field_71442_b.func_178890_a(mc.field_71439_g, mc.field_71441_e, mc.field_71439_g.field_71071_by.func_70448_g(), pos, EnumFacing.func_176733_a((double)mc.field_71439_g.field_70177_z), new Vec3(0.0D, 0.0D, 0.0D));
            mc.field_71439_g.field_71071_by.field_70461_c = holding;
            clicked.add(pos);
            return;
         }
      }

      if (!this.sent) {
         OringoClient.sendMessageWithPrefix("You don't have a required item in your hotbar!");
         this.sent = true;
      }

   }

   @SubscribeEvent
   public void onChat(PacketReceivedEvent event) {
      if (event.packet instanceof S02PacketChat && ChatFormatting.stripFormatting(((S02PacketChat)event.packet).func_148915_c().func_150254_d()).startsWith("[BOSS] Necron")) {
         inBoss = true;
      }

   }

   @SubscribeEvent
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S2DPacketOpenWindow && ChatFormatting.stripFormatting(((S2DPacketOpenWindow)event.packet).func_179840_c().func_150254_d()).equals("Chest") && SkyblockUtils.inDungeon && this.cancelChest.isEnabled()) {
         event.setCanceled(true);
         mc.func_147114_u().func_147298_b().func_179290_a(new C0DPacketCloseWindow(((S2DPacketOpenWindow)event.packet).func_148901_c()));
      }

   }

   @SubscribeEvent
   public void clear(Load event) {
      inBoss = false;
      clicked.clear();
   }
}
