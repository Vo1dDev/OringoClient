package me.oringo.oringoclient.qolfeatures.module.impl.render;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.ScoreboardRenderEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.StencilUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import me.oringo.oringoclient.utils.shader.BlurUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomInterfaces extends Module {
   public BooleanSetting customScoreboard = new BooleanSetting("Custom Scoreboard", true);
   public BooleanSetting customFont = new BooleanSetting("Custom Font", true) {
      public boolean isHidden() {
         return !CustomInterfaces.this.customScoreboard.isEnabled();
      }
   };
   public BooleanSetting outline = new BooleanSetting("Outline", false) {
      public boolean isHidden() {
         return !CustomInterfaces.this.customScoreboard.isEnabled();
      }
   };
   public BooleanSetting hideLobby = new BooleanSetting("Hide lobby", true) {
      public boolean isHidden() {
         return !CustomInterfaces.this.customScoreboard.isEnabled();
      }
   };
   public BooleanSetting customButtons = new BooleanSetting("Custom Buttons", true);
   public BooleanSetting customChat = new BooleanSetting("Custom chat", true);
   public BooleanSetting customChatFont = new BooleanSetting("Custom chat font", false, (aBoolean) -> {
      return !this.customChat.isEnabled();
   });
   public ModeSetting blurStrength = new ModeSetting("Blur Strength", "Low", new String[]{"None", "Low", "High"}) {
      public boolean isHidden() {
         return !CustomInterfaces.this.customScoreboard.isEnabled();
      }
   };
   public ModeSetting buttonLine = new ModeSetting("Button line", "Single", new String[]{"Wave", "Single", "None"}) {
      public boolean isHidden() {
         return !CustomInterfaces.this.customButtons.isEnabled();
      }
   };
   public ModeSetting lineLocation = new ModeSetting("Line location", "Top", new String[]{"Top", "Bottom"}) {
      public boolean isHidden() {
         return !CustomInterfaces.this.customButtons.isEnabled() || CustomInterfaces.this.buttonLine.is("None");
      }
   };

   public CustomInterfaces() {
      super("Interfaces", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.customChat, this.customChatFont, this.customScoreboard, this.customFont, this.outline, this.hideLobby, this.blurStrength, this.customButtons, this.buttonLine, this.lineLocation});
   }

   @SubscribeEvent
   public void onDraw(ScoreboardRenderEvent event) {
      if (this.isToggled() && this.customScoreboard.isEnabled()) {
         event.setCanceled(true);
         this.renderScoreboard(event.objective, event.resolution, this.customFont.isEnabled());
      }
   }

   private void renderScoreboard(ScoreObjective p_180475_1_, ScaledResolution p_180475_2_, boolean customFont) {
      Scoreboard scoreboard = p_180475_1_.func_96682_a();
      Collection<Score> collection = scoreboard.func_96534_i(p_180475_1_);
      List<Score> list = (List)collection.stream().filter((p_apply_1_) -> {
         return p_apply_1_.func_96653_e() != null && !p_apply_1_.func_96653_e().startsWith("#");
      }).collect(Collectors.toList());
      Object collection;
      if (list.size() > 15) {
         collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
      } else {
         collection = list;
      }

      float width = this.getStringWidth(p_180475_1_.func_96678_d(), customFont);
      int fontHeight = customFont ? Fonts.robotoMediumBold.getHeight() + 2 : mc.field_71466_p.field_78288_b;

      String s;
      for(Iterator var9 = ((Collection)collection).iterator(); var9.hasNext(); width = Math.max(width, this.getStringWidth(s, customFont))) {
         Score score = (Score)var9.next();
         ScorePlayerTeam scoreplayerteam = scoreboard.func_96509_i(score.func_96653_e());
         s = ScorePlayerTeam.func_96667_a(scoreplayerteam, score.func_96653_e()) + ": " + EnumChatFormatting.RED + score.func_96652_c();
      }

      float i1 = (float)(((Collection)collection).size() * fontHeight);
      float arrayHeight = OringoClient.clickGui.getHeight();
      float j1 = (float)p_180475_2_.func_78328_b() / 2.0F + i1 / 3.0F;
      if (OringoClient.clickGui.arrayList.isEnabled()) {
         j1 = Math.max(j1, arrayHeight + 40.0F + (float)(((Collection)collection).size() * fontHeight - fontHeight - 3));
      }

      float k1 = 3.0F;
      float l1 = (float)p_180475_2_.func_78326_a() - width - k1;
      float l = (float)p_180475_2_.func_78326_a() - k1 + 2.0F;
      int blur = 0;
      String var16 = this.blurStrength.getSelected();
      byte var17 = -1;
      switch(var16.hashCode()) {
      case 76596:
         if (var16.equals("Low")) {
            var17 = 0;
         }
         break;
      case 2249154:
         if (var16.equals("High")) {
            var17 = 1;
         }
      }

      switch(var17) {
      case 0:
         blur = 7;
         break;
      case 1:
         blur = 25;
      }

      if (blur > 0 && !this.outline.isEnabled()) {
         for(float i = 0.5F; i < 3.0F; i += 0.5F) {
            RenderUtils.drawRoundedRect2((double)(l1 - 2.0F - i), (double)(j1 - (float)(((Collection)collection).size() * fontHeight) - (float)fontHeight - 3.0F + i), (double)(l - (l1 - 2.0F)), (double)(fontHeight * (((Collection)collection).size() + 1) + 4), 5.0D, (new Color(20, 20, 20, 40)).getRGB());
         }
      }

      StencilUtils.initStencil();
      StencilUtils.bindWriteStencilBuffer();
      RenderUtils.drawRoundedRect2((double)(l1 - 2.0F), (double)(j1 - (float)(((Collection)collection).size() * fontHeight) - (float)fontHeight - 3.0F), (double)(l - (l1 - 2.0F)), (double)(fontHeight * (((Collection)collection).size() + 1) + 4), 5.0D, (new Color(21, 21, 21, 50)).getRGB());
      StencilUtils.bindReadStencilBuffer(1);
      BlurUtils.renderBlurredBackground((float)blur, (float)p_180475_2_.func_78326_a(), (float)p_180475_2_.func_78328_b(), l1 - 2.0F, j1 - (float)(((Collection)collection).size() * fontHeight) - (float)fontHeight - 3.0F, l - (l1 - 2.0F), (float)(fontHeight * (((Collection)collection).size() + 1) + 4));
      StencilUtils.uninitStencil();
      if (this.outline.isEnabled()) {
         this.drawBorderedRoundedRect(l1 - 2.0F, j1 - (float)(((Collection)collection).size() * fontHeight) - (float)fontHeight - 3.0F, l - (l1 - 2.0F), (float)(fontHeight * (((Collection)collection).size() + 1) + 4), 5.0F, 2.0F);
      } else {
         RenderUtils.drawRoundedRect2((double)(l1 - 2.0F), (double)(j1 - (float)(((Collection)collection).size() * fontHeight) - (float)fontHeight - 3.0F), (double)(l - (l1 - 2.0F)), (double)(fontHeight * (((Collection)collection).size() + 1) + 4), 5.0D, (new Color(21, 21, 21, 50)).getRGB());
      }

      int i = 0;
      Iterator var32 = ((Collection)collection).iterator();

      while(var32.hasNext()) {
         Score score1 = (Score)var32.next();
         ++i;
         ScorePlayerTeam scoreplayerteam1 = scoreboard.func_96509_i(score1.func_96653_e());
         String s1 = ScorePlayerTeam.func_96667_a(scoreplayerteam1, score1.func_96653_e());
         if (s1.contains("§ewww.hypixel.ne\ud83c\udf82§et")) {
            s1 = s1.replaceAll("§ewww.hypixel.ne\ud83c\udf82§et", "Oringo Client");
         }

         float k = j1 - (float)(i * fontHeight);
         Matcher matcher = Pattern.compile("[0-9][0-9]/[0-9][0-9]/[0-9][0-9]").matcher(s1);
         if (this.hideLobby.isEnabled() && matcher.find()) {
            s1 = ChatFormatting.GRAY + matcher.group();
         }

         boolean flag = s1.equals("Oringo Client");
         if (flag) {
            if (customFont) {
               Fonts.robotoMediumBold.drawSmoothCenteredStringWithShadow(s1, (double)(l1 + width / 2.0F), (double)k, OringoClient.clickGui.getColor().getRGB());
            } else {
               mc.field_71466_p.func_78276_b(s1, (int)(l1 + width / 2.0F - (float)(mc.field_71466_p.func_78256_a(s1) / 2)), (int)k, OringoClient.clickGui.getColor().getRGB());
            }
         } else {
            this.drawString(s1, l1, k, 553648127, customFont);
         }

         if (i == ((Collection)collection).size()) {
            String s3 = p_180475_1_.func_96678_d();
            this.drawString(s3, l1 + width / 2.0F - this.getStringWidth(s3, customFont) / 2.0F, k - (float)fontHeight, Color.white.getRGB(), customFont);
         }
      }

      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
   }

   private void drawBorderedRoundedRect(float x, float y, float width, float height, float radius, float linewidth) {
      RenderUtils.drawRoundedRect((double)x, (double)y, (double)(x + width), (double)(y + height), (double)radius, (new Color(21, 21, 21, 50)).getRGB());
      RenderUtils.drawGradientOutlinedRoundedRect(x, y, width, height, radius, linewidth, OringoClient.clickGui.getColor(0).getRGB(), OringoClient.clickGui.getColor(3).getRGB(), OringoClient.clickGui.getColor(6).getRGB(), OringoClient.clickGui.getColor(9).getRGB());
   }

   private void drawString(String s, float x, float y, int color, boolean customFont) {
      if (OringoClient.nickHider.isToggled() && s.contains(mc.func_110432_I().func_111285_a())) {
         s = s.replaceAll(mc.func_110432_I().func_111285_a(), OringoClient.nickHider.name.getValue());
      }

      if (customFont) {
         Fonts.robotoMediumBold.drawSmoothStringWithShadow(s, (double)x, (double)y, Color.white.getRGB());
      } else {
         mc.field_71466_p.func_78276_b(s, (int)x, (int)y, color);
      }

   }

   private float getStringWidth(String s, boolean customFont) {
      return customFont ? (float)Fonts.robotoMediumBold.getStringWidth(s) : (float)mc.field_71466_p.func_78256_a(s);
   }
}
