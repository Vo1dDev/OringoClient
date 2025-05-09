package org.spongepowered.asm.util;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.spongepowered.asm.lib.signature.SignatureReader;
import org.spongepowered.asm.lib.signature.SignatureVisitor;
import org.spongepowered.asm.lib.signature.SignatureWriter;
import org.spongepowered.asm.lib.tree.ClassNode;

public class ClassSignature {
   protected static final String OBJECT = "java/lang/Object";
   private final Map<ClassSignature.TypeVar, ClassSignature.TokenHandle> types = new LinkedHashMap();
   private ClassSignature.Token superClass = new ClassSignature.Token("java/lang/Object");
   private final List<ClassSignature.Token> interfaces = new ArrayList();
   private final Deque<String> rawInterfaces = new LinkedList();

   ClassSignature() {
   }

   private ClassSignature read(String signature) {
      if (signature != null) {
         try {
            (new SignatureReader(signature)).accept(new ClassSignature.SignatureParser());
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      return this;
   }

   protected ClassSignature.TypeVar getTypeVar(String varName) {
      Iterator var2 = this.types.keySet().iterator();

      ClassSignature.TypeVar typeVar;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         typeVar = (ClassSignature.TypeVar)var2.next();
      } while(!typeVar.matches(varName));

      return typeVar;
   }

   protected ClassSignature.TokenHandle getType(String varName) {
      Iterator var2 = this.types.keySet().iterator();

      ClassSignature.TypeVar typeVar;
      do {
         if (!var2.hasNext()) {
            ClassSignature.TokenHandle handle = new ClassSignature.TokenHandle();
            this.types.put(new ClassSignature.TypeVar(varName), handle);
            return handle;
         }

         typeVar = (ClassSignature.TypeVar)var2.next();
      } while(!typeVar.matches(varName));

      return (ClassSignature.TokenHandle)this.types.get(typeVar);
   }

   protected String getTypeVar(ClassSignature.TokenHandle handle) {
      Iterator var2 = this.types.entrySet().iterator();

      ClassSignature.TypeVar typeVar;
      ClassSignature.TokenHandle typeHandle;
      do {
         if (!var2.hasNext()) {
            return handle.token.asType();
         }

         Entry<ClassSignature.TypeVar, ClassSignature.TokenHandle> type = (Entry)var2.next();
         typeVar = (ClassSignature.TypeVar)type.getKey();
         typeHandle = (ClassSignature.TokenHandle)type.getValue();
      } while(handle != typeHandle && handle.asToken() != typeHandle.asToken());

      return "T" + typeVar + ";";
   }

   protected void addTypeVar(ClassSignature.TypeVar typeVar, ClassSignature.TokenHandle handle) throws IllegalArgumentException {
      if (this.types.containsKey(typeVar)) {
         throw new IllegalArgumentException("TypeVar " + typeVar + " is already present on " + this);
      } else {
         this.types.put(typeVar, handle);
      }
   }

   protected void setSuperClass(ClassSignature.Token superClass) {
      this.superClass = superClass;
   }

   public String getSuperClass() {
      return this.superClass.asType(true);
   }

   protected void addInterface(ClassSignature.Token iface) {
      if (!iface.isRaw()) {
         String raw = iface.asType(true);
         ListIterator iter = this.interfaces.listIterator();

         while(iter.hasNext()) {
            ClassSignature.Token intrface = (ClassSignature.Token)iter.next();
            if (intrface.isRaw() && intrface.asType(true).equals(raw)) {
               iter.set(iface);
               return;
            }
         }
      }

      this.interfaces.add(iface);
   }

   public void addInterface(String iface) {
      this.rawInterfaces.add(iface);
   }

   protected void addRawInterface(String iface) {
      ClassSignature.Token token = new ClassSignature.Token(iface);
      String raw = token.asType(true);
      Iterator var4 = this.interfaces.iterator();

      ClassSignature.Token intrface;
      do {
         if (!var4.hasNext()) {
            this.interfaces.add(token);
            return;
         }

         intrface = (ClassSignature.Token)var4.next();
      } while(!intrface.asType(true).equals(raw));

   }

   public void merge(ClassSignature other) {
      try {
         Set<String> typeVars = new HashSet();
         Iterator var3 = this.types.keySet().iterator();

         while(true) {
            if (!var3.hasNext()) {
               other.conform(typeVars);
               break;
            }

            ClassSignature.TypeVar typeVar = (ClassSignature.TypeVar)var3.next();
            typeVars.add(typeVar.toString());
         }
      } catch (IllegalStateException var5) {
         var5.printStackTrace();
         return;
      }

      Iterator var6 = other.types.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<ClassSignature.TypeVar, ClassSignature.TokenHandle> type = (Entry)var6.next();
         this.addTypeVar((ClassSignature.TypeVar)type.getKey(), (ClassSignature.TokenHandle)type.getValue());
      }

      var6 = other.interfaces.iterator();

      while(var6.hasNext()) {
         ClassSignature.Token iface = (ClassSignature.Token)var6.next();
         this.addInterface(iface);
      }

   }

