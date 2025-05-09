package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import com.mojang.authlib.properties.Property;
import java.util.ArrayList;
import java.util.HashMap;
import me.oringo.oringoclient.events.BlockChangeEvent;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class GhostBlocks extends Module {
   public NumberSetting range = new NumberSetting("Range", 10.0D, 1.0D, 100.0D, 1.0D);
   public BooleanSetting cordGhostBlocks = new BooleanSetting("Cord blocks", true);
   public ModeSetting mode = new ModeSetting("Speed", "Fast", new String[]{"Slow", "Fast"});
   private boolean wasPressed;
   private MilliTimer timer = new MilliTimer();
   private static ArrayList<BlockPos> ghostBlocks = new ArrayList();
   private static HashMap<Long, BlockChangeEvent> eventQueue = new HashMap();
   private boolean hasSent;
   private static final int[][] cords = new int[][]{{275, 220, 231}, {275, 220, 232}, {299, 168, 243}, {299, 168, 244}, {299, 168, 246}, {299, 168, 247}, {299, 168, 247}, {300, 168, 247}, {300, 168, 246}, {300, 168, 244}, {300, 168, 243}, {298, 168, 247}, {298, 168, 246}, {298, 168, 244}, {298, 168, 243}, {287, 167, 240}, {288, 167, 240}, {289, 167, 240}, {290, 167, 240}, {291, 167, 240}, {292, 167, 240}, {293, 167, 240}, {294, 167, 240}, {295, 167, 240}, {290, 167, 239}, {291, 167, 239}, {292, 167, 239}, {293, 167, 239}, {294, 167, 239}, {295, 167, 239}, {290, 166, 239}, {291, 166, 239}, {292, 166, 239}, {293, 166, 239}, {294, 166, 239}, {295, 166, 239}, {290, 166, 240}, {291, 166, 240}, {292, 166, 240}, {293, 166, 240}, {294, 166, 240}, {295, 166, 240}};

   public GhostBlocks() {
      super("Ghost Blocks", 0, Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.range, this.mode, this.cordGhostBlocks});
   }

   @SubscribeEvent
   public void onKey(ClientTickEvent event) {
      if (mc.field_71462_r == null && mc.field_71441_e != null && this.isToggled()) {
         if (this.cordGhostBlocks.isEnabled() && SecretAura.inBoss) {
            int[][] var2 = cords;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               int[] i = var2[var4];
               mc.field_71441_e.func_175698_g(new BlockPos(i[0], i[1], i[2]));
            }
         }

         this.hasSent = true;
         eventQueue.entrySet().removeIf((entry) -> {
            if (System.currentTimeMillis() - (Long)entry.getKey() > 250L) {
               mc.field_71441_e.func_175656_a(((BlockChangeEvent)entry.getValue()).pos, ((BlockChangeEvent)entry.getValue()).state);
               ghostBlocks.remove(((BlockChangeEvent)entry.getValue()).pos);
               return true;
            } else {
               return false;
            }
         });
         this.hasSent = false;
         if (this.isPressed() && (this.mode.getSelected().equals("Slow") && !this.wasPressed || this.mode.getSelected().equals("Fast"))) {
            Vec3 vec3 = mc.field_71439_g.func_174824_e(0.0F);
            Vec3 vec31 = mc.field_71439_g.func_70676_i(0.0F);
            Vec3 vec32 = vec3.func_72441_c(vec31.field_72450_a * this.range.getValue(), vec31.field_72448_b * this.range.getValue(), vec31.field_72449_c * this.range.getValue());
            BlockPos obj = mc.field_71441_e.func_147447_a(vec3, vec32, true, false, true).func_178782_a();
            if (this.isValidBlock(obj)) {
               return;
            }

            mc.field_71441_e.func_175698_g(obj);
            ghostBlocks.add(obj);
         }

         this.wasPressed = this.isPressed();
      }
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onPacket(PacketReceivedEvent event) {
      if (event.packet instanceof S08PacketPlayerPosLook) {
         eventQueue.clear();
      }

   }

   @SubscribeEvent
   public void onBlockChange(BlockChangeEvent event) {
      if (event.state != null && ghostBlocks.contains(event.pos) && !this.hasSent && this.isToggled() && event.state.func_177230_c() != Blocks.field_150350_a) {
         event.setCanceled(true);
         eventQueue.put(System.currentTimeMillis(), event);
      }

   }

   @SubscribeEvent
   public void onWorldJoin(WorldJoinEvent event) {
      eventQueue.clear();
      ghostBlocks.clear();
   }

   public boolean isKeybind() {
      return true;
   }

   private boolean isValidBlock(BlockPos blockPos) {
      Block block = mc.field_71441_e.func_180495_p(blockPos).func_177230_c();
      if (block == Blocks.field_150465_bP) {
         TileEntitySkull tileEntity = (TileEntitySkull)mc.field_71441_e.func_175625_s(blockPos);
         if (tileEntity.func_145904_a() == 3 && tileEntity.func_152108_a() != null && tileEntity.func_152108_a().getProperties() != null) {
            Property property = (Property)SkyblockUtils.firstOrNull(tileEntity.func_152108_a().getProperties().get("textures"));
            return property != null && property.getValue().equals("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRkYjRhZGZhOWJmNDhmZjVkNDE3MDdhZTM0ZWE3OGJkMjM3MTY1OWZjZDhjZDg5MzQ3NDlhZjRjY2U5YiJ9fX0=");
         }
      }

      return block == Blocks.field_150442_at || block == Blocks.field_150486_ae || block == Blocks.field_150447_bR;
   }
}
