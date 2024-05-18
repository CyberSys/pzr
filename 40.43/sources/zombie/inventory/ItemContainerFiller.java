package zombie.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;


public class ItemContainerFiller {
	public static ArrayList DistributionTarget = new ArrayList();
	public static ArrayList Containers = new ArrayList();

	public static void DistributeGoodItems(IsoCell cell) {
		PlaceOnRandomFloor(cell, "foodItems", "TinnedSoup", 8);
		PlaceOnRandomFloor(cell, "foodItems", "Crisps", 8);
		PlaceOnRandomFloor(cell, "foodItems", "Crisps2", 8);
		PlaceOnRandomFloor(cell, "foodItems", "Crisps3", 8);
		PlaceOnRandomFloor(cell, "foodItems", "Pop", 8);
		PlaceOnRandomFloor(cell, "foodItems", "Pop2", 8);
		PlaceOnRandomFloor(cell, "foodItems", "Pop3", 8);
		int int1;
		ItemContainer itemContainer;
		for (int1 = 0; int1 < 6; ++int1) {
			itemContainer = getRandomContainer("counter,wardrobe,crate");
			if (itemContainer != null) {
				itemContainer.AddItem("Shotgun");
				itemContainer.AddItem("ShotgunShells");
			}
		}

		for (int1 = 0; int1 < 15; ++int1) {
			itemContainer = getRandomContainer("counter,wardrobe,crate");
			if (itemContainer != null) {
				itemContainer.AddItem("ShotgunShells");
			}
		}

		for (int1 = 0; int1 < 6; ++int1) {
			itemContainer = getRandomContainer("counter,wardrobe,crate");
			if (itemContainer != null) {
				itemContainer.AddItem("Shotgun");
			}
		}

		for (int1 = 0; int1 < 8; ++int1) {
			itemContainer = getRandomContainer("counter,wardrobe,crate");
			if (itemContainer != null) {
				itemContainer.AddItem("BaseballBat");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("counter,crate,sidetable");
			if (itemContainer != null) {
				itemContainer.AddItem("Battery");
			}
		}

		for (int1 = 0; int1 < 6; ++int1) {
			itemContainer = getRandomContainer("crate");
			if (itemContainer != null) {
				itemContainer.AddItem("PetrolCan");
			}
		}

		for (int1 = 0; int1 < 6; ++int1) {
			itemContainer = getRandomContainer("crate,counter");
			if (itemContainer != null) {
				itemContainer.AddItem("Hammer");
			}
		}

		for (int1 = 0; int1 < 1; ++int1) {
			itemContainer = getRandomContainer("crate,counter");
			if (itemContainer != null) {
				itemContainer.AddItem("Axe");
			}
		}

		for (int1 = 0; int1 < 4; ++int1) {
			itemContainer = getRandomContainer("crate,counter");
			if (itemContainer != null) {
				itemContainer.AddItem("Axe");
			}
		}

		for (int1 = 0; int1 < 60; ++int1) {
			itemContainer = getRandomContainer("counter,crate,sidetable");
			if (itemContainer != null) {
				itemContainer.AddItem("Nails");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("wardrobe");
			if (itemContainer != null) {
				itemContainer.AddItem("Sheet");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("wardrobe");
			if (itemContainer != null) {
				itemContainer.AddItem("Belt");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("wardrobe");
			if (itemContainer != null) {
				itemContainer.AddItem("Socks");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("counter,crate,sidetable");
			if (itemContainer != null) {
				itemContainer.AddItem("Lighter");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("counter,crate,sidetable,fridge");
			if (itemContainer != null) {
				itemContainer.AddItem("WhiskeyFull");
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			itemContainer = getRandomContainer("vendingsnacks");
			if (itemContainer != null) {
				itemContainer.AddItem("Crisps");
				itemContainer.AddItem("Crisps2");
				itemContainer.AddItem("Crisps3");
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			itemContainer = getRandomContainer("vendingpop");
			if (itemContainer != null) {
				itemContainer.AddItem("Pop");
				itemContainer.AddItem("Pop2");
				itemContainer.AddItem("Pop3");
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			itemContainer = getRandomContainer("counter,crate,sidetable,fridge");
			if (itemContainer != null) {
				itemContainer.AddItem("Chocolate");
			}
		}

		for (int1 = 0; int1 < 5; ++int1) {
			itemContainer = getRandomContainer("counter,crate,sidetable,fridge");
			if (itemContainer != null) {
				itemContainer.AddItem("Torch");
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			itemContainer = getRandomContainer("fridge");
			if (itemContainer != null) {
				itemContainer.AddItem("Bread");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("counter");
			if (itemContainer != null) {
				itemContainer.AddItem("DishCloth");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("counter,sidetable");
			if (itemContainer != null) {
				itemContainer.AddItem("Pen");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("counter,sidetable");
			if (itemContainer != null) {
				itemContainer.AddItem("Pencil");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("fridge");
			if (itemContainer != null) {
				itemContainer.AddItem("Carrots");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("fridge");
			if (itemContainer != null) {
				itemContainer.AddItem("Steak");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("counter");
			if (itemContainer != null) {
				itemContainer.AddItem("Carrots");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("counter");
			if (itemContainer != null) {
				itemContainer.AddItem("Steak");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("medicine");
			if (itemContainer != null) {
				itemContainer.AddItem("Pills");
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			itemContainer = getRandomContainer("medicine");
			if (itemContainer != null) {
				itemContainer.AddItem("PillsBeta");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("medicine");
			if (itemContainer != null) {
				itemContainer.AddItem("PillsSleepingTablets");
			}
		}

		for (int1 = 0; int1 < 10; ++int1) {
			itemContainer = getRandomContainer("medicine");
			if (itemContainer != null) {
				itemContainer.AddItem("PillsAntiDep");
			}
		}

		for (int1 = 0; int1 < 20; ++int1) {
			itemContainer = getRandomContainer("fridge");
			if (itemContainer != null) {
				itemContainer.AddItem("Apple");
			}
		}

		for (int1 = 0; int1 < 6; ++int1) {
			itemContainer = getRandomContainer("counter");
			if (itemContainer != null) {
				itemContainer.AddItem("TinOpener");
			}
		}

		for (int1 = 0; int1 < 30; ++int1) {
			itemContainer = getRandomContainer("crate");
			if (itemContainer != null) {
				itemContainer.AddItem("Plank");
			}
		}

		for (int1 = 0; int1 < 3; ++int1) {
			itemContainer = getRandomContainer("counter,wardrobe,crate");
			if (itemContainer != null) {
				itemContainer.AddItem("BaseballBat");
			}
		}

		for (int1 = 0; int1 < 12; ++int1) {
			itemContainer = getRandomContainer("counter,wardrobe,crate");
			if (itemContainer != null) {
				itemContainer.AddItem("ShotgunShells");
			}
		}
	}

	public static void FillContainer(ItemContainer itemContainer, String string) {
		String string2 = itemContainer.type;
		if (string2.equals("counter")) {
			DoCounter(itemContainer, string);
		}

		if (string2.equals("wardrobe")) {
			DoWardrobe(itemContainer, string);
		}

		if (string2.equals("medicine")) {
			DoMedicine(itemContainer, string);
		}

		if (string.equals("rangerHut") && itemContainer.type.equals("counter")) {
			itemContainer.AddItem("Axe");
		}

		if (string.equals("tutKitchen2") && itemContainer.type.equals("fridge")) {
			itemContainer.AddItem("Carrots");
			itemContainer.AddItem("Apple");
		}
	}

	public static void FillRoom(IsoRoom room) {
		if (room.RoomDef.equals("shopBig")) {
			DoShopBig(room);
		}

		if (room.RoomDef.equals("bar")) {
			DoBar(room);
		}

		Iterator iterator = room.Containers.iterator();
		while (iterator.hasNext()) {
			ItemContainer itemContainer = (ItemContainer)iterator.next();
			FillContainer(itemContainer, room.RoomDef);
		}
	}

	public static void FillTable(IsoGridSquare square, String string) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (square.getProperties().Is(IsoFlagType.tableE)) {
			boolean1 = true;
		}

		if (square.getProperties().Is(IsoFlagType.tableS)) {
			boolean2 = true;
		}

		int int1;
		float float1;
		float float2;
		float float3;
		if (boolean1) {
			for (int1 = 0; int1 < 3; ++int1) {
				float1 = 0.5F;
				float2 = 0.45F + (float)Rand.Next(10) / 200.0F;
				float3 = (float)int1 * 0.33F;
				if (Rand.Next(5) == 0 || string.equals("shopGeneral") || string.equals("tutKitchen1")) {
					AddShelfItem(string, square, float2, float3, float1);
				}
			}
		}

		if (boolean2) {
			for (int1 = 0; int1 < 3; ++int1) {
				float1 = 0.5F;
				float2 = 0.45F + (float)Rand.Next(10) / 200.0F;
				float3 = (float)int1 * 0.33F;
				if (Rand.Next(5) == 0 || string.equals("shopGeneral") || string.equals("tutKitchen1")) {
					AddShelfItem(string, square, float3, float2, float1);
				}
			}
		}
	}

	public static void FillTable(IsoGridSquare square, String string, String string2, float float1) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (square.getProperties().Is(IsoFlagType.tableE)) {
			boolean1 = true;
		}

		if (square.getProperties().Is(IsoFlagType.tableS)) {
			boolean2 = true;
		}

		int int1;
		float float2;
		float float3;
		if (boolean1) {
			for (int1 = 0; int1 < 5; ++int1) {
				float2 = 0.8F;
				float3 = (float)int1 * 0.2F;
				AddShelfItem(string, square, float2, float3, float1, string2);
			}
		}

		if (boolean2) {
			for (int1 = 0; int1 < 5; ++int1) {
				float2 = 0.8F;
				float3 = (float)int1 * 0.2F;
				AddShelfItem(string, square, float3, float2, float1, string2);
			}
		}
	}

	public static void FillTable(int int1, IsoGridSquare square, String string, String string2, float float1) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (square.getProperties().Is(IsoFlagType.tableE)) {
			boolean1 = true;
		}

		if (square.getProperties().Is(IsoFlagType.tableS)) {
			boolean2 = true;
		}

		int int2;
		float float2;
		float float3;
		if (boolean1) {
			for (int2 = 0; int2 < 5; ++int2) {
				if (Rand.Next(int1) == 0) {
					float2 = 0.8F;
					float3 = (float)int2 * 0.2F;
					AddShelfItem(string, square, float2, float3, float1, string2);
				}
			}
		}

		if (boolean2) {
			for (int2 = 0; int2 < 5; ++int2) {
				if (Rand.Next(int1) == 0) {
					float2 = 0.8F;
					float3 = (float)int2 * 0.2F;
					AddShelfItem(string, square, float3, float2, float1, string2);
				}
			}
		}
	}

	public static void FillTable(int int1, IsoGridSquare square, String string, String string2, float float1, float float2) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (square.getProperties().Is(IsoFlagType.tableE)) {
			boolean1 = true;
		}

		if (square.getProperties().Is(IsoFlagType.tableS)) {
			boolean2 = true;
		}

		int int2;
		float float3;
		if (boolean1) {
			for (int2 = 0; int2 < 5; ++int2) {
				if (Rand.Next(int1) == 0) {
					float3 = (float)int2 * 0.2F;
					AddShelfItem(string, square, float2, float3, float1, string2);
				}
			}
		}

		if (boolean2) {
			for (int2 = 0; int2 < 5; ++int2) {
				if (Rand.Next(int1) == 0) {
					float3 = (float)int2 * 0.2F;
					AddShelfItem(string, square, float3, float2, float1, string2);
				}
			}
		}
	}

	public static void FillTable(IsoGridSquare square, String string, String string2, float float1, float float2) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (square.getProperties().Is(IsoFlagType.tableE)) {
			boolean1 = true;
		}

		if (square.getProperties().Is(IsoFlagType.tableS)) {
			boolean2 = true;
		}

		int int1;
		float float3;
		if (boolean1) {
			for (int1 = 0; int1 < 5; ++int1) {
				if (Rand.Next(4) == 0) {
					float3 = (float)int1 * 0.2F;
					AddShelfItem(string, square, float2, float3, float1, string2);
				}
			}
		}

		if (boolean2) {
			for (int1 = 0; int1 < 5; ++int1) {
				if (Rand.Next(4) == 0) {
					float3 = (float)int1 * 0.2F;
					AddShelfItem(string, square, float3, float2, float1, string2);
				}
			}
		}
	}

	static void AddShelfItem(String string, IsoGridSquare square, float float1, float float2, float float3) {
		if (string.equals("tutKitchen1") && square.getX() == 40 && square.getY() == 25 && float1 == 0.33F) {
			square.AddWorldInventoryItem("Pot", float1, float2, float3);
		}

		if (string.equals("kitchen")) {
			switch (Rand.Next(4)) {
			case 0: 
				square.AddWorldInventoryItem("WhiskeyHalf", float1, float2, float3);
				break;
			
			case 1: 
				square.AddWorldInventoryItem("WhiskeyFull", float1, float2, float3);
				break;
			
			case 2: 
				square.AddWorldInventoryItem("Bread", float1, float2, float3);
				break;
			
			case 3: 
				square.AddWorldInventoryItem("TinnedSoup", float1, float2, float3);
			
			}
		}
	}

	private static void AddShelfItem(String string, IsoGridSquare square, float float1, float float2, float float3, String string2) {
		square.AddWorldInventoryItem(string2, float1, float2, float3);
	}

	private static void AddToRandomContainer(IsoRoom room, String string) {
		int int1 = Rand.Next(room.Containers.size());
		((ItemContainer)room.Containers.get(int1)).AddItem(string);
	}

	private static void AddToRandomContainer(IsoRoom room, String string, String string2) {
		Stack stack = new Stack();
		Iterator iterator = room.Containers.iterator();
		while (iterator.hasNext()) {
			ItemContainer itemContainer = (ItemContainer)iterator.next();
			if (itemContainer.type.equals(string2)) {
				stack.add(itemContainer);
			}
		}

		int int1 = Rand.Next(stack.size());
		((ItemContainer)stack.get(int1)).AddItem(string);
	}

	private static void DoBar(IsoRoom room) {
		Iterator iterator = room.TileList.iterator();
		while (true) {
			IsoGridSquare square;
			boolean boolean1;
			int int1;
			IsoObject object;
			String string;
			label108: do {
				do {
					while (true) {
						if (!iterator.hasNext()) {
							return;
						}

						square = (IsoGridSquare)iterator.next();
						if (!square.getProperties().Is(IsoFlagType.shelfS)) {
							break;
						}

						boolean1 = false;
						for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
							object = (IsoObject)square.getObjects().get(int1);
							if (object.container != null && object.container.type.equals("counter")) {
								boolean1 = true;
							}
						}

						if (!boolean1) {
							string = "WhiskeyFull";
							switch (Rand.Next(5)) {
							case 0: 
								string = "WhiskeyFull";
								break;
							
							case 1: 
								string = "WhiskeyHalf";
								break;
							
							case 2: 
								string = "WhiskeyEmpty";
								break;
							
							case 3: 
								string = "WineEmpty";
								break;
							
							case 4: 
								string = "WineEmpty2";
							
							}

							FillTable(square, room.RoomDef, string, 0.75F);
							break;
						}
					}

					if (!square.getProperties().Is(IsoFlagType.tableS)) {
						continue label108;
					}

					boolean1 = false;
					for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
						object = (IsoObject)square.getObjects().get(int1);
						if (object.container != null && object.container.type.equals("counter")) {
							boolean1 = true;
						}
					}
				}		 while (boolean1);

				string = "WhiskeyFull";
				switch (Rand.Next(5)) {
				case 0: 
					string = "WhiskeyFull";
					break;
				
				case 1: 
					string = "WhiskeyHalf";
					break;
				
				case 2: 
					string = "WhiskeyEmpty";
					break;
				
				case 3: 
					string = "WineEmpty";
					break;
				
				case 4: 
					string = "WineEmpty2";
				
				}

				FillTable(square, room.RoomDef, string, 0.45F);
			}	 while (!square.getProperties().Is(IsoFlagType.floorS));

			boolean1 = false;
			for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
				object = (IsoObject)square.getObjects().get(int1);
				if (object.container != null && object.container.type.equals("counter")) {
					boolean1 = true;
				}
			}

			if (!boolean1) {
				string = "WhiskeyFull";
				switch (Rand.Next(5)) {
				case 0: 
					string = "WhiskeyFull";
					break;
				
				case 1: 
					string = "WhiskeyHalf";
					break;
				
				case 2: 
					string = "WhiskeyEmpty";
					break;
				
				case 3: 
					string = "WineEmpty";
					break;
				
				case 4: 
					string = "WineEmpty2";
				
				}

				FillTable(square, room.RoomDef, string, 0.15F);
			}
		}
	}

	private static void DoCounter(ItemContainer itemContainer, String string) {
		FillTable(itemContainer.SourceGrid, string);
		if (!string.equals("tutKitchen2")) {
			int int1;
			if (string.equals("shopGeneral")) {
				int int2 = Rand.Next(3) + 1;
				for (int1 = 0; int1 < int2; ++int1) {
					itemContainer.AddItem("Bread");
				}

				int2 = Rand.Next(3) + 1;
				for (int1 = 0; int1 < int2; ++int1) {
					itemContainer.AddItem("WhiskeyFull");
				}

				if (Rand.Next(10) == 0) {
					itemContainer.AddItem("BaseballBat");
				}
			} else if (string.equals("shed")) {
				byte byte1 = 10;
				for (int1 = 0; int1 < byte1; ++int1) {
					itemContainer.AddItem("Plank");
				}

				byte1 = 3;
				for (int1 = 0; int1 < byte1; ++int1) {
					itemContainer.AddItem("Nails");
				}
			}
		}
	}

	private static void DoMedicine(ItemContainer itemContainer, String string) {
		if (string.equals("tutorialBathroom")) {
			itemContainer.AddItem("Pills");
		}
	}

	private static void DoShopBig(IsoRoom room) {
		Iterator iterator = room.TileList.iterator();
		while (true) {
			IsoGridSquare square;
			boolean boolean1;
			int int1;
			IsoObject object;
			String string;
			label80: do {
				do {
					if (!iterator.hasNext()) {
						return;
					}

					square = (IsoGridSquare)iterator.next();
					if (!square.getProperties().Is(IsoFlagType.tableS)) {
						continue label80;
					}

					boolean1 = false;
					for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
						object = (IsoObject)square.getObjects().get(int1);
						if (object.container != null && object.container.type.equals("counter")) {
							boolean1 = true;
						}
					}
				}		 while (boolean1);

				string = "None";
				switch (Rand.Next(9)) {
				case 0: 
					string = "Bread";
					break;
				
				case 1: 
					string = "TinnedSoup";
					break;
				
				case 2: 
					string = "WhiskeyFull";
					break;
				
				case 3: 
					string = "Pop";
					break;
				
				case 4: 
					string = "Pop2";
					break;
				
				case 5: 
					string = "Pop3";
					break;
				
				case 6: 
					string = "Crisps";
					break;
				
				case 7: 
					string = "Crisps2";
					break;
				
				case 8: 
					string = "Crisps3";
				
				}

				FillTable(square, room.RoomDef, string, 0.5F);
			}	 while (!square.getProperties().Is(IsoFlagType.floorS));

			boolean1 = false;
			for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
				object = (IsoObject)square.getObjects().get(int1);
				if (object.container != null && object.container.type.equals("counter")) {
					boolean1 = true;
				}
			}

			if (!boolean1) {
				string = "None";
				switch (Rand.Next(9)) {
				case 0: 
					string = "Bread";
					break;
				
				case 1: 
					string = "TinnedSoup";
					break;
				
				case 2: 
					string = "WhiskeyFull";
					break;
				
				case 3: 
					string = "Pop";
					break;
				
				case 4: 
					string = "Pop2";
					break;
				
				case 5: 
					string = "Pop3";
					break;
				
				case 6: 
					string = "Crisps";
					break;
				
				case 7: 
					string = "Crisps2";
					break;
				
				case 8: 
					string = "Crisps3";
				
				}

				FillTable(square, room.RoomDef, string, 0.15F);
			}
		}
	}

	private static void DoWardrobe(ItemContainer itemContainer, String string) {
		if (string.equals("tutorialBedroom")) {
			itemContainer.AddItem("Sheet");
			itemContainer.AddItem("Pillow");
		}
	}

	private static ItemContainer getRandomContainer(String string) {
		ArrayList arrayList = new ArrayList();
		if (DistributionTarget.isEmpty()) {
			return null;
		} else {
			String[] stringArray = string.split(",");
			boolean boolean1 = false;
			while (!boolean1) {
				int int1 = Rand.Next(DistributionTarget.size());
				IsoBuilding building = (IsoBuilding)DistributionTarget.get(int1);
				for (int int2 = 0; int2 < building.container.size(); ++int2) {
					for (int int3 = 0; int3 < stringArray.length; ++int3) {
						if (((ItemContainer)building.container.get(int2)).type.equals(stringArray[int3].trim())) {
							arrayList.add(building.container.get(int2));
						}
					}
				}

				if (!arrayList.isEmpty()) {
					boolean1 = true;
				}
			}

			return (ItemContainer)arrayList.get(Rand.Next(arrayList.size()));
		}
	}

	private static void PlaceOnRandomFloor(IsoCell cell, String string, String string2, int int1) {
		ArrayList arrayList = new ArrayList();
		String[] stringArray = string.split(",");
		boolean boolean1 = false;
		int int2;
		IsoCell.Zone zone;
		while (!boolean1) {
			for (int2 = 0; int2 < cell.getZoneStack().size(); ++int2) {
				zone = (IsoCell.Zone)cell.getZoneStack().get(int2);
				for (int int3 = 0; int3 < stringArray.length; ++int3) {
					if (zone.Name.equals(stringArray[int3].trim())) {
						arrayList.add(zone);
					}
				}
			}

			if (!arrayList.isEmpty()) {
				boolean1 = true;
			}
		}

		for (int2 = 0; int2 < int1; ++int2) {
			zone = (IsoCell.Zone)arrayList.get(Rand.Next(arrayList.size()));
			IsoGridSquare square = cell.getFreeTile(zone);
			if (square != null) {
				square.AddWorldInventoryItem(string2, (float)(100 + Rand.Next(400)) / 1000.0F, (float)(100 + Rand.Next(400)) / 1000.0F, 0.0F);
			}
		}
	}
}
