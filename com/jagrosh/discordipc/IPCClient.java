package com.jagrosh.discordipc;

import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.Pipe;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IPCClient implements Closeable {
   private static final Logger LOGGER = LoggerFactory.getLogger(IPCClient.class);
   private final long clientId;
   private final HashMap<String, Callback> callbacks = new HashMap();
   private volatile Pipe pipe;
   private IPCListener listener = null;
   private Thread readThread = null;

   public IPCClient(long clientId) {
      this.clientId = clientId;
   }

   public void setListener(IPCListener listener) {
      this.listener = listener;
      if (this.pipe != null) {
         this.pipe.setListener(listener);
      }

   }

   public void connect(DiscordBuild... preferredOrder) throws NoDiscordClientException {
      this.checkConnected(false);
      this.callbacks.clear();
      this.pipe = null;
      this.pipe = Pipe.openPipe(this, this.clientId, this.callbacks, preferredOrder);
      LOGGER.debug("Client is now connected and ready!");
      if (this.listener != null) {
         this.listener.onReady(this);
      }

      this.startReading();
   }

   public void sendRichPresence(RichPresence presence) {
      this.sendRichPresence(presence, (Callback)null);
   }

   public void sendRichPresence(RichPresence presence, Callback callback) {
      this.checkConnected(true);
      LOGGER.debug("Sending RichPresence to discord: " + (presence == null ? null : presence.toJson().toString()));
      this.pipe.send(Packet.OpCode.FRAME, (new JSONObject()).put("cmd", (Object)"SET_ACTIVITY").put("args", (Object)(new JSONObject()).put("pid", getPID()).put("activity", (Object)(presence == null ? null : presence.toJson()))), callback);
   }

   public void subscribe(IPCClient.Event sub) {
      this.subscribe(sub, (Callback)null);
   }

   public void subscribe(IPCClient.Event sub, Callback callback) {
      this.checkConnected(true);
      if (!sub.isSubscribable()) {
         throw new IllegalStateException("Cannot subscribe to " + sub + " event!");
      } else {
         LOGGER.debug(String.format("Subscribing to Event: %s", sub.name()));
         this.pipe.send(Packet.OpCode.FRAME, (new JSONObject()).put("cmd", (Object)"SUBSCRIBE").put("evt", (Object)sub.name()), callback);
      }
   }

   public PipeStatus getStatus() {
      return this.pipe == null ? PipeStatus.UNINITIALIZED : this.pipe.getStatus();
   }

   public void close() {
      this.checkConnected(true);

      try {
         this.pipe.close();
      } catch (IOException var2) {
         LOGGER.debug((String)"Failed to close pipe", (Throwable)var2);
      }

   }

   public DiscordBuild getDiscordBuild() {
      return this.pipe == null ? null : this.pipe.getDiscordBuild();
   }

   private void checkConnected(boolean connected) {
      if (connected && this.getStatus() != PipeStatus.CONNECTED) {
         throw new IllegalStateException(String.format("IPCClient (ID: %d) is not connected!", this.clientId));
      } else if (!connected && this.getStatus() == PipeStatus.CONNECTED) {
         throw new IllegalStateException(String.format("IPCClient (ID: %d) is already connected!", this.clientId));
      }
   }

   private void startReading() {
      this.readThread = new Thread(() -> {
         while(true) {
            try {
               Packet p;
               if ((p = this.pipe.read()).getOp() != Packet.OpCode.CLOSE) {
                  JSONObject json = p.getJson();
                  IPCClient.Event event = IPCClient.Event.of(json.optString("evt", (String)null));
                  String nonce = json.optString("nonce", (String)null);
                  switch(event) {
                  case NULL:
                     if (nonce != null && this.callbacks.containsKey(nonce)) {
                        ((Callback)this.callbacks.remove(nonce)).succeed(p);
                     }
                     break;
                  case ERROR:
                     if (nonce != null && this.callbacks.containsKey(nonce)) {
                        ((Callback)this.callbacks.remove(nonce)).fail(json.getJSONObject("data").optString("message", (String)null));
                     }
                     break;
                  case ACTIVITY_JOIN:
                     LOGGER.debug("Reading thread received a 'join' event.");
                     break;
                  case ACTIVITY_SPECTATE:
                     LOGGER.debug("Reading thread received a 'spectate' event.");
                     break;
                  case ACTIVITY_JOIN_REQUEST:
                     LOGGER.debug("Reading thread received a 'join request' event.");
                     break;
                  case UNKNOWN:
                     LOGGER.debug("Reading thread encountered an event with an unknown type: " + json.getString("evt"));
                  }

                  if (this.listener == null || !json.has("cmd") || !json.getString("cmd").equals("DISPATCH")) {
                     continue;
                  }

                  try {
                     JSONObject data = json.getJSONObject("data");
                     switch(IPCClient.Event.of(json.getString("evt"))) {
                     case ACTIVITY_JOIN:
                        this.listener.onActivityJoin(this, data.getString("secret"));
                        continue;
                     case ACTIVITY_SPECTATE:
                        this.listener.onActivitySpectate(this, data.getString("secret"));
                        continue;
                     case ACTIVITY_JOIN_REQUEST:
                        JSONObject u = data.getJSONObject("user");
                        User user = new User(u.getString("username"), u.getString("discriminator"), Long.parseLong(u.getString("id")), u.optString("avatar", (String)null));
                        this.listener.onActivityJoinRequest(this, data.optString("secret", (String)null), user);
                     }
                  } catch (Exception var8) {
                     LOGGER.error((String)"Exception when handling event: ", (Throwable)var8);
                  }
                  continue;
               }

               this.pipe.setStatus(PipeStatus.DISCONNECTED);
               if (this.listener != null) {
                  this.listener.onClose(this, p.getJson());
               }
            } catch (JSONException | IOException var9) {
               if (var9 instanceof IOException) {
                  LOGGER.error((String)"Reading thread encountered an IOException", (Throwable)var9);
               } else {
                  LOGGER.error((String)"Reading thread encountered an JSONException", (Throwable)var9);
               }

               this.pipe.setStatus(PipeStatus.DISCONNECTED);
               if (this.listener != null) {
                  this.listener.onDisconnect(this, var9);
               }
            }

            return;
         }
      });
      LOGGER.debug("Starting IPCClient reading thread!");
      this.readThread.start();
   }

   private static int getPID() {
      String pr = ManagementFactory.getRuntimeMXBean().getName();
      return Integer.parseInt(pr.substring(0, pr.indexOf(64)));
   }

   public static enum Event {
      NULL(false),
      READY(false),
      ERROR(false),
      ACTIVITY_JOIN(true),
      ACTIVITY_SPECTATE(true),
      ACTIVITY_JOIN_REQUEST(true),
      UNKNOWN(false);

      private final boolean subscribable;

      private Event(boolean subscribable) {
         this.subscribable = subscribable;
      }

      public boolean isSubscribable() {
         return this.subscribable;
      }

      static IPCClient.Event of(String str) {
         if (str == null) {
            return NULL;
         } else {
            IPCClient.Event[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               IPCClient.Event s = var1[var3];
               if (s != UNKNOWN && s.name().equalsIgnoreCase(str)) {
                  return s;
               }
            }

            return UNKNOWN;
         }
      }
   }
}
