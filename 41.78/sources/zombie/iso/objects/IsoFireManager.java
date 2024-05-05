package zombie.iso.objects;

import fmod.fmod.FMODSoundEmitter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;
import zombie.WorldSoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.parameters.ParameterFireSize;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.list.PZArrayUtil;


public class IsoFireManager {
	public static double Red_Oscilator = 0.0;
	public static double Green_Oscilator = 0.0;
	public static double Blue_Oscilator = 0.0;
	public static double Red_Oscilator_Rate = 0.10000000149011612;
	public static double Green_Oscilator_Rate = 0.12999999523162842;
	public static double Blue_Oscilator_Rate = 0.08760000020265579;
	public static double Red_Oscilator_Val = 0.0;
	public static double Green_Oscilator_Val = 0.0;
	public static double Blue_Oscilator_Val = 0.0;
	public static double OscilatorSpeedScalar = 15.600000381469727;
	public static double OscilatorEffectScalar = 0.0038999998942017555;
	public static int MaxFireObjects = 75;
	public static int FireRecalcDelay = 25;
	public static int FireRecalc;
	public static boolean LightCalcFromBurningCharacters;
	public static float FireAlpha;
	public static float SmokeAlpha;
	public static float FireAnimDelay;
	public static float SmokeAnimDelay;
	public static ColorInfo FireTintMod;
	public static ColorInfo SmokeTintMod;
	public static final ArrayList FireStack;
	public static final ArrayList CharactersOnFire_Stack;
	private static final IsoFireManager.FireSounds fireSounds;
	private static Stack updateStack;
	private static final HashSet charactersOnFire;

	public static void Add(IsoFire fire) {
		if (FireStack.contains(fire)) {
			System.out.println("IsoFireManager.Add already added fire, ignoring");
		} else {
			if (FireStack.size() < MaxFireObjects) {
				FireStack.add(fire);
			} else {
				IsoFire fire2 = null;
				int int1 = 0;
				for (int int2 = 0; int2 < FireStack.size(); ++int2) {
					if (((IsoFire)FireStack.get(int2)).Age > int1) {
						int1 = ((IsoFire)FireStack.get(int2)).Age;
						fire2 = (IsoFire)FireStack.get(int2);
					}
				}

				if (fire2 != null && fire2.square != null) {
					fire2.square.getProperties().UnSet(IsoFlagType.burning);
					fire2.square.getProperties().UnSet(IsoFlagType.smoke);
					fire2.RemoveAttachedAnims();
					fire2.removeFromWorld();
					fire2.removeFromSquare();
				}

				FireStack.add(fire);
			}
		}
	}

	public static void AddBurningCharacter(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < CharactersOnFire_Stack.size(); ++int1) {
			if (CharactersOnFire_Stack.get(int1) == gameCharacter) {
				return;
			}
		}

