package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.ErosionRegions;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;


public abstract class ErosionCategory {
	public int ID;
	public ErosionRegions.Region region;
	protected ErosionCategory.SeasonDisplay[] seasonDisp = new ErosionCategory.SeasonDisplay[6];

	public ErosionCategory() {
		for (int int1 = 0; int1 < 6; ++int1) {
			this.seasonDisp[int1] = new ErosionCategory.SeasonDisplay();
		}
	}

	protected ErosionCategory.Data getCatModData(ErosionData.Square square) {
		for (int int1 = 0; int1 < square.regions.size(); ++int1) {
			ErosionCategory.Data data = (ErosionCategory.Data)square.regions.get(int1);
			if (data.regionID == this.region.ID && data.categoryID == this.ID) {
				return data;
			}
		}

		return null;
	}

	protected ErosionCategory.Data setCatModData(ErosionData.Square square) {
		ErosionCategory.Data data = this.getCatModData(square);
		if (data == null) {
			data = this.allocData();
			data.regionID = this.region.ID;
			data.categoryID = this.ID;
			square.regions.add(data);
			if (square.regions.size() > 5) {
				DebugLog.log("> 5 regions on a square");
			}
		}

		return data;
	}

	protected IsoObject validWall(IsoGridSquare square, boolean boolean1, boolean boolean2) {
		if (square == null) {
			return null;
		} else {
			IsoGridSquare square2 = boolean1 ? square.getTileInDirection(IsoDirections.N) : square.getTileInDirection(IsoDirections.W);
			Object object = null;
			if (square.isWallTo(square2)) {
				if (boolean1 && square.Is(IsoFlagType.cutN) && !square.Is(IsoFlagType.canPathN) || !boolean1 && square.Is(IsoFlagType.cutW) && !square.Is(IsoFlagType.canPathW)) {
					object = square.getWall(boolean1);
				}
			} else if (boolean2 && (square.isWindowBlockedTo(square2) || square.isWindowTo(square2))) {
				object = square.getWindowTo(square2);
				if (object == null) {
					object = square.getWall(boolean1);
				}
			}

			if (object != null) {
				if (square.getZ() > 0) {
					String string = ((IsoObject)object).getSprite().getName();
					return (IsoObject)(string != null && !string.contains("roof") ? object : null);
				} else {
					return (IsoObject)object;
				}
			} else {
				return null;
			}
		}
	}

	protected float clerp(float float1, float float2, float float3) {
		float float4 = (float)(1.0 - Math.cos((double)float1 * 3.141592653589793)) / 2.0F;
		return float2 * (1.0F - float4) + float3 * float4;
	}

	protected int currentSeason(float float1, ErosionObj erosionObj) {
		boolean boolean1 = false;
		ErosionSeason erosionSeason = ErosionMain.getInstance().getSeasons();
		int int1 = erosionSeason.getSeason();
		float float2 = erosionSeason.getSeasonDay();
		float float3 = erosionSeason.getSeasonDays();
		float float4 = float3 / 2.0F;
		float float5 = float4 * float1;
		ErosionCategory.SeasonDisplay seasonDisplay = this.seasonDisp[int1];
		int int2;
		if (seasonDisplay.split && float2 >= float4 + float5) {
			int2 = seasonDisplay.season2;
		} else if ((!seasonDisplay.split || !(float2 >= float5)) && !(float2 >= float3 * float1)) {
			ErosionCategory.SeasonDisplay seasonDisplay2;
			if (int1 == 5) {
				seasonDisplay2 = this.seasonDisp[4];
			} else if (int1 == 1) {
				seasonDisplay2 = this.seasonDisp[5];
			} else if (int1 == 2) {
				seasonDisplay2 = this.seasonDisp[1];
			} else {
				seasonDisplay2 = this.seasonDisp[2];
			}

			if (seasonDisplay2.split) {
				int2 = seasonDisplay2.season2;
			} else {
				int2 = seasonDisplay2.season1;
			}
		} else {
			int2 = seasonDisplay.season1;
		}

		return int2;
	}

