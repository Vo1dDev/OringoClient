package me.oringo.oringoclient.mixins;

import java.util.List;
import java.util.Map;
import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModList.class})
public class ModlessMixin {
   @Shadow
   private Map<String, String> modTags;

   @Inject(
      method = {"<init>(Ljava/util/List;)V"},
      at = {@At("RETURN")}
   )
   public void test(List<ModContainer> modList, CallbackInfo ci) {
      if (OringoClient.modless.isToggled()) {
         try {
            if (Minecraft.func_71410_x().func_71356_B()) {
               return;
            }
         } catch (Exception var4) {
            return;
         }

         this.modTags.entrySet().removeIf((mod) -> {
            return !((String)mod.getKey()).equalsIgnoreCase("fml") && !((String)mod.getKey()).equalsIgnoreCase("forge") && !((String)mod.getKey()).equalsIgnoreCase("mcp");
         });
      }
   }
}
