package me.oringo.oringoclient.events;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ScoreboardRenderEvent extends Event {
   public ScoreObjective objective;
   public ScaledResolution resolution;

   public ScoreboardRenderEvent(ScoreObjective objective, ScaledResolution resolution) {
      this.objective = objective;
      this.resolution = resolution;
   }
}
