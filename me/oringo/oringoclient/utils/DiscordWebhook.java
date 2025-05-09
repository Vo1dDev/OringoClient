package me.oringo.oringoclient.utils;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;

public class DiscordWebhook {
   private final String url;
   private String content;
   private String username;
   private String avatarUrl;
   private boolean tts;
   private List<DiscordWebhook.EmbedObject> embeds = new ArrayList();

   public DiscordWebhook(String url) {
      this.url = url;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
   }

   public void setTts(boolean tts) {
      this.tts = tts;
   }

   public void addEmbed(DiscordWebhook.EmbedObject embed) {
      this.embeds.add(embed);
   }

   public void execute() throws IOException {
      if (this.content == null && this.embeds.isEmpty()) {
         throw new IllegalArgumentException("Set content or add at least one EmbedObject");
      } else {
         DiscordWebhook.JSONObject json = new DiscordWebhook.JSONObject();
         json.put("content", this.content);
         json.put("username", this.username);
         json.put("avatar_url", this.avatarUrl);
         json.put("tts", this.tts);
         if (!this.embeds.isEmpty()) {
            List<DiscordWebhook.JSONObject> embedObjects = new ArrayList();
            Iterator var3 = this.embeds.iterator();

            while(var3.hasNext()) {
               DiscordWebhook.EmbedObject embed = (DiscordWebhook.EmbedObject)var3.next();
               DiscordWebhook.JSONObject jsonEmbed = new DiscordWebhook.JSONObject();
               jsonEmbed.put("title", embed.getTitle());
               jsonEmbed.put("description", embed.getDescription());
               jsonEmbed.put("url", embed.getUrl());
               if (embed.getColor() != null) {
                  Color color = embed.getColor();
                  int rgb = color.getRed();
                  rgb = (rgb << 8) + color.getGreen();
                  rgb = (rgb << 8) + color.getBlue();
                  jsonEmbed.put("color", rgb);
               }

               DiscordWebhook.EmbedObject.Footer footer = embed.getFooter();
               DiscordWebhook.EmbedObject.Image image = embed.getImage();
               DiscordWebhook.EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
               DiscordWebhook.EmbedObject.Author author = embed.getAuthor();
               List<DiscordWebhook.EmbedObject.Field> fields = embed.getFields();
               DiscordWebhook.JSONObject jsonAuthor;
               if (footer != null) {
                  jsonAuthor = new DiscordWebhook.JSONObject();
                  jsonAuthor.put("text", footer.getText());
                  jsonAuthor.put("icon_url", footer.getIconUrl());
                  jsonEmbed.put("footer", jsonAuthor);
               }

               if (image != null) {
                  jsonAuthor = new DiscordWebhook.JSONObject();
                  jsonAuthor.put("url", image.getUrl());
                  jsonEmbed.put("image", jsonAuthor);
               }

               if (thumbnail != null) {
                  jsonAuthor = new DiscordWebhook.JSONObject();
                  jsonAuthor.put("url", thumbnail.getUrl());
                  jsonEmbed.put("thumbnail", jsonAuthor);
               }

               if (author != null) {
                  jsonAuthor = new DiscordWebhook.JSONObject();
                  jsonAuthor.put("name", author.getName());
                  jsonAuthor.put("url", author.getUrl());
                  jsonAuthor.put("icon_url", author.getIconUrl());
                  jsonEmbed.put("author", jsonAuthor);
               }

               List<DiscordWebhook.JSONObject> jsonFields = new ArrayList();
               Iterator var12 = fields.iterator();

               while(var12.hasNext()) {
                  DiscordWebhook.EmbedObject.Field field = (DiscordWebhook.EmbedObject.Field)var12.next();
                  DiscordWebhook.JSONObject jsonField = new DiscordWebhook.JSONObject();
                  jsonField.put("name", field.getName());
                  jsonField.put("value", field.getValue());
                  jsonField.put("inline", field.isInline());
                  jsonFields.add(jsonField);
               }

               jsonEmbed.put("fields", jsonFields.toArray());
               embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
         }

         URL url = new URL(this.url);
         HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
         connection.setConnectTimeout(150000);
         connection.addRequestProperty("Content-Type", "application/json");
         connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
         connection.setDoOutput(true);
         connection.setRequestMethod("POST");
         OutputStream stream = connection.getOutputStream();
         stream.write(json.toString().getBytes());
         stream.flush();
         stream.close();
         connection.getInputStream().close();
         connection.disconnect();
      }
   }

   private class JSONObject {
      private final HashMap<String, Object> map;

      private JSONObject() {
         this.map = new HashMap();
      }

      void put(String key, Object value) {
         if (value != null) {
            this.map.put(key, value);
         }

      }

      public String toString() {
         StringBuilder builder = new StringBuilder();
         Set<Entry<String, Object>> entrySet = this.map.entrySet();
         builder.append("{");
         int i = 0;
         Iterator var4 = entrySet.iterator();

         while(var4.hasNext()) {
            Entry<String, Object> entry = (Entry)var4.next();
            Object val = entry.getValue();
            builder.append(this.quote((String)entry.getKey())).append(":");
            if (val instanceof String) {
               builder.append(this.quote(String.valueOf(val)));
            } else if (val instanceof Integer) {
               builder.append(Integer.valueOf(String.valueOf(val)));
            } else if (val instanceof Boolean) {
               builder.append(val);
            } else if (val instanceof DiscordWebhook.JSONObject) {
               builder.append(val.toString());
            } else if (val.getClass().isArray()) {
               builder.append("[");
               int len = Array.getLength(val);

               for(int j = 0; j < len; ++j) {
                  builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
               }

               builder.append("]");
            }

            ++i;
            builder.append(i == entrySet.size() ? "}" : ",");
         }

         return builder.toString();
      }

      private String quote(String string) {
         return "\"" + string + "\"";
      }

      // $FF: synthetic method
      JSONObject(Object x1) {
         this();
      }
   }

   public static class EmbedObject {
      private String title;
      private String description;
      private String url;
      private Color color;
      private DiscordWebhook.EmbedObject.Footer footer;
      private DiscordWebhook.EmbedObject.Thumbnail thumbnail;
      private DiscordWebhook.EmbedObject.Image image;
      private DiscordWebhook.EmbedObject.Author author;
      private List<DiscordWebhook.EmbedObject.Field> fields = new ArrayList();

      public String getTitle() {
         return this.title;
      }

      public String getDescription() {
         return this.description;
      }

      public String getUrl() {
         return this.url;
      }

      public Color getColor() {
         return this.color;
      }

      public DiscordWebhook.EmbedObject.Footer getFooter() {
         return this.footer;
      }

      public DiscordWebhook.EmbedObject.Thumbnail getThumbnail() {
         return this.thumbnail;
      }

      public DiscordWebhook.EmbedObject.Image getImage() {
         return this.image;
      }

      public DiscordWebhook.EmbedObject.Author getAuthor() {
         return this.author;
      }

      public List<DiscordWebhook.EmbedObject.Field> getFields() {
         return this.fields;
      }

      public DiscordWebhook.EmbedObject setTitle(String title) {
         this.title = title;
         return this;
      }

      public DiscordWebhook.EmbedObject setDescription(String description) {
         this.description = description;
         return this;
      }

      public DiscordWebhook.EmbedObject setUrl(String url) {
         this.url = url;
         return this;
      }

      public DiscordWebhook.EmbedObject setColor(Color color) {
         this.color = color;
         return this;
      }

      public DiscordWebhook.EmbedObject setFooter(String text, String icon) {
         this.footer = new DiscordWebhook.EmbedObject.Footer(text, icon);
         return this;
      }

      public DiscordWebhook.EmbedObject setThumbnail(String url) {
         this.thumbnail = new DiscordWebhook.EmbedObject.Thumbnail(url);
         return this;
      }

      public DiscordWebhook.EmbedObject setImage(String url) {
         this.image = new DiscordWebhook.EmbedObject.Image(url);
         return this;
      }

      public DiscordWebhook.EmbedObject setAuthor(String name, String url, String icon) {
         this.author = new DiscordWebhook.EmbedObject.Author(name, url, icon);
         return this;
      }

      public DiscordWebhook.EmbedObject addField(String name, String value, boolean inline) {
         this.fields.add(new DiscordWebhook.EmbedObject.Field(name, value, inline));
         return this;
      }

      private class Field {
         private String name;
         private String value;
         private boolean inline;

         private Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
         }

         private String getName() {
            return this.name;
         }

         private String getValue() {
            return this.value;
         }

         private boolean isInline() {
            return this.inline;
         }

         // $FF: synthetic method
         Field(String x1, String x2, boolean x3, Object x4) {
            this(x1, x2, x3);
         }
      }

      private class Author {
         private String name;
         private String url;
         private String iconUrl;

         private Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
         }

         private String getName() {
            return this.name;
         }

         private String getUrl() {
            return this.url;
         }

         private String getIconUrl() {
            return this.iconUrl;
         }

         // $FF: synthetic method
         Author(String x1, String x2, String x3, Object x4) {
            this(x1, x2, x3);
         }
      }

      private class Image {
         private String url;

         private Image(String url) {
            this.url = url;
         }

         private String getUrl() {
            return this.url;
         }

         // $FF: synthetic method
         Image(String x1, Object x2) {
            this(x1);
         }
      }

      private class Thumbnail {
         private String url;

         private Thumbnail(String url) {
            this.url = url;
         }

         private String getUrl() {
            return this.url;
         }

         // $FF: synthetic method
         Thumbnail(String x1, Object x2) {
            this(x1);
         }
      }

      private class Footer {
         private String text;
         private String iconUrl;

         private Footer(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
         }

         private String getText() {
            return this.text;
         }

         private String getIconUrl() {
            return this.iconUrl;
         }

         // $FF: synthetic method
         Footer(String x1, String x2, Object x3) {
            this(x1, x2);
         }
      }
   }
}
