package zombie.vehicles;

import java.nio.ByteBuffer;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;


public class VehicleInterpolationTest extends Assert {
	final VehicleInterpolation interpolation = new VehicleInterpolation();
	final float[] physics = new float[27];
	final float[] engineSound = new float[2];
	final ByteBuffer bb = ByteBuffer.allocateDirect(255);
	final int tick = 100;
	final int delay = 300;
	final int history = 200;
	final int bufferingIterations = 4;
	@Rule
	public TestRule watchman = new TestWatcher(){
    
    protected void failed(Throwable var1, Description var2) {
        System.out.println("interpolation.buffer:");
        System.out.print("TIME: ");
        Iterator var3 = VehicleInterpolationTest.this.interpolation.buffer.iterator();
        VehicleInterpolationData var4;
        while (var3.hasNext()) {
            var4 = (VehicleInterpolationData)var3.next();
            System.out.print(String.format(" %5d", var4.time));
        }
        System.out.println();
        System.out.print("   X: ");
        var3 = VehicleInterpolationTest.this.interpolation.buffer.iterator();
        while (var3.hasNext()) {
            var4 = (VehicleInterpolationData)var3.next();
            System.out.print(String.format(" %5.0f", var4.x));
        }
    }
};

	@Before
	public void setup() {
		this.interpolation.clear();
		this.interpolation.delay = 300;
		this.interpolation.history = 500;
		this.interpolation.reset();
	}

