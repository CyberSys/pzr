package zombie.iso;


public class IsoDirectionSet {
	public int set = 0;

	public static IsoDirections rotate(IsoDirections directions, int int1) {
		int1 += directions.index();
		int1 %= 8;
		return IsoDirections.fromIndex(int1);
	}

	public IsoDirections getNext() {
		for (int int1 = 0; int1 < 8; ++int1) {
			int int2 = 1 << int1;
			if ((this.set & int2) != 0) {
				this.set ^= int2;
				return IsoDirections.fromIndex(int1);
			}
		}

		return IsoDirections.Max;
	}
}
