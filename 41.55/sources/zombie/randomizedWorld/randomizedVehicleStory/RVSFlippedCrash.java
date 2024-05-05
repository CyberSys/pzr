package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.objects.IsoDeadBody;
import zombie.vehicles.BaseVehicle;


public final class RVSFlippedCrash extends RandomizedVehicleStoryBase {

	public RVSFlippedCrash() {
		this.name = "Flipped Crash";
		this.setChance(4);
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		boolean boolean1 = Rand.NextBool(5);
		IsoGridSquare square = IsoCell.getInstance().getGridSquare(this.minX, this.minY, zone.z);
		if (square != null) {
			IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
			if (this.horizontalZone) {
				directions = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
			}

			BaseVehicle baseVehicle = this.addVehicleFlipped(zone, square, chunk, boolean1 ? "normalburnt" : "bad", (String)null, (Integer)null, directions, (String)null);
			int int1 = Rand.Next(4);
			String string = null;
			switch (int1) {
			case 0: 
				string = "Front";
				break;
			
			case 1: 
				string = "Rear";
				break;
			
			case 2: 
				string = "Left";
				break;
			
			case 3: 
				string = "Right";
			
			}

			baseVehicle = baseVehicle.setSmashed(string, true);
			baseVehicle.setBloodIntensity("Front", Rand.Next(0.3F, 1.0F));
			baseVehicle.setBloodIntensity("Rear", Rand.Next(0.3F, 1.0F));
			baseVehicle.setBloodIntensity("Left", Rand.Next(0.3F, 1.0F));
			baseVehicle.setBloodIntensity("Right", Rand.Next(0.3F, 1.0F));
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
						zombie.DoZombieInventory();
						if (Rand.NextBool(10)) {
							zombie.setFakeDead(true);
							zombie.bCrawling = true;
							zombie.setCanWalk(false);
							zombie.setCrawlerType(1);
						}
					}

					new IsoDeadBody(zombie, false);
				}
			}
		}
	}
}
