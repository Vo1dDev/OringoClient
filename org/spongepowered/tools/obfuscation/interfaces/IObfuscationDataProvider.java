package org.spongepowered.tools.obfuscation.interfaces;

import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public interface IObfuscationDataProvider {
   <T> ObfuscationData<T> getObfEntryRecursive(MemberInfo var1);

   <T> ObfuscationData<T> getObfEntry(MemberInfo var1);

   <T> ObfuscationData<T> getObfEntry(IMapping<T> var1);

   ObfuscationData<MappingMethod> getObfMethodRecursive(MemberInfo var1);

   ObfuscationData<MappingMethod> getObfMethod(MemberInfo var1);

   ObfuscationData<MappingMethod> getRemappedMethod(MemberInfo var1);

   ObfuscationData<MappingMethod> getObfMethod(MappingMethod var1);

   ObfuscationData<MappingMethod> getRemappedMethod(MappingMethod var1);

   ObfuscationData<MappingField> getObfFieldRecursive(MemberInfo var1);

   ObfuscationData<MappingField> getObfField(MemberInfo var1);

   ObfuscationData<MappingField> getObfField(MappingField var1);

   ObfuscationData<String> getObfClass(TypeHandle var1);

   ObfuscationData<String> getObfClass(String var1);
}