	protected boolean currentBloom(float float1, ErosionObj erosionObj) {
		boolean boolean1 = false;
		ErosionSeason erosionSeason = ErosionMain.getInstance().getSeasons();
		int int1 = erosionSeason.getSeason();
		if (erosionObj.hasFlower && int1 == 2) {
			float float2 = erosionSeason.getSeasonDay();
			float float3 = erosionSeason.getSeasonDays();
			float float4 = float3 / 2.0F;
			float float5 = float4 * float1;
			float float6 = float3 - float5;
			float float7 = float2 - float5;
			float float8 = float6 * erosionObj.bloomEnd;
			float float9 = float6 * erosionObj.bloomStart;
			float float10 = (float8 - float9) / 2.0F;
			float float11 = float10 * float1;
			float8 = float9 + float10 + float11;
			float9 += float11;
			if (float7 >= float9 && float7 <= float8) {
				boolean1 = true;
			}
		}

		return boolean1;
	}

	public void updateObj(ErosionData.Square square, ErosionCategory.Data data, IsoGridSquare square2, ErosionObj erosionObj, boolean boolean1, int int1, int int2, boolean boolean2) {
		if (!data.hasSpawned) {
			if (!erosionObj.placeObject(square2, int1, boolean1, int2, boolean2)) {
				this.clearCatModData(square);
				return;
			}

			data.hasSpawned = true;
		} else if (data.stage != int1 || data.dispSeason != int2 || data.dispBloom != boolean2) {
			IsoObject object = erosionObj.getObject(square2, false);
			if (object == null) {
				this.clearCatModData(square);
				return;
			}

			erosionObj.setStageObject(int1, object, int2, boolean2);
		}

		data.stage = int1;
		data.dispSeason = int2;
		data.dispBloom = boolean2;
	}

	protected void clearCatModData(ErosionData.Square square) {
		for (int int1 = 0; int1 < square.regions.size(); ++int1) {
			ErosionCategory.Data data = (ErosionCategory.Data)square.regions.get(int1);
			if (data.regionID == this.region.ID && data.categoryID == this.ID) {
				square.regions.remove(int1);
				return;
			}
		}
	}

	public abstract void init();

	public abstract boolean replaceExistingObject(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2);

	public abstract boolean validateSpawn(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, boolean boolean1, boolean boolean2, boolean boolean3);

	public abstract void update(IsoGridSquare square, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int1);

	protected abstract ErosionCategory.Data allocData();

	public static ErosionCategory.Data loadCategoryData(ByteBuffer byteBuffer, int int1) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		ErosionCategory erosionCategory = ErosionRegions.getCategory(byte1, byte2);
		ErosionCategory.Data data = erosionCategory.allocData();
		data.regionID = byte1;
		data.categoryID = byte2;
		data.load(byteBuffer, int1);
		return data;
	}

	protected class SeasonDisplay {
		int season1;
		int season2;
		boolean split;
	}

	public class Data {
		public int regionID;
		public int categoryID;
		public boolean doNothing;
		public boolean hasSpawned;
		public int stage;
		public int dispSeason;
		public boolean dispBloom;

		public void save(ByteBuffer byteBuffer) {
			byte byte1 = 0;
			if (this.doNothing) {
				byte1 = (byte)(byte1 | 1);
			}

			if (this.hasSpawned) {
				byte1 = (byte)(byte1 | 2);
			}

			if (this.dispBloom) {
				byte1 = (byte)(byte1 | 4);
			}

			byteBuffer.put((byte)this.regionID);
			byteBuffer.put((byte)this.categoryID);
			byteBuffer.put((byte)this.stage);
			byteBuffer.put((byte)this.dispSeason);
			byteBuffer.put(byte1);
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			this.stage = byteBuffer.get();
			this.dispSeason = byteBuffer.get();
			byte byte1 = byteBuffer.get();
			this.doNothing = (byte1 & 1) != 0;
			this.hasSpawned = (byte1 & 2) != 0;
			this.dispBloom = (byte1 & 4) != 0;
		}
	}
}
