package zombie.ai.astar.heuristics;

import zombie.ai.astar.AStarHeuristic;
import zombie.ai.astar.Mover;
import zombie.ai.astar.TileBasedMap;


public class ManhattanHeuristic implements AStarHeuristic {
	private int minimumCost;

	public ManhattanHeuristic(int int1) {
		this.minimumCost = int1;
	}

	public float getCost(TileBasedMap tileBasedMap, Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		return (float)(this.minimumCost * (Math.abs(int1 - int4) + Math.abs(int2 - int5) + Math.abs(int3 - int6)));
	}
}
