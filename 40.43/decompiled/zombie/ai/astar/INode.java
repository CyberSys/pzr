package zombie.ai.astar;

import zombie.iso.IsoGridSquare;

public interface INode {
   Integer getID();

   int compareTo(Object var1);

   int setParent(int var1, int var2, IsoGridSquare var3);

   SearchData getSearchData(int var1);
}
