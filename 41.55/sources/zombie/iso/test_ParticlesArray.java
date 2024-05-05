package zombie.iso;

import java.io.PrintStream;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;
import zombie.core.Rand;


public class test_ParticlesArray extends Assert {

	@Test
	public void test_ParticlesArray_functional() {
		ParticlesArray particlesArray = new ParticlesArray();
		particlesArray.addParticle(new Integer(1));
		particlesArray.addParticle(new Integer(2));
		particlesArray.addParticle(new Integer(3));
		particlesArray.addParticle(new Integer(4));
		particlesArray.addParticle(new Integer(5));
		particlesArray.addParticle(new Integer(6));
		particlesArray.addParticle(new Integer(7));
		particlesArray.addParticle(new Integer(8));
		particlesArray.addParticle(new Integer(9));
		assertEquals(9L, (long)particlesArray.size());
		assertEquals(9L, (long)particlesArray.getCount());
		for (int int1 = 0; int1 < 9; ++int1) {
			assertEquals((long)(int1 + 1), (long)(Integer)particlesArray.get(int1));
		}

		particlesArray.deleteParticle(0);
		particlesArray.deleteParticle(1);
		particlesArray.deleteParticle(4);
		particlesArray.deleteParticle(7);
		particlesArray.deleteParticle(8);
		assertEquals(9L, (long)particlesArray.size());
		assertEquals(4L, (long)particlesArray.getCount());
		assertEquals((Object)null, particlesArray.get(0));
		assertEquals((Object)null, particlesArray.get(1));
		assertEquals(3L, (long)(Integer)particlesArray.get(2));
		assertEquals(4L, (long)(Integer)particlesArray.get(3));
		assertEquals((Object)null, particlesArray.get(4));
		assertEquals(6L, (long)(Integer)particlesArray.get(5));
		assertEquals(7L, (long)(Integer)particlesArray.get(6));
		assertEquals((Object)null, particlesArray.get(7));
		assertEquals((Object)null, particlesArray.get(8));
		particlesArray.defragmentParticle();
		assertEquals(9L, (long)particlesArray.size());
		assertEquals(4L, (long)particlesArray.getCount());
		assertEquals(7L, (long)(Integer)particlesArray.get(0));
		assertEquals(6L, (long)(Integer)particlesArray.get(1));
		assertEquals(3L, (long)(Integer)particlesArray.get(2));
		assertEquals(4L, (long)(Integer)particlesArray.get(3));
		assertEquals((Object)null, particlesArray.get(4));
		assertEquals((Object)null, particlesArray.get(5));
		assertEquals((Object)null, particlesArray.get(6));
		assertEquals((Object)null, particlesArray.get(7));
		assertEquals((Object)null, particlesArray.get(8));
		particlesArray.addParticle(new Integer(11));
		particlesArray.addParticle(new Integer(12));
		particlesArray.addParticle(new Integer(13));
		particlesArray.addParticle(new Integer(14));
		particlesArray.addParticle(new Integer(15));
		particlesArray.addParticle(new Integer(16));
		assertEquals(10L, (long)particlesArray.size());
		assertEquals(10L, (long)particlesArray.getCount());
		assertEquals(7L, (long)(Integer)particlesArray.get(0));
		assertEquals(6L, (long)(Integer)particlesArray.get(1));
		assertEquals(3L, (long)(Integer)particlesArray.get(2));
		assertEquals(4L, (long)(Integer)particlesArray.get(3));
		assertEquals(11L, (long)(Integer)particlesArray.get(4));
		assertEquals(12L, (long)(Integer)particlesArray.get(5));
		assertEquals(13L, (long)(Integer)particlesArray.get(6));
		assertEquals(14L, (long)(Integer)particlesArray.get(7));
		assertEquals(15L, (long)(Integer)particlesArray.get(8));
		assertEquals(16L, (long)(Integer)particlesArray.get(9));
		particlesArray.deleteParticle(0);
		particlesArray.deleteParticle(1);
		particlesArray.deleteParticle(4);
		particlesArray.deleteParticle(7);
		particlesArray.deleteParticle(8);
		particlesArray.deleteParticle(9);
		assertEquals(10L, (long)particlesArray.size());
		assertEquals(4L, (long)particlesArray.getCount());
		assertEquals((Object)null, particlesArray.get(0));
		assertEquals((Object)null, particlesArray.get(1));
		assertEquals(3L, (long)(Integer)particlesArray.get(2));
		assertEquals(4L, (long)(Integer)particlesArray.get(3));
		assertEquals((Object)null, particlesArray.get(4));
		assertEquals(12L, (long)(Integer)particlesArray.get(5));
		assertEquals(13L, (long)(Integer)particlesArray.get(6));
		assertEquals((Object)null, particlesArray.get(7));
		assertEquals((Object)null, particlesArray.get(8));
		assertEquals((Object)null, particlesArray.get(9));
		particlesArray.defragmentParticle();
		assertEquals(10L, (long)particlesArray.size());
		assertEquals(4L, (long)particlesArray.getCount());
		assertEquals(13L, (long)(Integer)particlesArray.get(0));
		assertEquals(12L, (long)(Integer)particlesArray.get(1));
		assertEquals(3L, (long)(Integer)particlesArray.get(2));
		assertEquals(4L, (long)(Integer)particlesArray.get(3));
		assertEquals((Object)null, particlesArray.get(4));
		assertEquals((Object)null, particlesArray.get(5));
		assertEquals((Object)null, particlesArray.get(6));
		assertEquals((Object)null, particlesArray.get(7));
		assertEquals((Object)null, particlesArray.get(8));
		assertEquals((Object)null, particlesArray.get(9));
		particlesArray.addParticle(new Integer(21));
		particlesArray.addParticle(new Integer(22));
		assertEquals(10L, (long)particlesArray.size());
		assertEquals(6L, (long)particlesArray.getCount());
		assertEquals(13L, (long)(Integer)particlesArray.get(0));
		assertEquals(12L, (long)(Integer)particlesArray.get(1));
		assertEquals(3L, (long)(Integer)particlesArray.get(2));
		assertEquals(4L, (long)(Integer)particlesArray.get(3));
		assertEquals(21L, (long)(Integer)particlesArray.get(4));
		assertEquals(22L, (long)(Integer)particlesArray.get(5));
		assertEquals((Object)null, particlesArray.get(6));
		assertEquals((Object)null, particlesArray.get(7));
		assertEquals((Object)null, particlesArray.get(8));
		assertEquals((Object)null, particlesArray.get(9));
		assertEquals(6L, (long)particlesArray.addParticle(new Integer(31)));
		assertEquals(7L, (long)particlesArray.addParticle(new Integer(32)));
		assertEquals(8L, (long)particlesArray.addParticle(new Integer(33)));
		assertEquals(9L, (long)particlesArray.addParticle(new Integer(34)));
		assertEquals(10L, (long)particlesArray.addParticle(new Integer(35)));
		assertEquals(11L, (long)particlesArray.size());
		assertEquals(11L, (long)particlesArray.getCount());
		particlesArray.deleteParticle(4);
		assertEquals(11L, (long)particlesArray.size());
		assertEquals(10L, (long)particlesArray.getCount());
		assertEquals(4L, (long)particlesArray.addParticle(new Integer(36)));
	}

