package me.oringo.oringoclient.utils;

import com.google.common.collect.Iterables;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.PacketReceivedEvent;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.ui.notifications.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class SkyblockUtils {
   private static final Minecraft mc = Minecraft.func_71410_x();
   public static boolean inDungeon;
   public static boolean isInOtherGame;
   public static boolean onSkyblock;
   public static boolean inBlood;
   public static boolean inP3;

   public static boolean isTerminal(String name) {
      return name.contains("Correct all the panes!") || name.contains("Navigate the maze!") || name.contains("Click in order!") || name.contains("What starts with:") || name.contains("Select all the") || name.contains("Change all to same color!") || name.contains("Click the button on time!");
   }

   @SubscribeEvent
   public void onChat(PacketReceivedEvent event) {
      if (event.packet instanceof S02PacketChat) {
         if (ChatFormatting.stripFormatting(((S02PacketChat)event.packet).func_148915_c().func_150254_d()).startsWith("[BOSS] The Watcher: ") && !inBlood) {
            inBlood = true;
            if (OringoClient.bloodAimbot.isToggled()) {
               Notifications.showNotification("Oringo Client", "Started Camp", 1000);
            }
         }

         if (ChatFormatting.stripFormatting(((S02PacketChat)event.packet).func_148915_c().func_150254_d()).equals("[BOSS] The Watcher: You have proven yourself. You may pass.")) {
            inBlood = false;
            if (OringoClient.bloodAimbot.isToggled()) {
               Notifications.showNotification("Oringo Client", "Stopped camp", 1000);
            }
         }

         if (ChatFormatting.stripFormatting(((S02PacketChat)event.packet).func_148915_c().func_150254_d()).startsWith("[BOSS] Necron: I hope you're in shape. BETTER GET RUNNING!")) {
            inP3 = true;
         }

         if (ChatFormatting.stripFormatting(((S02PacketChat)event.packet).func_148915_c().func_150254_d()).startsWith("[BOSS] Necron: THAT'S IT YOU HAVE DONE IT! MY ENTIRE FACTORY IS RUINED! ARE YOU HAPPY?!")) {
            inP3 = false;
         }
      }

   }

   @SubscribeEvent
   public void onWorldLoad(WorldJoinEvent event) {
      inBlood = false;
      inP3 = false;
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (mc.field_71441_e != null && event.phase == Phase.START) {
         inDungeon = hasLine("Cleared:") || hasLine("Start");
         isInOtherGame = isInOtherGame();
         onSkyblock = isOnSkyBlock();
      }

   }

   public static <T> T firstOrNull(Iterable<T> iterable) {
      return Iterables.getFirst(iterable, (Object)null);
   }

   public static boolean hasScoreboardTitle(String title) {
      return mc.field_71439_g != null && mc.field_71439_g.func_96123_co() != null && mc.field_71439_g.func_96123_co().func_96539_a(1) != null ? ChatFormatting.stripFormatting(mc.field_71439_g.func_96123_co().func_96539_a(1).func_96678_d()).equalsIgnoreCase(title) : false;
   }

   public static boolean isInOtherGame() {
      try {
         Scoreboard sb = mc.field_71439_g.func_96123_co();
         List<Score> list = new ArrayList(sb.func_96534_i(sb.func_96539_a(1)));
         Iterator var2 = list.iterator();

         while(var2.hasNext()) {
            Score score = (Score)var2.next();
            ScorePlayerTeam team = sb.func_96509_i(score.func_96653_e());
            String s = ChatFormatting.stripFormatting(ScorePlayerTeam.func_96667_a(team, score.func_96653_e()));
            if (s.contains("Map")) {
               return true;
            }
         }
      } catch (Exception var6) {
      }

      return false;
   }

   public static boolean isOnSkyBlock() {
      try {
         ScoreObjective titleObjective = mc.field_71439_g.func_96123_co().func_96539_a(1);
         return mc.field_71439_g.func_96123_co().func_96539_a(0) != null ? ChatFormatting.stripFormatting(mc.field_71439_g.func_96123_co().func_96539_a(0).func_96678_d()).contains("SKYBLOCK") : ChatFormatting.stripFormatting(mc.field_71439_g.func_96123_co().func_96539_a(1).func_96678_d()).contains("SKYBLOCK");
      } catch (Exception var1) {
         return false;
      }
   }

   public static boolean hasLine(String line) {
      if (mc.field_71439_g != null && mc.field_71439_g.func_96123_co() != null && mc.field_71439_g.func_96123_co().func_96539_a(1) != null) {
         Scoreboard sb = Minecraft.func_71410_x().field_71439_g.func_96123_co();
         List<Score> list = new ArrayList(sb.func_96534_i(sb.func_96539_a(1)));
         Iterator var3 = list.iterator();

         StringBuilder builder;
         do {
            Score score;
            ScorePlayerTeam team;
            do {
               if (!var3.hasNext()) {
                  return false;
               }

               score = (Score)var3.next();
               team = sb.func_96509_i(score.func_96653_e());
            } while(team == null);

            String s = ChatFormatting.stripFormatting(team.func_96668_e() + score.func_96653_e() + team.func_96663_f());
            builder = new StringBuilder();
            char[] var8 = s.toCharArray();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               char c = var8[var10];
               if (c < 256) {
                  builder.append(c);
               }
            }
         } while(!builder.toString().toLowerCase().contains(line.toLowerCase()));

         return true;
      } else {
         return false;
      }
   }

   public static boolean isMiniboss(Entity entity) {
      return entity.func_70005_c_().equals("Shadow Assassin") || entity.func_70005_c_().equals("Lost Adventurer") || entity.func_70005_c_().equals("Diamond Guy");
   }

   public static void click() {
      try {
         Method clickMouse;
         try {
            clickMouse = Minecraft.class.getDeclaredMethod("func_147116_af");
         } catch (NoSuchMethodException var2) {
            clickMouse = Minecraft.class.getDeclaredMethod("clickMouse");
         }

         clickMouse.setAccessible(true);
         clickMouse.invoke(Minecraft.func_71410_x());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static boolean anyTab(String s) {
      return Minecraft.func_71410_x().func_147114_u().func_175106_d().stream().anyMatch((player) -> {
         return player.func_178854_k() != null && ChatFormatting.stripFormatting(player.func_178854_k().func_150254_d()).toLowerCase().contains(s.toLowerCase(Locale.ROOT));
      });
   }

   public static boolean isNPC(Entity entity) {
      if (!(entity instanceof EntityOtherPlayerMP)) {
         return false;
      } else {
         EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
         return ChatFormatting.stripFormatting(entity.func_145748_c_().func_150260_c()).startsWith("[NPC]") || entity.func_110124_au().version() == 2 && entityLivingBase.func_110143_aJ() == 20.0F && entityLivingBase.func_110138_aP() == 20.0F;
      }
   }

   public static void rightClick() {
      try {
         Method rightClickMouse = null;

         try {
            rightClickMouse = Minecraft.class.getDeclaredMethod("func_147121_ag");
         } catch (NoSuchMethodException var2) {
            rightClickMouse = Minecraft.class.getDeclaredMethod("rightClickMouse");
         }

         rightClickMouse.setAccessible(true);
         rightClickMouse.invoke(Minecraft.func_71410_x());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static String getDisplayName(ItemStack item) {
      return item == null ? "null" : item.func_82833_r();
   }

   public static Color rainbow(int delay) {
      double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0D);
      rainbowState %= 360.0D;
      return Color.getHSBColor((float)(rainbowState / 360.0D), 1.0F, 1.0F);
   }

   public static int getPing() {
      NetworkPlayerInfo networkPlayerInfo = mc.func_147114_u().func_175102_a(Minecraft.func_71410_x().field_71439_g.func_110124_au());
      return networkPlayerInfo == null ? 0 : networkPlayerInfo.func_178853_c();
   }
}
