package zombie.characters;

import java.util.ArrayList;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.behaviors.survivor.orders.GotoOrder;
import zombie.behaviors.survivor.orders.Needs.Need;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;

public class SurvivorGroup {
   public IsoBuilding Safehouse = null;
   public ArrayList Members = new ArrayList();
   public SurvivorDesc Leader = null;
   public Stack GroupNeeds = new Stack();
   private KahluaTable luaGroup;

   public SurvivorGroup(KahluaTable var1) {
      this.luaGroup = var1;
   }

   public SurvivorGroup(SurvivorDesc var1) {
      this.Leader = var1;
      this.addMember(var1);
   }

   public void setLuaGroup(KahluaTable var1) {
      this.luaGroup = var1;
   }

   public ArrayList getMembers() {
      return this.Members;
   }

   public void addAll(SurvivorGroup var1) {
      for(int var2 = 0; var2 < var1.Members.size(); ++var2) {
         this.addMember((SurvivorDesc)var1.Members.get(var2));
      }

   }

   public KahluaTable getLuaGroup() {
      return this.luaGroup;
   }

   public void gotoOrder(int var1, int var2) {
      for(int var3 = 0; var3 < this.Members.size(); ++var3) {
         SurvivorDesc var4 = (SurvivorDesc)this.Members.get(var3);
         if (var4.Instance != null) {
            var4.Instance.GiveOrder(new GotoOrder(var4.Instance, var1, var2, 0), true);
         }
      }

   }

   public void gotoBuildingOrder(BuildingDef var1) {
      if (this.Leader == null) {
         this.pickNewLeader();
      }

      if (this.Leader.Instance != null) {
         boolean var2 = false;
      }

      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var1.getX(), var1.getY(), 0);
      IsoGridSquare var3 = IsoWorld.instance.CurrentCell.getGridSquare(var1.getX2(), var1.getY2(), 0);
      int var4;
      SurvivorDesc var5;
      if (var6 == null && var3 == null) {
         for(var4 = 0; var4 < this.Members.size(); ++var4) {
            var5 = (SurvivorDesc)this.Members.get(var4);
            if (var5.Instance != null) {
               var5.Instance.GiveOrder(new GotoOrder(var5.Instance, var1.getFirstRoom().getX(), var1.getFirstRoom().getY(), 0), true);
            }
         }
      } else {
         var6 = var1.getFreeSquareInRoom();

         for(var4 = 0; var4 < this.Members.size(); ++var4) {
            var5 = (SurvivorDesc)this.Members.get(var4);
            if (var5.Instance != null) {
               var5.Instance.GiveOrder(new GotoOrder(var5.Instance, var6.getX(), var6.getY(), 0), true);
            }
         }
      }

   }

   public void setLeader(SurvivorDesc var1) {
      this.Leader = var1;
   }

   public SurvivorDesc getLeader() {
      return this.Leader;
   }

   public void addMember(SurvivorDesc var1) {
      if (var1 != null) {
         var1.Group = this;
         if (!this.Members.contains(var1)) {
            this.Members.add(var1);
         }

         if (this.Leader == null || this.Leader.Group != this) {
            this.Leader = var1;
         }

      }
   }

   public boolean isInstanced() {
      return this.Leader != null && this.Leader.getInstance() != null;
   }

   public void removeMember(SurvivorDesc var1) {
      this.Members.remove(var1);
      if (this.Leader == var1) {
         this.pickNewLeader();
      }

   }

   public boolean isMember(SurvivorDesc var1) {
      return this.Members.contains(var1);
   }

   public boolean isMember(IsoGameCharacter var1) {
      return var1.descriptor == null ? false : this.Members.contains(var1.descriptor);
   }

   public boolean isLeader(SurvivorDesc var1) {
      return this.Leader == var1;
   }

   public boolean isLeader(IsoGameCharacter var1) {
      if (var1.descriptor == null) {
         return false;
      } else {
         return this.Leader == var1.descriptor;
      }
   }

   public void update() {
      for(int var1 = 0; var1 < this.Members.size(); ++var1) {
         if (((SurvivorDesc)this.Members.get(var1)).Group != this || ((SurvivorDesc)this.Members.get(var1)).bDead) {
            this.removeMember((SurvivorDesc)this.Members.get(var1));
            --var1;
         }
      }

      if (this.Leader == null || this.Leader.Group != this || this.Leader.Instance != null && this.Leader.Instance.isDead()) {
         this.pickNewLeader();
      }

      if (!this.Members.contains(this.Leader) && this.Leader != null) {
         this.Members.add(this.Leader);
      }

   }

   private void pickNewLeader() {
      if (!this.Members.isEmpty()) {
         this.setLeader((SurvivorDesc)this.Members.get(Rand.Next(this.Members.size())));
      }
   }

   IsoGameCharacter getRandomMemberExcept(IsoGameCharacter var1) {
      if (this.Members.size() == 1) {
         return null;
      } else {
         IsoGameCharacter var2 = null;

         do {
            var2 = ((SurvivorDesc)this.Members.get(Rand.Next(this.Members.size()))).Instance;
         } while(var2 == var1);

         return var2;
      }
   }

   public boolean HasOtherMembers(SurvivorDesc var1) {
      return this.Members.contains(var1) && this.Members.size() > 1;
   }

   public int getTotalNeedPriority() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.GroupNeeds.size(); ++var2) {
         var1 += ((Need)this.GroupNeeds.get(var2)).priority;
      }

      return var1;
   }

   public void AddNeed(String var1, int var2) {
      for(int var3 = 0; var3 < this.GroupNeeds.size(); ++var3) {
         if (((Need)this.GroupNeeds.get(var3)).item.equals(var1)) {
            ++((Need)this.GroupNeeds.get(var3)).numToSatisfy;
            if (((Need)this.GroupNeeds.get(var3)).priority < var2) {
               ((Need)this.GroupNeeds.get(var3)).priority = var2;
            }

            return;
         }
      }

      this.GroupNeeds.add(new Need(var1, var2));
   }

   public boolean HasNeed(String var1) {
      for(int var2 = 0; var2 < this.GroupNeeds.size(); ++var2) {
         if (((Need)this.GroupNeeds.get(var2)).item.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public void setLuaTable(KahluaTable var1) {
      this.luaGroup = var1;
   }

   public void setSafehouse(IsoBuilding var1) {
      this.Safehouse = var1;
      LuaEventManager.triggerEvent("OnPlayerSetSafehouse", this.luaGroup, var1.def);
   }

   public void instanceGroup(int var1, int var2) {
      int var3;
      SurvivorDesc var4;
      for(var3 = 0; var3 < this.Members.size(); ++var3) {
         var4 = (SurvivorDesc)this.Members.get(var3);
         if (var4.Instance == null) {
            var4.Instance = new IsoSurvivor(var4, IsoWorld.instance.CurrentCell, var1, var2, 0);
         }
      }

      for(var3 = 0; var3 < this.Members.size(); ++var3) {
         var4 = (SurvivorDesc)this.Members.get(var3);
         if (var4 != this.Leader) {
         }
      }

   }

   public void Despawn() {
      for(int var1 = 0; var1 < this.Members.size(); ++var1) {
         SurvivorDesc var2 = (SurvivorDesc)this.Members.get(var1);
         if (var2.Instance != null && !(var2.Instance instanceof IsoPlayer)) {
            IsoWorld.instance.CurrentCell.Remove(var2.Instance);
            var2.Instance = null;
         }
      }

   }
}
