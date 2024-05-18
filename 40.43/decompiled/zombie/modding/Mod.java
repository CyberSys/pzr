package zombie.modding;

import zombie.interfaces.IListBoxItem;

public class Mod implements IListBoxItem {
   public String name;
   public String temp;

   public void Mod() {
   }

   public String getLabel() {
      return this.name;
   }

   public String getLeftLabel() {
      return null;
   }

   public String getRightLabel() {
      return null;
   }
}
