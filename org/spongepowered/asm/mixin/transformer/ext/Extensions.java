package org.spongepowered.asm.mixin.transformer.ext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;

public final class Extensions {
   private final MixinTransformer transformer;
   private final List<IExtension> extensions = new ArrayList();
   private final Map<Class<? extends IExtension>, IExtension> extensionMap = new HashMap();
   private final List<IClassGenerator> generators = new ArrayList();
   private final List<IClassGenerator> generatorsView;
   private final Map<Class<? extends IClassGenerator>, IClassGenerator> generatorMap;
   private List<IExtension> activeExtensions;

   public Extensions(MixinTransformer transformer) {
      this.generatorsView = Collections.unmodifiableList(this.generators);
      this.generatorMap = new HashMap();
      this.activeExtensions = Collections.emptyList();
      this.transformer = transformer;
   }

   public MixinTransformer getTransformer() {
      return this.transformer;
   }

   public void add(IExtension extension) {
      this.extensions.add(extension);
      this.extensionMap.put(extension.getClass(), extension);
   }

   public List<IExtension> getExtensions() {
      return Collections.unmodifiableList(this.extensions);
   }

   public List<IExtension> getActiveExtensions() {
      return this.activeExtensions;
   }

   public <T extends IExtension> T getExtension(Class<? extends IExtension> extensionClass) {
      return (IExtension)lookup(extensionClass, this.extensionMap, this.extensions);
   }

   public void select(MixinEnvironment environment) {
      Builder<IExtension> activeExtensions = ImmutableList.builder();
      Iterator var3 = this.extensions.iterator();

      while(var3.hasNext()) {
         IExtension extension = (IExtension)var3.next();
         if (extension.checkActive(environment)) {
            activeExtensions.add(extension);
         }
      }

      this.activeExtensions = activeExtensions.build();
   }

   public void preApply(ITargetClassContext context) {
      Iterator var2 = this.activeExtensions.iterator();

      while(var2.hasNext()) {
         IExtension extension = (IExtension)var2.next();
         extension.preApply(context);
      }

   }

   public void postApply(ITargetClassContext context) {
      Iterator var2 = this.activeExtensions.iterator();

      while(var2.hasNext()) {
         IExtension extension = (IExtension)var2.next();
         extension.postApply(context);
      }

   }

   public void export(MixinEnvironment env, String name, boolean force, byte[] bytes) {
      Iterator var5 = this.activeExtensions.iterator();

      while(var5.hasNext()) {
         IExtension extension = (IExtension)var5.next();
         extension.export(env, name, force, bytes);
      }

   }

   public void add(IClassGenerator generator) {
      this.generators.add(generator);
      this.generatorMap.put(generator.getClass(), generator);
   }

   public List<IClassGenerator> getGenerators() {
      return this.generatorsView;
   }

   public <T extends IClassGenerator> T getGenerator(Class<? extends IClassGenerator> generatorClass) {
      return (IClassGenerator)lookup(generatorClass, this.generatorMap, this.generators);
   }

   private static <T> T lookup(Class<? extends T> extensionClass, Map<Class<? extends T>, T> map, List<T> list) {
      T extension = map.get(extensionClass);
      if (extension == null) {
         Iterator var4 = list.iterator();

         while(var4.hasNext()) {
            T classGenerator = var4.next();
            if (extensionClass.isAssignableFrom(classGenerator.getClass())) {
               extension = classGenerator;
               break;
            }
         }

         if (extension == null) {
            throw new IllegalArgumentException("Extension for <" + extensionClass.getName() + "> could not be found");
         }

         map.put(extensionClass, extension);
      }

      return extension;
   }
}
