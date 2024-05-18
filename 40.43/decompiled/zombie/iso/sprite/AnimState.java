package zombie.iso.sprite;

import java.util.ArrayList;

public class AnimState {
   public ArrayList entries = new ArrayList(0);
   public AnimStateMachine machine;
   public IsoSpriteInstance inst;
   public int loopEntry = 0;
   public int currentEntry = 0;
   public float lastFrame = 0.0F;

   public AnimState(String var1, AnimStateMachine var2, IsoSpriteInstance var3) {
      this.inst = var3;
      this.machine = var2;
   }

   public AnimState.AnimStateEntry addState(IsoAnim var1, float var2) {
      AnimState.AnimStateEntry var3 = new AnimState.AnimStateEntry(var1, var2);
      this.entries.add(var3);
      return var3;
   }

   public void update() {
      if (this.currentEntry < this.entries.size()) {
         AnimState.AnimStateEntry var1 = (AnimState.AnimStateEntry)this.entries.get(this.currentEntry);
         this.inst.parentSprite.PlayAnim(var1.anim);
         this.inst.AnimFrameIncrease = var1.AnimSpeedPerFrame;
         if (this.inst.Frame < this.lastFrame) {
            ++this.currentEntry;
            if (this.loopEntry != -1) {
               this.currentEntry = this.loopEntry;
            }
         }

         this.lastFrame = this.inst.Frame;
      }
   }

   public static class AnimStateEntry {
      public IsoAnim anim;
      public float AnimSpeedPerFrame = 0.0F;

      public AnimStateEntry(IsoAnim var1, float var2) {
         this.anim = var1;
         this.AnimSpeedPerFrame = var2;
      }
   }
}
