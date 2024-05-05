package zombie.characters;

import org.junit.Assert;
import org.junit.Test;
import zombie.iso.Vector2;


public class TestZombieInterpolate extends Assert {

	@Test
	public void test_predictor_stay() {
		NetworkCharacter networkCharacter = new NetworkCharacter();
		int int1 = 10000;
		short short1 = 250;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 1.0F;
		float float4 = -1.0F;
		networkCharacter.predict(short1, int1, float1, float2, float3, float4);
		int int2;
		NetworkCharacter.Transform transform;
		for (int2 = 0; int2 < 10; ++int2) {
			transform = networkCharacter.predict(short1, int1, float1, float2, float3, float4);
			assertEquals(100.0F, transform.position.x, 0.01F);
			assertEquals(200.0F, transform.position.y, 0.01F);
		}

		for (int2 = 0; int2 < 10; ++int2) {
			int1 += short1;
			transform = networkCharacter.predict(short1, int1, float1, float2, float3, float4);
			assertEquals(100.0F, transform.position.x, 0.01F);
			assertEquals(200.0F, transform.position.y, 0.01F);
		}
	}

	@Test
	public void test_predictor_normal_go() {
		NetworkCharacter networkCharacter = new NetworkCharacter();
		int int1 = 10000;
		short short1 = 250;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 1.0F;
		float float4 = -1.0F;
		NetworkCharacter.Transform transform = networkCharacter.predict(short1, int1, float1, float2, float3, float4);
		assertEquals(100.0F, transform.position.x, 0.01F);
		assertEquals(200.0F, transform.position.y, 0.01F);
		for (int int2 = 0; int2 < 30; ++int2) {
			int1 += short1;
			float1 += 10.0F;
			float2 -= 2.5F;
			transform = networkCharacter.predict(short1, int1, float1, float2, float3, float4);
			assertEquals(float1 + 10.0F, transform.position.x, 0.01F);
			assertEquals(float2 - 2.5F, transform.position.y, 0.01F);
		}
	}

	@Test
	public void test_predictor() {
		NetworkCharacter networkCharacter = new NetworkCharacter();
		short short1 = 10000;
		short short2 = 200;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 1.0F;
		float float4 = -1.0F;
		NetworkCharacter.Transform transform = networkCharacter.predict(short2, short1, float1, float2, float3, float4);
		assertEquals(100.0F, transform.position.x, 0.01F);
		assertEquals(200.0F, transform.position.y, 0.01F);
		int int1 = short1 + short2;
		float1 += 200.0F;
		float2 += 100.0F;
		transform = networkCharacter.predict(short2, int1, float1, float2, float3, float4);
		assertEquals(500.0F, transform.position.x, 0.01F);
		assertEquals(400.0F, transform.position.y, 0.01F);
		int1 += 10000;
		float1 = 500.0F;
		float2 = 500.0F;
		transform = networkCharacter.predict(short2, int1, float1, float2, float3, float4);
		assertEquals(500.0F, transform.position.x, 0.01F);
		assertEquals(500.0F, transform.position.y, 0.01F);
		int1 += short2;
		float1 = 400.0F;
		float2 = 300.0F;
		transform = networkCharacter.predict(short2, int1, float1, float2, float3, float4);
		assertEquals(300.0F, transform.position.x, 0.01F);
		assertEquals(100.0F, transform.position.y, 0.01F);
	}

	@Test
	public void test_predictor_normal_rotate() {
		NetworkCharacter networkCharacter = new NetworkCharacter();
		int int1 = 10000;
		short short1 = 250;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 1.0F;
		float float4 = -1.0F;
		NetworkCharacter.Transform transform = networkCharacter.predict(short1, int1, float1, float2, float3, float4);
		assertEquals(100.0F, transform.position.x, 0.01F);
		assertEquals(200.0F, transform.position.y, 0.01F);
		int int2;
		for (int2 = 0; int2 < 10; ++int2) {
			int1 += short1;
			float1 += 10.0F;
			float2 -= 2.5F;
			transform = networkCharacter.predict(short1, int1, float1, float2, float3, float4);
			assertEquals(float1 + 10.0F, transform.position.x, 0.01F);
			assertEquals(float2 - 2.5F, transform.position.y, 0.01F);
		}

		for (int2 = 0; int2 < 10; ++int2) {
			int1 += short1;
			float1 -= 10.0F;
			float2 += 2.5F;
			transform = networkCharacter.predict(short1, int1, float1, float2, float3, float4);
			assertEquals(float1 - 10.0F, transform.position.x, 0.01F);
			assertEquals(float2 + 2.5F, transform.position.y, 0.01F);
		}
	}

