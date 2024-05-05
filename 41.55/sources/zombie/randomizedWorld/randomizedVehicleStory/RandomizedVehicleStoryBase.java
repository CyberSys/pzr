package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.HashMap;
import java.util.Iterator;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.vehicles.BaseVehicle;


public class RandomizedVehicleStoryBase extends RandomizedWorldBase {
	private int chance = 0;
	private static int totalChance = 0;
	private static HashMap rvsMap = new HashMap();
	protected boolean horizontalZone = false;
	public static final float baseChance = 12.5F;
	protected int minX = 0;
	protected int minY = 0;
	protected int maxX = 0;
	protected int maxY = 0;
	protected int minZoneWidth = 0;
	protected int minZoneHeight = 0;

	public static void initAllRVSMapChance(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		totalChance = 0;
		rvsMap.clear();
		for (int int1 = 0; int1 < IsoWorld.instance.getRandomizedVehicleStoryList().size(); ++int1) {
			RandomizedVehicleStoryBase randomizedVehicleStoryBase = (RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(int1);
			if (randomizedVehicleStoryBase.isValid(zone, chunk, false) && randomizedVehicleStoryBase.isTimeValid(false)) {
				totalChance += ((RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(int1)).getChance();
				rvsMap.put((RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(int1), ((RandomizedVehicleStoryBase)IsoWorld.instance.getRandomizedVehicleStoryList().get(int1)).getChance());
			}
		}
	}

	public static boolean doRandomStory(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		float float1 = Rand.Next(0.0F, 500.0F);
		switch (SandboxOptions.instance.VehicleStoryChance.getValue()) {
		case 1: 
			return false;
		
		case 2: 
			float1 = Rand.Next(0.0F, 1000.0F);
		
		case 3: 
		
		default: 
			break;
		
		case 4: 
			float1 = Rand.Next(0.0F, 300.0F);
			break;
		
		case 5: 
			float1 = Rand.Next(0.0F, 175.0F);
			break;
		
		case 6: 
			float1 = Rand.Next(0.0F, 50.0F);
		
		}
		if (float1 < 12.5F) {
			if (!chunk.vehicles.isEmpty()) {
				return false;
			} else {
				RandomizedVehicleStoryBase randomizedVehicleStoryBase = null;
				initAllRVSMapChance(zone, chunk);
				randomizedVehicleStoryBase = getRandomStory();
				if (randomizedVehicleStoryBase == null) {
					return false;
				} else {
					randomizedVehicleStoryBase.randomizeVehicleStory(zone, chunk);
					++zone.hourLastSeen;
					return true;
				}
			}
		} else {
			return false;
		}
	}

	private static RandomizedVehicleStoryBase getRandomStory() {
		int int1 = Rand.Next(totalChance);
		Iterator iterator = rvsMap.keySet().iterator();
		int int2 = 0;
		RandomizedVehicleStoryBase randomizedVehicleStoryBase;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			randomizedVehicleStoryBase = (RandomizedVehicleStoryBase)iterator.next();
			int2 += (Integer)rvsMap.get(randomizedVehicleStoryBase);
		} while (int1 >= int2);

		return randomizedVehicleStoryBase;
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
	}

	public IsoGridSquare getCenterOfChunk(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		int int1 = Math.max(zone.x, chunk.wx * 10);
		int int2 = Math.max(zone.y, chunk.wy * 10);
		int int3 = Math.min(zone.x + zone.w, (chunk.wx + 1) * 10);
		int int4 = Math.min(zone.y + zone.h, (chunk.wy + 1) * 10);
		boolean boolean1 = false;
		boolean boolean2 = false;
		int int5;
		int int6;
		if (this.horizontalZone) {
			int6 = (zone.y + zone.y + zone.h) / 2;
			int5 = (int1 + int3) / 2;
		} else {
			int6 = (int2 + int4) / 2;
			int5 = (zone.x + zone.x + zone.w) / 2;
		}

		return IsoCell.getInstance().getGridSquare(int5, int6, zone.z);
	}

	public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		this.horizontalZone = false;
		this.debugLine = "";
		if (!boolean1 && zone.hourLastSeen != 0) {
			return false;
		} else if (!boolean1 && zone.haveConstruction) {
			return false;
		} else if (!"Nav".equals(zone.getType())) {
			this.debugLine = this.debugLine + "Not a \'Nav\' zone.";
			return false;
		} else {
			this.minX = Math.max(zone.x, chunk.wx * 10);
			this.minY = Math.max(zone.y, chunk.wy * 10);
			this.maxX = Math.min(zone.x + zone.w, (chunk.wx + 1) * 10);
			this.maxY = Math.min(zone.y + zone.h, (chunk.wy + 1) * 10);
			int int1 = 10;
			int int2 = 5;
			if (this.minZoneWidth > 0) {
				int1 = this.minZoneWidth;
			}

			if (this.minZoneHeight > 0) {
				int2 = this.minZoneHeight;
			}

			if (zone.w > 30 && zone.h < 15) {
				this.horizontalZone = true;
				if (this.maxX - this.minX < int1 || this.maxY - this.minY < int2) {
					this.debugLine = this.debugLine + "Horizontal street is too small, x:" + (this.maxX - this.minX) + " y:" + (this.maxY - this.minY);
				}

				return this.maxX - this.minX >= int1 && this.maxY - this.minY >= int2;
			} else if (zone.h > 30 && zone.w < 15) {
				if (this.maxX - this.minX < int2 || this.maxY - this.minY < int1) {
					this.debugLine = this.debugLine + "Vertical street is too small, x:" + (this.maxX - this.minX) + " y:" + (this.maxY - this.minY);
				}

				return this.maxX - this.minX >= int2 && this.maxY - this.minY >= int1;
			} else {
				this.debugLine = this.debugLine + "Zone too small";
				return false;
			}
		}
	}

	public BaseVehicle[] addSmashedOverlay(BaseVehicle baseVehicle, BaseVehicle baseVehicle2, int int1, int int2, boolean boolean1, boolean boolean2) {
		IsoDirections directions = baseVehicle.getDir();
		IsoDirections directions2 = baseVehicle2.getDir();
		String string = null;
		String string2 = null;
		if (!boolean1) {
			string = "Front";
			if (directions2 == IsoDirections.W) {
				if (directions == IsoDirections.S) {
					string2 = "Right";
				} else {
					string2 = "Left";
				}
			} else if (directions == IsoDirections.S) {
				string2 = "Left";
			} else {
				string2 = "Right";
			}
		} else {
			if (directions == IsoDirections.S) {
				if (int1 > 0) {
					string = "Left";
				} else {
					string = "Right";
				}
			} else if (int1 < 0) {
				string = "Left";
			} else {
				string = "Right";
			}

			string2 = "Front";
		}

		baseVehicle = baseVehicle.setSmashed(string);
		baseVehicle2 = baseVehicle2.setSmashed(string2);
		if (boolean2) {
			baseVehicle.setBloodIntensity(string, 1.0F);
			baseVehicle2.setBloodIntensity(string2, 1.0F);
		}

		return new BaseVehicle[]{baseVehicle, baseVehicle2};
	}

	public int getChance() {
		return this.chance;
	}

	public void setChance(int int1) {
		this.chance = int1;
	}

	public int getMinimumDays() {
		return this.minimumDays;
	}

	public void setMinimumDays(int int1) {
		this.minimumDays = int1;
	}

	public int getMaximumDays() {
		return this.maximumDays;
	}

	public void setMaximumDays(int int1) {
		this.maximumDays = int1;
	}

	public String getName() {
		return this.name;
	}

	public String getDebugLine() {
		return this.debugLine;
	}

	public void registerCustomOutfits() {
	}
}