	@Test
	public void test_ParticlesArray_Failure() {
		ParticlesArray particlesArray = new ParticlesArray();
		particlesArray.addParticle(new Integer(1));
		particlesArray.addParticle(new Integer(2));
		particlesArray.addParticle(new Integer(3));
		particlesArray.addParticle(new Integer(4));
		particlesArray.addParticle(new Integer(5));
		particlesArray.addParticle(new Integer(6));
		particlesArray.addParticle(new Integer(7));
		particlesArray.addParticle(new Integer(8));
		particlesArray.addParticle(new Integer(9));
		assertEquals(9L, (long)particlesArray.size());
		assertEquals(9L, (long)particlesArray.getCount());
		int int1;
		for (int1 = 0; int1 < 9; ++int1) {
			assertEquals((long)(int1 + 1), (long)(Integer)particlesArray.get(int1));
		}

		particlesArray.deleteParticle(-1);
		particlesArray.deleteParticle(100);
		particlesArray.addParticle((Object)null);
		assertEquals(9L, (long)particlesArray.size());
		assertEquals(9L, (long)particlesArray.getCount());
		for (int1 = 0; int1 < 9; ++int1) {
			assertEquals((long)(int1 + 1), (long)(Integer)particlesArray.get(int1));
		}

		particlesArray.deleteParticle(3);
		particlesArray.deleteParticle(3);
		particlesArray.deleteParticle(3);
	}

