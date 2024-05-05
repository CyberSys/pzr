package zombie.iso;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.List;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.MapCollisionData;
import zombie.SoundManager;
import zombie.core.Rand;
import zombie.core.properties.PropertyContainer;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.PolygonalMap2;


public class BrokenFences {
	private static final BrokenFences instance = new BrokenFences();
	private final THashMap s_unbrokenMap = new THashMap();
	private final THashMap s_brokenLeftMap = new THashMap();
	private final THashMap s_brokenRightMap = new THashMap();
	private final THashMap s_allMap = new THashMap();

	public static BrokenFences getInstance() {
		return instance;
	}

	private ArrayList tableToTiles(KahluaTableImpl kahluaTableImpl) {
		if (kahluaTableImpl == null) {
			return null;
		} else {
			ArrayList arrayList = null;
			for (KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator(); kahluaTableIterator.advance(); arrayList.add(kahluaTableIterator.getValue().toString())) {
				if (arrayList == null) {
					arrayList = new ArrayList();
				}
			}

			return arrayList;
		}
	}

	private ArrayList tableToTiles(KahluaTable kahluaTable, String string) {
		return this.tableToTiles((KahluaTableImpl)kahluaTable.rawget(string));
	}

	public void addBrokenTiles(KahluaTableImpl kahluaTableImpl) {
		KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
		while (kahluaTableIterator.advance()) {
			String string = kahluaTableIterator.getKey().toString();
			if (!"VERSION".equalsIgnoreCase(string)) {
				KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator.getValue();
				BrokenFences.Tile tile = new BrokenFences.Tile();
				tile.self = this.tableToTiles(kahluaTableImpl2, "self");
				tile.left = this.tableToTiles(kahluaTableImpl2, "left");
				tile.right = this.tableToTiles(kahluaTableImpl2, "right");
				this.s_unbrokenMap.put(string, tile);
				PZArrayUtil.forEach((List)tile.left, (kahluaTableIteratorx)->{
					this.s_brokenLeftMap.put(kahluaTableIteratorx, tile);
				});

				PZArrayUtil.forEach((List)tile.right, (kahluaTableIteratorx)->{
					this.s_brokenRightMap.put(kahluaTableIteratorx, tile);
				});
			}
		}

		this.s_allMap.putAll(this.s_unbrokenMap);
		this.s_allMap.putAll(this.s_brokenLeftMap);
		this.s_allMap.putAll(this.s_brokenRightMap);
	}

	public void addDebrisTiles(KahluaTableImpl kahluaTableImpl) {
		KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
		while (kahluaTableIterator.advance()) {
			String string = kahluaTableIterator.getKey().toString();
			if (!"VERSION".equalsIgnoreCase(string)) {
				KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator.getValue();
				BrokenFences.Tile tile = (BrokenFences.Tile)this.s_unbrokenMap.get(string);
				if (tile == null) {
					throw new IllegalArgumentException("addDebrisTiles() with unknown tile");
				}

				tile.debrisN = this.tableToTiles(kahluaTableImpl2, "north");
				tile.debrisS = this.tableToTiles(kahluaTableImpl2, "south");
				tile.debrisW = this.tableToTiles(kahluaTableImpl2, "west");
				tile.debrisE = this.tableToTiles(kahluaTableImpl2, "east");
			}
		}
	}

	public void setDestroyed(IsoObject object) {
		object.RemoveAttachedAnims();
		object.getSquare().removeBlood(false, true);
		this.updateSprite(object, true, true);
	}

	public void setDamagedLeft(IsoObject object) {
		this.updateSprite(object, true, false);
	}

	public void setDamagedRight(IsoObject object) {
		this.updateSprite(object, false, true);
	}

