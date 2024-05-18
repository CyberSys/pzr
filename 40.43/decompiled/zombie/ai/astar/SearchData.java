package zombie.ai.astar;

import zombie.iso.IsoGridSquare;

public class SearchData {
   public float cost;
   public short depth;
   public float heuristic;
   public IsoGridSquare parent;
}
