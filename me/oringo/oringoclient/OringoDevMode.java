package me.oringo.oringoclient;

import java.io.File;
import me.oringodevmode.Transformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class OringoDevMode implements IClassTransformer {
   private boolean enabled = (new File("OringoDev")).exists();

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (this.enabled && !transformedName.startsWith("java") && !transformedName.startsWith("sun") && !transformedName.startsWith("org.lwjgl") && !transformedName.startsWith("org.apache") && !transformedName.startsWith("org.objectweb")) {
         Transformer.classes.remove(transformedName);
         Transformer.classes.put(transformedName, basicClass);
      }

      return basicClass;
   }
}
