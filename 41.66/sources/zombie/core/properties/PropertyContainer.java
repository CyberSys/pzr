package zombie.core.properties;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Collections.NonBlockingHashMap;
import zombie.iso.SpriteDetails.IsoFlagType;


public final class PropertyContainer {
	private long SpriteFlags1 = 0L;
	private long SpriteFlags2 = 0L;
	private final TIntIntHashMap Properties = new TIntIntHashMap();
	private int[] keyArray;
	public static NonBlockingHashMap test = new NonBlockingHashMap();
	public static List sorted = Collections.synchronizedList(new ArrayList());
	private byte Surface;
	private byte SurfaceFlags;
	private short StackReplaceTileOffset;
	private static final byte SURFACE_VALID = 1;
	private static final byte SURFACE_ISOFFSET = 2;
	private static final byte SURFACE_ISTABLE = 4;
	private static final byte SURFACE_ISTABLETOP = 8;

	public void CreateKeySet() {
		TIntSet tIntSet = this.Properties.keySet();
		this.keyArray = tIntSet.toArray();
	}

	public void AddProperties(PropertyContainer propertyContainer) {
		if (propertyContainer.keyArray != null) {
			for (int int1 = 0; int1 < propertyContainer.keyArray.length; ++int1) {
				int int2 = propertyContainer.keyArray[int1];
				this.Properties.put(int2, propertyContainer.Properties.get(int2));
			}

			this.SpriteFlags1 |= propertyContainer.SpriteFlags1;
			this.SpriteFlags2 |= propertyContainer.SpriteFlags2;
		}
	}

	public void Clear() {
		this.SpriteFlags1 = 0L;
		this.SpriteFlags2 = 0L;
		this.Properties.clear();
		this.SurfaceFlags &= -2;
	}

	public boolean Is(IsoFlagType flagType) {
		long long1 = flagType.index() / 64 == 0 ? this.SpriteFlags1 : this.SpriteFlags2;
		return (long1 & 1L << flagType.index() % 64) != 0L;
	}

	public boolean Is(Double Double1) {
		return this.Is(IsoFlagType.fromIndex(Double1.intValue()));
	}

	public void Set(String string, String string2) {
		this.Set(string, string2, true);
	}

	public void Set(String string, String string2, boolean boolean1) {
		if (string != null) {
			if (boolean1) {
				IsoFlagType flagType = IsoFlagType.FromString(string);
				if (flagType != IsoFlagType.MAX) {
					this.Set(flagType);
					return;
				}
			}

			int int1 = TilePropertyAliasMap.instance.getIDFromPropertyName(string);
			if (int1 != -1) {
				int int2 = TilePropertyAliasMap.instance.getIDFromPropertyValue(int1, string2);
				this.SurfaceFlags &= -2;
				this.Properties.put(int1, int2);
			}
		}
	}

	public void Set(IsoFlagType flagType) {
		if (flagType.index() / 64 == 0) {
			this.SpriteFlags1 |= 1L << flagType.index() % 64;
		} else {
			this.SpriteFlags2 |= 1L << flagType.index() % 64;
		}
	}

	public void Set(IsoFlagType flagType, String string) {
		this.Set(flagType);
	}

	public void UnSet(String string) {
		int int1 = TilePropertyAliasMap.instance.getIDFromPropertyName(string);
		this.Properties.remove(int1);
	}

	public void UnSet(IsoFlagType flagType) {
		if (flagType.index() / 64 == 0) {
			this.SpriteFlags1 &= ~(1L << flagType.index() % 64);
		} else {
			this.SpriteFlags2 &= ~(1L << flagType.index() % 64);
		}
	}

	public String Val(String string) {
		int int1 = TilePropertyAliasMap.instance.getIDFromPropertyName(string);
		return !this.Properties.containsKey(int1) ? null : TilePropertyAliasMap.instance.getPropertyValueString(int1, this.Properties.get(int1));
	}

	public boolean Is(String string) {
		int int1 = TilePropertyAliasMap.instance.getIDFromPropertyName(string);
		return this.Properties.containsKey(int1);
	}

	public ArrayList getFlagsList() {
		ArrayList arrayList = new ArrayList();
		int int1;
		for (int1 = 0; int1 < 64; ++int1) {
			if ((this.SpriteFlags1 & 1L << int1) != 0L) {
				arrayList.add(IsoFlagType.fromIndex(int1));
			}
		}

		for (int1 = 0; int1 < 64; ++int1) {
			if ((this.SpriteFlags2 & 1L << int1) != 0L) {
				arrayList.add(IsoFlagType.fromIndex(64 + int1));
			}
		}

		return arrayList;
	}

	public ArrayList getPropertyNames() {
		ArrayList arrayList = new ArrayList();
		TIntSet tIntSet = this.Properties.keySet();
		tIntSet.forEach((arrayListx)->{
			arrayList.add(((TilePropertyAliasMap.TileProperty)TilePropertyAliasMap.instance.Properties.get(arrayListx)).propertyName);
			return true;
		});
		Collections.sort(arrayList);
		return arrayList;
	}

	private void initSurface() {
		if ((this.SurfaceFlags & 1) == 0) {
			this.Surface = 0;
			this.StackReplaceTileOffset = 0;
			this.SurfaceFlags = 1;
			this.Properties.forEachEntry((var1,var2)->{
				TilePropertyAliasMap.TileProperty tileProperty = (TilePropertyAliasMap.TileProperty)TilePropertyAliasMap.instance.Properties.get(var1);
				String string = tileProperty.propertyName;
				String string2 = (String)tileProperty.possibleValues.get(var2);
				if ("Surface".equals(string) && string2 != null) {
					try {
						int int1 = Integer.parseInt(string2);
						if (int1 >= 0 && int1 <= 128) {
							this.Surface = (byte)int1;
						}
					} catch (NumberFormatException numberFormatException) {
					}
				} else if ("IsSurfaceOffset".equals(string)) {
					this.SurfaceFlags = (byte)(this.SurfaceFlags | 2);
				} else if ("IsTable".equals(string)) {
					this.SurfaceFlags = (byte)(this.SurfaceFlags | 4);
				} else if ("IsTableTop".equals(string)) {
					this.SurfaceFlags = (byte)(this.SurfaceFlags | 8);
				} else if ("StackReplaceTileOffset".equals(string)) {
					try {
						this.StackReplaceTileOffset = (short)Integer.parseInt(string2);
					} catch (NumberFormatException numberFormatException2) {
					}
				}

				return true;
			});
		}
	}

	public int getSurface() {
		this.initSurface();
		return this.Surface;
	}

	public boolean isSurfaceOffset() {
		this.initSurface();
		return (this.SurfaceFlags & 2) != 0;
	}

	public boolean isTable() {
		this.initSurface();
		return (this.SurfaceFlags & 4) != 0;
	}

	public boolean isTableTop() {
		this.initSurface();
		return (this.SurfaceFlags & 8) != 0;
	}

	public int getStackReplaceTileOffset() {
		this.initSurface();
		return this.StackReplaceTileOffset;
	}

	public static class MostTested {
		public IsoFlagType flag;
		public int count;
	}

	private static class ProfileEntryComparitor implements Comparator {

		public ProfileEntryComparitor() {
		}

		public int compare(Object object, Object object2) {
			double double1 = (double)((PropertyContainer.MostTested)object).count;
			double double2 = (double)((PropertyContainer.MostTested)object2).count;
			if (double1 > double2) {
				return -1;
			} else {
				return double2 > double1 ? 1 : 0;
			}
		}
	}
}
