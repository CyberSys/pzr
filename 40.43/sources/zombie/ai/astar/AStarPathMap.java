package zombie.ai.astar;

import java.io.FileNotFoundException;
import java.io.IOException;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;


public class AStarPathMap {
	public TileBasedMap map;

	public AStarPathMap(TileBasedMap tileBasedMap) {
		this.map = tileBasedMap;
		int int1 = tileBasedMap.getWidthInTiles();
		float float1 = (float)int1;
	}

	public void LoadPrecalcFlle() throws FileNotFoundException, IOException {
	}

	public void DoPrecalcFile(AStarPathFinder aStarPathFinder) {
	}

	public int getFreeIndex(IsoGameCharacter gameCharacter) {
		return 0;
	}

	public float getMovementCost(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		return this.map.getCost(mover, int1, int2, int3, int4, int5, int6);
	}

	protected boolean isValidLocation(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9) {
		boolean boolean1 = false;
		if (int1 != int4 || int2 != int5 || int3 != int6) {
			boolean1 = this.map.blocked(mover, int4, int5, int6, int7, int8, int9);
		}

		return !boolean1;
	}

	public IsoGridSquare getNode(int int1, int int2, int int3) {
		return IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
	}
}
