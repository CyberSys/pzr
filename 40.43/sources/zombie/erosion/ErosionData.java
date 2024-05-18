package zombie.erosion;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.erosion.categories.ErosionCategory;
import zombie.iso.IsoChunk;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class ErosionData {

	public static class Chunk {
		public boolean init = false;
		public int eTickStamp = -1;
		public int epoch = -1;
		public int x;
		public int y;
		public float moisture;
		public float minerals;
		public int soil;

		public void set(IsoChunk chunk) {
			this.x = chunk.wx;
			this.y = chunk.wy;
		}

		public void save(ByteBuffer byteBuffer) {
			if (this.init) {
				byteBuffer.put((byte)1);
				byteBuffer.putInt(this.eTickStamp);
				byteBuffer.putInt(this.epoch);
				byteBuffer.putFloat(this.moisture);
				byteBuffer.putFloat(this.minerals);
				byteBuffer.put((byte)this.soil);
			} else {
				byteBuffer.put((byte)0);
			}
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			this.init = byteBuffer.get() == 1;
			if (this.init) {
				this.eTickStamp = byteBuffer.getInt();
				this.epoch = byteBuffer.getInt();
				this.moisture = byteBuffer.getFloat();
				this.minerals = byteBuffer.getFloat();
				this.soil = byteBuffer.get();
			}
		}
	}

	public static class Square {
		public boolean init = false;
		public boolean doNothing = false;
		public float noiseMain;
		public int noiseMainInt;
		public float noiseKudzu;
		public int soil;
		public float magicNum;
		public ArrayList regions = new ArrayList();
		private static final int[] rands = new int[]{33, 83, 22, 83, 8, 18, 53, 9, 95, 8, 73, 92, 12, 89, 5, 94, 92, 22, 87, 42, 99, 8, 94, 32, 28, 94, 56, 66, 43, 9, 38, 72, 36, 66, 40, 36, 94, 44, 45, 16, 92, 63, 8, 48, 40, 40, 67, 59, 70, 38, 30, 13, 74, 31, 36, 77, 55, 63, 10, 69, 94, 61, 76, 77, 14, 74, 0, 39, 33, 92, 45, 32, 18, 20, 92, 77, 17, 84, 2, 56, 5, 2, 81, 65, 55, 76, 42, 8, 28, 29, 93, 31, 34, 6, 69, 29, 89, 96, 58, 83, 4, 76, 87, 34, 48, 17, 20, 31, 62, 42, 78, 42, 74, 65, 20, 56, 33, 43, 10, 94, 21, 72, 7, 74, 69, 19, 71, 15, 49, 14, 13, 70, 67, 22, 37, 93, 56, 15, 12, 14, 41, 14, 45, 82, 5, 83, 37, 22, 76, 0, 54, 83, 74, 95, 88, 67, 48, 20, 58, 71, 58, 9, 67, 45, 7, 64, 70, 68, 3, 7, 82, 68, 51, 3, 22, 70, 50, 83, 7, 92, 14, 52, 56, 69, 48, 71, 12, 95, 28, 78, 63, 37, 80, 85, 20, 9, 23, 79, 73, 77, 86, 38, 84, 75, 1, 48, 43, 45, 6, 4, 77, 72, 35, 47, 96, 6, 12, 30, 98, 23, 95, 13, 6, 64, 91, 25, 16, 61, 52, 59, 68, 0, 88, 18, 12, 67, 31, 71, 72, 33, 27, 2, 5, 57, 50, 59, 96, 16, 27, 29, 74, 82, 99, 47, 19, 61, 0, 36, 62, 67, 71, 16, 13, 21, 69, 81, 19, 24, 88, 21, 52, 70, 90, 52, 93, 57, 54, 42, 83, 39, 22, 22, 46, 35, 56, 69, 52, 35, 80, 6, 3, 31, 43, 84, 12, 80, 96, 99, 14, 86, 19, 15, 32, 12, 87, 5, 19, 64, 7, 75, 91, 19, 32, 9, 82, 3, 68, 26, 25, 11, 87, 39, 59, 0, 21, 71, 50, 23, 38, 56, 6, 29, 32, 5, 28, 84, 39, 56, 35, 35, 17, 65, 22, 65, 63, 47, 77, 52, 15, 34, 20, 82, 12, 72, 26, 97, 23, 4, 96, 83, 86, 79, 69, 66, 16, 58, 97, 28, 63, 71, 67, 78, 66, 6, 53, 29, 39, 97, 9, 6, 13, 48, 84, 28, 32, 28, 0, 58, 56, 83, 3, 26, 52, 55, 29, 74, 70, 45, 2, 3, 24, 35, 4, 40, 69, 95, 55, 65, 53, 4, 46, 90, 86, 80, 21, 6, 59, 92, 10, 33, 69, 94, 82, 17, 61, 21, 14, 9, 72, 18, 58, 10, 76, 7, 38, 65, 33, 5, 47, 93, 9, 30, 18, 31, 55, 15, 30, 96, 22, 5, 38, 71, 48, 49, 44, 46, 40, 66, 62, 11, 22, 91, 76, 45, 10, 25, 46, 44, 81, 95, 33, 53, 50, 18, 83, 78, 53, 68, 60, 89, 32, 25, 18, 4, 19, 26, 1, 92, 56, 76, 48, 48, 20, 49, 48, 33, 66, 79, 27, 70, 21, 61, 56, 33, 91, 22, 64, 68, 80, 14, 5, 35, 11, 82, 85, 47, 1, 54, 96, 78, 70, 92, 15, 15, 30, 77, 26, 84, 33, 25, 31, 59, 88, 33, 72, 66, 95, 86, 35, 75, 82, 17, 98, 73, 15, 99, 69, 42, 57, 85, 23, 97, 75, 68, 23, 90, 91, 35, 88, 99, 76, 29, 62, 10, 8, 94, 38, 16, 31, 45, 74, 28, 42, 82, 37, 5, 98, 41, 49, 50, 3, 16, 45, 0, 61, 83, 79, 89, 48, 22, 80, 47, 4, 72, 69, 92, 5, 99, 57, 0, 30, 40, 16, 84, 90, 81, 29, 94, 55, 53, 88, 10, 71, 75, 58, 37, 15, 32, 76, 91, 0, 48, 76, 83, 17, 31, 27, 47, 94, 11, 83, 82, 39, 86, 7, 30, 16, 58, 50, 62, 63, 11, 50, 87, 62, 1, 3, 11, 74, 96, 63, 18, 60, 40, 98, 79, 67, 58, 24, 13, 44, 46, 33, 96, 95, 74, 63, 3, 70, 69, 93, 61, 82, 5, 91, 90, 52, 56, 96, 83, 25, 92, 51, 62, 91, 52, 32, 95, 41, 71, 85, 32, 73, 20, 21, 14, 70, 9, 98, 82, 56, 29, 2, 98, 38, 73, 9, 59, 22, 63, 84, 49, 18, 78, 9, 33, 2, 51, 15, 66, 67, 69, 59, 60, 12, 52, 85, 63, 27, 32, 54, 53, 37, 92, 55, 66, 14, 61, 51, 49, 41, 16, 3, 63, 41, 97, 93, 34, 60, 10, 24, 14, 77, 55, 29, 11, 27, 58, 22, 69, 20, 10, 98, 55, 65, 58, 36, 69, 9, 88, 84, 87, 41, 60, 88, 66, 31, 46, 25, 49, 6, 11, 9, 75, 94, 94, 80, 95, 4, 27, 47, 35, 43, 94, 82, 42, 53, 93, 20, 20, 94, 95, 53, 2, 28, 92, 41, 0, 97, 16, 83, 3, 18, 15, 97, 77, 23, 79, 95, 86, 28, 39, 41, 91, 1, 98, 58, 60, 34, 25, 39, 75, 56, 41, 1, 27, 69, 51, 9, 26, 52, 60, 82, 2, 84, 94, 47, 26, 0, 27, 68, 49, 79, 49, 97, 37, 67, 69, 21, 57, 57, 20, 78, 92, 55, 15, 96, 47, 65, 94, 88, 66, 5, 16, 94, 11, 0, 92, 38, 20, 80, 59, 81, 53, 85, 59, 12, 81, 74, 13, 76, 58, 79, 79, 61, 4, 48, 45, 61, 36, 77, 79, 54, 60, 6, 84, 2, 19, 3, 70, 68, 9, 54, 51, 96, 63, 75, 91, 35, 45, 5, 8, 4, 99, 78, 54, 40, 61, 68, 38, 18, 0, 71, 36, 32, 29, 67, 30, 14, 92, 4, 31, 90, 58, 8, 67, 85, 43, 2, 64, 47, 66, 12, 8, 68, 13, 22, 37, 32, 58, 56, 78, 15, 87, 42, 45, 11, 91, 6, 89, 81, 55, 19, 76, 74, 21, 80, 63, 73, 46, 74, 17, 14, 20, 49, 74, 70, 23, 62, 26, 92, 81, 97, 38, 54, 49, 85, 5, 59, 79, 78, 78, 0, 3, 72, 83, 84, 42, 91, 8, 28, 62, 17, 98, 31, 1, 5, 55, 4, 36, 89, 77, 52, 9, 94, 93, 15, 42, 8, 22, 31, 66, 24, 69, 18, 12, 46, 7, 95, 26, 66, 63, 40, 7, 36, 52, 70, 33, 98, 0, 90, 19, 69, 30, 44, 36, 83, 42, 42, 37, 78, 96, 95, 74, 53, 87, 16, 83, 13, 62, 47, 98, 70, 76, 27, 58, 53, 45, 83, 74, 40, 20, 10, 71, 84, 11, 61, 88, 37, 17, 1, 20, 31, 49, 87, 93, 40, 84, 12, 33, 47, 57, 44, 6, 95, 61, 77, 18, 6, 19, 97, 34, 91, 61, 71, 63, 59, 10, 74, 70, 18, 24, 27, 16, 52, 12, 45, 29, 90, 3, 38, 64, 77, 80, 19, 73, 5, 55, 95, 29, 20, 97, 2, 5, 7, 35, 59, 99, 19, 50, 37, 7, 82, 42, 90, 92, 39, 27, 33, 76, 17, 44, 22, 8, 34, 1, 81, 4, 37, 8, 69, 18, 71, 17, 97, 39, 28, 91, 62, 36, 62, 67, 62, 54, 90, 42, 63, 11, 14, 83, 13, 24, 69, 88, 66, 50, 73, 24, 15, 48, 70, 38, 39, 46, 64, 39, 33, 60, 5, 52, 52, 61, 21, 49, 18, 27, 3, 87, 4, 65, 48, 84, 81, 61, 41, 99, 71, 19, 64, 52, 51, 50, 31, 32, 84, 53, 41, 76, 85, 46, 62, 51, 70, 26, 22, 93, 95, 14, 66, 25, 68, 17, 27, 64, 20, 38, 21, 32, 36, 17, 7, 19, 18, 38, 44, 12, 24, 5, 32, 18, 30, 33, 95, 30, 20, 79, 46, 36, 24, 8, 79, 21, 4, 11, 33, 83, 48, 1, 59, 44, 3, 83, 12, 74, 31, 74, 18, 3, 14, 87, 87, 65, 66, 8, 10, 61, 80, 8, 52, 34, 86, 44, 82, 90, 39, 63, 86, 86, 12, 16, 14, 19, 47, 43, 29, 92, 9, 1, 2, 91, 6, 71, 56, 34, 58, 70, 69, 25, 31, 6, 45, 35, 80, 47, 64, 50, 16, 91, 31, 6, 6, 43, 89, 32, 34, 57, 48, 16, 27, 84, 35, 66, 41, 68, 11, 16, 98, 4, 20, 24, 92, 89, 80, 32, 39, 68, 58, 56, 65, 79, 2, 2, 48, 65, 91, 27, 50, 38, 94, 57, 58, 39, 73, 48, 83, 73, 15, 76, 48, 38, 80, 23, 1, 69, 76, 57, 64, 1, 71, 99, 54, 70, 80, 57, 24, 85, 87, 45, 17, 62, 57, 89, 14, 55, 50, 29, 43, 93, 82, 54, 47, 20, 54, 2, 80, 97, 59, 78, 13, 15, 95, 85, 9, 62, 49, 75, 1, 41, 55, 0, 8, 9, 44, 40, 75, 5, 5, 3, 44, 76, 10, 96, 63, 47, 32, 44, 68, 72, 72, 58, 47, 49, 41, 93, 71, 67, 70, 72, 32, 29, 88, 66, 38, 84, 94, 47, 27, 32, 69, 35, 8, 66, 92, 9, 81, 44, 55, 58, 42, 32, 89, 79, 96, 51, 26, 93, 42, 9, 8, 25, 95, 36, 90, 64, 43, 54, 44, 93, 3, 60, 72, 5, 16, 18, 58, 79, 35, 13, 12, 71, 29, 43, 96, 77, 75, 57, 95, 16, 92, 8, 25, 80, 95, 87, 80, 28, 66, 99, 67, 37, 82, 56, 32, 26, 17, 15, 42, 72, 32, 20, 88, 54, 79, 10, 34, 60, 20, 72, 5, 25, 4, 61, 55, 28, 83, 41, 68, 52, 99, 20, 50, 60, 57, 2, 21, 60, 82, 12, 63, 12, 69, 52, 46, 29, 35, 53, 56, 1, 23, 21, 65, 31, 47, 99, 26, 5, 82, 90, 50, 48, 32, 31, 89, 8, 78, 52, 84, 46, 40, 39, 97, 68, 5, 82, 28, 18, 39, 50, 0, 20, 34, 51, 69, 27, 23, 55, 33, 83, 99, 30, 0, 76, 7, 26, 19, 3, 79, 73, 85, 85, 36, 50, 66, 88, 50, 2, 39, 52, 82, 39, 88, 73, 27, 24, 47, 7, 58, 97, 43, 57, 76, 73, 60, 25, 16, 97, 23, 95, 40, 89, 19, 37, 26, 26, 14, 15, 92, 42, 60, 66, 22, 97, 37, 63, 84, 26, 80, 2, 41, 2, 19, 40, 6, 97, 7, 30, 7, 92, 78, 14, 36, 65, 65, 27, 35, 92, 67, 41, 42, 94, 5, 63, 43, 53, 6, 5, 90, 81, 13, 37, 26, 17, 32, 24, 55, 77, 78, 84, 53, 41, 75, 12, 26, 99, 19, 64, 7, 50, 80, 94, 7, 56, 14, 79, 96, 61, 71, 89, 38, 51, 3, 38, 3, 77, 28, 66, 37, 58, 60, 32, 48, 75, 65, 76, 61, 97, 60, 16, 23, 90, 60, 23, 3, 82, 86, 58, 8, 6, 22, 55, 61, 14, 33, 96, 42, 20, 42, 17, 27, 28, 77, 13, 12, 49, 17, 25, 8, 39, 10, 93, 21, 15, 4, 98, 91, 48, 6, 51, 92, 57, 70, 10, 67, 92, 5, 49, 42, 36, 66, 72, 47, 70, 8, 44, 48, 62, 64, 58, 97, 91, 99, 29, 16, 30, 10, 60, 0, 64, 8, 72, 83, 21, 28, 55, 82, 88, 77, 17, 86, 83, 54, 53, 44, 87, 56, 2, 91, 75, 67, 82, 35, 87, 42, 39, 28, 42, 88, 78, 34, 96, 83, 55, 62, 99, 46, 75, 69, 86, 39, 29, 14, 3, 27, 68, 87, 9, 34, 32, 35, 7, 38, 84, 35, 6, 67, 63, 81, 32, 29, 87, 39, 90, 33, 42, 15, 49, 85, 20, 84, 25, 70, 79, 26, 57, 83, 59, 92, 93, 23, 51, 6, 53, 11, 87, 66, 63, 1, 33, 79, 54, 8, 23, 60, 57, 21, 81, 83, 29, 57, 86, 5, 39, 74, 55, 99, 68, 66, 6, 12, 92, 88, 88, 53, 6, 77, 8, 34, 43, 22, 18, 94, 39, 11, 76, 3, 95, 73, 72, 16, 44, 85, 14, 83, 93, 89, 41, 53, 9, 68, 94, 59, 58, 89, 47, 29, 3, 55, 99, 23, 28, 6, 83, 44, 27, 76, 75, 80, 65, 35, 44, 81, 44, 94, 23, 72, 29, 83, 85, 3, 16, 86, 81, 87, 58, 40, 46, 1, 90, 8, 29, 56, 12, 39, 66, 18, 35, 63, 26, 9, 62, 84, 91, 80, 31, 58, 31, 30, 92, 49, 46, 80, 92, 72, 58, 88, 41, 99, 50, 2, 59, 74, 50, 21, 5, 82, 70, 9, 14, 60, 3, 88, 5, 88, 1, 5, 55, 91, 99, 90, 12, 19, 9, 27, 90, 8, 19, 38, 36, 50, 17, 63, 16, 12, 30, 25, 25, 51, 54, 23, 31, 59, 98, 9, 84, 56, 84, 33, 22, 10, 91, 2, 99, 12, 46, 36, 67, 71, 23, 10, 33, 62, 45, 66, 90, 23, 87, 63, 95, 72, 77, 69, 77, 71, 6, 42, 0, 86, 66, 7, 1, 60, 75, 24, 70, 89, 68, 9, 42, 10, 30, 55, 47, 71, 72, 61, 79, 91, 18, 63, 81, 65, 44, 85, 9, 91, 6, 6, 74, 41, 2, 90, 7, 67, 13, 34, 4, 77, 31, 80, 47, 21, 81, 42, 4, 31, 64, 9, 25, 14, 33, 72, 50, 31, 96, 79, 34, 84, 98, 94, 89, 38, 0, 59, 61, 94, 62, 91, 27, 13, 48, 51, 17, 51, 50, 53, 56, 17, 54, 93, 50, 71, 63, 10, 21, 55, 62, 7, 19, 93, 43, 15, 23, 33, 32, 68, 42, 7, 33, 32, 21, 39, 3, 75, 75, 9, 55, 18, 54, 15, 65, 72, 44, 80, 76, 19, 24, 48, 50, 47, 69, 35, 71, 14, 98, 94, 22, 28, 6, 4, 64, 82, 94, 22, 35, 29, 75, 73, 39, 40, 3, 32, 52, 13, 21, 37, 15, 40, 66, 40, 79, 91, 33, 16, 58, 76, 19, 94, 23, 74, 40, 41, 22, 66, 54, 98, 56, 6, 40, 4, 36, 96, 24, 99, 6, 18, 48, 28, 81, 24, 42, 58, 15, 90, 17, 73, 24, 66, 38, 12, 83, 19, 52, 50, 2, 2, 46, 16, 56, 18, 66, 33, 21, 37, 89, 3, 69, 6, 74, 54, 67, 69, 37, 43, 54, 25, 66, 9, 51, 27, 20, 72, 20, 92, 39, 39, 16, 52, 32, 60, 41, 52, 44, 4, 6, 10, 50, 94, 39, 29, 28, 2, 94, 23, 67, 35, 87, 64, 74, 72, 38, 76, 53, 15, 31, 3, 71, 41, 69, 95, 47, 22, 21, 40, 86, 1, 16, 60, 10, 44, 17, 85, 60, 96, 7, 15, 2, 67, 48, 56, 88, 92, 15, 43, 14, 9, 31, 40, 10, 86, 56, 94, 3, 0, 58, 32, 97, 72, 22, 44, 44, 54, 18, 89, 97, 45, 8, 31, 43, 28, 61, 86, 67, 98, 5, 11, 66, 85, 48, 10, 19, 85, 11, 58, 1, 12, 65, 26, 1, 85, 56, 57, 78, 88, 76, 65, 45, 64, 49, 89, 31, 59, 65, 23, 47, 70, 18, 66, 53, 34, 54, 62, 59, 21, 7, 21, 17, 72, 58, 65, 88, 91, 8, 55, 76, 98, 45, 26, 86, 55, 66, 39, 70, 34, 69, 51, 66, 27, 45, 93, 62, 50, 78, 61, 70, 23, 7, 47, 14, 15, 10, 73, 72, 16, 97, 34, 97, 43, 61, 57, 66, 30, 51, 82, 98, 3, 74, 71, 13, 42, 19, 18, 42, 92, 92, 75, 56, 98, 87, 2, 93, 75, 89, 16, 91, 36, 84, 28, 50, 83, 81, 7, 83, 20, 20, 3, 13, 33, 44, 3, 29, 36, 52, 8, 47, 38, 51, 21, 21, 68, 99, 66, 30, 53, 57, 52, 41, 45, 80, 27, 86, 18, 13, 70, 49, 22, 66, 14, 16, 97, 34, 93, 74, 21, 38, 77, 74, 17, 29, 65, 81, 11, 50, 12, 9, 0, 33, 67, 92, 24, 73, 20, 7, 23, 94, 22, 33, 50, 99, 57, 42, 39, 38, 60, 49, 48, 58, 13, 85, 42, 22, 64, 74, 98, 24, 34, 66, 35, 59, 13, 39, 87, 3, 35, 31, 85, 80, 37, 91, 14, 52, 7, 64, 47, 59, 99, 80, 26, 31, 4, 30, 29, 73, 87, 19, 16, 75, 54, 75, 95, 42, 45, 15, 0, 31, 48, 29, 58, 96, 32, 40, 46, 31, 21, 54, 39, 27, 37, 90, 68, 47, 15, 21, 19, 90, 66, 60, 4, 39, 9, 93, 36, 68, 69, 29, 36, 95, 37, 96, 16, 80, 62, 85, 51, 46, 47, 42, 77, 34, 4, 58, 27, 19, 40, 23, 59, 68, 61, 58, 38, 39, 1, 83, 32, 68, 20, 67, 62, 22, 40, 34, 97, 84, 40, 24, 37, 58, 20, 79, 12, 58, 11, 71, 91, 83, 52, 63, 13, 0, 40, 35, 30, 66, 92, 88, 35, 74, 26, 51, 83, 36, 99, 47, 69, 55, 22, 13, 25, 73, 72, 9, 31, 69, 68, 13, 86, 26, 83, 49, 65, 93, 75, 67, 65, 70, 26, 1, 83, 73, 64, 42, 61, 56, 33, 12, 42, 46, 52, 81, 25, 99, 29, 29, 44, 59, 44, 14, 71, 38, 97, 51, 63, 35, 2, 88, 96, 27, 74, 13, 57, 39, 93, 70, 10, 60, 19, 80, 65, 27, 69, 64, 56, 72, 3, 88, 6, 19, 55, 1, 79, 69, 68, 25, 41, 82, 56, 25, 69, 94, 51, 86, 25, 75, 32, 45, 38, 81, 87, 61, 0, 81, 58, 81, 97, 82, 3, 99, 37, 96, 17, 89, 99, 44, 34, 85, 90, 38, 42, 3, 74, 30, 85, 88, 98, 21, 35, 86, 18, 32, 84, 86, 77, 78, 95, 28, 69, 17, 73, 50, 53, 14, 17, 93, 89, 51, 20, 22, 2, 76, 94, 98, 36, 52, 61, 82, 76, 23, 56, 41, 0, 12, 46, 56, 28, 30, 2, 83, 63, 32, 99, 52, 76, 18, 56, 2, 26, 67, 61, 40, 84, 80, 65, 35, 74, 88, 49, 50, 55, 95, 26, 26, 86, 80, 22, 64, 96, 61, 97, 45, 56, 2, 92, 56, 47, 46, 33, 73, 58, 96, 26, 24, 66, 40, 62, 95, 84, 43, 21, 98, 35, 31, 98, 50, 53, 99, 71, 85, 41, 46, 52, 79, 68, 12, 62, 53, 94, 34, 83, 62, 91, 20, 39, 61, 39, 26, 65, 59, 48, 71, 34, 15, 50, 63, 0, 49, 92, 92, 55, 77, 60, 31, 64, 77, 37, 95, 84, 9, 30, 7, 9, 53, 48, 35, 86, 27, 74, 67, 29, 8, 71, 98, 81, 4, 77, 82, 23, 12, 37, 32, 69, 84, 60, 79, 88, 74, 47, 77, 88, 20, 38, 29, 59, 16, 70, 55, 76, 16, 76, 17, 50, 3, 83, 81, 59, 21, 41, 5, 65, 75, 30, 71, 50, 98, 79, 97, 50, 97, 95, 61, 70, 65, 43, 70, 91, 80, 29, 83, 32, 43, 82, 53, 29, 39, 1, 19, 23, 28, 10, 59, 38, 45, 29, 47, 42, 50, 10, 50, 98, 2, 20, 65, 30, 45, 16, 47, 67, 26, 69, 67, 7, 4, 50, 87, 13, 44, 64, 56, 81, 8, 58, 26, 41, 30, 63, 29, 84, 50, 54, 35, 21, 21, 44, 98, 62, 80, 59, 71, 21, 29, 98, 32, 59, 96, 18, 33, 5, 46, 92, 95, 36, 35, 57, 70, 68, 64, 78, 90, 62, 49, 73, 47, 79, 82, 53, 62, 22, 71, 34, 65, 19, 22, 73, 69, 46, 11, 42, 49, 90, 67, 58, 23, 23, 29, 73, 82, 59, 48, 23, 78, 48, 79, 86, 70, 2, 42, 6, 46, 8, 73, 81, 10, 30, 91, 53, 66, 56, 69, 86, 28, 23, 94, 39, 35, 69, 59, 54, 84, 9, 34, 44, 3, 49, 75, 90, 60, 19, 81, 4, 95, 91, 27, 69, 59, 59, 70, 68, 40, 95, 63, 39, 90, 86, 73, 5, 4, 5, 48, 88, 87, 11, 22, 91, 53, 17, 61, 3, 64, 51, 28, 17, 65, 44, 91, 54, 95, 84, 5, 98, 90, 25, 54, 53, 7, 51, 27, 3, 15, 19, 71, 67, 96, 42, 12, 19, 6, 14, 0, 10, 71, 9, 29, 12, 67, 93, 10, 69, 36, 32, 90, 45, 44, 7, 25, 28, 67, 85, 47, 12, 59, 22, 82, 53, 25, 44, 15, 10, 29, 33, 47, 20, 66, 19, 9, 87, 92, 30, 39, 36, 37, 32, 31, 68, 94, 43, 87, 86, 46, 32, 39, 12, 54, 68, 9, 67, 65, 19, 17, 94, 12, 6, 89, 86, 99, 79, 31, 76, 22, 59, 6, 14, 36, 83, 19, 1, 81, 5, 46, 67, 67, 48, 43, 52, 30, 2, 56, 53, 26, 32, 14, 8, 6, 72, 0, 8, 93, 81, 77, 75, 59, 46, 43, 86, 82, 49, 28, 2, 27, 36, 50, 40, 34, 18, 14, 66, 51, 18, 32, 42, 1, 83, 84, 16, 14, 11, 36, 84, 89, 95, 15, 72, 92, 3, 15, 32, 95, 43, 89, 82, 38, 17, 19, 10, 47, 76, 0, 88, 69, 74, 60, 19, 27, 21, 35, 98, 19, 26, 31, 9, 16, 40, 48, 97, 62, 85, 60, 5, 85, 72, 50, 0, 91, 62, 81, 64, 68, 11, 34, 23, 64, 85, 43, 30, 42, 89, 73, 37, 36, 94, 98, 37, 60, 77, 72, 39, 80, 63, 69, 29, 23, 40, 39, 69, 38, 67, 89, 1, 57, 24, 76, 67, 17, 66, 54, 48, 59, 55, 90, 59, 97, 87, 64, 89, 5, 74, 37, 70, 34, 81, 72, 26, 55, 21, 7, 48, 98, 12, 25, 66, 38, 22, 50, 61, 16, 45, 86, 28, 25, 78, 26, 95, 80, 73, 94, 31, 28, 65, 53, 46, 24, 27, 78, 1, 24, 70, 9, 3, 37, 77, 67, 11, 34, 11, 28, 31, 84, 88, 27, 8, 28, 97, 69, 98, 7, 16, 16, 91, 52, 44, 94, 93, 49, 13, 27, 84, 60, 16, 29, 10, 36, 43, 76, 15, 26, 69, 24, 74, 93, 57, 8, 78, 84, 71, 4, 3, 75, 67, 20, 61, 91, 72, 71, 30, 34, 6, 31, 22, 87, 61, 42, 37, 26, 69, 94, 95, 2, 87, 94, 94, 87, 60, 37, 94, 92, 18, 80, 13, 32, 41, 99, 62, 57, 71, 52, 52, 45, 59, 56, 54, 66, 14, 75, 85, 25, 93, 22, 30, 11, 90, 10, 90, 73, 83, 78, 52, 76, 63, 90, 7, 4, 7, 84, 19, 69, 22, 96, 10, 26, 64, 21, 17, 77, 78, 35, 39, 96, 57, 52, 12, 47, 86, 8, 91, 70, 47, 53, 63, 9, 36, 4, 76, 82, 89, 70, 93, 15, 11, 74, 84, 72, 83, 56, 53, 22, 35, 35, 11, 27, 63, 96, 83, 14, 85, 80, 19, 81, 54, 90, 58, 17, 94, 75, 54, 66, 56, 56, 22, 40, 64, 77, 10, 75, 18, 8, 15, 92, 13, 59, 43, 78, 12, 37, 38, 49, 3, 56, 77, 15, 13, 12, 27, 51, 40, 41, 62, 10, 10, 77, 35, 5, 82, 47, 83, 63, 79, 32, 91, 70, 97, 56, 13, 92, 38, 4, 18, 17, 86, 53, 78, 25, 64, 96, 16, 42, 12, 61, 52, 55, 70, 33, 43, 92, 17, 5, 13, 66, 89, 8, 23, 11, 90, 2, 22, 92, 12, 35, 16, 49, 31, 65, 7, 25, 91, 96, 3, 48, 79, 5, 47, 37, 85, 91, 51, 44, 57, 59, 86, 58, 75, 51, 20, 99, 92, 38, 64, 85, 35, 35, 33, 99, 51, 78, 17, 6, 19, 32, 37, 7, 12, 44, 65, 63, 35, 7, 21, 42, 69, 63, 5, 13, 38, 87, 79, 44, 60, 52, 2, 60, 59, 88, 6, 65, 76, 66, 36, 31, 32, 32, 39, 66, 86, 53, 20, 20, 27, 82, 88, 39, 64, 71, 99, 74, 56, 60, 65, 43, 94, 21, 29, 89, 98, 90, 74, 1, 76, 92, 98, 90, 6, 99, 27, 14, 73, 29, 46, 85, 95, 40, 46, 76, 95, 51, 78, 46, 4, 88, 29, 86, 91, 67, 91, 0, 72, 71, 13, 78, 47, 51, 69, 39, 97, 84, 61, 51, 87, 5, 91, 30, 78, 2, 0, 36, 68, 63, 98, 9, 85, 74, 46, 60, 3, 10, 57, 46, 99, 81, 14, 1, 98, 51, 90, 74, 44, 0, 63, 93, 66, 36, 60, 83, 91, 87, 11, 94, 24, 59, 92, 58, 8, 81, 69, 42, 19, 49, 87, 94, 85, 54, 34, 24, 45, 52, 32, 37, 45, 61, 71, 72, 74, 4, 8, 71, 40, 36, 79, 59, 36, 78, 68, 65, 78, 50, 62, 45, 7, 75, 94, 63, 90, 39, 85, 87, 45, 85, 95, 64, 6, 31, 60, 22, 68, 0, 20, 55, 92, 23, 85, 32, 82, 73, 15, 89, 14, 80, 5, 46, 85, 25, 84, 63, 5, 6, 5, 5, 90, 45, 48, 56, 92, 63, 89, 8, 66, 19, 19, 34, 91, 3, 56, 65, 42, 57, 3, 22, 94, 72, 68, 80, 52, 36, 4, 13, 26, 81, 56, 25, 7, 87, 52, 90, 47, 88, 51, 50, 66, 48, 79, 77, 98, 51, 67, 67, 92, 15, 48, 77, 23, 51, 18, 50, 57, 80, 50, 39, 45, 93, 34, 18, 44, 26, 93, 41, 95, 90, 16, 87, 61, 68, 84, 46, 13, 6, 88, 10, 69, 74, 44, 43, 32, 59, 91, 44, 34, 30, 44, 41, 28, 33, 26, 77, 32, 19, 5, 37, 47, 9, 50, 58, 63, 87, 10, 83, 28, 9, 19, 83, 96, 6, 76, 17, 19, 91, 91, 8, 48, 73, 44, 1, 28, 39, 75, 90, 85, 59, 23, 27, 21, 8, 53, 84, 12, 88, 94, 38, 25, 21, 99, 53, 90, 62, 40, 30, 20, 78, 55, 56, 20, 61, 25, 61, 30, 13, 44, 97, 59, 46, 47, 13, 83, 62, 27, 65, 0, 13, 9, 68, 70, 53, 11, 12, 23, 2, 92, 68, 31, 91, 14, 26, 26, 69, 53, 85, 58, 30, 67, 26, 17, 72, 75, 40, 95, 97, 58, 2, 54, 91, 69, 61, 6, 4, 50, 6, 29, 4, 88, 36, 38, 36, 47, 94, 35, 24, 33, 87, 27, 14, 97, 4, 10, 71, 29, 79, 52, 90, 86, 65, 75, 55, 76, 4, 65, 24, 46, 73, 32, 72, 17, 48, 96, 15, 60, 12, 93, 27, 82, 55, 53, 4, 76, 40, 54, 77, 24, 33, 5, 39, 19, 27, 7, 77, 97, 31, 54, 33, 4, 22, 84, 22, 56, 82, 99, 54, 31, 64, 84, 47, 46, 71, 62, 78, 33, 65, 57, 4, 89, 53, 44, 94, 34, 3, 55, 47, 52, 22, 78, 94, 25, 29, 79, 7, 44, 54, 45, 10, 39, 65, 5, 41, 70, 93, 8, 44, 9, 74, 59, 41, 31, 90, 60, 93, 12, 55, 52, 28, 65, 57, 20, 24, 56, 46, 58, 46, 69, 76, 39, 76, 39, 65, 82, 10, 14, 58, 7, 84, 47, 85, 16, 82, 86, 7, 80, 12, 69, 49, 54, 94, 33, 52, 8, 95, 96, 61, 92, 4, 97, 2, 12, 50, 92, 48, 83, 3, 73, 74, 34, 11, 70, 87, 20, 25, 56, 33, 72, 79, 29, 61, 4, 39, 54, 69, 20, 91, 58, 52, 83, 91, 76, 3, 12, 5, 91, 22, 41, 99, 18, 63, 22, 17, 5, 60, 86, 37, 97, 83, 56, 88, 85, 41, 52, 30, 76, 31, 65, 84, 68, 84, 71, 78, 86, 63, 78, 94, 87, 17, 89, 14, 76, 13, 91, 50, 50, 28, 70, 86, 50, 85, 39, 97, 7, 29, 72, 98, 68, 14, 27, 18, 64, 32, 95, 76, 73, 26, 5, 46, 92, 56, 60, 3, 8, 74, 83, 11, 87, 75, 61, 18, 4, 68, 1, 54, 44, 12, 51, 54, 89, 60, 93, 56, 71, 4, 40, 17, 48, 85, 29, 20, 88, 89, 38, 13, 46, 27, 87, 6, 26, 57, 42, 79, 15, 29, 31, 73, 49, 44, 60, 20, 0, 20, 78, 4, 83, 47, 90, 88, 15, 17, 84, 7, 22, 75, 56, 61, 73, 21, 29, 1, 41, 50, 70, 14, 11, 96, 69, 24, 94, 62, 58, 39, 33, 7, 36, 48, 28, 16, 90, 98, 69, 45, 73, 79, 26, 97, 87, 9, 8, 9, 77, 23, 63, 52, 26, 42, 75, 64, 33, 23, 33, 46, 43, 37, 91, 96, 54, 11, 36, 42, 15, 36, 7, 4, 0, 42, 92, 91, 35, 29, 67, 3, 75, 87, 21, 6, 6, 16, 23, 80, 46, 97, 17, 2, 81, 69, 54, 1, 61, 7, 63, 61, 33, 98, 54, 49, 0, 84, 69, 19, 46, 96, 66, 54, 53, 52, 98, 24, 86, 22, 91, 45, 84, 40, 12, 89, 13, 37, 99, 9, 58, 63, 61, 98, 63, 1, 89, 23, 55, 11, 95, 67, 96, 69, 88, 11, 10, 18, 71, 87, 9, 83, 84, 9, 95, 54, 98, 68, 86, 66, 30, 76, 0, 19, 17, 16, 34, 28, 97, 75, 63, 7, 53, 29, 61, 77, 78, 17, 65, 30, 11, 40, 93, 85, 95, 38, 77, 60, 27, 61, 31, 8, 17, 77, 43, 85, 31, 59, 97, 68, 6, 82, 6, 77, 94, 17, 8, 83, 36, 91, 76, 35, 86, 72, 49, 38, 56, 38, 90, 20, 83, 11, 3, 52, 21, 58, 37, 20, 47, 24, 1, 56, 82, 31, 85, 84, 92, 52, 43, 51, 51, 36, 82, 39, 6, 33, 1, 21, 34, 80, 23, 17, 2, 20, 71, 25, 66, 24, 23, 94, 18, 17, 62, 98, 12, 0, 86, 57, 35, 5, 36, 50, 89, 94, 18, 20, 9, 13, 85, 63, 55, 65, 34, 96, 41, 65, 16, 33, 3, 98, 83, 38, 11, 49, 1, 2, 9, 84, 59, 35, 38, 76, 38, 82, 57, 69, 54, 43, 66, 50, 69, 91, 4, 72, 14, 39, 62, 59, 86, 13, 58, 28, 9, 85, 3, 17, 61, 41, 32, 19, 38, 92, 43, 61, 4, 76, 40, 46, 10, 29, 10, 85, 86, 93, 53, 43, 97, 32, 23, 8, 92, 99, 34, 41, 58, 59, 91, 40, 46, 80, 1, 31, 1, 80, 46, 56, 66, 65, 78, 8, 84, 11, 52, 98, 24, 14, 35, 72, 91, 32, 32, 81, 51, 97, 15, 2, 52, 94, 78, 42, 70, 31, 80, 54, 35, 85, 55, 39, 57, 92, 79, 75, 88, 2, 5, 17, 21, 87, 62, 11, 10, 30, 98, 16, 90, 71, 23, 30, 81, 40, 8, 48, 55, 96, 59, 12, 69, 30, 57, 24, 12, 11, 16, 81, 66, 51, 82, 15, 58, 61, 32, 2, 58, 77, 57, 46, 54, 72, 57, 50, 59, 8, 73, 21, 34, 50, 90, 17, 90, 18, 20, 87, 64, 66, 94, 2, 79, 0, 17, 95, 24, 50, 36, 95, 58, 19, 27, 12, 93, 77, 3, 95, 60, 53, 86, 82, 71, 23, 31, 55, 51, 28, 93, 32, 47, 88, 59, 91, 71, 3, 80, 55, 51, 11, 62, 88, 9, 85, 65, 5, 12, 25, 60, 50, 3, 7, 14, 92, 94, 42, 93, 65, 54, 35, 73, 29, 34, 38, 63, 1, 18, 58, 69, 47, 55, 75, 59, 29, 80, 85, 28, 21, 23, 96, 13, 37, 15, 72, 83, 89, 6, 79, 70, 83, 60, 67, 59, 14, 44, 77, 28, 80, 81, 72, 1, 34, 70, 71, 93, 14, 78, 99, 39, 45, 55, 58, 70, 76, 58, 77, 90, 91, 89, 41, 4, 33, 41, 55, 92, 32, 59, 64, 21, 8, 23, 36, 49, 74, 39, 35, 79, 56, 92, 60, 96, 40, 34, 85, 59, 16, 78, 15, 56, 73, 21, 36, 48, 35, 26, 46, 11, 30, 15, 38, 52, 21, 98, 27, 71, 91, 82, 18, 32, 98, 95, 67, 34, 24, 94, 29, 73, 51, 77, 28, 80, 82, 30, 74, 8, 10, 41, 15, 76, 15, 16, 66, 9, 19, 25, 30, 23, 35, 74, 46, 30, 96, 30, 78, 3, 43, 93, 1, 42, 13, 26, 90, 10, 61, 72, 80, 6, 18, 96, 32, 21, 35, 45, 9, 83, 80, 20, 53, 58, 51, 0, 41, 75, 42, 51, 6, 41, 37, 38, 19, 4, 18, 35, 23, 43, 75, 29, 43, 70, 80, 16, 65, 56, 49, 97, 30, 0, 31, 37, 63, 69, 16, 50, 50, 1, 31, 69, 87, 31, 91, 71, 84, 31, 21, 97, 33, 84, 6, 13, 59, 32, 16, 77, 82, 38, 36, 59, 71, 71, 66, 20, 42, 11, 16, 28, 24, 73, 71, 53, 55, 36, 38, 39, 83, 52, 69, 48, 32, 41, 5, 20, 58, 61, 62, 20, 40, 63, 45, 84, 86, 72, 62, 35, 57, 2, 52, 42, 31, 94, 85, 2, 14, 25, 31, 29, 36, 56, 60, 95, 0, 46, 28, 3, 43, 75, 80, 27, 16, 10, 64, 11, 20, 13, 6, 24, 93, 55, 60, 27, 50, 2, 5, 53, 2, 49, 66, 20, 24, 63, 66, 87, 85, 31, 78, 68, 60, 88, 26, 2, 49, 79, 56, 42, 36, 74, 78, 36, 42, 10, 0, 79, 7, 63, 17, 30, 93, 88, 20, 62, 94, 39, 95, 73, 99, 33, 83, 1, 59, 11, 86, 6, 19, 95, 69, 12, 90, 32, 46, 89, 15, 3, 72, 62, 41, 3, 37, 78, 97, 40, 94, 95, 7, 9, 44, 68, 70, 99, 32, 99, 20, 10, 89, 68, 66, 73, 4, 46, 70, 32, 12, 64, 66, 36, 92, 62, 34, 89, 60, 56, 11, 17, 9, 91, 51, 85, 92, 18, 76, 78, 81, 63, 96, 39, 46, 94, 97, 88, 65, 86, 75, 32, 6, 81, 11, 10, 50, 6, 79, 98, 25, 33, 73, 73, 46, 0, 40, 47, 83, 77, 42, 68, 17, 4, 21, 47, 86, 11, 69, 85, 86, 80, 29, 38, 88, 9, 54, 68, 21, 35, 87, 39, 90, 12, 5, 2, 86, 36, 91, 15, 40, 27, 30, 49, 56, 17, 76, 34, 16, 10, 1, 0, 44, 24, 64, 3, 54, 72, 50, 28, 36, 61, 9, 72, 57, 60, 15, 42, 27, 17, 66, 44, 78, 39, 18, 47, 73, 84, 40, 1, 62, 58, 62, 40, 54, 67, 38, 62, 82, 73, 25, 56, 62, 65, 74, 32, 6, 29, 58, 18, 23, 53, 36, 10, 42, 56, 55, 80, 70, 53, 41, 52, 85, 1, 49, 55, 47, 55, 39, 84, 94, 85, 45, 46, 86, 94, 20, 59, 13, 75, 52, 21, 28, 55, 99, 5, 27, 83, 51, 4, 71, 23, 57, 63, 21, 52, 44, 78, 46, 77, 78, 76, 2, 19, 98, 78, 48, 29, 2, 65, 27, 95, 68, 54, 53, 40, 50, 83, 75, 85, 2, 28, 57, 63, 8, 52, 44, 90, 32, 3, 15, 78, 3, 98, 24, 91, 40, 91, 16, 98, 29, 78, 98, 49, 5, 19, 28, 7, 38, 63, 71, 13, 84, 75, 0, 24, 72, 78, 59, 91, 21, 3, 4, 7, 1, 37, 75, 93, 87, 52, 95, 33, 99, 90, 36, 38, 27, 23, 54, 64, 6, 68, 16, 61, 0, 55, 64, 6, 45, 69, 30, 70, 37, 42, 76, 97, 82, 76, 57, 6, 92, 98, 47, 64, 41, 47, 87, 8, 0, 33, 84, 62, 76, 22, 17, 34, 91, 42, 26, 2, 11, 22, 9, 20, 13, 14, 33, 87, 37, 86, 56, 24, 59, 43, 11, 62, 7, 45, 58, 12, 25, 3, 96, 29, 77, 30, 58, 68, 86, 68, 35, 83, 70, 89, 81, 9, 7, 78, 44, 29, 76, 59, 96, 43, 91, 97, 49, 15, 88, 95, 52, 8, 39, 33, 70, 37, 10, 31, 20, 51, 56, 39, 45, 90, 1, 61, 20, 8, 3, 4, 77, 53, 66, 90, 88, 1, 19, 54, 85, 70, 55, 80, 15, 98, 33, 54, 44, 49, 23, 90, 90, 24, 72, 17, 15, 0, 21, 44, 64, 74, 40, 94, 52, 24, 25, 39, 29, 69, 73, 30, 85, 60, 15, 79, 37, 14, 24, 93, 13, 7, 88, 87, 95, 44, 12, 32, 29, 79, 46, 69, 74, 8, 98, 85, 18, 63, 19, 17, 56, 51, 64, 87, 80, 54, 61, 58, 49, 18, 33, 16, 99, 32, 11, 54, 37, 64, 40, 95, 51, 72, 60, 22, 14, 57, 44, 66, 84, 60, 29, 10, 60, 94, 64, 56, 98, 90, 82, 33, 50, 57, 82, 12, 61, 37, 24, 93, 10, 23, 36, 28, 64, 97, 99, 74, 66, 43, 34, 40, 13, 69, 19, 20, 56, 93, 33, 66, 66, 69, 53, 79, 16, 45, 63, 58, 90, 7, 81, 44, 68, 33, 23, 7, 22, 86, 2, 46, 51, 56, 50, 22, 38, 25, 78, 74, 73, 24, 77, 39, 79, 40, 47, 7, 83, 63, 24, 95, 67, 20, 65, 5, 39, 89, 61, 58, 45, 40, 14, 58, 35, 70, 49, 30, 43, 55, 22, 17, 17, 4, 16, 49, 77, 55, 0, 78, 54, 91, 57, 5, 9, 63, 79, 58, 76, 15, 95, 99, 4, 71, 93, 0, 29, 65, 74, 71, 26, 49, 33, 31, 95, 0, 72, 94, 7, 18, 36, 30, 84, 87, 64, 54, 48, 91, 61, 0, 0, 55, 38, 7, 81, 77, 25, 46, 35, 63, 31, 5, 15, 25, 76, 51, 74, 37, 20, 10, 58, 56, 63, 5, 17, 94, 91, 35, 16, 57, 35, 64, 29, 85, 66, 2, 56, 84, 0, 49, 38, 12, 13, 82, 78, 50, 68, 21, 36, 91, 27, 67, 82, 33, 84, 59, 50, 32, 70, 31, 83, 74, 13, 83, 24, 48, 61, 17, 61, 56, 29, 57, 68, 83, 17, 56, 18, 78, 95, 52, 85, 98, 27, 81, 93, 6, 2, 41, 11, 49, 98, 62, 68, 14, 57, 42, 3, 70, 47, 9, 52, 26, 1, 95, 46, 63, 59, 33, 28, 28, 89, 57, 21, 94, 35, 2, 22, 7, 58, 82, 1, 77, 0, 51, 25, 89, 50, 26, 44, 95, 78, 62, 84, 65, 82, 41, 35, 20, 26, 7, 91, 50, 96, 85, 79, 51, 53, 30, 46, 79, 74, 74, 38, 85, 6, 77, 82, 44, 9, 48, 58, 81, 97, 35, 15, 95, 51, 44, 34, 82, 5, 9, 63, 90, 32, 85, 57, 83, 61, 41, 20, 7, 67, 14, 65, 8, 8, 67, 67, 46, 50, 26, 91, 5, 69, 20, 62, 57, 63, 13, 88, 91, 5, 2, 35, 97, 95, 93, 73, 96, 62, 50, 52, 25, 52, 77, 32, 25, 98, 62, 27, 80, 99, 52, 84, 64, 35, 32, 12, 72, 44, 62, 42, 33, 8, 92, 96, 74, 86, 64, 18, 63, 61, 61, 31, 94, 69, 73, 91, 66, 7, 33, 20, 75, 35, 89, 90, 16, 61, 20, 82, 38, 24, 74, 9, 93, 34, 84, 18, 38, 54, 67, 41, 61, 50, 82, 73, 75, 15, 90, 56, 25, 52, 74, 6, 69, 53, 17, 83, 81, 40, 24, 25, 72, 62, 73, 23, 37, 71, 20, 62, 12, 52, 33, 19, 53, 98, 59, 62, 23, 36, 45, 8, 76, 31, 47, 98, 63, 62, 86, 90, 36, 73, 9, 47, 85, 72, 42, 48, 8, 9, 77, 51, 62, 73, 37, 56, 32, 81, 37, 72, 7, 28, 10, 43, 80, 47, 25, 53, 6, 5, 95, 12, 79, 56, 37, 19, 90, 53, 70, 34, 84, 34, 67, 12, 78, 53, 8, 46, 21, 66, 4, 65, 47, 30, 81, 53, 29, 40, 44, 9, 72, 11, 31, 44, 53, 19, 31, 61, 36, 20, 17, 26, 32, 11, 40, 66, 81, 86, 1, 1, 59, 81, 52, 80, 21, 23, 31, 73, 12, 47, 3, 82, 29, 46, 83, 20, 2, 27, 77, 18, 35, 46, 34, 83, 12, 31, 2, 51, 33, 45, 88, 92, 43, 31, 90, 6, 93, 87, 60, 44, 39, 47, 94, 67, 74, 21, 16, 4, 49, 30, 42, 15, 11, 6, 5, 8, 9, 8, 81, 18, 77, 25, 98, 61, 28, 11, 27, 89, 37, 8, 2, 55, 17, 86, 76, 50, 32, 2, 98, 80, 10, 21, 60, 30, 11, 19, 36, 5, 34, 18, 41, 61, 27, 34, 68, 38, 21, 90, 15, 68, 26, 70, 61, 96, 42, 29, 35, 54, 48, 93, 88, 79, 49, 68, 38, 89, 84, 81, 54, 51, 43, 17, 37, 98, 70, 89, 36, 99, 42, 9, 61, 21, 24, 79, 90, 15, 29, 24, 14, 62, 41, 52, 41, 95, 99, 3, 98, 72, 88, 67, 71, 45, 96, 36, 52, 69, 70, 21, 2, 32, 41, 25, 52, 69, 4, 66, 31, 94, 97, 20, 3, 19, 50, 75, 36, 81, 23, 31, 31, 63, 39, 71, 45, 5, 59, 94, 89, 18, 73, 83, 2, 34, 63, 49, 25, 93, 34, 99, 63, 40, 98, 57, 52, 13, 33, 40, 66, 92, 14, 99, 93, 43, 67, 6, 21, 72, 12, 7, 60, 19, 57, 10, 43, 21, 18, 92, 27, 54, 89, 70, 45, 71, 0, 76, 58, 79, 76, 51, 68, 16, 51, 80, 6, 46, 35, 42, 9, 51, 83, 85, 85, 7, 30, 10, 47, 51, 58, 52, 58, 65, 29, 52, 26, 61, 35, 97, 53, 14, 33, 91, 88, 36, 86, 66, 86, 44, 9, 34, 60, 66, 40, 90, 20, 62, 21, 97, 66, 55, 95, 26, 56, 1, 30, 64, 25, 87, 2, 7, 24, 99, 72, 24, 17, 1, 56, 50, 92, 1, 66, 55, 93, 20, 99, 29, 47, 37, 71, 67, 93, 0, 3, 19, 91, 41, 26, 30, 97, 13, 61, 84, 75, 90, 49, 29, 99, 67, 91, 66, 22, 60, 1, 97, 16, 21, 36, 60, 15, 10, 41, 91, 55, 36, 84, 45, 15, 50, 62, 54, 73, 98, 11, 50, 18, 3, 16, 69, 52, 11, 51, 51, 32, 53, 5, 13, 30, 13, 48, 74, 72, 64, 73, 47, 62, 93, 40, 90, 70, 41, 27, 88, 34, 13, 47, 58, 31, 2, 15, 22, 63, 13, 21, 72, 32, 8, 63, 79, 0, 53, 20, 25, 34, 15, 41, 80, 81, 68, 67, 20, 89, 20, 0, 71, 93, 85, 62, 11, 60, 60, 64, 74, 77, 97, 86, 22, 59, 2, 13, 95, 35, 89, 18, 15, 41, 74, 17, 55, 28, 23, 17, 42, 56, 96, 13, 75, 60, 47, 65, 18, 98, 79, 4, 35, 9, 68, 47, 88, 13, 34, 4, 84, 21, 3, 80, 27, 51, 49, 91, 37, 25, 52, 58, 0, 16, 33, 64, 52, 79, 98, 92, 60, 73, 4, 63, 98, 76, 57, 68, 88, 90, 14, 26, 44, 89, 70, 21, 47, 27, 41, 58, 35, 86, 97, 86, 81, 75, 30, 88, 37, 3, 65, 5, 61, 38, 92, 7, 28, 88, 31, 74, 92, 23, 69, 77, 18, 49, 94, 75, 8, 31, 33, 81, 4, 49, 33, 46, 66, 76, 50, 49, 48, 82, 93, 31, 78, 80, 39, 59, 96, 47, 20, 31, 78, 65, 50, 16, 88, 56, 9, 14, 71, 71, 45, 81, 83, 90, 22, 2, 37, 31, 66, 35, 10, 54, 90, 61, 78, 72, 78, 36, 41, 27, 50, 17, 0, 84, 64, 26, 64, 90, 14, 47, 38, 61, 14, 4, 69, 74, 7, 6, 17, 43, 80, 56, 83, 5, 38, 61, 62, 51, 94, 61, 91, 76, 82, 79, 10, 53, 98, 26, 63, 92, 77, 63, 73, 95, 0, 11, 81, 99, 11, 66, 78, 56, 99, 0, 49, 96, 99, 32, 92, 75, 82, 39, 99, 60, 3, 62, 62, 71, 98, 70, 15, 8, 5, 54, 22, 67, 76, 69, 57, 12, 52, 62, 69, 24, 79, 39, 51, 28, 61, 72, 10, 8, 11, 3, 78, 40, 49, 49, 23, 11, 95, 53, 92, 88, 80, 49, 85, 16, 2, 25, 44, 32, 84, 13, 2, 28, 64, 92, 21, 2, 25, 13, 72, 80, 58, 16, 61, 76, 49, 47, 39, 17, 59, 98, 96, 7, 5, 96, 44, 80, 68, 95, 98, 60, 57, 39, 48, 4, 3, 34, 73, 3, 67, 63, 17, 7, 24, 6, 83, 99, 49, 95, 32, 34, 84, 39, 32, 61, 17, 77, 41, 32, 2, 48, 35, 13, 12, 72, 31, 56, 49, 88, 77, 66, 50, 22, 70, 1, 46, 24, 88, 10, 49, 49, 50, 11, 45, 44, 13, 10, 30, 18, 79, 0, 39, 72, 74, 1, 86, 87, 43, 77, 9, 90, 75, 9, 76, 0, 57, 44, 4, 59, 81, 3, 49, 73, 72, 82, 30, 71, 11, 27, 76, 64, 43, 36, 34, 25, 7, 84, 12, 56, 98, 7, 53, 90, 34, 20, 46, 54, 37, 52, 92, 58, 57, 60, 72, 16, 61, 5, 30, 52, 34, 65, 97, 63, 31, 43, 93, 88, 18, 84, 31, 67, 51, 58, 8, 6, 67, 59, 70, 79, 81, 97, 57, 2, 36, 78, 91, 39, 56, 67, 55, 69, 5, 11, 5, 13, 8, 73, 54, 56, 97, 6, 56, 96, 33, 10, 7, 19, 73, 39, 3, 82, 23, 2, 64, 5, 19, 21, 8, 57, 31, 35, 70, 43, 55, 32, 36, 67, 81, 77, 56, 20, 46, 65, 14, 16, 49, 30, 55, 85, 31, 14, 81, 3, 69, 42, 56, 26, 12, 35, 42, 85, 69, 48, 33, 85, 33, 88, 47, 50, 84, 98, 96, 50, 79, 10, 23, 26, 5, 59, 44, 27, 3, 11, 34, 45, 91, 78, 94, 33, 90, 38, 28, 79, 59, 94, 98, 63, 30, 9, 19, 82, 63, 28, 8, 76, 65, 21, 90, 8, 76, 90, 93, 2, 41, 62, 98, 42, 71, 30, 69, 76, 8, 6, 72, 45, 47, 53, 34, 76, 57, 33, 97, 61, 86, 72, 67, 79, 96, 89, 34, 61, 76, 68, 90, 34, 47, 39, 1, 28, 98, 36, 19, 47, 54, 13, 32, 4, 82, 49, 37, 35, 9, 19, 78, 27, 24, 83, 8, 23, 78, 9, 82, 20, 79, 87, 22, 7, 8, 41, 19, 85, 46, 77, 70, 82, 84, 86, 90, 90, 61, 99, 56, 82, 38, 61, 53, 13, 57, 68, 4, 50, 31, 20, 52, 71, 61, 72, 23, 77, 92, 74, 31, 34, 65, 1, 98, 91, 45, 64, 7, 90, 84, 33, 66, 32, 28, 82, 0, 39, 0, 0, 89, 76, 45, 54, 63, 87, 32, 54, 89, 38, 35, 68, 10, 58, 71, 89, 41, 81, 31, 26, 62, 4, 48, 57, 55, 0, 34, 32, 30, 4, 86, 6, 8, 49, 64, 63, 57, 98, 49, 77, 38, 76, 44, 25, 2, 96, 26, 97, 11, 69, 4, 51, 43, 86, 36, 44, 52, 73, 37, 79, 71, 40, 19, 42, 27, 27, 30, 43, 23, 62, 76, 29, 89, 43, 83, 50, 17, 61, 45, 65, 91, 92, 70, 83, 93, 63, 1, 97, 56, 84, 27, 39, 3, 47, 10, 92, 32, 39, 9, 62, 80, 24, 1, 17, 14, 59, 74, 90, 52, 9, 78, 59, 18, 7, 99, 0, 85, 23, 36, 31, 82, 95, 74, 65, 86, 49, 89, 43, 44, 82, 24, 64, 71, 97, 11, 85, 38, 46, 74, 0, 64, 24, 97, 24, 25, 56, 25, 10, 21, 98, 67, 83, 55, 56, 25, 0, 69, 50, 22, 9, 31, 26, 87, 96, 43, 53, 42, 44, 15, 67, 98, 11, 21, 52, 44, 6, 77, 31, 99, 33, 10, 68, 96, 16, 80, 68, 87, 52, 50, 29, 91, 74, 18, 25, 22, 43, 38, 99, 0, 8, 1, 16, 75, 22, 65, 54, 15, 51, 21, 48, 61, 27, 20, 25, 57, 91, 3, 73, 77, 64, 41, 27, 94, 68, 63, 70, 47, 54, 77, 47, 68, 16, 48, 32, 52, 81, 83, 66, 3, 72, 34, 97, 83, 69, 24, 74, 99, 75, 78, 85, 84, 29, 17, 51, 65, 41, 47, 91, 10, 92, 80, 49, 76, 62, 87, 81, 80, 78, 43, 81, 38, 74, 6, 39, 36, 7, 36, 75, 67, 5, 63, 58, 32, 42, 3, 29, 34, 81, 0, 30, 35, 61, 17, 40, 9, 83, 27, 96, 62, 96, 15, 62, 93, 96, 28, 49, 43, 21, 53, 6, 19, 32, 46, 95, 76, 88, 67, 51, 29, 16, 44, 97, 23, 89, 13, 49, 36, 13, 16, 41, 7, 99, 9, 34, 53, 93, 28, 66, 41, 28, 53, 34, 50, 24, 79, 9, 4, 23, 92, 16, 98, 90, 36, 21, 23, 79, 58, 26, 65, 7, 46, 28, 26, 99, 53, 52, 34, 72, 5, 92, 40, 17, 43, 43, 96, 34, 73, 27, 0, 39, 62, 39, 62, 20, 27, 0, 82, 11, 16, 26, 47, 27, 67, 11, 59, 54, 26, 88, 11, 73, 47, 53, 83};

		public void reset() {
			this.init = false;
			this.doNothing = false;
			this.regions.clear();
		}

		public void save(ByteBuffer byteBuffer) {
			if (this.init) {
				byteBuffer.put((byte)1);
				byteBuffer.put((byte)(this.doNothing ? 1 : 0));
				byteBuffer.putFloat(this.noiseMain);
				byteBuffer.put((byte)this.soil);
				byteBuffer.putFloat(this.magicNum);
				byteBuffer.put((byte)this.regions.size());
				for (int int1 = 0; int1 < this.regions.size(); ++int1) {
					((ErosionCategory.Data)this.regions.get(int1)).save(byteBuffer);
				}
			} else {
				byteBuffer.put((byte)0);
			}
		}

		public void load(ByteBuffer byteBuffer, int int1) {
			this.init = byteBuffer.get() == 1;
			if (this.init) {
				this.doNothing = byteBuffer.get() == 1;
				this.noiseMain = byteBuffer.getFloat();
				this.noiseMainInt = (int)Math.floor((double)(this.noiseMain * 100.0F));
				this.soil = byteBuffer.get();
				this.magicNum = byteBuffer.getFloat();
				if (int1 < 55) {
					byteBuffer.get();
				}

				byte byte1 = byteBuffer.get();
				if (byte1 > 5) {
					DebugLog.log("> 5 regions on a square, count=" + byte1);
				}

				this.regions.clear();
				this.regions.ensureCapacity(byte1);
				for (int int2 = 0; int2 < byte1; ++int2) {
					ErosionCategory.Data data = ErosionCategory.loadCategoryData(byteBuffer, int1);
					this.regions.add(data);
				}
			}
		}

		public final int rand(int int1, int int2, int int3) {
			if (!GameServer.bServer && !GameClient.bClient) {
				return Rand.Next(int3);
			} else {
				float float1 = (float)rands[int1 % 90 + int2 % 90 * 90] / 100.0F;
				return Math.min((int)(float1 * (float)int3), int3 - 1);
			}
		}
	}
}