	@Test
	public void test_reconstructor_stay() {
		NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
		NetworkCharacter.Transform transform = networkCharacter.transform;
		int int1 = 10000;
		short short1 = 250;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 1.0F;
		float float4 = -1.0F;
		networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
		int int2;
		for (int2 = 0; int2 < 10; ++int2) {
			networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
			transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
			assertEquals(100.0F, transform.position.x, 0.01F);
			assertEquals(200.0F, transform.position.y, 0.01F);
		}

		for (int2 = 0; int2 < 10; ++int2) {
			int1 += short1;
			networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
			transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
			if (Float.isNaN(transform.position.x)) {
				assertEquals(100.0F, transform.position.x, 0.01F);
			}

			assertEquals(200.0F, transform.position.y, 0.01F);
		}
	}

	@Test
	public void test_reconstructor_normal_go() {
		NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
		NetworkCharacter.Transform transform = networkCharacter.transform;
		int int1 = 10000;
		int int2 = int1;
		short short1 = 250;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 4.0F;
		float float4 = -1.0F;
		networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
		for (int int3 = 0; int3 < 30; ++int3) {
			int1 += short1;
			float1 += 10.0F;
			float2 -= 2.5F;
			networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
			transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
			for (int int4 = 0; int4 < 5; ++int4) {
				int2 += short1 / 5;
				transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
				System.out.print("transform:(" + transform.position.x + ", " + transform.position.y + ") rotation:(" + transform.rotation.x + ", " + transform.rotation.y + ") t:" + int2 + "\n");
				assertEquals(float1 + (float)(int4 + 1) * 2.0F - 10.0F, transform.position.x, 0.9F);
				assertEquals(float2 - (float)(int4 + 1) * 0.5F + 2.5F, transform.position.y, 0.9F);
			}
		}
	}

	@Test
	public void test_reconstructor_unnormal_go() {
		NetworkCharacter.Transform transform = new NetworkCharacter.Transform();
		transform.position = new Vector2();
		transform.rotation = new Vector2();
		NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
		NetworkCharacter.Transform transform2 = networkCharacter.transform;
		short short1 = 10000;
		int int1 = short1;
		short short2 = 250;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 4.0F;
		float float4 = -1.0F;
		System.out.print("update x:" + float1 + " y:" + float2 + " t:" + short1 + "\n");
		networkCharacter.updateInterpolationPoint(short1, float1, float2, float3, float4);
		int int2 = short1 + short2;
		float1 += 10.0F;
		float2 -= 2.5F;
		System.out.print("update x:" + float1 + " y:" + float2 + " t:" + int2 + "\n");
		networkCharacter.updateInterpolationPoint(int2, float1, float2, float3, float4);
		transform2 = networkCharacter.reconstruct(short1, transform2.position.x, transform2.position.y, transform2.rotation.x, transform2.rotation.y);
		int int3;
		for (int3 = 0; int3 < 5; ++int3) {
			int1 += short2 / 5;
			transform2 = networkCharacter.reconstruct(int1, transform2.position.x, transform2.position.y, transform2.rotation.x, transform2.rotation.y);
			System.out.print("transform:(" + transform2.position.x + ", " + transform2.position.y + ")\n");
			assertEquals(float1 + (float)(int3 + 1) * 2.0F - 10.0F, transform2.position.x, 1.9F);
			assertEquals(float2 - (float)(int3 + 1) * 0.5F + 2.5F, transform2.position.y, 1.5F);
		}

		for (int3 = 0; int3 < 30; ++int3) {
			int2 += short2;
			float1 += 10.0F;
			float2 -= 2.5F;
			System.out.print("update x:" + float1 + " y:" + float2 + " t:" + int2 + "\n");
			networkCharacter.updateInterpolationPoint(int2, float1, float2, float3, float4);
			for (int int4 = 0; int4 < 5; ++int4) {
				int1 += short2 / 5;
				transform2 = networkCharacter.reconstruct(int1, transform2.position.x, transform2.position.y, transform2.rotation.x, transform2.rotation.y);
				System.out.print("transform:(" + transform2.position.x + ", " + transform2.position.y + ")\n");
				assertEquals(float1 + (float)(int4 + 1) * 2.0F - 10.0F, transform2.position.x, 1.1F);
				assertEquals(float2 - (float)(int4 + 1) * 0.5F + 2.5F, transform2.position.y, 1.1F);
				transform.position.set(transform2.position);
				transform.rotation.set(transform2.rotation);
			}
		}
	}

