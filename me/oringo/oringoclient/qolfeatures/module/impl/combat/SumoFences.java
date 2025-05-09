package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.macro.AutoSumoBot;
import me.oringo.oringoclient.utils.DiscordWebhook;
import me.oringo.oringoclient.utils.ReflectionUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class SumoFences extends Module {
   private boolean sumo = false;
   private ArrayList<BlockPos> posList = new ArrayList();
   private List<String> win = Arrays.asList("gg", "gf");
   private List<String> lose = Arrays.asList("gg", "gf", "how");
   private String winTime = "";
   private static int shouldReconnect = -1;
   private static int wins = -1;
   private static int winstreak = 0;
   private static int lost = 0;

   public SumoFences() {
      super("Sumo Fences", 0, Module.Category.COMBAT);
   }

   @SubscribeEvent
   public void onSumoStart(ClientTickEvent event) {
      try {
         if (this.isToggled()) {
            if (SkyblockUtils.hasLine("Mode: Sumo") && SkyblockUtils.hasLine("Opponent:")) {
               if (!this.sumo) {
                  this.sumo = true;
                  Minecraft mc = Minecraft.func_71410_x();
                  BlockPos pos = mc.field_71439_g.func_180425_c();
                  int var4 = 0;

                  while(var4++ != 10) {
                     if (!Minecraft.func_71410_x().field_71441_e.func_180495_p(pos = pos.func_177977_b()).func_177230_c().equals(Blocks.field_150350_a)) {
                        (new Thread(() -> {
                           try {
                              Thread.sleep(1000L);
                           } catch (InterruptedException var3) {
                              var3.printStackTrace();
                           }

                           this.sumo = false;
                           this.posList.clear();
                           this.checkBlock(pos.func_177977_b());
                        })).start();
                        break;
                     }
                  }
               }
            } else {
               if (this.sumo) {
                  this.posList.clear();
               }

               this.sumo = false;
            }
         }
      } catch (Exception var6) {
      }

   }

   @SubscribeEvent
   public void onChat(ClientChatReceivedEvent event) {
      if (AutoSumoBot.thread != null) {
         if (ChatFormatting.stripFormatting(event.message.func_150254_d()).contains("                           Sumo Duel - ")) {
            this.winTime = ChatFormatting.stripFormatting(event.message.func_150254_d()).replaceFirst("                           Sumo Duel - ", "");
         }

         if (event.message.func_150254_d().contains("§f§lMelee Accuracy")) {
            (new Thread(() -> {
               try {
                  Thread.sleep(250L);
                  if (Minecraft.func_71410_x().field_71439_g.field_71075_bZ.field_75100_b) {
                     String title = (String)ReflectionUtils.getFieldByName(Minecraft.func_71410_x().field_71456_v, "field_175201_x");
                     DiscordWebhook webhook;
                     DiscordWebhook.EmbedObject embedx;
                     if (title != null && title.toUpperCase().contains("DRAW")) {
                        webhook = new DiscordWebhook(OringoClient.autoSumo.webhook.getValue());
                        webhook.setUsername("AutoSumo bot");
                        webhook.setAvatarUrl("https://cdn.discordapp.com/icons/913088401262137424/496d604510a63242db77526d8bfab9fa.png");
                        embedx = new DiscordWebhook.EmbedObject();
                        embedx.setTitle("Draw").setDescription("Opponent: " + AutoSumoBot.target.func_70005_c_() + " Time: " + this.winTime).setColor(Color.ORANGE);
                        webhook.addEmbed(embedx);
                        webhook.execute();
                        return;
                     }

                     Minecraft.func_71410_x().field_71439_g.func_71165_d("/ac " + (String)this.lose.get((new Random()).nextInt(this.lose.size())));
                     webhook = new DiscordWebhook(OringoClient.autoSumo.webhook.getValue());
                     webhook.setUsername("AutoSumo bot");
                     webhook.setAvatarUrl("https://cdn.discordapp.com/icons/913088401262137424/496d604510a63242db77526d8bfab9fa.png");
                     embedx = new DiscordWebhook.EmbedObject();
                     winstreak = 0;
                     ++lost;
                     embedx.setTitle("Lost").setDescription("Opponent: " + AutoSumoBot.target.func_70005_c_() + " Time: " + this.winTime + " Loses this session: " + lost).addField("Blacklist", " Added " + AutoSumoBot.target.func_110124_au().toString() + " to blacklist!", false).setColor(Color.RED);
                     webhook.addEmbed(embedx);
                     webhook.execute();
                  } else {
                     Minecraft.func_71410_x().field_71439_g.func_71165_d("/ac " + (String)this.win.get((new Random()).nextInt(this.win.size())));
                     DiscordWebhook webhookx = new DiscordWebhook(OringoClient.autoSumo.webhook.getValue());
                     webhookx.setUsername("AutoSumo bot");
                     webhookx.setAvatarUrl("https://cdn.discordapp.com/icons/913088401262137424/496d604510a63242db77526d8bfab9fa.png");
                     DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
                     ++wins;
                     ++winstreak;
                     embed.setTitle("WIN").setDescription("Opponent: " + AutoSumoBot.target.func_70005_c_() + " Time: " + this.winTime + " Wins: " + wins + " winstreak: " + winstreak).setColor(Color.GREEN);
                     webhookx.addEmbed(embed);
                     webhookx.execute();
                  }

                  Thread.sleep(100L);
               } catch (IOException | InterruptedException var4) {
                  var4.printStackTrace();
               }

               Minecraft.func_71410_x().field_71439_g.func_71165_d("/play duels_sumo_duel");
            })).start();
         }

      }
   }

   @SubscribeEvent
   public void onDisconnect(GuiOpenEvent event) {
      if (event.gui instanceof GuiDisconnected && AutoSumoBot.thread != null) {
         DiscordWebhook webhook = new DiscordWebhook(OringoClient.autoSumo.webhook.getValue());
         webhook.setUsername("AutoSumo bot");
         webhook.setAvatarUrl("https://cdn.discordapp.com/icons/913088401262137424/496d604510a63242db77526d8bfab9fa.png");
         webhook.setContent("Bot Disconnected! <@884509916868517898>");
         shouldReconnect = 2000;

         try {
            webhook.execute();
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

   }

   @SubscribeEvent
   public void reconnect(ClientTickEvent event) {
      if (shouldReconnect-- == 0 && AutoSumoBot.thread != null) {
         mc.func_147108_a(new GuiConnecting(new GuiMainMenu(), mc, new ServerData("Hypixel", "play.Hypixel.net", false)));
      }

   }

   private void checkBlock(BlockPos pos) {
      Iterator var2 = Arrays.asList(pos.func_177976_e(), pos.func_177978_c().func_177974_f(), pos.func_177978_c().func_177976_e(), pos.func_177968_d().func_177976_e(), pos.func_177968_d().func_177974_f(), pos.func_177974_f(), pos.func_177978_c(), pos.func_177968_d()).iterator();

      while(var2.hasNext()) {
         BlockPos blockPos = (BlockPos)var2.next();
         if (Minecraft.func_71410_x().field_71441_e.func_180495_p(blockPos).func_177230_c().equals(Blocks.field_150350_a)) {
            Minecraft.func_71410_x().field_71441_e.func_175656_a(blockPos.func_177984_a().func_177984_a().func_177984_a(), Blocks.field_150350_a.func_176223_P());
            Minecraft.func_71410_x().field_71441_e.func_175656_a(blockPos.func_177984_a().func_177984_a().func_177984_a(), Blocks.field_180407_aO.func_176223_P());
            Minecraft.func_71410_x().field_71441_e.func_175656_a(blockPos.func_177984_a().func_177984_a().func_177984_a().func_177984_a(), Blocks.field_180407_aO.func_176223_P());
         } else if (!this.posList.contains(blockPos)) {
            this.posList.add(blockPos);
            this.checkBlock(blockPos);
         }
      }

   }
}
