package zombie.behaviors.survivor;

import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;


public class TravelLocationFinder {

	private static float ScoreLocation(SurvivorDesc survivorDesc, IsoGridSquare square) {
		float float1 = 1.0F;
		if (square.getRoom() != null) {
			float1 += 10.0F;
			if (square.getRoom().building == null) {
				return float1;
			}

			float1 += square.getRoom().building.ScoreBuildingPersonSpecific(survivorDesc, false);
		}

		return float1;
	}

	public static IsoGridSquare FindLocation(SurvivorDesc survivorDesc, float float1, float float2, float float3, float float4, int int1) {
		IsoGridSquare square = null;
		float float5 = 0.0F;
		int int2 = 100;
		for (int int3 = 0; int3 < int1; ++int3) {
			--int2;
			if (int2 <= 0) {
				return null;
			}

			float float6 = 0.0F;
			int int4 = Rand.Next((int)float1, (int)float3);
			int int5 = Rand.Next((int)float2, (int)float4);
			IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, 0);
			if (square2 != null && !square2.getProperties().Is(IsoFlagType.solidtrans) && !square2.getProperties().Is(IsoFlagType.solid)) {
				float6 = ScoreLocation(survivorDesc, square2);
				if (float6 > float5) {
					square = square2;
					float5 = float6;
				}
			} else {
				--int3;
			}
		}

		if (float5 > 0.0F) {
			return square;
		} else {
			return null;
		}
	}
}
