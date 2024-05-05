package zombie.characters;

import java.io.IOException;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import zombie.DummySoundManager;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.network.ServerMap;


public class ZombieIDMapTest extends Assert {
	HashSet IDs = new HashSet();
	IsoCell cell = new IsoCell(300, 300);

	@BeforeClass
	public static void beforeAll() {
		try {
			Rand.init();
			ZomboidFileSystem.instance.init();
			LuaManager.init();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	@Test
	public void test10Allocations() {
		Rand.init();
		this.IDs.clear();
		byte byte1 = 10;
		for (short short1 = 0; short1 < byte1; ++short1) {
			short short2 = ServerMap.instance.getUniqueZombieId();
			System.out.println("id:" + short2);
		}
	}

	@Test
	public void test32653Allocations() {
		Rand.init();
		this.IDs.clear();
		char char1 = '蝝';
		long long1 = System.nanoTime();
		for (int int1 = 0; int1 < char1; ++int1) {
			short short1 = ServerMap.instance.getUniqueZombieId();
			assertFalse(this.IDs.contains(short1));
			this.IDs.add(short1);
		}

		long long2 = System.nanoTime();
		float float1 = (float)(long2 - long1) / 1000000.0F;
		System.out.println("time:" + float1);
		System.out.println("time per task:" + float1 / (float)char1);
	}

	@Test
	public void test32653Adds() {
		SoundManager.instance = new DummySoundManager();
		Rand.init();
		SurvivorFactory.addMaleForename("Bob");
		SurvivorFactory.addFemaleForename("Kate");
		SurvivorFactory.addSurname("Testova");
		this.IDs.clear();
		short short1 = 32653;
		long long1 = System.nanoTime();
		for (short short2 = 0; short2 < short1; ++short2) {
			short short3 = ServerMap.instance.getUniqueZombieId();
			assertNull(ServerMap.instance.ZombieMap.get(short3));
			assertFalse(this.IDs.contains(short3));
			IsoZombie zombie = new IsoZombie(this.cell);
			zombie.OnlineID = short3;
			ServerMap.instance.ZombieMap.put(short3, zombie);
			assertEquals((long)short3, (long)((IsoZombie)ServerMap.instance.ZombieMap.get(short3)).OnlineID);
			this.IDs.add(short3);
		}

		long long2 = System.nanoTime();
		float float1 = (float)(long2 - long1) / 1000000.0F;
		System.out.println("time:" + float1);
		System.out.println("time per task:" + float1 / (float)short1);
	}

	@Test
	public void test32653Process() {
		Rand.init();
		ServerMap.instance = new ServerMap();
		SoundManager.instance = new DummySoundManager();
		SurvivorFactory.addMaleForename("Bob");
		SurvivorFactory.addFemaleForename("Kate");
		SurvivorFactory.addSurname("Testova");
		this.IDs.clear();
		short short1 = 32653;
		long long1 = System.nanoTime();
		for (short short2 = 0; short2 < short1; ++short2) {
			assertNull(ServerMap.instance.ZombieMap.get(short2));
			IsoZombie zombie = new IsoZombie(this.cell);
			zombie.OnlineID = short2;
			ServerMap.instance.ZombieMap.put(short2, zombie);
			assertEquals((long)short2, (long)((IsoZombie)ServerMap.instance.ZombieMap.get(short2)).OnlineID);
		}

		long long2 = System.nanoTime();
		for (short short3 = 0; short3 < short1; ++short3) {
			assertEquals((long)short3, (long)((IsoZombie)ServerMap.instance.ZombieMap.get(short3)).OnlineID);
			ServerMap.instance.ZombieMap.remove(short3);
			assertNull(ServerMap.instance.ZombieMap.get(short3));
		}

		long long3 = System.nanoTime();
		for (short short4 = 0; short4 < short1; ++short4) {
			assertNull(ServerMap.instance.ZombieMap.get(short4));
			IsoZombie zombie2 = new IsoZombie(this.cell);
			zombie2.OnlineID = short4;
			ServerMap.instance.ZombieMap.put(short4, zombie2);
			assertEquals((long)short4, (long)((IsoZombie)ServerMap.instance.ZombieMap.get(short4)).OnlineID);
		}

		long long4 = System.nanoTime();
		for (short short5 = 0; short5 < short1; ++short5) {
			assertEquals((long)short5, (long)((IsoZombie)ServerMap.instance.ZombieMap.get(short5)).OnlineID);
			ServerMap.instance.ZombieMap.remove(short5);
			assertNull(ServerMap.instance.ZombieMap.get(short5));
		}

		long long5 = System.nanoTime();
		for (short short6 = 0; short6 < short1; ++short6) {
			assertNull(ServerMap.instance.ZombieMap.get(short6));
			IsoZombie zombie3 = new IsoZombie(this.cell);
			zombie3.OnlineID = short6;
			ServerMap.instance.ZombieMap.put(short6, zombie3);
			assertEquals((long)short6, (long)((IsoZombie)ServerMap.instance.ZombieMap.get(short6)).OnlineID);
		}

		long long6 = System.nanoTime();
		float float1 = (float)(long2 - long1) / 1000000.0F;
		float float2 = (float)(long3 - long2) / 1000000.0F;
		float float3 = (float)(long4 - long3) / 1000000.0F;
		float float4 = (float)(long5 - long4) / 1000000.0F;
		float float5 = (float)(long6 - long5) / 1000000.0F;
		System.out.println("time1:" + float1);
		System.out.println("time2:" + float2);
		System.out.println("time3:" + float3);
		System.out.println("time4:" + float4);
		System.out.println("time5:" + float5);
	}
}
