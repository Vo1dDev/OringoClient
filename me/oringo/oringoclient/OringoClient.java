package me.oringo.oringoclient;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import me.oringo.oringoclient.commands.CommandHandler;
import me.oringo.oringoclient.commands.impl.ArmorStandsCommand;
import me.oringo.oringoclient.commands.impl.BanCommand;
import me.oringo.oringoclient.commands.impl.ChecknameCommand;
import me.oringo.oringoclient.commands.impl.ClipCommand;
import me.oringo.oringoclient.commands.impl.ConfigCommand;
import me.oringo.oringoclient.commands.impl.CustomESPCommand;
import me.oringo.oringoclient.commands.impl.FireworkCommand;
import me.oringo.oringoclient.commands.impl.HelpCommand;
import me.oringo.oringoclient.commands.impl.SayCommand;
import me.oringo.oringoclient.commands.impl.SettingsCommand;
import me.oringo.oringoclient.commands.impl.StalkCommand;
import me.oringo.oringoclient.commands.impl.TestCommand;
import me.oringo.oringoclient.commands.impl.WardrobeCommand;
import me.oringo.oringoclient.config.ConfigManager;
import me.oringo.oringoclient.qolfeatures.AttackQueue;
import me.oringo.oringoclient.qolfeatures.LoginWithSession;
import me.oringo.oringoclient.qolfeatures.Updater;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.AimAssist;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.AntiBot;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.AutoBlock;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.AutoClicker;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.Hitboxes;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.NoHitDelay;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.NoSlow;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.Reach;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.SumoFences;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.TargetStrafe;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.WTap;
import me.oringo.oringoclient.qolfeatures.module.impl.macro.AOTVReturn;
import me.oringo.oringoclient.qolfeatures.module.impl.macro.AutoSumoBot;
import me.oringo.oringoclient.qolfeatures.module.impl.macro.MithrilMacro;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Flight;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.GuiMove;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.SafeWalk;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Scaffold;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Speed;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Sprint;
import me.oringo.oringoclient.qolfeatures.module.impl.movement.Step;
import me.oringo.oringoclient.qolfeatures.module.impl.other.AntiNicker;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Blink;
import me.oringo.oringoclient.qolfeatures.module.impl.other.ChatBypass;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Derp;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Disabler;
import me.oringo.oringoclient.qolfeatures.module.impl.other.GuessTheBuildAFK;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Modless;
import me.oringo.oringoclient.qolfeatures.module.impl.other.MurdererFinder;
import me.oringo.oringoclient.qolfeatures.module.impl.other.ServerBeamer;
import me.oringo.oringoclient.qolfeatures.module.impl.other.StaffAnalyser;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Test;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Timer;
import me.oringo.oringoclient.qolfeatures.module.impl.other.TntRunPing;
import me.oringo.oringoclient.qolfeatures.module.impl.player.AntiVoid;
import me.oringo.oringoclient.qolfeatures.module.impl.player.ArmorSwap;
import me.oringo.oringoclient.qolfeatures.module.impl.player.AutoHeal;
import me.oringo.oringoclient.qolfeatures.module.impl.player.AutoPot;
import me.oringo.oringoclient.qolfeatures.module.impl.player.AutoTool;
import me.oringo.oringoclient.qolfeatures.module.impl.player.ChestStealer;
import me.oringo.oringoclient.qolfeatures.module.impl.player.FastBreak;
import me.oringo.oringoclient.qolfeatures.module.impl.player.FastPlace;
import me.oringo.oringoclient.qolfeatures.module.impl.player.InvManager;
import me.oringo.oringoclient.qolfeatures.module.impl.player.NoFall;
import me.oringo.oringoclient.qolfeatures.module.impl.player.NoRotate;
import me.oringo.oringoclient.qolfeatures.module.impl.player.Velocity;
import me.oringo.oringoclient.qolfeatures.module.impl.render.AnimationCreator;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Animations;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Camera;
import me.oringo.oringoclient.qolfeatures.module.impl.render.ChestESP;
import me.oringo.oringoclient.qolfeatures.module.impl.render.ChinaHat;
import me.oringo.oringoclient.qolfeatures.module.impl.render.CustomESP;
import me.oringo.oringoclient.qolfeatures.module.impl.render.CustomInterfaces;
import me.oringo.oringoclient.qolfeatures.module.impl.render.DungeonESP;
import me.oringo.oringoclient.qolfeatures.module.impl.render.FreeCam;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Giants;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Gui;
import me.oringo.oringoclient.qolfeatures.module.impl.render.InventoryDisplay;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Nametags;
import me.oringo.oringoclient.qolfeatures.module.impl.render.NickHider;
import me.oringo.oringoclient.qolfeatures.module.impl.render.NoRender;
import me.oringo.oringoclient.qolfeatures.module.impl.render.PlayerESP;
import me.oringo.oringoclient.qolfeatures.module.impl.render.PopupAnimation;
import me.oringo.oringoclient.qolfeatures.module.impl.render.RichPresenceModule;
import me.oringo.oringoclient.qolfeatures.module.impl.render.ServerRotations;
import me.oringo.oringoclient.qolfeatures.module.impl.render.TargetHUD;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Trial;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.Aimbot;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.AntiNukebi;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.AutoRogueSword;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.AutoS1;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.BlazeSwapper;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.BoneThrower;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.CrimsonQOL;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.DojoHelper;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.GhostBlocks;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.IceFillHelp;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.Phase;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.RemoveAnnoyingMobs;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.SecretAura;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.Snowballs;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.TerminalAura;
import me.oringo.oringoclient.qolfeatures.module.impl.skyblock.TerminatorAura;
import me.oringo.oringoclient.ui.hud.impl.TargetComponent;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.ServerUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import me.oringo.oringoclient.utils.shader.BlurUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "examplemod",
   dependencies = "before:*",
   version = "1.7.1"
)
public class OringoClient {
   public static final Minecraft mc = Minecraft.func_71410_x();
   public static final String KEY = "NIGGER";
   public static final Logger LOGGER = Logger.getLogger("Oringo Client");
   public static final CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList();
   public static final Gui clickGui = new Gui();
   public static final KillAura killAura = new KillAura();
   public static final Velocity velocity = new Velocity();
   public static final Aimbot bloodAimbot = new Aimbot();
   public static final Modless modless = new Modless();
   public static final NoHitDelay noHitDelay = new NoHitDelay();
   public static final NoSlow noSlow = new NoSlow();
   public static final Sprint sprint = new Sprint();
   public static final Reach reach = new Reach();
   public static final AutoSumoBot autoSumo = new AutoSumoBot();
   public static final FastBreak fastBreak = new FastBreak();
   public static final AOTVReturn aotvReturn = new AOTVReturn();
   public static final NickHider nickHider = new NickHider();
   public static final Animations animations = new Animations();
   public static final AnimationCreator animationCreator = new AnimationCreator();
   public static final Camera camera = new Camera();
   public static final MithrilMacro mithrilMacro = new MithrilMacro();
   public static final Derp derp = new Derp();
   public static final Hitboxes hitboxes = new Hitboxes();
   public static final NoRotate noRotate = new NoRotate();
   public static final Phase phase = new Phase();
   public static final FreeCam freeCam = new FreeCam();
   public static final Giants giants = new Giants();
   public static final CustomInterfaces interfaces = new CustomInterfaces();
   public static final AutoBlock autoBlock = new AutoBlock();
   public static final Speed speed = new Speed();
   public static final Test test = new Test();
   public static final TargetStrafe targetStrafe = new TargetStrafe();
   public static final GuiMove guiMove = new GuiMove();
   public static final DojoHelper dojoHelper = new DojoHelper();
   public static final PopupAnimation popupAnimation = new PopupAnimation();
   public static final Disabler disabler = new Disabler();
   public static final Scaffold scaffold = new Scaffold();
   public static final Flight fly = new Flight();
   public static final InventoryDisplay inventoryHUDModule = new InventoryDisplay();
   public static boolean shouldUpdate;
   public static String[] vers;
   public static ArrayList<BlockPos> stop = new ArrayList();
   public static final String MODID = "examplemod";
   public static final String PREFIX = "§bOringoClient §3» §7";
   public static final String VERSION = "1.7.1";
   public static boolean devMode = false;
   public static ArrayList<Runnable> scheduledTasks = new ArrayList();
   public static HashMap<String, ResourceLocation> capes = new HashMap();
   public static HashMap<File, ResourceLocation> capesLoaded = new HashMap();
   private MilliTimer timer = new MilliTimer();
   private boolean wasOnline;

