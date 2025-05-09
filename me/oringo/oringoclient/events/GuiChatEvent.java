package me.oringo.oringoclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class GuiChatEvent extends Event {
   public int mouseX;
   public int mouseY;
   public int keyCode;
   public char keyChar;

   protected GuiChatEvent(int mouseX, int mouseY, int keyCode, char keyChar) {
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.keyCode = keyCode;
      this.keyChar = keyChar;
   }

   public static class Closed extends GuiChatEvent {
      public Closed() {
         super(0, 0, -1, '\u0000');
      }
   }

   public static class MouseReleased extends GuiChatEvent {
      public MouseReleased(int mouseX, int mouseY, int keyCode) {
         super(mouseX, mouseY, keyCode, '\u0000');
      }
   }

   public static class MouseClicked extends GuiChatEvent {
      public MouseClicked(int mouseX, int mouseY, int keyCode) {
         super(mouseX, mouseY, keyCode, '\u0000');
      }
   }

   public static class KeyTyped extends GuiChatEvent {
      public KeyTyped(int keyCode, char keyChar) {
         super(0, 0, keyCode, keyChar);
      }
   }

   public static class DrawChatEvent extends GuiChatEvent {
      public DrawChatEvent(int mouseX, int mouseY) {
         super(mouseX, mouseY, -1, '\u0000');
      }
   }
}
