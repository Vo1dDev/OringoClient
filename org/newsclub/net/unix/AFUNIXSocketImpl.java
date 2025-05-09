package org.newsclub.net.unix;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;

class AFUNIXSocketImpl extends SocketImpl {
   private static final int SHUT_RD = 0;
   private static final int SHUT_WR = 1;
   private static final int SHUT_RD_WR = 2;
   private String socketFile;
   private boolean closed = false;
   private boolean bound = false;
   private boolean connected = false;
   private boolean closedInputStream = false;
   private boolean closedOutputStream = false;
   private final AFUNIXSocketImpl.AFUNIXInputStream in = new AFUNIXSocketImpl.AFUNIXInputStream();
   private final AFUNIXSocketImpl.AFUNIXOutputStream out = new AFUNIXSocketImpl.AFUNIXOutputStream();

   AFUNIXSocketImpl() {
      this.fd = new FileDescriptor();
   }

   FileDescriptor getFD() {
      return this.fd;
   }

   protected void accept(SocketImpl socket) throws IOException {
      AFUNIXSocketImpl si = (AFUNIXSocketImpl)socket;
      NativeUnixSocket.accept(this.socketFile, this.fd, si.fd);
      si.socketFile = this.socketFile;
      si.connected = true;
   }

   protected int available() throws IOException {
      return NativeUnixSocket.available(this.fd);
   }

   protected void bind(SocketAddress addr) throws IOException {
      this.bind(0, addr);
   }

   protected void bind(int backlog, SocketAddress addr) throws IOException {
      if (!(addr instanceof AFUNIXSocketAddress)) {
         throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
      } else {
         AFUNIXSocketAddress socketAddress = (AFUNIXSocketAddress)addr;
         this.socketFile = socketAddress.getSocketFile();
         NativeUnixSocket.bind(this.socketFile, this.fd, backlog);
         this.bound = true;
         this.localport = socketAddress.getPort();
      }
   }

   protected void bind(InetAddress host, int port) throws IOException {
      throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
   }

   private void checkClose() throws IOException {
      if (this.closedInputStream && this.closedOutputStream) {
      }

   }

   protected synchronized void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         if (this.fd.valid()) {
            NativeUnixSocket.shutdown(this.fd, 2);
            NativeUnixSocket.close(this.fd);
         }

         if (this.bound) {
            NativeUnixSocket.unlink(this.socketFile);
         }

