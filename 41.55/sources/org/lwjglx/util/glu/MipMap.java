package org.lwjglx.util.glu;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;


public class MipMap extends Util {

	public static int gluBuild2DMipmaps(int int1, int int2, int int3, int int4, int int5, int int6, ByteBuffer byteBuffer) {
		if (int3 >= 1 && int4 >= 1) {
			int int7 = bytesPerPixel(int5, int6);
			if (int7 == 0) {
				return 100900;
			} else {
				int int8 = GL11.glGetInteger(3379);
				int int9 = nearestPower(int3);
				if (int9 > int8) {
					int9 = int8;
				}

				int int10 = nearestPower(int4);
				if (int10 > int8) {
					int10 = int8;
				}

				PixelStoreState pixelStoreState = new PixelStoreState();
				GL11.glPixelStorei(3330, 0);
				GL11.glPixelStorei(3333, 1);
				GL11.glPixelStorei(3331, 0);
				GL11.glPixelStorei(3332, 0);
				int int11 = 0;
				boolean boolean1 = false;
				ByteBuffer byteBuffer2;
				if (int9 == int3 && int10 == int4) {
					byteBuffer2 = byteBuffer;
				} else {
					byteBuffer2 = BufferUtils.createByteBuffer((int9 + 4) * int10 * int7);
					int int12 = gluScaleImage(int5, int3, int4, int6, byteBuffer, int9, int10, int6, byteBuffer2);
					if (int12 != 0) {
						int11 = int12;
						boolean1 = true;
					}

					GL11.glPixelStorei(3314, 0);
					GL11.glPixelStorei(3317, 1);
					GL11.glPixelStorei(3315, 0);
					GL11.glPixelStorei(3316, 0);
				}

				ByteBuffer byteBuffer3 = null;
				ByteBuffer byteBuffer4 = null;
				for (int int13 = 0; !boolean1; ++int13) {
					if (byteBuffer2 != byteBuffer) {
						GL11.glPixelStorei(3314, 0);
						GL11.glPixelStorei(3317, 1);
						GL11.glPixelStorei(3315, 0);
						GL11.glPixelStorei(3316, 0);
					}

					GL11.glTexImage2D(int1, int13, int2, int9, int10, 0, int5, int6, byteBuffer2);
					if (int9 == 1 && int10 == 1) {
						break;
					}

					int int14 = int9 < 2 ? 1 : int9 >> 1;
					int int15 = int10 < 2 ? 1 : int10 >> 1;
					ByteBuffer byteBuffer5;
					if (byteBuffer3 == null) {
						byteBuffer5 = byteBuffer3 = BufferUtils.createByteBuffer((int14 + 4) * int15 * int7);
					} else if (byteBuffer4 == null) {
						byteBuffer5 = byteBuffer4 = BufferUtils.createByteBuffer((int14 + 4) * int15 * int7);
					} else {
						byteBuffer5 = byteBuffer4;
					}

					int int16 = gluScaleImage(int5, int9, int10, int6, byteBuffer2, int14, int15, int6, byteBuffer5);
					if (int16 != 0) {
						int11 = int16;
						boolean1 = true;
					}

					byteBuffer2 = byteBuffer5;
					if (byteBuffer4 != null) {
						byteBuffer4 = byteBuffer3;
					}

					int9 = int14;
					int10 = int15;
				}

				pixelStoreState.save();
				return int11;
			}
		} else {
			return 100901;
		}
	}

