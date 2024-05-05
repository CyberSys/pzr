package zombie.iso;

import zombie.characters.IsoGameCharacter;


public final class LosUtil {
	public static int XSIZE = 200;
	public static int YSIZE = 200;
	public static int ZSIZE = 16;
	public static byte[][][][] cachedresults;
	public static boolean[] cachecleared;

	public static void init(int int1, int int2) {
		XSIZE = Math.min(int1, 200);
		YSIZE = Math.min(int2, 200);
		cachedresults = new byte[XSIZE][YSIZE][ZSIZE][4];
	}

	public static LosUtil.TestResults lineClear(IsoCell cell, int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1) {
		return lineClear(cell, int1, int2, int3, int4, int5, int6, boolean1, 10000);
	}

	public static LosUtil.TestResults lineClear(IsoCell cell, int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1, int int7) {
		if (int6 == int3 - 1) {
			IsoGridSquare square = cell.getGridSquare(int4, int5, int6);
			if (square != null && square.HasElevatedFloor()) {
				int6 = int3;
			}
		}

		LosUtil.TestResults testResults = LosUtil.TestResults.Clear;
		int int8 = int5 - int2;
		int int9 = int4 - int1;
		int int10 = int6 - int3;
		float float1 = 0.5F;
		float float2 = 0.5F;
		IsoGridSquare square2 = cell.getGridSquare(int1, int2, int3);
		int int11 = 0;
		boolean boolean2 = false;
		int int12;
		int int13;
		float float3;
		float float4;
		IsoGridSquare square3;
		LosUtil.TestResults testResults2;
		if (Math.abs(int9) > Math.abs(int8) && Math.abs(int9) > Math.abs(int10)) {
			float3 = (float)int8 / (float)int9;
			float4 = (float)int10 / (float)int9;
			float1 += (float)int2;
			float2 += (float)int3;
			int9 = int9 < 0 ? -1 : 1;
			float3 *= (float)int9;
			for (float4 *= (float)int9; int1 != int4; boolean2 = false) {
				int1 += int9;
				float1 += float3;
				float2 += float4;
				square3 = cell.getGridSquare(int1, (int)float1, (int)float2);
				if (square3 != null && square2 != null) {
					testResults2 = square3.testVisionAdjacent(square2.getX() - square3.getX(), square2.getY() - square3.getY(), square2.getZ() - square3.getZ(), true, boolean1);
					if (testResults2 == LosUtil.TestResults.ClearThroughWindow) {
						boolean2 = true;
					}

					if (testResults2 == LosUtil.TestResults.Blocked || testResults == LosUtil.TestResults.Clear || testResults2 == LosUtil.TestResults.ClearThroughWindow && testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
						testResults = testResults2;
					} else if (testResults2 == LosUtil.TestResults.ClearThroughClosedDoor && testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
						testResults = testResults2;
					}

					if (testResults == LosUtil.TestResults.Blocked) {
						return LosUtil.TestResults.Blocked;
					}

					if (boolean2) {
						if (int11 > int7) {
							return LosUtil.TestResults.Blocked;
						}

						int11 = 0;
					}
				}

				square2 = square3;
				int12 = (int)float1;
				int13 = (int)float2;
				++int11;
			}
		} else {
			int int14;
			if (Math.abs(int8) >= Math.abs(int9) && Math.abs(int8) > Math.abs(int10)) {
				float3 = (float)int9 / (float)int8;
				float4 = (float)int10 / (float)int8;
				float1 += (float)int1;
				float2 += (float)int3;
				int8 = int8 < 0 ? -1 : 1;
				float3 *= (float)int8;
				for (float4 *= (float)int8; int2 != int5; boolean2 = false) {
					int2 += int8;
					float1 += float3;
					float2 += float4;
					square3 = cell.getGridSquare((int)float1, int2, (int)float2);
					if (square3 != null && square2 != null) {
						testResults2 = square3.testVisionAdjacent(square2.getX() - square3.getX(), square2.getY() - square3.getY(), square2.getZ() - square3.getZ(), true, boolean1);
						if (testResults2 == LosUtil.TestResults.ClearThroughWindow) {
							boolean2 = true;
						}

						if (testResults2 == LosUtil.TestResults.Blocked || testResults == LosUtil.TestResults.Clear || testResults2 == LosUtil.TestResults.ClearThroughWindow && testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
							testResults = testResults2;
						} else if (testResults2 == LosUtil.TestResults.ClearThroughClosedDoor && testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
							testResults = testResults2;
						}

						if (testResults == LosUtil.TestResults.Blocked) {
							return LosUtil.TestResults.Blocked;
						}

						if (boolean2) {
							if (int11 > int7) {
								return LosUtil.TestResults.Blocked;
							}

							int11 = 0;
						}
					}

					square2 = square3;
					int14 = (int)float1;
					int13 = (int)float2;
					++int11;
				}
			} else {
				float3 = (float)int9 / (float)int10;
				float4 = (float)int8 / (float)int10;
				float1 += (float)int1;
				float2 += (float)int2;
				int10 = int10 < 0 ? -1 : 1;
				float3 *= (float)int10;
				for (float4 *= (float)int10; int3 != int6; boolean2 = false) {
					int3 += int10;
					float1 += float3;
					float2 += float4;
					square3 = cell.getGridSquare((int)float1, (int)float2, int3);
					if (square3 != null && square2 != null) {
						testResults2 = square3.testVisionAdjacent(square2.getX() - square3.getX(), square2.getY() - square3.getY(), square2.getZ() - square3.getZ(), true, boolean1);
						if (testResults2 == LosUtil.TestResults.ClearThroughWindow) {
							boolean2 = true;
						}

						if (testResults2 != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.Clear && (testResults2 != LosUtil.TestResults.ClearThroughWindow || testResults != LosUtil.TestResults.ClearThroughOpenDoor)) {
							if (testResults2 == LosUtil.TestResults.ClearThroughClosedDoor && testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
								testResults = testResults2;
							}
						} else {
							testResults = testResults2;
						}

						if (testResults == LosUtil.TestResults.Blocked) {
							return LosUtil.TestResults.Blocked;
						}

						if (boolean2) {
							if (int11 > int7) {
								return LosUtil.TestResults.Blocked;
							}

							int11 = 0;
						}
					}

					square2 = square3;
					int14 = (int)float1;
					int12 = (int)float2;
					++int11;
				}
			}
		}

		return testResults;
	}