		CharactersOnFire_Stack.add(gameCharacter);
	}

	public static void Fire_LightCalc(IsoGridSquare square, IsoGridSquare square2, int int1) {
		if (square2 != null && square != null) {
			byte byte1 = 0;
			byte byte2 = 8;
			int int2 = byte1 + Math.abs(square2.getX() - square.getX());
			int2 += Math.abs(square2.getY() - square.getY());
			int2 += Math.abs(square2.getZ() - square.getZ());
			if (int2 <= byte2) {
				float float1 = 0.199F / (float)byte2 * (float)(byte2 - int2);
				float float2 = float1 * 0.6F;
				float float3 = float1 * 0.4F;
				if (square2.getLightInfluenceR() == null) {
					square2.setLightInfluenceR(new ArrayList());
				}

				square2.getLightInfluenceR().add(float1);
				if (square2.getLightInfluenceG() == null) {
					square2.setLightInfluenceG(new ArrayList());
				}

				square2.getLightInfluenceG().add(float2);
				if (square2.getLightInfluenceB() == null) {
					square2.setLightInfluenceB(new ArrayList());
				}

				square2.getLightInfluenceB().add(float3);
				ColorInfo colorInfo = square2.lighting[int1].lightInfo();
				colorInfo.r += float1;
				colorInfo.g += float2;
				colorInfo.b += float3;
				if (colorInfo.r > 1.0F) {
					colorInfo.r = 1.0F;
				}

				if (colorInfo.g > 1.0F) {
					colorInfo.g = 1.0F;
				}

				if (colorInfo.b > 1.0F) {
					colorInfo.b = 1.0F;
				}
			}
		}
	}

	public static void LightTileWithFire(IsoGridSquare square) {
	}

	public static void explode(IsoCell cell, IsoGridSquare square, int int1) {
		if (square != null) {
			IsoGridSquare square2 = null;
			Object object = null;
			FireRecalc = 1;
			for (int int2 = -2; int2 <= 2; ++int2) {
				for (int int3 = -2; int3 <= 2; ++int3) {
					for (int int4 = 0; int4 <= 1; ++int4) {
						square2 = cell.getGridSquare(square.getX() + int2, square.getY() + int3, square.getZ() + int4);
						if (square2 != null && Rand.Next(100) < int1 && IsoFire.CanAddFire(square2, true)) {
							StartFire(cell, square2, true, Rand.Next(100, 250 + int1));
							square2.BurnWalls(true);
						}
					}
				}
			}
		}
	}

	@Deprecated
	public static void MolotovSmash(IsoCell cell, IsoGridSquare square) {
	}

	public static void Remove(IsoFire fire) {
		if (!FireStack.contains(fire)) {
			System.out.println("IsoFireManager.Remove unknown fire, ignoring");
		} else {
			FireStack.remove(fire);
		}
	}

	public static void RemoveBurningCharacter(IsoGameCharacter gameCharacter) {
		CharactersOnFire_Stack.remove(gameCharacter);
	}

	public static void StartFire(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1, int int2) {
		if (square.getFloor() != null && square.getFloor().getSprite() != null) {
			int1 -= square.getFloor().getSprite().firerequirement;
		}

		if (int1 < 5) {
			int1 = 5;
		}

		if (IsoFire.CanAddFire(square, boolean1)) {
			if (GameClient.bClient) {
				DebugLog.General.warn("The StartFire function was called on Client");
			} else if (GameServer.bServer) {
				GameServer.startFireOnClient(square, int1, boolean1, int2, false);
			} else {
				IsoFire fire = new IsoFire(cell, square, boolean1, int1, int2);
				Add(fire);
				square.getObjects().add(fire);
				if (Rand.Next(5) == 0) {
					WorldSoundManager.instance.addSound(fire, square.getX(), square.getY(), square.getZ(), 20, 20);
				}
			}
		}
	}

	public static void StartSmoke(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1, int int2) {
		if (IsoFire.CanAddSmoke(square, boolean1)) {
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.StartFire.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(square.getX());
				byteBufferWriter.putInt(square.getY());
				byteBufferWriter.putInt(square.getZ());
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putBoolean(boolean1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putBoolean(true);
				PacketTypes.PacketType.StartFire.send(GameClient.connection);
			} else if (GameServer.bServer) {
				GameServer.startFireOnClient(square, int1, boolean1, int2, true);
			} else {
				IsoFire fire = new IsoFire(cell, square, boolean1, int1, int2, true);
				Add(fire);
				square.getObjects().add(fire);
			}
		}
	}

	public static void StartFire(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1) {
		StartFire(cell, square, boolean1, int1, 0);
	}

	public static void addCharacterOnFire(IsoGameCharacter gameCharacter) {
		synchronized (charactersOnFire) {
			charactersOnFire.add(gameCharacter);
		}
	}

	public static void deleteCharacterOnFire(IsoGameCharacter gameCharacter) {
		synchronized (charactersOnFire) {
			charactersOnFire.remove(gameCharacter);
		}
	}

	public static void Update() {
		synchronized (charactersOnFire) {
			charactersOnFire.forEach(IsoGameCharacter::SpreadFireMP);
		}
		Red_Oscilator_Val = Math.sin(Red_Oscilator += Blue_Oscilator_Rate * OscilatorSpeedScalar);
		Green_Oscilator_Val = Math.sin(Green_Oscilator += Blue_Oscilator_Rate * OscilatorSpeedScalar);
		Blue_Oscilator_Val = Math.sin(Blue_Oscilator += Blue_Oscilator_Rate * OscilatorSpeedScalar);
		Red_Oscilator_Val = (Red_Oscilator_Val + 1.0) / 2.0;
		Green_Oscilator_Val = (Green_Oscilator_Val + 1.0) / 2.0;
		Blue_Oscilator_Val = (Blue_Oscilator_Val + 1.0) / 2.0;
		Red_Oscilator_Val *= OscilatorEffectScalar;
		Green_Oscilator_Val *= OscilatorEffectScalar;
		Blue_Oscilator_Val *= OscilatorEffectScalar;
		updateStack.clear();
		updateStack.addAll(FireStack);
		for (int int1 = 0; int1 < updateStack.size(); ++int1) {
			IsoFire fire = (IsoFire)updateStack.get(int1);
			if (fire.getObjectIndex() != -1 && FireStack.contains(fire)) {
				fire.update();
			}
		}

		--FireRecalc;
		if (FireRecalc < 0) {
			FireRecalc = FireRecalcDelay;
		}

		fireSounds.update();
	}

	public static void updateSound(IsoFire fire) {
		fireSounds.addFire(fire);
	}

	public static void stopSound(IsoFire fire) {
		fireSounds.removeFire(fire);
	}

	public static void RemoveAllOn(IsoGridSquare square) {
		for (int int1 = FireStack.size() - 1; int1 >= 0; --int1) {
			IsoFire fire = (IsoFire)FireStack.get(int1);
			if (fire.square == square) {
				fire.extinctFire();
			}
		}
	}

	public static void Reset() {
		FireStack.clear();
		CharactersOnFire_Stack.clear();
		fireSounds.Reset();
	}

	static  {
		FireRecalc = FireRecalcDelay;
		LightCalcFromBurningCharacters = false;
		FireAlpha = 1.0F;
		SmokeAlpha = 0.3F;
		FireAnimDelay = 0.2F;
		SmokeAnimDelay = 0.2F;
		FireTintMod = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
		SmokeTintMod = new ColorInfo(0.5F, 0.5F, 0.5F, 1.0F);
		FireStack = new ArrayList();
		CharactersOnFire_Stack = new ArrayList();
		fireSounds = new IsoFireManager.FireSounds(20);
		updateStack = new Stack();
		charactersOnFire = new HashSet();
	}

	private static final class FireSounds {
		final ArrayList fires = new ArrayList();
		final IsoFireManager.FireSounds.Slot[] slots;
		final Comparator comp = new Comparator(){
    
    public int compare(IsoFire var1, IsoFire var2) {
        float var3 = FireSounds.this.getClosestListener((float)var1.square.x + 0.5F, (float)var1.square.y + 0.5F, (float)var1.square.z);
        float var4 = FireSounds.this.getClosestListener((float)var2.square.x + 0.5F, (float)var2.square.y + 0.5F, (float)var2.square.z);
        if (var3 > var4) {
            return 1;
        } else {
            return var3 < var4 ? -1 : 0;
        }
    }
};

		FireSounds(int int1) {
			this.slots = (IsoFireManager.FireSounds.Slot[])PZArrayUtil.newInstance(IsoFireManager.FireSounds.Slot.class, int1, IsoFireManager.FireSounds.Slot::new);
		}

		void addFire(IsoFire fire) {
			if (!this.fires.contains(fire)) {
				this.fires.add(fire);
			}
		}

		void removeFire(IsoFire fire) {
			this.fires.remove(fire);
		}

		void update() {
			if (!GameServer.bServer) {
				int int1;
				for (int1 = 0; int1 < this.slots.length; ++int1) {
					this.slots[int1].playing = false;
				}

				if (this.fires.isEmpty()) {
					this.stopNotPlaying();
				} else {
					Collections.sort(this.fires, this.comp);
					int1 = Math.min(this.fires.size(), this.slots.length);
					int int2;
					IsoFire fire;
					int int3;
					for (int2 = 0; int2 < int1; ++int2) {
						fire = (IsoFire)this.fires.get(int2);
						if (this.shouldPlay(fire)) {
							int3 = this.getExistingSlot(fire);
							if (int3 != -1) {
								this.slots[int3].playSound(fire);
							}
						}
					}

					for (int2 = 0; int2 < int1; ++int2) {
						fire = (IsoFire)this.fires.get(int2);
						if (this.shouldPlay(fire)) {
							int3 = this.getExistingSlot(fire);
							if (int3 == -1) {
								int3 = this.getFreeSlot();
								this.slots[int3].playSound(fire);
							}
						}
					}

					this.stopNotPlaying();
					this.fires.clear();
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

		boolean shouldPlay(IsoFire fire) {
			return fire != null && fire.getObjectIndex() != -1 && fire.LifeStage < 4;
		}

		int getExistingSlot(IsoFire fire) {
			for (int int1 = 0; int1 < this.slots.length; ++int1) {
				if (this.slots[int1].fire == fire) {
					return int1;
				}
			}

			return -1;
		}

		int getFreeSlot() {
			for (int int1 = 0; int1 < this.slots.length; ++int1) {
				if (!this.slots[int1].playing) {
					return int1;
				}
			}

			return -1;
		}

		void stopNotPlaying() {
			for (int int1 = 0; int1 < this.slots.length; ++int1) {
				IsoFireManager.FireSounds.Slot slot = this.slots[int1];
				if (!slot.playing) {
					slot.stopPlaying();
					slot.fire = null;
				}
			}
		}

		void Reset() {
			for (int int1 = 0; int1 < this.slots.length; ++int1) {
				this.slots[int1].stopPlaying();
				this.slots[int1].fire = null;
				this.slots[int1].playing = false;
			}
		}

		static final class Slot {
			IsoFire fire;
			BaseSoundEmitter emitter;
			final ParameterFireSize parameterFireSize = new ParameterFireSize();
			long instance = 0L;
			boolean playing;

			void playSound(IsoFire fire) {
				if (this.emitter == null) {
					this.emitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
					if (!Core.SoundDisabled) {
						((FMODSoundEmitter)this.emitter).addParameter(this.parameterFireSize);
					}
				}

				this.emitter.setPos((float)fire.square.x + 0.5F, (float)fire.square.y + 0.5F, (float)fire.square.z);
				byte byte1;
				switch (fire.LifeStage) {
				case 1: 
				
				case 3: 
					byte1 = 1;
					break;
				
				case 2: 
					byte1 = 2;
					break;
				
				default: 
					byte1 = 0;
				
				}
				byte byte2 = byte1;
				this.parameterFireSize.setSize(byte2);
				if (fire.isCampfire()) {
					if (!this.emitter.isPlaying("CampfireRunning")) {
						this.instance = this.emitter.playSoundImpl("CampfireRunning", (IsoObject)null);
					}
				} else if (!this.emitter.isPlaying("Fire")) {
					this.instance = this.emitter.playSoundImpl("Fire", (IsoObject)null);
				}

				this.fire = fire;
				this.playing = true;
				this.emitter.tick();
			}

			void stopPlaying() {
				if (this.emitter != null && this.instance != 0L) {
					if (this.emitter.hasSustainPoints(this.instance)) {
						this.emitter.triggerCue(this.instance);
						this.instance = 0L;
					} else {
						this.emitter.stopAll();
						this.instance = 0L;
					}
				} else {
					if (this.emitter != null && !this.emitter.isEmpty()) {
						this.emitter.tick();
					}
				}
			}
		}
	}
}
