package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public final class RVSUtilityVehicle extends RandomizedVehicleStoryBase {
	private ArrayList tools = null;
	private ArrayList carpenterTools = null;

	public RVSUtilityVehicle() {
		this.name = "Utility Vehicle";
		this.minZoneWidth = 3;
		this.minZoneHeight = 3;
		this.setChance(7);
		this.tools = new ArrayList();
		this.tools.add("Base.PickAxe");
		this.tools.add("Base.Shovel");
		this.tools.add("Base.Shovel2");
		this.tools.add("Base.Hammer");
		this.tools.add("Base.LeadPipe");
		this.tools.add("Base.PipeWrench");
		this.tools.add("Base.Sledgehammer");
		this.tools.add("Base.Sledgehammer2");
		this.carpenterTools = new ArrayList();
		this.carpenterTools.add("Base.Hammer");
		this.carpenterTools.add("Base.NailsBox");
		this.carpenterTools.add("Base.Plank");
		this.carpenterTools.add("Base.Plank");
		this.carpenterTools.add("Base.Plank");
		this.carpenterTools.add("Base.Screwdriver");
		this.carpenterTools.add("Base.Saw");
		this.carpenterTools.add("Base.Saw");
		this.carpenterTools.add("Base.Woodglue");
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		int int1 = Rand.Next(0, 7);
		switch (int1) {
		case 0: 
			this.doUtilityVehicle(zone, chunk, (String)null, "Base.PickUpTruck", "ConstructionWorker", 0, "ConstructionWorker", this.tools, Rand.Next(0, 3), true);
			break;
		
		case 1: 
			this.doUtilityVehicle(zone, chunk, "police", (String)null, "Police", (Integer)null, (String)null, (ArrayList)null, 0, false);
			break;
		
		case 2: 
			this.doUtilityVehicle(zone, chunk, "fire", (String)null, "Fireman", (Integer)null, (String)null, (ArrayList)null, 0, false);
			break;
		
		case 3: 
			this.doUtilityVehicle(zone, chunk, "ranger", (String)null, "Ranger", (Integer)null, (String)null, (ArrayList)null, 0, true);
			break;
		
		case 4: 
			this.doUtilityVehicle(zone, chunk, "mccoy", (String)null, "McCoys", 0, "Carpenter", this.carpenterTools, Rand.Next(2, 6), true);
			break;
		
		case 5: 
			this.doUtilityVehicle(zone, chunk, "postal", (String)null, "Postal", (Integer)null, (String)null, (ArrayList)null, 0, false);
			break;
		
		case 6: 
			this.doUtilityVehicle(zone, chunk, "fossoil", (String)null, "Fossoil", (Integer)null, (String)null, (ArrayList)null, 0, false);
		
		}
	}

	public void doUtilityVehicle(IsoMetaGrid.Zone zone, IsoChunk chunk, String string, String string2, String string3, Integer integer, String string4, ArrayList arrayList, int int1, boolean boolean1) {
		IsoGridSquare square = this.getCenterOfChunk(zone, chunk);
		if (square != null) {
			IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
			if (this.horizontalZone) {
				directions = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
			}

			BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, string, string2, (Integer)null, directions, string4);
			if (baseVehicle != null) {
				this.addZombiesOnVehicle(Rand.Next(2, 5), string3, integer, baseVehicle);
				if (boolean1 && Rand.NextBool(7)) {
					this.addTrailer(baseVehicle, zone, chunk, string, string4, Rand.NextBool(1) ? "Base.Trailer" : "Base.TrailerCover");
				}

				if (arrayList != null) {
					for (int int2 = 0; int2 < int1; ++int2) {
						IsoGridSquare square2 = IsoCell.getInstance().getGridSquare((double)(square.x + Rand.Next(-4, 4)), (double)(square.y + Rand.Next(-4, 4)), (double)baseVehicle.z);
						if (square2 != null) {
							square2.AddWorldInventoryItem((String)arrayList.get(Rand.Next(arrayList.size())), 0.3F, 0.3F, 0.0F);
						}
					}
				}
			}
		}
	}
}
