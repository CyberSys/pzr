package zombie.vehicles;

import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import zombie.GameTime;


public class test_VehicleInterpolation extends Assert {

	@Test
	public void normalTest() {
		System.out.print("START: normalTest\n");
		GameTime.setServerTimeShift(0L);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(255);
		float[] floatArray = new float[23];
		VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(500);
		for (int int1 = 1; int1 < 30; ++int1) {
			System.out.print("Interation " + int1 + "\n");
			byteBuffer.position(0);
			byteBuffer.putLong(System.nanoTime());
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.position(0);
			vehicleInterpolation.interpolationDataAdd(byteBuffer);
			boolean boolean1 = vehicleInterpolation.interpolationDataGet(floatArray);
			if (int1 < 6) {
				assertEquals(false, boolean1);
			} else {
				assertEquals(true, boolean1);
				float float1 = floatArray[0];
				assertEquals((float)(int1 - 6 + 1), float1, 0.2F);
			}

			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	@Test
	public void normalZeroTest() {
		System.out.print("START: normalZeroTest\n");
		GameTime.setServerTimeShift(0L);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(255);
		float[] floatArray = new float[23];
		VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(0);
		for (int int1 = 1; int1 < 30; ++int1) {
			System.out.print("Interation " + int1 + "\n");
			byteBuffer.position(0);
			byteBuffer.putLong(System.nanoTime());
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.position(0);
			vehicleInterpolation.interpolationDataAdd(byteBuffer);
			boolean boolean1 = vehicleInterpolation.interpolationDataGet(floatArray);
			assertEquals(true, boolean1);
			float float1 = floatArray[0];
			assertEquals((float)(int1 - 1 + 1), float1, 0.2F);
			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	@Test
	public void interpolationTest() {
		System.out.print("START: interpolationTest\n");
		GameTime.setServerTimeShift(0L);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(255);
		float[] floatArray = new float[23];
		VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(500);
		for (int int1 = 1; int1 < 30; ++int1) {
			System.out.print("Interation " + int1 + "\n");
			byteBuffer.position(0);
			if (int1 % 2 == 1) {
				byteBuffer.putLong(System.nanoTime());
				byteBuffer.putFloat(new Float((float)int1));
				byteBuffer.putFloat(new Float((float)int1));
				byteBuffer.position(0);
				vehicleInterpolation.interpolationDataAdd(byteBuffer);
			}

			boolean boolean1 = vehicleInterpolation.interpolationDataGet(floatArray);
			if (int1 < 7) {
				assertEquals(false, boolean1);
			} else {
				assertEquals(true, boolean1);
				float float1 = floatArray[0];
				assertEquals((float)(int1 - 6 + 1), float1, 0.2F);
			}

			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	@Test
	public void testBufferRestoring() {
		System.out.print("START: normalTestBufferRestoring\n");
		GameTime.setServerTimeShift(0L);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(255);
		float[] floatArray = new float[23];
		VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(400);
		for (int int1 = 1; int1 < 30; ++int1) {
			System.out.print("Interation " + int1 + "\n");
			byteBuffer.position(0);
			byteBuffer.putLong(System.nanoTime());
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.position(0);
			vehicleInterpolation.interpolationDataAdd(byteBuffer);
			boolean boolean1 = vehicleInterpolation.interpolationDataGet(floatArray);
			if (int1 >= 5 && (int1 < 11 || int1 >= 15)) {
				assertEquals(true, boolean1);
				float float1 = floatArray[0];
				assertEquals((float)(int1 - 5 + 1), float1, 0.2F);
			} else {
				assertEquals(false, boolean1);
			}

			try {
				if (int1 == 10) {
					Thread.sleep(800L);
				}

				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	@Test
	public void normalTestBufferRestoring2() {
		System.out.print("START: normalTestBufferRestoring2\n");
		GameTime.setServerTimeShift(0L);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(255);
		float[] floatArray = new float[23];
		VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(400);
		try {
			for (int int1 = 1; int1 < 40; ++int1) {
				System.out.print("Interation " + int1 + "\n");
				byteBuffer.position(0);
				byteBuffer.putLong(System.nanoTime());
				byteBuffer.putFloat(new Float((float)int1));
				byteBuffer.position(0);
				if (int1 < 15 || int1 > 20) {
					vehicleInterpolation.interpolationDataAdd(byteBuffer);
				}

				boolean boolean1 = vehicleInterpolation.interpolationDataGet(floatArray);
				if (int1 >= 5 && (int1 < 18 || int1 >= 25)) {
					assertEquals(true, boolean1);
					float float1 = floatArray[0];
					assertEquals((float)(int1 - 5 + 1), float1, 0.1F);
				} else {
					assertEquals(false, boolean1);
				}

				try {
					Thread.sleep(100L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}
		} finally {
			;
		}
	}

	@Test
	public void normalTestPR() {
		System.out.print("START: normalTestPR\n");
		GameTime.setServerTimeShift(0L);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(255);
		float[] floatArray = new float[23];
		VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(500);
		for (int int1 = 1; int1 < 30; ++int1) {
			System.out.print("Interation " + int1 + "\n");
			byteBuffer.position(0);
			byteBuffer.putLong(System.nanoTime());
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.putFloat(new Float((float)int1));
			byteBuffer.position(0);
			vehicleInterpolation.interpolationDataAdd(byteBuffer);
			boolean boolean1 = vehicleInterpolation.interpolationDataGetPR(floatArray);
			if (int1 < 6) {
				assertEquals(false, boolean1);
			} else {
				assertEquals(true, boolean1);
				float float1 = floatArray[0];
				assertEquals((float)(int1 - 6 + 1), float1, 1.0F);
			}

			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}
}
