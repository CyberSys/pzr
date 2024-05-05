package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.util.Type;


public class RandomizedZoneStoryBase extends RandomizedWorldBase {
	public boolean alwaysDo = false;
	public static final int baseChance = 15;
	public static int totalChance = 0;
	public static final String zoneStory = "ZoneStory";
	public int chance = 0;
	protected int minZoneWidth = 0;
	protected int minZoneHeight = 0;
	public final ArrayList zoneType = new ArrayList();
	private static final HashMap rzsMap = new HashMap();

	public static boolean isValidForStory(IsoMetaGrid.Zone zone, boolean boolean1) {
		if (zone.pickedXForZoneStory > 0 && zone.pickedYForZoneStory > 0 && zone.pickedRZStory != null && checkCanSpawnStory(zone, boolean1)) {
			zone.pickedRZStory.randomizeZoneStory(zone);
			zone.pickedRZStory = null;
			zone.pickedXForZoneStory = 0;
			zone.pickedYForZoneStory = 0;
		}

		if (!boolean1 && zone.hourLastSeen != 0) {
			return false;
		} else if (!boolean1 && zone.haveConstruction) {
			return false;
		} else if ("ZoneStory".equals(zone.type)) {
			doRandomStory(zone);
			return true;
		} else {
			return false;
		}
	}

	public static void initAllRZSMapChance(IsoMetaGrid.Zone zone) {
		totalChance = 0;
		rzsMap.clear();
		for (int int1 = 0; int1 < IsoWorld.instance.getRandomizedZoneList().size(); ++int1) {
			RandomizedZoneStoryBase randomizedZoneStoryBase = (RandomizedZoneStoryBase)IsoWorld.instance.getRandomizedZoneList().get(int1);
			if (randomizedZoneStoryBase.isValid(zone, false) && randomizedZoneStoryBase.isTimeValid(false)) {
				totalChance += randomizedZoneStoryBase.chance;
				rzsMap.put(randomizedZoneStoryBase, randomizedZoneStoryBase.chance);
			}
		}
	}

	public boolean isValid(IsoMetaGrid.Zone zone, boolean boolean1) {
		boolean boolean2 = false;
		for (int int1 = 0; int1 < this.zoneType.size(); ++int1) {
			if (((String)this.zoneType.get(int1)).equals(zone.name)) {
				boolean2 = true;
				break;
			}
		}

		return boolean2 && zone.w >= this.minZoneWidth && zone.h >= this.minZoneHeight;
	}

	private static boolean doRandomStory(IsoMetaGrid.Zone zone) {
		++zone.hourLastSeen;
		byte byte1 = 6;
		switch (SandboxOptions.instance.ZoneStoryChance.getValue()) {
		case 1: 
			return false;
		
		case 2: 
			byte1 = 2;
		
		case 3: 
		
		default: 
			break;
		
		case 4: 
			byte1 = 12;
			break;
		
		case 5: 
			byte1 = 20;
			break;
		
		case 6: 
			byte1 = 40;
		
		}
		RandomizedZoneStoryBase randomizedZoneStoryBase = null;
		int int1;
		for (int1 = 0; int1 < IsoWorld.instance.getRandomizedZoneList().size(); ++int1) {
			RandomizedZoneStoryBase randomizedZoneStoryBase2 = (RandomizedZoneStoryBase)IsoWorld.instance.getRandomizedZoneList().get(int1);
			if (randomizedZoneStoryBase2.alwaysDo && randomizedZoneStoryBase2.isValid(zone, false) && randomizedZoneStoryBase2.isTimeValid(false)) {
				randomizedZoneStoryBase = randomizedZoneStoryBase2;
			}
		}

		int int2;
		int int3;
		int int4;
		if (randomizedZoneStoryBase != null) {
			int1 = zone.x;
			int4 = zone.y;
			int2 = zone.x + zone.w - randomizedZoneStoryBase.minZoneWidth / 2;
			int3 = zone.y + zone.h - randomizedZoneStoryBase.minZoneHeight / 2;
			zone.pickedXForZoneStory = Rand.Next(int1, int2 + 1);
			zone.pickedYForZoneStory = Rand.Next(int4, int3 + 1);
			zone.pickedRZStory = randomizedZoneStoryBase;
			return true;
		} else if (Rand.Next(100) < byte1) {
			initAllRZSMapChance(zone);
			randomizedZoneStoryBase = getRandomStory();
			if (randomizedZoneStoryBase == null) {
				return false;
			} else {
				int1 = zone.x;
				int4 = zone.y;
				int2 = zone.x + zone.w - randomizedZoneStoryBase.minZoneWidth / 2;
				int3 = zone.y + zone.h - randomizedZoneStoryBase.minZoneHeight / 2;
				zone.pickedXForZoneStory = Rand.Next(int1, int2 + 1);
				zone.pickedYForZoneStory = Rand.Next(int4, int3 + 1);
				zone.pickedRZStory = randomizedZoneStoryBase;
				return true;
			}
		} else {
			return false;
		}
	}