	public void updateSprite(IsoObject object, boolean boolean1, boolean boolean2) {
		if (this.isBreakableObject(object)) {
			BrokenFences.Tile tile = (BrokenFences.Tile)this.s_allMap.get(object.sprite.name);
			String string = null;
			if (boolean1 && boolean2) {
				string = tile.pickRandom(tile.self);
			} else if (boolean1) {
				string = tile.pickRandom(tile.left);
			} else if (boolean2) {
				string = tile.pickRandom(tile.right);
			}

			if (string != null) {
				IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
				sprite.name = string;
				object.setSprite(sprite);
				object.transmitUpdatedSprite();
				object.getSquare().RecalcAllWithNeighbours(true);
				MapCollisionData.instance.squareChanged(object.getSquare());
				PolygonalMap2.instance.squareChanged(object.getSquare());
				IsoRegions.squareChanged(object.getSquare());
			}
		}
	}

	private boolean isNW(IsoObject object) {
		PropertyContainer propertyContainer = object.getProperties();
		return propertyContainer.Is(IsoFlagType.collideN) && propertyContainer.Is(IsoFlagType.collideW);
	}

	private void damageAdjacent(IsoGridSquare square, IsoDirections directions, IsoDirections directions2) {
		IsoGridSquare square2 = square.getAdjacentSquare(directions);
		if (square2 != null) {
			boolean boolean1 = directions == IsoDirections.W || directions == IsoDirections.E;
			IsoObject object = this.getBreakableObject(square2, boolean1);
			if (object != null) {
				boolean boolean2 = directions == IsoDirections.N || directions == IsoDirections.E;
				boolean boolean3 = directions == IsoDirections.S || directions == IsoDirections.W;
				if (!this.isNW(object) || directions != IsoDirections.S && directions != IsoDirections.E) {
					if (boolean2 && this.isBrokenRight(object)) {
						this.destroyFence(object, directions2);
					} else if (boolean3 && this.isBrokenLeft(object)) {
						this.destroyFence(object, directions2);
					} else {
						this.updateSprite(object, boolean2, boolean3);
					}
				}
			}
		}
	}

	public void destroyFence(IsoObject object, IsoDirections directions) {
		if (this.isBreakableObject(object)) {
			IsoGridSquare square = object.getSquare();
			if (GameServer.bServer) {
				GameServer.PlayWorldSoundServer("BreakObject", false, square, 1.0F, 20.0F, 1.0F, true);
			} else {
				SoundManager.instance.PlayWorldSound("BreakObject", square, 1.0F, 20.0F, 1.0F, true);
			}

			boolean boolean1 = object.getProperties().Is(IsoFlagType.collideN);
			boolean boolean2 = object.getProperties().Is(IsoFlagType.collideW);
			if (object instanceof IsoThumpable) {
				IsoObject object2 = IsoObject.getNew();
				object2.setSquare(square);
				object2.setSprite(object.getSprite());
				int int1 = object.getObjectIndex();
				square.transmitRemoveItemFromSquare(object);
				square.transmitAddObjectToSquare(object2, int1);
				object = object2;
			}

			this.addDebrisObject(object, directions);
			this.setDestroyed(object);
			if (boolean1 && boolean2) {
				this.damageAdjacent(square, IsoDirections.S, directions);
				this.damageAdjacent(square, IsoDirections.E, directions);
			} else if (boolean1) {
				this.damageAdjacent(square, IsoDirections.W, directions);
				this.damageAdjacent(square, IsoDirections.E, directions);
			} else if (boolean2) {
				this.damageAdjacent(square, IsoDirections.N, directions);
				this.damageAdjacent(square, IsoDirections.S, directions);
			}

			square.RecalcAllWithNeighbours(true);
			MapCollisionData.instance.squareChanged(square);
			PolygonalMap2.instance.squareChanged(square);
			IsoRegions.squareChanged(square);
		}
	}

	private boolean isUnbroken(IsoObject object) {
		return object != null && object.sprite != null && object.sprite.name != null ? this.s_unbrokenMap.contains(object.sprite.name) : false;
	}