   private void conform(Set<String> typeVars) {
      Iterator var2 = this.types.keySet().iterator();

      while(var2.hasNext()) {
         ClassSignature.TypeVar typeVar = (ClassSignature.TypeVar)var2.next();
         String name = this.findUniqueName(typeVar.getOriginalName(), typeVars);
         typeVar.rename(name);
         typeVars.add(name);
      }

   }

   private String findUniqueName(String typeVar, Set<String> typeVars) {
      if (!typeVars.contains(typeVar)) {
         return typeVar;
      } else {
         String name;
         if (typeVar.length() == 1) {
            name = this.findOffsetName(typeVar.charAt(0), typeVars);
            if (name != null) {
               return name;
            }
         }

         name = this.findOffsetName('T', typeVars, "", typeVar);
         if (name != null) {
            return name;
         } else {
            name = this.findOffsetName('T', typeVars, typeVar, "");
            if (name != null) {
               return name;
            } else {
               name = this.findOffsetName('T', typeVars, "T", typeVar);
               if (name != null) {
                  return name;
               } else {
                  name = this.findOffsetName('T', typeVars, "", typeVar + "Type");
                  if (name != null) {
                     return name;
                  } else {
                     throw new IllegalStateException("Failed to conform type var: " + typeVar);
                  }
               }
            }
         }
      }
   }

   private String findOffsetName(char c, Set<String> typeVars) {
      return this.findOffsetName(c, typeVars, "", "");
   }

   private String findOffsetName(char c, Set<String> typeVars, String prefix, String suffix) {
      String name = String.format("%s%s%s", prefix, c, suffix);
      if (!typeVars.contains(name)) {
         return name;
      } else {
         if (c > '@' && c < '[') {
            for(int s = c - 64; s + 65 != c; s %= 26) {
               name = String.format("%s%s%s", prefix, (char)(s + 65), suffix);
               if (!typeVars.contains(name)) {
                  return name;
               }

               ++s;
            }
         }

         return null;
      }
   }

   public SignatureVisitor getRemapper() {
      return new ClassSignature.SignatureRemapper();
   }

   public String toString() {
      while(this.rawInterfaces.size() > 0) {
         this.addRawInterface((String)this.rawInterfaces.remove());
      }

      StringBuilder sb = new StringBuilder();
      if (this.types.size() > 0) {
         boolean valid = false;
         StringBuilder types = new StringBuilder();
         Iterator var4 = this.types.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<ClassSignature.TypeVar, ClassSignature.TokenHandle> type = (Entry)var4.next();
            String bound = ((ClassSignature.TokenHandle)type.getValue()).asBound();
            if (!bound.isEmpty()) {
               types.append(type.getKey()).append(':').append(bound);
               valid = true;
            }
         }

         if (valid) {
            sb.append('<').append(types).append('>');
         }
      }

