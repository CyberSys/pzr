package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import java.util.List;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;


public final class RVSUtilityVehicle extends RandomizedVehicleStoryBase {
	private ArrayList tools = null;
	private ArrayList carpenterTools = null;
	private RVSUtilityVehicle.Params params = new RVSUtilityVehicle.Params();

	public RVSUtilityVehicle() {
		this.name = "Utility Vehicle";
		this.minZoneWidth = 8;
		this.minZoneHeight = 9;
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
		this.callVehicleStorySpawner(zone, chunk, 0.0F);
	}

	public void doUtilityVehicle(IsoMetaGrid.Zone zone, IsoChunk chunk, String string, String string2, String string3, Integer integer, String string4, ArrayList arrayList, int int1, boolean boolean1) {
		this.params.zoneName = string;
		this.params.scriptName = string2;
		this.params.outfits = string3;
		this.params.femaleChance = integer;
		this.params.vehicleDistrib = string4;
		this.params.items = arrayList;
		this.params.nbrOfItem = int1;
		this.params.addTrailer = boolean1;
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
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
		VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
		vehicleStorySpawner.clear();
		Vector2 vector2 = IsoDirections.N.ToVector();
		float float1 = 0.5235988F;
		if (boolean1) {
			float1 = 0.0F;
		}

		vector2.rotate(Rand.Next(-float1, float1));
		float float2 = -2.0F;
		byte byte1 = 5;
		vehicleStorySpawner.addElement("vehicle1", 0.0F, float2, vector2.getDirection(), 2.0F, (float)byte1);
		if (this.params.addTrailer && Rand.NextBool(7)) {
			byte byte2 = 3;
			vehicleStorySpawner.addElement("trailer", 0.0F, float2 + (float)byte1 / 2.0F + 1.0F + (float)byte2 / 2.0F, vector2.getDirection(), 2.0F, (float)byte2);
		}

		if (this.params.items != null) {
			for (int int2 = 0; int2 < this.params.nbrOfItem; ++int2) {
				vehicleStorySpawner.addElement("tool", Rand.Next(-3.5F, 3.5F), Rand.Next(-3.5F, 3.5F), 0.0F, 1.0F, 1.0F);
			}
		}

		vehicleStorySpawner.setParameter("zone", zone);
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			BaseVehicle baseVehicle = (BaseVehicle)vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
			String string = element.id;
			byte byte1 = -1;
			switch (string.hashCode()) {
			case -1067215565: 
				if (string.equals("trailer")) {
					byte1 = 1;
				}

				break;
			
			case 3565976: 
				if (string.equals("tool")) {
					byte1 = 0;
				}

				break;
			
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 2;
				}

			
			}

			switch (byte1) {
			case 0: 
				if (baseVehicle != null) {
					float float2 = PZMath.max(element.position.x - (float)square.x, 0.001F);
					float float3 = PZMath.max(element.position.y - (float)square.y, 0.001F);
					float float4 = 0.0F;
					square.AddWorldInventoryItem((String)PZArrayUtil.pickRandom((List)this.params.items), float2, float3, float4);
				}

				break;
			
			case 1: 
				if (baseVehicle != null) {
					this.addTrailer(baseVehicle, zone, square.getChunk(), this.params.zoneName, this.params.vehicleDistrib, Rand.NextBool(1) ? "Base.Trailer" : "Base.TrailerCover");
				}

				break;
			
			case 2: 
				baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, float1, element.direction, this.params.zoneName, this.params.scriptName, (Integer)null, this.params.vehicleDistrib);
				if (baseVehicle != null) {
					this.addZombiesOnVehicle(Rand.Next(2, 5), this.params.outfits, this.params.femaleChance, baseVehicle);
				}

			
			}
		}
	}

	private static final class Params {
		String zoneName;
		String scriptName;
		String outfits;
		Integer femaleChance;
		String vehicleDistrib;
		ArrayList items;
		int nbrOfItem;
		boolean addTrailer;
	}
}
