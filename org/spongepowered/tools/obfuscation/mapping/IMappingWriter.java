package org.spongepowered.tools.obfuscation.mapping;

import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationType;

public interface IMappingWriter {
   void write(String var1, ObfuscationType var2, IMappingConsumer.MappingSet<MappingField> var3, IMappingConsumer.MappingSet<MappingMethod> var4);
}
