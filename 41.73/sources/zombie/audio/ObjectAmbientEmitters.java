package zombie.audio;

import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import zombie.GameSounds;
import zombie.audio.parameters.ParameterCurrentZone;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;


public final class ObjectAmbientEmitters {
	private final HashMap powerPolicyMap = new HashMap();
	private static ObjectAmbientEmitters instance = null;
	static final Vector2 tempVector2 = new Vector2();
	private final HashMap m_added = new HashMap();
	private final ObjectPool m_objectPool = new ObjectPool(ObjectAmbientEmitters.ObjectWithDistance::new);
	private final ArrayList m_objects = new ArrayList();
	private final ObjectAmbientEmitters.Slot[] m_slots;
	private final Comparator comp = new Comparator(){
    
    public int compare(ObjectAmbientEmitters.ObjectWithDistance var1, ObjectAmbientEmitters.ObjectWithDistance var2) {
        return Float.compare(var1.distSq, var2.distSq);
    }
};

	public static ObjectAmbientEmitters getInstance() {
		if (instance == null) {
			instance = new ObjectAmbientEmitters();
		}

		return instance;
	}

	private ObjectAmbientEmitters() {
		byte byte1 = 16;
		this.m_slots = (ObjectAmbientEmitters.Slot[])PZArrayUtil.newInstance(ObjectAmbientEmitters.Slot.class, byte1, ObjectAmbientEmitters.Slot::new);
		this.powerPolicyMap.put("FactoryMachineAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
		this.powerPolicyMap.put("HotdogMachineAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
		this.powerPolicyMap.put("PayPhoneAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
		this.powerPolicyMap.put("StreetLightAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
		this.powerPolicyMap.put("NeonLightAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
		this.powerPolicyMap.put("NeonSignAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
		this.powerPolicyMap.put("JukeboxAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
		this.powerPolicyMap.put("ControlStationAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
		this.powerPolicyMap.put("ClockAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
		this.powerPolicyMap.put("GasPumpAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
		this.powerPolicyMap.put("LightBulbAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
		this.powerPolicyMap.put("ArcadeMachineAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
	}

	private void addObject(IsoObject object, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
		if (!GameServer.bServer) {
			if (!this.m_added.containsKey(object)) {
				boolean boolean1 = false;
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && object.getObjectIndex() != -1) {
						byte byte1 = 15;
						if (perObjectLogic instanceof ObjectAmbientEmitters.DoorLogic || perObjectLogic instanceof ObjectAmbientEmitters.WindowLogic) {
							byte1 = 10;
						}

						if ((object.square.z == PZMath.fastfloor(player.getZ()) || !(perObjectLogic instanceof ObjectAmbientEmitters.DoorLogic) && !(perObjectLogic instanceof ObjectAmbientEmitters.WindowLogic)) && !(player.DistToSquared((float)object.square.x + 0.5F, (float)object.square.y + 0.5F) > (float)(byte1 * byte1))) {
							boolean1 = true;
							break;
						}
					}
				}

				if (boolean1) {
					ObjectAmbientEmitters.ObjectWithDistance objectWithDistance = (ObjectAmbientEmitters.ObjectWithDistance)this.m_objectPool.alloc();
					objectWithDistance.object = object;
					objectWithDistance.logic = perObjectLogic;
					this.m_objects.add(objectWithDistance);
					this.m_added.put(object, objectWithDistance);
				}
			}
		}
	}

	void removeObject(IsoObject object) {
		if (!GameServer.bServer) {
			ObjectAmbientEmitters.ObjectWithDistance objectWithDistance = (ObjectAmbientEmitters.ObjectWithDistance)this.m_added.remove(object);
			if (objectWithDistance != null) {
				this.m_objects.remove(objectWithDistance);
				this.m_objectPool.release((Object)objectWithDistance);
			}
		}
	}

	public void update() {
		if (!GameServer.bServer) {
			this.addObjectsFromChunks();
			int int1;
			for (int1 = 0; int1 < this.m_slots.length; ++int1) {
				this.m_slots[int1].playing = false;
			}

			if (this.m_objects.isEmpty()) {
				this.stopNotPlaying();
			} else {
				IsoObject object;
				ObjectAmbientEmitters.PerObjectLogic perObjectLogic;
				for (int1 = 0; int1 < this.m_objects.size(); ++int1) {
					ObjectAmbientEmitters.ObjectWithDistance objectWithDistance = (ObjectAmbientEmitters.ObjectWithDistance)this.m_objects.get(int1);
					object = objectWithDistance.object;
					perObjectLogic = ((ObjectAmbientEmitters.ObjectWithDistance)this.m_objects.get(int1)).logic;
					if (!this.shouldPlay(object, perObjectLogic)) {
						this.m_added.remove(object);
						this.m_objects.remove(int1--);
						this.m_objectPool.release((Object)objectWithDistance);
					} else {
						object.getFacingPosition(tempVector2);
						objectWithDistance.distSq = this.getClosestListener(tempVector2.x, tempVector2.y, (float)object.square.z);
					}
				}

				this.m_objects.sort(this.comp);
				int1 = Math.min(this.m_objects.size(), this.m_slots.length);
				int int2;
				int int3;
				for (int3 = 0; int3 < int1; ++int3) {
					object = ((ObjectAmbientEmitters.ObjectWithDistance)this.m_objects.get(int3)).object;
					perObjectLogic = ((ObjectAmbientEmitters.ObjectWithDistance)this.m_objects.get(int3)).logic;
					if (this.shouldPlay(object, perObjectLogic)) {
						int2 = this.getExistingSlot(object);
						if (int2 != -1) {
							this.m_slots[int2].playSound(object, perObjectLogic);
						}
					}
				}

				for (int3 = 0; int3 < int1; ++int3) {
					object = ((ObjectAmbientEmitters.ObjectWithDistance)this.m_objects.get(int3)).object;
					perObjectLogic = ((ObjectAmbientEmitters.ObjectWithDistance)this.m_objects.get(int3)).logic;
					if (this.shouldPlay(object, perObjectLogic)) {
						int2 = this.getExistingSlot(object);
						if (int2 == -1) {
							int2 = this.getFreeSlot();
							if (this.m_slots[int2].object != null) {
								this.m_slots[int2].stopPlaying();
								this.m_slots[int2].object = null;
							}

							this.m_slots[int2].playSound(object, perObjectLogic);
						}
					}
				}

				this.stopNotPlaying();
				this.m_added.clear();
				this.m_objectPool.release((List)this.m_objects);
				this.m_objects.clear();
			}
		}
	}

	void addObjectsFromChunks() {
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int1];
			if (!chunkMap.ignore) {
				int int2 = IsoChunkMap.ChunkGridWidth / 2;
				int int3 = IsoChunkMap.ChunkGridWidth / 2;
				for (int int4 = -1; int4 <= 1; ++int4) {
					for (int int5 = -1; int5 <= 1; ++int5) {
						IsoChunk chunk = chunkMap.getChunk(int2 + int5, int3 + int4);
						if (chunk != null) {
							Set set = chunk.m_objectEmitterData.m_objects.keySet();
							Iterator iterator = set.iterator();
							while (iterator.hasNext()) {
								IsoObject object = (IsoObject)iterator.next();
								this.addObject(object, (ObjectAmbientEmitters.PerObjectLogic)chunk.m_objectEmitterData.m_objects.get(object));
							}
						}
					}
				}
			}
		}
	}

	float getClosestListener(float float1, float float2, float float3) {
		float float4 = Float.MAX_VALUE;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && player.getCurrentSquare() != null) {
				float float5 = player.getX();
				float float6 = player.getY();
				float float7 = player.getZ();
				float float8 = IsoUtils.DistanceToSquared(float5, float6, float7 * 3.0F, float1, float2, float3 * 3.0F);
				if (player.Traits.HardOfHearing.isSet()) {
					float8 *= 4.5F;
				}

				if (float8 < float4) {
					float4 = float8;
				}
			}
		}

		return float4;
	}

	boolean shouldPlay(IsoObject object, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
		if (object == null) {
			return false;
		} else {
			return object.getObjectIndex() == -1 ? false : perObjectLogic.shouldPlaySound();
		}
	}

	int getExistingSlot(IsoObject object) {
		for (int int1 = 0; int1 < this.m_slots.length; ++int1) {
			if (this.m_slots[int1].object == object) {
				return int1;
			}
		}

		return -1;
	}

	int getFreeSlot() {
		for (int int1 = 0; int1 < this.m_slots.length; ++int1) {
			if (!this.m_slots[int1].playing) {
				return int1;
			}
		}

		return -1;
	}

	void stopNotPlaying() {
		for (int int1 = 0; int1 < this.m_slots.length; ++int1) {
			ObjectAmbientEmitters.Slot slot = this.m_slots[int1];
			if (!slot.playing) {
				slot.stopPlaying();
				slot.object = null;
			}
		}
	}

	public void render() {
		if (DebugOptions.instance.ObjectAmbientEmitterRender.getValue()) {
			IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[IsoCamera.frameState.playerIndex];
			int int1;
			if (!chunkMap.ignore) {
				int1 = IsoChunkMap.ChunkGridWidth / 2;
				int int2 = IsoChunkMap.ChunkGridWidth / 2;
				for (int int3 = -1; int3 <= 1; ++int3) {
					for (int int4 = -1; int4 <= 1; ++int4) {
						IsoChunk chunk = chunkMap.getChunk(int1 + int4, int2 + int3);
						if (chunk != null) {
							Set set = chunk.m_objectEmitterData.m_objects.keySet();
							Iterator iterator = set.iterator();
							while (iterator.hasNext()) {
								IsoObject object = (IsoObject)iterator.next();
								if (object.square.z == (int)IsoCamera.frameState.CamCharacterZ) {
									object.getFacingPosition(tempVector2);
									float float1 = tempVector2.x;
									float float2 = tempVector2.y;
									float float3 = (float)object.square.z;
									LineDrawer.addLine(float1 - 0.45F, float2 - 0.45F, float3, float1 + 0.45F, float2 + 0.45F, float3, 0.5F, 0.5F, 0.5F, (String)null, false);
								}
							}
						}
					}
				}
			}

			for (int1 = 0; int1 < this.m_slots.length; ++int1) {
				ObjectAmbientEmitters.Slot slot = this.m_slots[int1];
				if (slot.playing) {
					IsoObject object2 = slot.object;
					object2.getFacingPosition(tempVector2);
					float float4 = tempVector2.x;
					float float5 = tempVector2.y;
					float float6 = (float)object2.square.z;
					LineDrawer.addLine(float4 - 0.45F, float5 - 0.45F, float6, float4 + 0.45F, float5 + 0.45F, float6, 0.0F, 0.0F, 1.0F, (String)null, false);
				}
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			for (int int1 = 0; int1 < instance.m_slots.length; ++int1) {
				instance.m_slots[int1].stopPlaying();
				instance.m_slots[int1].object = null;
				instance.m_slots[int1].playing = false;
			}
		}
	}

	static final class Slot {
		IsoObject object = null;
		ObjectAmbientEmitters.PerObjectLogic logic = null;
		BaseSoundEmitter emitter = null;
		long instance = 0L;
		boolean playing = false;

		void playSound(IsoObject object, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
			if (this.emitter == null) {
				this.emitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
			}

			object.getFacingPosition(ObjectAmbientEmitters.tempVector2);
			this.emitter.setPos(ObjectAmbientEmitters.tempVector2.getX(), ObjectAmbientEmitters.tempVector2.getY(), (float)object.square.z);
			this.object = object;
			this.logic = perObjectLogic;
			String string = perObjectLogic.getSoundName();
			if (!this.emitter.isPlaying(string)) {
				this.emitter.stopAll();
				FMODSoundEmitter fMODSoundEmitter = (FMODSoundEmitter)Type.tryCastTo(this.emitter, FMODSoundEmitter.class);
				if (fMODSoundEmitter != null) {
					fMODSoundEmitter.clearParameters();
				}

				this.instance = this.emitter.playSoundImpl(string, (IsoObject)null);
				perObjectLogic.startPlaying(this.emitter, this.instance);
			}

			perObjectLogic.checkParameters(this.emitter, this.instance);
			this.playing = true;
			this.emitter.tick();
		}

		void stopPlaying() {
			if (this.emitter != null && this.instance != 0L) {
				this.logic.stopPlaying(this.emitter, this.instance);
				if (this.emitter.hasSustainPoints(this.instance)) {
					this.emitter.triggerCue(this.instance);
					this.instance = 0L;
				} else {
					this.emitter.stopAll();
					this.instance = 0L;
				}
			}
		}
	}

	static enum PowerPolicy {

		NotRequired,
		InteriorHydro,
		ExteriorOK;

		private static ObjectAmbientEmitters.PowerPolicy[] $values() {
			return new ObjectAmbientEmitters.PowerPolicy[]{NotRequired, InteriorHydro, ExteriorOK};
		}
	}

	public static final class DoorLogic extends ObjectAmbientEmitters.PerObjectLogic {
		public boolean shouldPlaySound() {
			return true;
		}

		public String getSoundName() {
			return "DoorAmbiance";
		}

		public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
		}

		public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			this.parameterValue1 = Float.NaN;
		}

		public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1) {
			IsoDoor door = (IsoDoor)Type.tryCastTo(this.object, IsoDoor.class);
			float float1 = door.IsOpen() ? 1.0F : 0.0F;
			this.setParameterValue1(baseSoundEmitter, long1, "DoorWindowOpen", float1);
		}
	}

	public static final class WindowLogic extends ObjectAmbientEmitters.PerObjectLogic {

		public boolean shouldPlaySound() {
			return true;
		}

		public String getSoundName() {
			return "WindowAmbiance";
		}

		public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
		}

		public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			this.parameterValue1 = Float.NaN;
		}

		public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1) {
			IsoWindow window = (IsoWindow)Type.tryCastTo(this.object, IsoWindow.class);
			float float1 = !window.IsOpen() && !window.isDestroyed() ? 0.0F : 1.0F;
			this.setParameterValue1(baseSoundEmitter, long1, "DoorWindowOpen", float1);
		}
	}

	static final class ObjectWithDistance {
		IsoObject object;
		ObjectAmbientEmitters.PerObjectLogic logic;
		float distSq;
	}

	public abstract static class PerObjectLogic {
		public IsoObject object;
		public float parameterValue1 = Float.NaN;

		public ObjectAmbientEmitters.PerObjectLogic init(IsoObject object) {
			this.object = object;
			return this;
		}

		void setParameterValue1(BaseSoundEmitter baseSoundEmitter, long long1, String string, float float1) {
			if (float1 != this.parameterValue1) {
				this.parameterValue1 = float1;
				FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = FMODManager.instance.getParameterDescription(string);
				baseSoundEmitter.setParameterValue(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION, float1);
			}
		}

		void setParameterValue1(BaseSoundEmitter baseSoundEmitter, long long1, FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION, float float1) {
			if (float1 != this.parameterValue1) {
				this.parameterValue1 = float1;
				baseSoundEmitter.setParameterValue(long1, fMOD_STUDIO_PARAMETER_DESCRIPTION, float1);
			}
		}

		public abstract boolean shouldPlaySound();

		public abstract String getSoundName();

		public abstract void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1);

		public abstract void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1);

