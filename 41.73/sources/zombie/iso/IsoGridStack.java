package zombie.iso;

import java.util.ArrayList;


public class IsoGridStack {
	public ArrayList Squares;

	public IsoGridStack(int int1) {
		this.Squares = new ArrayList(int1);
		for (int int2 = 0; int2 < int1; ++int2) {
			this.Squares.add(new ArrayList(5000));
		}
	}
}