	public static boolean lineClearCollide(int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		int int7 = int2 - int5;
		int int8 = int1 - int4;
		int int9 = int3 - int6;
		float float1 = 0.5F;
		float float2 = 0.5F;
		IsoGridSquare square = cell.getGridSquare(int4, int5, int6);
		int int10;
		int int11;
		float float3;
		float float4;
		IsoGridSquare square2;
		boolean boolean2;
		if (Math.abs(int8) > Math.abs(int7) && Math.abs(int8) > Math.abs(int9)) {
			float3 = (float)int7 / (float)int8;
			float4 = (float)int9 / (float)int8;
			float1 += (float)int5;
			float2 += (float)int6;
			int8 = int8 < 0 ? -1 : 1;
			float3 *= (float)int8;
			for (float4 *= (float)int8; int4 != int1; int11 = (int)float2) {
				int4 += int8;
				float1 += float3;
				float2 += float4;
				square2 = cell.getGridSquare(int4, (int)float1, (int)float2);
				if (square2 != null && square != null) {
					boolean2 = square2.CalculateCollide(square, false, false, true, true);
					if (!boolean1 && square2.isDoorBlockedTo(square)) {
						boolean2 = true;
					}

					if (boolean2) {
						return true;
					}
				}

				square = square2;
				int10 = (int)float1;
			}
		} else {
			int int12;
			if (Math.abs(int7) >= Math.abs(int8) && Math.abs(int7) > Math.abs(int9)) {
				float3 = (float)int8 / (float)int7;
				float4 = (float)int9 / (float)int7;
				float1 += (float)int4;
				float2 += (float)int6;
				int7 = int7 < 0 ? -1 : 1;
				float3 *= (float)int7;
				for (float4 *= (float)int7; int5 != int2; int11 = (int)float2) {
					int5 += int7;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare((int)float1, int5, (int)float2);
					if (square2 != null && square != null) {
						boolean2 = square2.CalculateCollide(square, false, false, true, true);
						if (!boolean1 && square2.isDoorBlockedTo(square)) {
							boolean2 = true;
						}

						if (boolean2) {
							return true;
						}
					}

					square = square2;
					int12 = (int)float1;
				}
			} else {
				float3 = (float)int8 / (float)int9;
				float4 = (float)int7 / (float)int9;
				float1 += (float)int4;
				float2 += (float)int5;
				int9 = int9 < 0 ? -1 : 1;
				float3 *= (float)int9;
				for (float4 *= (float)int9; int6 != int3; int10 = (int)float2) {
					int6 += int9;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare((int)float1, (int)float2, int6);
					if (square2 != null && square != null) {
						boolean2 = square2.CalculateCollide(square, false, false, true, true);
						if (boolean2) {
							return true;
						}
					}

					square = square2;
					int12 = (int)float1;
				}
			}
		}

		return false;
	}

