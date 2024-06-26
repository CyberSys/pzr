package zombie.network;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;


public class MPStatisticsTest {
	private UdpConnection connection;
	private final long limit = 200L;

	@BeforeClass
	public static void init() {
		Rand.init();
		ServerOptions.instance.init();
	}

	@Before
	public void reset() {
		ServerOptions.instance.PingLimit.setValue(200);
		this.connection = new UdpConnection((UdpEngine)null, 0L, 0);
		MPStatistics.pingIntervalCount = 10L;
		MPStatistics.pingLimitCount = 0L;
		MPStatistics.maxPingToSum = 400L;
	}

	@Test
	public void TestAveragePingIsGreaterThanLimit() {
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, long1);
		Assert.assertTrue(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestAveragePingIsLessThanLimit() {
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		this.connection.pingHistory.addFirst(199L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(199L, long1);
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestAveragePingIsEqualToLimit() {
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(200L, long1);
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestAveragePingIsSlightlyGreaterThanLimit() {
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(201L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, long1);
		Assert.assertTrue(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestAveragePingIsSlightlyLessThanLimit() {
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(199L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(200L, long1);
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestNotEnoughPingIntervals() {
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 5L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(0L, long1);
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestEnoughPingIntervals() {
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 5L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, long1);
		Assert.assertTrue(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestNotEnoughPingSpikes() {
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingLimitCount = 3L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(0L, long1);
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestEnoughPingSpikes() {
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingLimitCount = 3L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, long1);
		Assert.assertTrue(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestSeveralHugeSpikesDoesNotExceedTheLimit() {
		this.connection.pingHistory.addFirst(10000000L);
		this.connection.pingHistory.addFirst(10000000L);
		this.connection.pingHistory.addFirst(99L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(200L, long1);
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestSeveralHugeSpikesEqualToLimit() {
		this.connection.pingHistory.addFirst(10000000L);
		this.connection.pingHistory.addFirst(10000000L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(200L, long1);
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestSeveralHugeSpikesExceedTheLimit() {
		this.connection.pingHistory.addFirst(10000000L);
		this.connection.pingHistory.addFirst(10000000L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(200L);
		this.connection.pingHistory.addFirst(201L);
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, long1);
		Assert.assertTrue(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestTheLatestDataIsUsedForCounting() {
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(100L);
		this.connection.pingHistory.addFirst(301L);
		this.connection.pingHistory.removeLast();
		MPStatistics.pingIntervalCount = 2L;
		MPStatistics.pingLimitCount = 1L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, MPStatistics.checkLatest(this.connection, 200L));
		Assert.assertTrue(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestKickWhileLoadingWhenKickIsDisabledViaMinValue() {
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 1L;
		MPStatistics.pingLimitCount = 0L;
		ServerOptions.instance.PingLimit.setValue((int)ServerOptions.instance.PingLimit.getMin());
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, MPStatistics.checkLatest(this.connection, 200L));
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestKickWhileLoadingWhenConnectionIsPreferredInQueue() {
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 1L;
		MPStatistics.pingLimitCount = 0L;
		this.connection.preferredInQueue = true;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, MPStatistics.checkLatest(this.connection, 200L));
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestKickWhileLoadingWhenConnectionIsAdmin() {
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 1L;
		MPStatistics.pingLimitCount = 0L;
		this.connection.accessLevel = 32;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, MPStatistics.checkLatest(this.connection, 200L));
		Assert.assertFalse(MPStatistics.doKickWhileLoading(this.connection, long1));
	}

	@Test
	public void TestKick() {
		this.connection.setFullyConnected();
		UdpConnection udpConnection = this.connection;
		udpConnection.connectionTimestamp -= 120000L;
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 1L;
		MPStatistics.pingLimitCount = 0L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, MPStatistics.checkLatest(this.connection, 200L));
		Assert.assertTrue(MPStatistics.doKick(this.connection, long1));
	}

	@Test
	public void TestKickGraceInterval() {
		this.connection.setFullyConnected();
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 1L;
		MPStatistics.pingLimitCount = 0L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, MPStatistics.checkLatest(this.connection, 200L));
		Assert.assertFalse(MPStatistics.doKick(this.connection, long1));
	}

	@Test
	public void TestKickIsNotFullyConnected() {
		UdpConnection udpConnection = this.connection;
		udpConnection.connectionTimestamp -= 120000L;
		this.connection.pingHistory.addFirst(201L);
		MPStatistics.pingIntervalCount = 1L;
		MPStatistics.pingLimitCount = 0L;
		long long1 = MPStatistics.checkLatest(this.connection, 200L);
		Assert.assertEquals(201L, MPStatistics.checkLatest(this.connection, 200L));
		Assert.assertFalse(MPStatistics.doKick(this.connection, long1));
	}
}
