package fmod.fmod;


public abstract class BaseSoundListener {
	public int index;
	public float x;
	public float y;
	public float z;

	public BaseSoundListener(int int1) {
		this.index = int1;
	}

	public void setPos(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public abstract void tick();
}
