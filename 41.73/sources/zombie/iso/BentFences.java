package zombie.iso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.MapCollisionData;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.vehicles.PolygonalMap2;


public class BentFences {
	private static final BentFences instance = new BentFences();
	private final ArrayList m_entries = new ArrayList();
	private final HashMap m_bentMap = new HashMap();
	private final HashMap m_unbentMap = new HashMap();

	public static BentFences getInstance() {
		return instance;
	}

	private void tableToTiles(KahluaTableImpl kahluaTableImpl, ArrayList arrayList) {
		if (kahluaTableImpl != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
			while (kahluaTableIterator.advance()) {
				arrayList.add(kahluaTableIterator.getValue().toString());
			}
		}
	}

	private void tableToTiles(KahluaTable kahluaTable, ArrayList arrayList, String string) {
		this.tableToTiles((KahluaTableImpl)kahluaTable.rawget(string), arrayList);
	}

	public void addFenceTiles(int int1, KahluaTableImpl kahluaTableImpl) {
		KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
		while (true) {
			BentFences.Entry entry;
			do {
				do {
					if (!kahluaTableIterator.advance()) {
						return;
					}

					KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator.getValue();
					entry = new BentFences.Entry();
					entry.dir = IsoDirections.valueOf(kahluaTableImpl2.rawgetStr("dir"));
					this.tableToTiles(kahluaTableImpl2, entry.unbent, "unbent");
					this.tableToTiles(kahluaTableImpl2, entry.bent, "bent");
				}		 while (entry.unbent.isEmpty());
			}	 while (entry.unbent.size() != entry.bent.size());

			this.m_entries.add(entry);
			Iterator iterator;
			String string;
			ArrayList arrayList;
			for (iterator = entry.unbent.iterator(); iterator.hasNext(); arrayList.add(entry)) {
				string = (String)iterator.next();
				arrayList = (ArrayList)this.m_unbentMap.get(string);
				if (arrayList == null) {
					arrayList = new ArrayList();
					this.m_unbentMap.put(string, arrayList);
				}
			}

			for (iterator = entry.bent.iterator(); iterator.hasNext(); arrayList.add(entry)) {
				string = (String)iterator.next();
				arrayList = (ArrayList)this.m_bentMap.get(string);
				if (arrayList == null) {
					arrayList = new ArrayList();
					this.m_bentMap.put(string, arrayList);
				}
			}
		}
	}

	public boolean isBentObject(IsoObject object) {
		return this.getEntryForObject(object, (IsoDirections)null) != null;
	}

	public boolean isUnbentObject(IsoObject object) {
		return this.getEntryForObject(object, IsoDirections.Max) != null;
	}

	private BentFences.Entry getEntryForObject(IsoObject object, IsoDirections directions) {
		if (object != null && object.sprite != null && object.sprite.name != null) {
			boolean boolean1 = directions != null;
			ArrayList arrayList = boolean1 ? (ArrayList)this.m_unbentMap.get(object.sprite.name) : (ArrayList)this.m_bentMap.get(object.sprite.name);
			if (arrayList != null) {
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					BentFences.Entry entry = (BentFences.Entry)arrayList.get(int1);
					if ((!boolean1 || directions == IsoDirections.Max || directions == entry.dir) && this.isValidObject(object, entry, boolean1)) {
						return entry;
					}
				}
			}

			return null;
		} else {
			return null;
		}
	}

	private boolean isValidObject(IsoObject object, BentFences.Entry entry, boolean boolean1) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		ArrayList arrayList = boolean1 ? entry.unbent : entry.bent;
		int int1 = ((String)arrayList.get(2)).equals(object.sprite.name) ? 2 : (((String)arrayList.get(3)).equals(object.sprite.name) ? 3 : -1);
		if (int1 == -1) {
			return false;
		} else {
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				int int3 = object.square.x + (entry.isNorth() ? int2 - int1 : 0);
				int int4 = object.square.y + (entry.isNorth() ? 0 : int2 - int1);
				IsoGridSquare square = cell.getGridSquare(int3, int4, object.square.z);
				if (square == null) {
					return false;
				}

				if (int1 != int2 && this.getObjectForEntry(square, arrayList, int2) == null) {
					return false;
				}
			}

			return true;
		}
	}

	IsoObject getObjectForEntry(IsoGridSquare square, ArrayList arrayList, int int1) {
		for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			if (object.sprite != null && object.sprite.name != null && ((String)arrayList.get(int1)).equals(object.sprite.name)) {
				return object;
			}
		}

		return null;
	}

	public void swapTiles(IsoObject object, IsoDirections directions) {
		boolean boolean1 = directions != null;
		BentFences.Entry entry = this.getEntryForObject(object, directions);
		if (entry != null) {
			if (boolean1) {
				if (entry.isNorth() && directions != IsoDirections.N && directions != IsoDirections.S) {
					return;
				}

				if (!entry.isNorth() && directions != IsoDirections.W && directions != IsoDirections.E) {
					return;
				}
			}

			IsoCell cell = IsoWorld.instance.CurrentCell;
			ArrayList arrayList = boolean1 ? entry.unbent : entry.bent;
			int int1 = ((String)arrayList.get(2)).equals(object.sprite.name) ? 2 : (((String)arrayList.get(3)).equals(object.sprite.name) ? 3 : -1);
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				int int3 = object.square.x + (entry.isNorth() ? int2 - int1 : 0);
				int int4 = object.square.y + (entry.isNorth() ? 0 : int2 - int1);
				IsoGridSquare square = cell.getGridSquare(int3, int4, object.square.z);
				if (square != null) {
					IsoObject object2 = this.getObjectForEntry(square, arrayList, int2);
					if (object2 != null) {
						String string = boolean1 ? (String)entry.bent.get(int2) : (String)entry.unbent.get(int2);
						IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
						sprite.name = string;
						object2.setSprite(sprite);
						object2.transmitUpdatedSprite();
						square.RecalcAllWithNeighbours(true);
						MapCollisionData.instance.squareChanged(square);
						PolygonalMap2.instance.squareChanged(square);
						IsoRegions.squareChanged(square);
					}
				}
			}
		}
	}

	public void bendFence(IsoObject object, IsoDirections directions) {
		this.swapTiles(object, directions);
	}

	public void unbendFence(IsoObject object) {
		this.swapTiles(object, (IsoDirections)null);
	}

	public void Reset() {
		this.m_entries.clear();
		this.m_bentMap.clear();
		this.m_unbentMap.clear();
	}

	private static final class Entry {
		IsoDirections dir;
		final ArrayList unbent;
		final ArrayList bent;

		private Entry() {
			this.dir = IsoDirections.Max;
			this.unbent = new ArrayList();
			this.bent = new ArrayList();
		}

		boolean isNorth() {
			return this.dir == IsoDirections.N || this.dir == IsoDirections.S;
		}
	}
}
