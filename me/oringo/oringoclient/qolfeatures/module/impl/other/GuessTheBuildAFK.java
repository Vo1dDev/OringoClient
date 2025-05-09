package me.oringo.oringoclient.qolfeatures.module.impl.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuessTheBuildAFK extends Module {
   private ArrayList<String> wordList = new ArrayList();
   private int tips = 0;
   private Thread guesses = null;
   private int period = 3200;
   private long lastGuess = 0L;

   public GuessTheBuildAFK() {
      super("Auto GTB", 0, Module.Category.OTHER);
   }

   @SubscribeEvent
   public void onChat(ClientChatReceivedEvent event) {
      if (this.isToggled()) {
         try {
            ScoreObjective o = Minecraft.func_71410_x().field_71439_g.func_96123_co().func_96539_a(0);
            if (ChatFormatting.stripFormatting((o == null ? Minecraft.func_71410_x().field_71439_g.func_96123_co().func_96539_a(1) : o).func_96678_d()).contains("GUESS THE BUILD") && ChatFormatting.stripFormatting(event.message.func_150254_d()).startsWith("This game has been recorded")) {
               Minecraft.func_71410_x().field_71439_g.func_71165_d("/play build_battle_guess_the_build");
            }
         } catch (Exception var5) {
         }

         if (event.message.func_150254_d().startsWith("§eThe theme was") && this.guesses != null) {
            this.guesses.stop();
            this.guesses = null;
         }

         if (ChatFormatting.stripFormatting(event.message.func_150254_d()).startsWith(Minecraft.func_71410_x().field_71439_g.func_70005_c_() + " correctly guessed the theme!") && this.guesses != null) {
            this.guesses.stop();
            this.guesses = null;
         }

         if (event.type == 2) {
            if (event.message.func_150254_d().contains("The theme is") && event.message.func_150254_d().contains("_")) {
               if (this.wordList.isEmpty()) {
                  this.loadWords();
               }

               int newTips = this.getTips(event.message.func_150254_d());
               if (newTips != this.tips) {
                  this.tips = newTips;
                  String tip = ChatFormatting.stripFormatting(event.message.func_150254_d()).replaceFirst("The theme is ", "");
                  ArrayList<String> matchingWords = this.getMatchingWords(tip);
                  if (matchingWords.size() == 1) {
                     Notifications.showNotification("Oringo Client", "Found 1 matching word! Sending: §f" + (String)matchingWords.get(0), 2000);
                     if (this.guesses != null) {
                        this.guesses.stop();
                        this.guesses = null;
                        (new Thread(() -> {
                           try {
                              Thread.sleep((long)this.period - (System.currentTimeMillis() - this.lastGuess));
                           } catch (Exception var3) {
                              var3.printStackTrace();
                           }

                           Minecraft.func_71410_x().field_71439_g.func_71165_d("/ac " + ((String)matchingWords.get(0)).toLowerCase());
                        })).start();
                        return;
                     }

                     Minecraft.func_71410_x().field_71439_g.func_71165_d("/ac " + ((String)matchingWords.get(0)).toLowerCase());
                     return;
                  }

                  Notifications.showNotification("Oringo Client", String.format("Found %s matching words!", matchingWords.size()), 1500);
                  if (matchingWords.size() <= 15) {
                     Collections.shuffle(matchingWords);
                     (this.guesses = new Thread(() -> {
                        Iterator var2 = matchingWords.iterator();

                        while(var2.hasNext()) {
                           String matchingWord = (String)var2.next();
                           this.lastGuess = System.currentTimeMillis();
                           Minecraft.func_71410_x().field_71439_g.func_71165_d("/ac " + matchingWord.toLowerCase());
                           Notifications.showNotification("Oringo Client", "Trying: §f" + matchingWord, 2000);

                           try {
                              Thread.sleep((long)this.period);
                           } catch (Exception var5) {
                              var5.printStackTrace();
                           }
                        }

                     })).start();
                  }
               }
            } else {
               this.tips = 0;
            }
         }

      }
   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      if (this.isToggled()) {
         (new Thread(() -> {
            try {
               Thread.sleep(1000L);
               if (event.gui instanceof GuiChest) {
                  Minecraft mc = Minecraft.func_71410_x();
                  ScoreObjective o = mc.field_71439_g.func_96123_co().func_96539_a(0);
                  if (ChatFormatting.stripFormatting((o == null ? mc.field_71439_g.func_96123_co().func_96539_a(1) : o).func_96678_d()).contains("GUESS THE BUILD")) {
                     mc.field_71442_b.func_78753_a(((GuiChest)event.gui).field_147002_h.field_75152_c, 15, 0, 0, mc.field_71439_g);
                     Thread.sleep(2000L);
                     KeyBinding.func_74510_a(mc.field_71474_y.field_74311_E.func_151463_i(), true);
                     Thread.sleep(2000L);
                     KeyBinding.func_74510_a(mc.field_71474_y.field_74311_E.func_151463_i(), false);
                     mc.field_71439_g.field_71071_by.field_70461_c = 1;
                     mc.field_71439_g.field_70125_A = 40.0F;
                     Thread.sleep(500L);
                     SkyblockUtils.rightClick();
                  }
               }
            } catch (Exception var3) {
               var3.printStackTrace();
            }

         })).start();
      }
   }

   private int getTips(String text) {
      return text.replaceAll(" ", "").replaceAll("_", "").length();
   }

   private void loadWords() {
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(OringoClient.class.getClassLoader().getResourceAsStream("words.txt")));

         String line;
         while((line = br.readLine()) != null) {
            this.wordList.add(line);
         }

         br.close();
      } catch (Exception var3) {
         var3.printStackTrace();
         OringoClient.sendMessageWithPrefix("Couldn't load word list!");
      }

   }

   public ArrayList<String> getMatchingWords(String tip) {
      ArrayList<String> list = new ArrayList();
      Iterator var3 = this.wordList.iterator();

      while(true) {
         String word;
         do {
            if (!var3.hasNext()) {
               return list;
            }

            word = (String)var3.next();
         } while(word.length() != tip.length());

         boolean matching = true;

         for(int i = 0; i < word.length(); ++i) {
            if (tip.charAt(i) == '_') {
               if (word.charAt(i) != ' ') {
                  continue;
               }

               matching = false;
            }

            if (tip.charAt(i) != word.charAt(i)) {
               matching = false;
            }

            if (!matching) {
               break;
            }
         }

         if (matching) {
            list.add(word);
         }
      }
   }
}
