package zombie.iso;

import java.io.PrintStream;
import zombie.debug.LineDrawer;
import zombie.iso.SpriteDetails.IsoFlagType;


public class NearestWalls {
	private static final int CPW = 10;
	private static final int CPWx4 = 40;
	private static final int LEVELS = 8;
	private static int CHANGE_COUNT = 0;
	private static int renderX;
	private static int renderY;
	private static int renderZ;

	public static void chunkLoaded(IsoChunk chunk) {
		++CHANGE_COUNT;
		if (CHANGE_COUNT < 0) {
			CHANGE_COUNT = 0;
		}

		chunk.nearestWalls.changeCount = -1;
	}

	private static void calcDistanceOnThisChunkOnly(IsoChunk chunk) {
		byte[] byteArray = chunk.nearestWalls.distanceSelf;
		for (int int1 = 0; int1 < 8; ++int1) {
			int int2;
			byte byte1;
			int int3;
			int int4;
			IsoGridSquare square;
			int int5;
			for (int2 = 0; int2 < 10; ++int2) {
				byte1 = -1;
				for (int3 = 0; int3 < 10; ++int3) {
					chunk.nearestWalls.closest[int3 + int2 * 10 + int1 * 10 * 10] = -1;
					int4 = int3 * 4 + int2 * 40 + int1 * 10 * 40;
					byteArray[int4 + 0] = byte1 == -1 ? -1 : (byte)(int3 - byte1);
					byteArray[int4 + 1] = -1;
					square = chunk.getGridSquare(int3, int2, int1);
					if (square != null && (square.Is(IsoFlagType.WallW) || square.Is(IsoFlagType.DoorWallW) || square.Is(IsoFlagType.WallNW) || square.Is(IsoFlagType.WindowW))) {
						byte1 = (byte)int3;
						byteArray[int4 + 0] = 0;
						for (int5 = int3 - 1; int5 >= 0; --int5) {
							int4 = int5 * 4 + int2 * 40 + int1 * 10 * 40;
							if (byteArray[int4 + 1] != -1) {
								break;
							}

							byteArray[int4 + 1] = (byte)(byte1 - int5);
						}
					}
				}
			}

			for (int2 = 0; int2 < 10; ++int2) {
				byte1 = -1;
				for (int3 = 0; int3 < 10; ++int3) {
					int4 = int2 * 4 + int3 * 40 + int1 * 10 * 40;
					byteArray[int4 + 2] = byte1 == -1 ? -1 : (byte)(int3 - byte1);
					byteArray[int4 + 3] = -1;
					square = chunk.getGridSquare(int2, int3, int1);
					if (square != null && (square.Is(IsoFlagType.WallN) || square.Is(IsoFlagType.DoorWallN) || square.Is(IsoFlagType.WallNW) || square.Is(IsoFlagType.WindowN))) {
						byte1 = (byte)int3;
						byteArray[int4 + 2] = 0;
						for (int5 = int3 - 1; int5 >= 0; --int5) {
							int4 = int2 * 4 + int5 * 40 + int1 * 10 * 40;
							if (byteArray[int4 + 3] != -1) {
								break;
							}

							byteArray[int4 + 3] = (byte)(byte1 - int5);
						}
					}
				}
			}
		}
	}

	private static int getIndex(IsoChunk chunk, int int1, int int2, int int3) {
		return (int1 - chunk.wx * 10) * 4 + (int2 - chunk.wy * 10) * 40 + int3 * 10 * 40;
	}

	private static int getNearestWallOnSameChunk(IsoChunk chunk, int int1, int int2, int int3, int int4) {
		NearestWalls.ChunkData chunkData = chunk.nearestWalls;
		if (chunkData.changeCount != CHANGE_COUNT) {
			calcDistanceOnThisChunkOnly(chunk);
			chunkData.changeCount = CHANGE_COUNT;
		}

		int int5 = getIndex(chunk, int1, int2, int3);
		return chunkData.distanceSelf[int5 + int4];
	}

	private static boolean hasWall(IsoChunk chunk, int int1, int int2, int int3, int int4) {
		return getNearestWallOnSameChunk(chunk, int1, int2, int3, int4) == 0;
	}