   @EventHandler
   public void onPre(FMLPreInitializationEvent event) {
   }

   @EventHandler
   public void onInit(FMLPreInitializationEvent event) {
      (new File(mc.field_71412_D.getPath() + "/config/OringoClient")).mkdir();
      (new File(mc.field_71412_D.getPath() + "/config/OringoClient/capes")).mkdir();
      (new File(mc.field_71412_D.getPath() + "/config/OringoClient/configs")).mkdir();
      update();
      modless.setToggled(true);
      modules.add(new AntiVoid());
      modules.add(clickGui);
      modules.add(killAura);
      modules.add(noRotate);
      modules.add(velocity);
      modules.add(bloodAimbot);
      modules.add(new AntiNicker());
      modules.add(new TerminalAura());
      modules.add(new ChatBypass());
      modules.add(new TerminatorAura());
      modules.add(new SecretAura());
      modules.add(new DungeonESP());
      modules.add(new SafeWalk());
      modules.add(new RemoveAnnoyingMobs());
      modules.add(new GhostBlocks());
      modules.add(new SumoFences());
      modules.add(dojoHelper);
      modules.add(new CrimsonQOL());
      modules.add(modless);
      modules.add(noHitDelay);
      modules.add(new TntRunPing());
      modules.add(noSlow);
      modules.add(sprint);
      modules.add(reach);
      modules.add(new AutoS1());
      modules.add(new InvManager());
      modules.add(new ChestStealer());
      modules.add(new PlayerESP());
      modules.add(autoSumo);
      modules.add(fastBreak);
      modules.add(nickHider);
      modules.add(new ChinaHat());
      modules.add(aotvReturn);
      modules.add(mithrilMacro);
      RichPresenceModule richPresence = new RichPresenceModule();
      modules.add(richPresence);
      modules.add(inventoryHUDModule);
      modules.add(new CustomESP());
      modules.add(fly);
      modules.add(new AutoRogueSword());
      modules.add(new GuessTheBuildAFK());
      modules.add(new Snowballs());
      modules.add(new IceFillHelp());
      modules.add(animations);
      modules.add(ServerRotations.getInstance());
      modules.add(TargetHUD.getInstance());
      modules.add(new WTap());
      modules.add(new AutoTool());
      modules.add(camera);
      modules.add(interfaces);
      modules.add(new ServerBeamer());
      modules.add(FastPlace.getInstance());
      modules.add(derp);
      modules.add(new Blink());
      modules.add(freeCam);
      modules.add(hitboxes);
      modules.add(new MurdererFinder());
      modules.add(new ChestESP());
      modules.add(test);
      modules.add(new BoneThrower());
      modules.add(phase);
      modules.add(giants);
      modules.add(new AutoPot());
      modules.add(disabler);
      modules.add(guiMove);
      modules.add(autoBlock);
      modules.add(speed);
      modules.add(new NoFall());
      modules.add(new Step());
      modules.add(popupAnimation);
      modules.add(targetStrafe);
      modules.add(new AntiNukebi());
      modules.add(new NoRender());
      modules.add(new Trial());
      modules.add(new AutoClicker());
      modules.add(new StaffAnalyser());
      modules.add(new ArmorSwap());
      modules.add(scaffold);
      modules.add(new AimAssist());
      modules.add(AntiBot.getAntiBot());
      modules.add(new AutoHeal());
      modules.add(new Nametags());
      modules.add(new BlazeSwapper());
      modules.add(new Timer());
      interfaces.setToggled(true);
      BlurUtils.registerListener();
      Iterator var3 = modules.iterator();

      while(var3.hasNext()) {
         Module m = (Module)var3.next();
         MinecraftForge.EVENT_BUS.register(m);
      }

      CommandHandler.register(new StalkCommand());
      CommandHandler.register(new WardrobeCommand());
      CommandHandler.register(new HelpCommand());
      CommandHandler.register(new ArmorStandsCommand());
      CommandHandler.register(new ChecknameCommand());
      CommandHandler.register(new ClipCommand());
      CommandHandler.register(new ConfigCommand());
      CommandHandler.register(new FireworkCommand());
      CommandHandler.register(new SettingsCommand());
      CommandHandler.register(new SayCommand());
      CommandHandler.register(new TestCommand());
      CommandHandler.register(new CustomESPCommand());
      MinecraftForge.EVENT_BUS.register(new Notifications());
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(ServerUtils.instance);
      MinecraftForge.EVENT_BUS.register(new Updater());
      MinecraftForge.EVENT_BUS.register(new AttackQueue());
      MinecraftForge.EVENT_BUS.register(new SkyblockUtils());
      ConfigManager.loadConfig();
      TargetComponent.INSTANCE.setPosition(TargetHUD.getInstance().x.getValue(), TargetHUD.getInstance().y.getValue());
      if (Disabler.first.isEnabled()) {
         disabler.setToggled(true);
         Disabler.first.setEnabled(false);
      }

      if (richPresence.isToggled()) {
         richPresence.onEnable();
      }

      if ((new File("OringoDev")).exists()) {
         devMode = true;
      }

      if (devMode) {
         CommandHandler.register(new BanCommand());
         MinecraftForge.EVENT_BUS.register(new LoginWithSession());
         modules.add(animationCreator);
      }

      Fonts.bootstrap();
   }

