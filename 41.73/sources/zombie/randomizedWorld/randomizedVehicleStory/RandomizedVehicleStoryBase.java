package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.HashMap;
import java.util.Iterator;
import org.joml.Vector2f;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.vehicles.BaseVehicle;


public class RandomizedVehicleStoryBase extends RandomizedWorldBase {
	private int chance = 0;
	private static int totalChance = 0;
	private static HashMap rvsMap = new HashMap();
	protected boolean horizontalZone = false;
	protected int zoneWidth = 0;
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
				totalChance += randomizedVehicleStoryBase.getChance();
				rvsMap.put(randomizedVehicleStoryBase, randomizedVehicleStoryBase.getChance());
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
					VehicleStorySpawnData vehicleStorySpawnData = randomizedVehicleStoryBase.initSpawnDataForChunk(zone, chunk);
					chunk.setRandomVehicleStoryToSpawnLater(vehicleStorySpawnData);
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

	public int getMinZoneWidth() {
		return this.minZoneWidth <= 0 ? 10 : this.minZoneWidth;
	}

	public int getMinZoneHeight() {
		return this.minZoneHeight <= 0 ? 5 : this.minZoneHeight;
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
		this.zoneWidth = 0;
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
			return this.getSpawnPoint(zone, chunk, (float[])null);
		}
	}

	public VehicleStorySpawnData initSpawnDataForChunk(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		int int1 = this.getMinZoneWidth();
		int int2 = this.getMinZoneHeight();
		float[] floatArray = new float[3];
		if (!this.getSpawnPoint(zone, chunk, floatArray)) {
			return null;
		} else {
			float float1 = floatArray[0];
			float float2 = floatArray[1];
			float float3 = floatArray[2];
			int[] intArray = new int[4];
			VehicleStorySpawner.getInstance().getAABB(float1, float2, (float)int1, (float)int2, float3, intArray);
			return new VehicleStorySpawnData(this, zone, float1, float2, float3, intArray[0], intArray[1], intArray[2], intArray[3]);
		}
	}

	public boolean getSpawnPoint(IsoMetaGrid.Zone zone, IsoChunk chunk, float[] floatArray) {
		return this.getRectangleSpawnPoint(zone, chunk, floatArray) || this.getPolylineSpawnPoint(zone, chunk, floatArray);
	}

	public boolean getRectangleSpawnPoint(IsoMetaGrid.Zone zone, IsoChunk chunk, float[] floatArray) {
		if (!zone.isRectangle()) {
			return false;
		} else {
			int int1 = this.getMinZoneWidth();
			int int2 = this.getMinZoneHeight();
			int int3;
			float float1;
			float float2;
			float float3;
			if (zone.w > 30 && zone.h < 15) {
				this.horizontalZone = true;
				this.zoneWidth = zone.h;
				if (zone.getWidth() < int2) {
					int3 = zone.getWidth();
					this.debugLine = "Horizontal street is too small, w:" + int3 + " h:" + zone.getHeight();
					return false;
				} else if (zone.getHeight() < int1) {
					int3 = zone.getWidth();
					this.debugLine = "Horizontal street is too small, w:" + int3 + " h:" + zone.getHeight();
					return false;
				} else if (floatArray == null) {
					return true;
				} else {
					float1 = (float)zone.getX();
					float2 = (float)(zone.getX() + zone.getWidth());
					float3 = (float)zone.getY() + (float)zone.getHeight() / 2.0F;
					floatArray[0] = PZMath.clamp((float)(chunk.wx * 10) + 5.0F, float1 + (float)int2 / 2.0F, float2 - (float)int2 / 2.0F);
					floatArray[1] = float3;
					floatArray[2] = Vector2.getDirection(float2 - float1, 0.0F);
					return true;
				}
			} else if (zone.h > 30 && zone.w < 15) {
				this.horizontalZone = false;
				this.zoneWidth = zone.w;
				if (zone.getWidth() < int1) {
					int3 = zone.getWidth();
					this.debugLine = "Vertical street is too small, w:" + int3 + " h:" + zone.getHeight();
					return false;
				} else if (zone.getHeight() < int2) {
					int3 = zone.getWidth();
					this.debugLine = "Vertical street is too small, w:" + int3 + " h:" + zone.getHeight();
					return false;
				} else if (floatArray == null) {
					return true;
				} else {
					float1 = (float)zone.getY();
					float2 = (float)(zone.getY() + zone.getHeight());
					float3 = (float)zone.getX() + (float)zone.getWidth() / 2.0F;
					floatArray[0] = float3;
					floatArray[1] = PZMath.clamp((float)(chunk.wy * 10) + 5.0F, float1 + (float)int2 / 2.0F, float2 - (float)int2 / 2.0F);
					floatArray[2] = Vector2.getDirection(0.0F, float1 - float2);
					return true;
				}
			} else {
				this.debugLine = "Zone too small or too large";
				return false;
			}
		}
	}

	public boolean getPolylineSpawnPoint(IsoMetaGrid.Zone zone, IsoChunk chunk, float[] floatArray) {
		if (zone.isPolyline() && zone.polylineWidth > 0) {
			int int1 = this.getMinZoneWidth();
			int int2 = this.getMinZoneHeight();
			if (zone.polylineWidth < int1) {
				this.debugLine = "Polyline zone is too narrow, width:" + zone.polylineWidth;
				return false;
			} else {
				double[] doubleArray = new double[2];
				int int3 = zone.getClippedSegmentOfPolyline(chunk.wx * 10, chunk.wy * 10, (chunk.wx + 1) * 10, (chunk.wy + 1) * 10, doubleArray);
				if (int3 == -1) {
					return false;
				} else {
					double double1 = doubleArray[0];
					double double2 = doubleArray[1];
					float float1 = zone.polylineWidth % 2 == 0 ? 0.0F : 0.5F;
					float float2 = (float)zone.points.get(int3 * 2) + float1;
					float float3 = (float)zone.points.get(int3 * 2 + 1) + float1;
					float float4 = (float)zone.points.get(int3 * 2 + 2) + float1;
					float float5 = (float)zone.points.get(int3 * 2 + 3) + float1;
					float float6 = float4 - float2;
					float float7 = float5 - float3;
					float float8 = Vector2f.length(float6, float7);
					if (float8 < (float)int2) {
						return false;
					} else {
						this.zoneWidth = zone.polylineWidth;
						if (floatArray == null) {
							return true;
						} else {
							float float9 = (float)int2 / 2.0F / float8;
							float float10 = PZMath.max((float)double1 - float9, float9);
							float float11 = PZMath.min((float)double2 + float9, 1.0F - float9);
							float float12 = float2 + float6 * float10;
							float float13 = float3 + float7 * float10;
							float float14 = float2 + float6 * float11;
							float float15 = float3 + float7 * float11;
							float float16 = Rand.Next(0.0F, 1.0F);
							if (Core.bDebug) {
								float16 = (float)(System.currentTimeMillis() / 20L % 360L) / 360.0F;
							}

							floatArray[0] = float12 + (float14 - float12) * float16;
							floatArray[1] = float13 + (float15 - float13) * float16;
							floatArray[2] = Vector2.getDirection(float6, float7);
							return true;
						}
					}
				}
			}
		} else {
			return false;
		}
	}

	public boolean isFullyStreamedIn(int int1, int int2, int int3, int int4) {
		byte byte1 = 10;
		int int5 = int1 / byte1;
		int int6 = int2 / byte1;
		int int7 = (int3 - 1) / byte1;
		int int8 = (int4 - 1) / byte1;
		for (int int9 = int6; int9 <= int8; ++int9) {
			for (int int10 = int5; int10 <= int7; ++int10) {
				if (!this.isChunkLoaded(int10, int9)) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean isChunkLoaded(int int1, int int2) {
		IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1, int2) : IsoWorld.instance.CurrentCell.getChunk(int1, int2);
		return chunk != null && chunk.bLoaded;
	}

	public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		return false;
	}

	public boolean callVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, float float1) {
		float[] floatArray = new float[3];
		if (!this.getSpawnPoint(zone, chunk, floatArray)) {
			return false;
		} else {
			this.initVehicleStorySpawner(zone, chunk, false);
			VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
			float float2 = floatArray[2];
			if (Rand.NextBool(2)) {
				float2 += 3.1415927F;
			}

			float2 += float1;
			++float2;
			vehicleStorySpawner.spawn(floatArray[0], floatArray[1], 0.0F, float2, this::spawnElement);
			return true;
		}
	}

	public void spawnElement(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element) {
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