	public static int lineClearCollideCount(IsoGameCharacter gameCharacter, IsoCell cell, int int1, int int2, int int3, int int4, int int5, int int6) {
		int int7 = 0;
		int int8 = int2 - int5;
		int int9 = int1 - int4;
		int int10 = int3 - int6;
		float float1 = 0.5F;
		float float2 = 0.5F;
		IsoGridSquare square = cell.getGridSquare(int4, int5, int6);
		int int11;
		int int12;
		float float3;
		float float4;
		IsoGridSquare square2;
		boolean boolean1;
		if (Math.abs(int9) > Math.abs(int8) && Math.abs(int9) > Math.abs(int10)) {
			float3 = (float)int8 / (float)int9;
			float4 = (float)int10 / (float)int9;
			float1 += (float)int5;
			float2 += (float)int6;
			int9 = int9 < 0 ? -1 : 1;
			float3 *= (float)int9;
			for (float4 *= (float)int9; int4 != int1; int12 = (int)float2) {
				int4 += int9;
				float1 += float3;
				float2 += float4;
				square2 = cell.getGridSquare(int4, (int)float1, (int)float2);
				if (square2 != null && square != null) {
					boolean1 = square.testCollideAdjacent(gameCharacter, square2.getX() - square.getX(), square2.getY() - square.getY(), square2.getZ() - square.getZ());
					if (boolean1) {
						return int7;
					}
				}

				++int7;
				square = square2;
				int11 = (int)float1;
			}
		} else {
			int int13;
			if (Math.abs(int8) >= Math.abs(int9) && Math.abs(int8) > Math.abs(int10)) {
				float3 = (float)int9 / (float)int8;
				float4 = (float)int10 / (float)int8;
				float1 += (float)int4;
				float2 += (float)int6;
				int8 = int8 < 0 ? -1 : 1;
				float3 *= (float)int8;
				for (float4 *= (float)int8; int5 != int2; int12 = (int)float2) {
					int5 += int8;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare((int)float1, int5, (int)float2);
					if (square2 != null && square != null) {
						boolean1 = square.testCollideAdjacent(gameCharacter, square2.getX() - square.getX(), square2.getY() - square.getY(), square2.getZ() - square.getZ());
						if (boolean1) {
							return int7;
						}
					}

					++int7;
					square = square2;
					int13 = (int)float1;
				}
			} else {
				float3 = (float)int9 / (float)int10;
				float4 = (float)int8 / (float)int10;
				float1 += (float)int4;
				float2 += (float)int5;
				int10 = int10 < 0 ? -1 : 1;
				float3 *= (float)int10;
				for (float4 *= (float)int10; int6 != int3; int11 = (int)float2) {
					int6 += int10;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare((int)float1, (int)float2, int6);
					if (square2 != null && square != null) {
						boolean1 = square.testCollideAdjacent(gameCharacter, square2.getX() - square.getX(), square2.getY() - square.getY(), square2.getZ() - square.getZ());
						if (boolean1) {
							return int7;
						}
					}

					++int7;
					square = square2;
					int13 = (int)float1;
				}
			}
		}

		return int7;
	}