   @EventHandler
   public void onPost(FMLPostInitializationEvent event) {
      loadCapes();
   }

   public static Map<Integer, DestroyBlockProgress> getBlockBreakProgress() {
      try {
         Field field_72738_e = RenderGlobal.class.getDeclaredField("field_72738_E");
         field_72738_e.setAccessible(true);
         return (Map)field_72738_e.get(Minecraft.func_71410_x().field_71438_f);
      } catch (Exception var1) {
         return new HashMap();
      }
   }

   public static void handleKeypress(int key) {
      if (key != 0) {
         Iterator var1 = modules.iterator();

         while(var1.hasNext()) {
            Module m = (Module)var1.next();
            if (m.getKeycode() == key && !m.isKeybind()) {
               m.toggle();
               if (!clickGui.disableNotifs.isEnabled()) {
                  Notifications.showNotification("Oringo Client", m.getName() + (m.isToggled() ? " enabled!" : " disabled!"), 2500);
               }
            }
         }

      }
   }

   private static void update() {
      checkForUpdates();

      try {
         vers = (new BufferedReader(new InputStreamReader((new URL("http://niger.5v.pl/version")).openStream()))).readLine().split(" ");
         if (!"1.7.1".equals(vers[0])) {
            shouldUpdate = true;
         }
      } catch (Exception var1) {
         var1.printStackTrace();
         System.out.println("Couldn't update");
      }

   }

