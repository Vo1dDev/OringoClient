package org.newsclub.net.unix;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class AFUNIXServerSocket extends ServerSocket {
   private final AFUNIXSocketImpl implementation = new AFUNIXSocketImpl();
   private AFUNIXSocketAddress boundEndpoint = null;
   private final Thread shutdownThread = new Thread() {
      public void run() {
         try {
            if (AFUNIXServerSocket.this.boundEndpoint != null) {
               NativeUnixSocket.unlink(AFUNIXServerSocket.this.boundEndpoint.getSocketFile());
            }
         } catch (IOException var2) {
         }

      }
   };

   protected AFUNIXServerSocket() throws IOException {
      NativeUnixSocket.initServerImpl(this, this.implementation);
      Runtime.getRuntime().addShutdownHook(this.shutdownThread);
      NativeUnixSocket.setCreatedServer(this);
   }

   public static AFUNIXServerSocket newInstance() throws IOException {
      AFUNIXServerSocket instance = new AFUNIXServerSocket();
      return instance;
   }

   public static AFUNIXServerSocket bindOn(AFUNIXSocketAddress addr) throws IOException {
      AFUNIXServerSocket socket = newInstance();
      socket.bind(addr);
      return socket;
   }

   public void bind(SocketAddress endpoint, int backlog) throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (this.isBound()) {
         throw new SocketException("Already bound");
      } else if (!(endpoint instanceof AFUNIXSocketAddress)) {
         throw new IOException("Can only bind to endpoints of type " + AFUNIXSocketAddress.class.getName());
      } else {
         this.implementation.bind(backlog, endpoint);
         this.boundEndpoint = (AFUNIXSocketAddress)endpoint;
      }
   }

   public boolean isBound() {
      return this.boundEndpoint != null;
   }

   public Socket accept() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else {
         AFUNIXSocket as = AFUNIXSocket.newInstance();
         this.implementation.accept(as.impl);
         as.addr = this.boundEndpoint;
         NativeUnixSocket.setConnected(as);
         return as;
      }
   }

   public String toString() {
      return !this.isBound() ? "AFUNIXServerSocket[unbound]" : "AFUNIXServerSocket[" + this.boundEndpoint.getSocketFile() + "]";
   }

   public void close() throws IOException {
      if (!this.isClosed()) {
         super.close();
         this.implementation.close();
         if (this.boundEndpoint != null) {
            NativeUnixSocket.unlink(this.boundEndpoint.getSocketFile());
         }

         try {
            Runtime.getRuntime().removeShutdownHook(this.shutdownThread);
         } catch (IllegalStateException var2) {
         }

      }
   }

   public static boolean isSupported() {
      return NativeUnixSocket.isLoaded();
   }
}
