package zombie.iso;

import zombie.core.Rand;



public enum IsoDirections {

	N,
	NW,
	W,
	SW,
	S,
	SE,
	E,
	NE,
	Max,
	VALUES,
	directionLookup,
	temp,
	index;

	private IsoDirections(int int1) {
		this.index = int1;
	}
	public static IsoDirections fromIndex(int int1) {
		while (int1 < 0) {
			int1 += 8;
		}

		int1 %= 8;
		return VALUES[int1];
	}
	public IsoDirections RotLeft(int int1) {
		IsoDirections directions = RotLeft(this);
		for (int int2 = 0; int2 < int1 - 1; ++int2) {
			directions = RotLeft(directions);
		}

		return directions;
	}
	public IsoDirections RotRight(int int1) {
		IsoDirections directions = RotRight(this);
		for (int int2 = 0; int2 < int1 - 1; ++int2) {
			directions = RotRight(directions);
		}

		return directions;
	}
	public IsoDirections RotLeft() {
		return RotLeft(this);
	}
	public IsoDirections RotRight() {
		return RotRight(this);
	}
	public static IsoDirections RotLeft(IsoDirections directions) {
		switch (directions) {
		case NE: 
			return N;
		
		case N: 
			return NW;
		
		case NW: 
			return W;
		
		case W: 
			return SW;
		
		case SW: 
			return S;
		
		case S: 
			return SE;
		
		case SE: 
			return E;
		
		case E: 
			return NE;
		
		default: 
			return Max;
		
		}
	}
	public static IsoDirections RotRight(IsoDirections directions) {
		switch (directions) {
		case NE: 
			return E;
		
		case N: 
			return NE;
		
		case NW: 
			return N;
		
		case W: 
			return NW;
		
		case SW: 
			return W;
		
		case S: 
			return SW;
		
		case SE: 
			return S;
		
		case E: 
			return SE;
		
		default: 
			return Max;
		
		}
	}
	public static void generateTables() {
		directionLookup = new IsoDirections[200][200];
		for (int int1 = 0; int1 < 200; ++int1) {
			for (int int2 = 0; int2 < 200; ++int2) {
				int int3 = int1 - 100;
				int int4 = int2 - 100;
				float float1 = (float)int3 / 100.0F;
				float float2 = (float)int4 / 100.0F;
				Vector2 vector2 = new Vector2(float1, float2);
				vector2.normalize();
				directionLookup[int1][int2] = fromAngleActual(vector2);
			}
		}
	}
	public static IsoDirections fromAngleActual(Vector2 vector2) {
		temp.x = vector2.x;
		temp.y = vector2.y;
		temp.normalize();
		float float1 = temp.getDirectionNeg();
		float float2 = 0.7853982F;
		float float3 = 6.2831855F;
		float3 = (float)((double)float3 + Math.toRadians(112.5));
		for (int int1 = 0; int1 < 8; ++int1) {
			float3 += float2;
			if (float1 >= float3 && float1 <= float3 + float2 || float1 + 6.2831855F >= float3 && float1 + 6.2831855F <= float3 + float2 || float1 - 6.2831855F >= float3 && float1 - 6.2831855F <= float3 + float2) {
				return fromIndex(int1);
			}

			if ((double)float3 > 6.283185307179586) {
				float3 = (float)((double)float3 - 6.283185307179586);
			}
		}

		if (temp.x > 0.5F) {
			if (temp.y < -0.5F) {
				return NE;
			} else if (temp.y > 0.5F) {
				return SE;
			} else {
				return E;
			}
		} else if (temp.x < -0.5F) {
			if (temp.y < -0.5F) {
				return NW;
			} else if (temp.y > 0.5F) {
				return SW;
			} else {
				return W;
			}
		} else if (temp.y < -0.5F) {
			return N;
		} else if (temp.y > 0.5F) {
			return S;
		} else {
			return N;
		}
	}
	public static IsoDirections fromAngle(Vector2 vector2) {
		temp.x = vector2.x;
		temp.y = vector2.y;
		if (vector2.getLength() != 1.0F) {
			temp.normalize();
		}

		if (directionLookup == null) {
			generateTables();
		}

		int int1 = (int)((temp.x + 1.0F) * 100.0F);
		int int2 = (int)((temp.y + 1.0F) * 100.0F);
		if (int1 >= 200) {
			int1 = 199;
		}

		if (int2 >= 200) {
			int2 = 199;
		}

		if (int1 < 0) {
			int1 = 0;
		}

		if (int2 < 0) {
			int2 = 0;
		}

		return directionLookup[int1][int2];
	}
	public static IsoDirections cardinalFromAngle(Vector2 vector2) {
		boolean boolean1 = vector2.getX() >= vector2.getY();
		boolean boolean2 = vector2.getX() > -vector2.getY();
		if (boolean1) {
			return boolean2 ? E : N;
		} else {
			return boolean2 ? S : W;
		}
	}
	public static IsoDirections reverse(IsoDirections directions) {
		switch (directions) {
		case NE: 
			return SW;
		
		case N: 
			return S;
		
		case NW: 
			return SE;
		
		case W: 
			return E;
		
		case SW: 
			return NE;
		
		case S: 
			return N;
		
		case SE: 
			return NW;
		
		case E: 
			return W;
		
		default: 
			return Max;
		
		}
	}
	public int index() {
		return this.index % 8;
	}
	public String toCompassString() {
		switch (this.index) {
		case 0: 
			return "9";
		
		case 1: 
			return "8";
		
		case 2: 
			return "7";
		
		case 3: 
			return "4";
		
		case 4: 
			return "1";
		
		case 5: 
			return "2";
		
		case 6: 
			return "3";
		
		case 7: 
			return "6";
		
		default: 
			return "";
		
		}
	}
	public Vector2 ToVector() {
		switch (this) {
		case NE: 
			temp.x = 1.0F;
			temp.y = -1.0F;
			break;
		
		case N: 
			temp.x = 0.0F;
			temp.y = -1.0F;
			break;
		
		case NW: 
			temp.x = -1.0F;
			temp.y = -1.0F;
			break;
		
		case W: 
			temp.x = -1.0F;
			temp.y = 0.0F;
			break;
		
		case SW: 
			temp.x = -1.0F;
			temp.y = 1.0F;
			break;
		
		case S: 
			temp.x = 0.0F;
			temp.y = 1.0F;
			break;
		
		case SE: 
			temp.x = 1.0F;
			temp.y = 1.0F;
			break;
		
		case E: 
			temp.x = 1.0F;
			temp.y = 0.0F;
		
		}
		temp.normalize();
		return temp;
	}
	public float toAngle() {
		float float1 = 0.7853982F;
		switch (this) {
		case NE: 
			return float1 * 7.0F;
		
		case N: 
			return float1 * 0.0F;
		
		case NW: 
			return float1 * 1.0F;
		
		case W: 
			return float1 * 2.0F;
		
		case SW: 
			return float1 * 3.0F;
		
		case S: 
			return float1 * 4.0F;
		
		case SE: 
			return float1 * 5.0F;
		
		case E: 
			return float1 * 6.0F;
		
		default: 
			return 0.0F;
		
		}
	}
	public static IsoDirections getRandom() {
		return fromIndex(Rand.Next(0, Max.index));
	}
	private static IsoDirections[] $values() {
		return new IsoDirections[]{N, NW, W, SW, S, SE, E, NE, Max};
	}
}
