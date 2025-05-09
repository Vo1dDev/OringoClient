package me.oringodevmode;

import java.util.HashMap;
import java.util.Map;
import me.oringo.oringoclient.OringoDevMode;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import org.spongepowered.asm.launch.MixinBootstrap;

@MCVersion("1.8.9")
public class Transformer implements IFMLLoadingPlugin {
   public static HashMap<String, byte[]> classes = new HashMap();

   public Transformer() {
      MixinBootstrap.init();
   }

   public String[] getASMTransformerClass() {
      return new String[]{OringoDevMode.class.getName()};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