   private static void checkForUpdates() {
   }

   private static void loadCapes() {
      try {
         HashMap capeData = (HashMap)(new Gson()).fromJson(new InputStreamReader((new URL("http://niger.5v.pl/capes.txt")).openStream()), HashMap.class);
         capes.clear();
         if (capeData != null) {
            HashMap cache = new HashMap();
            capeData.forEach((key, value) -> {
               try {
                  ResourceLocation capeFromCache = (ResourceLocation)cache.get(value);
                  if (capeFromCache == null) {
                     File capeFile = new File(mc.field_71412_D.getPath() + "/config/OringoClient/capes/" + value + ".png");
                     if (!capeFile.exists()) {
                        InputStream in = (new URL("http://niger.5v.pl/capes/" + value + ".png")).openStream();
                        Files.copy(in, capeFile.toPath(), new CopyOption[0]);
                     }

                     ResourceLocation cape;
                     if (capesLoaded.containsKey(capeFile)) {
                        cape = (ResourceLocation)capesLoaded.get(capeFile);
                     } else {
                        cape = mc.func_110434_K().func_110578_a("oringoclient", new DynamicTexture(ImageIO.read(capeFile)));
                        capesLoaded.put(capeFile, cape);
                     }

                     cache.put(value, cape);
                     capes.put((String)key, cape);
                  } else {
                     capes.put((String)key, (ResourceLocation)cache.get(value));
                  }
               } catch (Exception var7) {
                  var7.printStackTrace();
                  System.out.println("Error loading cape " + value);
               }

            });
            if (devMode) {
               System.out.println((new Gson()).toJson(capeData));
            }
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   private void disableSSLVerification() {
      TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
         public X509Certificate[] getAcceptedIssuers() {
            return null;
         }

         public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
         }

         public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
         }
      }};
      SSLContext sc = null;

      try {
         sc = SSLContext.getInstance("SSL");
      } catch (NoSuchAlgorithmException var5) {
         var5.printStackTrace();
      }

      try {
         sc.init((KeyManager[])null, trustAllCerts, new SecureRandom());
      } catch (KeyManagementException var4) {
         var4.printStackTrace();
      }

      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HostnameVerifier validHosts = new HostnameVerifier() {
         public boolean verify(String arg0, SSLSession arg1) {
            return true;
         }
      };
      HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
   }

   private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
      BufferedImage bufferedimage = ImageIO.read(imageStream);
      int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[])null, 0, bufferedimage.getWidth());
      ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
      int[] var5 = aint;
      int var6 = aint.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         int i = var5[var7];
         bytebuffer.putInt(i << 8 | i >> 24 & 255);
      }

      bytebuffer.flip();
      return bytebuffer;
   }

   public static void sendMessage(String message) {
      Minecraft.func_71410_x().field_71439_g.func_145747_a(new ChatComponentText(message));
   }

   public static void sendMessageWithPrefix(String message) {
      Minecraft.func_71410_x().field_71439_g.func_145747_a(new ChatComponentText("§bOringoClient §3» §7" + message));
   }
}
