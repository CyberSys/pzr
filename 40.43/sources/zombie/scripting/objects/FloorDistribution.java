package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Stack;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.scripting.ScriptManager;


public class FloorDistribution extends BaseScriptObject {
	public String Zone;
	public ArrayList Entries = new ArrayList(1);

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
		if (string.equals("Zone")) {
			this.Zone = string2;
		} else {
			boolean boolean1 = false;
			boolean boolean2 = false;
			int int1;
			int int2;
			if (string2.contains("-")) {
				String[] stringArray = string2.split("-");
				int1 = Integer.parseInt(stringArray[0].trim());
				int2 = Integer.parseInt(stringArray[1].trim());
			} else {
				int1 = int2 = Integer.parseInt(string2.trim());
			}

			FloorDistribution.Entry entry = new FloorDistribution.Entry(string.trim(), int1, int2);
			this.Entries.add(entry);
		}
	}

	public void Process(IsoCell cell) {
		Stack stack = ScriptManager.instance.getZones(this.Zone);
		if (!stack.isEmpty()) {
			for (int int1 = 0; int1 < this.Entries.size(); ++int1) {
				int int2 = Rand.Next(((FloorDistribution.Entry)this.Entries.get(int1)).minimum, ((FloorDistribution.Entry)this.Entries.get(int1)).maximum);
				for (int int3 = 0; int3 < int2; ++int3) {
					Zone zone = (Zone)stack.get(Rand.Next(stack.size()));
					IsoGridSquare square = cell.getFreeTile(new IsoCell.Zone(zone.name, zone.x, zone.y, zone.x2 - zone.x, zone.y2 - zone.y, zone.z));
					if (square != null) {
						String string = ((FloorDistribution.Entry)this.Entries.get(int1)).objectType;
						if (!string.contains(".")) {
							string = this.module.name + "." + string;
						}

						square.AddWorldInventoryItem(string, (float)(100 + Rand.Next(400)) / 1000.0F, (float)(100 + Rand.Next(400)) / 1000.0F, 0.0F);
					}
				}
			}
		}
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
