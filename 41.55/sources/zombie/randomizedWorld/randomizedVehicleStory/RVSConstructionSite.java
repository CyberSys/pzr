package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;


public final class RVSConstructionSite extends RandomizedVehicleStoryBase {
	private ArrayList tools = null;

	public RVSConstructionSite() {
		this.name = "Construction Site";
		this.minZoneWidth = 7;
		this.minZoneHeight = 4;
		this.setChance(3);
		this.tools = new ArrayList();
		this.tools.add("Base.PickAxe");
		this.tools.add("Base.Shovel");
		this.tools.add("Base.Shovel2");
		this.tools.add("Base.Hammer");
		this.tools.add("Base.LeadPipe");
		this.tools.add("Base.PipeWrench");
		this.tools.add("Base.Sledgehammer");
		this.tools.add("Base.Sledgehammer2");
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		IsoGridSquare square = IsoCell.getInstance().getGridSquare(this.minX, this.minY, zone.z);
		IsoGridSquare square2 = this.getCenterOfChunk(zone, chunk);
		if (square != null && square2 != null) {
			IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
			if (this.horizontalZone) {
				directions = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
			}

			BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, (String)null, "Base.PickUpTruck", (Integer)null, directions, "ConstructionWorker");
			this.addZombiesOnVehicle(Rand.Next(2, 5), "ConstructionWorker", 0, baseVehicle);
			this.addZombiesOnVehicle(1, "Foreman", 0, baseVehicle);
			square2.AddTileObject(IsoObject.getNew(square2, "street_decoration_01_15", (String)null, false));
			IsoGridSquare square3 = IsoCell.getInstance().getGridSquare((double)(square2.x + 1), (double)square2.y, (double)baseVehicle.z);
			if (square3 != null) {
				square3.AddTileObject(IsoObject.getNew(square3, "street_decoration_01_26", (String)null, false));
			}

			square3 = IsoCell.getInstance().getGridSquare((double)(square2.x - 1), (double)square2.y, (double)baseVehicle.z);
			if (square3 != null) {
				square3.AddTileObject(IsoObject.getNew(square3, "street_decoration_01_26", (String)null, false));
			}

			square3 = IsoCell.getInstance().getGridSquare((double)square2.x, (double)(square2.y + 1), (double)baseVehicle.z);
			if (square3 != null) {
				square3.AddTileObject(IsoObject.getNew(square3, "street_decoration_01_26", (String)null, false));
			}

			square3 = IsoCell.getInstance().getGridSquare((double)square2.x, (double)(square2.y - 1), (double)baseVehicle.z);
			if (square3 != null) {
				square3.AddTileObject(IsoObject.getNew(square3, "street_decoration_01_26", (String)null, false));
			}

			int int1 = Rand.Next(0, 3);
			for (int int2 = 0; int2 < int1; ++int2) {
				square3 = IsoCell.getInstance().getGridSquare((double)(square2.x + Rand.Next(-4, 4)), (double)(square2.y + Rand.Next(-4, 4)), (double)baseVehicle.z);
				if (square3 != null) {
					square3.AddWorldInventoryItem((String)this.tools.get(Rand.Next(this.tools.size())), 0.3F, 0.3F, 0.0F);
				}
			}
		}
	}
}
