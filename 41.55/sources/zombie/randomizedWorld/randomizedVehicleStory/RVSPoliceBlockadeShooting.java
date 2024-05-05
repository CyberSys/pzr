package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;


public final class RVSPoliceBlockadeShooting extends RandomizedVehicleStoryBase {

	public RVSPoliceBlockadeShooting() {
		this.name = "Police Blockade Shooting";
		this.setChance(1);
		this.setMaximumDays(30);
	}

	public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean boolean1) {
		boolean boolean2 = super.isValid(zone, chunk, boolean1);
		if (!boolean2) {
			return false;
		} else {
			byte byte1 = 10;
			if (this.horizontalZone) {
				return chunk.wy * byte1 <= zone.y && (chunk.wy + 1) * byte1 >= zone.y + zone.h;
			} else {
				return chunk.wx * byte1 <= zone.x && (chunk.wx + 1) * byte1 >= zone.x + zone.w;
			}
		}
	}

	public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		boolean boolean1 = Rand.NextBool(2);
		IsoDirections directions = Rand.NextBool(2) ? IsoDirections.N : IsoDirections.S;
		if (!this.horizontalZone) {
			directions = Rand.NextBool(2) ? IsoDirections.E : IsoDirections.W;
		}

		IsoDirections directions2 = directions.RotLeft(4);
		int int1 = this.minX;
		int int2 = this.minY;
		if (boolean1) {
			int1 = this.minX;
			int2 = this.maxY;
			if (this.horizontalZone) {
				int1 = this.maxX;
				int2 = this.minY;
			}
		}

		IsoGridSquare square = IsoCell.getInstance().getGridSquare(int1, int2, zone.z);
		if (square != null) {
			byte byte1 = 0;
			byte byte2 = 0;
			if (!this.horizontalZone) {
				byte1 = 5;
			} else {
				byte2 = 5;
			}

			IsoGridSquare square2 = square.getCell().getGridSquare(square.x + byte1, square.y + byte2, square.z);
			if (square2 != null) {
				String string = "Base.CarLightsPolice";
				if (Rand.NextBool(3)) {
					string = "Base.PickUpVanLightsPolice";
				}

				BaseVehicle baseVehicle = this.addVehicle(zone, square, chunk, (String)null, string, directions);
				if (Rand.NextBool(3)) {
					baseVehicle.setHeadlightsOn(true);
					baseVehicle.setLightbarLightsMode(2);
				}

				BaseVehicle baseVehicle2 = this.addVehicle(zone, square2, chunk, (String)null, string, directions2);
				if (Rand.NextBool(3)) {
					baseVehicle2.setHeadlightsOn(true);
					baseVehicle2.setLightbarLightsMode(2);
				}

				String string2 = "Police";
				Integer integer = null;
				if (Rand.NextBool(6)) {
					string2 = "PoliceRiot";
					integer = 0;
				}

				string2 = "PoliceRiot";
				integer = 0;
				this.addZombiesOnVehicle(Rand.Next(2, 4), string2, integer, baseVehicle);
				this.addZombiesOnVehicle(Rand.Next(2, 4), string2, integer, baseVehicle2);
				int int3 = Rand.Next(7, 15);
				IsoDirections directions3 = null;
				if (!this.horizontalZone) {
					if (boolean1) {
						directions3 = IsoDirections.S;
					} else {
						directions3 = IsoDirections.N;
					}
				} else if (boolean1) {
					directions3 = IsoDirections.W;
				} else {
					directions3 = IsoDirections.E;
				}

				int int4;
				for (int int5 = 0; int5 < int3; ++int5) {
					boolean boolean2 = false;
					boolean boolean3 = false;
					int int6;
					if (!this.horizontalZone) {
						if (boolean1) {
							int6 = square.y + Rand.Next(3, 6);
						} else {
							int6 = square.y + Rand.Next(-6, -3);
						}

						int4 = Rand.Next(zone.x, zone.x + zone.w);
					} else {
						if (boolean1) {
							int4 = square.x + Rand.Next(3, 6);
						} else {
							int4 = square.x + Rand.Next(-6, -3);
						}

						int6 = Rand.Next(zone.y, zone.y + zone.h);
					}

					createRandomDeadBody(int4, int6, zone.z, (IsoDirections)null, 10, 10);
					this.addTraitOfBlood(directions3, 5, int4, int6, zone.z);
				}

				byte byte3;
				IsoGridSquare square3;
				if (!this.horizontalZone) {
					byte3 = -2;
					if (boolean1) {
						byte3 = 2;
					}

					for (int4 = zone.x - 1; int4 < zone.x + zone.w + 1; ++int4) {
						square3 = IsoCell.getInstance().getGridSquare(int4, square.y + byte3, zone.z);
						if (square3 != null) {
							if (int4 != zone.x - 1 && int4 != zone.x + zone.w) {
								square3.AddTileObject(IsoObject.getNew(square3, "construction_01_8", (String)null, false));
							} else {
								square3.AddTileObject(IsoObject.getNew(square3, "street_decoration_01_26", (String)null, false));
							}
						}
					}
				} else {
					byte3 = -2;
					if (boolean1) {
						byte3 = 2;
					}

					for (int4 = zone.y - 1; int4 < zone.y + zone.h + 1; ++int4) {
						square3 = IsoCell.getInstance().getGridSquare(square.x + byte3, int4, zone.z);
						if (square3 != null) {
							if (int4 != zone.y - 1 && int4 != zone.y + zone.h) {
								square3.AddTileObject(IsoObject.getNew(square3, "construction_01_9", (String)null, false));
							} else {
								square3.AddTileObject(IsoObject.getNew(square3, "street_decoration_01_26", (String)null, false));
							}
						}
					}
				}
			}
		}
	}
}
