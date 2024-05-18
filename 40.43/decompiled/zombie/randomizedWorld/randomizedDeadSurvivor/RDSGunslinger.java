package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;

public class RDSGunslinger extends RandomizedDeadSurvivorBase {
   private ArrayList weaponsList = new ArrayList();
   private ArrayList ammoList = new ArrayList();

   public void randomizeDeadSurvivor(BuildingDef var1) {
      KahluaTable var2 = (KahluaTable)LuaManager.env.rawget("WeaponUpgrades");
      if (var2 != null) {
         IsoGridSquare var3 = var1.getFreeSquareInRoom();
         if (var3 != null) {
            IsoDeadBody var4 = super.createRandomDeadBody(var3.getX(), var3.getY(), var3.getZ());
            int var5 = Rand.Next(1, 6);
            int var6 = Rand.Next(3, 7);

            int var7;
            for(var7 = 0; var7 < var5; ++var7) {
               HandWeapon var8 = (HandWeapon)var4.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.weaponsList.get(Rand.Next(0, this.weaponsList.size()))));
               if (var8 != null) {
                  KahluaTable var9 = (KahluaTable)var2.rawget(var8.getType());
                  if (var9 != null) {
                     int var10 = Rand.Next(1, var9.len() + 1);

                     for(int var11 = 1; var11 <= var10; ++var11) {
                        WeaponPart var12 = (WeaponPart)InventoryItemFactory.CreateItem((String)var9.rawget(var11));
                        var8.attachWeaponPart(var12);
                     }
                  }
               }
            }

            for(var7 = 0; var7 < var6; ++var7) {
               var4.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.ammoList.get(Rand.Next(0, this.ammoList.size()))));
            }

         }
      }
   }

   public RDSGunslinger() {
      this.weaponsList.add("Base.Shotgun");
      this.weaponsList.add("Base.Pistol");
      this.weaponsList.add("Base.VarmintRifle");
      this.weaponsList.add("Base.HuntingRifle");
      this.ammoList.add("Base.ShotgunShells");
      this.ammoList.add("Base.223Bullets");
      this.ammoList.add("Base.308Bullets");
      this.ammoList.add("Base.223Box");
      this.ammoList.add("Base.308Box");
      this.ammoList.add("Base.Bullets9mm");
      this.ammoList.add("Base.BulletsBox");
   }
}
