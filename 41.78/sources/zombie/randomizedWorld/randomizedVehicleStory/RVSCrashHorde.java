package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.vehicles.BaseVehicle;


public final class RVSCrashHorde extends RandomizedVehicleStoryBase {

	public RVSCrashHorde() {
		this.name = "Crash Horde";
		this.minZoneWidth = 8;
		this.minZoneHeight = 8;
		this.setChance(1);
		this.setMinimumDays(60);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		this.callVehicleStorySpawner(zone, chunk, 0.0F);
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
		vehicleStorySpawner.clear();
		float float1 = 0.5235988F;
		if (boolean1) {
			float1 = 0.0F;
		}

		Vector2 vector2 = IsoDirections.N.ToVector();
		vector2.rotate(Rand.Next(-float1, float1));
		vehicleStorySpawner.addElement("vehicle1", 0.0F, 0.0F, vector2.getDirection(), 2.0F, 5.0F);
		vehicleStorySpawner.setParameter("zone", zone);
		vehicleStorySpawner.setParameter("burnt", Rand.NextBool(5));
		return true;
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
		IsoGridSquare square = element.square;
		if (square != null) {
			float float1 = element.z;
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
			boolean boolean1 = vehicleStorySpawner.getParameterBoolean("burnt");
			String string = element.id;
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 2014205573: 
				if (string.equals("vehicle1")) {
					byte1 = 0;
				}

			
			}

			switch (byte1) {
			case 0: 
				BaseVehicle baseVehicle = this.addVehicleFlipped(zone, element.position.x, element.position.y, float1 + 0.25F, element.direction, boolean1 ? "normalburnt" : "bad", (String)null, (Integer)null, (String)null);
				if (baseVehicle != null) {
					int int1 = Rand.Next(4);
					String string2 = null;
					switch (int1) {
					case 0: 
						string2 = "Front";
						break;
					
					case 1: 
						string2 = "Rear";
						break;
					
					case 2: 
						string2 = "Left";
						break;
					
					case 3: 
						string2 = "Right";
					
					}

					baseVehicle = baseVehicle.setSmashed(string2);
					baseVehicle.setBloodIntensity("Front", Rand.Next(0.7F, 1.0F));
					baseVehicle.setBloodIntensity("Rear", Rand.Next(0.7F, 1.0F));
					baseVehicle.setBloodIntensity("Left", Rand.Next(0.7F, 1.0F));
					baseVehicle.setBloodIntensity("Right", Rand.Next(0.7F, 1.0F));
					ArrayList arrayList = this.addZombiesOnVehicle(Rand.Next(2, 4), (String)null, (Integer)null, baseVehicle);
					if (arrayList != null) {
						for (int int2 = 0; int2 < arrayList.size(); ++int2) {
							IsoZombie zombie = (IsoZombie)arrayList.get(int2);
							zombie.upKillCount = false;
							this.addBloodSplat(zombie.getSquare(), Rand.Next(10, 20));
							if (boolean1) {
								zombie.setSkeleton(true);
								zombie.getHumanVisual().setSkinTextureIndex(0);
							} else {
								zombie.DoCorpseInventory();
								if (Rand.NextBool(10)) {
									zombie.setFakeDead(true);
									zombie.bCrawling = true;
									zombie.setCanWalk(false);
									zombie.setCrawlerType(1);
								}
							}

							new IsoDeadBody(zombie, false);
						}

						this.addZombiesOnVehicle(Rand.Next(12, 20), (String)null, (Integer)null, baseVehicle);
					}
				}

			
			default: 
			
			}
		}
	}
}
