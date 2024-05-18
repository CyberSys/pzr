package zombie.ai.astar.heuristics;

import zombie.ai.astar.AStarHeuristic;
import zombie.ai.astar.Mover;
import zombie.ai.astar.TileBasedMap;


public class ClosestSquaredHeuristic implements AStarHeuristic {

	public float getCost(TileBasedMap tileBasedMap, Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		float float1 = (float)(int4 - int1);
		float float2 = (float)(int5 - int2);
		float float3 = (float)(int6 - int3);
		return float1 * float1 + float2 * float2 + float3 * float3;
	}
}
