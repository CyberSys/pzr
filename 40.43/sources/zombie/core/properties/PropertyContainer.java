package zombie.core.properties;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Collections.NonBlockingHashMap;
import zombie.iso.SpriteDetails.IsoFlagType;


public class PropertyContainer {
	private EnumSet SpriteFlags;
	private TIntIntHashMap Properties = new TIntIntHashMap();
	private int[] keyArray;
	public static NonBlockingHashMap test = new NonBlockingHashMap();
	public static List sorted = Collections.synchronizedList(new ArrayList());
	public boolean solid = false;
	public boolean trans = false;
	public boolean solidtrans = false;
	public boolean collideN = false;
	public boolean collideW = false;
	public boolean solidfloor = false;
	public boolean water = false;
	public boolean isBush = false;
	private byte Surface;
	private byte SurfaceFlags;
	private short StackReplaceTileOffset;
	private static final byte SURFACE_VALID = 1;
	private static final byte SURFACE_ISOFFSET = 2;
	private static final byte SURFACE_ISTABLE = 4;
	private static final byte SURFACE_ISTABLETOP = 8;

	public PropertyContainer() {
		this.SpriteFlags = EnumSet.noneOf(IsoFlagType.class);
	}

	public void CreateKeySet() {
		TIntSet tIntSet = this.Properties.keySet();
		this.keyArray = tIntSet.toArray();
	}

	public PropertyContainer(PropertyContainer propertyContainer) {
		this.AddProperties(propertyContainer);
	}

	public void AddProperties(PropertyContainer propertyContainer) {
		if (propertyContainer.keyArray != null) {
			for (int int1 = 0; int1 < propertyContainer.keyArray.length; ++int1) {
				int int2 = propertyContainer.keyArray[int1];
				this.Properties.put(int2, propertyContainer.Properties.get(int2));
			}

			this.solid |= propertyContainer.solid;
			this.trans |= propertyContainer.trans;
			this.solidtrans |= propertyContainer.solidtrans;
			this.collideN |= propertyContainer.collideN;
			this.collideW |= propertyContainer.collideW;
			this.solidfloor |= propertyContainer.solidfloor;
			this.water |= propertyContainer.water;
			this.isBush |= propertyContainer.isBush;
			this.SpriteFlags.addAll(propertyContainer.SpriteFlags);
		}
	}

	public void Clear() {
		this.solid = false;
		this.trans = false;
		this.solidtrans = false;
		this.collideN = false;
		this.collideW = false;
		this.solidfloor = false;
		this.water = false;
		this.isBush = false;
		this.SpriteFlags.clear();
		this.Properties.clear();
		this.SurfaceFlags &= -2;
	}

	public boolean Is(IsoFlagType flagType) {
		if (flagType == IsoFlagType.solid) {
			return this.solid;
		} else if (flagType == IsoFlagType.trans) {
			return this.trans;
		} else if (flagType == IsoFlagType.solidtrans) {
			return this.solidtrans;
		} else if (flagType == IsoFlagType.collideN) {
			return this.collideN;
		} else if (flagType == IsoFlagType.collideW) {
			return this.collideW;
		} else if (flagType == IsoFlagType.solidfloor) {
			return this.solidfloor;
		} else {
			return flagType == IsoFlagType.water ? this.water : this.SpriteFlags.contains(flagType);
		}
	}

	public boolean Is(Double Double1) {
		return this.SpriteFlags.contains(Double1.intValue());
	}

	public void Set(String string, String string2) {
		this.Set(string, string2, true);
	}

