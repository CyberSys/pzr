package zombie.ai.astar;


public interface AStarHeuristic {

	float getCost(TileBasedMap tileBasedMap, Mover mover, int int1, int int2, int int3, int int4, int int5, int int6);
}