package me.oringo.oringoclient.commands.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public class SettingsCommand extends Command {
   public SettingsCommand() {
      super("oringo");
   }

   public void execute(String[] args) throws Exception {
      if (args.length != 0 && args[0].equalsIgnoreCase("scoreboard") && mc.field_71439_g.func_96123_co() != null) {
         StringBuilder builder = new StringBuilder();
         Scoreboard sb = Minecraft.func_71410_x().field_71439_g.func_96123_co();
         List<Score> list = new ArrayList(sb.func_96534_i(sb.func_96539_a(1)));
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Score score = (Score)var5.next();
            ScorePlayerTeam team = sb.func_96509_i(score.func_96653_e());
            String s = team.func_96668_e() + score.func_96653_e() + team.func_96663_f();
            char[] var9 = s.toCharArray();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               char c = var9[var11];
               if (c < 256) {
                  builder.append(c);
               }
            }

            builder.append("\n");
         }

         builder.append(mc.field_71439_g.func_96123_co().func_96539_a(1).func_96678_d());
         System.out.println(builder);
      } else {
         OringoClient.clickGui.toggle();
      }
   }

   public String getDescription() {
      return "Opens the menu";
   }
}