	public void Set(String string, String string2, boolean boolean1) {
		if (string != null) {
			if (boolean1) {
				IsoFlagType flagType = IsoFlagType.FromString(string);
				if (flagType != IsoFlagType.MAX) {
					if (flagType == IsoFlagType.solid) {
						this.solid = true;
					} else if (flagType == IsoFlagType.trans) {
						this.trans = true;
					} else if (flagType == IsoFlagType.solidtrans) {
						this.solidtrans = true;
					} else if (flagType == IsoFlagType.collideN) {
						this.collideN = true;
					} else if (flagType == IsoFlagType.collideW) {
						this.collideW = true;
					} else if (flagType == IsoFlagType.solidfloor) {
						this.solidfloor = true;
					} else if (flagType == IsoFlagType.water) {
						this.water = true;
					}

					this.Set(flagType, string2);
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
		if (flagType == IsoFlagType.solid) {
			this.solid = true;
		} else if (flagType == IsoFlagType.trans) {
			this.trans = true;
		} else if (flagType == IsoFlagType.solidtrans) {
			this.solidtrans = true;
		} else if (flagType == IsoFlagType.collideN) {
			this.collideN = true;
		} else if (flagType == IsoFlagType.collideW) {
			this.collideW = true;
		} else if (flagType == IsoFlagType.solidfloor) {
			this.solidfloor = true;
		} else if (flagType == IsoFlagType.water) {
			this.water = true;
		}

		this.SpriteFlags.add(flagType);
	}

	public void Set(IsoFlagType flagType, String string) {
		if (flagType == IsoFlagType.solid) {
			this.solid = true;
		} else if (flagType == IsoFlagType.trans) {
			this.trans = true;
		} else if (flagType == IsoFlagType.solidtrans) {
			this.solidtrans = true;
		} else if (flagType == IsoFlagType.collideN) {
			this.collideN = true;
		} else if (flagType == IsoFlagType.collideW) {
			this.collideW = true;
		} else if (flagType == IsoFlagType.solidfloor) {
			this.solidfloor = true;
		} else if (flagType == IsoFlagType.water) {
			this.water = true;
		}

		this.SpriteFlags.add(flagType);
	}

	public void UnSet(String string) {
		int int1 = TilePropertyAliasMap.instance.getIDFromPropertyName(string);
		this.Properties.remove(int1);
	}

	public void UnSet(IsoFlagType flagType) {
		if (flagType == IsoFlagType.solid) {
			this.solid = false;
		} else if (flagType == IsoFlagType.trans) {
			this.trans = false;
		} else if (flagType == IsoFlagType.solidtrans) {
			this.solidtrans = false;
		} else if (flagType == IsoFlagType.collideN) {
			this.collideN = false;
		} else if (flagType == IsoFlagType.collideW) {
			this.collideW = false;
		} else if (flagType == IsoFlagType.solidfloor) {
			this.solidfloor = false;
		} else if (flagType == IsoFlagType.water) {
			this.water = false;
		}

		this.SpriteFlags.remove(flagType);
	}

	public String Val(String string) {
		int int1 = TilePropertyAliasMap.instance.getIDFromPropertyName(string);
		return !this.Properties.containsKey(int1) ? null : TilePropertyAliasMap.instance.getPropertyValueString(int1, this.Properties.get(int1));
	}

	public boolean Is(String string) {
		int int1 = TilePropertyAliasMap.instance.getIDFromPropertyName(string);
		return this.Properties.containsKey(int1);
	}

	public EnumSet getFlags() {
		return this.SpriteFlags;
	}

	public ArrayList getPropertyNames() {
		final ArrayList arrayList = new ArrayList();
		TIntSet tIntSet = this.Properties.keySet();
		tIntSet.forEach(new TIntProcedure(){
			
			public boolean execute(int arrayListx) {
				arrayList.add(((TilePropertyAliasMap.TileProperty)TilePropertyAliasMap.instance.Properties.get(arrayListx)).propertyName);
				return true;
			}
		});
		return arrayList;
	}

	private void initSurface() {
		if ((this.SurfaceFlags & 1) == 0) {
			this.Surface = 0;
			this.StackReplaceTileOffset = 0;
			this.SurfaceFlags = 1;
			if (this.Properties != null) {
				this.Properties.forEachEntry(new TIntIntProcedure(){
					
					public boolean execute(int var1, int var2) {
						TilePropertyAliasMap.TileProperty var3 = (TilePropertyAliasMap.TileProperty)TilePropertyAliasMap.instance.Properties.get(var1);
						String var4 = var3.propertyName;
						String var5 = (String)var3.possibleValues.get(var2);
						if ("Surface".equals(var4) && var5 != null) {
							try {
								int var6 = Integer.parseInt(var5);
								if (var6 >= 0 && var6 <= 128) {
									PropertyContainer.this.Surface = (byte)var6;
								}
							} catch (NumberFormatException var8) {
							}
						} else if ("IsSurfaceOffset".equals(var4)) {
							PropertyContainer.this.SurfaceFlags = (byte)(PropertyContainer.this.SurfaceFlags | 2);
						} else if ("IsTable".equals(var4)) {
							PropertyContainer.this.SurfaceFlags = (byte)(PropertyContainer.this.SurfaceFlags | 4);
						} else if ("IsTableTop".equals(var4)) {
							PropertyContainer.this.SurfaceFlags = (byte)(PropertyContainer.this.SurfaceFlags | 8);
						} else if ("StackReplaceTileOffset".equals(var4)) {
							try {
								PropertyContainer.this.StackReplaceTileOffset = (short)Integer.parseInt(var5);
							} catch (NumberFormatException var7) {
							}
						}

						return true;
					}
				});
			}
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
