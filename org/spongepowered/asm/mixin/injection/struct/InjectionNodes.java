package org.spongepowered.asm.mixin.injection.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.util.Bytecode;

public class InjectionNodes extends ArrayList<InjectionNodes.InjectionNode> {
   private static final long serialVersionUID = 1L;

   public InjectionNodes.InjectionNode add(AbstractInsnNode node) {
      InjectionNodes.InjectionNode injectionNode = this.get(node);
      if (injectionNode == null) {
         injectionNode = new InjectionNodes.InjectionNode(node);
         this.add(injectionNode);
      }

      return injectionNode;
   }

   public InjectionNodes.InjectionNode get(AbstractInsnNode node) {
      Iterator var2 = this.iterator();

      InjectionNodes.InjectionNode injectionNode;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         injectionNode = (InjectionNodes.InjectionNode)var2.next();
      } while(!injectionNode.matches(node));

      return injectionNode;
   }

   public boolean contains(AbstractInsnNode node) {
      return this.get(node) != null;
   }

   public void replace(AbstractInsnNode oldNode, AbstractInsnNode newNode) {
      InjectionNodes.InjectionNode injectionNode = this.get(oldNode);
      if (injectionNode != null) {
         injectionNode.replace(newNode);
      }

   }

   public void remove(AbstractInsnNode node) {
      InjectionNodes.InjectionNode injectionNode = this.get(node);
      if (injectionNode != null) {
         injectionNode.remove();
      }

   }

   public static class InjectionNode implements Comparable<InjectionNodes.InjectionNode> {
      private static int nextId = 0;
      private final int id;
      private final AbstractInsnNode originalTarget;
      private AbstractInsnNode currentTarget;
      private Map<String, Object> decorations;

      public InjectionNode(AbstractInsnNode node) {
         this.currentTarget = this.originalTarget = node;
         this.id = nextId++;
      }

      public int getId() {
         return this.id;
      }

      public AbstractInsnNode getOriginalTarget() {
         return this.originalTarget;
      }

      public AbstractInsnNode getCurrentTarget() {
         return this.currentTarget;
      }

      public InjectionNodes.InjectionNode replace(AbstractInsnNode target) {
         this.currentTarget = target;
         return this;
      }

      public InjectionNodes.InjectionNode remove() {
         this.currentTarget = null;
         return this;
      }

      public boolean matches(AbstractInsnNode node) {
         return this.originalTarget == node || this.currentTarget == node;
      }

      public boolean isReplaced() {
         return this.originalTarget != this.currentTarget;
      }

      public boolean isRemoved() {
         return this.currentTarget == null;
      }

      public <V> InjectionNodes.InjectionNode decorate(String key, V value) {
         if (this.decorations == null) {
            this.decorations = new HashMap();
         }

         this.decorations.put(key, value);
         return this;
      }

      public boolean hasDecoration(String key) {
         return this.decorations != null && this.decorations.get(key) != null;
      }

      public <V> V getDecoration(String key) {
         return this.decorations == null ? null : this.decorations.get(key);
      }

      public int compareTo(InjectionNodes.InjectionNode other) {
         return other == null ? Integer.MAX_VALUE : this.hashCode() - other.hashCode();
      }

      public String toString() {
         return String.format("InjectionNode[%s]", Bytecode.describeNode(this.currentTarget).replaceAll("\\s+", " "));
      }
   }
}
