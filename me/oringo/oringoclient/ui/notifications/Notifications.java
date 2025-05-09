package me.oringo.oringoclient.ui.notifications;

import java.awt.Color;
import java.util.ArrayList;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.lwjgl.opengl.GL11;

public class Notifications {
   private static final ArrayList<Notifications.Notification> notifications = new ArrayList();

   @SubscribeEvent
   public void onRender(RenderTickEvent event) {
      if (event.phase == Phase.END) {
         GL11.glPushMatrix();
         notifications.removeIf((n) -> {
            return n.getEnd() <= System.currentTimeMillis();
         });
         if (notifications.size() > 0) {
            ScaledResolution res = new ScaledResolution(OringoClient.mc);
            float y = (float)(res.func_78328_b() - 37);

            for(int i = 0; i < notifications.size(); ++i) {
               Notifications.Notification notification = (Notifications.Notification)notifications.get(i);
               GL11.glPushMatrix();
               float width = (float)Math.max(150.0D, Fonts.robotoMediumBold.getStringWidth(notification.getDescription()) + 10.0D);
               float height = 35.0F;
               float x = (float)res.func_78326_a() - width - 2.0F;
               long time;
               if (notification.getCurrentTime() <= 250L) {
                  if (notification.getCurrentTime() >= 100L) {
                     x = (float)((double)x + (double)(250L - notification.getCurrentTime()) / 150.0D * (double)(width + 2.0F));
                  } else {
                     x += 10000.0F;
                  }
               } else if (notification.getEnd() - System.currentTimeMillis() <= 250L) {
                  time = notification.getEnd() - System.currentTimeMillis();
                  if (time >= 100L) {
                     x = (float)((double)x + (double)(250L - time) / 150.0D * (double)(width + 2.0F));
                  } else {
                     x += 10000.0F;
                  }
               }

               RenderUtils.drawRoundedRect((double)x, (double)y, (double)(x + width), (double)(y + height), 3.0D, (new Color(21, 21, 21, 90)).getRGB());
               Fonts.robotoBig.drawSmoothStringWithShadow(notification.getTitle(), (double)(x + 3.0F), (double)(y + 5.0F), notification.getColor().getRGB());
               Fonts.robotoMediumBold.drawSmoothStringWithShadow(notification.getDescription(), (double)(x + 5.0F), (double)(y + 10.0F + (float)Fonts.robotoBig.getHeight()), Color.white.getRGB());
               RenderUtils.drawRect(x, y + height - 2.0F, x + width * (float)notification.getCurrentTime() / (float)notification.getTime(), y + height, notification.getColor().getRGB());
               if (notification.getCurrentTime() < 100L) {
                  height = (float)((double)height * ((double)notification.getCurrentTime() / 100.0D));
               } else if (notification.getEnd() - System.currentTimeMillis() < 100L) {
                  time = notification.getEnd() - System.currentTimeMillis();
                  height = (float)((double)height * ((double)time / 100.0D));
               }

               y -= height + 1.0F;
               GL11.glPopMatrix();
            }
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
      }

   }

   public static void showNotification(String description, int time) {
      showNotification("Oringo Client", description, time);
   }

   public static void showNotification(String title, String description, int time) {
      showNotification(description, time, Notifications.NotificationType.INFO);
   }

   public static void showNotification(String description, int time, Notifications.NotificationType type) {
      notifications.add(new Notifications.Notification(description, time, type));
   }

   public static enum NotificationType {
      WARNING("Warning", new Color(255, 204, 0)),
      INFO("Notification", Color.white),
      ERROR("Error", new Color(208, 3, 3));

      private final String name;
      private final Color color;

      private NotificationType(String name, Color color) {
         this.name = name;
         this.color = color;
      }

      public String getName() {
         return this.name;
      }

      public Color getColor(int i) {
         return this == INFO ? OringoClient.clickGui.getColor(i) : this.color;
      }
   }

   private static class Notification {
      private final Notifications.NotificationType type;
      private final String description;
      private final long end;
      private final int time;
      private final int colorIndex;

      public Notification(String description, int time, Notifications.NotificationType type) {
         time += 500;
         this.description = description;
         this.end = System.currentTimeMillis() + (long)time;
         this.time = time;
         this.type = type;
         this.colorIndex = Notifications.notifications.size() + 1;
      }

      public int getTime() {
         return this.time;
      }

      public String getTitle() {
         return this.type.getName();
      }

      public long getCurrentTime() {
         return System.currentTimeMillis() - this.end + (long)this.time;
      }

      public String getDescription() {
         return this.description;
      }

      public long getEnd() {
         return this.end;
      }

      public Color getColor() {
         return this.type.getColor(this.colorIndex);
      }
   }
}
