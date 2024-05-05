package org.lwjglx.util.glu;


public class Util {

	protected static int ceil(int int1, int int2) {
		return int1 % int2 == 0 ? int1 / int2 : int1 / int2 + 1;
	}

	protected static float[] normalize(float[] floatArray) {
		float float1 = (float)Math.sqrt((double)(floatArray[0] * floatArray[0] + floatArray[1] * floatArray[1] + floatArray[2] * floatArray[2]));
		if ((double)float1 == 0.0) {
			return floatArray;
		} else {
			float1 = 1.0F / float1;
			floatArray[0] *= float1;
			floatArray[1] *= float1;
			floatArray[2] *= float1;
			return floatArray;
		}
	}

	protected static void cross(float[] floatArray, float[] floatArray2, float[] floatArray3) {
		floatArray3[0] = floatArray[1] * floatArray2[2] - floatArray[2] * floatArray2[1];
		floatArray3[1] = floatArray[2] * floatArray2[0] - floatArray[0] * floatArray2[2];
		floatArray3[2] = floatArray[0] * floatArray2[1] - floatArray[1] * floatArray2[0];
	}

	protected static int compPerPix(int int1) {
		switch (int1) {
		case 6400: 
		
		case 6401: 
		
		case 6402: 
		
		case 6403: 
		
		case 6404: 
		
		case 6405: 
		
		case 6406: 
		
		case 6409: 
			return 1;
		
		case 6407: 
		
		case 32992: 
			return 3;
		
		case 6408: 
		
		case 32993: 
			return 4;
		
		case 6410: 
			return 2;
		
		default: 
			return -1;
		
		}
	}

	protected static int nearestPower(int int1) {
		int int2 = 1;
		if (int1 == 0) {
			return -1;
		} else {
			while (int1 != 1) {
				if (int1 == 3) {
					return int2 << 2;
				}

				int1 >>= 1;
				int2 <<= 1;
			}

			return int2;
		}
	}

	protected static int bytesPerPixel(int int1, int int2) {
		byte byte1;
		switch (int1) {
		case 6400: 
		
		case 6401: 
		
		case 6402: 
		
		case 6403: 
		
		case 6404: 
		
		case 6405: 
		
		case 6406: 
		
		case 6409: 
			byte1 = 1;
			break;
		
		case 6407: 
		
		case 32992: 
			byte1 = 3;
			break;
		
		case 6408: 
		
		case 32993: 
			byte1 = 4;
			break;
		
		case 6410: 
			byte1 = 2;
			break;
		
		default: 
			byte1 = 0;
		
		}
		byte byte2;
		switch (int2) {
		case 5120: 
			byte2 = 1;
			break;
		
		case 5121: 
			byte2 = 1;
			break;
		
		case 5122: 
			byte2 = 2;
			break;
		
		case 5123: 
			byte2 = 2;
			break;
		
		case 5124: 
			byte2 = 4;
			break;
		
		case 5125: 
			byte2 = 4;
			break;
		
		case 5126: 
			byte2 = 4;
			break;
		
		case 6656: 
			byte2 = 1;
			break;
		
		default: 
			byte2 = 0;
		
		}
		return byte1 * byte2;
	}
}