	public IsoGridSquare getRandomFreeSquare(RandomizedZoneStoryBase randomizedZoneStoryBase, IsoMetaGrid.Zone zone) {
		IsoGridSquare square = null;
		for (int int1 = 0; int1 < 1000; ++int1) {
			int int2 = Rand.Next(zone.pickedXForZoneStory - randomizedZoneStoryBase.minZoneWidth / 2, zone.pickedXForZoneStory + randomizedZoneStoryBase.minZoneWidth / 2);
			int int3 = Rand.Next(zone.pickedYForZoneStory - randomizedZoneStoryBase.minZoneHeight / 2, zone.pickedYForZoneStory + randomizedZoneStoryBase.minZoneHeight / 2);
			square = this.getSq(int2, int3, zone.z);
			if (square != null && square.isFree(false)) {
				return square;
			}
		}

		return null;
	}

	public IsoGridSquare getRandomFreeSquareFullZone(RandomizedZoneStoryBase randomizedZoneStoryBase, IsoMetaGrid.Zone zone) {
		IsoGridSquare square = null;
		for (int int1 = 0; int1 < 1000; ++int1) {
			int int2 = Rand.Next(zone.x, zone.x + zone.w);
			int int3 = Rand.Next(zone.y, zone.y + zone.h);
			square = this.getSq(int2, int3, zone.z);
			if (square != null && square.isFree(false)) {
				return square;
			}
		}

		return null;
	}

	private static RandomizedZoneStoryBase getRandomStory() {
		int int1 = Rand.Next(totalChance);
		Iterator iterator = rzsMap.keySet().iterator();
		int int2 = 0;
		RandomizedZoneStoryBase randomizedZoneStoryBase;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			randomizedZoneStoryBase = (RandomizedZoneStoryBase)iterator.next();
			int2 += (Integer)rzsMap.get(randomizedZoneStoryBase);
		} while (int1 >= int2);

		return randomizedZoneStoryBase;
	}

	private static boolean checkCanSpawnStory(IsoMetaGrid.Zone zone, boolean boolean1) {
		int int1 = zone.pickedXForZoneStory - zone.pickedRZStory.minZoneWidth / 2 - 2;
		int int2 = zone.pickedYForZoneStory - zone.pickedRZStory.minZoneHeight / 2 - 2;
		int int3 = zone.pickedXForZoneStory + zone.pickedRZStory.minZoneWidth / 2 + 2;
		int int4 = zone.pickedYForZoneStory + zone.pickedRZStory.minZoneHeight / 2 + 2;
		int int5 = int1 / 10;
		int int6 = int2 / 10;
		int int7 = int3 / 10;
		int int8 = int4 / 10;
		for (int int9 = int6; int9 <= int8; ++int9) {
			for (int int10 = int5; int10 <= int7; ++int10) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int10, int9) : IsoWorld.instance.CurrentCell.getChunk(int10, int9);
				if (chunk == null || !chunk.bLoaded) {
					return false;
				}
			}
		}

		return true;
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
	}

	public boolean isValid() {
		return true;
	}

	public void cleanAreaForStory(RandomizedZoneStoryBase randomizedZoneStoryBase, IsoMetaGrid.Zone zone) {
		int int1 = zone.pickedXForZoneStory - randomizedZoneStoryBase.minZoneWidth / 2 - 1;
		int int2 = zone.pickedYForZoneStory - randomizedZoneStoryBase.minZoneHeight / 2 - 1;
		int int3 = zone.pickedXForZoneStory + randomizedZoneStoryBase.minZoneWidth / 2 + 1;
		int int4 = zone.pickedYForZoneStory + randomizedZoneStoryBase.minZoneHeight / 2 + 1;
		for (int int5 = int1; int5 < int3; ++int5) {
			for (int int6 = int2; int6 < int4; ++int6) {
				IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(int5, int6, zone.z);
				if (square != null) {
					square.removeBlood(false, false);
					int int7;
					IsoObject object;
					for (int7 = square.getObjects().size() - 1; int7 >= 0; --int7) {
						object = (IsoObject)square.getObjects().get(int7);
						if (square.getFloor() != object) {
							square.RemoveTileObject(object);
						}
					}

					for (int7 = square.getSpecialObjects().size() - 1; int7 >= 0; --int7) {
						object = (IsoObject)square.getSpecialObjects().get(int7);
						square.RemoveTileObject(object);
					}

					for (int7 = square.getStaticMovingObjects().size() - 1; int7 >= 0; --int7) {
						IsoDeadBody deadBody = (IsoDeadBody)Type.tryCastTo((IsoMovingObject)square.getStaticMovingObjects().get(int7), IsoDeadBody.class);
						if (deadBody != null) {
							square.removeCorpse(deadBody, false);
						}
					}

					square.RecalcProperties();
					square.RecalcAllWithNeighbours(true);
				}
			}
		}
	}

	public int getMinimumWidth() {
		return this.minZoneWidth;
	}

	public int getMinimumHeight() {
		return this.minZoneHeight;
	}

	public static enum ZoneType {

		Forest,
		Beach,
		Lake,
		Baseball,
		MusicFestStage,
		MusicFest,
		NewsStory;

		private static RandomizedZoneStoryBase.ZoneType[] $values() {
			return new RandomizedZoneStoryBase.ZoneType[]{Forest, Beach, Lake, Baseball, MusicFestStage, MusicFest, NewsStory};
		}
	}
}
