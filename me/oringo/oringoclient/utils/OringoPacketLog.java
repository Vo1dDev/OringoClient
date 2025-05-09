package me.oringo.oringoclient.utils;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class OringoPacketLog {
   private static JFrame frame = null;
   private static JTextArea in = null;
   private static JTextArea out = null;

   public static void start() {
      frame = new JFrame("Oringo Packet Log");
      frame.setAlwaysOnTop(true);
      frame.setSize(400, 200);
      JTabbedPane tabbedPane = new JTabbedPane();
      frame.add(tabbedPane);
      tabbedPane.addTab("In", in = new JTextArea());
      tabbedPane.addTab("Out", out = new JTextArea());
      frame.setVisible(true);
   }

   public static void logIn(String text) {
      in.setText(in.getText() + text + "\n");
      if (in.getText().length() - in.getText().replaceAll("\n", "").length() > 20) {
         in.setText(in.getText().substring(in.getText().indexOf("\n") + 1));
      }

   }

   public static void logOut(String text) {
      out.setText(out.getText() + text + "\n");
      if (out.getText().length() - out.getText().replaceAll("\n", "").length() > 20) {
         out.setText(out.getText().substring(out.getText().indexOf("\n") + 1));
      }

   }

   public static boolean isEnabled() {
      return frame != null && frame.isVisible();
   }

   public static JFrame getFrame() {
      return frame;
   }

   public static JTextArea getIn() {
      return in;
   }

   public static JTextArea getOut() {
      return out;
   }
}