      sb.append(this.superClass.asType());
      Iterator var7 = this.interfaces.iterator();

      while(var7.hasNext()) {
         ClassSignature.Token iface = (ClassSignature.Token)var7.next();
         sb.append(iface.asType());
      }

      return sb.toString();
   }

   public ClassSignature wake() {
      return this;
   }

   public static ClassSignature of(String signature) {
      return (new ClassSignature()).read(signature);
   }

   public static ClassSignature of(ClassNode classNode) {
      return classNode.signature != null ? of(classNode.signature) : generate(classNode);
   }

   public static ClassSignature ofLazy(ClassNode classNode) {
      return (ClassSignature)(classNode.signature != null ? new ClassSignature.Lazy(classNode.signature) : generate(classNode));
   }

   private static ClassSignature generate(ClassNode classNode) {
      ClassSignature generated = new ClassSignature();
      generated.setSuperClass(new ClassSignature.Token(classNode.superName != null ? classNode.superName : "java/lang/Object"));
      Iterator var2 = classNode.interfaces.iterator();

      while(var2.hasNext()) {
         String iface = (String)var2.next();
         generated.addInterface(new ClassSignature.Token(iface));
      }

      return generated;
   }

   class SignatureRemapper extends SignatureWriter {
      private final Set<String> localTypeVars = new HashSet();

      public void visitFormalTypeParameter(String name) {
         this.localTypeVars.add(name);
         super.visitFormalTypeParameter(name);
      }

      public void visitTypeVariable(String name) {
         if (!this.localTypeVars.contains(name)) {
            ClassSignature.TypeVar typeVar = ClassSignature.this.getTypeVar(name);
            if (typeVar != null) {
               super.visitTypeVariable(typeVar.toString());
               return;
            }
         }

         super.visitTypeVariable(name);
      }
   }

   class SignatureParser extends SignatureVisitor {
      private ClassSignature.SignatureParser.FormalParamElement param;

      SignatureParser() {
         super(327680);
      }

      public void visitFormalTypeParameter(String name) {
         this.param = new ClassSignature.SignatureParser.FormalParamElement(name);
      }

      public SignatureVisitor visitClassBound() {
         return this.param.visitClassBound();
      }

      public SignatureVisitor visitInterfaceBound() {
         return this.param.visitInterfaceBound();
      }

      public SignatureVisitor visitSuperclass() {
         return new ClassSignature.SignatureParser.SuperClassElement();
      }

      public SignatureVisitor visitInterface() {
         return new ClassSignature.SignatureParser.InterfaceElement();
      }

      class InterfaceElement extends ClassSignature.SignatureParser.TokenElement {
         InterfaceElement() {
            super();
         }

         public void visitEnd() {
            ClassSignature.this.addInterface(this.token);
         }
      }

      class SuperClassElement extends ClassSignature.SignatureParser.TokenElement {
         SuperClassElement() {
            super();
         }

         public void visitEnd() {
            ClassSignature.this.setSuperClass(this.token);
         }
      }

      class BoundElement extends ClassSignature.SignatureParser.TokenElement {
         private final ClassSignature.SignatureParser.TokenElement type;
         private final boolean classBound;

         BoundElement(ClassSignature.SignatureParser.TokenElement type, boolean classBound) {
            super();
            this.type = type;
            this.classBound = classBound;
         }

         public void visitClassType(String name) {
            this.token = this.type.token.addBound(name, this.classBound);
         }

         public void visitTypeArgument() {
            this.token.addTypeArgument('*');
         }

         public SignatureVisitor visitTypeArgument(char wildcard) {
            return SignatureParser.this.new TypeArgElement(this, wildcard);
         }
      }

      class TypeArgElement extends ClassSignature.SignatureParser.TokenElement {
         private final ClassSignature.SignatureParser.TokenElement type;
         private final char wildcard;

         TypeArgElement(ClassSignature.SignatureParser.TokenElement type, char wildcard) {
            super();
            this.type = type;
            this.wildcard = wildcard;
         }

         public SignatureVisitor visitArrayType() {
            this.type.setArray();
            return this;
         }

         public void visitBaseType(char descriptor) {
            this.token = this.type.addTypeArgument(descriptor).asToken();
         }

         public void visitTypeVariable(String name) {
            ClassSignature.TokenHandle token = ClassSignature.this.getType(name);
            this.token = this.type.addTypeArgument(token).setWildcard(this.wildcard).asToken();
         }

         public void visitClassType(String name) {
            this.token = this.type.addTypeArgument(name).setWildcard(this.wildcard).asToken();
         }

         public void visitTypeArgument() {
            this.token.addTypeArgument('*');
         }

         public SignatureVisitor visitTypeArgument(char wildcard) {
            return SignatureParser.this.new TypeArgElement(this, wildcard);
         }

         public void visitEnd() {
         }
      }

      class FormalParamElement extends ClassSignature.SignatureParser.TokenElement {
         private final ClassSignature.TokenHandle handle;

         FormalParamElement(String param) {
            super();
            this.handle = ClassSignature.this.getType(param);
            this.token = this.handle.asToken();
         }
      }

      abstract class TokenElement extends ClassSignature.SignatureParser.SignatureElement {
         protected ClassSignature.Token token;
         private boolean array;

         TokenElement() {
            super();
         }

         public ClassSignature.Token getToken() {
            if (this.token == null) {
               this.token = new ClassSignature.Token();
            }

            return this.token;
         }

         protected void setArray() {
            this.array = true;
         }

         private boolean getArray() {
            boolean array = this.array;
            this.array = false;
            return array;
         }

         public void visitClassType(String name) {
            this.getToken().setType(name);
         }

         public SignatureVisitor visitClassBound() {
            this.getToken();
            return SignatureParser.this.new BoundElement(this, true);
         }

         public SignatureVisitor visitInterfaceBound() {
            this.getToken();
            return SignatureParser.this.new BoundElement(this, false);
         }

         public void visitInnerClassType(String name) {
            this.token.addInnerClass(name);
         }

         public SignatureVisitor visitArrayType() {
            this.setArray();
            return this;
         }

         public SignatureVisitor visitTypeArgument(char wildcard) {
            return SignatureParser.this.new TypeArgElement(this, wildcard);
         }

         ClassSignature.Token addTypeArgument() {
            return this.token.addTypeArgument('*').asToken();
         }

         ClassSignature.IToken addTypeArgument(char symbol) {
            return this.token.addTypeArgument(symbol).setArray(this.getArray());
         }

         ClassSignature.IToken addTypeArgument(String name) {
            return this.token.addTypeArgument(name).setArray(this.getArray());
         }

         ClassSignature.IToken addTypeArgument(ClassSignature.Token token) {
            return this.token.addTypeArgument(token).setArray(this.getArray());
         }

         ClassSignature.IToken addTypeArgument(ClassSignature.TokenHandle token) {
            return this.token.addTypeArgument(token).setArray(this.getArray());
         }
      }

      abstract class SignatureElement extends SignatureVisitor {
         public SignatureElement() {
            super(327680);
         }
      }
   }

   class TokenHandle implements ClassSignature.IToken {
      final ClassSignature.Token token;
      boolean array;
      char wildcard;

      TokenHandle() {
         this(new ClassSignature.Token());
      }

      TokenHandle(ClassSignature.Token token) {
         this.token = token;
      }

      public ClassSignature.IToken setArray(boolean array) {
         this.array |= array;
         return this;
      }

      public ClassSignature.IToken setWildcard(char wildcard) {
         if ("+-".indexOf(wildcard) > -1) {
            this.wildcard = wildcard;
         }

         return this;
      }

      public String asBound() {
         return this.token.asBound();
      }

      public String asType() {
         StringBuilder sb = new StringBuilder();
         if (this.wildcard > 0) {
            sb.append(this.wildcard);
         }

         if (this.array) {
            sb.append('[');
         }

         return sb.append(ClassSignature.this.getTypeVar(this)).toString();
      }

      public ClassSignature.Token asToken() {
         return this.token;
      }

      public String toString() {
         return this.token.toString();
      }

      public ClassSignature.TokenHandle clone() {
         return ClassSignature.this.new TokenHandle(this.token);
      }
   }

   static class Token implements ClassSignature.IToken {
      static final String SYMBOLS = "+-*";
      private final boolean inner;
      private boolean array;
      private char symbol;
      private String type;
      private List<ClassSignature.Token> classBound;
      private List<ClassSignature.Token> ifaceBound;
      private List<ClassSignature.IToken> signature;
      private List<ClassSignature.IToken> suffix;
      private ClassSignature.Token tail;

      Token() {
         this(false);
      }

      Token(String type) {
         this(type, false);
      }

      Token(char symbol) {
         this();
         this.symbol = symbol;
      }

      Token(boolean inner) {
         this((String)null, inner);
      }

      Token(String type, boolean inner) {
         this.symbol = 0;
         this.inner = inner;
         this.type = type;
      }

      ClassSignature.Token setSymbol(char symbol) {
         if (this.symbol == 0 && "+-*".indexOf(symbol) > -1) {
            this.symbol = symbol;
         }

         return this;
      }

      ClassSignature.Token setType(String type) {
         if (this.type == null) {
            this.type = type;
         }

         return this;
      }

      boolean hasClassBound() {
         return this.classBound != null;
      }

      boolean hasInterfaceBound() {
         return this.ifaceBound != null;
      }

      public ClassSignature.IToken setArray(boolean array) {
         this.array |= array;
         return this;
      }

      public ClassSignature.IToken setWildcard(char wildcard) {
         return "+-".indexOf(wildcard) == -1 ? this : this.setSymbol(wildcard);
      }

      private List<ClassSignature.Token> getClassBound() {
         if (this.classBound == null) {
            this.classBound = new ArrayList();
         }

         return this.classBound;
      }

      private List<ClassSignature.Token> getIfaceBound() {
         if (this.ifaceBound == null) {
            this.ifaceBound = new ArrayList();
         }

         return this.ifaceBound;
      }

      private List<ClassSignature.IToken> getSignature() {
         if (this.signature == null) {
            this.signature = new ArrayList();
         }

         return this.signature;
      }

      private List<ClassSignature.IToken> getSuffix() {
         if (this.suffix == null) {
            this.suffix = new ArrayList();
         }

         return this.suffix;
      }

      ClassSignature.IToken addTypeArgument(char symbol) {
         if (this.tail != null) {
            return this.tail.addTypeArgument(symbol);
         } else {
            ClassSignature.Token token = new ClassSignature.Token(symbol);
            this.getSignature().add(token);
            return token;
         }
      }

      ClassSignature.IToken addTypeArgument(String name) {
         if (this.tail != null) {
            return this.tail.addTypeArgument(name);
         } else {
            ClassSignature.Token token = new ClassSignature.Token(name);
            this.getSignature().add(token);
            return token;
         }
      }

      ClassSignature.IToken addTypeArgument(ClassSignature.Token token) {
         if (this.tail != null) {
            return this.tail.addTypeArgument(token);
         } else {
            this.getSignature().add(token);
            return token;
         }
      }

      ClassSignature.IToken addTypeArgument(ClassSignature.TokenHandle token) {
         if (this.tail != null) {
            return this.tail.addTypeArgument(token);
         } else {
            ClassSignature.TokenHandle handle = token.clone();
            this.getSignature().add(handle);
            return handle;
         }
      }

      ClassSignature.Token addBound(String bound, boolean classBound) {
         return classBound ? this.addClassBound(bound) : this.addInterfaceBound(bound);
      }

      ClassSignature.Token addClassBound(String bound) {
         ClassSignature.Token token = new ClassSignature.Token(bound);
         this.getClassBound().add(token);
         return token;
      }

      ClassSignature.Token addInterfaceBound(String bound) {
         ClassSignature.Token token = new ClassSignature.Token(bound);
         this.getIfaceBound().add(token);
         return token;
      }

      ClassSignature.Token addInnerClass(String name) {
         this.tail = new ClassSignature.Token(name, true);
         this.getSuffix().add(this.tail);
         return this.tail;
      }

      public String toString() {
         return this.asType();
      }

      public String asBound() {
         StringBuilder sb = new StringBuilder();
         if (this.type != null) {
            sb.append(this.type);
         }

         Iterator var2;
         ClassSignature.Token token;
         if (this.classBound != null) {
            var2 = this.classBound.iterator();

            while(var2.hasNext()) {
               token = (ClassSignature.Token)var2.next();
               sb.append(token.asType());
            }
         }

         if (this.ifaceBound != null) {
            var2 = this.ifaceBound.iterator();

            while(var2.hasNext()) {
               token = (ClassSignature.Token)var2.next();
               sb.append(':').append(token.asType());
            }
         }

         return sb.toString();
      }

      public String asType() {
         return this.asType(false);
      }

      public String asType(boolean raw) {
         StringBuilder sb = new StringBuilder();
         if (this.array) {
            sb.append('[');
         }

         if (this.symbol != 0) {
            sb.append(this.symbol);
         }

         if (this.type == null) {
            return sb.toString();
         } else {
            if (!this.inner) {
               sb.append('L');
            }

            sb.append(this.type);
            if (!raw) {
               Iterator var3;
               ClassSignature.IToken token;
               if (this.signature != null) {
                  sb.append('<');
                  var3 = this.signature.iterator();

                  while(var3.hasNext()) {
                     token = (ClassSignature.IToken)var3.next();
                     sb.append(token.asType());
                  }

                  sb.append('>');
               }

               if (this.suffix != null) {
                  var3 = this.suffix.iterator();

                  while(var3.hasNext()) {
                     token = (ClassSignature.IToken)var3.next();
                     sb.append('.').append(token.asType());
                  }
               }
            }

            if (!this.inner) {
               sb.append(';');
            }

            return sb.toString();
         }
      }

      boolean isRaw() {
         return this.signature == null;
      }

      String getClassType() {
         return this.type != null ? this.type : "java/lang/Object";
      }

      public ClassSignature.Token asToken() {
         return this;
      }
   }

   interface IToken {
      String WILDCARDS = "+-";

      String asType();

      String asBound();

      ClassSignature.Token asToken();

      ClassSignature.IToken setArray(boolean var1);

      ClassSignature.IToken setWildcard(char var1);
   }

   static class TypeVar implements Comparable<ClassSignature.TypeVar> {
      private final String originalName;
      private String currentName;

      TypeVar(String name) {
         this.currentName = this.originalName = name;
      }

      public int compareTo(ClassSignature.TypeVar other) {
         return this.currentName.compareTo(other.currentName);
      }

      public String toString() {
         return this.currentName;
      }

      String getOriginalName() {
         return this.originalName;
      }

      void rename(String name) {
         this.currentName = name;
      }

      public boolean matches(String originalName) {
         return this.originalName.equals(originalName);
      }

      public boolean equals(Object obj) {
         return this.currentName.equals(obj);
      }

      public int hashCode() {
         return this.currentName.hashCode();
      }
   }

   static class Lazy extends ClassSignature {
      private final String sig;
      private ClassSignature generated;

      Lazy(String sig) {
         this.sig = sig;
      }

      public ClassSignature wake() {
         if (this.generated == null) {
            this.generated = ClassSignature.of(this.sig);
         }

         return this.generated;
      }
   }
}
