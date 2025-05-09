package me.oringo.oringoclient.ui.animation;

import me.oringo.oringoclient.utils.MilliTimer;

public abstract class Animation {
   private final MilliTimer animationTimer = new MilliTimer();
   protected long time;

   public abstract void doAnimation(double var1, double var3);

   public Animation(long time) {
      this.time = time;
   }

   public boolean isFinished() {
      return this.animationTimer.hasTimePassed(this.time);
   }

   public void start() {
      this.animationTimer.reset();
   }
}
