package me.oringo.oringoclient.mixins;

import me.oringo.oringoclient.events.MoveStateUpdateEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {MovementInputFromOptions.class},
   priority = 1
)
public abstract class MovementInputFromOptionsMixin extends MovementInputMixin {
   @Shadow
   @Final
   private GameSettings field_78903_e;

   @Overwrite
   public void func_78898_a() {
      this.field_78902_a = 0.0F;
      this.field_78900_b = 0.0F;
      if (this.field_78903_e.field_74351_w.func_151470_d()) {
         ++this.field_78900_b;
      }

      if (this.field_78903_e.field_74368_y.func_151470_d()) {
         --this.field_78900_b;
      }

      if (this.field_78903_e.field_74370_x.func_151470_d()) {
         ++this.field_78902_a;
      }

      if (this.field_78903_e.field_74366_z.func_151470_d()) {
         --this.field_78902_a;
      }

      MoveStateUpdateEvent event = new MoveStateUpdateEvent(this.field_78900_b, this.field_78902_a, this.field_78903_e.field_74314_A.func_151470_d(), this.field_78903_e.field_74311_E.func_151470_d());
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         this.field_78901_c = event.isJump();
         this.field_78899_d = event.isSneak();
         this.field_78900_b = event.getForward();
         this.field_78902_a = event.getStrafe();
         if (this.field_78899_d) {
            this.field_78902_a = (float)((double)this.field_78902_a * 0.3D);
            this.field_78900_b = (float)((double)this.field_78900_b * 0.3D);
         }

      }
   }
}
