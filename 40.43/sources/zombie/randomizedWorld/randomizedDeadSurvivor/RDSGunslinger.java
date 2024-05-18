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

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("WeaponUpgrades");
		if (kahluaTable != null) {
			IsoGridSquare square = buildingDef.getFreeSquareInRoom();
			if (square != null) {
				IsoDeadBody deadBody = super.createRandomDeadBody(square.getX(), square.getY(), square.getZ());
				int int1 = Rand.Next(1, 6);
				int int2 = Rand.Next(3, 7);
				int int3;
				for (int3 = 0; int3 < int1; ++int3) {
					HandWeapon handWeapon = (HandWeapon)deadBody.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.weaponsList.get(Rand.Next(0, this.weaponsList.size()))));
					if (handWeapon != null) {
						KahluaTable kahluaTable2 = (KahluaTable)kahluaTable.rawget(handWeapon.getType());
						if (kahluaTable2 != null) {
							int int4 = Rand.Next(1, kahluaTable2.len() + 1);
							for (int int5 = 1; int5 <= int4; ++int5) {
								WeaponPart weaponPart = (WeaponPart)InventoryItemFactory.CreateItem((String)kahluaTable2.rawget(int5));
								handWeapon.attachWeaponPart(weaponPart);
							}
						}
					}
				}

				for (int3 = 0; int3 < int2; ++int3) {
					deadBody.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.ammoList.get(Rand.Next(0, this.ammoList.size()))));
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