		public abstract void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1);
	}

	public static final class ChunkData {
		final HashMap m_objects = new HashMap();

		public boolean hasObject(IsoObject object) {
			return this.m_objects.containsKey(object);
		}

		public void addObject(IsoObject object, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
			if (!this.m_objects.containsKey(object)) {
				this.m_objects.put(object, perObjectLogic);
			}
		}

		public void removeObject(IsoObject object) {
			this.m_objects.remove(object);
		}

		public void reset() {
			this.m_objects.clear();
		}
	}

	public static final class WaterDripLogic extends ObjectAmbientEmitters.PerObjectLogic {

		public boolean shouldPlaySound() {
			return this.object.sprite != null && this.object.sprite.getProperties().Is(IsoFlagType.waterPiped) && (float)this.object.getWaterAmount() > 0.0F;
		}

		public String getSoundName() {
			return "WaterDrip";
		}

		public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			if (this.object.sprite != null && this.object.sprite.getProperties().Is("SinkType")) {
				String string = this.object.sprite.getProperties().Val("SinkType");
				byte byte1 = -1;
				switch (string.hashCode()) {
				case -1961939338: 
					if (string.equals("Ceramic")) {
						byte1 = 0;
					}

					break;
				
				case 74234599: 
					if (string.equals("Metal")) {
						byte1 = 1;
					}

				
				}

				byte byte2;
				switch (byte1) {
				case 0: 
					byte2 = 1;
					break;
				
				case 1: 
					byte2 = 2;
					break;
				
				default: 
					byte2 = 0;
				
				}

				byte byte3 = byte2;
				this.setParameterValue1(baseSoundEmitter, long1, "SinkType", (float)byte3);
			}
		}

		public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			this.parameterValue1 = Float.NaN;
		}

		public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1) {
		}
	}

	public static final class TreeAmbianceLogic extends ObjectAmbientEmitters.PerObjectLogic {

		public boolean shouldPlaySound() {
			return true;
		}

		public String getSoundName() {
			return "TreeAmbiance";
		}

		public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			FMODSoundEmitter fMODSoundEmitter = (FMODSoundEmitter)Type.tryCastTo(baseSoundEmitter, FMODSoundEmitter.class);
			if (fMODSoundEmitter != null) {
				fMODSoundEmitter.addParameter(new ParameterCurrentZone(this.object));
			}

			baseSoundEmitter.playAmbientLoopedImpl("BirdInTree");
		}

		public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			baseSoundEmitter.stopOrTriggerSoundByName("BirdInTree");
		}

		public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1) {
		}
	}

	public static final class TentAmbianceLogic extends ObjectAmbientEmitters.PerObjectLogic {

		public boolean shouldPlaySound() {
			return this.object.sprite != null && this.object.sprite.getName() != null && this.object.sprite.getName().startsWith("camping_01") && (this.object.sprite.tileSheetIndex == 0 || this.object.sprite.tileSheetIndex == 3);
		}

		public String getSoundName() {
			return "TentAmbiance";
		}

		public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
		}

		public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
		}

		public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1) {
		}
	}

	public static final class AmbientSoundLogic extends ObjectAmbientEmitters.PerObjectLogic {
		ObjectAmbientEmitters.PowerPolicy powerPolicy;
		boolean bHasGeneratorParameter;

		public AmbientSoundLogic() {
			this.powerPolicy = ObjectAmbientEmitters.PowerPolicy.NotRequired;
			this.bHasGeneratorParameter = false;
		}

		public ObjectAmbientEmitters.PerObjectLogic init(IsoObject object) {
			super.init(object);
			String string = this.getSoundName();
			this.powerPolicy = (ObjectAmbientEmitters.PowerPolicy)ObjectAmbientEmitters.getInstance().powerPolicyMap.getOrDefault(string, ObjectAmbientEmitters.PowerPolicy.NotRequired);
			if (this.powerPolicy != ObjectAmbientEmitters.PowerPolicy.NotRequired) {
				GameSound gameSound = GameSounds.getSound(string);
				this.bHasGeneratorParameter = gameSound != null && gameSound.numClipsUsingParameter("Generator") > 0;
			}

			return this;
		}

		public boolean shouldPlaySound() {
			boolean boolean1;
			if (this.powerPolicy == ObjectAmbientEmitters.PowerPolicy.InteriorHydro) {
				boolean1 = this.object.square.haveElectricity() || IsoWorld.instance.isHydroPowerOn() && this.object.square.getRoom() != null;
				if (!boolean1) {
					return false;
				}
			}

			if (this.powerPolicy == ObjectAmbientEmitters.PowerPolicy.ExteriorOK) {
				boolean1 = this.object.square.haveElectricity() || IsoWorld.instance.isHydroPowerOn();
				if (!boolean1) {
					return false;
				}
			}

			if (this.powerPolicy != ObjectAmbientEmitters.PowerPolicy.NotRequired && !IsoWorld.instance.isHydroPowerOn() && !this.bHasGeneratorParameter) {
				return false;
			} else {
				PropertyContainer propertyContainer = this.object.getProperties();
				return propertyContainer != null && propertyContainer.Is("AmbientSound");
			}
		}

		public String getSoundName() {
			return this.object.getProperties().Val("AmbientSound");
		}

		public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
		}

		public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			this.parameterValue1 = Float.NaN;
		}

		public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1) {
			if (this.powerPolicy != ObjectAmbientEmitters.PowerPolicy.NotRequired) {
				this.setParameterValue1(baseSoundEmitter, long1, "Generator", IsoWorld.instance.isHydroPowerOn() ? 0.0F : 1.0F);
			}
		}
	}

	public static final class FridgeHumLogic extends ObjectAmbientEmitters.PerObjectLogic {

		public boolean shouldPlaySound() {
			ItemContainer itemContainer = this.object.getContainerByEitherType("fridge", "freezer");
			return itemContainer != null && itemContainer.isPowered();
		}

		public String getSoundName() {
			return "FridgeHum";
		}

		public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
		}

		public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long long1) {
			this.parameterValue1 = Float.NaN;
		}

		public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long1) {
			this.setParameterValue1(baseSoundEmitter, long1, "Generator", IsoWorld.instance.isHydroPowerOn() ? 0.0F : 1.0F);
		}
	}
}