	@Test
	public void normalTest() {
		long long1 = 9223372036853775807L;
		for (int int1 = 1; int1 < 30; ++int1) {
			this.bb.position(0);
			this.interpolation.interpolationDataAdd(this.bb, long1, (float)(int1 * 2), (float)(int1 * 2), 0.0F, long1);
			boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, long1 - 298L);
			if (int1 < 4) {
				assertFalse(boolean1);
			} else {
				assertTrue(boolean1);
				assertEquals((float)(int1 - 4 + 1) * 2.0F, this.physics[0], 0.2F);
			}

			this.interpolation.interpolationDataGet(this.physics, this.engineSound, long1 - 298L + 50L);
			if (int1 < 4) {
				assertFalse(boolean1);
			} else {
				assertTrue(boolean1);
				assertEquals((float)(int1 - 4 + 1) * 2.0F + 1.0F, this.physics[0], 0.2F);
			}

			long1 += 100L;
		}
	}

	@Test
	public void interpolationTest() {
		int int1 = 0;
		for (int int2 = 1; int2 < 30; ++int2) {
			this.bb.position(0);
			if (int2 % 2 == 1) {
				this.interpolation.interpolationDataAdd(this.bb, (long)int1, (float)int2, (float)int2, 0.0F, (long)int1);
			}

			boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, (long)(int1 - 298));
			if (int2 < 4) {
				assertFalse(boolean1);
			} else {
				assertTrue(boolean1);
				assertEquals((float)(int2 - 4 + 1), this.physics[0], 0.2F);
			}

			int1 += 100;
		}
	}

	@Test
	public void interpolationMicroStepTest() {
		int int1 = 0;
		int int2;
		for (int2 = 1; int2 < 30; ++int2) {
			this.bb.position(0);
			this.interpolation.interpolationDataAdd(this.bb, (long)int1, (float)int2, (float)int2, 0.0F, (long)int1);
			boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, (long)(int1 - 298));
			if (int2 < 4) {
				assertFalse(boolean1);
			} else {
				assertTrue(boolean1);
				assertEquals((float)(int2 - 4 + 1), this.physics[0], 0.2F);
			}

			int1 += 100;
		}

		for (int2 = 30; int2 < 35; ++int2) {
			this.interpolation.interpolationDataAdd(this.bb, (long)int1, (float)int2, (float)int2, 0.0F, (long)int1);
			for (int int3 = 0; int3 < 100; ++int3) {
				boolean boolean2 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, (long)(int1 - 300 + 100 * int3 / 100));
				assertTrue(boolean2);
				assertEquals((float)(int2 - 4 + 1) + (float)int3 / 100.0F, this.physics[0], 0.001F);
			}

			int1 += 100;
		}
	}

	@Test
	public void interpolationMicroStepTest2() {
		long long1 = 0L;
		byte byte1 = 50;
		int int1;
		for (int1 = 1; int1 < 30; ++int1) {
			this.bb.position(0);
			this.interpolation.interpolationDataAdd(this.bb, long1, (float)int1, (float)int1, 0.0F, long1);
			boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, long1 - 298L);
			System.out.println(int1 + "   " + long1 + " " + boolean1 + " " + this.physics[0]);
			long1 += (long)byte1;
		}

		for (int1 = 30; int1 < 35; ++int1) {
			this.interpolation.interpolationDataAdd(this.bb, long1, (float)int1, (float)int1, 0.0F, long1);
			for (int int2 = 0; int2 < 10; ++int2) {
				boolean boolean2 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, long1 - 300L + (long)(byte1 * int2 / 10));
				System.out.println(int1 + "." + int2 + " " + (long1 + (long)(byte1 * int2 / 10)) + " " + boolean2 + " " + this.physics[0] + " " + ((float)int1 - 6.0F + (float)int2 / 10.0F));
				assertTrue(boolean2);
				assertEquals((float)int1 - 6.0F + (float)int2 / 10.0F, this.physics[0], 0.001F);
			}

			long1 += (long)byte1;
		}
	}

	@Test
	public void testBufferRestoring() {
		int int1 = 0;
		for (int int2 = 1; int2 < 30; ++int2) {
			this.bb.position(0);
			this.interpolation.interpolationDataAdd(this.bb, (long)int1, (float)int2, (float)int2, 0.0F, (long)int1);
			boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, (long)(int1 - 298));
			System.out.println(int2 + " " + int1 + " " + boolean1 + " " + this.physics[0]);
			if (int2 >= 4 && (int2 <= 10 || int2 >= 14)) {
				assertTrue(boolean1);
				assertEquals((float)(int2 - 4 + 1), this.physics[0], 0.2F);
			}

			if (int2 == 10) {
				int1 += 500;
			}

			int1 += 100;
		}
	}

	@Test
	public void normalTestBufferRestoring2() {
		int int1 = 0;
		for (int int2 = 1; int2 < 100; ++int2) {
			this.bb.position(0);
			boolean boolean1 = int2 < 15 || int2 > 21;
			if (boolean1) {
				this.interpolation.interpolationDataAdd(this.bb, (long)int1, (float)int2, 0.0F, 0.0F, (long)int1);
			}

			boolean boolean2 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, (long)(int1 - 298));
			System.out.println(int2 + " " + boolean2 + " " + this.physics[0]);
			if (int2 < 4 || int2 > 17 && int2 < 25) {
				assertFalse(boolean2);
			} else {
				assertTrue(boolean2);
				if (int2 >= 17 && int2 <= 21) {
					assertEquals(14.0F, this.physics[0], 0.1F);
				} else {
					assertEquals((float)(int2 - 4 + 1), this.physics[0], 0.1F);
				}
			}

			int1 += 100;
		}
	}

	@Test
	public void normalTestBufferRestoring3() {
		int int1 = 0;
		for (int int2 = 1; int2 < 40; ++int2) {
			this.bb.position(0);
			if (int2 != 10 && int2 != 12 && int2 != 13 && int2 != 15 && int2 != 16) {
				this.interpolation.interpolationDataAdd(this.bb, (long)int1, (float)int2, 0.0F, 0.0F, (long)int1);
			}

			if (int2 > 26 && int2 < 33) {
				this.interpolation.interpolationDataAdd(this.bb, (long)(int1 + 50), (float)int2 + 0.5F, 0.0F, 0.0F, (long)int1);
			}

			boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, (long)(int1 - 298));
			System.out.println(int2 + " " + boolean1 + " " + this.physics[0]);
			if (int2 < 4) {
				assertFalse(boolean1);
			} else {
				assertTrue(boolean1);
				assertEquals((float)(int2 - 4 + 1), this.physics[0], 0.1F);
			}

			int1 += 100;
		}
	}
}