	@Test
	public void test_all() {
		NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
		NetworkCharacter.Transform transform = networkCharacter.transform;
		int int1 = 10000;
		int int2 = int1;
		short short1 = 250;
		float float1 = 100.0F;
		float float2 = 200.0F;
		float float3 = 0.04F;
		float float4 = -0.01F;
		System.out.print("update x:" + float1 + " y:" + float2 + " t:" + int1 + "\n");
		networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
		System.out.print("Normal interpolate\n");
		int int3;
		int int4;
		for (int3 = 0; int3 < 10; ++int3) {
			int1 += short1;
			float1 += 10.0F;
			float2 -= 2.5F;
			System.out.print("update x:" + float1 + " y:" + float2 + " t:" + int1 + "\n");
			networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
			for (int4 = 0; int4 < 5; ++int4) {
				int2 += short1 / 5;
				transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
				System.out.print("transform:(" + transform.position.x + ", " + transform.position.y + ") rotation:(" + transform.rotation.x + ", " + transform.rotation.y + ") t:" + int2 + "\n");
			}
		}

		System.out.print("Extrapolate\n");
		for (int3 = 0; int3 < 20; ++int3) {
			int2 += short1 / 5;
			transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
			System.out.print("transform:(" + transform.position.x + ", " + transform.position.y + ") rotation:(" + transform.rotation.x + ", " + transform.rotation.y + ") t:" + int2 + "\n");
		}

		System.out.print("Teleport\n");
		int1 += short1 * 10;
		float1 += 100.0F;
		float2 -= 25.0F;
		System.out.print("update x:" + float1 + " y:" + float2 + " t:" + int1 + "\n");
		networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
		for (int3 = 0; int3 < 30; ++int3) {
			int2 += short1 / 5;
			transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
			System.out.print("transform:(" + transform.position.x + ", " + transform.position.y + ") rotation:(" + transform.rotation.x + ", " + transform.rotation.y + ") t:" + int2 + "\n");
		}

		System.out.print("Normal interpolate\n");
		for (int3 = 0; int3 < 10; ++int3) {
			int1 += short1;
			float1 += 10.0F;
			float2 -= 2.5F;
			System.out.print("update x:" + float1 + " y:" + float2 + " t:" + int1 + "\n");
			networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
			for (int4 = 0; int4 < 5; ++int4) {
				int2 += short1 / 5;
				transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
				System.out.print("transform:(" + transform.position.x + ", " + transform.position.y + ") rotation:(" + transform.rotation.x + ", " + transform.rotation.y + ") t:" + int2 + "\n");
			}
		}

		System.out.print("Extrapolate\n");
		for (int3 = 0; int3 < 20; ++int3) {
			int2 += short1;
			transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
			System.out.print("transform:(" + transform.position.x + ", " + transform.position.y + ") rotation:(" + transform.rotation.x + ", " + transform.rotation.y + ") t:" + int2 + "\n");
		}

		int1 += short1 * 20;
		float1 += 200.0F;
		float2 -= 50.0F;
		System.out.print("Normal interpolate\n");
		for (int3 = 0; int3 < 10; ++int3) {
			int1 += short1;
			float1 += 10.0F;
			float2 -= 2.5F;
			System.out.print("update x:" + float1 + " y:" + float2 + " t:" + int1 + "\n");
			networkCharacter.updateInterpolationPoint(int1, float1, float2, float3, float4);
			for (int4 = 0; int4 < 5; ++int4) {
				int2 += short1 / 5;
				transform = networkCharacter.reconstruct(int2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
				System.out.print("transform:(" + transform.position.x + ", " + transform.position.y + ") rotation:(" + transform.rotation.x + ", " + transform.rotation.y + ") t:" + int2 + "\n");
			}
		}
	}

	@Test
	public void test_case1() {
		NetworkCharacter.Transform transform = new NetworkCharacter.Transform();
		transform.position = new Vector2();
		transform.rotation = new Vector2();
		long[] longArray = new long[]{982999607L, 982999623L, 982999640L, 982999656L, 982999674L, 982999690L, 982999706L, 982999723L, 982999740L, 982999756L, 982999773L, 982999791L, 982999807L, 982999823L, 982999840L, 982999856L, 982999872L};
		NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
		NetworkCharacter.Transform transform2 = networkCharacter.transform;
		System.out.print("update x:10593.158 y:9952.486 t:982998656\n");
		System.out.print("update x:10593.23 y:9950.746 t:982999872\n");
		networkCharacter.updateInterpolationPoint(982998656, 10593.158F, 9952.486F, 0.0F, -0.0014706347F);
		networkCharacter.updateInterpolationPoint(982999872, 10593.23F, 9950.746F, 0.0F, -0.0014323471F);
		int int1 = (int)longArray[0];
		long[] longArray2 = longArray;
		int int2 = longArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			long long1 = longArray2[int3];
			transform2 = networkCharacter.reconstruct((int)long1, transform2.position.x, transform2.position.y, transform2.rotation.x, transform2.rotation.y);
			System.out.print("transform:(" + transform2.position.x + ", " + transform2.position.y + ") rotation:(" + transform2.rotation.x + ", " + transform2.rotation.y + ") t:" + long1 + " t\':" + (long1 - (long)int1) + "\n");
			if (long1 > longArray[0]) {
			}

			transform.position.set(transform2.position);
			transform.rotation.set(transform2.rotation);
			int1 = (int)long1;
		}
	}
}
