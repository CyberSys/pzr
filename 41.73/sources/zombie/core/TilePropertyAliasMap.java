package zombie.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public final class TilePropertyAliasMap {
	public static final TilePropertyAliasMap instance = new TilePropertyAliasMap();
	public final HashMap PropertyToID = new HashMap();
	public final ArrayList Properties = new ArrayList();

	public void Generate(HashMap hashMap) {
		this.Properties.clear();
		this.PropertyToID.clear();
		Iterator iterator = hashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string = (String)entry.getKey();
			ArrayList arrayList = (ArrayList)entry.getValue();
			this.PropertyToID.put(string, this.Properties.size());
			TilePropertyAliasMap.TileProperty tileProperty = new TilePropertyAliasMap.TileProperty();
			this.Properties.add(tileProperty);
			tileProperty.propertyName = string;
			tileProperty.possibleValues.addAll(arrayList);
			ArrayList arrayList2 = tileProperty.possibleValues;
			for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
				String string2 = (String)arrayList2.get(int1);
				tileProperty.idMap.put(string2, int1);
			}
		}
	}

	public int getIDFromPropertyName(String string) {
		return !this.PropertyToID.containsKey(string) ? -1 : (Integer)this.PropertyToID.get(string);
	}

	public int getIDFromPropertyValue(int int1, String string) {
		TilePropertyAliasMap.TileProperty tileProperty = (TilePropertyAliasMap.TileProperty)this.Properties.get(int1);
		if (tileProperty.possibleValues.isEmpty()) {
			return 0;
		} else {
			return !tileProperty.idMap.containsKey(string) ? 0 : (Integer)tileProperty.idMap.get(string);
		}
	}

	public String getPropertyValueString(int int1, int int2) {
		TilePropertyAliasMap.TileProperty tileProperty = (TilePropertyAliasMap.TileProperty)this.Properties.get(int1);
		return tileProperty.possibleValues.isEmpty() ? "" : (String)tileProperty.possibleValues.get(int2);
	}

	public static final class TileProperty {
		public String propertyName;
		public final ArrayList possibleValues = new ArrayList();
		public final HashMap idMap = new HashMap();
	}
}
