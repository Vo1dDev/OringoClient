package me.oringo.oringoclient.ui.hud;

public class DraggableComponent extends Component {
   private double startX;
   private double startY;
   private boolean dragging;

   public boolean isDragging() {
      return this.dragging;
   }

   public void startDragging() {
      this.dragging = true;
      this.startX = this.x - (double)getMouseX();
      this.startY = this.y - (double)getMouseY();
   }

   public void stopDragging() {
      this.dragging = false;
   }

   public void onTick() {
   }

   public HudVec drawScreen() {
      if (this.dragging) {
         this.y = (double)getMouseY() + this.startY;
         this.x = (double)getMouseX() + this.startX;
      }

      return null;
   }
}