	public static int gluScaleImage(int int1, int int2, int int3, int int4, ByteBuffer byteBuffer, int int5, int int6, int int7, ByteBuffer byteBuffer2) {
		int int8 = compPerPix(int1);
		if (int8 == -1) {
			return 100900;
		} else {
			float[] floatArray = new float[int2 * int3 * int8];
			float[] floatArray2 = new float[int5 * int6 * int8];
			byte byte1;
			switch (int4) {
			case 5121: 
				byte1 = 1;
				break;
			
			case 5126: 
				byte1 = 4;
				break;
			
			default: 
				return 1280;
			
			}

			byte byte2;
			switch (int7) {
			case 5121: 
				byte2 = 1;
				break;
			
			case 5126: 
				byte2 = 4;
				break;
			
			default: 
				return 1280;
			
			}

			PixelStoreState pixelStoreState = new PixelStoreState();
			int int9;
			if (pixelStoreState.unpackRowLength > 0) {
				int9 = pixelStoreState.unpackRowLength;
			} else {
				int9 = int2;
			}

			int int10;
			if (byte1 >= pixelStoreState.unpackAlignment) {
				int10 = int8 * int9;
			} else {
				int10 = pixelStoreState.unpackAlignment / byte1 * ceil(int8 * int9 * byte1, pixelStoreState.unpackAlignment);
			}

			int int11;
			int int12;
			int int13;
			int int14;
			label184: switch (int4) {
			case 5121: 
				int13 = 0;
				byteBuffer.rewind();
				int11 = 0;
				while (true) {
					if (int11 >= int3) {
						break label184;
					}

					int14 = int11 * int10 + pixelStoreState.unpackSkipRows * int10 + pixelStoreState.unpackSkipPixels * int8;
					for (int12 = 0; int12 < int2 * int8; ++int12) {
						floatArray[int13++] = (float)(byteBuffer.get(int14++) & 255);
					}

					++int11;
				}

			
			case 5126: 
				int13 = 0;
				byteBuffer.rewind();
				int11 = 0;
				while (true) {
					if (int11 >= int3) {
						break label184;
					}

					int14 = 4 * (int11 * int10 + pixelStoreState.unpackSkipRows * int10 + pixelStoreState.unpackSkipPixels * int8);
					for (int12 = 0; int12 < int2 * int8; ++int12) {
						floatArray[int13++] = byteBuffer.getFloat(int14);
						int14 += 4;
					}

					++int11;
				}

			
			default: 
				return 100900;
			
			}

			float float1 = (float)int2 / (float)int5;
			float float2 = (float)int3 / (float)int6;
			float[] floatArray3 = new float[int8];
			int int15;
			for (int15 = 0; int15 < int6; ++int15) {
				for (int int16 = 0; int16 < int5; ++int16) {
					int int17 = (int)((float)int16 * float1);
					int int18 = (int)((float)(int16 + 1) * float1);
					int int19 = (int)((float)int15 * float2);
					int int20 = (int)((float)(int15 + 1) * float2);
					int int21 = 0;
					int int22;
					for (int22 = 0; int22 < int8; ++int22) {
						floatArray3[int22] = 0.0F;
					}

					int int23;
					for (int22 = int17; int22 < int18; ++int22) {
						for (int int24 = int19; int24 < int20; ++int24) {
							int23 = (int24 * int2 + int22) * int8;
							for (int int25 = 0; int25 < int8; ++int25) {
								floatArray3[int25] += floatArray[int23 + int25];
							}

							++int21;
						}
					}

					int int26 = (int15 * int5 + int16) * int8;
					if (int21 == 0) {
						int23 = (int19 * int2 + int17) * int8;
						for (int22 = 0; int22 < int8; ++int22) {
							floatArray2[int26++] = floatArray[int23 + int22];
						}
					} else {
						for (int13 = 0; int13 < int8; ++int13) {
							floatArray2[int26++] = floatArray3[int13] / (float)int21;
						}
					}
				}
			}

			if (pixelStoreState.packRowLength > 0) {
				int9 = pixelStoreState.packRowLength;
			} else {
				int9 = int5;
			}

			if (byte2 >= pixelStoreState.packAlignment) {
				int10 = int8 * int9;
			} else {
				int10 = pixelStoreState.packAlignment / byte2 * ceil(int8 * int9 * byte2, pixelStoreState.packAlignment);
			}

			switch (int7) {
			case 5121: 
				int13 = 0;
				for (int11 = 0; int11 < int6; ++int11) {
					int15 = int11 * int10 + pixelStoreState.packSkipRows * int10 + pixelStoreState.packSkipPixels * int8;
					for (int12 = 0; int12 < int5 * int8; ++int12) {
						byteBuffer2.put(int15++, (byte)((int)floatArray2[int13++]));
					}
				}

				return 0;
			
			case 5126: 
				int13 = 0;
				for (int11 = 0; int11 < int6; ++int11) {
					int15 = 4 * (int11 * int10 + pixelStoreState.unpackSkipRows * int10 + pixelStoreState.unpackSkipPixels * int8);
					for (int12 = 0; int12 < int5 * int8; ++int12) {
						byteBuffer2.putFloat(int15, floatArray2[int13++]);
						int15 += 4;
					}
				}

				return 0;
			
			default: 
				return 100900;
			
			}
		}
	}
}
