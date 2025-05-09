package me.oringo.oringoclient.utils.font;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class MinecraftFontRenderer extends CFont {
   CFont.CharData[] boldChars;
   CFont.CharData[] italicChars;
   CFont.CharData[] boldItalicChars;
   int[] colorCode;
   String colorcodeIdentifiers;
   DynamicTexture texBold;
   DynamicTexture texItalic;
   DynamicTexture texItalicBold;

   public MinecraftFontRenderer(Font font) {
      this(font, true, false);
   }

   public MinecraftFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
      super(font, antiAlias, fractionalMetrics);
      this.boldChars = new CFont.CharData[256];
      this.italicChars = new CFont.CharData[256];
      this.boldItalicChars = new CFont.CharData[256];
      this.colorCode = new int[32];
      this.colorcodeIdentifiers = "0123456789abcdefklmnor";
      this.setupMinecraftColorcodes();
      this.setupBoldItalicIDs();
   }

   public int drawStringWithShadow(String text, double x2, double y2, int color) {
      float shadowWidth = this.drawString(text, x2 + 0.5D, y2 + 0.8999999761581421D, (new Color(20, 20, 20)).getRGB(), true, 8.3F);
      return (int)Math.max(shadowWidth, this.drawString(text, x2, y2, color, false, 8.3F));
   }

   public int drawSmoothStringWithShadow(String text, double x2, double y2, int color) {
      float shadowWidth = this.drawSmoothString(text, x2 + 0.5D, y2 + 0.8999999761581421D, (new Color(20, 20, 20)).getRGB(), true);
      return (int)Math.max(shadowWidth, this.drawSmoothString(text, x2, y2, color, false));
   }

   public int drawSmoothCenteredStringWithShadow(String text, double x2, double y2, int color) {
      float shadowWidth = this.drawSmoothString(text, x2 + 0.5D - (double)((float)(this.getStringWidth(text) / 2.0D)), y2 + 0.8999999761581421D, (new Color(20, 20, 20)).getRGB(), true);
      return (int)Math.max(shadowWidth, this.drawSmoothString(text, x2 - (double)((float)(this.getStringWidth(text) / 2.0D)), y2, color, false));
   }

   public int drawString(String text, double x2, double y2, int color) {
      return (int)this.drawString(text, x2, y2, color, false, 8.3F);
   }

   public int drawNoBSString(String text, double x2, float y2, int color) {
      return (int)this.drawNoBSString(text, x2, (double)y2, color, false);
   }

   public float drawSmoothString(String text, double x2, float y2, int color) {
      return this.drawSmoothString(text, x2, (double)y2, color, false);
   }

   public float drawSmoothCenteredString(String text, float x2, float y2, int color) {
      return this.drawSmoothString(text, (double)(x2 - (float)(this.getStringWidth(text) / 2.0D)), y2, color);
   }

   public float drawCenteredString(String text, float x2, float y2, int color) {
      return (float)this.drawString(text, (double)(x2 - (float)(this.getStringWidth(text) / 2.0D)), (double)y2, color);
   }

   public float drawNoBSCenteredString(String text, float x2, float y2, int color) {
      return (float)this.drawNoBSString(text, (double)(x2 - (float)(this.getStringWidth(text) / 2.0D)), y2, color);
   }

   public float drawCenteredStringWithShadow(String text, float x2, float y2, int color) {
      return (float)this.drawStringWithShadow(text, (double)(x2 - (float)(this.getStringWidth(text) / 2.0D)), (double)y2, color);
   }

   public float drawString(String text, double x, double y, int color, boolean shadow, float kerning) {
      --x;
      if (text == null) {
         return 0.0F;
      } else {
         if (color == 553648127) {
            color = 16777215;
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (shadow) {
            color = (color & 16579836) >> 2 | color & -16777216;
         }

         CFont.CharData[] currentData = this.charData;
         float alpha = (float)(color >> 24 & 255) / 255.0F;
         boolean randomCase = false;
         boolean bold = false;
         boolean italic = false;
         boolean strikethrough = false;
         boolean underline = false;
         boolean render = true;
         x *= 2.0D;
         y = (y - 3.0D) * 2.0D;
         GL11.glPushMatrix();
         GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
         GlStateManager.func_179147_l();
         GlStateManager.func_179112_b(770, 771);
         GlStateManager.func_179131_c((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
         GlStateManager.func_179098_w();
         GlStateManager.func_179144_i(this.tex.func_110552_b());
         GL11.glBindTexture(3553, this.tex.func_110552_b());

         for(int index = 0; index < text.length(); ++index) {
            char character = text.charAt(index);
            if (character == 167) {
               int colorIndex = 21;

               try {
                  colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
               } catch (Exception var21) {
                  var21.printStackTrace();
               }

               if (colorIndex < 16) {
                  bold = false;
                  italic = false;
                  underline = false;
                  strikethrough = false;
                  GlStateManager.func_179144_i(this.tex.func_110552_b());
                  currentData = this.charData;
                  if (colorIndex < 0) {
                     colorIndex = 15;
                  }

                  if (shadow) {
                     colorIndex += 16;
                  }

                  int colorcode = this.colorCode[colorIndex];
                  GlStateManager.func_179131_c((float)(colorcode >> 16 & 255) / 255.0F, (float)(colorcode >> 8 & 255) / 255.0F, (float)(colorcode & 255) / 255.0F, alpha);
               } else if (colorIndex == 17) {
                  bold = true;
                  if (italic) {
                     GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
                     currentData = this.boldItalicChars;
                  } else {
                     GlStateManager.func_179144_i(this.texBold.func_110552_b());
                     currentData = this.boldChars;
                  }
               } else if (colorIndex == 18) {
                  strikethrough = true;
               } else if (colorIndex == 19) {
                  underline = true;
               } else if (colorIndex == 20) {
                  italic = true;
                  if (bold) {
                     GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
                     currentData = this.boldItalicChars;
                  } else {
                     GlStateManager.func_179144_i(this.texItalic.func_110552_b());
                     currentData = this.italicChars;
                  }
               } else {
                  bold = false;
                  italic = false;
                  underline = false;
                  strikethrough = false;
                  GlStateManager.func_179131_c((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
                  GlStateManager.func_179144_i(this.tex.func_110552_b());
                  currentData = this.charData;
               }

               ++index;
            } else if (character < currentData.length) {
               GL11.glBegin(4);
               this.drawChar(currentData, character, (float)x, (float)y);
               GL11.glEnd();
               if (strikethrough) {
                  this.drawLine(x, y + (double)(currentData[character].height / 2), x + (double)currentData[character].width - 8.0D, y + (double)(currentData[character].height / 2), 1.0F);
               }

               if (underline) {
                  this.drawLine(x, y + (double)currentData[character].height - 2.0D, x + (double)currentData[character].width - 8.0D, y + (double)currentData[character].height - 2.0D, 1.0F);
               }

               x += (double)((float)currentData[character].width - kerning + (float)this.charOffset);
            }
         }

         GL11.glHint(3155, 4352);
         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)x / 2.0F;
      }
   }

   public float drawSmoothString(String text, double x, double y, int color, boolean shadow) {
      --x;
      if (text == null) {
         return 0.0F;
      } else {
         CFont.CharData[] currentData = this.charData;
         float alpha = (float)(color >> 24 & 255) / 255.0F;
         boolean randomCase = false;
         boolean bold = false;
         boolean italic = false;
         boolean strikethrough = false;
         boolean underline = false;
         boolean render = true;
         x *= 2.0D;
         y = (y - 3.0D) * 2.0D;
         GL11.glPushMatrix();
         GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
         GlStateManager.func_179147_l();
         GlStateManager.func_179112_b(770, 771);
         GlStateManager.func_179131_c((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
         GlStateManager.func_179098_w();
         GlStateManager.func_179144_i(this.tex.func_110552_b());
         GL11.glBindTexture(3553, this.tex.func_110552_b());
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);

         for(int index = 0; index < text.length(); ++index) {
            char character = text.charAt(index);
            if (character == 167) {
               if (index < text.length() - 1) {
                  int colorIndex = 21;

                  try {
                     colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
                  } catch (Exception var20) {
                     var20.printStackTrace();
                  }

                  if (colorIndex < 16) {
                     bold = false;
                     italic = false;
                     underline = false;
                     strikethrough = false;
                     GlStateManager.func_179144_i(this.tex.func_110552_b());
                     currentData = this.charData;
                     if (colorIndex < 0) {
                        colorIndex = 15;
                     }

                     int colorcode = this.colorCode[colorIndex];
                     if (!shadow) {
                        GlStateManager.func_179131_c((float)(colorcode >> 16 & 255) / 255.0F, (float)(colorcode >> 8 & 255) / 255.0F, (float)(colorcode & 255) / 255.0F, alpha);
                     }
                  } else if (colorIndex != 16) {
                     if (colorIndex == 17) {
                        bold = true;
                        if (italic) {
                           GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
                           GL11.glBindTexture(3553, this.texItalicBold.func_110552_b());
                           GL11.glTexParameteri(3553, 10241, 9729);
                           GL11.glTexParameteri(3553, 10240, 9729);
                           currentData = this.boldItalicChars;
                        } else {
                           GlStateManager.func_179144_i(this.texBold.func_110552_b());
                           GL11.glBindTexture(3553, this.texBold.func_110552_b());
                           GL11.glTexParameteri(3553, 10241, 9729);
                           GL11.glTexParameteri(3553, 10240, 9729);
                           currentData = this.boldChars;
                        }
                     } else if (colorIndex == 18) {
                        strikethrough = true;
                     } else if (colorIndex == 19) {
                        underline = true;
                     } else if (colorIndex == 20) {
                        italic = true;
                        if (bold) {
                           GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
                           GL11.glBindTexture(3553, this.texItalicBold.func_110552_b());
                           GL11.glTexParameteri(3553, 10241, 9729);
                           GL11.glTexParameteri(3553, 10240, 9729);
                           currentData = this.boldItalicChars;
                        } else {
                           GlStateManager.func_179144_i(this.texItalic.func_110552_b());
                           GL11.glBindTexture(3553, this.texItalic.func_110552_b());
                           GL11.glTexParameteri(3553, 10241, 9729);
                           GL11.glTexParameteri(3553, 10240, 9729);
                           currentData = this.italicChars;
                        }
                     } else {
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                        GlStateManager.func_179131_c((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
                        GlStateManager.func_179144_i(this.tex.func_110552_b());
                        currentData = this.charData;
                     }
                  }
               }

               ++index;
            } else if (character < currentData.length) {
               GL11.glBegin(4);
               this.drawChar(currentData, character, (float)x, (float)y);
               GL11.glEnd();
               if (strikethrough) {
                  this.drawLine(x, y + (double)(currentData[character].height / 2), x + (double)currentData[character].width - 8.0D, y + (double)(currentData[character].height / 2), 1.0F);
               }

               if (underline) {
                  this.drawLine(x, y + (double)currentData[character].height - 2.0D, x + (double)currentData[character].width - 8.0D, y + (double)currentData[character].height - 2.0D, 1.0F);
               }

               x += (double)((float)currentData[character].width - 8.3F + (float)this.charOffset);
            }
         }

         GL11.glHint(3155, 4352);
         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)x / 2.0F;
      }
   }

   public float drawSmoothCenteredString(String text, float x2, float y2, int topColor, int bottomColor) {
      return this.drawSmoothMultiColoredStringTB(text, (double)(x2 - (float)(this.getStringWidth(text) / 2.0D)), (double)y2, topColor, bottomColor, false);
   }

   public float drawSmoothMultiColoredStringTB(String text, double x, double y, int topColor, int bottomColor, boolean shadow) {
      --x;
      if (text == null) {
         return 0.0F;
      } else {
         CFont.CharData[] currentData = this.charData;
         x *= 2.0D;
         y = (y - 3.0D) * 2.0D;
         GL11.glPushMatrix();
         GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
         GlStateManager.func_179147_l();
         GlStateManager.func_179112_b(770, 771);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179098_w();
         GlStateManager.func_179144_i(this.tex.func_110552_b());
         GL11.glBindTexture(3553, this.tex.func_110552_b());
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glShadeModel(7425);

         for(int index = 0; index < text.length(); ++index) {
            char character = text.charAt(index);
            if (character < currentData.length) {
               GL11.glShadeModel(7425);
               GL11.glBegin(4);
               this.drawMultiColoredCharTB(currentData, character, (float)x, (float)y, topColor, bottomColor);
               GL11.glEnd();
               GL11.glShadeModel(7424);
               x += (double)((float)currentData[character].width - 8.3F + (float)this.charOffset);
            }
         }

         GL11.glHint(3155, 4352);
         GL11.glShadeModel(7424);
         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)x / 2.0F;
      }
   }

   public float drawSmoothMultiColoredStringLR(String text, double x, double y, int leftColor, int rightColor) {
      --x;
      if (text == null) {
         return 0.0F;
      } else {
         CFont.CharData[] currentData = this.charData;
         x *= 2.0D;
         y = (y - 3.0D) * 2.0D;
         GL11.glPushMatrix();
         GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
         GlStateManager.func_179147_l();
         GlStateManager.func_179112_b(770, 771);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179098_w();
         GlStateManager.func_179144_i(this.tex.func_110552_b());
         GL11.glBindTexture(3553, this.tex.func_110552_b());
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glShadeModel(7425);
         Color color1 = new Color(leftColor);
         Color color2 = new Color(rightColor);

         for(int index = 0; index < text.length(); ++index) {
            char character = text.charAt(index);
            if (character < currentData.length) {
               float point = (float)index / (float)text.length();
               float point2 = (float)(index + 1) / (float)text.length();
               GL11.glShadeModel(7425);
               GL11.glBegin(4);
               this.drawMulticoloredCharLR(currentData, character, (float)x, (float)y, RenderUtils.interpolateColor(color1, color2, point).getRGB(), RenderUtils.interpolateColor(color1, color2, point2).getRGB());
               GL11.glEnd();
               GL11.glShadeModel(7424);
               x += (double)((float)currentData[character].width - 8.3F + (float)this.charOffset);
            }
         }

         GL11.glHint(3155, 4352);
         GL11.glShadeModel(7424);
         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)x / 2.0F;
      }
   }

   public float drawNoBSString(String text, double x, double y, int color, boolean shadow) {
      --x;
      if (text == null) {
         return 0.0F;
      } else {
         CFont.CharData[] currentData = this.charData;
         float alpha = (float)(color >> 24 & 255) / 255.0F;
         boolean randomCase = false;
         boolean bold = false;
         boolean italic = false;
         boolean strikethrough = false;
         boolean underline = false;
         boolean render = true;
         x *= 2.0D;
         y = (y - 3.0D) * 2.0D;
         GL11.glPushMatrix();
         GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
         GlStateManager.func_179147_l();
         GlStateManager.func_179112_b(770, 771);
         GlStateManager.func_179131_c((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
         GlStateManager.func_179098_w();
         GlStateManager.func_179144_i(this.tex.func_110552_b());
         GL11.glBindTexture(3553, this.tex.func_110552_b());
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);

         for(int index = 0; index < text.length(); ++index) {
            char character = text.charAt(index);
            if (character == 167) {
               int colorIndex = 21;

               try {
                  colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
               } catch (Exception var20) {
                  var20.printStackTrace();
               }

               if (colorIndex < 16) {
                  bold = false;
                  italic = false;
                  underline = false;
                  strikethrough = false;
                  GlStateManager.func_179144_i(this.tex.func_110552_b());
                  currentData = this.charData;
                  if (colorIndex < 0) {
                     colorIndex = 15;
                  }

                  if (shadow) {
                     colorIndex += 16;
                  }

                  int colorcode = this.colorCode[colorIndex];
                  GlStateManager.func_179131_c((float)(colorcode >> 16 & 255) / 255.0F, (float)(colorcode >> 8 & 255) / 255.0F, (float)(colorcode & 255) / 255.0F, alpha);
               } else if (colorIndex != 16) {
                  if (colorIndex == 17) {
                     bold = true;
                     if (italic) {
                        GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
                        currentData = this.boldItalicChars;
                     } else {
                        GlStateManager.func_179144_i(this.texBold.func_110552_b());
                        currentData = this.boldChars;
                     }
                  } else if (colorIndex == 18) {
                     strikethrough = true;
                  } else if (colorIndex == 19) {
                     underline = true;
                  } else if (colorIndex == 20) {
                     italic = true;
                     if (bold) {
                        GlStateManager.func_179144_i(this.texItalicBold.func_110552_b());
                        currentData = this.boldItalicChars;
                     } else {
                        GlStateManager.func_179144_i(this.texItalic.func_110552_b());
                        currentData = this.italicChars;
                     }
                  } else {
                     bold = false;
                     italic = false;
                     underline = false;
                     strikethrough = false;
                     GlStateManager.func_179131_c((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
                     GlStateManager.func_179144_i(this.tex.func_110552_b());
                     currentData = this.charData;
                  }
               }

               ++index;
            } else if (character < currentData.length) {
               GL11.glBegin(4);
               this.drawChar(currentData, character, (float)x, (float)y);
               GL11.glEnd();
               if (strikethrough) {
                  this.drawLine(x, y + (double)(currentData[character].height / 2), x + (double)currentData[character].width - 8.0D, y + (double)(currentData[character].height / 2), 1.0F);
               }

               if (underline) {
                  this.drawLine(x, y + (double)currentData[character].height - 2.0D, x + (double)currentData[character].width - 8.0D, y + (double)currentData[character].height - 2.0D, 1.0F);
               }

               x += (double)((float)currentData[character].width - 8.3F + (float)this.charOffset);
            }
         }

         GL11.glHint(3155, 4352);
         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         return (float)x / 2.0F;
      }
   }

   public double getStringWidth(String text) {
      if (text == null) {
         return 0.0D;
      } else {
         float width = 0.0F;
         CFont.CharData[] currentData = this.charData;
         boolean bold = false;
         boolean italic = false;

         for(int index = 0; index < text.length(); ++index) {
            char character = text.charAt(index);
            if (character == 167) {
               int colorIndex = "0123456789abcdefklmnor".indexOf(character);
               ++index;
            } else if (character < currentData.length) {
               width += (float)currentData[character].width - 8.3F + (float)this.charOffset;
            }
         }

         return (double)(width / 2.0F);
      }
   }

   public double getStringWidth(String text, float kerning) {
      if (text == null) {
         return 0.0D;
      } else {
         float width = 0.0F;
         CFont.CharData[] currentData = this.charData;
         boolean bold = false;
         boolean italic = false;

         for(int index = 0; index < text.length(); ++index) {
            char c = text.charAt(index);
            if (c == 167) {
               int colorIndex = "0123456789abcdefklmnor".indexOf(c);
               ++index;
            } else if (c < currentData.length) {
               width += (float)currentData[c].width - kerning + (float)this.charOffset;
            }
         }

         return (double)(width / 2.0F);
      }
   }

   public int getHeight() {
      return (this.fontHeight - 8) / 2;
   }

   public void setFont(Font font) {
      super.setFont(font);
      this.setupBoldItalicIDs();
   }

   public void setAntiAlias(boolean antiAlias) {
      super.setAntiAlias(antiAlias);
      this.setupBoldItalicIDs();
   }

   public void setFractionalMetrics(boolean fractionalMetrics) {
      super.setFractionalMetrics(fractionalMetrics);
      this.setupBoldItalicIDs();
   }

   private void setupBoldItalicIDs() {
      this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
      this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
      this.texItalicBold = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
   }

   private void drawLine(double x2, double y2, double x1, double y1, float width) {
      GL11.glDisable(3553);
      GL11.glLineWidth(width);
      GL11.glBegin(1);
      GL11.glVertex2d(x2, y2);
      GL11.glVertex2d(x1, y1);
      GL11.glEnd();
      GL11.glEnable(3553);
   }

   public List<String> wrapWords(String text, double width) {
      ArrayList<String> finalWords = new ArrayList();
      if (this.getStringWidth(text) > width) {
         String[] words = text.split(" ");
         StringBuilder currentWord = new StringBuilder();
         char lastColorCode = '\uffff';
         String[] var8 = words;
         int var9 = words.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            String word = var8[var10];

            for(int innerIndex = 0; innerIndex < word.toCharArray().length; ++innerIndex) {
               char c = word.toCharArray()[innerIndex];
               if (c == 167 && innerIndex < word.toCharArray().length - 1) {
                  lastColorCode = word.toCharArray()[innerIndex + 1];
               }
            }

            if (this.getStringWidth(currentWord + word + " ") < width) {
               currentWord.append(word).append(" ");
            } else {
               finalWords.add(currentWord.toString());
               currentWord = new StringBuilder("§" + lastColorCode + word + " ");
            }
         }

         if (currentWord.length() > 0) {
            if (this.getStringWidth(currentWord.toString()) < width) {
               finalWords.add("§" + lastColorCode + currentWord + " ");
               new StringBuilder();
            } else {
               finalWords.addAll(this.formatString(currentWord.toString(), width));
            }
         }
      } else {
         finalWords.add(text);
      }

      return finalWords;
   }

   public List<String> formatString(String string, double width) {
      ArrayList<String> finalWords = new ArrayList();
      StringBuilder currentWord = new StringBuilder();
      char lastColorCode = '\uffff';
      char[] chars = string.toCharArray();

      for(int index = 0; index < chars.length; ++index) {
         char c = chars[index];
         if (c == 167 && index < chars.length - 1) {
            lastColorCode = chars[index + 1];
         }

         if (this.getStringWidth(currentWord.toString() + c) < width) {
            currentWord.append(c);
         } else {
            finalWords.add(currentWord.toString());
            currentWord = new StringBuilder("§" + lastColorCode + c);
         }
      }

      if (currentWord.length() > 0) {
         finalWords.add(currentWord.toString());
      }

      return finalWords;
   }

   private void setupMinecraftColorcodes() {
      for(int index = 0; index < 32; ++index) {
         int noClue = (index >> 3 & 1) * 85;
         int red = (index >> 2 & 1) * 170 + noClue;
         int green = (index >> 1 & 1) * 170 + noClue;
         int blue = (index & 1) * 170 + noClue;
         if (index == 6) {
            red += 85;
         }

         if (index >= 16) {
            red /= 4;
            green /= 4;
            blue /= 4;
         }

         this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
      }

   }

   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   private float getCharWidthFloat(char c) {
      if (c == 167) {
         return -1.0F;
      } else if (c == ' ') {
         return 2.0F;
      } else if (this.charData[c] == null) {
         return 0.0F;
      } else {
         int var2 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(c);
         if (c > 0 && var2 != -1) {
            return (float)this.charData[var2].width / 2.0F - 4.0F;
         } else if ((float)this.charData[c].width / 2.0F - 4.0F != 0.0F) {
            int var3 = (int)((float)this.charData[c].width / 2.0F - 4.0F) >>> 4;
            int var4 = (int)((float)this.charData[c].width / 2.0F - 4.0F) & 15;
            var3 &= 15;
            ++var4;
            return (float)((var4 - var3) / 2 + 1);
         } else {
            return 0.0F;
         }
      }
   }

   public String trimStringToWidth(String text, int width, boolean custom) {
      StringBuilder buffer = new StringBuilder();
      float lineWidth = 0.0F;
      int offset = custom ? text.length() - 1 : 0;
      int increment = custom ? -1 : 1;
      boolean var8 = false;
      boolean var9 = false;

      for(int index = offset; index >= 0 && index < text.length() && lineWidth < (float)width; index += increment) {
         char character = text.charAt(index);
         float charWidth = this.getCharWidthFloat(character);
         if (var8) {
            var8 = false;
            if (character != 'l' && character != 'L') {
               if (character == 'r' || character == 'R') {
                  var9 = false;
               }
            } else {
               var9 = true;
            }
         } else if (charWidth < 0.0F) {
            var8 = true;
         } else {
            lineWidth += charWidth;
            if (var9) {
               ++lineWidth;
            }
         }

         if (lineWidth > (float)width) {
            break;
         }

         if (custom) {
            buffer.insert(0, character);
         } else {
            buffer.append(character);
         }
      }

      return buffer.toString();
   }
}
