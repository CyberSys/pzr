package zombie.scripting.objects;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemContainerFiller;
import zombie.iso.IsoCell;
import zombie.iso.areas.IsoRoom;


public class ContainerDistribution extends BaseScriptObject {
	public String RoomDef;
	public ArrayList Containers = new ArrayList(1);
	public ArrayList Entries = new ArrayList(1);
	static ArrayList roomTemp = new ArrayList();

	public void Load(String string, String[] stringArray) {
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray2[int2];
			string2 = string2.trim();
			String[] stringArray3 = string2.split("=");
			if (stringArray3.length == 2) {
				this.DoLine(stringArray3[0].trim(), stringArray3[1].trim());
			}
		}
	}

	private void DoLine(String string, String string2) {
		if (string.equals("Room")) {
			this.RoomDef = string2;
		} else if (string.equals("Containers")) {
			String[] stringArray = string2.split("/");
			String[] stringArray2 = stringArray;
			int int1 = stringArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				String string3 = stringArray2[int2];
				this.Containers.add(string3.trim());
			}
		} else {
			boolean boolean1 = false;
			boolean boolean2 = false;
			int int3;
			int int4;
			if (string2.contains("-")) {
				String[] stringArray3 = string2.split("-");
				int3 = Integer.parseInt(stringArray3[0].trim());
				int4 = Integer.parseInt(stringArray3[1].trim());
			} else {
				int3 = int4 = Integer.parseInt(string2.trim());
			}

			ContainerDistribution.Entry entry = new ContainerDistribution.Entry(string.trim(), int3, int4);
			this.Entries.add(entry);
		}
	}

	public boolean ContainerValid(String string) {
		if (this.Containers.isEmpty()) {
			return true;
		} else {
			for (int int1 = 0; int1 < this.Containers.size(); ++int1) {
				if (((String)this.Containers.get(int1)).equals(string)) {
					return true;
				}
			}

			return false;
		}
	}

	public void Process(IsoCell cell) {
		int int1;
		if (this.RoomDef != null) {
			ArrayList arrayList = this.FindRooms(cell);
			if (arrayList.isEmpty()) {
				return;
			}

			IsoRoom room = null;
			for (int1 = 0; int1 < this.Entries.size(); ++int1) {
				ContainerDistribution.Entry entry = (ContainerDistribution.Entry)this.Entries.get(int1);
				int int2 = Rand.Next(entry.minimum, entry.maximum);
				for (int int3 = 0; int3 < int2; ++int3) {
					room = (IsoRoom)arrayList.get(Rand.Next(arrayList.size()));
					if (room != null && !room.Containers.isEmpty()) {
						int int4 = Rand.Next(room.Containers.size());
						if (this.ContainerValid(((ItemContainer)room.Containers.get(int4)).type)) {
							ItemContainer itemContainer = (ItemContainer)room.Containers.get(int4);
							String string = entry.objectType;
							if (!string.contains(".")) {
								string = this.module.name + "." + string;
							}

							itemContainer.AddItem(string);
						} else {
							boolean boolean1 = false;
							for (int int5 = 0; int5 < room.Containers.size(); ++int5) {
								if (this.ContainerValid(((ItemContainer)room.Containers.get(int5)).type)) {
									boolean1 = true;
								}
							}

							if (boolean1) {
								--int3;
							}
						}
					} else {
						--int3;
					}
				}
			}
		} else {
			for (int int6 = 0; int6 < this.Entries.size(); ++int6) {
				ContainerDistribution.Entry entry2 = (ContainerDistribution.Entry)this.Entries.get(int6);
				int1 = Rand.Next(entry2.minimum, entry2.maximum);
				for (int int7 = 0; int7 < int1; ++int7) {
					ItemContainer itemContainer2 = this.getRandomContainer();
					if (itemContainer2 != null) {
						String string2 = entry2.objectType;
						if (!string2.contains(".")) {
							string2 = this.module.name + "." + string2;
						}

						itemContainer2.AddItem(string2);
					}
				}
			}
		}
	}

	private ItemContainer getRandomContainer() {
		ArrayList arrayList = new ArrayList();
		if (ItemContainerFiller.DistributionTarget.isEmpty()) {
			return null;
		} else {
			boolean boolean1 = false;
			int int1 = 2000;
			while (!boolean1) {
				--int1;
				if (int1 <= 0) {
					return null;
				}

				for (int int2 = 0; int2 < ItemContainerFiller.Containers.size(); ++int2) {
					for (int int3 = 0; int3 < this.Containers.size(); ++int3) {
						if (((ItemContainer)ItemContainerFiller.Containers.get(int2)).type.equals(this.Containers.get(int3))) {
							arrayList.add(ItemContainerFiller.Containers.get(int2));
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

	private IsoRoom FindRoom(IsoCell cell) {
		roomTemp.clear();
		for (int int1 = 0; int1 < cell.getRoomList().size(); ++int1) {
			IsoRoom room = (IsoRoom)cell.getRoomList().get(int1);
			if (room.RoomDef != null && room.RoomDef.equals(this.RoomDef)) {
				roomTemp.add(room);
			}
		}

		if (!roomTemp.isEmpty()) {
			return (IsoRoom)roomTemp.get(Rand.Next(roomTemp.size()));
		} else {
			return null;
		}
	}

	private ArrayList FindRooms(IsoCell cell) {
		roomTemp.clear();
		for (int int1 = 0; int1 < cell.getRoomList().size(); ++int1) {
			IsoRoom room = (IsoRoom)cell.getRoomList().get(int1);
			if (room.RoomDef != null && room.RoomDef.equals(this.RoomDef)) {
				roomTemp.add(room);
			}
		}

		return roomTemp;
	}

	public class Entry {
		String objectType;
		int minimum;
		int maximum;

		public Entry(String string, int int1, int int2) {
			this.objectType = string;
			this.minimum = int1;
			this.maximum = int2;
		}
	}
}