	@Test
	public void test_ParticlesArray_time() {
		ParticlesArray particlesArray = new ParticlesArray();
		long long1 = System.currentTimeMillis();
		for (int int1 = 0; int1 < 1000000; ++int1) {
			particlesArray.addParticle(new Integer(int1));
		}

		long long2 = System.currentTimeMillis();
		System.out.println("Add 1000000 elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		int int2 = 0;
		long1 = System.currentTimeMillis();
		int int3;
		for (int3 = 0; int3 < 1000000; ++int3) {
			if (particlesArray.deleteParticle(int3)) {
				++int2;
			}
		}

		long2 = System.currentTimeMillis();
		System.out.println("Delete " + int2 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		long1 = System.currentTimeMillis();
		for (int3 = 0; int3 < 1000000; ++int3) {
			particlesArray.addParticle(new Integer(int3));
		}

		long2 = System.currentTimeMillis();
		System.out.println("Add 1000000 elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		Rand.init();
		int2 = 0;
		long1 = System.currentTimeMillis();
		for (int3 = 0; int3 < 500000; ++int3) {
			if (particlesArray.deleteParticle(Rand.Next(1000000))) {
				++int2;
			}
		}

		long2 = System.currentTimeMillis();
		System.out.println("Delete random " + int2 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		long1 = System.currentTimeMillis();
		for (int3 = 0; int3 < 1000000; ++int3) {
			particlesArray.addParticle(new Integer(int3));
		}

		long2 = System.currentTimeMillis();
		System.out.println("Add 1000000 elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		Comparator comparator = (var0,int2x)->{
    return var0.compareTo(int2x);
};
		long1 = System.currentTimeMillis();
		particlesArray.sort(comparator);
		long2 = System.currentTimeMillis();
		PrintStream printStream = System.out;
		int int4 = particlesArray.size();
		printStream.println("Sort " + int4 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		int2 = 0;
		long1 = System.currentTimeMillis();
		int int5;
		for (int5 = 0; int5 < 500000; ++int5) {
			if (particlesArray.deleteParticle(Rand.Next(1000000))) {
				++int2;
			}
		}

		long2 = System.currentTimeMillis();
		System.out.println("Delete random " + int2 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		long1 = System.currentTimeMillis();
		particlesArray.defragmentParticle();
		long2 = System.currentTimeMillis();
		printStream = System.out;
		int4 = particlesArray.size();
		printStream.println("Defragment " + int4 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		long1 = System.currentTimeMillis();
		for (int5 = 0; int5 < 1000000; ++int5) {
			particlesArray.addParticle(new Integer(int5));
		}

		long2 = System.currentTimeMillis();
		System.out.println("Add 1000000 elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		int2 = 0;
		long1 = System.currentTimeMillis();
		for (int5 = 0; int5 < 500000; ++int5) {
			if (particlesArray.deleteParticle(Rand.Next(1000000))) {
				++int2;
			}
		}

		long2 = System.currentTimeMillis();
		System.out.println("Delete random " + int2 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		long1 = System.currentTimeMillis();
		for (int5 = 0; int5 < 1000000; ++int5) {
			particlesArray.addParticle(new Integer(int5));
		}

		long2 = System.currentTimeMillis();
		System.out.println("Add 1000000 elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		int2 = 0;
		long1 = System.currentTimeMillis();
		for (int5 = 0; int5 < 1000000; ++int5) {
			if (particlesArray.deleteParticle(Rand.Next(1000000))) {
				++int2;
			}
		}

		long2 = System.currentTimeMillis();
		System.out.println("Delete random " + int2 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
		long1 = System.currentTimeMillis();
		int5 = 0;
		for (int int6 = 0; int6 < 100000; ++int6) {
			for (int int7 = 0; int7 < particlesArray.size(); ++int7) {
				if (particlesArray.get(int7) == null) {
					particlesArray.set(int7, new Integer(int6));
					++int5;
					break;
				}
			}
		}

		long2 = System.currentTimeMillis();
		System.out.println("Simple add " + int5 + " elements = " + (long2 - long1) + " ms (size=" + particlesArray.size() + ", count=" + particlesArray.getCount() + ")");
	}
}