         this.connected = false;
      }
   }

   protected void connect(String host, int port) throws IOException {
      throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
   }

   protected void connect(InetAddress address, int port) throws IOException {
      throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
   }

   protected void connect(SocketAddress addr, int timeout) throws IOException {
      if (!(addr instanceof AFUNIXSocketAddress)) {
         throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
      } else {
         AFUNIXSocketAddress socketAddress = (AFUNIXSocketAddress)addr;
         this.socketFile = socketAddress.getSocketFile();
         NativeUnixSocket.connect(this.socketFile, this.fd);
         this.address = socketAddress.getAddress();
         this.port = socketAddress.getPort();
         this.localport = 0;
         this.connected = true;
      }
   }

   protected void create(boolean stream) throws IOException {
   }

   protected InputStream getInputStream() throws IOException {
      if (!this.connected && !this.bound) {
         throw new IOException("Not connected/not bound");
      } else {
         return this.in;
      }
   }

   protected OutputStream getOutputStream() throws IOException {
      if (!this.connected && !this.bound) {
         throw new IOException("Not connected/not bound");
      } else {
         return this.out;
      }
   }

   protected void listen(int backlog) throws IOException {
      NativeUnixSocket.listen(this.fd, backlog);
   }

   protected void sendUrgentData(int data) throws IOException {
      NativeUnixSocket.write(this.fd, new byte[]{(byte)(data & 255)}, 0, 1);
   }

   public String toString() {
      return super.toString() + "[fd=" + this.fd + "; file=" + this.socketFile + "; connected=" + this.connected + "; bound=" + this.bound + "]";
   }

   private static int expectInteger(Object value) throws SocketException {
      try {
         return (Integer)value;
      } catch (ClassCastException var2) {
         throw new AFUNIXSocketException("Unsupported value: " + value, var2);
      } catch (NullPointerException var3) {
         throw new AFUNIXSocketException("Value must not be null", var3);
      }
   }

   private static int expectBoolean(Object value) throws SocketException {
      try {
         return (Boolean)value ? 1 : 0;
      } catch (ClassCastException var2) {
         throw new AFUNIXSocketException("Unsupported value: " + value, var2);
      } catch (NullPointerException var3) {
         throw new AFUNIXSocketException("Value must not be null", var3);
      }
   }

   public Object getOption(int optID) throws SocketException {
      try {
         switch(optID) {
         case 1:
         case 8:
            return NativeUnixSocket.getSocketOptionInt(this.fd, optID) != 0;
         case 128:
         case 4097:
         case 4098:
         case 4102:
            return NativeUnixSocket.getSocketOptionInt(this.fd, optID);
         default:
            throw new AFUNIXSocketException("Unsupported option: " + optID);
         }
      } catch (AFUNIXSocketException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new AFUNIXSocketException("Error while getting option", var4);
      }
   }

   public void setOption(int optID, Object value) throws SocketException {
      try {
         switch(optID) {
         case 1:
         case 8:
            NativeUnixSocket.setSocketOptionInt(this.fd, optID, expectBoolean(value));
            return;
         case 128:
            if (value instanceof Boolean) {
               boolean b = (Boolean)value;
               if (b) {
                  throw new SocketException("Only accepting Boolean.FALSE here");
               }

               NativeUnixSocket.setSocketOptionInt(this.fd, optID, -1);
               return;
            }

            NativeUnixSocket.setSocketOptionInt(this.fd, optID, expectInteger(value));
            return;
         case 4097:
         case 4098:
         case 4102:
            NativeUnixSocket.setSocketOptionInt(this.fd, optID, expectInteger(value));
            return;
         default:
            throw new AFUNIXSocketException("Unsupported option: " + optID);
         }
      } catch (AFUNIXSocketException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new AFUNIXSocketException("Error while setting option", var5);
      }
   }

   protected void shutdownInput() throws IOException {
      if (!this.closed && this.fd.valid()) {
         NativeUnixSocket.shutdown(this.fd, 0);
      }

   }

   protected void shutdownOutput() throws IOException {
      if (!this.closed && this.fd.valid()) {
         NativeUnixSocket.shutdown(this.fd, 1);
      }

   }

   static class Lenient extends AFUNIXSocketImpl {
      public void setOption(int optID, Object value) throws SocketException {
         try {
            super.setOption(optID, value);
         } catch (SocketException var4) {
            switch(optID) {
            case 1:
               return;
            default:
               throw var4;
            }
         }
      }

      public Object getOption(int optID) throws SocketException {
         try {
            return super.getOption(optID);
         } catch (SocketException var3) {
            switch(optID) {
            case 1:
            case 8:
               return false;
            default:
               throw var3;
            }
         }
      }
   }

   private final class AFUNIXOutputStream extends OutputStream {
      private boolean streamClosed;

      private AFUNIXOutputStream() {
         this.streamClosed = false;
      }

      public void write(int oneByte) throws IOException {
         byte[] buf1 = new byte[]{(byte)oneByte};
         this.write(buf1, 0, 1);
      }

      public void write(byte[] buf, int off, int len) throws IOException {
         if (this.streamClosed) {
            throw new AFUNIXSocketException("This OutputStream has already been closed.");
         } else if (len > buf.length - off) {
            throw new IndexOutOfBoundsException();
         } else {
            try {
               while(len > 0 && !Thread.interrupted()) {
                  int written = NativeUnixSocket.write(AFUNIXSocketImpl.this.fd, buf, off, len);
                  if (written == -1) {
                     throw new IOException("Unspecific error while writing");
                  }

                  len -= written;
                  off += written;
               }

            } catch (IOException var5) {
               throw (IOException)(new IOException(var5.getMessage() + " at " + AFUNIXSocketImpl.this.toString())).initCause(var5);
            }
         }
      }

      public void close() throws IOException {
         if (!this.streamClosed) {
            this.streamClosed = true;
            if (AFUNIXSocketImpl.this.fd.valid()) {
               NativeUnixSocket.shutdown(AFUNIXSocketImpl.this.fd, 1);
            }

            AFUNIXSocketImpl.this.closedOutputStream = true;
            AFUNIXSocketImpl.this.checkClose();
         }
      }

      // $FF: synthetic method
      AFUNIXOutputStream(Object x1) {
         this();
      }
   }

   private final class AFUNIXInputStream extends InputStream {
      private boolean streamClosed;

      private AFUNIXInputStream() {
         this.streamClosed = false;
      }

      public int read(byte[] buf, int off, int len) throws IOException {
         if (this.streamClosed) {
            throw new IOException("This InputStream has already been closed.");
         } else if (len == 0) {
            return 0;
         } else {
            int maxRead = buf.length - off;
            if (len > maxRead) {
               len = maxRead;
            }

            try {
               return NativeUnixSocket.read(AFUNIXSocketImpl.this.fd, buf, off, len);
            } catch (IOException var6) {
               throw (IOException)(new IOException(var6.getMessage() + " at " + AFUNIXSocketImpl.this.toString())).initCause(var6);
            }
         }
      }

      public int read() throws IOException {
         byte[] buf1 = new byte[1];
         int numRead = this.read(buf1, 0, 1);
         return numRead <= 0 ? -1 : buf1[0] & 255;
      }

      public void close() throws IOException {
         if (!this.streamClosed) {
            this.streamClosed = true;
            if (AFUNIXSocketImpl.this.fd.valid()) {
               NativeUnixSocket.shutdown(AFUNIXSocketImpl.this.fd, 0);
            }

            AFUNIXSocketImpl.this.closedInputStream = true;
            AFUNIXSocketImpl.this.checkClose();
         }
      }

      public int available() throws IOException {
         int av = NativeUnixSocket.available(AFUNIXSocketImpl.this.fd);
         return av;
      }

      // $FF: synthetic method
      AFUNIXInputStream(Object x1) {
         this();
      }
   }
}
