package com.jagrosh.discordipc.entities.pipe;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.Packet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnixPipe extends Pipe {
   private static final Logger LOGGER = LoggerFactory.getLogger(UnixPipe.class);
   private final AFUNIXSocket socket = AFUNIXSocket.newInstance();

   UnixPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, String location) throws IOException {
      super(ipcClient, callbacks);
      this.socket.connect(new AFUNIXSocketAddress(new File(location)));
   }

   public Packet read() throws IOException, JSONException {
      InputStream is = this.socket.getInputStream();

      while((this.status == PipeStatus.CONNECTED || this.status == PipeStatus.CLOSING) && is.available() == 0) {
         try {
            Thread.sleep(50L);
         } catch (InterruptedException var6) {
         }
      }

      if (this.status == PipeStatus.DISCONNECTED) {
         throw new IOException("Disconnected!");
      } else if (this.status == PipeStatus.CLOSED) {
         return new Packet(Packet.OpCode.CLOSE, (JSONObject)null);
      } else {
         byte[] d = new byte[8];
         is.read(d);
         ByteBuffer bb = ByteBuffer.wrap(d);
         Packet.OpCode op = Packet.OpCode.values()[Integer.reverseBytes(bb.getInt())];
         d = new byte[Integer.reverseBytes(bb.getInt())];
         is.read(d);
         Packet p = new Packet(op, new JSONObject(new String(d)));
         LOGGER.debug(String.format("Received packet: %s", p.toString()));
         if (this.listener != null) {
            this.listener.onPacketReceived(this.ipcClient, p);
         }

         return p;
      }
   }

   public void write(byte[] b) throws IOException {
      this.socket.getOutputStream().write(b);
   }

   public void close() throws IOException {
      LOGGER.debug("Closing IPC pipe...");
      this.status = PipeStatus.CLOSING;
      this.send(Packet.OpCode.CLOSE, new JSONObject(), (Callback)null);
      this.status = PipeStatus.CLOSED;
      this.socket.close();
   }
}
