package zombie;

import zombie.iso.RoomDef;


public abstract class BaseAmbientStreamManager {

	public abstract void stop();

	public abstract void doAlarm(RoomDef roomDef);

	public abstract void doGunEvent();

	public abstract void init();

	public abstract void addBlend(String string, float float1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4);

	protected abstract void addRandomAmbient();

	public abstract void doOneShotAmbients();

	public abstract void update();

	public abstract void addAmbient(String string, int int1, int int2, int int3, float float1);

	public abstract void addAmbientEmitter(float float1, float float2, int int1, String string);

	public abstract void addDaytimeAmbientEmitter(float float1, float float2, int int1, String string);
}
