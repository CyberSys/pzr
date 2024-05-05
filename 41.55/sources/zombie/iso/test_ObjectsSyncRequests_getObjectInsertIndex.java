package zombie.iso;

import org.junit.Assert;
import org.junit.Test;


public class test_ObjectsSyncRequests_getObjectInsertIndex extends Assert {

	@Test
	public void test_getInsertIndex() {
		long[] longArray = new long[]{13L, 88L, 51L};
		long[] longArray2 = new long[]{8L, 13L, 52L, 21L, 88L, 36L, 51L, 15L};
		assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 8L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 13L));
		assertEquals(1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 52L));
		assertEquals(1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 21L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 88L));
		assertEquals(2L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 36L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 51L));
		assertEquals(3L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 15L));
	}

	@Test
	public void test_getInsertIndex2() {
		long[] longArray = new long[0];
		long[] longArray2 = new long[]{81L, 45L, 72L};
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 8L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 13L));
		assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 81L));
		assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 45L));
		assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 72L));
	}

	@Test
	public void test_getInsertIndex3() {
		long[] longArray = new long[]{71L, 66L, 381L};
		long[] longArray2 = new long[]{55L, 81L, 71L, 41L, 66L, 381L, 68L};
		assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 55L));
		assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 81L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 71L));
		assertEquals(1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 41L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 66L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 381L));
		assertEquals(3L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 68L));
		assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(longArray, longArray2, 33L));
	}
}
