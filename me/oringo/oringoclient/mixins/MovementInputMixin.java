package me.oringo.oringoclient.mixins;

import net.minecraft.util.MovementInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({MovementInput.class})
public class MovementInputMixin {
   @Shadow
   public boolean field_78899_d;
   @Shadow
   public boolean field_78901_c;
   @Shadow
   public float field_78902_a;
   @Shadow
   public float field_78900_b;
}
