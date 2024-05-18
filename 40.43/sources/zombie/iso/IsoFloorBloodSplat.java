package zombie.iso;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import zombie.GameTime;


public class IsoFloorBloodSplat {
	IsoChunk chunk;
	public float x;
	public float y;
	public float z;
	public int Type;
	public float worldAge;
	public int index;
	public int fade;
	public static final float FADE_HOURS = 72.0F;
	public static HashMap SpriteMap = new HashMap();
	public static String[] FloorBloodTypes = new String[]{"blood_floor_small_01", "blood_floor_small_02", "blood_floor_small_03", "blood_floor_small_04", "blood_floor_small_05", "blood_floor_small_06", "blood_floor_small_07", "blood_floor_small_08", "blood_floor_med_01", "blood_floor_med_02", "blood_floor_med_03", "blood_floor_med_04", "blood_floor_med_05", "blood_floor_med_06", "blood_floor_med_07", "blood_floor_med_08", "blood_floor_large_01", "blood_floor_large_02", "blood_floor_large_03", "blood_floor_large_04", "blood_floor_large_05"};

	public IsoFloorBloodSplat() {
	}

	public IsoFloorBloodSplat(float float1, float float2, float float3, int int1, float float4) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.Type = int1;
		this.worldAge = float4;
	}

	public void save(ByteBuffer byteBuffer) {
		int int1 = (int)(this.x / 10.0F * 255.0F);
		if (int1 < 0) {
			int1 = 0;
		}

		if (int1 > 255) {
			int1 = 255;
		}

		int int2 = (int)(this.y / 10.0F * 255.0F);
		if (int2 < 0) {
			int2 = 0;
		}

		if (int2 > 255) {
			int2 = 255;
		}

		int int3 = (int)(this.z / 8.0F * 255.0F);
		if (int3 < 0) {
			int3 = 0;
		}

		if (int3 > 255) {
			int3 = 255;
		}

		byteBuffer.put((byte)int1);
		byteBuffer.put((byte)int2);
		byteBuffer.put((byte)int3);
		byteBuffer.put((byte)this.Type);
		byteBuffer.putFloat(this.worldAge);
		byteBuffer.put((byte)this.index);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		if (int1 >= 65) {
			this.x = (float)(byteBuffer.get() & 255) / 255.0F * 10.0F;
			this.y = (float)(byteBuffer.get() & 255) / 255.0F * 10.0F;
			this.z = (float)(byteBuffer.get() & 255) / 255.0F * 8.0F;
			this.Type = byteBuffer.get();
			this.worldAge = byteBuffer.getFloat();
			if (int1 >= 73) {
				this.index = byteBuffer.get();
			}
		} else {
			this.x = byteBuffer.getFloat();
			this.y = byteBuffer.getFloat();
			this.z = byteBuffer.getFloat();
			this.Type = byteBuffer.getInt();
			this.worldAge = (float)GameTime.getInstance().getWorldAgeHours();
		}
	}
}
