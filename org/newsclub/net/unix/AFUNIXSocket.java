package org.newsclub.net.unix;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

public class AFUNIXSocket extends Socket {
   protected AFUNIXSocketImpl impl;
   AFUNIXSocketAddress addr;

   private AFUNIXSocket(AFUNIXSocketImpl impl) throws IOException {
      super(impl);

      try {
         NativeUnixSocket.setCreated(this);
      } catch (UnsatisfiedLinkError var3) {
         var3.printStackTrace();
      }

   }

   public static AFUNIXSocket newInstance() throws IOException {
      AFUNIXSocketImpl impl = new AFUNIXSocketImpl.Lenient();
      AFUNIXSocket instance = new AFUNIXSocket(impl);
      instance.impl = impl;
      return instance;
   }

   public static AFUNIXSocket newStrictInstance() throws IOException {
      AFUNIXSocketImpl impl = new AFUNIXSocketImpl();
      AFUNIXSocket instance = new AFUNIXSocket(impl);
      instance.impl = impl;
      return instance;
   }

   public static AFUNIXSocket connectTo(AFUNIXSocketAddress addr) throws IOException {
      AFUNIXSocket socket = newInstance();
      socket.connect(addr);
      return socket;
   }

   public void bind(SocketAddress bindpoint) throws IOException {
      super.bind(bindpoint);
      this.addr = (AFUNIXSocketAddress)bindpoint;
   }

   public void connect(SocketAddress endpoint) throws IOException {
      this.connect(endpoint, 0);
   }

   public void connect(SocketAddress endpoint, int timeout) throws IOException {
      if (!(endpoint instanceof AFUNIXSocketAddress)) {
         throw new IOException("Can only connect to endpoints of type " + AFUNIXSocketAddress.class.getName());
      } else {
         this.impl.connect(endpoint, timeout);
         this.addr = (AFUNIXSocketAddress)endpoint;
         NativeUnixSocket.setConnected(this);
      }
   }

   public String toString() {
      return this.isConnected() ? "AFUNIXSocket[fd=" + this.impl.getFD() + ";path=" + this.addr.getSocketFile() + "]" : "AFUNIXSocket[unconnected]";
   }

   public static boolean isSupported() {
      return NativeUnixSocket.isLoaded();
   }
}
