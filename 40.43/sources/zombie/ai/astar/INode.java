package zombie.ai.astar;

import zombie.iso.IsoGridSquare;


public interface INode {

	Integer getID();

	int compareTo(Object object);

	int setParent(int int1, int int2, IsoGridSquare square);

	SearchData getSearchData(int int1);
}
