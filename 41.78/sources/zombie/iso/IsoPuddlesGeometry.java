package zombie.iso;

import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.popman.ObjectPool;


public final class IsoPuddlesGeometry {
	final float[] x = new float[4];
	final float[] y = new float[4];
	final float[] pdne = new float[4];
	final float[] pdnw = new float[4];
	final float[] pda = new float[4];
	final float[] pnon = new float[4];
	final int[] color = new int[4];
	IsoGridSquare square = null;
	boolean bRecalc = true;
	private boolean interiorCalc = false;
	public static final ObjectPool pool = new ObjectPool(IsoPuddlesGeometry::new);

	public IsoPuddlesGeometry init(IsoGridSquare square) {
		this.interiorCalc = false;
		this.x[0] = IsoUtils.XToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3), (float)square.z, square.z);
		this.y[0] = IsoUtils.YToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3), (float)square.z, square.z);
		this.x[1] = IsoUtils.XToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3 + 1), 0.0F, 0);
		this.y[1] = IsoUtils.YToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3 + 1), 0.0F, 0);
		this.x[2] = IsoUtils.XToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3 + 1), 0.0F, 0);
		this.y[2] = IsoUtils.YToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3 + 1), 0.0F, 0);
		this.x[3] = IsoUtils.XToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3), 0.0F, 0);
		this.y[3] = IsoUtils.YToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3), 0.0F, 0);
		this.square = square;
		int int1;
		if (!square.getProperties().Is(IsoFlagType.water) && square.getProperties().Is(IsoFlagType.exterior)) {
			for (int1 = 0; int1 < 4; ++int1) {
				this.pdne[int1] = 0.0F;
				this.pdnw[int1] = 0.0F;
				this.pda[int1] = 1.0F;
				this.pnon[int1] = 0.0F;
			}

			if (Core.getInstance().getPerfPuddles() > 1) {
				return this;
			} else {
				IsoCell cell = square.getCell();
				IsoGridSquare square2 = cell.getGridSquare(square.x - 1, square.y, square.z);
				IsoGridSquare square3 = cell.getGridSquare(square.x - 1, square.y - 1, square.z);
				IsoGridSquare square4 = cell.getGridSquare(square.x, square.y - 1, square.z);
				IsoGridSquare square5 = cell.getGridSquare(square.x - 1, square.y + 1, square.z);
				IsoGridSquare square6 = cell.getGridSquare(square.x, square.y + 1, square.z);
				IsoGridSquare square7 = cell.getGridSquare(square.x + 1, square.y + 1, square.z);
				IsoGridSquare square8 = cell.getGridSquare(square.x + 1, square.y, square.z);
				IsoGridSquare square9 = cell.getGridSquare(square.x + 1, square.y - 1, square.z);
				if (square4 != null && square3 != null && square2 != null && square5 != null && square6 != null && square7 != null && square8 != null && square9 != null) {
					this.setFlags(0, square2.getPuddlesDir() | square3.getPuddlesDir() | square4.getPuddlesDir());
					this.setFlags(1, square2.getPuddlesDir() | square5.getPuddlesDir() | square6.getPuddlesDir());
					this.setFlags(2, square6.getPuddlesDir() | square7.getPuddlesDir() | square8.getPuddlesDir());
					this.setFlags(3, square8.getPuddlesDir() | square9.getPuddlesDir() | square4.getPuddlesDir());
					return this;
				} else {
					return this;
				}
			}
		} else {
			for (int1 = 0; int1 < 4; ++int1) {
				this.pdne[int1] = 0.0F;
				this.pdnw[int1] = 0.0F;
				this.pda[int1] = 0.0F;
				this.pnon[int1] = 0.0F;
			}

			return this;
		}
	}

	private void setFlags(int int1, int int2) {
		this.pdne[int1] = 0.0F;
		this.pdnw[int1] = 0.0F;
		this.pda[int1] = 0.0F;
		this.pnon[int1] = 0.0F;
		if ((int2 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NE) != 0) {
			this.pdne[int1] = 1.0F;
		}

		if ((int2 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NW) != 0) {
			this.pdnw[int1] = 1.0F;
		}

		if ((int2 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_ALL) != 0) {
			this.pda[int1] = 1.0F;
		}
	}

	public void recalcIfNeeded() {
		if (this.bRecalc) {
			this.bRecalc = false;
			try {
				this.init(this.square);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		}
	}

	public boolean shouldRender() {
		this.recalcIfNeeded();
		int int1;
		for (int1 = 0; int1 < 4; ++int1) {
			if (this.pdne[int1] + this.pdnw[int1] + this.pda[int1] + this.pnon[int1] > 0.0F) {
				return true;
			}
		}

		if (this.square.getProperties().Is(IsoFlagType.water)) {
			return false;
		} else if (IsoPuddles.leakingPuddlesInTheRoom && !this.interiorCalc && this.square != null) {
			for (int1 = 0; int1 < 4; ++int1) {
				this.pdne[int1] = 0.0F;
				this.pdnw[int1] = 0.0F;
				this.pda[int1] = 0.0F;
				this.pnon[int1] = 1.0F;
			}

			IsoGridSquare square = this.square.getAdjacentSquare(IsoDirections.W);
			IsoGridSquare square2 = this.square.getAdjacentSquare(IsoDirections.NW);
			IsoGridSquare square3 = this.square.getAdjacentSquare(IsoDirections.N);
			IsoGridSquare square4 = this.square.getAdjacentSquare(IsoDirections.SW);
			IsoGridSquare square5 = this.square.getAdjacentSquare(IsoDirections.S);
			IsoGridSquare square6 = this.square.getAdjacentSquare(IsoDirections.SE);
			IsoGridSquare square7 = this.square.getAdjacentSquare(IsoDirections.E);
			IsoGridSquare square8 = this.square.getAdjacentSquare(IsoDirections.NE);
			if (square == null || square3 == null || square5 == null || square7 == null || square2 == null || square8 == null || square4 == null || square6 == null || !square.getProperties().Is(IsoFlagType.exterior) && !square3.getProperties().Is(IsoFlagType.exterior) && !square5.getProperties().Is(IsoFlagType.exterior) && !square7.getProperties().Is(IsoFlagType.exterior)) {
				return false;
			} else {
				int int2;
				if (!this.square.getProperties().Is(IsoFlagType.collideW) && square.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[0] = 0.0F;
					this.pnon[1] = 0.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (!square5.getProperties().Is(IsoFlagType.collideN) && square5.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[1] = 0.0F;
					this.pnon[2] = 0.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (!square7.getProperties().Is(IsoFlagType.collideW) && square7.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[2] = 0.0F;
					this.pnon[3] = 0.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (!this.square.getProperties().Is(IsoFlagType.collideN) && square3.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[3] = 0.0F;
					this.pnon[0] = 0.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square3.getProperties().Is(IsoFlagType.collideW) || !square2.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[0] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square5.getProperties().Is(IsoFlagType.collideW) || !square4.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[1] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square4.getProperties().Is(IsoFlagType.collideN) || !square4.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[1] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square6.getProperties().Is(IsoFlagType.collideN) || !square6.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[2] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square6.getProperties().Is(IsoFlagType.collideW) || !square6.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[2] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square8.getProperties().Is(IsoFlagType.collideW) || !square8.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[3] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square7.getProperties().Is(IsoFlagType.collideN) || !square8.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[3] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				if (square.getProperties().Is(IsoFlagType.collideN) || !square2.getProperties().Is(IsoFlagType.exterior)) {
					this.pnon[0] = 1.0F;
					for (int2 = 0; int2 < 4; ++int2) {
						this.pda[int2] = 1.0F;
					}
				}

				this.interiorCalc = true;
				for (int2 = 0; int2 < 4; ++int2) {
					if (this.pdne[int2] + this.pdnw[int2] + this.pda[int2] + this.pnon[int2] > 0.0F) {
						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public void updateLighting(int int1) {
		this.setLightingAtVert(0, this.square.getVertLight(0, int1));
		this.setLightingAtVert(1, this.square.getVertLight(3, int1));
		this.setLightingAtVert(2, this.square.getVertLight(2, int1));
		this.setLightingAtVert(3, this.square.getVertLight(1, int1));
	}

	private void setLightingAtVert(int int1, int int2) {
		this.color[int1] = int2;
	}
}
