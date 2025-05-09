package org.spongepowered.asm.mixin.extensibility;

import java.util.List;
import java.util.Set;
import org.spongepowered.asm.lib.tree.ClassNode;

public interface IMixinConfigPlugin {
   void onLoad(String var1);

   String getRefMapperConfig();

   boolean shouldApplyMixin(String var1, String var2);

   void acceptTargets(Set<String> var1, Set<String> var2);

   List<String> getMixins();

   void preApply(String var1, ClassNode var2, String var3, IMixinInfo var4);

   void postApply(String var1, ClassNode var2, String var3, IMixinInfo var4);
}
