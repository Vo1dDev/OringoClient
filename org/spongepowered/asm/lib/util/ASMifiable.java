package org.spongepowered.asm.lib.util;

import java.util.Map;
import org.spongepowered.asm.lib.Label;

public interface ASMifiable {
   void asmify(StringBuffer var1, String var2, Map<Label, String> var3);
}