	private static int getNearestWallWest(IsoChunk chunk, int int1, int int2, int int3) {
		byte byte1 = 0;
		byte byte2 = -1;
		byte byte3 = 0;
		int int4 = getNearestWallOnSameChunk(chunk, int1, int2, int3, byte1);
		if (int4 != -1) {
			return int1 - int4;
		} else {
			for (int int5 = 1; int5 <= 3; ++int5) {
				IsoChunk chunk2 = IsoWorld.instance.CurrentCell.getChunk(chunk.wx + int5 * byte2, chunk.wy + int5 * byte3);
				if (chunk2 == null) {
					break;
				}

				int int6 = (chunk2.wx + 1) * 10 - 1;
				int4 = getNearestWallOnSameChunk(chunk2, int6, int2, int3, byte1);
				if (int4 != -1) {
					return int6 - int4;
				}
			}

			return -1;
		}
	}

	private static int getNearestWallEast(IsoChunk chunk, int int1, int int2, int int3) {
		byte byte1 = 1;
		byte byte2 = 1;
		byte byte3 = 0;
		int int4 = getNearestWallOnSameChunk(chunk, int1, int2, int3, byte1);
		if (int4 != -1) {
			return int1 + int4;
		} else {
			for (int int5 = 1; int5 <= 3; ++int5) {
				IsoChunk chunk2 = IsoWorld.instance.CurrentCell.getChunk(chunk.wx + int5 * byte2, chunk.wy + int5 * byte3);
				if (chunk2 == null) {
					break;
				}

				int int6 = chunk2.wx * 10;
				int4 = hasWall(chunk2, int6, int2, int3, 0) ? 0 : getNearestWallOnSameChunk(chunk2, int6, int2, int3, byte1);
				if (int4 != -1) {
					return int6 + int4;
				}
			}

			return -1;
		}
	}

	private static int getNearestWallNorth(IsoChunk chunk, int int1, int int2, int int3) {
		byte byte1 = 2;
		byte byte2 = 0;
		byte byte3 = -1;
		int int4 = getNearestWallOnSameChunk(chunk, int1, int2, int3, byte1);
		if (int4 != -1) {
			return int2 - int4;
		} else {
			for (int int5 = 1; int5 <= 3; ++int5) {
				IsoChunk chunk2 = IsoWorld.instance.CurrentCell.getChunk(chunk.wx + int5 * byte2, chunk.wy + int5 * byte3);
				if (chunk2 == null) {
					break;
				}

				int int6 = (chunk2.wy + 1) * 10 - 1;
				int4 = getNearestWallOnSameChunk(chunk2, int1, int6, int3, byte1);
				if (int4 != -1) {
					return int6 - int4;
				}
			}

			return -1;
		}
	}

	private static int getNearestWallSouth(IsoChunk chunk, int int1, int int2, int int3) {
		byte byte1 = 3;
		byte byte2 = 0;
		byte byte3 = 1;
		int int4 = getNearestWallOnSameChunk(chunk, int1, int2, int3, byte1);
		if (int4 != -1) {
			return int2 + int4;
		} else {
			for (int int5 = 1; int5 <= 3; ++int5) {
				IsoChunk chunk2 = IsoWorld.instance.CurrentCell.getChunk(chunk.wx + int5 * byte2, chunk.wy + int5 * byte3);
				if (chunk2 == null) {
					break;
				}

				int int6 = chunk2.wy * 10;
				int4 = hasWall(chunk2, int1, int6, int3, 2) ? 0 : getNearestWallOnSameChunk(chunk2, int1, int6, int3, byte1);
				if (int4 != -1) {
					return int6 + int4;
				}
			}

			return -1;
		}
	}

