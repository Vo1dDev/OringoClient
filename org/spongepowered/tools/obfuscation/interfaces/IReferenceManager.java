package org.spongepowered.tools.obfuscation.interfaces;

import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.refmap.ReferenceMapper;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationData;

public interface IReferenceManager {
   void setAllowConflicts(boolean var1);

   boolean getAllowConflicts();

   void write();

   ReferenceMapper getMapper();

   void addMethodMapping(String var1, String var2, ObfuscationData<MappingMethod> var3);

   void addMethodMapping(String var1, String var2, MemberInfo var3, ObfuscationData<MappingMethod> var4);

   void addFieldMapping(String var1, String var2, MemberInfo var3, ObfuscationData<MappingField> var4);

   void addClassMapping(String var1, String var2, ObfuscationData<String> var3);
}