	public static LosUtil.TestResults lineClearCached(IsoCell cell, int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1, int int7) {
		if (int3 == int6 - 1) {
			IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
			if (square != null && square.HasElevatedFloor()) {
				int3 = int6;
			}
		}

		int int8 = int4;
		int int9 = int5;
		int int10 = int6;
		int int11 = int2 - int5;
		int int12 = int1 - int4;
		int int13 = int3 - int6;
		int int14 = int12 + XSIZE / 2;
		int int15 = int11 + YSIZE / 2;
		int int16 = int13 + ZSIZE / 2;
		if (int14 >= 0 && int15 >= 0 && int16 >= 0 && int14 < XSIZE && int15 < YSIZE && int16 < ZSIZE) {
			LosUtil.TestResults testResults = LosUtil.TestResults.Clear;
			byte byte1 = 1;
			if (cachedresults[int14][int15][int16][int7] != 0) {
				if (cachedresults[int14][int15][int16][int7] == 1) {
					testResults = LosUtil.TestResults.Clear;
				}

				if (cachedresults[int14][int15][int16][int7] == 2) {
					testResults = LosUtil.TestResults.ClearThroughOpenDoor;
				}

				if (cachedresults[int14][int15][int16][int7] == 3) {
					testResults = LosUtil.TestResults.ClearThroughWindow;
				}

				if (cachedresults[int14][int15][int16][int7] == 4) {
					testResults = LosUtil.TestResults.Blocked;
				}

				if (cachedresults[int14][int15][int16][int7] == 5) {
					testResults = LosUtil.TestResults.ClearThroughClosedDoor;
				}

				return testResults;
			} else {
				float float1 = 0.5F;
				float float2 = 0.5F;
				IsoGridSquare square2 = cell.getGridSquare(int4, int5, int6);
				int int17;
				int int18;
				float float3;
				float float4;
				IsoGridSquare square3;
				int int19;
				int int20;
				int int21;
				if (Math.abs(int12) > Math.abs(int11) && Math.abs(int12) > Math.abs(int13)) {
					float3 = (float)int11 / (float)int12;
					float4 = (float)int13 / (float)int12;
					float1 += (float)int5;
					float2 += (float)int6;
					int12 = int12 < 0 ? -1 : 1;
					float3 *= (float)int12;
					for (float4 *= (float)int12; int4 != int1; int18 = (int)float2) {
						int4 += int12;
						float1 += float3;
						float2 += float4;
						square3 = cell.getGridSquare(int4, (int)float1, (int)float2);
						if (square3 != null && square2 != null) {
							if (byte1 != 4 && square3.testVisionAdjacent(square2.getX() - square3.getX(), square2.getY() - square3.getY(), square2.getZ() - square3.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
								byte1 = 4;
							}

							int19 = int4 - int8;
							int20 = (int)float1 - int9;
							int21 = (int)float2 - int10;
							int19 += XSIZE / 2;
							int20 += YSIZE / 2;
							int21 += ZSIZE / 2;
							if (cachedresults[int19][int20][int21][int7] == 0) {
								cachedresults[int19][int20][int21][int7] = (byte)byte1;
							}
						} else {
							int19 = int4 - int8;
							int20 = (int)float1 - int9;
							int21 = (int)float2 - int10;
							int19 += XSIZE / 2;
							int20 += YSIZE / 2;
							int21 += ZSIZE / 2;
							if (cachedresults[int19][int20][int21][int7] == 0) {
								cachedresults[int19][int20][int21][int7] = (byte)byte1;
							}
						}

						square2 = square3;
						int17 = (int)float1;
					}
				} else {
					int int22;
					if (Math.abs(int11) >= Math.abs(int12) && Math.abs(int11) > Math.abs(int13)) {
						float3 = (float)int12 / (float)int11;
						float4 = (float)int13 / (float)int11;
						float1 += (float)int4;
						float2 += (float)int6;
						int11 = int11 < 0 ? -1 : 1;
						float3 *= (float)int11;
						for (float4 *= (float)int11; int5 != int2; int18 = (int)float2) {
							int5 += int11;
							float1 += float3;
							float2 += float4;
							square3 = cell.getGridSquare((int)float1, int5, (int)float2);
							if (square3 != null && square2 != null) {
								if (byte1 != 4 && square3.testVisionAdjacent(square2.getX() - square3.getX(), square2.getY() - square3.getY(), square2.getZ() - square3.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
									byte1 = 4;
								}

								int19 = (int)float1 - int8;
								int20 = int5 - int9;
								int21 = (int)float2 - int10;
								int19 += XSIZE / 2;
								int20 += YSIZE / 2;
								int21 += ZSIZE / 2;
								if (cachedresults[int19][int20][int21][int7] == 0) {
									cachedresults[int19][int20][int21][int7] = (byte)byte1;
								}
							} else {
								int19 = (int)float1 - int8;
								int20 = int5 - int9;
								int21 = (int)float2 - int10;
								int19 += XSIZE / 2;
								int20 += YSIZE / 2;
								int21 += ZSIZE / 2;
								if (cachedresults[int19][int20][int21][int7] == 0) {
									cachedresults[int19][int20][int21][int7] = (byte)byte1;
								}
							}

							square2 = square3;
							int22 = (int)float1;
						}
					} else {
						float3 = (float)int12 / (float)int13;
						float4 = (float)int11 / (float)int13;
						float1 += (float)int4;
						float2 += (float)int5;
						int13 = int13 < 0 ? -1 : 1;
						float3 *= (float)int13;
						for (float4 *= (float)int13; int6 != int3; int17 = (int)float2) {
							int6 += int13;
							float1 += float3;
							float2 += float4;
							square3 = cell.getGridSquare((int)float1, (int)float2, int6);
							if (square3 != null && square2 != null) {
								if (byte1 != 4 && square3.testVisionAdjacent(square2.getX() - square3.getX(), square2.getY() - square3.getY(), square2.getZ() - square3.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
									byte1 = 4;
								}

								int19 = (int)float1 - int8;
								int20 = (int)float2 - int9;
								int21 = int6 - int10;
								int19 += XSIZE / 2;
								int20 += YSIZE / 2;
								int21 += ZSIZE / 2;
								if (cachedresults[int19][int20][int21][int7] == 0) {
									cachedresults[int19][int20][int21][int7] = (byte)byte1;
								}
							} else {
								int19 = (int)float1 - int8;
								int20 = (int)float2 - int9;
								int21 = int6 - int10;
								int19 += XSIZE / 2;
								int20 += YSIZE / 2;
								int21 += ZSIZE / 2;
								if (cachedresults[int19][int20][int21][int7] == 0) {
									cachedresults[int19][int20][int21][int7] = (byte)byte1;
								}
							}

							square2 = square3;
							int22 = (int)float1;
						}
					}
				}

				if (byte1 == 1) {
					cachedresults[int14][int15][int16][int7] = (byte)byte1;
					return LosUtil.TestResults.Clear;
				} else if (byte1 == 2) {
					cachedresults[int14][int15][int16][int7] = (byte)byte1;
					return LosUtil.TestResults.ClearThroughOpenDoor;
				} else if (byte1 == 3) {
					cachedresults[int14][int15][int16][int7] = (byte)byte1;
					return LosUtil.TestResults.ClearThroughWindow;
				} else if (byte1 == 4) {
					cachedresults[int14][int15][int16][int7] = (byte)byte1;
					return LosUtil.TestResults.Blocked;
				} else if (byte1 == 5) {
					cachedresults[int14][int15][int16][int7] = (byte)byte1;
					return LosUtil.TestResults.ClearThroughClosedDoor;
				} else {
					return LosUtil.TestResults.Blocked;
				}
			}
		} else {
			return LosUtil.TestResults.Blocked;
		}
	}

	static  {
		cachedresults = new byte[XSIZE][YSIZE][ZSIZE][4];
		cachecleared = new boolean[4];
	for (int var0 = 0; var0 < 4; ++var0) {
		cachecleared[var0] = true;
	}
	}

	public static enum TestResults {

		Clear,
		ClearThroughOpenDoor,
		ClearThroughWindow,
		Blocked,
		ClearThroughClosedDoor;

		private static LosUtil.TestResults[] $values() {
			return new LosUtil.TestResults[]{Clear, ClearThroughOpenDoor, ClearThroughWindow, Blocked, ClearThroughClosedDoor};
		}
	}
}
