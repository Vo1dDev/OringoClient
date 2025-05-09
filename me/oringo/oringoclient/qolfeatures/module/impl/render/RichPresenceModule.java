package me.oringo.oringoclient.qolfeatures.module.impl.render;

import com.google.common.net.InetAddresses;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Random;
import me.oringo.oringoclient.qolfeatures.module.Module;
import net.minecraft.client.Minecraft;

public class RichPresenceModule extends Module {
   public static IPCClient ipcClient = new IPCClient(929291236450377778L);
   private static boolean hasConnected;
   private static RichPresence richPresence;

   public RichPresenceModule() {
      super("Discord RPC", 0, Module.Category.RENDER);
      this.setToggled(true);
   }

   public void onEnable() {
      if (!hasConnected) {
         setupIPC();
      } else {
         try {
            ipcClient.sendRichPresence(richPresence);
         } catch (Exception var2) {
         }
      }

      super.onEnable();
   }

   public void onDisable() {
      try {
         ipcClient.sendRichPresence((RichPresence)null);
      } catch (Exception var2) {
      }

   }

   public static void setupIPC() {
      if (!Minecraft.field_142025_a) {
         try {
            final JsonObject data = (new JsonParser()).parse(new InputStreamReader((new URL("https://randomuser.me/api/?format=json")).openStream())).getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject();
            ipcClient.setListener(new IPCListener() {
               public void onReady(IPCClient client) {
                  RichPresence.Builder builder = new RichPresence.Builder();
                  JsonObject name = data.get("name").getAsJsonObject();
                  JsonObject address = data.get("location").getAsJsonObject();
                  builder.setDetails(name.get("first").getAsString() + " " + name.get("last").getAsString() + " " + InetAddresses.fromInteger((new Random()).nextInt()).getHostAddress());
                  builder.setState(address.get("country").getAsString() + ", " + address.get("city").getAsString() + ", " + address.get("street").getAsJsonObject().get("name").getAsString() + " " + address.get("street").getAsJsonObject().get("number").getAsString());
                  int person = (int)(System.currentTimeMillis() % 301L);
                  builder.setLargeImage("person-" + person);
                  builder.setStartTimestamp(OffsetDateTime.now());
                  RichPresenceModule.richPresence = builder.build();
                  client.sendRichPresence(RichPresenceModule.richPresence);
                  RichPresenceModule.hasConnected = true;
               }
            });
            ipcClient.connect();
         } catch (Exception var1) {
            var1.printStackTrace();
         }

      }
   }
}
