package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Stack;
import zombie.core.Rand;
import zombie.inventory.ItemContainerFiller;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.scripting.ScriptManager;


public class ShelfDistribution extends BaseScriptObject {
	public String Zone;
	public int LootedValue = 0;
	public ArrayList Entries = new ArrayList(1);
	private float ItemDepth = 0.5F;

	public void Load(String string, String[] stringArray) {
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray2[int2];
			string2 = string2.trim();
			String[] stringArray3 = string2.split("=");
			if (stringArray3.length == 2) {
				this.DoLine(stringArray3[0].trim(), stringArray3[1].trim());
			} else if (stringArray3[0].trim().length() > 0) {
				this.Entries.add(new ShelfDistribution.Entry(stringArray3[0].trim(), 1, 1));
			}
		}
	}

	private void DoLine(String string, String string2) {
		if (string.equals("Zone")) {
			this.Zone = string2;
		}

		if (string.equals("LootedValue")) {
			this.LootedValue = Integer.parseInt(string2);
		}

		if (string.equals("ItemDepth")) {
			this.ItemDepth = Float.parseFloat(string2);
		}
	}

	public void Process(IsoCell cell) {
		Stack stack = ScriptManager.instance.getZones(this.Zone);
		for (int int1 = 0; int1 < stack.size(); ++int1) {
			Zone zone = (Zone)stack.get(int1);
			for (int int2 = zone.x; int2 < zone.x2; ++int2) {
				for (int int3 = zone.y; int3 < zone.y2; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int2, int3, zone.z);
					if (square != null) {
						float float1;
						String string;
						if (square.getProperties().Is(IsoFlagType.floorS) || square.getProperties().Is(IsoFlagType.floorE)) {
							float1 = 0.10000001F;
							string = ((ShelfDistribution.Entry)this.Entries.get(Rand.Next(this.Entries.size()))).objectType;
							if (!string.contains(".")) {
								string = this.module.name + "." + string;
							}

							ItemContainerFiller.FillTable(this.LootedValue, square, (String)null, string, float1, this.ItemDepth);
						}

						if (square.getProperties().Is(IsoFlagType.tableS) || square.getProperties().Is(IsoFlagType.tableE)) {
							float1 = 0.4F;
							string = ((ShelfDistribution.Entry)this.Entries.get(Rand.Next(this.Entries.size()))).objectType;
							if (!string.contains(".")) {
								string = this.module.name + "." + string;
							}

							ItemContainerFiller.FillTable(this.LootedValue, square, (String)null, string, float1);
						}

						if (square.getProperties().Is(IsoFlagType.shelfE) || square.getProperties().Is(IsoFlagType.shelfS)) {
							float1 = 0.65F;
							string = ((ShelfDistribution.Entry)this.Entries.get(Rand.Next(this.Entries.size()))).objectType;
							if (!string.contains(".")) {
								string = this.module.name + "." + string;
							}

							ItemContainerFiller.FillTable(this.LootedValue, square, (String)null, string, float1, this.ItemDepth);
						}
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
