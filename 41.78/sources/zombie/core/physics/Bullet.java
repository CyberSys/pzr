package zombie.core.physics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.vehicles.BaseVehicle;


public class Bullet {
	public static ByteBuffer cmdBuf;
	public static final byte TO_ADD_VEHICLE = 4;
	public static final byte TO_SCROLL_CHUNKMAP = 5;
	public static final byte TO_ACTIVATE_CHUNKMAP = 6;
	public static final byte TO_INIT_WORLD = 7;
	public static final byte TO_UPDATE_CHUNK = 8;
	public static final byte TO_DEBUG_DRAW_WORLD = 9;
	public static final byte TO_STEP_SIMULATION = 10;
	public static final byte TO_UPDATE_PLAYER_LIST = 12;
	public static final byte TO_END = -1;

	public static void init() {
		String string = "";
		if ("1".equals(System.getProperty("zomboid.debuglibs.bullet"))) {
			DebugLog.log("***** Loading debug version of PZBullet");
			string = "d";
		}

		String string2 = "";
		if (GameServer.bServer && GameWindow.OSValidator.isUnix()) {
			string2 = "NoOpenGL";
		}

		if (System.getProperty("os.name").contains("OS X")) {
			System.loadLibrary("PZBullet");
		} else if (System.getProperty("sun.arch.data.model").equals("64")) {
			System.loadLibrary("PZBullet" + string2 + "64" + string);
		} else {
			System.loadLibrary("PZBullet" + string2 + "32" + string);
		}

		cmdBuf = ByteBuffer.allocateDirect(4096);
		cmdBuf.order(ByteOrder.LITTLE_ENDIAN);
	}

	private static native void ToBullet(ByteBuffer byteBuffer);

	public static void CatchToBullet(ByteBuffer byteBuffer) {
		try {
			MPStatistic.getInstance().Bullet.Start();
			ToBullet(byteBuffer);
			MPStatistic.getInstance().Bullet.End();
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
		}
	}

	public static native void initWorld(int int1, int int2, boolean boolean1);

	public static native void destroyWorld();

	public static native void activateChunkMap(int int1, int int2, int int3, int int4);

	public static native void deactivateChunkMap(int int1);

	public static void initWorld(int int1, int int2, int int3, int int4, int int5) {
		MPStatistic.getInstance().Bullet.Start();
		initWorld(int1, int2, GameServer.bServer);
		activateChunkMap(0, int3, int4, int5);
		MPStatistic.getInstance().Bullet.End();
	}

	public static void updatePlayerList(ArrayList arrayList) {
		cmdBuf.clear();
		cmdBuf.put((byte)12);
		cmdBuf.putShort((short)arrayList.size());
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			IsoPlayer player = (IsoPlayer)iterator.next();
			cmdBuf.putInt(player.OnlineID);
			cmdBuf.putInt((int)player.getX());
			cmdBuf.putInt((int)player.getY());
		}

		cmdBuf.put((byte)-1);
		cmdBuf.put((byte)-1);
		CatchToBullet(cmdBuf);
	}

	public static void beginUpdateChunk(IsoChunk chunk) {
		cmdBuf.clear();
		cmdBuf.put((byte)8);
		cmdBuf.putShort((short)chunk.wx);
		cmdBuf.putShort((short)chunk.wy);
	}

	public static void updateChunk(int int1, int int2, int int3, int int4, byte[] byteArray) {
		cmdBuf.put((byte)int1);
		cmdBuf.put((byte)int2);
		cmdBuf.put((byte)int3);
		cmdBuf.put((byte)int4);
		for (int int5 = 0; int5 < int4; ++int5) {
			cmdBuf.put(byteArray[int5]);
		}
	}

	public static void endUpdateChunk() {
		if (cmdBuf.position() != 5) {
			cmdBuf.put((byte)-1);
			cmdBuf.put((byte)-1);
			CatchToBullet(cmdBuf);
		}
	}

	public static native void scrollChunkMap(int int1, int int2);

	public static void scrollChunkMapLeft(int int1) {
		MPStatistic.getInstance().Bullet.Start();
		scrollChunkMap(int1, 0);
		MPStatistic.getInstance().Bullet.End();
	}

	public static void scrollChunkMapRight(int int1) {
		MPStatistic.getInstance().Bullet.Start();
		scrollChunkMap(int1, 1);
		MPStatistic.getInstance().Bullet.End();
	}

	public static void scrollChunkMapUp(int int1) {
		MPStatistic.getInstance().Bullet.Start();
		scrollChunkMap(int1, 2);
		MPStatistic.getInstance().Bullet.End();
	}

	public static void scrollChunkMapDown(int int1) {
		MPStatistic.getInstance().Bullet.Start();
		scrollChunkMap(int1, 3);
		MPStatistic.getInstance().Bullet.End();
	}

	public static void setVehicleActive(BaseVehicle baseVehicle, boolean boolean1) {
		baseVehicle.isActive = boolean1;
		setVehicleActive(baseVehicle.getId(), boolean1);
	}

	public static int setVehicleStatic(BaseVehicle baseVehicle, boolean boolean1) {
		baseVehicle.isStatic = boolean1;
		return setVehicleStatic(baseVehicle.getId(), boolean1);
	}

	public static native void addVehicle(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, String string);

	public static native void removeVehicle(int int1);

	public static native void controlVehicle(int int1, float float1, float float2, float float3);

	public static native void setVehicleActive(int int1, boolean boolean1);

	public static native void applyCentralForceToVehicle(int int1, float float1, float float2, float float3);

	public static native void applyTorqueToVehicle(int int1, float float1, float float2, float float3);

	public static native void teleportVehicle(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7);

	public static native void setTireInflation(int int1, int int2, float float1);

	public static native void setTireRemoved(int int1, int int2, boolean boolean1);

	public static native void stepSimulation(float float1, int int1, float float2);

	public static native int getVehicleCount();

	public static native int getVehiclePhysics(int int1, float[] floatArray);

	public static native int getOwnVehiclePhysics(int int1, float[] floatArray);

	public static native int setOwnVehiclePhysics(int int1, float[] floatArray);

	public static native int setVehicleParams(int int1, float[] floatArray);

	public static native int setVehicleMass(int int1, float float1);

	public static native int getObjectPhysics(float[] floatArray);

	public static native void createServerCell(int int1, int int2);

	public static native void removeServerCell(int int1, int int2);

	public static native int addPhysicsObject(float float1, float float2);

	public static native void defineVehicleScript(String string, float[] floatArray);

	public static native void setVehicleVelocityMultiplier(int int1, float float1, float float2);

	public static native int setVehicleStatic(int int1, boolean boolean1);

	public static native int addHingeConstraint(int int1, int int2, float float1, float float2, float float3, float float4, float float5, float float6);

	public static native int addPointConstraint(int int1, int int2, float float1, float float2, float float3, float float4, float float5, float float6);

	public static native int addRopeConstraint(int int1, int int2, float float1, float float2, float float3, float float4, float float5, float float6, float float7);

	public static native void removeConstraint(int int1);
}