	private boolean isBrokenLeft(IsoObject object) {
		return object != null && object.sprite != null && object.sprite.name != null ? this.s_brokenLeftMap.contains(object.sprite.name) : false;
	}

	private boolean isBrokenRight(IsoObject object) {
		return object != null && object.sprite != null && object.sprite.name != null ? this.s_brokenRightMap.contains(object.sprite.name) : false;
	}

	public boolean isBreakableObject(IsoObject object) {
		return object != null && object.sprite != null && object.sprite.name != null ? this.s_allMap.containsKey(object.sprite.name) : false;
	}

	private IsoObject getBreakableObject(IsoGridSquare square, boolean boolean1) {
		for (int int1 = 0; int1 < square.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)square.Objects.get(int1);
			if (this.isBreakableObject(object) && (boolean1 && object.getProperties().Is(IsoFlagType.collideN) || !boolean1 && object.getProperties().Is(IsoFlagType.collideW))) {
				return object;
			}
		}

		return null;
	}

	private void addItems(IsoObject object, IsoGridSquare square) {
		PropertyContainer propertyContainer = object.getProperties();
		if (propertyContainer != null) {
			String string = propertyContainer.Val("Material");
			String string2 = propertyContainer.Val("Material2");
			String string3 = propertyContainer.Val("Material3");
			if ("Wood".equals(string) || "Wood".equals(string2) || "Wood".equals(string3)) {
				square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
				if (Rand.NextBool(5)) {
					square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
				}
			}

			if (("MetalBars".equals(string) || "MetalBars".equals(string2) || "MetalBars".equals(string3)) && Rand.NextBool(2)) {
				square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.MetalBar"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("MetalWire".equals(string) || "MetalWire".equals(string2) || "MetalWire".equals(string3)) && Rand.NextBool(3)) {
				square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Wire"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("Nails".equals(string) || "Nails".equals(string2) || "Nails".equals(string3)) && Rand.NextBool(2)) {
				square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Nails"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("Screws".equals(string) || "Screws".equals(string2) || "Screws".equals(string3)) && Rand.NextBool(2)) {
				square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Screws"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}
		}
	}

	private void addDebrisObject(IsoObject object, IsoDirections directions) {
		if (this.isBreakableObject(object)) {
			BrokenFences.Tile tile = (BrokenFences.Tile)this.s_allMap.get(object.sprite.name);
			IsoGridSquare square = object.getSquare();
			String string;
			switch (directions) {
			case N: 
				string = tile.pickRandom(tile.debrisN);
				square = square.getAdjacentSquare(directions);
				break;
			
			case S: 
				string = tile.pickRandom(tile.debrisS);
				break;
			
			case W: 
				string = tile.pickRandom(tile.debrisW);
				square = square.getAdjacentSquare(directions);
				break;
			
			case E: 
				string = tile.pickRandom(tile.debrisE);
				break;
			
			default: 
				throw new IllegalArgumentException("invalid direction");
			
			}

			if (string != null && square != null && square.TreatAsSolidFloor()) {
				IsoObject object2 = IsoObject.getNew(square, string, (String)null, false);
				square.transmitAddObjectToSquare(object2, square == object.getSquare() ? object.getObjectIndex() : -1);
				this.addItems(object, square);
			}
		}
	}

	public void Reset() {
		this.s_unbrokenMap.clear();
		this.s_brokenLeftMap.clear();
		this.s_brokenRightMap.clear();
		this.s_allMap.clear();
	}

	private static final class Tile {
		ArrayList self = null;
		ArrayList left = null;
		ArrayList right = null;
		ArrayList debrisN = null;
		ArrayList debrisS = null;
		ArrayList debrisW = null;
		ArrayList debrisE = null;

		String pickRandom(ArrayList arrayList) {
			return arrayList == null ? null : (String)PZArrayUtil.pickRandom((List)arrayList);
		}
	}
}