	public static void render(int int1, int int2, int int3) {
		IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, int3);
		if (chunk != null) {
			if (renderX != int1 || renderY != int2 || renderZ != int3) {
				renderX = int1;
				renderY = int2;
				renderZ = int3;
				PrintStream printStream = System.out;
				int int4 = ClosestWallDistance(chunk, int1, int2, int3);
				printStream.println("ClosestWallDistance=" + int4);
			}

			int int5 = getNearestWallWest(chunk, int1, int2, int3);
			if (int5 != -1) {
				DrawIsoLine((float)int5, (float)int2 + 0.5F, (float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
				DrawIsoLine((float)int5, (float)int2, (float)int5, (float)(int2 + 1), (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
			}

			int5 = getNearestWallEast(chunk, int1, int2, int3);
			if (int5 != -1) {
				DrawIsoLine((float)int5, (float)int2 + 0.5F, (float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
				DrawIsoLine((float)int5, (float)int2, (float)int5, (float)(int2 + 1), (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
			}

			int int6 = getNearestWallNorth(chunk, int1, int2, int3);
			if (int6 != -1) {
				DrawIsoLine((float)int1 + 0.5F, (float)int6, (float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
				DrawIsoLine((float)int1, (float)int6, (float)(int1 + 1), (float)int6, (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
			}

			int6 = getNearestWallSouth(chunk, int1, int2, int3);
			if (int6 != -1) {
				DrawIsoLine((float)int1 + 0.5F, (float)int6, (float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
				DrawIsoLine((float)int1, (float)int6, (float)(int1 + 1), (float)int6, (float)int3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
			}
		}
	}

	private static void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, int int1) {
		float float10 = IsoUtils.XToScreenExact(float1, float2, float5, 0);
		float float11 = IsoUtils.YToScreenExact(float1, float2, float5, 0);
		float float12 = IsoUtils.XToScreenExact(float3, float4, float5, 0);
		float float13 = IsoUtils.YToScreenExact(float3, float4, float5, 0);
		LineDrawer.drawLine(float10, float11, float12, float13, float6, float7, float8, float9, int1);
	}

	public static int ClosestWallDistance(IsoGridSquare square) {
		return square != null && square.chunk != null ? ClosestWallDistance(square.chunk, square.x, square.y, square.z) : 127;
	}

	public static int ClosestWallDistance(IsoChunk chunk, int int1, int int2, int int3) {
		if (chunk == null) {
			return 127;
		} else {
			NearestWalls.ChunkData chunkData = chunk.nearestWalls;
			byte[] byteArray = chunkData.closest;
			if (chunkData.changeCount != CHANGE_COUNT) {
				calcDistanceOnThisChunkOnly(chunk);
				chunkData.changeCount = CHANGE_COUNT;
			}

			int int4 = int1 - chunk.wx * 10 + (int2 - chunk.wy * 10) * 10 + int3 * 10 * 10;
			byte byte1 = byteArray[int4];
			if (byte1 != -1) {
				return byte1;
			} else {
				int int5 = getNearestWallWest(chunk, int1, int2, int3);
				int int6 = getNearestWallEast(chunk, int1, int2, int3);
				int int7 = getNearestWallNorth(chunk, int1, int2, int3);
				int int8 = getNearestWallSouth(chunk, int1, int2, int3);
				if (int5 == -1 && int6 == -1 && int7 == -1 && int8 == -1) {
					return byteArray[int4] = 127;
				} else {
					int int9 = -1;
					if (int5 != -1 && int6 != -1) {
						int9 = int6 - int5;
					}

					int int10 = -1;
					if (int7 != -1 && int8 != -1) {
						int10 = int8 - int7;
					}

					if (int9 != -1 && int10 != -1) {
						return byteArray[int4] = (byte)Math.min(int9, int10);
					} else if (int9 != -1) {
						return byteArray[int4] = (byte)int9;
					} else if (int10 != -1) {
						return byteArray[int4] = (byte)int10;
					} else {
						IsoGridSquare square = chunk.getGridSquare(int1 - chunk.wx * 10, int2 - chunk.wy * 10, int3);
						if (square != null && square.isOutside()) {
							int5 = int5 == -1 ? 127 : int1 - int5;
							int6 = int6 == -1 ? 127 : int6 - int1 - 1;
							int7 = int7 == -1 ? 127 : int2 - int7;
							int8 = int8 == -1 ? 127 : int8 - int2 - 1;
							return byteArray[int4] = (byte)Math.min(int5, Math.min(int6, Math.min(int7, int8)));
						} else {
							return byteArray[int4] = 127;
						}
					}
				}
			}
		}
	}

	public static final class ChunkData {
		int changeCount = -1;
		final byte[] distanceSelf = new byte[3200];
		final byte[] closest = new byte[800];
	}
}
