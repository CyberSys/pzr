package zombie.erosion;

import zombie.erosion.categories.ErosionCategory;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;


public final class ErosionWorld {

	public boolean init() {
		ErosionRegions.init();
		return true;
	}

	public void validateSpawn(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk) {
		boolean boolean1 = square.Is(IsoFlagType.exterior);
		boolean boolean2 = square.Has(IsoObjectType.wall);
		IsoObject object = square.getFloor();
		String string = object != null && object.getSprite() != null ? object.getSprite().getName() : null;
		if (string == null) {
			square2.doNothing = true;
		} else {
			boolean boolean3 = false;
			for (int int1 = 0; int1 < ErosionRegions.regions.size(); ++int1) {
				ErosionRegions.Region region = (ErosionRegions.Region)ErosionRegions.regions.get(int1);
				String string2 = region.tileNameMatch;
				if ((string2 == null || string.startsWith(string2)) && (!region.checkExterior || region.isExterior == boolean1) && (!region.hasWall || region.hasWall == boolean2)) {
					for (int int2 = 0; int2 < region.categories.size(); ++int2) {
						ErosionCategory erosionCategory = (ErosionCategory)region.categories.get(int2);
						boolean boolean4 = erosionCategory.replaceExistingObject(square, square2, chunk, boolean1, boolean2);
						if (!boolean4) {
							boolean4 = erosionCategory.validateSpawn(square, square2, chunk, boolean1, boolean2, false);
						}

						if (boolean4) {
							boolean3 = true;
							break;
						}
					}
				}
			}

			if (!boolean3) {
				square2.doNothing = true;
			}
		}
	}

	public void update(IsoGridSquare square, ErosionData.Square square2, ErosionData.Chunk chunk, int int1) {
		if (square2.regions != null) {
			for (int int2 = 0; int2 < square2.regions.size(); ++int2) {
				ErosionCategory.Data data = (ErosionCategory.Data)square2.regions.get(int2);
				ErosionCategory erosionCategory = ErosionRegions.getCategory(data.regionID, data.categoryID);
				int int3 = square2.regions.size();
				erosionCategory.update(square, square2, data, chunk, int1);
				if (int3 > square2.regions.size()) {
					--int2;
				}
			}
		}
	}
}
