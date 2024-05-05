package zombie.randomizedWorld.randomizedBuilding.TableStories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;


public class RBTableStoryBase extends RandomizedBuildingBase {
	public static ArrayList allStories = new ArrayList();
	public static int totalChance = 0;
	protected int chance = 0;
	protected ArrayList rooms = new ArrayList();
	protected boolean need2Tables = false;
	protected boolean ignoreAgainstWall = false;
	protected IsoObject table2 = null;
	protected IsoObject table1 = null;
	protected boolean westTable = false;
	private static final HashMap rbtsmap = new HashMap();
	private static final ArrayList tableObjects = new ArrayList();
	public ArrayList fullTableMap = new ArrayList();

	public static void initStories(IsoGridSquare square, IsoObject object) {
		if (allStories.isEmpty()) {
			allStories.add(new RBTSBreakfast());
			allStories.add(new RBTSDinner());
			allStories.add(new RBTSSoup());
			allStories.add(new RBTSSewing());
			allStories.add(new RBTSElectronics());
			allStories.add(new RBTSFoodPreparation());
			allStories.add(new RBTSButcher());
			allStories.add(new RBTSSandwich());
			allStories.add(new RBTSDrink());
		}

		totalChance = 0;
		rbtsmap.clear();
		for (int int1 = 0; int1 < allStories.size(); ++int1) {
			RBTableStoryBase rBTableStoryBase = (RBTableStoryBase)allStories.get(int1);
			if (rBTableStoryBase.isValid(square, object, false) && rBTableStoryBase.isTimeValid(false)) {
				totalChance += rBTableStoryBase.chance;
				rbtsmap.put(rBTableStoryBase, rBTableStoryBase.chance);
			}
		}
	}

	public static RBTableStoryBase getRandomStory(IsoGridSquare square, IsoObject object) {
		initStories(square, object);
		int int1 = Rand.Next(totalChance);
		Iterator iterator = rbtsmap.keySet().iterator();
		int int2 = 0;
		RBTableStoryBase rBTableStoryBase;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			rBTableStoryBase = (RBTableStoryBase)iterator.next();
			int2 += (Integer)rbtsmap.get(rBTableStoryBase);
		} while (int1 >= int2);

		rBTableStoryBase.table1 = object;
		return rBTableStoryBase;
	}

	public boolean isValid(IsoGridSquare square, IsoObject object, boolean boolean1) {
		if (boolean1) {
			return true;
		} else if (this.rooms != null && square.getRoom() != null && !this.rooms.contains(square.getRoom().getName())) {
			return false;
		} else {
			if (this.need2Tables) {
				this.table2 = this.getSecondTable(object);
				if (this.table2 == null) {
					return false;
				}
			}

			return !this.ignoreAgainstWall || !square.getWallFull();
		}
	}

	public IsoObject getSecondTable(IsoObject object) {
		this.westTable = true;
		IsoGridSquare square = object.getSquare();
		if (this.ignoreAgainstWall && square.getWallFull()) {
			return null;
		} else {
			object.getSpriteGridObjects(tableObjects);
			IsoGridSquare square2 = square.getAdjacentSquare(IsoDirections.W);
			IsoObject object2 = this.checkForTable(square2, object, tableObjects);
			if (object2 == null) {
				square2 = square.getAdjacentSquare(IsoDirections.E);
				object2 = this.checkForTable(square2, object, tableObjects);
			}

			if (object2 == null) {
				this.westTable = false;
			}

			if (object2 == null) {
				square2 = square.getAdjacentSquare(IsoDirections.N);
				object2 = this.checkForTable(square2, object, tableObjects);
			}

			if (object2 == null) {
				square2 = square.getAdjacentSquare(IsoDirections.S);
				object2 = this.checkForTable(square2, object, tableObjects);
			}

			return object2 != null && this.ignoreAgainstWall && square2.getWallFull() ? null : object2;
		}
	}

	private IsoObject checkForTable(IsoGridSquare square, IsoObject object, ArrayList arrayList) {
		if (square == null) {
			return null;
		} else if (square.isSomethingTo(object.getSquare())) {
			return null;
		} else {
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object2 = (IsoObject)square.getObjects().get(int1);
				if ((arrayList.isEmpty() || arrayList.contains(object2)) && object2.getProperties().isTable() && object2.getContainer() == null && object2 != object) {
					return object2;
				}
			}

			return null;
		}
	}
}
