package zombie.behaviors.survivor.orders.Needs;

public class Need {
   public int priority;
   public String item;
   public int numToSatisfy = 1;

   public Need(String var1, int var2) {
      this.item = var1;
      this.priority = var2;
   }
}
