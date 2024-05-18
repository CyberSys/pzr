package zombie;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;

public class LOSThread {
   public static LOSThread instance = new LOSThread();
   public Thread losThread;
   public boolean finished = false;
   public boolean running = false;
   public Stack SeenList = new Stack();
   public Stack Jobs = new Stack();

   public void Start() {
   }

   public void AddJob(IsoGameCharacter var1) {
   }

   private void run() throws InterruptedException {
   }

   public class LOSJob {
      public IsoGameCharacter POVCharacter;

      private void Execute() {
         LOSThread.this.SeenList.clear();

         for(int var1 = 0; var1 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++var1) {
            IsoMovingObject var2 = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(var1);
            if (var2 != this.POVCharacter && var2 instanceof IsoGameCharacter && (!(var2 instanceof IsoZombie) || !((IsoZombie)var2).Ghost)) {
               float var3 = var2.DistTo(this.POVCharacter);
               if (!(var3 > GameTime.getInstance().getViewDist()) && this.POVCharacter.CanSee(var2)) {
                  LOSThread.this.SeenList.add(var2);
               }
            }
         }

         this.POVCharacter.Seen(LOSThread.this.SeenList);
         LOSThread.this.SeenList.clear();
      }
   }
}
