package me.oringo.oringoclient.qolfeatures;

import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URL;
import javax.swing.JOptionPane;
import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.Session;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LoginWithSession {
   private Session original = null;

   @SubscribeEvent
   public void onGuiCreate(Post event) {
      if (OringoClient.devMode && event.gui instanceof GuiMainMenu) {
         event.buttonList.add(new GuiButton(2137, 5, 5, 100, 20, "Login"));
         event.buttonList.add(new GuiButton(21370, 115, 5, 100, 20, "Save"));
      }

   }

   @SubscribeEvent
   public void onClick(net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post event) {
      if (event.gui instanceof GuiMainMenu) {
         if (event.button.field_146127_k == 2137) {
            if (this.original == null) {
               this.original = Minecraft.func_71410_x().func_110432_I();
            }

            String login = JOptionPane.showInputDialog("Login");
            if (login == null || login.isEmpty()) {
               return;
            }

            Field field_71449_j;
            if (login.equalsIgnoreCase("reset")) {
               try {
                  field_71449_j = Minecraft.class.getDeclaredField("field_71449_j");
                  field_71449_j.setAccessible(true);
                  field_71449_j.set(Minecraft.func_71410_x(), this.original);
               } catch (Exception var5) {
                  var5.printStackTrace();
               }

               return;
            }

            try {
               field_71449_j = Minecraft.class.getDeclaredField("field_71449_j");
               field_71449_j.setAccessible(true);
               String username = (new JsonParser()).parse(new InputStreamReader((new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + login.split(": ")[1])).openStream())).getAsJsonObject().get("name").getAsString();
               field_71449_j.set(Minecraft.func_71410_x(), new Session(username, login.split(": ")[1], login.split(": ")[0], "mojang"));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }

         if (event.button.field_146127_k == 21370) {
            try {
               BufferedWriter savedData = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("savedData")));
               savedData.write(Minecraft.func_71410_x().func_110432_I().func_148254_d() + ": " + Minecraft.func_71410_x().func_110432_I().func_148255_b());
               savedData.close();
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }
      }

   }
}
