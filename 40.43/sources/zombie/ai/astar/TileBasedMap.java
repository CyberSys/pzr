package zombie.ai.astar;

import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;


public interface TileBasedMap {

	boolean blocked(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6);

	float getCost(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6);

	int getElevInTiles();

	int getHeightInTiles();

	int getWidthInTiles();

	void pathFinderVisited(int int1, int int2, int int3);

	boolean isNull(int int1, int int2, int int3);

	boolean IsStairsNode(IsoGridSquare square, IsoGridSquare square2, IsoDirections directions);
}
